package com.example.eventbooking.Admin.Images;


import android.media.Image;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentTransaction;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
/**
 * ViewImagesFragment is a Fragment that displays a list of images fetched from Firebase.
 * Each image can be clicked to view more details in a separate fragment.
 */
public class ViewImagesFragment extends Fragment {
    private ListView imagesListView;
    private ImageAdapter imageAdapter;
    private List<ImageClass> imageList;
    private Button adminGoBack;
    /**
     * Inflates the fragment's layout and initializes the components.
     *
     * @param inflater           LayoutInflater used to inflate the layout
     * @param container          ViewGroup container in which the fragment is placed
     * @param savedInstanceState Bundle containing saved state data (if any)
     * @return the root View for the fragment's layout
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_images, container, false);

        // Initialize ListView
//        imagesListView = view.findViewById(R.id.imagesListView);
        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(getContext(), imageList);
        imagesListView.setAdapter(imageAdapter);
        adminGoBack = view.findViewById(R.id.admin_go_back);

        // Load images from Firebase
        loadImagesFromFirebase();

        // Set ListView item click listener
        imagesListView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            ImageClass selectedImage = imageList.get(position);
            openImageDetailsFragment(selectedImage);
        });

        adminGoBack.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .commit();
        });

        return view;
    }
    /**
     * Loads images from Firebase database and updates the ListView adapter.
     * On successful data retrieval, images are added to the list and displayed.
     * Displays a Toast message if data retrieval fails.
     */
    private void loadImagesFromFirebase() {
        FirebaseDatabase.getInstance().getReference("images")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        imageList.clear();
                        for (DataSnapshot imageSnapshot : snapshot.getChildren()) {
//                            ImageClass image = imageSnapshot.getValue(Image.class);
//                            imageList.add(image);
                        }
                        imageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Failed to load images.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    /**
     * Opens a fragment to display details for a selected image.
     *
     * @param image the ImageClass object representing the selected image
     */
    private void openImageDetailsFragment(ImageClass image) {
//        EditImageFragment fragment = EditImageFragment.newInstance(image);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//        transaction.replace(R.id.flFragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
