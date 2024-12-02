package com.example.eventbooking.Testing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility.Facility;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

/**
 * FirebaseTestingActivity is an activity that demonstrates interacting with Firebase Firestore
 * and Firebase Storage. It allows users to upload images, save them to Firestore, and load data
 * from Firestore for users, facilities, and events.
 */
public class FirebaseTestingActivity extends AppCompatActivity {

    private static final String TAG = "FirebaseTestingActivity";
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button btnGenerateData, btnLoadData, btnSelectImage, btnUploadImage;
    private ImageView imageView;
    private TextView txtStatus;

    private Uri imageUri;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    /**
     * An instance of SampleTable used to generate and manage sample data
     * for users, facilities, and events.
     */
    private SampleTable sampleTable;

    /**
     * Called when the activity is first created. Initializes Firebase, UI components, and sets
     * click listeners for the buttons.
     *
     * @param savedInstanceState The saved state from a previous instance of the activity, if any.
     */
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

    /**
     * Generates data for users, facilities, and events, and saves it to Firebase Firestore.
     * Displays a status message and shows a toast message upon success or failure.
     */
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

    /**
     * Loads data from Firebase Firestore, including users, facilities, and events.
     * Displays the number of loaded items and logs any errors.
     */
    private void loadDataFromFirebase() {
        txtStatus.setText("Loading data from Firebase...");

        // Load Users
        db.collection("Users").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> users = queryDocumentSnapshots.toObjects(User.class);
                    Log.d(TAG, "Users loaded: " + users.size());
                    txtStatus.setText("Users loaded: " + users.size());
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading users", e));

        // Load Facilities
        db.collection("facilities").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Facility> facilities = queryDocumentSnapshots.toObjects(Facility.class);
                    Log.d(TAG, "Facilities loaded: " + facilities.size());
                    txtStatus.append("\nFacilities loaded: " + facilities.size());
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading facilities", e));

        // Load Events
        db.collection("Events").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> events = queryDocumentSnapshots.toObjects(Event.class);
                    Log.d(TAG, "Events loaded: " + events.size());
                    txtStatus.append("\nEvents loaded: " + events.size());
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading events", e));
    }

    /**
     * Opens a file chooser to allow the user to select an image file from their device.
     * This method launches an intent to open the device's content picker for image files.
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the file chooser. Displays the selected image in an ImageView.
     *
     * @param requestCode The request code provided when starting the activity.
     * @param resultCode  The result code returned by the activity.
     * @param data        The data returned by the activity, containing the selected image URI.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            imageUri = data.getData();
            Picasso.get().load(imageUri).into(imageView);
        }
    }

    /**
     * Uploads the selected image to Firebase Storage and saves the image URL to Firestore.
     */
    private void uploadImage() {
        if (imageUri != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image...");
            progressDialog.show();

            StorageReference storageRef = storage.getReference();
            String fileName = UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storageRef.child("images/" + fileName);

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
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

    /**
     * Saves the image link to Firestore under the "Images" collection.
     *
     * @param imageUrl   The URL of the uploaded image.
     * @param usage      The usage description for the image (e.g., "Profile Picture").
     * @param description A brief description of the image.
     */
    private void saveImageLinkToFirestore(String imageUrl, String usage, String description) {
        String documentId = UUID.randomUUID().toString();
        ImageData imageData = new ImageData(imageUrl, usage, description);

        db.collection("Images").document(documentId)
                .set(imageData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Image link saved to Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error saving image link", e));
    }

    /**
     * ImageData is a class representing the data structure for an image stored in Firestore.
     */
    public static class ImageData {
        private String imageUrl;
        private String usage;
        private String description;

        /**
         * No-arg constructor required for Firestore serialization.
         */
        public ImageData() {
            // Firestore requires a no-arg constructor
        }

        /**
         * Constructs an ImageData object for saving image information in Firestore.
         *
         * @param imageUrl   The public URL of the image stored in Firebase Storage.
         * @param usage      A short description of the purpose of the image (e.g., "Profile Picture").
         * @param description A longer description or additional details about the image.
         */
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
