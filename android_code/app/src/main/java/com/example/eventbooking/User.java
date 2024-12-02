package com.example.eventbooking;

import static android.app.PendingIntent.getActivity;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.eventbooking.Admin.Images.ImageClass;
import com.example.eventbooking.Events.EventData.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.SetOptions;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.location.Location;
import com.google.firebase.firestore.GeoPoint;
import android.Manifest;
import android.content.Context;

/**
 * The User class where we are storing the data and is the main model for User.
 * Instances of firebase related to User go through this where we load based on the document ID
 * and pass data through here to be updated.
 *
 * @since   2024-11-04
 */
public class User implements Parcelable{
    // Unsure if making this universal for the user upon creation of a new user or not
    // Once loaded do we keep this constantly? How is this saved in Firebase?

    public static String standardProfileURL;
    private String username;
    private String deviceId;//changed from int to string here
    private String email;
    private String phoneNumber;
    private GeoPoint geolocation;
    // profile picture
    private String profilePictureUrl;
    private String defaultprofilepictureurl;
    private String location; // this is for facilities
    private String address = "123 Applewood St.";
    private boolean adminLevel;
    private boolean facilityAssociated;
    private boolean notificationAsk;
    private boolean geolocationAsk;
    private boolean testing = true;

    private List<String> roles;
    //Firebase
    public StorageReference storageReference;
//    FirebaseFirestore db;
    private FirebaseFirestore db;
    private FirebaseStorage storage;


