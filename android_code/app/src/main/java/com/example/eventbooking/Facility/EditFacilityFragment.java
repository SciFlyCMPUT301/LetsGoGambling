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

import com.example.eventbooking.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditFacilityFragment extends Fragment {

    private EditText editFacilityName, editFacilityId, editFacilityLocation;
    private Button saveButton, deleteButton, cancelButton;
    private FirebaseFirestore db;
    private String facilityId; // Passed from the previous fragment
    private boolean isExistingFacility;

    public EditFacilityFragment() {
        // Required empty public constructor
    }

    public EditFacilityFragment(String facilityId) {
        this.facilityId = facilityId;
        this.isExistingFacility = !TextUtils.isEmpty(facilityId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_facility_fragment, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        editFacilityName = view.findViewById(R.id.facility_edit_name);
        editFacilityId = view.findViewById(R.id.facility_edit_facilityID);
        editFacilityLocation = view.findViewById(R.id.facility_edit_location);
        saveButton = view.findViewById(R.id.save_button_facility);
        deleteButton = view.findViewById(R.id.delete_button_facility);
        cancelButton = view.findViewById(R.id.cancel_button_facility);

        // Set listeners for action buttons
        saveButton.setOnClickListener(v -> saveFacility());
        deleteButton.setOnClickListener(v -> deleteFacility());
        cancelButton.setOnClickListener(v -> cancelEdit());

        // Load facility details if editing an existing one
        if (isExistingFacility) {
            loadFacilityDetails();
        }

        return view;
    }

    /**
     * Loads the details of the facility into the input fields.
     */
    private void loadFacilityDetails() {
        db.collection("Facilities").document(facilityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        editFacilityName.setText(documentSnapshot.getString("name"));
                        editFacilityId.setText(documentSnapshot.getString("facilityID"));
                        editFacilityLocation.setText(documentSnapshot.getString("location"));
                    } else {
                        Toast.makeText(getContext(), "Facility not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error loading facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Saves the facility details to Firebase Firestore.
     */
    private void saveFacility() {
        String facilityName = editFacilityName.getText().toString().trim();
        String facilityID = editFacilityId.getText().toString().trim();
        String facilityLocation = editFacilityLocation.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(facilityName)) {
            Toast.makeText(getContext(), "Facility name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(facilityID)) {
            Toast.makeText(getContext(), "Facility ID is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data for Firestore
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", facilityName);
        facilityData.put("facilityID", facilityID);
        facilityData.put("location", facilityLocation);

        // Check if it's an existing facility to merge data
        db.collection("Facilities").document(facilityId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Retain existing fields like 'organizer'
                        Map<String, Object> existingData = documentSnapshot.getData();
                        if (existingData != null && existingData.containsKey("organizer")) {
                            facilityData.put("organizer", existingData.get("organizer"));
                        }
                    }
                    // Save data to Firestore (merging retained fields)
                    db.collection("Facilities").document(facilityID)
                            .set(facilityData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Facility saved successfully", Toast.LENGTH_SHORT).show();
                                getParentFragmentManager().popBackStack(); // Navigate back
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error saving facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error loading facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Deletes the facility from Firebase Firestore.
     */
    private void deleteFacility() {
        if (TextUtils.isEmpty(facilityId)) {
            Toast.makeText(getContext(), "No facility selected to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("Facilities").document(facilityId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Facility deleted successfully", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack(); // Navigate back
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Cancels the edit and navigates back to the previous screen.
     */
    private void cancelEdit() {
        getParentFragmentManager().popBackStack();
    }
}
