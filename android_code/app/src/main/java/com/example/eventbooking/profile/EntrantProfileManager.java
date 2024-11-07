package com.example.eventbooking.profile;

import com.google.firebase.firestore.FirebaseFirestore;

public class EntrantProfileManager {
    /**
     * The EntrantProfileManager class is responsible for managing the EntrantProfile data
     * in Firebase Firestore. It provides methods to create, update, and retrieve profiles.
     */
    // FirebaseFirestore instance to interact with Firestore database
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    /**
     * Creates or updates a profile in Firestore using the device ID.
     * This method saves the provided EntrantProfile to the "Users" collection in Firestore.
     * If a profile already exists for the device ID, it will be updated.
     *
     * @param deviceID The unique device ID of the user
     * @param profile The EntrantProfile to be saved or updated
     */
    public void createOrUpdateProfile(String deviceID, EntrantProfile profile) {
        db.collection("Users")
                .document(deviceID)
                .set(profile)
                .addOnSuccessListener(aVoid -> System.out.println("Profile saved successfully."))
                .addOnFailureListener(e -> System.err.println("Error saving profile: " + e.getMessage()));
    }

    /**
     * Retrieves a profile from Firestore using the device ID.
     * If the profile exists, it is returned to the provided callback.
     * If the profile doesn't exist, a new empty EntrantProfile is returned.
     *
     * @param deviceID The unique device ID of the user
     * @param callback The callback to handle the loaded profile
     */
    public void getProfile(String deviceID, ProfileLoadCallback callback) {
        db.collection("Users").document(deviceID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        EntrantProfile profile = documentSnapshot.toObject(EntrantProfile.class);
                        callback.onProfileLoaded(profile != null ? profile : new EntrantProfile());
                    } else {
                        System.out.println("No profile found for this device ID.");
                        callback.onProfileLoaded(null);
                    }
                })
                .addOnFailureListener(e -> {
                    System.err.println("Error getting profile: " + e.getMessage());
                    callback.onProfileLoaded(null);
                });
    }
    /**
     * Callback interface used for handling the loaded profile.
     * Implement this interface to define actions once a profile is loaded.
     */
    public interface ProfileLoadCallback {
        /**
         * Called when the profile is successfully loaded from Firestore.
         *
         * @param profile The loaded EntrantProfile object, or null if no profile was found
         */
        void onProfileLoaded(EntrantProfile profile);
    }
}
