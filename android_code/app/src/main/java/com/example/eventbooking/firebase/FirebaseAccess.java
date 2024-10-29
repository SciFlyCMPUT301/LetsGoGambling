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

public class FirebaseAccess {
    private int deviceID;
    private User user;
    private Event event;
//    private FirebaseDatabase database = FirebaseDatabase.getInstance();
//    private DatabaseReference databaseReference = database.getReference();
//    Firebase Storage link
//    gs://letsgogambling-9ebb8.appspot.com

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




//    Uri imageUri = /* get image URI */;
//    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//    String storagePath = "users/" + userId + "/profile.jpg";
//
//    uploadImageToStorage(imageUri, storagePath, downloadUri -> {
//        String imageUrl = downloadUri.toString();
//        // Update user profile with imageUrl
//        User user = new User(userId, name, email, imageUrl);
//        addUserToFirestore(user);
//    });


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
