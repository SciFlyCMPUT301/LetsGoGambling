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
import com.example.eventbooking.UserManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ViewFacilityFragment extends Fragment {

    private EditText editFacilityName, editFacilityId, editFacilityLocation;
    private Button saveButton, deleteButton, cancelButton, goBackButton;
    private FirebaseFirestore db;
    private String organizerId; // Dynamically fetched organizer ID
    private String facilityId; // The ID of the facility associated with the organizer

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_facility2, container, false);

        // Initialize Firestore and UI components
        db = FirebaseFirestore.getInstance();
        organizerId = UserManager.getInstance().getUserId(); // Get the current organizer's ID dynamically
        editFacilityName = view.findViewById(R.id.facility_edit_name);
        editFacilityId = view.findViewById(R.id.facility_edit_facilityID);
        editFacilityLocation = view.findViewById(R.id.facility_edit_location);
        saveButton = view.findViewById(R.id.save_button_facility);
        deleteButton = view.findViewById(R.id.delete_button_facility);
        cancelButton = view.findViewById(R.id.cancel_button_facility);
        goBackButton = view.findViewById(R.id.go_back);

        // Load existing facility for the organizer
        loadFacility();

        // Set listeners
        saveButton.setOnClickListener(v -> saveFacility());
        deleteButton.setOnClickListener(v -> deleteFacility());
        cancelButton.setOnClickListener(v -> navigateToHomeFragment());
        goBackButton.setOnClickListener(v -> navigateToHomeFragment());

        return view;
    }

    /**
     * Load the facility for the current organizer from Firestore.
     */
    private void loadFacility() {
        db.collection("Facilities")
                .whereEqualTo("organizer", organizerId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0); // Assuming one facility per organizer
                        facilityId = document.getId();
                        Map<String, Object> facilityData = document.getData();
                        if (facilityData != null) {
                            editFacilityName.setText((String) facilityData.get("name"));
                            editFacilityId.setText((String) facilityData.get("facilityID"));
                            editFacilityLocation.setText((String) facilityData.get("location"));
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error loading facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Save the facility details to Firestore.
     */
    private void saveFacility() {
        String facilityName = editFacilityName.getText().toString().trim();
        String facilityID = editFacilityId.getText().toString().trim();
        String facilityLocation = editFacilityLocation.getText().toString().trim();

        if (TextUtils.isEmpty(facilityName) || TextUtils.isEmpty(facilityID)) {
            Toast.makeText(getContext(), "Name and ID are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", facilityName);
        facilityData.put("facilityID", facilityID);
        facilityData.put("location", facilityLocation);
        facilityData.put("organizer", organizerId);

        if (facilityId == null) {
            // Create new facility
            db.collection("Facilities")
                    .add(facilityData)
                    .addOnSuccessListener(documentReference -> {
                        facilityId = documentReference.getId();
                        Toast.makeText(getContext(), "Facility created successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error creating facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // Update existing facility
            db.collection("Facilities").document(facilityId)
                    .set(facilityData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Facility updated successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        navigateToHomeFragment();
    }

    /**
     * Delete the current facility.
     */
    private void deleteFacility() {
        if (facilityId == null) {
            Toast.makeText(getContext(), "No facility to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Facilities").document(facilityId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Facility deleted successfully", Toast.LENGTH_SHORT).show();
                    clearForm();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        navigateToHomeFragment();
    }

    /**
     * Clear the input form.
     */
    private void clearForm() {
        editFacilityName.setText("");
        editFacilityId.setText("");
        editFacilityLocation.setText("");
        facilityId = null;
    }

    /**
     * Navigate back to the HomeFragment.
     */
    private void navigateToHomeFragment() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
    }
}
