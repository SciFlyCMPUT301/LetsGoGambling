package com.example.eventbooking.Events.EventCreate;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.profile.ProfileFragment;

public class EventCreateFragment extends Fragment {
    private static final String ARG_INTEGER = "arg_integer";
    private int receivedInteger;

    public static EventCreateFragment newInstance(int integer) {
        EventCreateFragment fragment = new EventCreateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INTEGER, integer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the integer from arguments
        if (getArguments() != null) {
            receivedInteger = getArguments().getInt(ARG_INTEGER);
        }
    }

    // Inflate the layout and display the integer
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_create, container, false);
        TextView integerTextView = rootView.findViewById(R.id.event_create_integer_text);
        integerTextView.setText("Integer: " + receivedInteger);
        TextView page_name = rootView.findViewById(R.id.event_create_title);
        // Set up button to go back to HomeFragment
        Button backButton = rootView.findViewById(R.id.button_back_home);
        backButton.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        return rootView;
    }
}
