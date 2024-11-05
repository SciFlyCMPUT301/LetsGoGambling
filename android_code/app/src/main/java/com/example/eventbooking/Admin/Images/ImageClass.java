package com.example.eventbooking.Admin.Images;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImageClass {
    private static final String TAG = "ImageClass";

    private String URL;
    private Bitmap image; // Using Bitmap for images in Android
    private String source;
    private String subSource;

    // Getters and setters
    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSubsource() {
        return subSource;
    }

    public void setSubsource(String subsource) {
        this.subSource = subsource;
    }

    public void uploadImageToFirebase(String imageId, Uri imageUri, String description, String usage, OnUploadCompleteListener listener) {
        if (imageUri == null) {
            Log.e(TAG, "Image URI is null. Cannot upload.");
            if (listener != null) {
                listener.onUploadFailure("Image URI is null.");
            }
            return;
        }

        // Use the provided imageId for the image
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + imageId);

        // Upload the image to Firebase Storage
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        URL = uri.toString(); // Set the URL

                        // Save image details to Firestore
                        saveImageDetailsToFirestore(imageId, description, usage, listener);
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get download URL: " + e.getMessage());
                        if (listener != null) {
                            listener.onUploadFailure(e.getMessage());
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload image: " + e.getMessage());
                    if (listener != null) {
                        listener.onUploadFailure(e.getMessage());
                    }
                });
    }

    // Method to save the image details to the "Images" collection in Firestore
    private void saveImageDetailsToFirestore(String imageId, String description, String usage, OnUploadCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a map to store image data
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("description", description);
        imageData.put("imageUrl", URL);
        imageData.put("usage", usage);

        // Save the image data to Firestore
        db.collection("Images").document(imageId).set(imageData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Image details saved to Firestore successfully.");
                    if (listener != null) {
                        listener.onUploadSuccess(URL);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to save image details to Firestore: " + e.getMessage());
                    if (listener != null) {
                        listener.onUploadFailure(e.getMessage());
                    }
                });
    }

    // Method to delete the image from Firebase Storage and Firestore
    public void deleteImageFromFirebase(OnDeleteCompleteListener listener) {
        if (URL == null || URL.isEmpty()) {
            Log.e(TAG, "Image URL is null or empty. Cannot delete.");
            if (listener != null) {
                listener.onDeleteFailure("Image URL is null or empty.");
            }
            return;
        }

        // Step 1: Delete the image from Firebase Storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(URL);
        storageReference.delete().addOnSuccessListener(aVoid -> {
            Log.d(TAG, "Image deleted from Firebase Storage successfully.");

            // Step 2: Delete the image details from the "Images" collection in Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Images").document(subSource).delete()
                    .addOnSuccessListener(aVoid1 -> {
                        Log.d(TAG, "Image details deleted from Images collection successfully.");

                        // Step 3: Delete the image URL from the corresponding Firestore document in Users or Events
                        db.collection(source).document(subSource).update("imageUrl", null)
                                .addOnSuccessListener(aVoid2 -> {
                                    Log.d(TAG, "Image URL deleted from " + source + " collection successfully.");
                                    if (listener != null) {
                                        listener.onDeleteSuccess();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Failed to delete image URL from " + source + " collection: " + e.getMessage());
                                    if (listener != null) {
                                        listener.onDeleteFailure("Failed to delete image URL from " + source + " collection: " + e.getMessage());
                                    }
                                });
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to delete image details from Images collection: " + e.getMessage());
                        if (listener != null) {
                            listener.onDeleteFailure("Failed to delete image details from Images collection: " + e.getMessage());
                        }
                    });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to delete image from Firebase Storage: " + e.getMessage());
            if (listener != null) {
                listener.onDeleteFailure("Failed to delete image from Firebase Storage: " + e.getMessage());
            }
        });
    }

    // Interface to handle upload completion
    public interface OnUploadCompleteListener {
        void onUploadSuccess(String downloadUrl);
        void onUploadFailure(String errorMessage);
    }

    // Interface to handle delete completion
    public interface OnDeleteCompleteListener {
        void onDeleteSuccess();
        void onDeleteFailure(String errorMessage);
    }
}
