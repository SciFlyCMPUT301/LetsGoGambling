package com.example.eventbooking.Testing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility;
import com.example.eventbooking.R; // Ensure you have the correct R import
import com.example.eventbooking.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// Picasso library
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

public class FirebaseTestingActivity extends AppCompatActivity {

    private static final String TAG = "FirebaseTestingActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button btnGenerateData, btnLoadData, btnSelectImage, btnUploadImage;
    private ImageView imageView;
    private TextView txtStatus;

    private Uri imageUri;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private SampleTable sampleTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_testing);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize UI components
        btnGenerateData = findViewById(R.id.btnGenerateData);
        btnLoadData = findViewById(R.id.btnLoadData);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        imageView = findViewById(R.id.imageView);
        txtStatus = findViewById(R.id.txtStatus);

        // Initialize SampleTable
        sampleTable = new SampleTable();

        // Set click listeners
        btnGenerateData.setOnClickListener(v -> generateAndSaveData());
        btnLoadData.setOnClickListener(v -> loadDataFromFirebase());
        btnSelectImage.setOnClickListener(v -> openFileChooser());
        btnUploadImage.setOnClickListener(v -> uploadImage());
    }

    private void generateAndSaveData() {
        txtStatus.setText("Generating and saving data...");
        sampleTable.makeUserList();
        sampleTable.makeFacilityList();
        sampleTable.makeEventList();
        sampleTable.saveDataToFirebase(() -> {
            txtStatus.setText("Data saved successfully.");
            Toast.makeText(this, "Data saved to Firebase", Toast.LENGTH_SHORT).show();
        }, e -> {
            txtStatus.setText("Error saving data.");
            Toast.makeText(this, "Error saving data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    private void loadDataFromFirebase() {
        txtStatus.setText("Loading data from Firebase...");
        // Load Users
        db.collection("Users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = queryDocumentSnapshots.toObjects(User.class);
                    Log.d(TAG, "Users loaded: " + users.size());
                    // Display or process users as needed
                    txtStatus.setText("Users loaded: " + users.size());
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading users", e));

        // Load Facilities
        db.collection("facilities").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Facility> facilities = queryDocumentSnapshots.toObjects(Facility.class);
                    Log.d(TAG, "Facilities loaded: " + facilities.size());
                    // Display or process facilities as needed
                    txtStatus.append("\nFacilities loaded: " + facilities.size());
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading facilities", e));

        // Load Events
        db.collection("Events").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = queryDocumentSnapshots.toObjects(Event.class);
                    Log.d(TAG, "Events loaded: " + events.size());
                    // Display or process events as needed
                    txtStatus.append("\nEvents loaded: " + events.size());
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading events", e));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Handle result of image chooser
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if image was selected
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imageUri = data.getData();

            // Display selected image in ImageView
            Picasso.get().load(imageUri).into(imageView);
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            // Show progress dialog
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image...");
            progressDialog.show();

            // Get a reference to the storage
            StorageReference storageRef = storage.getReference();

            // Create a unique name for the image
            String fileName = UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storageRef.child("images/" + fileName);

            // Upload the image
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            // Save the link in Firestore
                            saveImageLinkToFirestore(downloadUrl, "Profile Picture", "User profile image");
                            progressDialog.dismiss();
                            Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageLinkToFirestore(String imageUrl, String usage, String description) {
        // Create a map to store in Firestore
        String documentId = UUID.randomUUID().toString();
        ImageData imageData = new ImageData(imageUrl, usage, description);

        // Save to Firestore in "Images" collection
        db.collection("Images").document(documentId)
                .set(imageData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Image link saved to Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving image link", e));
    }

    // ImageData class to represent image documents in Firestore
    public static class ImageData {
        private String imageUrl;
        private String usage;
        private String description;

        public ImageData() {
            // Firestore requires a no-arg constructor
        }

        public ImageData(String imageUrl, String usage, String description) {
            this.imageUrl = imageUrl;
            this.usage = usage;
            this.description = description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getUsage() {
            return usage;
        }

        public String getDescription() {
            return description;
        }
    }
}
