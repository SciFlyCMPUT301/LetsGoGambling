package com.example.eventbooking.Events.EventPageFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeFragment;
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
/**
 * Fragment to display all events based on user role and context.
 */
public class EventFragment extends Fragment {
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
    /**
     * Creates a new instance of EventFragment.
     * Useful for testing and modular initialization.
     *
     * @return A new instance of the EventFragment class.
     */
    public static EventFragment newInstance() {
        EventFragment fragment = new EventFragment();
        return fragment;
    }
    /**
     * Called to do initial creation of the fragment.
     * This is where any arguments or initial setup should be handled.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous state, this contains the most recent data.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Inflates the fragment layout and initializes UI elements and listeners.
     *
     * @param inflater           the LayoutInflater object that can be used to inflate
     *                           any views in the fragment
     * @param container          the parent view that the fragment's UI should be attached to
     * @param savedInstanceState if non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here
     * @return the View for the fragment's UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        // Starting from here views
        backButton = view.findViewById(R.id.button_back_home);
        eventList = new ArrayList<>();
        eventSwitch = view.findViewById(R.id.event_switch);
        eventListView = view.findViewById(R.id.event_list_view);
        addFacilityButton = view.findViewById(R.id.add_button);

        // Set up adapter for ListView
        eventAdapter = new EventViewAdapter(getContext(), eventList, true);
        eventListView.setAdapter(eventAdapter);

        // Fetch the current user and events from UserManager
        userManager = UserManager.getInstance();
        currentUser = userManager.getCurrentUser();


        // Set up switch listener to toggle between event views
        eventSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updateEventList(isChecked));

        // Set up add button to prompt facility creation if necessary
        addFacilityButton.setOnClickListener(v -> {

            // Supersceeding if statement for testing
            if(testing){
                navigateToEventCreate();
            }

            //Normal expected pathway
            if (!userManager.userHasFacility()) {
                promptCreateFacility();
            } else {
                navigateToEventCreate();
            }
        });

        // Set up list item click to navigate to event details if user-related events
        eventListView.setOnItemClickListener((parent, view1, position, id) -> {
            Event selectedEvent = eventList.get(position);
            String userDeviceId = UserManager.getInstance().getCurrentUser().getDeviceID(); // Assuming UserManager provides the current user

            if (eventSwitch.isChecked()) {
                // User Event List: Navigate to EventViewFragment
                String user1list = "";
                if(selectedEvent.getAcceptedParticipantIds().contains("User1"))
                    user1list = "Accepted";
                if(selectedEvent.getWaitingParticipantIds().contains("User1"))
                    user1list = "Waiting";
                if(selectedEvent.getCanceledParticipantIds().contains("User1"))
                    user1list = "Canceled";
                if(selectedEvent.getSignedUpParticipantIds().contains("User1"))
                    user1list = "SignedUp";

                Bundle bundle = new Bundle();
                bundle.putString("eventId", selectedEvent.getEventId());
//                Log.e("eventId", "Event found with ID: " + selectedEvent.getEventId());
                bundle.putString("deviceId", userDeviceId);

                // remove the list add when actually using the app
                bundle.putString("listAdd", user1list);

                EventViewFragment eventViewFragment = new EventViewFragment();
                eventViewFragment.setArguments(bundle);

                // Replace current fragment with EventViewFragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, eventViewFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                // Organizer Event List: Navigate to OrganizerMenuFragment
                Bundle bundle = new Bundle();
                bundle.putString("eventId", selectedEvent.getEventId());
//                Log.e("Bundle", "Event ID: " + selectedEvent.getEventId());
                OrganizerMenuFragment organizerMenuFragment = new OrganizerMenuFragment();
                organizerMenuFragment.setArguments(bundle);

                // Replace current fragment with OrganizerMenuFragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, organizerMenuFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        // Initial load of events, defaulting to user events
        updateEventList(eventSwitch.isChecked());


        backButton.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        return view;
    }
   //test
    private User getUserData() {
        User Johhny = new User("deviceID1", "User1", "admin1@example.com", "555-001", null);
        Johhny.addRole("admin");
        UserManager temp = UserManager.getInstance();
        temp.setCurrentUser(Johhny);
        return Johhny;  // Example user, replace with actual Firebase logic
    }

    /**
     * Updates the event list based on the current user's role (user or organizer).
     * Fetches events from Firestore and updates the ListView adapter.
     *
     * @param showUserEvents true if displaying user-related events, false for organizer events.
     */
    private void updateEventList(boolean showUserEvents) {
        eventList.clear();
        /// TODO:
        // Temporary fix to just fill the data, delete this later!!!
        getFiveRandomEvents().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Event> randomEvents = task.getResult();
                if (randomEvents != null) {
//                        Log.d("EventList", "Event Title: " + event.getEventTitle() + ", Description: " + event.getDescription());
//                    }
                } else {
                    Log.d("EventList", "No events found");
                }
                eventList.addAll(randomEvents);
                eventAdapter.notifyDataSetChanged();
            } else {
                Exception e = task.getException();
                // Handle the error as needed
            }
        });
        if (showUserEvents){
            addFacilityButton.setVisibility(View.GONE);
        }
        else{
            addFacilityButton.setVisibility(View.VISIBLE);
        }

        eventAdapter.notifyDataSetChanged();
    }
    /**
     * Prompts the user to create a facility if they do not have one.
     */
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
    // Navigation method for event creation
    private void navigateToEventCreate() {
        if (getParentFragmentManager() != null && getContext() != null) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new EventCreateFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
    /**
     * Navigates to the facility creation fragment.
     */
    // Navigation method for facility creation
    private void navigateToFacilityCreation() {
        if (getParentFragmentManager() != null && getContext() != null) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            ///TODO:
            //Here replace this fragment with the facility fragment
            transaction.replace(R.id.fragment_container, new EventCreateFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    /**
     * Finds events where the participant is associated with the given device ID.
     *
     * @param deviceID The participant's device ID.
     * @return List of events associated with the participant.
     */
    public List<Event> findEventsByParticipantDeviceId(String deviceID) {
        List<Event> participantEvents = new ArrayList<>();
        for (Event event : eventList) {
            if (event.getWaitingList().getWaitingParticipantIds().contains(deviceID) ||
                    event.getAcceptedParticipantIds().contains(deviceID) ||
                    event.getCanceledParticipantIds().contains(deviceID) ||
                    event.getSignedUpParticipantIds().contains(deviceID)) {
                participantEvents.add(event);
                System.out.println("Added event");
            }
        }
        return participantEvents;
    }

    /**
     * Finds events organized by the user with the specified device ID.
     *
     * @param deviceID The organizer's device ID.
     * @return List of events organized by the user.
     */

    public List<Event> findEventsByOrganizerDeviceId(String deviceID) {
        List<Event> organizerEvents = new ArrayList<>();
        for (Event event : eventList) {
            if (deviceID.equals(event.getOrganizerId())) {
                organizerEvents.add(event);
            }
        }
        return organizerEvents;
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
