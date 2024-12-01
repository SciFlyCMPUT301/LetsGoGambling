package com.example.eventbooking.Admin.Images;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Facility.Facility;
import com.example.eventbooking.R;
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.User;
import com.example.eventbooking.Events.EventData.Event;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewImagesFragment extends Fragment {
    private ListView imagesListView;
    private ImageAdapter imageAdapter;
    // Dont ever do this, why????
//    private ArrayList<Map<String, String>> imageList; // Stores image data
    // Do this instead
    private List<ImageClass> imageList;
    private Button adminGoBack, removeButton;
    private FirebaseFirestore db;
    private Map<String, String> selectedImageData = null;
    private ImageClass selectedImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_images, container, false);

        // Initialize Firestore and UI components
        if(!UniversalProgramValues.getInstance().getTestingMode())
            db = FirebaseFirestore.getInstance();
        imageList = new ArrayList<>();
        imagesListView = view.findViewById(R.id.image_list);
        imageAdapter = new ImageAdapter(getContext(), imageList);
        imagesListView.setAdapter(imageAdapter);
        adminGoBack = view.findViewById(R.id.admin_go_back);
        removeButton = view.findViewById(R.id.remove_image_button);

        // Load images from Firestore collections
        if(!UniversalProgramValues.getInstance().getTestingMode())
            loadImagesFromCollections();
        else{
            loadTestDataset();
        }

        // Handle ListView item clicks
        imagesListView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            // What is this????
            selectedImage = imageList.get(position);
            Toast.makeText(getContext(), "Selected Image", Toast.LENGTH_SHORT).show();
        });

        // Handle Remove Button click
        removeButton.setOnClickListener(v -> {
            if (selectedImage != null) {
                if(!UniversalProgramValues.getInstance().getTestingMode())
                    removeImage();
                else
                    removeImageUITest();
            } else {
                Toast.makeText(getContext(), "Please select an image to remove.", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate back to Admin panel
        adminGoBack.setOnClickListener(v -> getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AdminFragment())
                .commit());

        return view;
    }

    /**
     * Fetch images directly from "Users" and "Events" collections.
     */
    private void loadImagesFromCollections() {
        imageList.clear();


        // Fetch images from "Users" collection
        db.collection("Users").get()
                .addOnSuccessListener(userSnapshots -> {
                    for (DocumentSnapshot user : userSnapshots.getDocuments()) {
                        String imageUrl = user.getString("profilePictureUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            ImageClass image = new ImageClass(imageUrl,
                                    "User: " + user.getString("username")
                                    , user.getId()
                                    , "Users");
                            imageList.add(image);
                        }
                    }

                    // Fetch images from "Events" collection
                    db.collection("Events").get()
                            .addOnSuccessListener(eventSnapshots -> {
                                for (DocumentSnapshot event : eventSnapshots.getDocuments()) {
                                    String imageUrl = event.getString("imageUrl");
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        ImageClass image = new ImageClass(imageUrl,
                                                "Event: " + event.getString("eventTitle")
                                                , event.getId()
                                                , "Events");
                                        imageList.add(image);
                                    }
                                }

                                // Notify adapter after all data is loaded
                                imageAdapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ViewImagesFragment", "Error fetching Event images: " + e.getMessage());
                                Toast.makeText(getContext(), "Failed to load images from Events.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("ViewImagesFragment", "Error fetching User images: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to load images from Users.", Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Removes the selected image metadata from Firestore.
     *
     */
    private void removeImage() {
        String collection = selectedImage.getCollection();
        String documentId = selectedImage.getDocumentId();
        if (collection != null && documentId != null) {
            if (collection.equals("Users")) {
                // Fetch the defaultProfilePictureUrl and update profilePictureUrl
                db.collection("Users").document(documentId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String defaultProfilePictureUrl = documentSnapshot.getString("defaultProfilePictureUrl");
                            if (defaultProfilePictureUrl != null && !defaultProfilePictureUrl.isEmpty()) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("profilePictureUrl", defaultProfilePictureUrl); // Set to defaultProfilePictureUrl

                                db.collection("Users").document(documentId).update(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Profile picture reset to default.", Toast.LENGTH_SHORT).show();

                                            // Update the local list
                                            imageList.remove(selectedImage);
                                            imageAdapter.notifyDataSetChanged();
                                            selectedImageData = null;
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("ViewImagesFragment", "Error updating profilePictureUrl: " + e.getMessage());
                                            Toast.makeText(getContext(), "Failed to reset profile picture: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(getContext(), "Default profile picture URL not found.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ViewImagesFragment", "Error fetching defaultProfilePictureUrl: " + e.getMessage());
                            Toast.makeText(getContext(), "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                        });
            }
            else if (collection.equals("Events")) {
                // Fetch the defaultEventPosterURL and update imageUrl
                db.collection("Events").document(documentId).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String defaultEventPosterURL = documentSnapshot.getString("defaultEventPosterURL");
                            if (defaultEventPosterURL != null && !defaultEventPosterURL.isEmpty()) {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("imageUrl", defaultEventPosterURL); // Set to defaultEventPosterURL

                                db.collection("Events").document(documentId).update(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(getContext(), "Event poster reset to default.", Toast.LENGTH_SHORT).show();

                                            // Update the local list
                                            imageList.remove(selectedImage);
                                            imageAdapter.notifyDataSetChanged();
                                            selectedImageData = null;
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("ViewImagesFragment", "Error updating imageUrl: " + e.getMessage());
                                            Toast.makeText(getContext(), "Failed to reset event poster: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(getContext(), "Default event poster URL not found.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("ViewImagesFragment", "Error fetching defaultEventPosterURL: " + e.getMessage());
                            Toast.makeText(getContext(), "Failed to fetch event data.", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getContext(), "Invalid collection.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Invalid image data.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Loading test data, only for testing the UI
     */
    private void loadTestDataset(){
//        imageList = new ArrayList<>();
        imageList.addAll(UniversalProgramValues.getInstance().getImageList());
        imageAdapter.notifyDataSetChanged();

    }

    private void removeImageUITest(){
        UniversalProgramValues.getInstance().removeSpecificImage(selectedImage.getImageUrl());
        imageList.remove(selectedImage);
        imageAdapter.notifyDataSetChanged();
        selectedImageData = null;
    }



}