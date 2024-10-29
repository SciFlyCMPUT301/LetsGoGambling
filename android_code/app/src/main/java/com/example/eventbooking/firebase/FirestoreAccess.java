package com.example.eventbooking.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreAccess {
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    public FirestoreAccess() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
    }

    public Task<DocumentSnapshot> getUser(String userId) {
        return usersRef.document(userId).get();
    }
}
