package com.example.eventbooking.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.Event.ViewEventsFragment;
import com.example.eventbooking.Admin.Facility.ViewFacilitiesFragment;
import com.example.eventbooking.Admin.Images.ViewImagesFragment;
import com.example.eventbooking.Admin.Users.ViewUsersFragment;
import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventPageFragment.EventFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.profile.ProfileFragment;

import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

public class AdminFragment extends Fragment {
    private Button viewUsersButton, viewEventsButton, viewFacilitiesButton, viewImagesButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        // Initialize buttons
        viewUsersButton = view.findViewById(R.id.viewUsersButton);
        viewEventsButton = view.findViewById(R.id.viewEventsButton);
        viewFacilitiesButton = view.findViewById(R.id.viewFacilitiesButton);

        //for now hiding it because not dealing with images
//        viewImagesButton = view.findViewById(R.id.viewImagesButton);

        // Set button listeners
        ViewUsersFragment usersFragment = new ViewUsersFragment();
        ViewEventsFragment eventsFragment = new ViewEventsFragment();
        ViewFacilitiesFragment facilitiesFragment = new ViewFacilitiesFragment();

        viewUsersButton.setOnClickListener(v -> replaceFragment(usersFragment));
        viewEventsButton.setOnClickListener(v -> replaceFragment(eventsFragment));
        viewFacilitiesButton.setOnClickListener(v -> replaceFragment(facilitiesFragment));
        //for now hiding it because not dealing with images, it doesnt work
//        viewImagesButton.setOnClickListener(v -> replaceFragment(new ViewImagesFragment()));

        return view;
    }




    private void replaceFragment(Fragment fragment) {
        Toast.makeText(getContext(), "Event created successfully!", Toast.LENGTH_SHORT).show();
        if (getParentFragmentManager() != null && getContext() != null) {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else {
            Toast.makeText(getContext(), "Error replacing fragment", Toast.LENGTH_SHORT).show();
        }
    }


}

