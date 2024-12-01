package com.example.eventbooking.Home;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventbooking.Admin.Users.UserViewAdapter;
import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventPageFragment.EventFragment;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.MainActivity;
import com.example.eventbooking.R;
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.notification.NotificationFragment;
import com.example.eventbooking.profile.ProfileEntrantFragment;
import com.example.eventbooking.profile.ProfileFragment;
import com.example.eventbooking.Events.EventData.Event;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// For now let the home page be where all users end up after sign up or device recognized


/**
 * HomeFragment serves as the main landing page for users after login or device recognition.
 * It displays the user's options, including event creation, viewing events, notifications, and profile management.
 */
public class HomeFragment extends Fragment {
    private int someInteger = 42; // Example integer to pass
    private String userId;
    private SearchView searchView;
    private Spinner filterSpinner;
    private List<Event> allEvents;
    private HomeUserEventAdapter adapter;
    private ListView usersEventListView;

    // UI Test Parameters here
    private Boolean testMode = false;
    private String testDeviceID;
    private List<Event> eventList;
    /**
     * Creates a new instance of HomeFragment with the provided user ID.
     * @param userId
     * @return
     */
    public static HomeFragment newInstance(String userId) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

//    /**
//     * Creates a bundle that we can get testing information off of to allow easier UI testing
//     * @param testMode
//     * @param deviceID
//     * @param eventList
//     * @return
//     */
//    public static HomeFragment newInstance(Boolean testMode, String deviceID, List <Event> eventList) {
//        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putBoolean("testMode", testMode);
//        args.putString("deviceID", deviceID);
//        args.putParcelableArrayList("eventList", new ArrayList<>(eventList));
//        fragment.setArguments(args);
//        return fragment;
//    }


    /**
     * Inflates the fragment layout, initializes UI components, and sets up button click listeners for navigation.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Home Fragment", "Home Fragment Launch");
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initalizeUI(rootView);

        if(!testMode){
            Event.getUserEvents(userId, userEvents -> {
                if (isAdded() && getActivity() instanceof MainActivity) {
                    allEvents = new ArrayList<>(userEvents);
                    adapter = new HomeUserEventAdapter(getContext(), userEvents, userId);
                    usersEventListView.setAdapter(adapter);
                    setupSearchFilter();
                    // Set item click listener
                    usersEventListView.setOnItemClickListener((parent, view, position, id) -> {
                        Event selectedEvent = userEvents.get(position);
                        EventViewFragment eventViewFragment = EventViewFragment.newInstance(selectedEvent.getEventId(), userId);

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, eventViewFragment)
                                .addToBackStack(null) // Ensures returning to HomeFragment
                                .commit();
                    });
                }
            }, e -> {
                Log.e("HomeFragment", "Failed to fetch events: " + e.getMessage());
            });
        }

        else{

            allEvents = new ArrayList<>(eventList);
            adapter = new HomeUserEventAdapter(getContext(), eventList, testDeviceID);
            usersEventListView.setAdapter(adapter);
            setupSearchFilter();
            // Set item click listener
            usersEventListView.setOnItemClickListener((parent, view, position, id) -> {
                Event selectedEvent = eventList.get(position);
                EventViewFragment eventViewFragment = EventViewFragment.newInstance(selectedEvent.getEventId(), testDeviceID);
                UniversalProgramValues.getInstance().setCurrentEvent(selectedEvent);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, eventViewFragment)
                        .addToBackStack(null) // Ensures returning to HomeFragment
                        .commit();
            });

        }



        return rootView;
    }


    private void setupSearchFilter() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterEvents(query, filterSpinner.getSelectedItem().toString());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterEvents(newText, filterSpinner.getSelectedItem().toString());
                return true;
            }
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterEvents(searchView.getQuery().toString(), parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void filterEvents(String query, String filter) {
        if (query.isEmpty()) {
            adapter.updateEvents(allEvents);
            return;
        }

        List<Event> filteredEvents = allEvents.stream().filter(event -> {
            switch (filter.toLowerCase()) {
                case "title":
                    return event.getEventTitle().toLowerCase().contains(query.toLowerCase());
                case "description":
                    return event.getDescription().toLowerCase().contains(query.toLowerCase());
                case "location":
                    return event.getLocation().toLowerCase().contains(query.toLowerCase());
                default:
                    return false;
            }
        }).collect(Collectors.toList());

        adapter.updateEvents(filteredEvents);
    }


    private void initalizeUI(View rootView){
        ((MainActivity) getActivity()).showNavigationUI();
        testMode = UniversalProgramValues.getInstance().getTestingMode();
        if(testMode){
            eventList = UniversalProgramValues.getInstance().getEventList();
            testDeviceID = UniversalProgramValues.getInstance().getDeviceID();
        }
        if(getArguments() != null){

            if(getArguments().getString("deviceID") != null)
                testDeviceID = getArguments().getString("deviceID");

        }
        usersEventListView = rootView.findViewById(R.id.user_events_list);
        searchView = rootView.findViewById(R.id.search_bar);
        filterSpinner = rootView.findViewById(R.id.filter_spinner);
        userId = UserManager.getInstance().getUserId();



    }
}

