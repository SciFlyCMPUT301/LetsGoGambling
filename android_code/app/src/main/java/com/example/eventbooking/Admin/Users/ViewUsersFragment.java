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
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewUsersFragment is a Fragment that displays a list of users fetched from Firestore.
 * This fragment allows the admin to view user details, edit or delete users, and navigate back to the AdminFragment.
 */
public class ViewUsersFragment extends Fragment {
    private FirebaseFirestore db; // Firestore instance for fetching user data.
    //    private ArrayList<String> documentIds = new ArrayList<>();
    private ListView usersListView;// ListView to display users in the fragment.
    private UserViewAdapter userAdapter;// Adapter to bind the user list to the ListView.
    private ArrayList<User> userList; // List of User objects to be displayed in the ListView.
    private Button adminGoBack; // Button for navigating back to the AdminFragment.

    /**
     * Inflates the fragment's layout, initializes the components, and sets up the user list.
     * The method also handles the navigation back to the AdminFragment and listens for item clicks
     * to open the details of a selected user.
     *
     * @param inflater           LayoutInflater used to inflate the layout.
     * @param container          ViewGroup container in which the fragment is placed.
     * @param savedInstanceState Bundle containing saved state data (if any).
     * @return the root View for the fragment's layout.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_users, container, false);

        // Initialize ListView
//        imagesListView = view.findViewById(R.id.imagesListView);
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            db = FirebaseFirestore.getInstance();
        }

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
     * Opens the EditUserFragment with the details of the selected user for viewing or editing.
     * The method passes the user's information such as device ID, username, email, and roles
     * to the EditUserFragment so that the admin can modify or view the user's details.
     *
     * @param selectedUser The User object representing the selected user to be viewed or edited.
     */
    private void openUserDetailsFragment(User selectedUser) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        EditUserFragment fragment = new EditUserFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("userData", selectedUser);
//
//        // Pass user details to EditUserFragment
//        bundle.putString("deviceId", selectedUser.getDeviceID());
//        bundle.putString("username", selectedUser.getUsername());
//        bundle.putString("email", selectedUser.getEmail());
//        bundle.putString("phoneNumber", selectedUser.getPhoneNumber());
//        bundle.putString("location", selectedUser.getLocation());
//        bundle.putString("profilePictureUrl", selectedUser.getProfilePictureUrl());
        if (selectedUser.getRoles() != null) {
            bundle.putStringArrayList("roles", new ArrayList<>(selectedUser.getRoles()));
        }

        fragment.setArguments(bundle);
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Handles error logging if the Firestore fetch operation fails.
     * Logs an error message using Log.e, which includes the exception message for debugging purposes.
     */
    private void loadUsersFromFirestore() {
        if (!UniversalProgramValues.getInstance().getTestingMode()) {
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
        else{
            for(int i = 0; i < UniversalProgramValues.getInstance().getUserList().size(); i++){
                User user = UniversalProgramValues.getInstance().getUserList().get(i);
                userList.add(user);
            }
            userList = UniversalProgramValues.getInstance().getUserList();
            Log.d("ViewUsersFragment", "DeviceID: " + userList.get(1).getDeviceID());
            Log.d("ViewUsersFragment", "DeviceID: " + userList.get(2).getUsername());
            userAdapter.notifyDataSetChanged();
            Log.d("ViewUsersFragment", "Users loaded: " + userList.size());
        }
    }








}