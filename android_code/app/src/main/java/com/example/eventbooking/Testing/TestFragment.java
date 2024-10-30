package com.example.eventbooking.Testing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility;
import com.example.eventbooking.R; // Ensure you have the correct R import
import com.example.eventbooking.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

// Picasso library
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.Manifest;

public class TestFragment extends Fragment {

    private static final String TAG = "FirebaseTestingFragment";
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button btnGenerateData, btnLoadData, btnSelectImage, btnUploadImage;
    private ImageView imageView;
    private TextView txtStatus;

    private Uri imageUri;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private SampleTable sampleTable;

    public TestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize SampleTable
        sampleTable = new SampleTable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        // Initialize UI components
        btnGenerateData = view.findViewById(R.id.btnGenerateData);
        btnLoadData = view.findViewById(R.id.btnLoadData);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        imageView = view.findViewById(R.id.imageView);
        txtStatus = view.findViewById(R.id.txtStatus);

        // Set click listeners
        btnGenerateData.setOnClickListener(v -> generateAndSaveData());
        btnLoadData.setOnClickListener(v -> loadDataFromFirebase());
        btnSelectImage.setOnClickListener(v -> openFileChooser());
        btnUploadImage.setOnClickListener(v -> uploadImage());

        return view;
    }

    private void generateAndSaveData() {
        txtStatus.setText("Generating and saving data...");
        sampleTable.makeUserList();
        sampleTable.makeFacilityList();
        sampleTable.makeEventList();
        sampleTable.saveDataToFirebase(() -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    txtStatus.setText("Data saved successfully.");
                    Toast.makeText(getActivity(), "Data saved to Firebase", Toast.LENGTH_SHORT).show();
                });
            }
        }, e -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    txtStatus.setText("Error saving data.");
                    Toast.makeText(getActivity(), "Error saving data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
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
        db.collection("Facilities").get()
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
        // Start activity for result
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Handle result of image chooser
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if image was selected
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {

            imageUri = data.getData();

            // Display selected image in ImageView
            Picasso.get().load(imageUri).into(imageView);
        }
    }

    private void uploadImage() {
        if (imageUri != null) {
            // Show progress dialog
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
                            Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_SHORT).show();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();
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
