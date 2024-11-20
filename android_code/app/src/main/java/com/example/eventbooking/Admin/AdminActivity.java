package com.example.eventbooking.Admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.Event.ViewEventsFragment;
import com.example.eventbooking.Admin.Facility.ViewFacilitiesFragment;
import com.example.eventbooking.Admin.Images.ViewImagesFragment;
import com.example.eventbooking.Admin.Users.ViewUsersFragment;
import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventPageFragment.EventFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.Home.HomeViewFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.profile.ProfileFragment;

import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
/**
 * AdminFragment serves as the main interface for the admin to navigate between different sections,
 * such as viewing users, events, facilities, and images.
 */
public class AdminActivity extends AppCompatActivity {
    private Button viewUsersButton, viewEventsButton, viewFacilitiesButton, viewImagesButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Log.d("Admin Activity", "Launched Activity");

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragmentAdmin, new AdminViewFragment())
                    .commit();
        }

        // Initialize buttons
//        viewUsersButton = findViewById(R.id.viewUsersButton);
//        viewEventsButton = findViewById(R.id.viewEventsButton);
//        viewFacilitiesButton = findViewById(R.id.viewFacilitiesButton);
//        viewImagesButton = findViewById(R.id.viewImagesButton);
//
//        //for now hiding it because not dealing with images
////        viewImagesButton = view.findViewById(R.id.viewImagesButton);
//
//        // Set button listeners
//        //initialize fragment
//        ViewUsersFragment usersFragment = new ViewUsersFragment();
//        ViewEventsFragment eventsFragment = new ViewEventsFragment();
//        ViewFacilitiesFragment facilitiesFragment = new ViewFacilitiesFragment();
//        //set up on clicklistener
//        viewUsersButton.setOnClickListener(v -> replaceFragment(usersFragment));
//        viewEventsButton.setOnClickListener(v -> replaceFragment(eventsFragment));
//        viewFacilitiesButton.setOnClickListener(v -> replaceFragment(facilitiesFragment));
//        //for now hiding it because not dealing with images, it doesnt work
//        viewImagesButton.setOnClickListener(v -> replaceFragment(new ViewImagesFragment()));

    }



//    /**
//     * Replaces the current fragment with the specified fragment and adds it to the back stack.
//     * Displays a success message on successful replacement, otherwise shows an error message.
//     *
//     * @param fragment the Fragment to display in the fragment container
//     */
//    private void replaceFragment(Fragment fragment) {
//        Toast.makeText(this, "Fragment launched successfully!", Toast.LENGTH_SHORT).show();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.flFragmentAdmin, fragment)
//                .commit();
//    }


}


