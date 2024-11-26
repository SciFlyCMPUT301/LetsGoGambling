package com.example.eventbooking.Admin.Images;

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
import com.example.eventbooking.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewImagesFragment extends Fragment {
    private ListView imagesListView;
    private ImageAdapter imageAdapter;
    private ArrayList<Map<String, String>> imageList; // Stores image data
    private Button adminGoBack, removeButton;
    private FirebaseFirestore db;
    private Map<String, String> selectedImageData = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_images, container, false);

        // Initialize Firestore and UI components
        db = FirebaseFirestore.getInstance();
        imageList = new ArrayList<>();
        imagesListView = view.findViewById(R.id.image_list);
        imageAdapter = new ImageAdapter(getContext(), imageList);
        imagesListView.setAdapter(imageAdapter);
        adminGoBack = view.findViewById(R.id.admin_go_back);
        removeButton = view.findViewById(R.id.remove_image_button);

        // Load images from Firestore collections
        loadImagesFromCollections();

        // Handle ListView item clicks
        imagesListView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            selectedImageData = (Map<String, String>) parent.getItemAtPosition(position);
            Toast.makeText(getContext(), "Selected Image", Toast.LENGTH_SHORT).show();
        });

        // Handle Remove Button click
        removeButton.setOnClickListener(v -> {
            if (selectedImageData != null) {
                removeImage(selectedImageData);
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
                            Map<String, String> data = new HashMap<>();
                            data.put("imageUrl", imageUrl);
                            data.put("source", "User: " + user.getString("deviceId"));
                            data.put("documentId", user.getId());
                            data.put("collection", "Users");
                            imageList.add(data);
                        }
                    }

                    // Fetch images from "Events" collection
                    db.collection("Events").get()
                            .addOnSuccessListener(eventSnapshots -> {
                                for (DocumentSnapshot event : eventSnapshots.getDocuments()) {
                                    String imageUrl = event.getString("imageUrl");
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        Map<String, String> data = new HashMap<>();
                                        data.put("imageUrl", imageUrl);
                                        data.put("source", "Event: " + event.getString("eventTitle"));
                                        data.put("documentId", event.getId());
                                        data.put("collection", "Events");
                                        imageList.add(data);
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
     * @param imageData Metadata of the selected image.
     */
    private void removeImage(Map<String, String> imageData) {
        String collection = imageData.get("collection");
        String documentId = imageData.get("documentId");

        if (collection != null && documentId != null) {
            db.collection(collection).document(documentId).delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Image removed successfully.", Toast.LENGTH_SHORT).show();
                        imageList.remove(imageData);
                        imageAdapter.notifyDataSetChanged();
                        selectedImageData = null;
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ViewImagesFragment", "Error removing image: " + e.getMessage());
                        Toast.makeText(getContext(), "Failed to remove image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Invalid image data.", Toast.LENGTH_SHORT).show();
        }
    }
}
