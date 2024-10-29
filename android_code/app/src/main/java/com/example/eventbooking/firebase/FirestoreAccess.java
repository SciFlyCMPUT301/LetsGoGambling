package com.example.eventbooking.firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreAccess {
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    public FirestoreAccess() {
        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
    }

    public void getUser(String userId, DocumentCallback callback) {
        usersRef.document(userId).get().addOnSuccessListener(callback::onCallback);
    }
}
