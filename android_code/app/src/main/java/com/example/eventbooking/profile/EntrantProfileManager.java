package com.example.eventbooking.profile;

import com.google.firebase.firestore.FirebaseFirestore;

public class EntrantProfileManager {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Method to create or update a profile in Firestore using the device ID
    public void createOrUpdateProfile(String deviceID, EntrantProfile profile) {
        db.collection("Users")
                .document(deviceID)
                .set(profile)
                .addOnSuccessListener(aVoid -> System.out.println("Profile saved successfully."))
                .addOnFailureListener(e -> System.err.println("Error saving profile: " + e.getMessage()));
    }

    // Method to get a profile from Firestore using the device ID
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

    public interface ProfileLoadCallback {
        void onProfileLoaded(EntrantProfile profile);
    }
}
