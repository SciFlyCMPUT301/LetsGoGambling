package com.example.eventbooking.Testing;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility;
import com.example.eventbooking.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * The FirebaseTesting class provides methods to interact with Firebase services, including
 * uploading images to Firebase Storage, storing metadata in Firestore, and loading data from Firestore.
 * It also provides methods to test Firebase operations by loading and saving data for users, facilities, and events.
 */
public class FirebaseTesting {

    private static final String TAG = "FirebaseTesting";
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    /**
     * Constructor that initializes Firebase Firestore and Firebase Storage instances.
     */
    public FirebaseTesting() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Uploads an image to Firebase Storage and stores its metadata (link, usage location, and description) in Firestore.
     *
     * @param localImagePath Path to the image file on the local storage
     * @param usageLocation A description of where the image will be used
     * @param description A detailed description of the image
     * @throws IllegalArgumentException if the image path is invalid or empty
     */
    public void uploadImage(String localImagePath, String usageLocation, String description) {
        if (localImagePath == null || localImagePath.isEmpty()) {
            throw new IllegalArgumentException("Invalid image path.");
        }

        // Create a reference to Firebase Storage
        StorageReference storageRef = storage.getReference();
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child("Images/" + fileName);

        // Convert the local image path to a Uri
        Uri fileUri = Uri.fromFile(new File(localImagePath));

        // Start the upload task
        UploadTask uploadTask = imageRef.putFile(fileUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL after the upload is successful
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                Log.d(TAG, "Image uploaded successfully: " + imageUrl);

                // Create a map to store in Firestore
                Map<String, Object> imageData = new HashMap<>();
                imageData.put("link", imageUrl);
                imageData.put("usageLocation", usageLocation);
                imageData.put("description", description);

                // Store the image data in Firestore under the "Images" collection
                db.collection("Images").add(imageData)
                        .addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "Image data stored in Firestore with ID: " + documentReference.getId());
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error adding image data to Firestore", e);
                        });
            }).addOnFailureListener(exception -> {
                Log.e(TAG, "Failed to retrieve download URL from Firebase Storage.", exception);
            });
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Failed to upload image to Firebase Storage.", exception);
        });
    }

    /**
     * Loads image data from Firestore and logs the image details (link, usage location, and description).
     * This can be extended to display images using a UI component such as an ImageView.
     */
    public void loadImages() {
        db.collection("Images").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String link = document.getString("link");
                            String usageLocation = document.getString("usageLocation");
                            String description = document.getString("description");

                            // Log the image details (you could display the image in your app here)
                            Log.d(TAG, "Image: " + link);
                            Log.d(TAG, "Usage Location: " + usageLocation);
                            Log.d(TAG, "Description: " + description);
                        }
                    } else {
                        Log.e(TAG, "Error getting images from Firestore", task.getException());
                    }
                });
    }

    /**
     * A method that demonstrates loading and saving data (users, facilities, events) in Firebase.
     * This is intended for testing Firebase operations.
     */
    public void testFirebaseOperations() {
        // Create a SampleTable instance and generate data
        SampleTable sampleTable = new SampleTable();
        sampleTable.makeUserList();
        sampleTable.makeFacilityList();
        sampleTable.makeEventList();

        // Save data to Firebase
        sampleTable.saveDataToFirebase(null, null);

        // Load data from Firebase
        loadUsersFromFirebase();
        loadFacilitiesFromFirebase();
        loadEventsFromFirebase();
    }

    /**
     * Loads user data from the "Users" collection in Firestore and logs the user details (username, email).
     */
    public void loadUsersFromFirebase() {
        db.collection("Users").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");
                            String email = document.getString("email");
                            // Log user details
                            Log.d(TAG, "User: " + username + ", Email: " + email);
                        }
                    } else {
                        Log.e(TAG, "Error getting users from Firestore", task.getException());
                    }
                });
    }

    /**
     * Loads facility data from the "Facilities" collection in Firestore and logs the facility details (name, address).
     */
    public void loadFacilitiesFromFirebase() {
        db.collection("facilities").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String address = document.getString("address");
                            // Log facility details
                            Log.d(TAG, "Facility: " + name + ", Address: " + address);
                        }
                    } else {
                        Log.e(TAG, "Error getting facilities from Firestore", task.getException());
                    }
                });
    }

    /**
     * Loads event data from the "Events" collection in Firestore and logs the event details (eventId, eventTitle).
     */
    public void loadEventsFromFirebase() {
        db.collection("Events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventId = document.getString("eventId");
                            String eventTitle = document.getString("eventTitle");
                            // Log event details
                            Log.d(TAG, "Event: " + eventId + ", Title: " + eventTitle);
                        }
                    } else {
                        Log.e(TAG, "Error getting events from Firestore", task.getException());
                    }
                });
    }
}
