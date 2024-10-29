package com.example.eventbooking.firebase;

import com.google.firebase.firestore.DocumentSnapshot;

public interface DocumentCallback {
    void onCallback(DocumentSnapshot snapshot);
}
