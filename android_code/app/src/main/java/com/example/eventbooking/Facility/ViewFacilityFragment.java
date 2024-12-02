package com.example.eventbooking.Facility;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventPageFragment.OragnizerEventFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.UniversalProgramValues;
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
        if(!UniversalProgramValues.getInstance().getTestingMode())
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
//        cancelButton.setOnClickListener(v -> navigateToHomeFragment());
//        goBackButton.setOnClickListener(v -> navigateToHomeFragment());
        cancelButton.setOnClickListener(v -> navigateToOrganizerFragment());
        goBackButton.setOnClickListener(v -> navigateToOrganizerFragment());

        return view;
    }

    /**
     * Load the facility for the current organizer from Firestore.
     */
    private void loadFacility() {
        if(!UniversalProgramValues.getInstance().getTestingMode()) {
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
        else{
            Facility temp_facility = UniversalProgramValues.getInstance().queryFacilityByOrganizer(organizerId);
            editFacilityName.setText(temp_facility.getName());
            editFacilityId.setText(temp_facility.getFacilityID());
            editFacilityLocation.setText(temp_facility.getAddress());
            facilityId = temp_facility.getFacilityID();
        }
    }

    /**
     * Save the facility details to Firestore.
     */
    private void saveFacility() {
        String newFacilityName = editFacilityName.getText().toString().trim();
        String newFacilityID = editFacilityId.getText().toString().trim();
        String newFacilityLocation = editFacilityLocation.getText().toString().trim();

        if (TextUtils.isEmpty(newFacilityName) || TextUtils.isEmpty(newFacilityID)) {
            Toast.makeText(getContext(), "Name and ID are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", newFacilityName);
        facilityData.put("facilityID", facilityId);
        facilityData.put("location", newFacilityLocation);
        facilityData.put("organizer", organizerId);

        if (facilityId == null) {
            // Create new facility
            if(!UniversalProgramValues.getInstance().getTestingMode()) {
                db.collection("Facilities")
                        .add(facilityData)
                        .addOnSuccessListener(documentReference -> {
                            facilityId = documentReference.getId();
                            Toast.makeText(getContext(), "Facility created successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error creating facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
            else{
                Facility newFacility = new Facility();
                newFacility.setFacilityID(facilityId);
                newFacility.setOrganizer(organizerId);
                newFacility.setName(newFacilityName);
                newFacility.setAddress(newFacilityLocation);
                UniversalProgramValues.getInstance().getFacilityList().add(newFacility);
            }
        } else {
            // Update existing facility
            if(!UniversalProgramValues.getInstance().getTestingMode()) {
                db.collection("Facilities").document(facilityId)
                        .set(facilityData)
                        .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Facility updated successfully", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(getContext(), "Error updating facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
            else{
                UniversalProgramValues.getInstance().queryFacilityByOrganizer(organizerId).setFacilityID(facilityId);
                UniversalProgramValues.getInstance().queryFacilityByOrganizer(organizerId).setName(newFacilityName);
                UniversalProgramValues.getInstance().queryFacilityByOrganizer(organizerId).setAddress(newFacilityLocation);
            }
        }
//        navigateToHomeFragment();
        navigateToOrganizerFragment();
    }

    /**
     * Delete the current facility.
     */
    private void deleteFacility() {
        if (facilityId == null) {
            Toast.makeText(getContext(), "No facility to delete", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!UniversalProgramValues.getInstance().getTestingMode()) {
            db.collection("Facilities").document(facilityId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Facility deleted successfully", Toast.LENGTH_SHORT).show();
                        clearForm();
                        UserManager.getInstance().getCurrentUser().setFacilityAssociated(false);
                        UserManager.getInstance().getCurrentUser().saveUserDataToFirestore();
                        // Deleting all events associated with the facility
                        Event.deleteEventsByOrganizer(UserManager.getInstance().getUserId(),
                                task -> {
                                    Log.d("Event", "All events for organizer " + UserManager.getInstance().getUserId() + " deleted successfully.");
                                },
                                exception -> {
                                    Log.e("Event", "Failed to delete events: " + exception.getMessage());
                                }
                        );
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Error deleting facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        else{
            Facility temp_facility = UniversalProgramValues.getInstance().queryFacilityByOrganizer(organizerId);
            UniversalProgramValues.getInstance().getFacilityList().remove(temp_facility);
            UserManager.getInstance().getCurrentUser().setFacilityAssociated(false);
            UniversalProgramValues.getInstance().getSingle_user().setFacilityAssociated(false);
            Log.d("View Facility", "User Manager Device ID: " + UserManager.getInstance().getCurrentUser().getDeviceID());
            Log.d("View Facility", "organizerId: " + organizerId);
//            UniversalProgramValues.getInstance().queryUser(UserManager.getInstance().getCurrentUser().getDeviceID()).setFacilityAssociated(false);
            UniversalProgramValues.getInstance().queryUser(organizerId).setFacilityAssociated(false);
            clearForm();
        }
//        navigateToHomeFragment();
        navigateToOrganizerFragment();
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

    /**
     * Navigate back to the Organizer page.
     */
    private void navigateToOrganizerFragment(){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OragnizerEventFragment())
                .commit();
    }
}
