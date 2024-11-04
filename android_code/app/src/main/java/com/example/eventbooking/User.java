package com.example.eventbooking;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.eventbooking.Events.EventData.Event;
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

/**
 * The User class where we are storing the data and is the main model for User.
 * Instances of firebase related to User go through this where we load based on the document ID
 * and pass data through here to be updated.
 *
 * @since   2024-11-04
 */
public class User{
    // Unsure if making this universal for the user upon creation of a new user or not
    // Once loaded do we keep this constantly? How is this saved in Firebase?

    public static String standardProfileURL;




    private String username;
    private String deviceID;//changed from int to string here
    private String email;
    private String phoneNumber;
    // profile picture
    private String profilePictureUrl;
    private Location location;
    private String address = "123 Applewood St.";
    private boolean adminLevel;
    private boolean facilityAssociated;
    private boolean notificationAsk;
    private boolean geolocationAsk;

    private List<String> roles;
    //Firebase
    private FirebaseStorage storage;
    private FirebaseFirestore db;

    /**
     * This constructor is used to instantiate lists inside of the class so when calling them
     * they are not null
     */
    public User() {
        //init roles to avoid null pointer exception
        this.roles = new ArrayList<>();
        storage = FirebaseStorage.getInstance(); // Initialize Firebase Storage
        db = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    /**
     * This constructor is used to put in data to the User object such that the "base" user is defined.
     * Other parts of the user need to be defined as the fields are generated
     */
    public User(String deviceID, String username, String email, String phoneNumber,Set<String> roles) {
        this.deviceID = deviceID;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = defaultProfilePictureUrl();
        this.roles = new ArrayList<>();
        this.roles.add(Role.ENTRANT); //set default role to be entrant
        this.storage = FirebaseStorage.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }


    /**
     * Getters and Setters for the given fields that can be easily set or we want to get
     */
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

    // Getter and Setter for location, hard set for now
    public String getAddress() {
        return address;
    }
    public void setAddress(String newAddress){
        this.address = newAddress;
    }

    /**
     * This getter and setter is more unique as it is a set that we are looking at and wanting to
     * add or check if the role exists, also remove role if such a role exists
     */
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

    public void removeRole(String role){
        if(roles==null){
            return;
        }
        if(hasRole(role)){
            roles.remove(role);
        }
    }

    /**
     * This setter sets the defualt profile picture for the user when they first create the account.
     * This makes sure we dont have null values when looking for images to load when reaching the
     * given page
     */
    public String defaultProfilePictureUrl(){
        if(username != null && !username.isEmpty()){
            return "https://firebasestorage.googleapis.com/v0/b/YOUR_FIREBASE_PROJECT_ID/o/default%2F" + username + ".png?alt=media";
        }
        else {
            throw new IllegalArgumentException("User is invalid");
        }
    }

    /**
     * Task to save the given user to firebase, take in all associated values  and put them in a map
     * which will then be saved as a collective document, firebase handles the direct mapping so long
     * as the fields names match.
     *
     * It saves it to the Users collection inside of firebase
     */
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
        return db.collection("Users").document(deviceID)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("User data successfully written to Firestore!");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error writing user data to Firestore: " + e.getMessage());
                });
    }

    /**
     * The core aspect of this function uploadProfilePictureToFirebase is to get a picture to upload
     * Then given that picture that was uploaded we get the link reference to firebase storage.
     * This link is saved to the user so then when calling the users profile it calls this new link
     * instead.
     */
    public void uploadProfilePictureToFirebase(String picture){
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username must be set before uploading a profile picture.");
        }
        if (picture == null || picture.isEmpty()) {
            throw new IllegalArgumentException("Invalid picture path.");
        }

        StorageReference storageRef = storage.getReference();

        // Create a unique filename for the picture
        String fileName = "profile_picture_" + System.currentTimeMillis() + ".jpg";
        StorageReference profilePicRef = storageRef.child("users/" + username + "/profile_pictures/" + fileName);

        // Convert the picture path to a Uri
        Uri fileUri = Uri.fromFile(new File(picture));

        // Start the upload task
        UploadTask uploadTask = profilePicRef.putFile(fileUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL after the upload is successful
            profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                profilePictureUrl = uri.toString();
                System.out.println("Profile picture uploaded successfully: " + profilePictureUrl);
                Map<String, Object> updates = new HashMap<>();
                updates.put("profilePictureUrl", profilePictureUrl);

                db.collection("Users").document(username)
                        .update(updates)
                        .addOnSuccessListener(updateVoid -> {
                            System.out.println("Profile picture URL successfully updated in Firestore.");
                        })
                        .addOnFailureListener(e -> {
                            System.out.println("Error updating profile picture URL in Firestore: " + e.getMessage());
                        });

            }).addOnFailureListener(exception -> {
                throw new IllegalArgumentException("Failed to retrieve download URL from Firebase.", exception);
            });
        }).addOnFailureListener(exception -> {
            throw new IllegalArgumentException("Failed to upload the profile picture to Firebase.", exception);
        });
    }

    // I dont understand why this is here, it doesnt make sense when you call the above function
    // With no new functionality?
    public void updateProfilePictureUrlToFirebase(String newPicture){
        uploadProfilePictureToFirebase(newPicture);
    }

    /**
     * Deleting a photo from firebase requires we find the reference inside of the storage and if
     * that reference is there then delete it and then make the default picture the photo
     */

    ///TODO:
    // For future implementation we need to add the default picture if we are making the link
    // Unique, ie. not using one photo and linking it to everything rather having one default photo
    // per account.
    // Ideas for the future, also if we are doing a default image, consider what happens when we
    // delete the user (if they have uploaded an image we need to delete both) or if the link
    // is automatically generated for us then save it somewhere rather than generating a new one
    // time incase the photo is deleted
    public void deleteProfilePictureFromFirebase(){
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            StorageReference storageRef = storage.getReferenceFromUrl(profilePictureUrl);

            // Start the deletion task
            storageRef.delete().addOnSuccessListener(aVoid -> {
                // Reset the profile picture URL to the default after deletion
                profilePictureUrl = defaultProfilePictureUrl();
                Map<String, Object> updates = new HashMap<>();
                updates.put("profilePictureUrl", profilePictureUrl);

                db.collection("users").document(username)
                        .update(updates)
                        .addOnSuccessListener(updateVoid -> {
                            System.out.println("Profile picture URL successfully updated in Firestore.");
                        })
                        .addOnFailureListener(e -> {
                            System.out.println("Error updating profile picture URL in Firestore: " + e.getMessage());
                        });
                System.out.println("Profile picture deleted successfully.");
            }).addOnFailureListener(exception -> {
                throw new IllegalArgumentException("Failed to delete the profile picture from Firebase.", exception);
            });
        } else {
            throw new IllegalArgumentException("Profile picture URL is invalid or already deleted.");
        }
    }


}