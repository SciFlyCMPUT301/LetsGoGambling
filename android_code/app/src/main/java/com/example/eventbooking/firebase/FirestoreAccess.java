package com.example.eventbooking.firebase;

import com.example.eventbooking.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirestoreAccess {
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    public FirestoreAccess() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("Users");
    }

    public Task<DocumentSnapshot> getUser(String userId) {
        return usersRef.document(userId).get();
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
