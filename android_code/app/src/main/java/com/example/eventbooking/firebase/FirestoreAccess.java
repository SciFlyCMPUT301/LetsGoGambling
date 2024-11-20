package com.example.eventbooking.firebase;

import android.util.Log;

import com.example.eventbooking.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirestoreAccess {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;
    private static FirestoreAccess instance;
    private CollectionReference facilitiesRef;

    public FirestoreAccess() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("Users");
        eventsRef = db.collection("Events");
        facilitiesRef = db.collection("Facilities");
        Log.d("Firestore Access", "Instantiating Firestore");
    }

    public static FirestoreAccess getInstance() {
        if (instance == null) {
            instance = new FirestoreAccess();
        }
        return instance;
    }

    public Task<DocumentSnapshot> getUser(String userId) {
        Log.d("Firestore Access", "Getting User " + userId);
//        return usersRef.document(userId).get();
        DocumentReference userDoc = usersRef.document(userId);
        return userDoc.get();
    }

    public Task<QuerySnapshot> getUserEvents(String userId) {
        return eventsRef.whereEqualTo("userId", userId).get();
    }

    public Task<QuerySnapshot> getUserFacility(String userId) {
        return facilitiesRef.whereEqualTo("userId", userId).get();  // Query facility data based on userId
    }

    public Task<QuerySnapshot> getOrganizerEvents(String userId) {
        return eventsRef.whereEqualTo("organizerId", userId).get();  // Assuming "organizerId" is the field linking events to the organizer
    }

    /**
     * Retrieves all users with the given username.
     *
     * @param username The username to search for in the Users collection.
     * @return A Task<QuerySnapshot> containing the query results.
     */
    public Task<QuerySnapshot> getUsersByUsername(String username) {
        Log.d("Firestore Access", "Querying Users by username: " + username);
        return usersRef.whereEqualTo("username", username).get();
    }

    public Task<Void> addUser(User user) {
//        Map<String, Object> userdata = new HashMap<>();
//        userdata.put("username", user.getUsername());
//        userdata.put("email", user.getEmail());
//        userdata.put("phoneNumber", user.getPhoneNumber());
//        userdata.put("profilePictureUrl", user.getProfilePictureUrl());
//        userdata.put("roles", new ArrayList<>(user.getRoles())); // Serializing Collections is not supported, please use Lists instead (found in field 'roles')

//        return usersRef.document(user.getDeviceID()).set(userdata);
        return usersRef.document(user.getDeviceID()).set(user);
    }
}
