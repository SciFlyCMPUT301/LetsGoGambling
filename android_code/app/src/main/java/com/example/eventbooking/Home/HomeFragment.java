package com.example.eventbooking.Home;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventPageFragment.EventFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.example.eventbooking.notification.NotificationFragment;
import com.example.eventbooking.profile.ProfileFragment;

// For now let the home page be where all users end up after sign up or device recognized


public class HomeFragment extends Fragment {
    private int someInteger = 42; // Example integer to pass

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);


        // Display the integer
        TextView integerTextView = rootView.findViewById(R.id.home_integer_text);
        integerTextView.setText("Integer: " + someInteger);
        TextView page_name = rootView.findViewById(R.id.home_title);

        // Set up buttons to navigate to other fragments
        Button eventCreateButton = rootView.findViewById(R.id.button_event_create);
        eventCreateButton.setOnClickListener(v -> {
            // Navigate to EventCreateFragment and pass the integer
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, EventCreateFragment.newInstance(someInteger))
                    .commit();
        });

        // Repeat for other buttons
        Button eventButton = rootView.findViewById(R.id.button_event);
        eventButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, EventFragment.newInstance(someInteger))
                    .commit();
        });

        Button notificationButton = rootView.findViewById(R.id.button_notification);
        notificationButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, NotificationFragment.newInstance(someInteger))
                    .commit();
        });

        Button profileButton = rootView.findViewById(R.id.button_profile);
        profileButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment.newInstance(someInteger))
                    .commit();
        });





        return rootView;
    }
}

