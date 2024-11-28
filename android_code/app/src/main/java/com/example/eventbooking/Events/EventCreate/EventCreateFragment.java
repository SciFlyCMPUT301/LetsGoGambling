package com.example.eventbooking.Events.EventCreate;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.waitinglist.WaitingList;
import com.example.eventbooking.Location;
import com.example.eventbooking.R;
import com.example.eventbooking.Role;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.profile.ProfileFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/**
 * Fragment for creating new events. This fragment allows users to input event details, such as title, description,
 * image URL, location, and participant limits. The event is saved to Firestore, and if the user does not have
 * the organizer role, it is assigned to them and updated in Firestore.
 */
public class EventCreateFragment extends Fragment {
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextImageUrl;
    private EditText editTextLocation;
    private EditText editMaxParticipants;
    private EditText editWaitingListLimit;
    private Button createEventButton;
    private Button backButton;
    private FirebaseFirestore db;
    private ImageView QRCode;
    private ImageView posterImageView;
    private Switch geolocationSwitch;
    private boolean isGeolocationRequired = false;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Event currentEvent;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    private QRcodeGenerator qrCodeGenerator;
    //empty constructor
    private boolean roleAssigned = false, testingFlag;
    public EventCreateFragment(){}

    /**
     * Creates a new instance of EventCreateFragment, primarily for testing.
     * @param testing
     * @return
     */
    public static EventCreateFragment newInstance(boolean testing) {
        EventCreateFragment fragment = new EventCreateFragment();
        Bundle args = new Bundle();
        args.putBoolean("testing flag", testing);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is created. Retrieves the testing flag from arguments if provided.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the integer from arguments
        if (getArguments() != null) {
            testingFlag = getArguments().getBoolean("testing flag");
        }
        qrCodeGenerator = new QRcodeGenerator(getContext());
    }

