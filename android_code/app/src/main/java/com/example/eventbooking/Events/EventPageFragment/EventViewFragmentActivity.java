package com.example.eventbooking.Events.EventPageFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventView.EventViewActivity;
import com.example.eventbooking.Home.HomeActivity;
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

public class EventViewFragmentActivity extends Fragment {

    private Button addFacilityButton, backButton;
    private String currentUserId;
    private ArrayList<Event> eventList;
    private boolean testing = true;
    private Switch eventSwitch;
    private ListView eventListView;
    private FirebaseFirestore db;
    private User currentUser;
    private EventViewAdapter eventAdapter;
    private UserManager userManager;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_activity_view, container, false);
        Log.d("Event View Activity", "Launched Fragment");
        backButton = view.findViewById(R.id.button_back_home);
        addFacilityButton = view.findViewById(R.id.add_button);
        eventSwitch = view.findViewById(R.id.event_switch);
        eventListView = view.findViewById(R.id.event_list_view);

        // Initialize data
        eventList = new ArrayList<>();
        eventAdapter = new EventViewAdapter(getContext(), eventList, true);
        eventListView.setAdapter(eventAdapter);

        // Fetch the current user and events
        userManager = UserManager.getInstance();
        currentUser = userManager.getCurrentUser();

        // Set up switch listener
        eventSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updateEventList(isChecked));

        // Add facility button listener
        addFacilityButton.setOnClickListener(v -> {
            if (testing) {
                navigateToEventCreation();
            }

            if (!userManager.userHasFacility()) {
                promptCreateFacility();
            } else {
                navigateToEventCreation();
            }
        });

        // List item click listener
        eventListView.setOnItemClickListener((parent, view1, position, id) -> {
            Event selectedEvent = eventList.get(position);
            String userDeviceId = userManager.getCurrentUser().getDeviceID();

            if (eventSwitch.isChecked()) {
                // Navigate to EventViewFragment
                navigateToEventView(selectedEvent, userDeviceId);
            } else {
                // Navigate to OrganizerMenuFragment
                navigateToOrganizerMenu(selectedEvent);
            }
        });

        // Initial load of events
        updateEventList(eventSwitch.isChecked());

        // Back button listener
        backButton.setOnClickListener(v -> startActivity(new Intent(getContext(), HomeActivity.class))); // Close activity and return to previous screen




        return view;
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

//        Bundle bundle = new Bundle();
//        bundle.putString("eventId", selectedEvent.getEventId());
//        bundle.putString("deviceId", userDeviceId);
//        bundle.putString("listAdd", user1list);
//
//        com.example.eventbooking.Events.EventView.EventViewFragment eventViewFragment = new com.example.eventbooking.Events.EventView.EventViewFragment();
//        eventViewFragment.setArguments(bundle);
//
//        // Here you can start a new activity or replace fragments in the activity
//        getParentFragmentManager().beginTransaction()
//                .replace(R.id.event_container, eventViewFragment)
//                .addToBackStack(null)
//                .commit();

//        startActivity(new Intent(getContext(), EventViewActivity.class));
        Intent intent;
        intent = new Intent(getContext(), EventViewActivity.class);
        intent.putExtra("eventId", selectedEvent.getEventId());
        intent.putExtra("deviceId", userDeviceId);
        intent.putExtra("source_file", "EventActivity");
        intent.putExtra("listAdd", user1list);
        startActivity(intent);
    }

    private void navigateToOrganizerMenu(Event selectedEvent) {
        Bundle bundle = new Bundle();
        bundle.putString("eventId", selectedEvent.getEventId());

        OrganizerMenuFragment organizerMenuFragment = new OrganizerMenuFragment();
        organizerMenuFragment.setArguments(bundle);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.event_container, organizerMenuFragment)
                .addToBackStack(null)
                .commit();
    }

    private void promptCreateFacility() {
        new AlertDialog.Builder(getContext())
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

        getParentFragmentManager().beginTransaction()
                .replace(R.id.event_container, new EventCreateFragment())
                .addToBackStack(null)
                .commit();
    }

    private void navigateToFacilityCreation() {
        // Example of navigating to another fragment in the activity
        ///TODO: Change this into facility creation, fragment or activity?
        getParentFragmentManager().beginTransaction()
                .replace(R.id.event_container, new EventCreateFragment())
                .addToBackStack(null)
                .commit();
    }

    /**
     * Fetches five random events from Firestore for testing purposes.
     * Retrieves events from Firestore, shuffles them, and returns a list of five events.
     *
     * @return Task<List<Event>> task containing a list of five random events
     */
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

                    // Shuffle the list and select the first five events
                    Collections.shuffle(allEvents);
                    List<Event> randomFiveEvents = allEvents.subList(0, Math.min(5, allEvents.size()));
                    String user1DeviceId = "User1";
                    randomFiveEvents.get(0).addAcceptedParticipantId(user1DeviceId);
                    randomFiveEvents.get(1).addCanceledParticipantIds(user1DeviceId);
                    randomFiveEvents.get(2).addSignedUpParticipantIds(user1DeviceId);
                    randomFiveEvents.get(3).addWaitingParticipantIds(user1DeviceId);
                    // Set the result on the TaskCompletionSource
                    taskCompletionSource.setResult(randomFiveEvents);
                })
                .addOnFailureListener(e -> {
                    // Set an empty list or handle the error as needed
                    taskCompletionSource.setException(e);
                });

        return taskCompletionSource.getTask();
    }


}
