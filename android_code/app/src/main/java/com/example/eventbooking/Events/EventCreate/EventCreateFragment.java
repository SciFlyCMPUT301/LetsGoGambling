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
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventCreateFragment extends Fragment {
    private static final String ARG_INTEGER = "arg_integer";
    private int receivedInteger;
    //UI
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextImageUrl;
    private EditText editTextLocation;
    private EditText editMaxParticipants;
    private Button createEventButton;
    private Button backButton;
    //empty constructor
    private boolean roleAssigned = false;
    public EventCreateFragment(){}


    public static EventCreateFragment newInstance(int integer) {
        EventCreateFragment fragment = new EventCreateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INTEGER, integer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the integer from arguments
        if (getArguments() != null) {
            receivedInteger = getArguments().getInt(ARG_INTEGER);
        }
    }

    // Inflate the layout and display the integer
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_create, container, false);
        //init UI elements
        TextView integerTextView = rootView.findViewById(R.id.event_create_integer_text);
        integerTextView.setText("Integer: " + receivedInteger);
        editTextTitle = rootView.findViewById(R.id.event_create_title);

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
    //set the function createEvent
    // i need soemthing event id
    private void createEvent(){
        //retrive user input

        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String imageUrl = editTextImageUrl.getText().toString().trim();
        String locationStr = editTextLocation.getText().toString().trim();
        String maxParticipantsStr= editMaxParticipants.getText().toString().trim(); // make it to int later

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
        //location, revise later after location class implemented
        //Location location = new Location(locationStr);

        User currentUser = UserManager.getInstance().getCurrentUser();
        if(currentUser == null){
            Toast.makeText(getContext(),"User not found",Toast.LENGTH_SHORT).show();
            return;
        }

        //assign organizer to the current user

        if(!currentUser.hasRole(Role.ORGANIZER)){
            currentUser.addRole(Role.ORGANIZER);
            roleAssigned= true;
            //handle firebase later
            //updateUserInDatabase(currentUser);

        }

        //everything ready, create the event object now
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events");
        String eventId = eventRef.push().getKey(); //get the event id
        if(eventId==null){
            Toast.makeText(getContext(),"Failed to generate event id", Toast.LENGTH_SHORT).show();
            return;
        }
        //modify event to add the paramter current User
        //leave location to that location str now

        Event newEvent = new Event(eventId, title, description,imageUrl,System.currentTimeMillis(),locationStr, maxParticipants,currentUser.getDeviceID());

        //Firebase to save everything at this step
        //save event to firebase
        eventRef.child(eventId).setValue(newEvent).addOnCompleteListener(task->{
            if(task.isSuccessful()){

                Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                clearEventForm();
                //initalize the waitinglist
                initWaitingList(eventId,maxParticipants);
            }
            if(roleAssigned){
                Toast.makeText(getContext(), "Role updated to Organizer.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getContext(), "Failed to create event.", Toast.LENGTH_SHORT).show();

            }
        });


    }

    //update user in firebase
    private void updateUserInDatabase(User user){
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(user.getDeviceID()).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Role updated successfully
                // Optionally, notify user or perform additional actions
            } else {
                Toast.makeText(getContext(), "Failed to update user role.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearEventForm(){
        editTextTitle.setText("");
        editMaxParticipants.setText("");
        editTextLocation.setText("");
        editTextImageUrl.setText("");
        editTextLocation.setText("");
    }
    /**
     * initialize the waiting list */
    private void initWaitingList(String eventId, int maxParticipants){
       //just set it for now, change later
        int waitingListLimit = 50;
        WaitingList waitingList = new WaitingList(eventId,maxParticipants,waitingListLimit);
        //waiting list to firebase,
        DatabaseReference waitingListRef = FirebaseDatabase.getInstance().getReference("waitingLists");
        waitingListRef.child(eventId).setValue(waitingList).addOnCompleteListener(task->{
            if(task.isSuccessful()){
                Toast.makeText(getContext(),"waiting list init success",Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(getContext(),"waiting list init failed",Toast.LENGTH_SHORT).show();
            }
        });

    }

}
