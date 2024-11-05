package com.example.eventbooking;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The User class where we are storing the data and is the main model for User.
 * Instances of firebase related to User go through this where we load based on the document ID
 * and pass data through here to be updated.
 *
 * @since   2024-11-04
 */
public class User {
    // Unsure if making this universal for the user upon creation of a new user or not
    // Once loaded do we keep this constantly? How is this saved in Firebase?

    public static String standardProfileURL;
    private String username;
    private String deviceID;//changed from int to string here
    private String email;
    private String phoneNumber;
    // profile picture
    private String profilePictureUrl;
    private String defaulutProfilePictureUrl;
    private Location location; // this is for facilities
    private String address = "123 Applewood St.";
    private boolean adminLevel;
    private boolean facilityAssociated;
    private boolean notificationAsk;
    private boolean geolocationAsk;

    private List<String> roles;
    //Firebase
    private final StorageReference storageReference;
    private FirebaseFirestore db;

    /**
     * This constructor is used to instantiate lists inside of the class so when calling them
     * they are not null
     */
    public User() {
        //init roles to avoid null pointer exception
        this.roles = new ArrayList<>();
        this.storageReference = FirebaseStorage.getInstance().getReference();
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * This constructor is used to put in data to the User object such that the "base" user is defined.
     * Other parts of the user need to be defined as the fields are generated
     */
    public User(String deviceID, String username, String email, String phoneNumber, Set<String> roles, StorageReference storageReference) {
        this.deviceID = deviceID;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.storageReference = storageReference;
        this.profilePictureUrl = defaultProfilePictureUrl();
        this.defaulutProfilePictureUrl = defaultProfilePictureUrl();
        this.roles = new ArrayList<>();
        this.roles.add(Role.ENTRANT); //set default role to be entrant
    }


    /**
     * Getters and Setters for the given fields that can be easily set or we want to get
     */
    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

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

    public void setAddress(String newAddress) {
        this.address = newAddress;
    }

    /**
     * This getter and setter is more unique as it is a set that we are looking at and wanting to
     * add or check if the role exists, also remove role if such a role exists
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role); //check if it already has a role
    }

    //add role
    public void addRole(String role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(role);
    }

    public void removeRole(String role) {
        if (roles == null) {
            return;
        }
        if (hasRole(role)) {
            roles.remove(role);
        }
    }

    /**
     * This setter sets the defualt profile picture for the user when they first create the account.
     * This makes sure we dont have null values when looking for images to load when reaching the
     * given page
     */
    private String defaultProfilePictureUrl() {
        if (username != null && !username.isEmpty()) {
            return "https://firebasestorage.googleapis.com/v0/b/letsgogambling-9ebb8.appspot.com/o/default%2F" + username + ".png?alt=media";
        } else {
            throw new IllegalArgumentException("Username is invalid.");
        }
    }

    /**
     * Task to save the given user to firebase, take in all associated values  and put them in a map
     * which will then be saved as a collective document, firebase handles the direct mapping so long
     * as the fields names match.
     * <p>
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
//    public void uploadProfilePictureToFirebase(String picture){
    public void uploadImage(Uri imageUri) {
        // Generate a unique key for the image
        final String randomKey = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("images/" + randomKey);

        // Start uploading the image
        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    ref.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                        String imageURL = downloadUrl.toString();
                        // Save the image URL to Firestore
                        saveImageUrl(imageURL);
                        profilePictureUrl = imageURL;
                    }).addOnFailureListener(e -> {
                        // Log the error if getting the download URL fails
                        Log.e("Firebase", "Failed to get download URL", e);
                    });
                })
                .addOnFailureListener(e -> {
                    // Log the error if the upload fails
                    Log.e("Firebase", "Image upload failed", e);
                });
    }

    // Save image URL to Firestore
    private void saveImageUrl(String imageURL) {

        // Create a map to hold the image URL
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("profilePictureUrl", imageURL);

        db.collection("Users").document(username)
                .update(imageData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Image URL updated successfully.");

                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Failed to save image URL", e);

                });
    }

    // Update profile picture
    public void updateProfilePicture(Uri newPictureUri) {
        uploadImage(newPictureUri);
    }

    public void deleteSelectedImageFromFirebase(String imageUrl) {
        // Get a reference to the image in Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

        // Delete the image from Firebase Storage
        storageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Successfully deleted the image
                    Log.d("FirebaseStorage", "Selected image deleted successfully.");

                    // Create a map to update the profile picture URL to the default value
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("profilePictureUrl", defaulutProfilePictureUrl);

                    // Update the user's profile picture URL in Firestore
                    db.collection("Users").document(username)
                            .update(updates)
                            .addOnSuccessListener(updateVoid -> {
                                // Successfully updated Firestore
                                Log.d("Firestore", "Profile picture URL reset to default successfully.");
                            })
                            .addOnFailureListener(e -> {
                                // Failed to update Firestore
                                Log.e("Firestore", "Failed to update profile picture URL", e);
                            });
                })
                .addOnFailureListener(e -> {
                    // Failed to delete the image from Firebase Storage
                    Log.e("FirebaseStorage", "Failed to delete selected image", e);
                });
    }
}
