package com.example.eventbooking.firebase;

import android.net.Uri;
import android.util.Log;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
/**
 * A helper class to handle Firebase operations such as adding, getting user data,
 * adding, getting event data, and uploading images to Firebase Storage.
 */
public class FirebaseAccess {
    private int deviceID;
    private User user;
    private Event event;
    /**
     * Adds a user to the Firebase Realtime Database under the "users" node.
     *
     * @param user The User object to be added to the database.
     */
    public void addUserToDatabase(User user) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(String.valueOf(user.getDeviceID())).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("RealtimeDB", "User added with ID: " + user.getDeviceID());
                })
                .addOnFailureListener(e -> {
                    Log.w("RealtimeDB", "Error adding user", e);
                });

    }
    /**
     * Retrieves a user from the Firebase Realtime Database by their UID.
     *
     * @param uid The UID of the user to be retrieved.
     */
    public void getUserFromDatabase(String uid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    Log.d("RealtimeDB", "User: " + user.getUsername());
                } else {
                    Log.d("RealtimeDB", "No such user");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("RealtimeDB", "Error getting user", databaseError.toException());
            }
        });
    }
    /**
     * Adds an event to the Firebase Realtime Database under the "events" node.
     *
     * @param event The Event object to be added to the database.
     */
    public void addEventToDatabase(Event event) {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference("events");
        eventsRef.child(event.getEventId()).setValue(event)
                .addOnSuccessListener(aVoid -> {
                    Log.d("RealtimeDB", "Event added with ID: " + event.getEventId());
                })
                .addOnFailureListener(e -> {
                    Log.w("RealtimeDB", "Error adding event", e);
                });
    }

    /**
     * Retrieves an event from the Firebase Realtime Database by its event ID.
     *
     * @param eventId The ID of the event to be retrieved.
     */
    public void getEventFromDatabase(String eventId) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("events").child(eventId);
        eventRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Event event = dataSnapshot.getValue(Event.class);
                    Log.d("RealtimeDB", "Event: " + event.getEventTitle());
                } else {
                    Log.d("RealtimeDB", "No such event");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("RealtimeDB", "Error getting event", databaseError.toException());
            }
        });
    }
    /**
     * Uploads an image to Firebase Storage and retrieves its download URL upon success.
     *
     * @param imageUri The URI of the image to be uploaded.
     * @param storagePath The path in Firebase Storage where the image will be uploaded.
     * @param onSuccessListener The listener to be invoked upon a successful upload.
     * @param onFailureListener The listener to be invoked if the upload fails.
     */
    public void uploadImageToStorage(Uri imageUri, String storagePath, OnSuccessListener<Uri> onSuccessListener, OnFailureListener onFailureListener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(storagePath);

        UploadTask uploadTask = storageRef.putFile(imageUri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException();
            }
            return storageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                onSuccessListener.onSuccess(downloadUri);
            } else {
                onFailureListener.onFailure(task.getException());
                Log.w("FirebaseStorage", "Upload failed", task.getException());
            }
        });
    }





}
