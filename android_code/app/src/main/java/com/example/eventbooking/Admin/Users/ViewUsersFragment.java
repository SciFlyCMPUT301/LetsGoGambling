package com.example.eventbooking.Admin.Users;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Admin.Images.EditImageFragment;
import com.example.eventbooking.Admin.Images.ImageAdapter;
import com.example.eventbooking.Admin.Images.ImageClass;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewUsersFragment extends Fragment {
    private FirebaseFirestore db;
    private ArrayList<String> documentIds = new ArrayList<>();
    private ListView usersListView;
    private UserViewAdapter userAdapter;
    private ArrayList<User> userList;
    private Button addUser;
    private Button adminGoBack;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_users, container, false);

        // Initialize ListView
//        imagesListView = view.findViewById(R.id.imagesListView);
        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        usersListView = view.findViewById(R.id.user_list);
        userAdapter = new UserViewAdapter(getContext(), userList);
        usersListView.setAdapter(userAdapter);
        addUser = view.findViewById(R.id.add_user_button);
        adminGoBack = view.findViewById(R.id.admin_go_back);
        // Load images from Firebase
//        loadImagesFromFirebase();
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Add document data to the list
                    documentIds.add(document.getId());  // Store document IDs
                }
                userAdapter.notifyDataSetChanged();
            } else {
                Log.d("FirestoreError", "Error getting documents: ", task.getException());
            }
        });
        addUser.setOnClickListener(v -> {
            // Open EditUserFragment with empty fields for a new user
            openNewUserFragment();
        });
        adminGoBack.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .commit();
        });


        // Set ListView item click listener
        usersListView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            String documentId = documentIds.get(position);
            openUserDetailsFragment(documentId);
        });





        return view;
    }

    private void openNewUserFragment() {
        EditUserFragment fragment = new EditUserFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("isNewUser", true); // Pass flag for new user
        fragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openUserDetailsFragment(String documentId) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        EditUserFragment fragment = new EditUserFragment();
        Bundle bundle = new Bundle();
        bundle.putString("documentId", documentId);
        bundle.putBoolean("isNewUser", false);
        fragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, fragment); // The container to replace
        transaction.addToBackStack(null); // Optional: Adds the transaction to the back stack
        transaction.commit();
    }




}
