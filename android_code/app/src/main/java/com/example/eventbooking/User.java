package com.example.eventbooking;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
public class User {

    private String username;
    private String deviceID;//changed from int to string here
    private String email;
    private String phoneNumber;
    // profile picture
    private String profilePictureUrl;
    private String defaulutProfilePictureUrl;
    private Location location; // this is for facilities
    private boolean adminLevel;
    private boolean facilityAssociated;
    private boolean notificationAsk;
    private boolean geolocationAsk;

    private List<String> roles;
    //Firebase
    private FirebaseStorage storage;
    private FirebaseFirestore db;


    public User() {
        //init roles to avoid null pointer exception
        this.roles = new ArrayList<>();
        this.storage = FirebaseStorage.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    public User(String deviceID, String username, String email, String phoneNumber,Set<String> roles) {
        this.deviceID = deviceID;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = defaultProfilePictureUrl();
        this.defaulutProfilePictureUrl = defaultProfilePictureUrl();
        this.roles = new ArrayList<>();
        this.roles.add(Role.ENTRANT); //set default role to be entrant
        this.storage = storage;
        this.db = db;
    }


    public String getDeviceID() { return deviceID; }
    public void setDeviceID(String deviceID) { this.deviceID = deviceID; }

    public String getUsername() { return username; }
    public void setUsername(String name) { this.username = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber(){return phoneNumber;}
    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public List<String> getRoles() {return roles;}
    public void setRoles(List<String> roles) {this.roles = roles;}

    public boolean isFacilityAssociated() {
        return facilityAssociated;
    }
    public void setFacilityAssociated(boolean facilityAssociated) {
        this.facilityAssociated = facilityAssociated;
    }

    public boolean hasRole(String role){
        return roles != null && roles.contains(role); //check if it already has a role
    }
    //add role
    public void addRole(String role){
        if(roles==null){
            roles = new ArrayList<>();
        }
        roles.add(role);
    }

    public String defaultProfilePictureUrl(){
        if(username != null && !username.isEmpty()){
            return "https://firebasestorage.googleapis.com/v0/b/YOUR_FIREBASE_PROJECT_ID/o/default%2F" + username + ".png?alt=media";
        }
        else {
            throw new IllegalArgumentException("User is invalid");
        }
    }

    public Task<Void> saveUserDataToFirestore() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("deviceID", deviceID);
        userData.put("email", email);
        userData.put("phoneNumber", phoneNumber);
        userData.put("profilePictureUrl", profilePictureUrl);
        userData.put("location", location != null ? location.toString() : null);
        userData.put("adminLevel", adminLevel);
        userData.put("facilityAssociated", facilityAssociated);
        userData.put("notificationAsk", notificationAsk);
        userData.put("geolocationAsk", geolocationAsk);
        userData.put("roles", roles);

        // Save data under "Users" collection and return the Task
        return db.collection("Users").document(username)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("User data successfully written to Firestore!");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error writing user data to Firestore: " + e.getMessage());
                });
    }

    public void uploadProfilePictureToFirebase(Uri pictureUri) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must be set before uploading a profile picture.");
        }
        if (pictureUri == null) {
            throw new IllegalArgumentException("Invalid picture Uri.");
        }

        StorageReference storageRef = storage.getReference();
        String fileName = "profile_picture_" + System.currentTimeMillis() + ".jpg";
        StorageReference profilePicRef = storageRef.child("users/" + username + "/profile_pictures/" + fileName);

        // Start the upload task using Uri
        profilePicRef.putFile(pictureUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL after the upload is successful
                    profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        profilePictureUrl = uri.toString();
                        System.out.println("Profile picture uploaded successfully: " + profilePictureUrl);

                        // Update the URL in Firestore
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("profilePictureUrl", profilePictureUrl);

                        db.collection("Users").document(username)
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    System.out.println("Profile picture URL successfully updated in Firestore.");
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("Error updating profile picture URL in Firestore: " + e.getMessage());
                                });
                    }).addOnFailureListener(exception -> {
                        System.out.println("Failed to retrieve download URL from Firebase: " + exception.getMessage());
                    });
                })
                .addOnFailureListener(exception -> {
                    System.out.println("Failed to upload the profile picture to Firebase: " + exception.getMessage());
                });
    }


    public void updateProfilePictureUrlToFirebase(Uri newPictureUri){
        uploadProfilePictureToFirebase(newPictureUri);
    }

    public void deleteSelectedImageFromFirebase(String selectedImageUrl) {
        if (selectedImageUrl != null && !selectedImageUrl.isEmpty()) {
            // Create a StorageReference for the selected image
            StorageReference storageRef = storage.getReferenceFromUrl(selectedImageUrl);

            // Start the deletion task
            storageRef.delete().addOnSuccessListener(aVoid -> {
                System.out.println("Selected image deleted successfully from Firebase Storage.");

                // Optionally, update the Firestore database if necessary
                Map<String, Object> updates = new HashMap<>();
                updates.put("profilePictureUrl", defaultProfilePictureUrl());
                db.collection("Users").document(username)
                        .update(updates)
                        .addOnSuccessListener(updateVoid -> {
                            System.out.println("Profile picture URL successfully updated to default in Firestore.");
                        })
                        .addOnFailureListener(e -> {
                            System.out.println("Error updating profile picture URL in Firestore: " + e.getMessage());
                        });
            }).addOnFailureListener(exception -> {
                System.out.println("Failed to delete the selected image from Firebase Storage: " + exception.getMessage());
            });
        } else {
            throw new IllegalArgumentException("Selected image URL is invalid or already deleted.");
        }
    }


}