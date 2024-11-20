package com.example.eventbooking.Admin.Facility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Facility.Facility;
import com.example.eventbooking.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EditFacilityFragment extends Fragment {

    private EditText faciltyNameEditText, facilityIdEditText, facilityOrganizerEditText, facilityLocationEditText;
    private Button saveButton, deleteButton, cancelButton;
//    private Switch notificiation, geolocation, entrantSwitch, organizerSwitch, adminSwitch;
    private DatabaseReference userRef;
    private Facility facility;
    private FirebaseFirestore db;
    private String documentId;
    private boolean newFacility;
    private ListView facilityEventsListView;
    private List<String> eventList;
    private ArrayAdapter<String> facilityEventsAdapter;


    /**
     *Constructor for the EditFacilityFragment class.
     * @param selectedFacility
     */
    public EditFacilityFragment(Facility selectedFacility) {
        this.facility = selectedFacility;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_facility, container, false);
        db = FirebaseFirestore.getInstance();
        initateUI(view);

        if (facility != null) {
            newFacility = false;
            documentId = facility.getFacilityID();
        }
        else{
            newFacility = true;
            deleteButton.setVisibility(View.GONE);
        }



        saveButton.setOnClickListener(v -> {
            if (newFacility) {
                addFacilityToFirestore();
            } else {
                updateFacility(documentId);
            }
        });

        deleteButton.setOnClickListener(v ->{
            deleteFacility(documentId);
        });
        cancelButton.setOnClickListener(v -> {
            backToFacilityListView();
        });

        return view;
    }


    private void initateUI(View view){
        faciltyNameEditText = view.findViewById(R.id.facility_edit_name);
        facilityIdEditText = view.findViewById(R.id.facility_edit_facilityID);
        facilityOrganizerEditText = view.findViewById(R.id.facility_edit_organizer);
        facilityLocationEditText = view.findViewById(R.id.facility_edit_location);
        saveButton = view.findViewById(R.id.save_button_facility);
        deleteButton = view.findViewById(R.id.delete_button_facility);
        cancelButton = view.findViewById(R.id.cancel_button_facility);
        facilityEventsListView = view.findViewById(R.id.facility_event_list);
    }

    private void fillUI(View view){
        faciltyNameEditText.setText(facility.getName());
        facilityIdEditText.setText(facility.getFacilityID());
        facilityOrganizerEditText.setText(facility.getOrganizer());
        facilityLocationEditText.setText(facility.getAddress());
        facilityEventsListView = view.findViewById(R.id.facility_event_list);
        eventList = new ArrayList<>(facility.getAllEvents());
        facilityEventsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, eventList);
        facilityEventsListView.setAdapter(facilityEventsAdapter);
    }

    /**
     * Adds a new facility to Firebase Firestore based on input field values.
     * Creates a new document in the "Facilities" collection with facility attributes.
     */
    private void addFacilityToFirestore() {
        String saveFacilityID = facilityIdEditText.getText().toString();
        facility.setName(faciltyNameEditText.getText().toString());
        facility.setFacilityID(facilityIdEditText.getText().toString());
        facility.setOrganizer(facilityOrganizerEditText.getText().toString());
        facility.setAddress(facilityLocationEditText.getText().toString());
        facility.setAllEvents(eventList);
        facility.saveFacilityProfile();
    }



    /**
     * Updates the facilities information in Firebase Firestore.
     * Retrieves facility data from input fields, assembles it into a Map,
     * and updates the Firestore document corresponding to the facility ID.
     *
     * @param documentId The document ID of the facility in Firestore.
     */
    private void updateFacility(String documentId) {
        Facility updatedFacility = new Facility();
        updatedFacility.setFacilityID(facilityIdEditText.getText().toString());
        updatedFacility.setName(faciltyNameEditText.getText().toString());
        updatedFacility.setOrganizer(facilityOrganizerEditText.getText().toString());
        updatedFacility.setAddress(facilityLocationEditText.getText().toString());
        updatedFacility.setAllEvents(eventList);

        updatedFacility.saveFacilityProfile();

        Toast.makeText(getContext(), "Facility updated successfully!", Toast.LENGTH_SHORT).show();

        // Navigate back after updating
        backToFacilityListView();
    }

    /**
     * Deletes the facilties document from Firebase Firestore.
     * Displays a success message on successful deletion and navigates back.
     *
     * @param documentId The document ID of the facility in Firestore.
     */
    private void deleteFacility(String documentId) {
        db.collection("Facilities").document(documentId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Facility deleted successfully.", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed(); // Navigate back after deletion
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete facility: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
//        getActivity().onBackPressed();
    }

    private void backToFacilityListView(){
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragmentAdmin, new ViewFacilitiesFragment())
                .addToBackStack(null)
                .commit();
    }

}
