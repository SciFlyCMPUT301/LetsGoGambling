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
import com.example.eventbooking.Admin.HashedQrcode.ViewQRcodeFragment;
import com.example.eventbooking.Admin.Images.ViewImagesFragment;
import com.example.eventbooking.Admin.Users.ViewUsersFragment;
import com.example.eventbooking.R;
/**
 * AdminFragment serves as the main interface for the admin to navigate between different sections,
 * such as viewing users, events, facilities, and images.
 */
public class AdminFragment extends Fragment {
    private Button viewUsersButton, viewEventsButton, viewFacilitiesButton, viewImagesButton, viewDataButton;

    /**
     * Inflates the fragment's layout and initializes components.
     *
     * @param inflater           LayoutInflater used to inflate the layout
     * @param container          ViewGroup container in which the fragment is placed
     * @param savedInstanceState Bundle containing saved state data (if any)
     * @return the root View for the fragment's layout
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        // Initialize buttons
        viewUsersButton = view.findViewById(R.id.viewUsersButton);
        viewEventsButton = view.findViewById(R.id.viewEventsButton);
        viewFacilitiesButton = view.findViewById(R.id.viewFacilitiesButton);
        viewImagesButton = view.findViewById(R.id.viewImagesButton);
        viewDataButton = view.findViewById(R.id.viewdataButton);

        //for now hiding it because not dealing with images
//        viewImagesButton = view.findViewById(R.id.viewImagesButton);

        // Set button listeners
        //initialize fragment
        ViewUsersFragment usersFragment = new ViewUsersFragment();
        ViewEventsFragment eventsFragment = new ViewEventsFragment();
        ViewFacilitiesFragment facilitiesFragment = new ViewFacilitiesFragment();
        ViewQRcodeFragment QRcodeFragment = new ViewQRcodeFragment();

        //set up on clicklistener
        viewUsersButton.setOnClickListener(v -> replaceFragment(usersFragment));
        viewEventsButton.setOnClickListener(v -> replaceFragment(eventsFragment));
        viewFacilitiesButton.setOnClickListener(v -> replaceFragment(facilitiesFragment));
        //for now hiding it because not dealing with images, it doesnt work
        viewImagesButton.setOnClickListener(v -> replaceFragment(new ViewImagesFragment()));
        viewDataButton.setOnClickListener(v -> replaceFragment(QRcodeFragment));

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
        // Replace the current fragment with the specified fragment if manager and context are valid
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

