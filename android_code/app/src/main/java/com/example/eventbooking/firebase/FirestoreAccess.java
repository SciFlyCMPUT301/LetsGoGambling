package com.example.eventbooking.firebase;

import com.example.eventbooking.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class for firebase firestore access.
 * Use is optional.
 */
public class FirestoreAccess {
    private static FirestoreAccess instance;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference facilitiesRef;
    private CollectionReference eventsRef;

    public FirestoreAccess() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("Users");
        facilitiesRef = db.collection("Facilities");
        eventsRef = db.collection("Events");
    }

    public static synchronized FirestoreAccess getInstance() {
        if (instance == null) {
            instance = new FirestoreAccess();
        }

        return instance;
    }

    /**
     * Attempts to get user from Users collection
     * @param userId userId (deviceId) that is the id of the document
     * @return Task that you can attach listeners to
     */
    public Task<DocumentSnapshot> getUser(String userId) {
        return usersRef.document(userId).get();
    }

    /**
     * Attempts to add user to Users collection
     * @param user User object that will be put in firebase
     * @return Task that you can attach listeners to
     */
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

    public Task<QuerySnapshot> getUserFacility(String userId) {
        Query query = facilitiesRef.whereEqualTo("organizer", userId);
        return query.get();
    }

    public Task<QuerySnapshot> getOrganizerEvents(String userId) {
        Query query = eventsRef.whereEqualTo("organizerId", userId);
        return query.get();
    }

    public Task<QuerySnapshot> getUserEvents(String userId) {
        Query query = eventsRef.where(Filter.or(
                Filter.arrayContains("acceptedParticipantIds", userId),
                Filter.arrayContains("canceledParticipantIds", userId),
                Filter.arrayContains("signedUpParticipantIds", userId),
                Filter.arrayContains("waitingparticipantIds", userId)
        ));
        return query.get();
    }
}


