package com.example.eventbooking.Admin.Users;

import android.os.Bundle;
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
import com.example.eventbooking.Role;
import com.example.eventbooking.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EditUserFragment provides an interface for administrators to add, update, or delete
 * a user's information. It includes fields for the user’s personal details, role toggles,
 * and notification preferences.
 *
 * <p>Administrators can edit user data, assign roles (entrant, organizer, admin),
 * enable/disable notifications and geolocation tracking, and save changes to Firebase Firestore.</p>
 */
public class EditUserFragment extends Fragment {
    private EditText usernameEditText, deviceIdEditText, emailEditText, phoneNumberEditText, profilePictureUrlEditText, locationEditText, dateJoinedEditText, rolesEditText;
    private Button saveButton, deleteButton, cancelButton;
    private Switch notificiation, geolocation, entrantSwitch, organizerSwitch, adminSwitch;
    private DatabaseReference userRef;
    private User user;
    private FirebaseFirestore db;
    private String documentId;
    private boolean isNewUser;



    /**
     *Constructor for the EditUserFragment class.
     * @param selectedUser
     */
    public EditUserFragment(User selectedUser) {
        this.user = selectedUser;
    }


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
//        rolesEditText = view.findViewById(R.id.user_edit_roles);
        saveButton = view.findViewById(R.id.save_button_user);
        deleteButton = view.findViewById(R.id.delete_button_user);
        cancelButton = view.findViewById(R.id.cancel_button_user);
        entrantSwitch = view.findViewById(R.id.entrant_switch);
        organizerSwitch = view.findViewById(R.id.organizer_switch);
        adminSwitch = view.findViewById(R.id.admin_switch);

        if (user != null) {
            documentId = user.getDeviceID();
            isNewUser = false;
            Log.d("Edit User Fragment", "Document ID: "+ documentId);
            if (!isNewUser) {
                // Load existing user data
//                String documentId = args.getString("documentId");
                usernameEditText.setText(user.getUsername());
                deviceIdEditText.setText(user.getDeviceID());
                emailEditText.setText(user.getEmail());
                phoneNumberEditText.setText(user.getPhoneNumber());
                profilePictureUrlEditText.setText(user.getProfilePictureUrl());
                locationEditText.setText(user.getLocation());
                entrantSwitch.setChecked(user.hasRole(Role.ENTRANT));
                adminSwitch.setChecked(user.hasRole(Role.ADMIN));
                organizerSwitch.setChecked(user.hasRole(Role.ORGANIZER));
                deleteButton.setVisibility(View.VISIBLE); // Show delete button for existing users
            } else {
                deleteButton.setVisibility(View.GONE); // Hide delete button for new user
            }
        }else{
            this.user = new User();
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

        cancelButton.setOnClickListener(v -> {
            backToUserListView();
        });

        return view;
    }




    /**
     * Updates the user's information in Firebase Firestore.
     * Retrieves user data from input fields, assembles it into a Map,
     * and updates the Firestore document corresponding to the user ID.
     *
     * @param documentId The document ID of the user in Firestore.
     */
    private void updateUser(String documentId) {
        // Create a User object and set values from input fields
        User updatedUser = new User();
        updatedUser.setDeviceID(deviceIdEditText.getText().toString());
        updatedUser.setUsername(usernameEditText.getText().toString());
        updatedUser.setEmail(emailEditText.getText().toString());
        updatedUser.setPhoneNumber(phoneNumberEditText.getText().toString());
        updatedUser.setLocation(locationEditText.getText().toString());

        // Collect roles based on the switch states
        List<String> roleArray = new ArrayList<>();
        if (entrantSwitch.isChecked()) roleArray.add("entrant");
        if (organizerSwitch.isChecked()) roleArray.add("organizer");
        if (adminSwitch.isChecked()) roleArray.add("admin");
        updatedUser.setRoles(roleArray);

        // Set notification and geolocation preferences
        updatedUser.setNotificationAsk(notificiation.isChecked());
        updatedUser.setGeolocationAsk(geolocation.isChecked());
        updatedUser.setdefaultProfilePictureUrl(user.getdefaultProfilePictureUrl());
        String profileURL = profilePictureUrlEditText.getText().toString();
        if(profileURL.length() > 10)
            user.setProfilePictureUrl(profileURL);
        else
            user.setProfilePictureUrl(user.getProfilePictureUrl());

        updatedUser.saveUserDataToFirestore();

        Toast.makeText(getContext(), "User updated successfully!", Toast.LENGTH_SHORT).show();

        backToUserListView();
    }

