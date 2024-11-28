package com.example.eventbooking.Testing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Facility;
import com.example.eventbooking.Facility;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.app.Activity;

/**
 * The TestFragment class is a Fragment used for testing and managing data related to users,
 * facilities, events, and image uploads within the app. This fragment provides functionality
 * for generating and saving sample data to Firebase, loading data from Firebase, selecting and
 * uploading images, and deleting all generated data from Firebase.
 */
public class TestFragment extends Fragment {

    private static final String TAG = "FirebaseTestingFragment";
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button btnGenerateData, btnLoadData, btnSelectImage, btnMapEvent;
    private Button generateQRCode,  btnUploadImage, btnDelete, btnBackHome;
    private ImageView imageView, QRCode;
    private TextView txtStatus;
    private EditText eventIDForQR;
    private QRcodeGenerator qrCodeGenerator;

    private Uri imageUri;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private SampleTable sampleTable;

    private boolean francisTest = true;

    /**
     * Default constructor for the TestFragment.
     */
    public TestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        qrCodeGenerator = new QRcodeGenerator(getContext());

        // Initialize SampleTable
        sampleTable = new SampleTable();
    }

    /**
     * Inflates the layout for this fragment and initializes UI components and event listeners.
     *
     * @param inflater LayoutInflater to inflate the view
     * @param container Parent view to contain the fragment's view
     * @param savedInstanceState The saved state of the fragment (if any)
     * @return The View for this fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);

        // Initialize UI components
        btnGenerateData = view.findViewById(R.id.btnGenerateData);
        btnLoadData = view.findViewById(R.id.btnLoadData);
        btnSelectImage = view.findViewById(R.id.btnSelectImage);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        generateQRCode = view.findViewById(R.id.button_generate_qr_code);
        btnDelete = view.findViewById(R.id.btnDeleteAllGenData);
        btnBackHome = view.findViewById(R.id.button_back_home);
        imageView = view.findViewById(R.id.imageView);
        QRCode = view.findViewById(R.id.potentialQRCode);
        txtStatus = view.findViewById(R.id.txtStatus);
        eventIDForQR = view.findViewById(R.id.event_id_for_qr);

        btnMapEvent = view.findViewById(R.id.button_event_map);

        if (francisTest == false) {
            btnDelete.setVisibility(View.GONE);
        }

        // Set click listeners for buttons
        btnGenerateData.setOnClickListener(v -> generateAndSaveData());
        btnLoadData.setOnClickListener(v -> loadDataFromFirebase());
        btnSelectImage.setOnClickListener(v -> openFileChooser());
        btnUploadImage.setOnClickListener(v -> uploadImage());
        btnDelete.setOnClickListener(v -> deleteAllData());
        btnBackHome.setOnClickListener(v -> backToHome());
        generateQRCode.setOnClickListener(v -> generateAndDisplayQRCode());
        btnMapEvent.setOnClickListener(v -> goToMap());
        return view;
    }

    /**
     * Generates sample data (users, facilities, and events) and saves it to Firebase.
     */
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

    /**
     * Loads data (users, facilities, and events) from Firebase Firestore and displays status.
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
        db.collection("Facilities").get()
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
     * Opens the file chooser to select an image from the device.
     */
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Start activity for result
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the image selection from the file chooser.
     *
     * @param requestCode The request code for the activity
     * @param resultCode The result code of the activity
     * @param data The intent containing the selected image URI
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
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
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
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

    /**
     * Saves the image URL to Firestore under the "Images" collection.
     *
     * @param imageUrl The URL of the uploaded image
     * @param usage The intended usage of the image
     * @param description A description of the image
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
     * Deletes all generated data (users, facilities, events) from Firebase Firestore.
     */
    private void deleteAllData() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Deleting All Data...");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        WriteBatch batch = db.batch();

        db.collection("Users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                String userId = document.getId();
                String profilePictureUrl = document.getString("profilePictureUrl");
                String defaultProfilePictureUrl = document.getString("defaultProfilePictureUrl");

                // Delete the profile picture(s) from Firebase Storage
                if (profilePictureUrl != null) {
                    if (profilePictureUrl.equals(defaultProfilePictureUrl)) {
                        // Delete only the default profile picture if the profile matches default
                        deleteProfilePictureFromStorage(defaultProfilePictureUrl);
                    } else {
                        // Delete both profile and default pictures if they are different
                        deleteProfilePictureFromStorage(profilePictureUrl);
                        deleteProfilePictureFromStorage(defaultProfilePictureUrl);
                    }
                }
                batch.delete(document.getReference());
            }

            db.collection("Facilities").get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                for (DocumentSnapshot document : queryDocumentSnapshots1.getDocuments()) {
                    batch.delete(document.getReference());
                }

                db.collection("Events").get().addOnSuccessListener(queryDocumentSnapshots2 -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots2.getDocuments()) {
                        batch.delete(document.getReference());
                    }

                    batch.commit().addOnSuccessListener(aVoid -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "All data deleted successfully.", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Failed to delete data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Error deleting events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Error deleting facilities: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Error deleting users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Represents image data that will be saved in Firestore.
     */
    public static class ImageData {
        private String imageUrl;
        private String usage;
        private String description;

        /**
         * Default constructor required for Firestore.
         */
        public ImageData() {
            // Firestore requires a no-arg constructor
        }

        /**
         * Constructs an ImageData object.
         *
         * @param imageUrl The URL of the image
         * @param usage The usage of the image
         * @param description A description of the image
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

    /**
     * Deletes a profile picture from Firebase Storage.
     *
     * @param imageUrl The URL of the profile picture to delete.
     */
    private void deleteProfilePictureFromStorage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);

        storageReference.delete().addOnSuccessListener(aVoid -> {
            Log.d("DeleteProfilePicture", "Profile picture deleted successfully.");
        }).addOnFailureListener(e -> {
            Log.e("DeleteProfilePicture", "Failed to delete profile picture: " + e.getMessage());
        });
    }



    /**
     * This function will get the QR code associated with the event to be scanned and displayed when scanned
     * Once the QR code is generated then we display the QR code
     */
    private void generateAndDisplayQRCode() {
        // URL to be encoded into the QR code (example URL with eventId)
        String event = eventIDForQR.getText().toString();
        String hashInput = event + Calendar.getInstance().getTime();
        String qrCodeHash = qrCodeGenerator.createQRCodeHash(hashInput);
        String eventUrl = "eventbooking://eventDetail?eventID=" + event + "?hash=" + qrCodeHash;
//        String eventUrl = "eventbooking://eventDetail?eventID=" + event;

        // Generate QR code using the QRcodeGenerator class
        Bitmap qrCodeBitmap = qrCodeGenerator.generateQRCode(eventUrl);

        if (qrCodeBitmap != null) {
            QRCode.setImageBitmap(qrCodeBitmap);
            qrCodeGenerator.saveQRCode(qrCodeBitmap, event);
            Toast.makeText(getContext(), "QR code generated and saved.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to generate QR code.", Toast.LENGTH_SHORT).show();
        }
    }

    private void backToHome(){
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new HomeFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void goToMap(){
        Bundle bundle = new Bundle();
        bundle.putString("eventId", "eventID5");
        EventMapFragment eventMapFragment = new EventMapFragment();
        eventMapFragment.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new EventMapFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
