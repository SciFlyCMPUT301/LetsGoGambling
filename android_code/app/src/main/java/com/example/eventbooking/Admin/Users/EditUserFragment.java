package com.example.eventbooking.Admin.Users;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

/**
 * EditUserFragment provides an interface for administrators to add, update, or delete
 * a user's information. It includes fields for the user’s personal details, role toggles,
 * and notification preferences.
 *
 * <p>Administrators can edit user data, assign roles (entrant, organizer, admin),
 * enable/disable notifications and geolocation tracking, and save changes to Firebase Firestore.</p>
 */
public class EditUserFragment extends Fragment {
    private TextView usernameTextView, deviceIdTextView, emailTextView, phoneNumberTextView, profilePictureUrlTextView, locationTextView, dateJoinedTextView, roleTextView;
    private Button deleteButton, cancelButton;
    private Switch notificiation, geolocation, entrantSwitch, organizerSwitch, adminSwitch;
    private DatabaseReference userRef;
    private User user;
    private FirebaseFirestore db;
    private String documentId;
    private boolean isNewUser;

    /**
     * Inflates the layout for the fragment and initializes views, listeners, and Firestore instance.
     *
     * @param inflater           LayoutInflater to inflate the fragment's view.
     * @param container          ViewGroup containing the fragment’s UI.
     * @param savedInstanceState Saved instance state for re-creation.
     * @return The root view of the fragment.
     */
    @Nullable
    @Override

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user, container, false);
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        usernameTextView = view.findViewById(R.id.user_view_username);
        deviceIdTextView = view.findViewById(R.id.user_view_deviceID);
        emailTextView = view.findViewById(R.id.user_view_email);
        phoneNumberTextView = view.findViewById(R.id.user_view_phoneNumber);
        profilePictureUrlTextView = view.findViewById(R.id.user_view_profilePictureUrl);
        locationTextView = view.findViewById(R.id.user_view_location);
        dateJoinedTextView = view.findViewById(R.id.user_view_dateJoined);
        roleTextView = view.findViewById(R.id.user_view_roles);
        deleteButton = view.findViewById(R.id.delete_button_user);
        cancelButton = view.findViewById(R.id.cancel_button_user);
        entrantSwitch = view.findViewById(R.id.entrant_switch);
        organizerSwitch = view.findViewById(R.id.organizer_switch);
        adminSwitch = view.findViewById(R.id.admin_switch);

        Bundle args = getArguments();
        if (args != null) {
            documentId = args.getString("deviceId");
            isNewUser = args.getBoolean("isNewUser", false);

            if (!isNewUser) {
                usernameTextView.setText(args.getString("username", "N/A"));
                deviceIdTextView.setText(args.getString("deviceId", "N/A"));
                emailTextView.setText(args.getString("email", "N/A"));
                phoneNumberTextView.setText(args.getString("phoneNumber", "N/A"));
                profilePictureUrlTextView.setText(args.getString("profilePictureUrl", "N/A"));
                locationTextView.setText(args.getString("location", "N/A"));
                dateJoinedTextView.setText(args.getString("dateJoined", "N/A"));

                ArrayList<String> roles = args.getStringArrayList("roles");
                if (roles != null && !roles.isEmpty()) {
                    roleTextView.setText(String.join(", ", roles));
                } else {
                    roleTextView.setText("N/A");
                }

                // Check roles using utility method
                entrantSwitch.setChecked(hasRole(roles, "entrant"));
                adminSwitch.setChecked(hasRole(roles, "admin"));
                organizerSwitch.setChecked(hasRole(roles, "organizer"));

                deleteButton.setVisibility(View.VISIBLE);
            } else {
                deleteButton.setVisibility(View.GONE);
            }
        }

        deleteButton.setOnClickListener(v -> {
            if (documentId != null) {
                deleteUser(documentId);
            }
        });

        cancelButton.setOnClickListener(v -> getActivity().onBackPressed());

        return view;
    }

    private boolean hasRole(ArrayList<String> roles, String role) {
        return roles != null && roles.contains(role);
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
}