package com.example.eventbooking.notification;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.eventbooking.Notification;
import com.example.eventbooking.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyNotificationManager {
    private FirebaseFirestore fb;

    public MyNotificationManager(FirebaseFirestore fb) {
        this.fb = fb;
    }

    public Task<QuerySnapshot> getUserNotifications(String userId) {
        Query query = fb.collection("Notifications").whereEqualTo("userId", userId);
        return query.get();
    }

    public Task<Void> createNotification(Notification notification) {
        DocumentReference ref = fb.collection("Notifications").document();
        notification.setNotificationId(ref.getId());
        return fb.collection("Notifications").document().set(notification);
    }

    public Task<Void> updateNotification(Notification notification) {
        return fb.collection("Notifications").document(notification.getNotificationId())
                .set(notification);
    }

    public void notifyUserUnread(String userId, Context context) {
        String channelId = "my_channel_id";

        Query query = fb.collection("Notifications").whereEqualTo("userId", userId)
                .whereEqualTo("read", false);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Notification> notifications = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                notifications.add(doc.toObject(Notification.class));
            }

            for (Notification notif : notifications) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_notification_foreground)
                        .setContentTitle(notif.getTitle())
                        .setContentText(notif.getText())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                Log.d("NotificationManager", "notification title: "+notif.getTitle());
                notificationManager.notify(1, builder.build()); // `1` is the notification ID
            }
        });
    }
}
