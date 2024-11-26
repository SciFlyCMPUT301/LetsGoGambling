package com.example.eventbooking.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.profile.ProfileFragment;
/**
 * A fragment that displays a notification with an integer value and provides buttons to navigate
 * to the ProfileFragment or HomeFragment.
 */
public class NotificationFragment extends Fragment {
    /**
     * Creates a new instance of NotificationFragment.
     *
     * @return A new instance of NotificationFragment.
     */

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_INTEGER, integer);
//        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Called when the fragment is created. Retrieves the integer argument passed to the fragment.
     *
     * @param savedInstanceState The saved state of the fragment (not used here).
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the integer from arguments
        if (getArguments() != null) {
           // receivedInteger = getArguments().getInt(ARG_INTEGER);
        }
    }
    /**
     * Inflates the fragment layout and sets up the view components.
     *
     * @param inflater           The LayoutInflater object to inflate views.
     * @param container          The container that holds the fragment.
     * @param savedInstanceState The saved state of the fragment (not used here).
     * @return The root view of the fragment.
     */

    // Inflate the layout and display the integer
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        Button profileButton = rootView.findViewById(R.id.button_back_profile);
        profileButton.setOnClickListener(v -> {
            // Replace the current fragment with ProfileFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .commit();
        });

        ListView notificationList = rootView.findViewById(R.id.notification_list);


        // Set up the Back button to navigate to HomeFragment
//        Button backButton = rootView.findViewById(R.id.button_back_home);
//        backButton.setOnClickListener(v -> {
//            // Replace the current fragment with HomeFragment
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, new HomeFragment())
//                    .commit();
//        });
// Return the root view to be displayed
        return rootView;
    }
}
