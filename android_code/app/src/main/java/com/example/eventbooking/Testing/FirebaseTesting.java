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

public class FirebaseTesting {
    private static final String TAG = "FirebaseTesting";
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public FirebaseTesting() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // Method to upload an image to Firebase Storage and store its metadata in Firestore
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

    // Method to load images from Firestore and display them
    public void loadImages() {
        db.collection("Images").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String link = document.getString("link");
                            String usageLocation = document.getString("usageLocation");
                            String description = document.getString("description");

                            // Here, you can use the link to display the image in your app
                            Log.d(TAG, "Image: " + link);
                            Log.d(TAG, "Usage Location: " + usageLocation);
                            Log.d(TAG, "Description: " + description);

                            // For example, you can use an ImageView and a library like Glide or Picasso
                            // ImageView imageView = findViewById(R.id.imageView);
                            // Glide.with(context).load(link).into(imageView);
                        }
                    } else {
                        Log.e(TAG, "Error getting images from Firestore", task.getException());
                    }
                });
    }

    // Method to demonstrate loading and storing data
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

    // Method to load users from Firebase
    public void loadUsersFromFirebase() {
        db.collection("Users").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getString("username");
                            String email = document.getString("email");
                            // Retrieve other fields as needed
                            Log.d(TAG, "User: " + username + ", Email: " + email);
                        }
                    } else {
                        Log.e(TAG, "Error getting users from Firestore", task.getException());
                    }
                });
    }

    // Method to load facilities from Firebase
    public void loadFacilitiesFromFirebase() {
        db.collection("facilities").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String address = document.getString("address");
                            Log.d(TAG, "Facility: " + name + ", Address: " + address);
                        }
                    } else {
                        Log.e(TAG, "Error getting facilities from Firestore", task.getException());
                    }
                });
    }

    // Method to load events from Firebase
    public void loadEventsFromFirebase() {
        db.collection("Events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventId = document.getString("eventId");
                            String eventTitle = document.getString("eventTitle");
                            Log.d(TAG, "Event: " + eventId + ", Title: " + eventTitle);
                        }
                    } else {
                        Log.e(TAG, "Error getting events from Firestore", task.getException());
                    }
                });
    }
}
