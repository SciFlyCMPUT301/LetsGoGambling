package com.example.eventbooking.notification;

import com.example.eventbooking.Notification;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class NotificationManager {
    private FirebaseFirestore fb;

    public NotificationManager(FirebaseFirestore fb) {
        this.fb = fb;
    }

    public Task<QuerySnapshot> getUserNotifications(String userId) {
        Query query = fb.collection("Notifications").whereEqualTo("userId", userId);
        return query.get();
    }

    public Task<Void> createNotification(Notification notification) {
        return fb.collection("Notifications").document().set(notification);
    }
}
