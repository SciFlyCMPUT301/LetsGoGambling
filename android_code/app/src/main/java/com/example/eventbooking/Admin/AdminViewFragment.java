package com.example.eventbooking.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.Event.ViewEventsFragment;
import com.example.eventbooking.Admin.Facility.ViewFacilitiesFragment;
import com.example.eventbooking.Admin.Images.ViewImagesFragment;
import com.example.eventbooking.Admin.Users.ViewUsersFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.Testing.TestFragment;

public class AdminViewFragment extends Fragment {
    private Button viewUsersButton, viewEventsButton, viewFacilitiesButton, viewImagesButton;
//    initialize fragment
    private ViewUsersFragment usersFragment = new ViewUsersFragment();
    private ViewEventsFragment eventsFragment = new ViewEventsFragment();
    private ViewFacilitiesFragment facilitiesFragment = new ViewFacilitiesFragment();
    private ViewImagesFragment imagesFragment = new ViewImagesFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_view, container, false);
        Log.d("Admin View Activity", "Launched Fragment");
        viewUsersButton = view.findViewById(R.id.viewUsersButton);
        viewEventsButton = view.findViewById(R.id.viewEventsButton);
        viewFacilitiesButton = view.findViewById(R.id.viewFacilitiesButton);
        viewImagesButton = view.findViewById(R.id.viewImagesButton);

        //for now hiding it because not dealing with images
//        viewImagesButton = view.findViewById(R.id.viewImagesButton);

        // Set button listeners

        //set up on clicklistener
        viewUsersButton.setOnClickListener(v -> replaceFragment(usersFragment));
        viewEventsButton.setOnClickListener(v -> replaceFragment(eventsFragment));
        viewFacilitiesButton.setOnClickListener(v -> replaceFragment(facilitiesFragment));
        //for now hiding it because not dealing with images, it doesnt work
        viewImagesButton.setOnClickListener(v -> replaceFragment(imagesFragment));

        return view;
    }

    /**
     * Replaces the current fragment with the specified fragment and adds it to the back stack.
     * Displays a success message on successful replacement, otherwise shows an error message.
     *
     * @param fragment the Fragment to display in the fragment container
     */
    private void replaceFragment(Fragment fragment) {
        Toast.makeText(getContext(), "Fragment launched successfully!", Toast.LENGTH_SHORT).show();
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragmentAdmin, fragment)
                .addToBackStack(null)
                .commit();
    }
//    backHomeButton
}
