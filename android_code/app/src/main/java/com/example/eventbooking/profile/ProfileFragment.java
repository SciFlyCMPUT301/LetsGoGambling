package com.example.eventbooking.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.notification.NotificationFragment;


public class ProfileFragment extends Fragment {

    private static final String ARG_INTEGER = "arg_integer";
    private int receivedInteger;
    /**
     * Creates a new instance of ProfileFragment.
     *
     * @return A new instance of ProfileFragment
     */

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
//        Bundle args = new Bundle();
//        args.putInt(ARG_INTEGER, integer);
//        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Initializes the fragment by retrieving any passed arguments.
     *
     * @param savedInstanceState The saved state of the fragment, if any
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the integer from arguments
        if (getArguments() != null) {
            receivedInteger = getArguments().getInt(ARG_INTEGER);
        }
    }
    /**
     * Inflates the layout for the fragment and sets up the UI components.
     *
     * @param inflater           The LayoutInflater object used to inflate the view
     * @param container          The container that will hold the fragment's view
     * @param savedInstanceState The saved state of the fragment, if any
     * @return The inflated view for the fragment
     */

    // Inflate the layout and display the integer
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for the fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        // Display the received integer on the UI
        TextView integerTextView = rootView.findViewById(R.id.profile_integer_text);
        integerTextView.setText("Integer: " + receivedInteger);
        // Set the page title text
        TextView page_name = rootView.findViewById(R.id.profile_title);
        // Set up button to go back to NavigationFragment
        Button notification_button = rootView.findViewById(R.id.notification_button);
        notification_button.setOnClickListener(v -> {
            // Navigate back to NavigationFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new NotificationFragment())
                    .commit();
        });

        Button backButton = rootView.findViewById(R.id.button_back_home);
        backButton.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });
        // Return the root view for the fragment
        return rootView;
    }



}
