package com.example.eventbooking.Events.EventPageFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventbooking.Admin.AdminViewFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventView.EventViewFragment;

import com.example.eventbooking.Home.HomeViewFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventActivity extends AppCompatActivity {
    private Button addFacilityButton, backButton;
    private String currentUserId = "User1";
    private ArrayList<Event> eventList;
    private boolean testing = true;
    private Switch eventSwitch;
    private ListView eventListView;
    private FirebaseFirestore db;
    private User currentUser;
    private EventViewAdapter eventAdapter;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event); // Update with your actual layout name
        Log.d("Event Activity", "Launched Activity");
        Log.d("Event Activity", "Saved Instance State: " + savedInstanceState + ";");
        if (savedInstanceState == null) {
            Log.d("Event Activity", "Inside Saved Instance State: ");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.event_container, new EventViewFragmentActivity())
                    .commit();
        }
        Log.d("Event Activity", "Outside Saved Instance State: ");

//        // Set up views
//        backButton = findViewById(R.id.button_back_home);
//        addFacilityButton = findViewById(R.id.add_button);
//        eventSwitch = findViewById(R.id.event_switch);
//        eventListView = findViewById(R.id.event_list_view);
//
//        // Initialize data
//        eventList = new ArrayList<>();
//        eventAdapter = new EventViewAdapter(this, eventList, true);
//        eventListView.setAdapter(eventAdapter);
//
//        // Fetch the current user and events
//        userManager = UserManager.getInstance();
//        currentUser = userManager.getCurrentUser();
//
//        // Set up switch listener
//        eventSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updateEventList(isChecked));
//
//        // Add facility button listener
//        addFacilityButton.setOnClickListener(v -> {
//            if (testing) {
//                navigateToEventCreation();
//            }
//
//            if (!userManager.userHasFacility()) {
//                promptCreateFacility();
//            } else {
//                navigateToEventCreation();
//            }
//        });
//
//        // List item click listener
//        eventListView.setOnItemClickListener((parent, view, position, id) -> {
//            Event selectedEvent = eventList.get(position);
//            String userDeviceId = userManager.getCurrentUser().getDeviceID();
//
//            if (eventSwitch.isChecked()) {
//                // Navigate to EventViewFragment
//                navigateToEventView(selectedEvent, userDeviceId);
//            } else {
//                // Navigate to OrganizerMenuFragment
//                navigateToOrganizerMenu(selectedEvent);
//            }
//        });
//
//        // Initial load of events
//        updateEventList(eventSwitch.isChecked());
//
//        // Back button listener
//        backButton.setOnClickListener(v -> finish()); // Close activity and return to previous screen
    }

    private void updateEventList(boolean showUserEvents) {
        eventList.clear();

        // Temporary fix to add random events for testing
        getFiveRandomEvents().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Event> randomEvents = task.getResult();
                eventList.addAll(randomEvents);
                eventAdapter.notifyDataSetChanged();
            } else {
                Log.e("EventActivity", "Error fetching events", task.getException());
            }
        });

        if (showUserEvents) {
            addFacilityButton.setVisibility(View.GONE);
        } else {
            addFacilityButton.setVisibility(View.VISIBLE);
        }
    }

    private void navigateToEventView(Event selectedEvent, String userDeviceId) {
        String user1list = "";
        if (selectedEvent.getAcceptedParticipantIds().contains("User1"))
            user1list = "Accepted";
        if (selectedEvent.getWaitingParticipantIds().contains("User1"))
            user1list = "Waiting";
        if (selectedEvent.getCanceledParticipantIds().contains("User1"))
            user1list = "Canceled";
        if (selectedEvent.getSignedUpParticipantIds().contains("User1"))
            user1list = "SignedUp";

        Bundle bundle = new Bundle();
        bundle.putString("eventId", selectedEvent.getEventId());
        bundle.putString("deviceId", userDeviceId);
        bundle.putString("listAdd", user1list);

        EventViewFragment eventViewFragment = new EventViewFragment();
        eventViewFragment.setArguments(bundle);

        // Here you can start a new activity or replace fragments in the activity
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.event_container, eventViewFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToOrganizerMenu(Event selectedEvent) {
        Bundle bundle = new Bundle();
        bundle.putString("eventId", selectedEvent.getEventId());

        OrganizerMenuFragment organizerMenuFragment = new OrganizerMenuFragment();
        organizerMenuFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.event_container, organizerMenuFragment)
                .addToBackStack(null)
                .commit();
    }

    private void promptCreateFacility() {
        new AlertDialog.Builder(this)
                .setTitle("No Facility Associated")
                .setMessage("You don't have an associated facility. Would you like to create one?")
                .setPositiveButton("Create", (dialog, which) -> navigateToFacilityCreation())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Navigates to EventCreateFragment to allow the user to create a new event.
     */
    private void navigateToEventCreation() {
        // Example of navigating to another fragment in the activity
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.event_container, new EventCreateFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToFacilityCreation() {
        // Example of navigating to another fragment in the activity
        ///TODO: Change this into facility creation
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.event_container, new EventCreateFragment())
                .addToBackStack(null)
                .commit();
    }

    public Task<List<Event>> getFiveRandomEvents() {
        TaskCompletionSource<List<Event>> taskCompletionSource = new TaskCompletionSource<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Event> allEvents = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Event event = document.toObject(Event.class);
                        allEvents.add(event);
                    }

                    // Shuffle and return five events
                    Collections.shuffle(allEvents);
                    List<Event> randomFiveEvents = allEvents.subList(0, Math.min(5, allEvents.size()));
                    taskCompletionSource.setResult(randomFiveEvents);
                })
                .addOnFailureListener(e -> taskCompletionSource.setException(e));

        return taskCompletionSource.getTask();
    }
}