    /**
     * Deletes the user's document from Firebase Firestore.
     * Displays a success message on successful deletion and navigates back.
     *
     * @param documentId The document ID of the user in Firestore.
     */

    // change this so it deletes it from firebase
//    private void deleteUser(String documentId) {
//        db.collection("Users").document(documentId).delete()
//                .addOnSuccessListener(aVoid -> {
//                    Toast.makeText(getContext(), "User deleted successfully.", Toast.LENGTH_SHORT).show();
//                    backToUserListView();
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(getContext(), "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                });
////        getActivity().onBackPressed();
//    }
    // Cascade deletes all references to said user
    private void deleteUser(String documentId) {
        String deviceId = deviceIdEditText.getText().toString();

        db.collection("Users").document(documentId).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "User deleted successfully.", Toast.LENGTH_SHORT).show();


                    db.collection("Events").get()
                            .addOnSuccessListener(querySnapshot -> {
                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    Map<String, Object> updates = new HashMap<>();


                                    String[] fieldsToCheck = {
                                            "acceptedParticipantIds",
                                            "canceledParticipantIds",
                                            "declinedParticipantIds",
                                            "signedUpParticipantIds",
                                            "waitingParticipantIds"
                                    };

                                    for (String field : fieldsToCheck) {
                                        List<String> participants = (List<String>) doc.get(field);
                                        if (participants != null && participants.contains(deviceId)) {
                                            participants.remove(deviceId);
                                            updates.put(field, participants);
                                        }
                                    }


                                    if (!updates.isEmpty()) {
                                        db.collection("Events").document(doc.getId())
                                                .update(updates)
                                                .addOnSuccessListener(aVoid2 -> Log.d("EditUserFragment", "Updated Events for user removal"))
                                                .addOnFailureListener(e -> Log.e("EditUserFragment", "Failed to update Events: " + doc.getId(), e));
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Log.e("EditUserFragment", "Failed to query Events collection", e));


                    db.collection("Facilities").get()
                            .addOnSuccessListener(querySnapshot -> {
                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    String organizer = doc.getString("organizer");
                                    if (organizer != null && organizer.equals(deviceId)) {
                                        db.collection("Facilities").document(doc.getId())
                                                .update("organizer", null) // Set organizer to null
                                                .addOnSuccessListener(aVoid3 -> Log.d("EditUserFragment", "Removed user from Facilities: " + doc.getId()))
                                                .addOnFailureListener(e -> Log.e("EditUserFragment", "Failed to update Facilities: " + doc.getId(), e));
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Log.e("EditUserFragment", "Failed to query Facilities collection", e));

                    // Navigate back after all operations
                    getActivity().onBackPressed();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
        user.setDeviceID(deviceIdEditText.getText().toString());
        user.setUsername(usernameEditText.getText().toString());
        user.setEmail(emailEditText.getText().toString());
        user.setPhoneNumber(phoneNumberEditText.getText().toString());
        user.setLocation(locationEditText.getText().toString());

        List <String> roleArray = new ArrayList<>();
        if(entrantSwitch.isChecked())
            roleArray.add("entrant");
        if(organizerSwitch.isChecked())
            roleArray.add("organizer");
        if(adminSwitch.isChecked())
            roleArray.add("admin");
        user.setRoles(roleArray);
        user.setNotificationAsk(notificiation.isChecked());
        user.setGeolocationAsk(geolocation.isChecked());
        user.defaultProfilePictureUrl(user.getUsername());
        String profileURL = profilePictureUrlEditText.getText().toString();
        if(profileURL.length() > 10)
            user.setProfilePictureUrl(profileURL);
        user.saveUserDataToFirestore();
    }


    private void backToUserListView(){
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragmentAdmin, new ViewUsersFragment())
                .addToBackStack(null)
                .commit();
    }


}
