package com.example.eventbooking.Facility;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.Role;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * FacilityCreateFragment is responsible for handling the creation of facilities
 * by users with the Organizer role. The fragment provides a form where users can input
 * the facility name, location, and ID. It also ensures that each organizer can only
 * create one facility at a time.
 */
public class FacilityCreateFragment extends Fragment {

    private EditText editFacilityName, editFacilityLocation, editFacilityId;
    private Button createFacilityButton, backButton, cancelButton;
    private FirebaseFirestore db;
    private boolean roleAssigned = false, testingFlag;

    /**
     * Called when the fragment's view is created. Initializes Firebase, views, and button listeners.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState A bundle containing saved state, if any.
     * @return The view hierarchy associated with the fragment.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facility_create, container, false);

        // Initialize Firebase and views
        db = FirebaseFirestore.getInstance();
        editFacilityName = view.findViewById(R.id.facility_name);
        editFacilityLocation = view.findViewById(R.id.facility_location);
        editFacilityId = view.findViewById(R.id.facility_id);
        createFacilityButton = view.findViewById(R.id.button_create_facility);
        cancelButton = view.findViewById(R.id.button_cancel);
        backButton = view.findViewById(R.id.button_back_home);

        // Handle "Create Facility" button click
        createFacilityButton.setOnClickListener(v -> {
            checkAndCreateFacility();// Call the create facility method
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit(); // Navigate back to HomeFragment
        });

        cancelButton.setOnClickListener(v ->{
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit(); // Navigate back to HomeFragment
        });

        // Handle "Back to Home" button click
        backButton.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        return view;
    }


    /**
     * Checks if the organizer already has a facility. If so, prompts them to delete it first.
     * Otherwise, proceeds with facility creation.
     */
    private void checkAndCreateFacility() {
        User currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String organizerId = currentUser.getDeviceID();

        db.collection("Facilities")
                .whereEqualTo("organizer", organizerId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Facility already exists for this organizer
                        Toast.makeText(getContext(), "You already have a facility. Delete it before creating a new one.", Toast.LENGTH_LONG).show();
                    } else {
                        // No facility exists; proceed with creation
                        createFacility();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error checking existing facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Creates a facility in Firebase Firestore with the input data.
     */
    private void createFacility() {
        String facilityName = editFacilityName.getText().toString().trim();
        String facilityLocation = editFacilityLocation.getText().toString().trim();
        String facilityId = editFacilityId.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(facilityName)) {
            Toast.makeText(getContext(), "Facility Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(facilityId)) {
            Toast.makeText(getContext(), "Facility ID is required", Toast.LENGTH_SHORT).show();
            return;
        }


        User currentUser = UserManager.getInstance().getCurrentUser();
        if(currentUser == null){
            Toast.makeText(getContext(),"User not found",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!currentUser.hasRole(Role.ORGANIZER)){
            currentUser.addRole(Role.ORGANIZER);
            roleAssigned= true;
        }

        if (roleAssigned) {
            db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("Users");
            usersRef.document(currentUser.getDeviceID())
                    .update("roles", currentUser.getRoles())
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "User role updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to update user role", Toast.LENGTH_SHORT).show();
                    });
        }

        // Prepare data for Firestore
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", facilityName);
        facilityData.put("location", facilityLocation);
        facilityData.put("facilityID", facilityId);
        facilityData.put("organizer", currentUser.getDeviceID());

        // Save to Firestore
        db.collection("Facilities").document(facilityId)
                .set(facilityData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Facility created successfully", Toast.LENGTH_SHORT).show();
                    clearInputFields();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to create facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Clears the input fields after a successful creation.
     */
    private void clearInputFields() {
        editFacilityName.setText("");
        editFacilityLocation.setText("");
        editFacilityId.setText("");
    }
}
