package com.example.eventbooking.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventbooking.Admin.Event.ViewEventsFragment;
import com.example.eventbooking.Admin.Facility.ViewFacilitiesFragment;
import com.example.eventbooking.Admin.Images.ViewImagesFragment;
import com.example.eventbooking.Admin.Users.ViewUsersFragment;
import com.example.eventbooking.R;

public class AdminActivity extends AppCompatActivity {

    private Button viewUsersButton, viewEventsButton, viewFacilitiesButton, viewImagesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin); // Use the appropriate layout for AdminActivity

        viewUsersButton = findViewById(R.id.viewUsersButton);
        viewEventsButton = findViewById(R.id.viewEventsButton);
        viewFacilitiesButton = findViewById(R.id.viewFacilitiesButton);
        viewImagesButton = findViewById(R.id.viewImagesButton);

        // Set button listeners to switch fragments
        viewUsersButton.setOnClickListener(v -> replaceFragment(new ViewUsersFragment()));
        viewEventsButton.setOnClickListener(v -> replaceFragment(new ViewEventsFragment()));
        viewFacilitiesButton.setOnClickListener(v -> replaceFragment(new ViewFacilitiesFragment()));
        viewImagesButton.setOnClickListener(v -> replaceFragment(new ViewImagesFragment()));

        // Optionally, load a default fragment when the activity starts
        if (savedInstanceState == null) {
            // Load default fragment (e.g., ViewUsersFragment) when the activity starts
            replaceFragment(new ViewUsersFragment());
        }
    }

//    /**
//     * Starts the specified activity.
//     *
//     * @param activityClass The class of the activity to navigate to.
//     */
//    private void navigateTo(Class<?> activityClass) {
//        try {
//            Intent intent = new Intent(AdminActivity.this, activityClass);
//            startActivity(intent);
//            Toast.makeText(this, "Navigating to " + activityClass.getSimpleName(), Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Toast.makeText(this, "Error navigating to " + activityClass.getSimpleName(), Toast.LENGTH_SHORT).show();
//        }
//    }

    /**
     * Replaces the current fragment with the specified one inside AdminActivity.
     *
     * @param fragment the Fragment to display.
     */
    private void replaceFragment(Fragment fragment) {
        Toast.makeText(this, "Fragment launched successfully!", Toast.LENGTH_SHORT).show();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null); // Add this transaction to the back stack (optional)
        transaction.commit();
    }
}
