package com.example.eventbooking.Admin.Users;

import android.content.Intent;
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

import com.example.eventbooking.Admin.AdminActivity;
import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Admin.Event.EditEventFragment;
import com.example.eventbooking.Admin.Images.EditImageFragment;
import com.example.eventbooking.Admin.Images.ImageAdapter;
import com.example.eventbooking.Admin.Images.ImageClass;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Home.HomeActivity;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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
    private Button addUser;
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
        Log.d("View Users Fragment", "Fragment Launched");
        // Initialize ListView
//        imagesListView = view.findViewById(R.id.imagesListView);
        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        // Set up ListView and adapter to display users
        usersListView = view.findViewById(R.id.user_list);
        userAdapter = new UserViewAdapter(getContext(), userList);
        usersListView.setAdapter(userAdapter);
        // Initialize buttons for adding users and navigating back
        addUser = view.findViewById(R.id.add_user_button);
        adminGoBack = view.findViewById(R.id.admin_go_back);

        // Load Users
        loadUsersFromFirestore();
        Log.d("View Users Fragment", "After Firestore Call");


        addUser.setOnClickListener(v -> {
            // Open EditUserFragment with empty fields for a new user
            openUserDetailPage(null);
        });
        adminGoBack.setOnClickListener(v -> {
            backToAdmin();
        });

        // Set ListView item click listener
        usersListView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            User selectedUser = userList.get(position);
            openUserDetailPage(selectedUser);
        });


        return view;
    }


    /**
     * Opens the detail view for the selected user, allowing further edits.
     *
     * @param selectedUser The event selected by the user, represented as an {@link Event} object.
     */
    private void openUserDetailPage(User selectedUser) {
        // Create and navigate to the Event Detail Fragment
        Log.d("View Users Fragment", "Open User Edit Page");
        EditUserFragment fragment = new EditUserFragment(selectedUser);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.flFragmentAdmin, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Loads users from Firestore and updates the ListView adapter.
     * Logs the device ID of each loaded user and shows an error message on failure.
     */
    private void loadUsersFromFirestore() {
        Log.d("View Users Fragment", "Loading Users From Firestore");
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Iterate through the documents and add each user to the list
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User user = document.toObject(User.class);
                    userList.add(user);
                    Log.d("View Users Fragment", "User deviceId: " + user.getDeviceID());
                }
                // Notify adapter to refresh ListView
                userAdapter.notifyDataSetChanged();
                Log.d("View Users Fragment", "Users loaded: " + userList.size());
            } else {
                Log.e("FirestoreError", "Error getting documents: ", task.getException());
            }
        });
        Log.d("ViewUsersFragment", "Users loaded: " + userList.size());
    }

    private void backToAdmin(){
        Intent intent = new Intent(getActivity(), AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }




}
