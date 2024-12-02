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

import com.example.eventbooking.Events.EventPageFragment.OragnizerEventFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.Role;
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FacilityCreateFragment extends Fragment {

    private EditText editFacilityName, editFacilityLocation, editFacilityId;
    private Button createFacilityButton, backButton, cancelButton;
    private FirebaseFirestore db;
    private boolean roleAssigned = false, testingFlag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facility_create, container, false);

        // Initialize Firebase and views
        if(!UniversalProgramValues.getInstance().getTestingMode())
            db = FirebaseFirestore.getInstance();
        editFacilityName = view.findViewById(R.id.facility_name);
        editFacilityLocation = view.findViewById(R.id.facility_location);
        editFacilityId = view.findViewById(R.id.facility_id);
        createFacilityButton = view.findViewById(R.id.button_create_facility);
        cancelButton = view.findViewById(R.id.button_cancel);
        backButton = view.findViewById(R.id.button_back_organizer);

        // Handle "Create Facility" button click
        createFacilityButton.setOnClickListener(v -> {
            checkAndCreateFacility();// Call the create facility method
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new OragnizerEventFragment())
                    .commit(); // Navigate back to HomeFragment
        });

        cancelButton.setOnClickListener(v ->{
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new OragnizerEventFragment())
                    .commit(); // Navigate back to HomeFragment
        });


        // Handle "Back to Home" button click
        backButton.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new OragnizerEventFragment())
                    .commit();
        });

        return view;
    }

    private void checkAndCreateFacility() {
        User currentUser = UserManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String organizerId = currentUser.getDeviceID();
        if(!UniversalProgramValues.getInstance().getTestingMode()){
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
        else{
            if(UniversalProgramValues.getInstance().queryFacilityOrganizer(organizerId)){
                Toast.makeText(getContext(), "You already have a facility. Delete it before creating a new one.", Toast.LENGTH_LONG).show();
            }
            else{
                createFacility();
            }
        }

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
            currentUser.setFacilityAssociated(true);
            roleAssigned= true;
        }

        if (roleAssigned) {
            if(!UniversalProgramValues.getInstance().getTestingMode()){
                db = FirebaseFirestore.getInstance();
                currentUser.saveUserDataToFirestore()
                        .addOnSuccessListener(aVoid -> {
                            UserManager.getInstance().setCurrentUser(currentUser);
                            Toast.makeText(getContext(), "User role updated successfully", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Failed to update user role", Toast.LENGTH_SHORT).show();
                        });
//                CollectionReference usersRef = db.collection("Users");
//                usersRef.document(currentUser.getDeviceID())
//                        .update("roles", currentUser.getRoles())
//                        .addOnSuccessListener(aVoid -> {
//                            Toast.makeText(getContext(), "User role updated successfully", Toast.LENGTH_SHORT).show();
//                        })
//                        .addOnFailureListener(e -> {
//                            Toast.makeText(getContext(), "Failed to update user role", Toast.LENGTH_SHORT).show();
//                        });
            }
            else{
                for(int i = 0; i < UniversalProgramValues.getInstance().getUserList().size(); i++){
                    if(UniversalProgramValues.getInstance().getUserList().get(i).getDeviceID() == currentUser.getDeviceID()){
                        UniversalProgramValues.getInstance().getUserList().get(i).setRoles(currentUser.getRoles());
                        Log.d("Facility Create", "Setting new User");
                        Log.d("Facility Create", "New User facility: " + currentUser.isFacilityAssociated());
                        UserManager.getInstance().setCurrentUser(currentUser);
                    }
                }
            }

        }

        // Prepare data for Firestore
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", facilityName);
        facilityData.put("location", facilityLocation);
        facilityData.put("facilityID", facilityId);
        facilityData.put("organizer", currentUser.getDeviceID());

        // Save to Firestore
        if(!UniversalProgramValues.getInstance().getTestingMode()) {
            db.collection("Facilities").document(facilityId)
                    .set(facilityData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Facility created successfully", Toast.LENGTH_SHORT).show();
                        clearInputFields();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to create facility: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
        else{
            Facility newFacility = new Facility();
            newFacility.setFacilityID(facilityId);
            newFacility.setOrganizer(currentUser.getDeviceID());
            newFacility.setName(facilityName);
            newFacility.setAddress(facilityLocation);
            UniversalProgramValues.getInstance().getFacilityList().add(newFacility);
        }
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