    /**
     * Inflates the fragment's layout and initializes UI elements.
     *
     * @param inflater           LayoutInflater used to inflate the layout
     * @param container          ViewGroup container in which the fragment is placed
     * @param savedInstanceState Bundle containing saved state data (if any)
     * @return the root View for the fragment's layout
     */
    // Inflate the layout and display the integer
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_create, container, false);
        //init UI elements
        editTextTitle = rootView.findViewById(R.id.event_create_title);
        editWaitingListLimit = rootView.findViewById(R.id.waiting_list_limit);
        editTextDescription = rootView.findViewById(R.id.event_description);
        editTextLocation = rootView.findViewById(R.id.event_location);
        editMaxParticipants = rootView.findViewById(R.id.max_participants);
        posterImageView = rootView.findViewById(R.id.event_poster_image);
        //remainder here , add the limit number to set up maximum entrant in witinglist
        editTextImageUrl = rootView.findViewById(R.id.event_image_url);
        createEventButton= rootView.findViewById(R.id.button_create_event);
        QRCode = rootView.findViewById(R.id.qr_image_view);
        geolocationSwitch = rootView.findViewById(R.id.geolocation_switch);
        geolocationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isGeolocationRequired = isChecked;
            if(isChecked){
                Toast.makeText(getContext(),"Geolocation requirement enabled.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getContext(),"Geolocation requiremt diabled",Toast.LENGTH_SHORT).show();
            }
        });




        // Set up button to go back to HomeFragment
        Button backButton = rootView.findViewById(R.id.button_back_home);
        backButton.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });
        //set up the create event button
        createEventButton.setOnClickListener(v->{
            createEvent();
        });


        return rootView;
    }
    /**
     * Creates a new event using the information entered in the form fields. The event is saved to Firestore,
     * and if the user does not already have the organizer role, it is assigned to them and updated in Firestore.
     * The method also validates input fields and provides feedback to the user in case of errors.
     */
    private void createEvent(){


        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String imageUrl = editTextImageUrl.getText().toString().trim();
        String locationStr = editTextLocation.getText().toString().trim();
        String maxParticipantsStr= editMaxParticipants.getText().toString().trim();
        String waitingListLimitStr = editWaitingListLimit.getText().toString().trim();// make it to int later

        //error handling
        if(TextUtils.isEmpty(title)||TextUtils.isEmpty(description)||TextUtils.isEmpty(locationStr)
                ||TextUtils.isEmpty(maxParticipantsStr)){
            Toast.makeText(getContext(),"Please fill all the required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        //handle participant
        int maxParticipants;
        try{
            maxParticipants = Integer.parseInt(maxParticipantsStr);
            if(maxParticipants<=0) throw new NumberFormatException();
        }catch(NumberFormatException e){
            Toast.makeText(getContext(),"Please enter valid participant", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer waitingListLimit = null;
        if (!TextUtils.isEmpty(waitingListLimitStr)) {
            try {
                waitingListLimit = Integer.parseInt(waitingListLimitStr);
                if (waitingListLimit <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter a valid number for the waiting list limit", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        //location, revise later after location class implemented
        //Location location = new Location(locationStr);

        User currentUser = UserManager.getInstance().getCurrentUser();
        if(currentUser == null){
            Toast.makeText(getContext(),"User not found",Toast.LENGTH_SHORT).show();
            return;
        }
        //generate event id
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference eventRef = db.collection("Events");
        DocumentReference newEventRef = eventRef.document();
        String eventId = newEventRef.getId();
        Event event = new Event(eventId, title, description,imageUrl,System.currentTimeMillis(),locationStr,maxParticipants,currentUser.getDeviceID());
        currentEvent = event;

        event.setGeolocationRequired(isGeolocationRequired);



        WaitingList waitingList = new WaitingList(eventId);
        waitingList.setMaxParticipants(maxParticipants);
        if(waitingListLimit != null){
            waitingList.setWaitingListLimit(waitingListLimit);
        }

        //assign organizer to the current user

        if(!currentUser.hasRole(Role.ORGANIZER)){
            currentUser.addRole(Role.ORGANIZER);
            roleAssigned= true;


        }


        //update role in firebase
        if (roleAssigned) {
            db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("Users");
            usersRef.document(currentUser.getDeviceID())
                    .update("roles", currentUser.getRoles())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "User role updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to update user role", Toast.LENGTH_SHORT).show();
                    });
        }
        if (currentEvent != null && TextUtils.isEmpty(currentEvent.getImageUrl())) {
            currentEvent.uploadDefaultPoster(title)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("EventCreateFragment", "Default poster uploaded successfully.");

                        displayCurrentPoster();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("EventCreateFragment", "Failed to upload default poster.", e);
                        Toast.makeText(getContext(), "Failed to upload default poster.", Toast.LENGTH_SHORT).show();
                    });
        }
        Log.d("Create Event Fragment", "Save Event");
        event.saveEventDataToFirestore()
                .addOnSuccessListener(aVoid -> {
//                    event
                    QRCode.setImageBitmap(qrCodeGenerator.generateAndSendBackQRCode(event.getEventId()));
//                    generateAndDisplayQRCode(event.getEventId());
                    Log.d("Event", "Event successfully saved!");
                })
                .addOnFailureListener(e -> Log.e("Event", "Error saving event", e));




    }


    /**
     * Clears the input fields in the event creation form, resetting them to their default empty state.
     */

    private void clearEventForm(){
        editTextTitle.setText("");
        editMaxParticipants.setText("");
        editTextLocation.setText("");
        editTextImageUrl.setText("");
        editTextLocation.setText("");
    }

    private void generateAndDisplayQRCode(String eventID) {
        // URL to be encoded into the QR code (example URL with eventId)
        String event = eventID;
        String hashInput = eventID + Calendar.getInstance().getTime();
        String qrCodeHash = qrCodeGenerator.createQRCodeHash(hashInput);
        String eventUrl = "eventbooking://eventDetail?eventID=" + eventID + "?hash=" + qrCodeHash;

        // Generate QR code using the QRcodeGenerator class
        Bitmap qrCodeBitmap = qrCodeGenerator.generateQRCode(eventUrl);

        if (qrCodeBitmap != null) {
            QRCode.setImageBitmap(qrCodeBitmap);

            qrCodeGenerator.saveQRCode(qrCodeBitmap, event);

            Toast.makeText(getContext(), "QR code generated and saved.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to generate QR code.", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayCurrentPoster() {
        if (currentEvent == null) {
            posterImageView.setImageResource(R.drawable.placeholder_image_foreground);
            return;
        }

        String posterUrl = currentEvent.getImageUrl();
        if (posterUrl == null || posterUrl.isEmpty()) {
            posterImageView.setImageResource(R.drawable.placeholder_image_foreground);
        } else {
            Picasso.get()
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_image_foreground)
                    .error(R.drawable.error_image_foreground)
                    .into(posterImageView);
        }
    }


}
