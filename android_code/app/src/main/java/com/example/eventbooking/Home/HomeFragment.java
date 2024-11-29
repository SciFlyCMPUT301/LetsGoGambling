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
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.notification.NotificationFragment;
import com.example.eventbooking.profile.ProfileEntrantFragment;
import com.example.eventbooking.profile.ProfileFragment;
import com.example.eventbooking.Events.EventData.Event;

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

        ListView usersEventListView = rootView.findViewById(R.id.user_events_list);
        searchView = rootView.findViewById(R.id.search_bar);
        filterSpinner = rootView.findViewById(R.id.filter_spinner);
        String currentUserId = UserManager.getInstance().getUserId();

        Event.getUserEvents(currentUserId, userEvents -> {
            if (isAdded() && getActivity() instanceof MainActivity) {
                allEvents = new ArrayList<>(userEvents);
                adapter = new HomeUserEventAdapter(getContext(), userEvents, currentUserId);
                usersEventListView.setAdapter(adapter);

                // Set item click listener
                usersEventListView.setOnItemClickListener((parent, view, position, id) -> {
                    Event selectedEvent = userEvents.get(position);
                    EventViewFragment eventViewFragment = EventViewFragment.newInstance(selectedEvent.getEventId(), currentUserId);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, eventViewFragment)
                            .addToBackStack(null) // Ensures returning to HomeFragment
                            .commit();
                });
            }
        }, e -> {
            Log.e("HomeFragment", "Failed to fetch events: " + e.getMessage());
        });

//        // Display the integer
//        TextView integerTextView = rootView.findViewById(R.id.home_integer_text);
//        integerTextView.setText("Integer: " + someInteger);
//        TextView page_name = rootView.findViewById(R.id.home_title);
//
//        // Set up buttons to navigate to other fragments
//        Button eventCreateButton = rootView.findViewById(R.id.button_event_create);
//        eventCreateButton.setOnClickListener(v -> {
//            // Navigate to EventCreateFragment and pass the integer
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, EventCreateFragment.newInstance(true))
//                    .commit();
//        });
//
//        // Repeat for other buttons
//        Button eventButton = rootView.findViewById(R.id.button_event);
//        eventButton.setOnClickListener(v -> {
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, EventFragment.newInstance())
//                    .commit();
//        });
//        //set up button for navigate to notification fragment
//        Button notificationButton = rootView.findViewById(R.id.button_notification);
//        notificationButton.setOnClickListener(v -> {
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, NotificationFragment.newInstance())
//                    .commit();
//        });
//        //set up button for navigate to profile fragment
//        Button profileButton = rootView.findViewById(R.id.button_profile);
//        profileButton.setOnClickListener(v -> {
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, ProfileEntrantFragment.newInstance(false, null, UserManager.getInstance().getUserId()))
//                    .commit();
//        });





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
}

