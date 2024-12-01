package com.example.eventbooking.Admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventbooking.Admin.Event.ViewEventsFragment;
import com.example.eventbooking.Admin.Facility.ViewFacilitiesFragment;
import com.example.eventbooking.Admin.Images.ViewImagesFragment;
import com.example.eventbooking.Admin.Users.ViewUsersFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.MainActivity;
import com.example.eventbooking.R;
import com.example.eventbooking.Testing.TestFragment;

/**
 * AdminFragment serves as the main navigation interface for the admin to manage and view different sections
 * such as users, events, facilities, QR codes, and images. The fragment allows the admin to easily switch between
 * different management screens.
 */
public class AdminFragment extends Fragment {
    private Button viewUsersButton, viewEventsButton, viewFacilitiesButton, viewImagesButton, viewTestPageButton;
    private Button backHomeButton;
    /**
     * Inflates the fragment's layout, initializes the navigation buttons, and sets up the click listeners.
     * Each button, when clicked, replaces the current fragment with a corresponding fragment (users, events, facilities, etc.).
     * The 'View Images' button is currently hidden due to unimplemented functionality.
     *
     * @param inflater           LayoutInflater used to inflate the layout.
     * @param container          ViewGroup container in which the fragment is placed.
     * @param savedInstanceState Bundle containing saved state data (if any).
     * @return the root View for the fragment's layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        ((MainActivity) getActivity()).hideNavigationUI();
        // Initialize buttons
        viewUsersButton = view.findViewById(R.id.users_button); // Button to navigate to the ViewUsersFragment.
        viewEventsButton = view.findViewById(R.id.events_button);// Button to navigate to the ViewEventsFragment.
        viewFacilitiesButton = view.findViewById(R.id.facilities_button); // Button to navigate to the ViewFacilitiesFragment.
        viewImagesButton = view.findViewById(R.id.images_button);// Button to navigate to the ViewImagesFragment (currently hidden).
        viewTestPageButton = view.findViewById(R.id.test_page_button);
        backHomeButton = view.findViewById(R.id.home_button);// Button to navigate back to the HomeFragment.


        //for now hiding it because not dealing with images
//        viewImagesButton = view.findViewById(R.id.viewImagesButton);

        // Set button listeners
        //initialize fragment
        ViewUsersFragment usersFragment = new ViewUsersFragment();
        ViewEventsFragment eventsFragment = new ViewEventsFragment();
        ViewFacilitiesFragment facilitiesFragment = new ViewFacilitiesFragment();
        ViewImagesFragment imagesFragment = new ViewImagesFragment();
        TestFragment testFragment = new TestFragment();

        //set up on clicklistener
        viewUsersButton.setOnClickListener(v -> replaceFragment(usersFragment));
        viewEventsButton.setOnClickListener(v -> replaceFragment(eventsFragment));
        viewFacilitiesButton.setOnClickListener(v -> replaceFragment(facilitiesFragment));
        viewTestPageButton.setOnClickListener(v -> replaceFragment(testFragment));
        //for now hiding it because not dealing with images, it doesnt work
        viewImagesButton.setOnClickListener(v -> replaceFragment(imagesFragment));
        backHomeButton.setOnClickListener(v -> navigateHome());

        return view;
    }



    /**
     * Replaces the current fragment with the specified fragment and adds it to the back stack.
     * Displays a success message ("Fragment launched successfully!") upon successful fragment replacement.
     * If the fragment replacement fails (e.g., due to an invalid context or manager), an error message is shown.
     * This method ensures that the previous fragments are kept in the back stack for easy navigation back.
     *
     * @param fragment The Fragment to display in the fragment container.
     */
    private void replaceFragment(Fragment fragment) {
        Toast.makeText(getContext(), "Fragment launched successfully!", Toast.LENGTH_SHORT).show();
        // Replace the current fragment with the specified fragment if manager and context are valid
        // Ensure the FragmentManager and context are not null before attempting to replace the fragment.
        // This avoids potential NullPointerExceptions in case the fragment is not properly attached.

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

    private void navigateHome(){
        ((MainActivity) getActivity()).showNavigationUI();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new HomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }


}

