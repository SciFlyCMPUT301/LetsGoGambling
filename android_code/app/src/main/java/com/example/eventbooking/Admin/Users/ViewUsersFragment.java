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
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

/**
 * ViewUsersFragment is a Fragment that displays a list of users from Firestore.
 * This fragment allows the admin to add new users, view user details, or navigate back to the AdminFragment.
 */
public class ViewUsersFragment extends Fragment {
    private FirebaseFirestore db;
//    private ArrayList<String> documentIds = new ArrayList<>();
    private ListView usersListView;
    private UserViewAdapter userAdapter;
    private ArrayList<User> userList;
    private Button adminGoBack;

    /**
     * Inflates the fragment's layout and initializes components.
     *
     * @param inflater           LayoutInflater used to inflate the layout
     * @param container          ViewGroup container in which the fragment is placed
     * @param savedInstanceState Bundle containing saved state data (if any)
     * @return the root View for the fragment's layout
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_users, container, false);

        // Initialize ListView
//        imagesListView = view.findViewById(R.id.imagesListView);
        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        // Set up ListView and adapter to display users
        usersListView = view.findViewById(R.id.user_list);
        userAdapter = new UserViewAdapter(getContext(), userList);
        usersListView.setAdapter(userAdapter);
        // Initialize buttons for adding users and navigating back
        adminGoBack = view.findViewById(R.id.admin_go_back);
        // Load images from Firebase
//        loadImagesFromFirebase();


        // Load Users
        loadUsersFromFirestore();

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

    /**
     * Opens the EditUserFragment with details of the selected user for viewing or editing.
     *
     * @param selectedUser the User object representing the selected user
     */
    private void openUserDetailsFragment(User selectedUser) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        EditUserFragment fragment = new EditUserFragment();

        Bundle bundle = new Bundle();
        bundle.putString("deviceId", selectedUser.getDeviceID());
        bundle.putString("username", selectedUser.getUsername());
        bundle.putString("email", selectedUser.getEmail());
        bundle.putString("phoneNumber", selectedUser.getPhoneNumber());
        bundle.putString("location", selectedUser.getAddress());
        bundle.putString("profilePictureUrl", selectedUser.getProfilePictureUrl());
        fragment.setArguments(bundle);
        // Replace current fragment with EditUserFragment and add to back stack
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    /**
     * Loads users from Firestore and updates the ListView adapter.
     * Logs the device ID of each loaded user and shows an error message on failure.
     */
    private void loadUsersFromFirestore() {
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Iterate through the documents and add each user to the list
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    userList.add(user);
                    Log.d("ViewUsersFragment", "User deviceId: " + user.getDeviceID());
                }
                // Notify adapter to refresh ListView
                userAdapter.notifyDataSetChanged();
                Log.d("ViewUsersFragment", "Users loaded: " + userList.size());
            } else {
                Log.e("FirestoreError", "Error getting documents: ", task.getException());
            }
        });
    }




}
