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
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventbooking.R;
import com.example.eventbooking.Role;
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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
        if(!UniversalProgramValues.getInstance().getTestingMode())
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
        // "deviceId" represents the unique identifier of the user.
        // "isNewUser" indicates whether this is a new user being created or an existing user being edited.
        Bundle args = getArguments();
        if (args != null) {
            user = args.getParcelable("userData");
            documentId = user.getDeviceID();

            usernameTextView.setText(user.getUsername());
            deviceIdTextView.setText(user.getDeviceID());
            emailTextView.setText(user.getEmail());
            phoneNumberTextView.setText(user.getPhoneNumber());
            profilePictureUrlTextView.setText(user.getProfilePictureUrl());
            locationTextView.setText(user.getLocation());
            dateJoinedTextView.setText("N/A");

            List<String> roles = user.getRoles();
            if (roles != null && !roles.isEmpty()) {
                roleTextView.setText(String.join(", ", roles));
            } else {
                roleTextView.setText("N/A");
            }

            // Check roles using utility method
            entrantSwitch.setChecked(user.hasRole(Role.ENTRANT));
            adminSwitch.setChecked(user.hasRole(Role.ADMIN));
            organizerSwitch.setChecked(user.hasRole(Role.ORGANIZER));

            deleteButton.setVisibility(View.VISIBLE);

        }

        deleteButton.setOnClickListener(v -> {
            if (documentId != null) {
                deleteUser(documentId);
            }
        });

        cancelButton.setOnClickListener(v -> moveFragment(new ViewUsersFragment()));

        return view;
    }

    private boolean hasRole(ArrayList<String> roles, String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Deletes the user's document from Firebase Firestore.
     * This operation removes the user's data from the "Users" collection.
     * A success message is shown on successful deletion, and the user is navigated back.
     *
     * @param documentId The document ID of the user in Firestore, which uniquely identifies the user to delete.
     */
    // change this so it deletes it from firebase
    private void deleteUser(String documentId) {
        if(!UniversalProgramValues.getInstance().getTestingMode()) {
            db.collection("Users").document(documentId).delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "User deleted successfully.", Toast.LENGTH_SHORT).show();
                        moveFragment(new ViewUsersFragment());
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
//        getActivity().onBackPressed();
        }
        else{
            UniversalProgramValues.getInstance().removeSpecificUser(documentId);
            moveFragment(new ViewUsersFragment());
        }
    }

    private void moveFragment(Fragment movingFragment){
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, movingFragment)
                .addToBackStack(null)
                .commit();
    }
}