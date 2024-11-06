package com.example.eventbooking.Events.EventCreate;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Home.HomeFragment;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    //empty constructor
    private boolean roleAssigned = false, testingFlag;
    public EventCreateFragment(){}


    public static EventCreateFragment newInstance(boolean testing) {
        EventCreateFragment fragment = new EventCreateFragment();
        Bundle args = new Bundle();
        args.putBoolean("testing flag", testing);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the integer from arguments
        if (getArguments() != null) {
            testingFlag = getArguments().getBoolean("testing flag");
        }
    }

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
        //remainder here , add the limit number to set up maximum entrant in witinglist
        editTextImageUrl = rootView.findViewById(R.id.event_image_url);
        createEventButton= rootView.findViewById(R.id.button_create_event);

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

        int waitingListLimit;
        try{
            waitingListLimit = Integer.parseInt(waitingListLimitStr);
            if(waitingListLimit<=0) throw new NumberFormatException();
        }catch(NumberFormatException e){
            Toast.makeText(getContext(),"Please enter valid participant", Toast.LENGTH_SHORT).show();
            return;
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

        event.saveEventDataToFirestore();



    }


    private void clearEventForm(){
        editTextTitle.setText("");
        editMaxParticipants.setText("");
        editTextLocation.setText("");
        editTextImageUrl.setText("");
        editTextLocation.setText("");
    }


}
