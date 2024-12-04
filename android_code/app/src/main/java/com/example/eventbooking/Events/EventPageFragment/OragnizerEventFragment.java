package com.example.eventbooking.Events.EventPageFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventCreate.EventEditFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventView.OrganizerEventDetailFragment;
import com.example.eventbooking.Facility.FacilityCreateFragment;
import com.example.eventbooking.Facility.ViewFacilityFragment;
import com.example.eventbooking.Home.HomeUserEventAdapter;
import com.example.eventbooking.R;
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code OragnizerEventFragment} class represents a UI fragment for organizers to manage their events.
 * It displays a list of events created by the organizer, provides navigation to event details,
 * and includes options for creating events and facilities.
 */
@SuppressWarnings("all")
public class OragnizerEventFragment  extends Fragment{
    private int someInteger = 42; // Example integer to pass
    private String userId;
    private ListView usersEventListView;
    private List<Event> organizerEventList;
    //private Button addEvent;

    /**
     * Factory method to create a new instance of {@code OragnizerEventFragment} with a specified user ID.
     *
     * @param userId The ID of the current user.
     * @return A new instance of {@code OragnizerEventFragment}.
     */
    public static OragnizerEventFragment newInstance(String userId) {
        OragnizerEventFragment fragment = new OragnizerEventFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Inflates the fragment layout, initializes UI components, and sets up button click listeners for navigation.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The root view for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("HomeFragment", "Organizer Event Fragment Launch");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_organizer_event, container, false);

        usersEventListView = rootView.findViewById(R.id.user_events_list);
        String currentUserId = UserManager.getInstance().getUserId();

        // Fetch events for the organizer
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            Event.getOrganizerEvents(currentUserId, organizerEvents -> {
                HomeUserEventAdapter adapter = new HomeUserEventAdapter(getContext(), organizerEvents, currentUserId);
                usersEventListView.setAdapter(adapter);

                // Set item click listener to navigate to EventViewFragment
                usersEventListView.setOnItemClickListener((parent, view, position, id) -> {
                    Event selectedEvent = organizerEvents.get(position);
                    // EventViewFragment eventViewFragment = EventViewFragment.newInstance(selectedEvent.getEventId(), currentUserId);
                    EventEditFragment eventEditFragment = EventEditFragment.newInstance(selectedEvent);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, eventEditFragment)
                            .addToBackStack(null) // Ensures returning to OragnizerEventFragment
                            .commit();
//                    OrganizerEventDetailFragment eventDetailView = OrganizerEventDetailFragment.newInstance(selectedEvent.getEventId());
//                    getParentFragmentManager().beginTransaction()
//                            .replace(R.id.fragment_container, eventDetailView)
//                            .addToBackStack(null) // Ensures returning to OragnizerEventFragment
//                            .commit();
                });
            }, e -> {
                Log.e("OragnizerEventFragment", "Failed to fetch events: " + e.getMessage());
            });
        }
        else{
            organizerEventList = new ArrayList<>();
            organizerEventList.addAll(UniversalProgramValues.getInstance().getEventList());
            HomeUserEventAdapter adapter = new HomeUserEventAdapter(getContext(), organizerEventList, currentUserId);
            usersEventListView.setAdapter(adapter);

            // Set item click listener to navigate to EventViewFragment
            usersEventListView.setOnItemClickListener((parent, view, position, id) -> {
                Event selectedEvent = organizerEventList.get(position);
                // EventViewFragment eventViewFragment = EventViewFragment.newInstance(selectedEvent.getEventId(), currentUserId);
//                OrganizerEventDetailFragment eventDetailView = OrganizerEventDetailFragment.newInstance(selectedEvent.getEventId());
//                getParentFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, eventDetailView)
//                        .addToBackStack(null) // Ensures returning to OragnizerEventFragment
//                        .commit();
                EventEditFragment editEventFragment = EventEditFragment.newInstance(selectedEvent);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, editEventFragment)
                        .addToBackStack(null)
                        .commit();

            });

        }

        // Button to create a new event
        Button addEventButton = rootView.findViewById(R.id.btn_add_event);
        Button addFacilityButton = rootView.findViewById(R.id.btn_add_facility);
        Button viewFacilityButton = rootView.findViewById(R.id.btn_view_facility);
        if(UserManager.getInstance().getCurrentUser().isFacilityAssociated()){
            addFacilityButton.setVisibility(View.GONE);
            viewFacilityButton.setVisibility(View.VISIBLE);
            addEventButton.setVisibility(View.VISIBLE);
        }
        else{
            addFacilityButton.setVisibility(View.VISIBLE);
            viewFacilityButton.setVisibility(View.GONE);
            addEventButton.setVisibility(View.GONE);
        }
        //set up click listeners
        viewFacilityButton.setOnClickListener(v -> {
            ViewFacilityFragment viewFacilityFragment = new ViewFacilityFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, viewFacilityFragment)
                    .addToBackStack(null)
                    .commit();
        });

        addFacilityButton.setOnClickListener(v -> {
            FacilityCreateFragment facilityCreateFragment = new FacilityCreateFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, facilityCreateFragment)
                    .addToBackStack(null)
                    .commit();
        });

        addEventButton.setOnClickListener(v -> {
            EventCreateFragment eventCreateFragment = new EventCreateFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, eventCreateFragment)
                    .addToBackStack(null)
                    .commit();
        });
        // Return the root view of the fragment
        return rootView;
    }




}
