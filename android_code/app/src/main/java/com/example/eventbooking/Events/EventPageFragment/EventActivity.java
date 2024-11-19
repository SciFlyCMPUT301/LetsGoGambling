package com.example.eventbooking.Events.EventPageFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.MainActivity;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import androidx.appcompat.app.AlertDialog;

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
        setContentView(R.layout.activity_event); // Set the Activity layout

        currentUser = getUserData();
        userManager = UserManager.getInstance();
        userManager.setCurrentUser(currentUser);

        // Initialize UI components
        backButton = findViewById(R.id.button_back_home);
        eventList = new ArrayList<>();
        eventSwitch = findViewById(R.id.event_switch);
        eventListView = findViewById(R.id.event_list_view);
        addFacilityButton = findViewById(R.id.add_button);

        // Set up adapter for ListView
        eventAdapter = new EventViewAdapter(this, eventList, true);
        eventListView.setAdapter(eventAdapter);

        // Set up switch listener to toggle between event views
        eventSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updateEventList(isChecked));

        // Set up add button to prompt facility creation if necessary
        addFacilityButton.setOnClickListener(v -> {
            if (testing) {
                navigateToEventCreate();
            }

            // Normal expected pathway
            if (!userManager.userHasFacility()) {
                promptCreateFacility();
            } else {
                navigateToEventCreate();
            }
        });

        // Set up list item click to navigate to event details if user-related events
        eventListView.setOnItemClickListener((parent, view, position, id) -> {
            Event selectedEvent = eventList.get(position);
            String userDeviceId = UserManager.getInstance().getCurrentUser().getDeviceID();

            if (eventSwitch.isChecked()) {
                // User Event List: Navigate to EventViewFragment
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

                // You can continue to use FragmentTransaction if you still want to load these as fragments inside an Activity
                EventViewFragment eventViewFragment = new EventViewFragment();
                eventViewFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, eventViewFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                // Organizer Event List: Navigate to OrganizerMenuFragment
                Bundle bundle = new Bundle();
                bundle.putString("eventId", selectedEvent.getEventId());

                OrganizerMenuFragment organizerMenuFragment = new OrganizerMenuFragment();
                organizerMenuFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, organizerMenuFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Initial load of events, defaulting to user events
        updateEventList(eventSwitch.isChecked());

        ///TODO: Fix this for later such that HomeActivity makes more sense and not some garbage
        // Back button listener to return to HomeActivity (or HomeFragment if embedded)
        backButton.setOnClickListener(v -> {
            // Navigate back to HomeActivity
            startActivity(new Intent(EventActivity.this, MainActivity.class));
            finish();
        });
    }

    private void updateEventList(boolean showUserEvents) {
        eventList.clear();
        getFiveRandomEvents().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Event> randomEvents = task.getResult();
                if (randomEvents != null) {
                    eventList.addAll(randomEvents);
                }
                eventAdapter.notifyDataSetChanged();
            } else {
                // Handle error
            }
        });

        if (showUserEvents) {
            addFacilityButton.setVisibility(View.GONE);
        } else {
            addFacilityButton.setVisibility(View.VISIBLE);
        }
    }

    private void promptCreateFacility() {
        new AlertDialog.Builder(this)
                .setTitle("No Facility Associated")
                .setMessage("You don't have an associated facility. Would you like to create one?")
                .setPositiveButton("Create", (dialog, which) -> navigateToFacilityCreation())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void navigateToEventCreate() {
        // Navigate to EventCreateActivity or EventCreateFragment within this Activity
        EventCreateFragment eventCreateFragment = new EventCreateFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, eventCreateFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToFacilityCreation() {
        // Navigate to Facility creation screen (if needed)
        // FacilityCreateFragment or Activity logic goes here
    }

    private User getUserData() {
        User Johhny = new User("deviceID1", "User1", "admin1@example.com", "555-001", null);
        Johhny.addRole("admin");
        UserManager temp = UserManager.getInstance();
        temp.setCurrentUser(Johhny);
        return Johhny;
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

                    // Shuffle and select five random events
                    Collections.shuffle(allEvents);
                    List<Event> randomFiveEvents = allEvents.subList(0, Math.min(5, allEvents.size()));
                    taskCompletionSource.setResult(randomFiveEvents);
                })
                .addOnFailureListener(e -> taskCompletionSource.setException(e));

        return taskCompletionSource.getTask();
    }
}