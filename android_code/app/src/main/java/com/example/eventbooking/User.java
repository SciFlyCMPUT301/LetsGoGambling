package com.example.eventbooking;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
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
    private String deviceId;//changed from int to string here
    private String email;
    private String phoneNumber;
    // profile picture
    private String profilePictureUrl;
    private String defaultprofilepictureurl;
    private String location; // this is for facilities
    private String address = "123 Applewood St.";
    private boolean adminLevel;
    private boolean facilityAssociated;
    private boolean notificationAsk;
    private boolean geolocationAsk;

    private List<String> roles;
    //Firebase
    StorageReference storageReference;
    FirebaseFirestore db;

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
    public User(String deviceID, String username, String email, String phoneNumber, Set<String> roles) {
        this.deviceId = deviceID;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = null;
        this.defaultprofilepictureurl = null;
        this.roles = new ArrayList<>();
        this.roles.add(Role.ENTRANT); //set default role to be entrant
        this.storageReference = FirebaseStorage.getInstance().getReference();
        this.db = FirebaseFirestore.getInstance();
    }


    public boolean isAdminLevel() {
        return adminLevel;
    }
    public void setAdminLevel(boolean adminLevel) {
        this.adminLevel = adminLevel;
    }

    /**
     * Getters and Setters for the given fields that can be easily set or we want to get
     */


    public String getDeviceID() {
        return deviceId;
    }

    public void setDeviceID(String deviceID) {
        this.deviceId = deviceID;
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

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isGeolocationAsk() {
        return geolocationAsk;
    }
    public void setGeolocationAsk(boolean geolocationAsk) {
        this.geolocationAsk = geolocationAsk;
    }

    public boolean isNotificationAsk() {
        return notificationAsk;
    }
    public void setNotificationAsk(boolean notificationAsk) {
        this.notificationAsk = notificationAsk;
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
     * Generates a circular bitmap with a single letter in the center.
     * The letter is the first character of the provided name, or "A" if the name is empty.
     * The bitmap is randomly colored for visual uniqueness.
     *
     * @param name The name from which to extract the first letter for the profile picture.
     * @return A 100x100 pixel circular bitmap with the initial letter in the center.
     */
    public Bitmap generateProfileBitmap(String name) {
        String letter = (name == null || name.isEmpty()) ? "A" : String.valueOf(name.charAt(0)).toUpperCase();
        int size = 100;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
        paint.setColor(Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        float yPos = (canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText(letter, size / 2, yPos, paint);

        return bitmap;
    }

    /**
     * Generates a profile picture, compresses it to a PNG format, and initiates its upload to Firebase Storage.
     * After successful upload, the Firebase Storage URL will be saved in Firestore as the profile picture URL.
     *
     * @param name The name from which to generate the profile picture with the first letter.
     * @return A string message indicating that profile picture generation and upload have been initiated.
     */
    public String defaultProfilePictureUrl(String name) {
        Bitmap bitmap = generateProfileBitmap(name);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Upload image to Firebase Storage and save the URL
        uploadDefaultImageToFirebaseStorage(byteArray);

        return "Profile picture generation initiated.";
    }

    /**
     * Uploads an image byte array to Firebase Storage and retrieves the download URL upon successful upload.
     * The URL is then saved as the profile picture URL in Firestore.
     *
     * @param imageBytes The byte array of the image to upload.
     */
    public void uploadDefaultImageToFirebaseStorage(byte[] imageBytes) {
        // Generate a unique identifier for the image
        String imageFileName = "defaultProfilePictures/" + UUID.randomUUID().toString() + ".png";
        StorageReference imageRef = storageReference.child(imageFileName);

        // Upload the image to Firebase Storage
        imageRef.putBytes(imageBytes)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        // Save the download URL to Firestore
                        profilePictureUrl = downloadUrl; // Set the profile URL here
                        defaultprofilepictureurl = downloadUrl; // Set as the default profile URL

                        saveImageUrl(downloadUrl);
                    }).addOnFailureListener(e -> {
                        Log.e("Firebase", "Failed to retrieve download URL", e);
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("Firebase", "Image upload failed", e);
                });
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
        final String[] new_userID = {deviceId};
        if(deviceId == null){
            getNewUserID(new OnUserIDGenerated() {
                @Override
                public void onUserIDGenerated(String userID) {
                    if (userID != null) {
                        new_userID[0] = userID;
                        Log.d("New User", userID);
                    } else {
                        // Handle the error if userID is null
                        Log.e("New User", "Failed to generate user ID.");
                    }
                }
            });

            userData.put("deviceId", new_userID[0]);
            this.deviceId = new_userID[0];
        }
        else{
            userData.put("deviceId", deviceId);
        }
        Log.d("User", "User ID: " + deviceId);
        userData.put("username", username);
        userData.put("email", email);
        userData.put("phoneNumber", phoneNumber);
        userData.put("profilePictureUrl", profilePictureUrl);
        userData.put("defaultProfilePictureUrl", defaultprofilepictureurl);
        userData.put("location", location != null ? location.toString() : null);
        userData.put("adminLevel", adminLevel);
        userData.put("facilityAssociated", facilityAssociated);
        userData.put("notificationAsk", notificationAsk);
        userData.put("geolocationAsk", geolocationAsk);
        userData.put("roles", roles);

        // Save data under "Users" collection and return the Task
        return db.collection("Users").document(deviceId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("User data successfully written to Firestore!");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error writing user data to Firestore: " + e.getMessage());
                });
    }


    public Task<Void> saveUserDataToFirestore(final OnUserIDGenerated callback) {
        Map<String, Object> userData = new HashMap<>();

        if (deviceId == null) {
            getNewUserID(new OnUserIDGenerated() {
                @Override
                public void onUserIDGenerated(String userID) {
                    if (userID != null) {
                        deviceId = userID;
                        userData.put("eventId", deviceId);
                        Log.d("User", "User ID: " + deviceId);
                        saveDataToFirestore(userData);  // Save data after ID is set
                        callback.onUserIDGenerated(deviceId);
                    } else {
                        Log.e("New User", "Failed to generate user ID.");
                        callback.onUserIDGenerated(null); // Notify callback about failure
                    }
                }
            });
        } else {
            userData.put("eventId", deviceId);
            saveDataToFirestore(userData);  // Save data if ID is already set
            callback.onUserIDGenerated(deviceId);  // Notify callback with the ID
        }

        return null;  // You can also return the result of save operation here
    }

    private void saveDataToFirestore(Map<String, Object> userData) {
        userData.put("username", username);
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
        db.collection("Users").document(deviceId)
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("User", "User data successfully written to Firestore!");
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error writing user data to Firestore: " + e.getMessage());
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
    void saveImageUrl(String imageURL) {

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
                    updates.put("profilePictureUrl", defaultprofilepictureurl);

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

    public interface OnUserIDGenerated {
        void onUserIDGenerated(String userID);
    }

    /**
     * Generating a new user deviceID for testing, in practice this is replaced with get device ID
     * and subsequent calls to that to a singleton
     *
     * @param callback
     */
    public void getNewUserID(final OnUserIDGenerated callback) {
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Calculate the new user ID by adding 1 to the current size
                    int newUserID = task.getResult().size() + 1;

                    // Create the final user ID with deviceID prefix
                    String userID = "deviceID" + newUserID;

                    // Pass the user ID to the callback
                    callback.onUserIDGenerated(userID);

                    // Log the user ID
                    Log.d("New User", "Generated user ID: " + userID);
                } else {
                    // Handle error if necessary
                    Log.e("Firebase", "Error getting documents: ", task.getException());
                    callback.onUserIDGenerated(null); // Notify callback about failure
                }
            }
        });
    }
}
