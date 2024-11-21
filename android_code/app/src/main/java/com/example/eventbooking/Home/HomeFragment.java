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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventbooking.Admin.Users.UserViewAdapter;
import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventPageFragment.EventFragment;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.notification.NotificationFragment;
import com.example.eventbooking.profile.ProfileEntrantFragment;
import com.example.eventbooking.profile.ProfileFragment;
import com.example.eventbooking.Events.EventData.Event;

import java.util.ArrayList;

// For now let the home page be where all users end up after sign up or device recognized


/**
 * HomeFragment serves as the main landing page for users after login or device recognition.
 * It displays the user's options, including event creation, viewing events, notifications, and profile management.
 */
public class HomeFragment extends Fragment {
    private int someInteger = 42; // Example integer to pass
    private String userId;

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
        String currentUserId = UserManager.getInstance().getUserId();

        Event.getUserEvents(currentUserId, userEvents -> {
            HomeUserEventAdapter adapter = new HomeUserEventAdapter(getContext(), userEvents, currentUserId);
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
}

