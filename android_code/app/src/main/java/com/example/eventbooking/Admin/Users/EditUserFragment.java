package com.example.eventbooking.Admin.Users;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * EditUserFragment provides an interface for administrators to add, update, or delete
 * a user's information. It includes fields for the user’s personal details, role toggles,
 * and notification preferences.
 *
 * <p>Administrators can edit user data, assign roles (entrant, organizer, admin),
 * enable/disable notifications and geolocation tracking, and save changes to Firebase Firestore.</p>
 */
public class EditUserFragment extends Fragment {
    private EditText usernameEditText, deviceIdEditText, emailEditText, phoneNumberEditText, profilePictureUrlEditText, locationEditText, dateJoinedEditText;
    private Button saveButton, deleteButton, cancelButton;
    private Switch notificiation, geolocation, entrantSwitch, organizerSwitch, adminSwitch;
    private DatabaseReference userRef;
    private User user;
    private FirebaseFirestore db;
    private String documentId;
    private boolean isNewUser;
    /**
     * Inflates the layout for the fragment and initializes views, listeners, and Firestore instance.
     *
     * @param inflater LayoutInflater to inflate the fragment's view.
     * @param container ViewGroup containing the fragment’s UI.
     * @param savedInstanceState Saved instance state for re-creation.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);
        db = FirebaseFirestore.getInstance();
        // Initialize Views
        usernameEditText = view.findViewById(R.id.user_edit_username);
        deviceIdEditText = view.findViewById(R.id.user_edit_deviceID);
        emailEditText = view.findViewById(R.id.user_edit_email);
        phoneNumberEditText = view.findViewById(R.id.user_edit_phoneNumber);
        profilePictureUrlEditText = view.findViewById(R.id.user_edit_profilePictureUrl);
        locationEditText = view.findViewById(R.id.user_edit_location);
        dateJoinedEditText = view.findViewById(R.id.user_edit_dateJoined);
        saveButton = view.findViewById(R.id.save_button_user);
        deleteButton = view.findViewById(R.id.delete_button_user);
        cancelButton = view.findViewById(R.id.cancel_button_user);
        entrantSwitch = view.findViewById(R.id.entrant_switch);
        organizerSwitch = view.findViewById(R.id.organizer_switch);
        adminSwitch = view.findViewById(R.id.admin_switch);

        //retrive arguments to check if editing an existing user or creating a new one
        Bundle args = getArguments();
        if (args != null) {
            documentId = args.getString("deviceId");
            isNewUser = args.getBoolean("isNewUser", false);
            Log.d("Edit User Fragment", "Document ID: "+ documentId);
            if (!isNewUser) {
                // Load existing user data
//                String documentId = args.getString("documentId");
                usernameEditText.setText(args.getString("username"));
                deviceIdEditText.setText(args.getString("deviceID"));
                emailEditText.setText(args.getString("email"));
                phoneNumberEditText.setText(args.getString("phoneNumber"));
                profilePictureUrlEditText.setText(args.getString("profilePictureUrl"));
                locationEditText.setText(args.getString("location"));
                entrantSwitch.setChecked(args.getBoolean("entrant"));
                adminSwitch.setChecked(args.getBoolean("admin"));
                organizerSwitch.setChecked(args.getBoolean("organizer"));
                deleteButton.setVisibility(View.VISIBLE); // Show delete button for existing users
            } else {
                deleteButton.setVisibility(View.GONE); // Hide delete button for new user
            }
        }
        // Set button listeners
        saveButton.setOnClickListener(v -> {
            if (isNewUser) {
                addUserToFirestore();
            } else {
                updateUser(documentId);
            }
        });
        deleteButton.setOnClickListener(v -> {
            Log.d("Edit User", "Document ID: "+ documentId);
            if (documentId != null) {
                deleteUser(documentId);

            }
        });

        cancelButton.setOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }

    /**
     * Updates the user's information in Firebase Firestore.
     * Retrieves user data from input fields, assembles it into a Map,
     * and updates the Firestore document corresponding to the user ID.
     *
     * @param documentId The document ID of the user in Firestore.
     */
    // make update user update the firebase
    private void updateUser(String documentId) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("username", usernameEditText.getText().toString());
        updatedData.put("deviceID", deviceIdEditText.getText().toString());
        updatedData.put("email", emailEditText.getText().toString());
        updatedData.put("phoneNumber", phoneNumberEditText.getText().toString());
        updatedData.put("location", locationEditText.getText().toString());
        //collect user roles based on selected switches
        List<String> roles = new ArrayList<>();
        if(entrantSwitch.isChecked())
            roles.add("entrant");
        if(organizerSwitch.isChecked())
            roles.add("organizer");
        if(adminSwitch.isChecked())
            roles.add("admin");
        updatedData.put("role", roles);
        updatedData.put("notificationAsk", notificiation.isChecked());
        updatedData.put("geolocationAsk", geolocation.isChecked());
        db.collection("Users").document(documentId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "User updated successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.d("FirestoreError", "Error updating user", e);
                });
        getActivity().onBackPressed();
    }

    /**
     * Deletes the user's document from Firebase Firestore.
     * Displays a success message on successful deletion and navigates back.
     *
     * @param documentId The document ID of the user in Firestore.
     */

    // change this so it deletes it from firebase
    private void deleteUser(String documentId) {
        db.collection("Users").document(documentId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "User deleted successfully.", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed(); // Navigate back after deletion
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
//        getActivity().onBackPressed();
    }

    /**
     * Loads user data from Firebase Firestore and populates the UI fields.
     * This method is used to retrieve details of an existing user for editing.
     *
     * @param documentId The document ID of the user in Firestore.
     */
    private void loadUserData(String documentId) {
        db.collection("Users").document(documentId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Get the user data and populate the fields
                String username = documentSnapshot.getString("username");
                String deviceID = documentSnapshot.getString("deviceID");
                String email = documentSnapshot.getString("email");
                String phoneNumber = documentSnapshot.getString("phoneNumber");
                String location = documentSnapshot.getString("location");
                String dateJoined = documentSnapshot.getString("dateJoined");
                String profileURL = documentSnapshot.getString("profilePictureUrl");
                Boolean adminLevel = documentSnapshot.getBoolean("adminLevel");
                Boolean facilityAssociated = documentSnapshot.getBoolean("facilityAssociated");
                Boolean notificationAsk = documentSnapshot.getBoolean("notificationAsk");
                Boolean geolocationAsk = documentSnapshot.getBoolean("geolocationAsk");

                List<String> roles = (List<String>) documentSnapshot.get("roles");
                profilePictureUrlEditText.setText(profileURL);
                locationEditText.setText(location);
                dateJoinedEditText.setText(dateJoined);
                usernameEditText.setText(username);
                deviceIdEditText.setText(deviceID);
                emailEditText.setText(email);
                phoneNumberEditText.setText(phoneNumber);
                notificiation.setChecked(notificationAsk != null && notificationAsk);
                geolocation.setChecked(geolocationAsk != null && geolocationAsk);

                if(roles.contains("entrant"))
                    entrantSwitch.setChecked(true);
                else
                    entrantSwitch.setChecked(false);
                if(roles.contains("entrant"))
                    organizerSwitch.setChecked(true);
                else
                    organizerSwitch.setChecked(false);
                if(roles.contains("entrant"))
                    adminSwitch.setChecked(true);
                else
                    adminSwitch.setChecked(false);
            }
        });
    }

    /**
     * Adds a new user to Firebase Firestore based on input field values.
     * Creates a new document in the "Users" collection with user attributes.
     */
    private void addUserToFirestore() {
        String username = usernameEditText.getText().toString();
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("deviceID", deviceIdEditText.getText().toString());
        userData.put("email", emailEditText.getText().toString());
        userData.put("phoneNumber", phoneNumberEditText.getText().toString());
        userData.put("location", locationEditText.getText().toString());
        String roleArray[] = new String[3];
        if(entrantSwitch.isChecked())
            roleArray[0] = "entrant";
        if(organizerSwitch.isChecked())
            roleArray[1] = "organizer";
        if(adminSwitch.isChecked())
            roleArray[2] = "admin";
        userData.put("role", roleArray);
        userData.put("notificationAsk", notificiation.isChecked());
        userData.put("geolocationAsk", geolocation.isChecked());

        db.collection("Users").document(username).set(userData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "New user added successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.d("FirestoreError", "Error adding new user", e);
                });
    }


}