    /**
     * This constructor is used to instantiate lists inside of the class so when calling them
     * they are not null
     */
    public User() {
        //init roles to avoid null pointer exception
        this.roles = new ArrayList<>();
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            this.storageReference = FirebaseStorage.getInstance().getReference();
            this.db = FirebaseFirestore.getInstance();
        }

    }

    public User(Boolean testing) {
        //init roles to avoid null pointer exception
        this.roles = new ArrayList<>();
    }



    public User(FirebaseFirestore db, FirebaseStorage storage) {
        this.db = db;
        this.storage = storage;
        this.roles = new ArrayList<>();
    }

    // Constructor for testing that allows injecting mock dependencies
    public User(StorageReference storageReference, FirebaseFirestore db) {
        this.storageReference = storageReference;
        this.db = db;
        this.roles = new ArrayList<>();
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

    public GeoPoint getGeolocation() {
        return geolocation;
    }
    public void setGeolocation(GeoPoint geolocation) {
        this.geolocation = geolocation;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getdefaultProfilePictureUrl() {
        return defaultprofilepictureurl;
    }
    public void setdefaultProfilePictureUrl(String defaultprofilepictureurl) {
        this.defaultprofilepictureurl = defaultprofilepictureurl;
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
     * Interface for uploading images to firebase storage
     */
    public interface OnImageUploadComplete {
        void onImageUploadComplete(String imageURL);
        void onImageUploadFailed(Exception e);
    }

    /**
     * Interface for removing images from firebase storage
     */
    public interface OnImageRemovalComplete {
        void onImageRemovalSuccess();
        void onImageRemovalFailed(Exception e);
    }

    /**
     * Interface for saving generated images to firebase storage
     */
    public interface OnProfilePictureGeneratedListener {
        void onProfilePictureGenerated();
        void onProfilePictureGenerationFailed(Exception e);
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
    public Task<Void> defaultProfilePictureUrl(String name) {
        Bitmap bitmap = generateProfileBitmap(name);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        // Upload image to Firebase Storage and save the URL
        return uploadDefaultImageToFirebaseStorage(byteArray);
    }

    /**
     * Uploads an image byte array to Firebase Storage and retrieves the download URL upon successful upload.
     * The URL is then saved as the profile picture URL in Firestore.
     *
     * @param imageBytes The byte array of the image to upload.
     */
    public Task<Void> uploadDefaultImageToFirebaseStorage(byte[] imageBytes) {
        // Generate a unique identifier for the image
        String imageFileName = "defaultProfilePictures/" + UUID.randomUUID().toString() + ".png";
        StorageReference imageRef = storageReference.child(imageFileName);

        // Upload the image to Firebase Storage
        return imageRef.putBytes(imageBytes)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Get the download URL of the uploaded image
                    return imageRef.getDownloadUrl();
                })
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    Uri downloadUri = task.getResult();
                    String downloadUrl = downloadUri.toString();
                    profilePictureUrl = downloadUrl;
                    defaultprofilepictureurl = downloadUrl;

                    return saveGeneratedImageUrl(downloadUrl);
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
        Log.d("User", "User ID: " + deviceId);
        Log.d("User", "User ID: " + profilePictureUrl);
        Log.d("User", "User ID: " + defaultprofilepictureurl);
        final String[] new_userID = {deviceId};
        if(deviceId == null){
            Log.d("User", "User ID is null");
            getNewUserID(new OnUserIDGenerated() {
                @Override
                public void onUserIDGenerated(String userID) {
                    if (userID != null) {
                        deviceId = userID;
                        userData.put("deviceId", deviceId);
                        saveDataToFirestore(userData);
//                        new_userID[0] = userID;
                        Log.d("New User", userID);
                    } else {
                        // Handle the error if userID is null
                        Log.e("New User", "Failed to generate user ID.");
                        Log.d("New User", "Failed to generate user ID");
                    }
                }
            });
            Log.d("User", "User ID generated to be: " + new_userID[0]);
            userData.put("deviceId", new_userID[0]);
            this.deviceId = new_userID[0];
        }
        else{
            userData.put("deviceId", deviceId);
        }
        Log.d("User", "User ID: " + deviceId);
        Log.d("User", "User ID: " + profilePictureUrl);
        Log.d("User", "User ID: " + defaultprofilepictureurl);
        userData.put("username", username);
        userData.put("email", email);
        userData.put("phoneNumber", phoneNumber);
        userData.put("profilePictureUrl", profilePictureUrl);
        if (defaultprofilepictureurl != null) {
            userData.put("defaultProfilePictureUrl", defaultprofilepictureurl);
        }
        userData.put("location", location != null ? location.toString() : null);
        userData.put("adminLevel", adminLevel);
        userData.put("facilityAssociated", facilityAssociated);
        userData.put("notificationAsk", notificationAsk);
        userData.put("geolocationAsk", geolocationAsk);
        userData.put("roles", roles);
        userData.put("geolocation", geolocation);

        // Save data under "Users" collection and return the Task
        return db.collection("Users").document(deviceId)
                .set(userData, SetOptions.merge())
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
                        userData.put("deviceId", deviceId);
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
            userData.put("deviceId", deviceId);
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
        if (defaultprofilepictureurl != null) {
            userData.put("defaultProfilePictureUrl", defaultprofilepictureurl);
        }
        userData.put("location", location != null ? location.toString() : null);
        userData.put("adminLevel", adminLevel);
        userData.put("facilityAssociated", facilityAssociated);
        userData.put("notificationAsk", notificationAsk);
        userData.put("geolocationAsk", geolocationAsk);
        userData.put("geolocation", geolocation);
        userData.put("roles", roles);

        // Save data under "Users" collection and return the Task
        db.collection("Users").document(deviceId)
                .set(userData, SetOptions.merge())
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
//                        saveImageUrl(imageURL);
                        profilePictureUrl = imageURL;
                        Log.d("User", "Profile picture URL updated: " + imageURL);
                        saveUserDataToFirestore()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("User", "User data updated with new profile picture URL.");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("User", "Failed to update user data with new profile picture URL.", e);
                                });
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

//    /**
//     * Alternate version to the above upload image code to upload an image to firebase and
//     * get a link to it back
//     *
//     *
//     * @param imageUri
//     * @param callback
//     */
//    public void uploadImageAndGetUrl(Uri imageUri, OnImageUploadComplete callback) {
//        // Generate a unique key for the image
//        final String randomKey = UUID.randomUUID().toString();
//        StorageReference ref = storageReference.child("images/" + randomKey);
//
//        ref.putFile(imageUri)
//                .addOnSuccessListener(taskSnapshot -> {
//                    // Get the download URL
//                    ref.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
//                        String imageURL = downloadUrl.toString();
//                        // Do not save the image URL to Firestore here
//                        // Return the imageURL via the callback
//                        callback.onImageUploadComplete(imageURL);
//                    }).addOnFailureListener(e -> {
//                        Log.e("Firebase", "Failed to get download URL", e);
//                        callback.onImageUploadFailed(e);
//                    });
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("Firebase", "Image upload failed", e);
//                    callback.onImageUploadFailed(e);
//                });
//    }


    // Save image URL to Firestore

    /**
     * Saving the generated image url to the user and updating the firebase
     * @param imageURL
     */
    public Task<Void> saveGeneratedImageUrl(String imageURL) {

        Map<String, Object> imageData = new HashMap<>();
        imageData.put("profilePictureUrl", imageURL);
        imageData.put("defaultProfilePictureUrl", imageURL);

        return db.collection("Users").document(deviceId)
                .set(imageData, SetOptions.merge());
    }

    // Update profile picture
    public void updateProfilePicture(Uri newPictureUri) {
        uploadImage(newPictureUri);
    }

    /**
     * This will delete a given image from firebase storage (assuming that this is used when deleting
     * the users uploaded profile picture
     * @param imageUrl
     */
    public void deleteSelectedImageFromFirebase(String imageUrl) {
        // Get a reference to the image in Firebase Storage
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

//             Delete the image from Firebase Storage
            storageRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // Successfully deleted the image
                        Log.d("FirebaseStorage", "Selected image deleted successfully.");

                        // Update the user's profile picture URL to the default
                        this.profilePictureUrl = defaultprofilepictureurl;

                        // Save the updated user data to Firestore
                        saveUserDataToFirestore()
                                .addOnSuccessListener(saveVoid -> {
                                    Log.d("Firestore", "Profile picture URL reset to default successfully.");
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Failed to update profile picture URL", e);
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Failed to delete the image from Firebase Storage
                        Log.e("FirebaseStorage", "Failed to delete selected image", e);
                    });

        }
        else{
            UniversalProgramValues.getInstance().setDeleteFirebaseImage(imageUrl);
            UniversalProgramValues.getInstance().getSingle_user().setProfilePictureUrl(
                    UniversalProgramValues.getInstance().getSingle_user().getdefaultProfilePictureUrl());
            this.profilePictureUrl = defaultprofilepictureurl;
//            callback.onComplete(Task.forResult(true));
        }

    }
    /**
     * Checking to see if there is another URL for the user or not
     *
     * @return true boolean if the default URL is the main one
     */
    public boolean isDefaultURLMain(){
        if(profilePictureUrl == defaultprofilepictureurl)
            return true;
        return false;
    }
    /**
     * Setting the called upon profile picture URL to the default
     */
    public void setMainToDefault(){
        this.profilePictureUrl = this.defaultprofilepictureurl;
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
        Log.d("User", "Generating new user ID");
        db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int newUserID = task.getResult().size() + 1;
                    String userID = "deviceID" + newUserID;
                    callback.onUserIDGenerated(userID);
                    Log.d("New User", "Generated user ID: " + userID);
                } else {
                    // Handle error if necessary
                    Log.d("New User", "Error getting new user ID");
                    Log.e("Firebase", "Error getting documents: ", task.getException());
                    callback.onUserIDGenerated(null); // Notify callback about failure
                }
            }
        });
    }


    /**
     * Below code is to make User parcelable and I can pass this back and forth
     *
     *
     */

    // Parcelable constructor
    protected User(Parcel in) {
        username = in.readString();
        deviceId = in.readString();
        email = in.readString();
        phoneNumber = in.readString();
        geolocation = in.readParcelable(GeoPoint.class.getClassLoader());
        profilePictureUrl = in.readString();
        defaultprofilepictureurl = in.readString();
        location = in.readString();
        address = in.readString();
        adminLevel = in.readByte() != 0;
        facilityAssociated = in.readByte() != 0;
        notificationAsk = in.readByte() != 0;
        geolocationAsk = in.readByte() != 0;
        testing = in.readByte() != 0;
        roles = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(deviceId);
        dest.writeString(email);
        dest.writeString(phoneNumber);
        dest.writeParcelable((Parcelable) geolocation, flags);
        dest.writeString(profilePictureUrl);
        dest.writeString(defaultprofilepictureurl);
        dest.writeString(location);
        dest.writeString(address);
        dest.writeByte((byte) (adminLevel ? 1 : 0));
        dest.writeByte((byte) (facilityAssociated ? 1 : 0));
        dest.writeByte((byte) (notificationAsk ? 1 : 0));
        dest.writeByte((byte) (geolocationAsk ? 1 : 0));
        dest.writeByte((byte) (testing ? 1 : 0));
        dest.writeStringList(roles);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };









}
