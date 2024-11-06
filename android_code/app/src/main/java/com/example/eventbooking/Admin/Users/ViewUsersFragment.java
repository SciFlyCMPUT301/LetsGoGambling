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
//    private ArrayList<String> documentIds = new ArrayList<>();
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


        // Load Users
        loadUsersFromFirestore();


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
            User selectedUser = userList.get(position);
            openUserDetailsFragment(selectedUser);
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

    private void openUserDetailsFragment(User selectedUser) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        EditUserFragment fragment = new EditUserFragment();

        Bundle bundle = new Bundle();
        bundle.putString("deviceId", selectedUser.getDeviceID());
        Log.d("Loading User", "Document ID: "+ selectedUser.getDeviceID());
        bundle.putString("username", selectedUser.getUsername());
        bundle.putString("email", selectedUser.getEmail());
        bundle.putString("phoneNumber", selectedUser.getPhoneNumber());
        bundle.putString("location", selectedUser.getAddress());
        bundle.putString("profilePictureUrl", selectedUser.getProfilePictureUrl());
        bundle.putBoolean("isNewUser", false);
        if(selectedUser.hasRole("admin"))
            bundle.putBoolean("admin", true);
        else
            bundle.putBoolean("admin", false);
        if(selectedUser.hasRole("entrant"))
            bundle.putBoolean("entrant", true);
        else
            bundle.putBoolean("entrant", false);
        if(selectedUser.hasRole("organizer"))
            bundle.putBoolean("organizer", true);
        else
            bundle.putBoolean("organizer", false);
        fragment.setArguments(bundle);

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void loadUsersFromFirestore() {
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    userList.add(user);
                    Log.d("ViewUsersFragment", "User deviceId: " + user.getDeviceID());
                }
                userAdapter.notifyDataSetChanged();
                Log.d("ViewUsersFragment", "Users loaded: " + userList.size());
            } else {
                Log.e("FirestoreError", "Error getting documents: ", task.getException());
            }
        });
    }




}
