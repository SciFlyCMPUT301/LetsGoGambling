package com.example.eventbooking.firebase;

import android.util.Log;

import com.example.eventbooking.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * A helper class to manage Firebase Firestore operations, including retrieving
 * user data, event data, and facilities data. It also supports querying users
 * by username and checking if an event exists by its ID and event hash.
 */
public class FirestoreAccess {
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private CollectionReference eventsRef;
    private static FirestoreAccess instance;
    private CollectionReference facilitiesRef;

    /**
     * Initializes Firestore instance and references for Users, Events, and Facilities collections.
     */
    public FirestoreAccess() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("Users");
        eventsRef = db.collection("Events");
        facilitiesRef = db.collection("Facilities");
        Log.d("Firestore Access", "Instantiating Firestore");
    }
    /**
     * Returns the singleton instance of FirestoreAccess.
     *
     * @return The singleton instance of FirestoreAccess.
     */
    public static FirestoreAccess getInstance() {
        if (instance == null) {
            instance = new FirestoreAccess();
        }
        return instance;
    }
    /**
     * Sets a custom instance of FirestoreAccess. This method is useful for dependency injection.
     *
     * @param firestoreAccess The FirestoreAccess instance to set.
     */
    public static void setInstance(FirestoreAccess firestoreAccess) {
        instance = firestoreAccess;
    }
    /**
     * Retrieves a user from the Firestore database by their user ID.
     *
     * @param userId The ID of the user to be retrieved.
     * @return A Task<DocumentSnapshot> containing the user document.
     */
    public Task<DocumentSnapshot> getUser(String userId) {
        Log.d("Firestore Access", "Getting User " + userId);
//        return usersRef.document(userId).get();
        DocumentReference userDoc = usersRef.document(userId);
        return userDoc.get();
    }
    /**
     * Retrieves all events associated with a specific user by their user ID.
     *
     * @param userId The ID of the user whose events are to be retrieved.
     * @return A Task<QuerySnapshot> containing the query results (events).
     */
    public Task<QuerySnapshot> getUserEvents(String userId) {
        return eventsRef.whereEqualTo("userId", userId).get();
    }
    /**
     * Retrieves all facilities associated with a specific user by their user ID.
     *
     * @param userId The ID of the user whose facilities are to be retrieved.
     * @return A Task<QuerySnapshot> containing the query results (facilities).
     */
    public Task<QuerySnapshot> getUserFacility(String userId) {
        return facilitiesRef.whereEqualTo("organizer", userId).get();  // Query facility data based on userId
    }
    /**
     * Retrieves all events where the user is the organizer, using the organizer's ID.
     *
     * @param userId The ID of the user whose organized events are to be retrieved.
     * @return A Task<QuerySnapshot> containing the query results (events).
     */
    public Task<QuerySnapshot> getOrganizerEvents(String userId) {
        return eventsRef.whereEqualTo("organizerId", userId).get();  // Assuming "organizerId" is the field linking events to the organizer
    }

    /**
     * Retrieves all users that have the given username from the Firestore database.
     *
     * @param username The username to search for in the Users collection.
     * @return A Task<QuerySnapshot> containing the query results (users).
     */
    public Task<QuerySnapshot> getUsersByUsername(String username) {
        Log.d("Firestore Access", "Querying Users by username: " + username);
        return usersRef.whereEqualTo("username", username).get();
    }
    /**
     * Adds a new user to the Firestore database.
     *
     * @param user The User object to be added to the database.
     * @return A Task<Void> indicating the success or failure of the operation.
     */
    public Task<Void> addUser(User user) {
        return usersRef.document(user.getDeviceID()).set(user);
    }

    /**
     * Checks if an event exists in the Firestore database based on its event ID and event hash.
     *
     * @param eventId The ID of the event to search for.
     * @param eventHash The event hash to check for.
     * @return A Task containing a boolean result: true if the event exists, false otherwise.
     */
    public Task<Boolean> checkEventExists(String eventId, String eventHash) {
        return eventsRef.whereEqualTo(FieldPath.documentId(), eventId)  // Match by documentId
                .whereEqualTo("qrcodehash", eventHash)  // Match by eventHash
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // If query returns non-empty result, event exists
                        return true;
                    }
                    // If no matching event is found
                    return false;
                });
    }

}
