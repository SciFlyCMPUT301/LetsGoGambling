package com.example.eventbooking.notification;

import android.Manifest;
import android.app.NotificationManager;
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
/**
 * Manages notifications for users in the EventBooking app.
 * Handles fetching, creating, updating, and sending notifications to users.
 */
public class MyNotificationManager {
    private FirebaseFirestore fb;
    /**
     * Constructs a MyNotificationManager instance to manage notifications.
     *
     * @param fb The FirebaseFirestore instance used for accessing Firestore data.
     */

    public MyNotificationManager(FirebaseFirestore fb) {
        this.fb = fb;
    }
    /**
     * Retrieves all unread notifications for a specified user from Firestore.
     *
     * @param userId The ID of the user for whom to fetch notifications.
     * @return A Task representing the Firestore query to retrieve notifications.
     */
    public Task<QuerySnapshot> getUserNotifications(String userId) {
        Query query = fb.collection("Notifications").whereEqualTo("userId", userId);
        return query.get();
    }
    /**
     * Creates a new notification in Firestore.
     *
     * @param notification The Notification object to be added to Firestore.
     * @return A Task representing the Firestore operation to create a notification.
     */
    public Task<Void> createNotification(Notification notification) {
        DocumentReference ref = fb.collection("Notifications").document();
        notification.setNotificationId(ref.getId());
        return fb.collection("Notifications").document().set(notification);
    }
    /**
     * Updates an existing notification in Firestore.
     *
     * @param notification The Notification object with updated details.
     * @return A Task representing the Firestore operation to update the notification.
     */
    public Task<Void> updateNotification(Notification notification) {
        return fb.collection("Notifications").document(notification.getNotificationId())
                .set(notification);
    }
    /**
     * Notifies the user of any unread notifications, displaying them as system notifications.
     *
     * @param userId The ID of the user to notify.
     * @param context The context in which the notification is created, typically an activity or service.
     */
    public void notifyUserUnread(String userId, Context context) {
        String channelId = "my_channel_id";
// Fetch unread notifications for the user
        Query query = fb.collection("Notifications").whereEqualTo("userId", userId)
                .whereEqualTo("read", false);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Notification> notifications = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                notifications.add(doc.toObject(Notification.class));
            }
            // Build and send a notification for each unread notification
            for (Notification notif : notifications) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_notification_foreground)
                        .setContentTitle(notif.getTitle())
                        .setContentText(notif.getText())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                Log.d("NotificationManager", "notification title: "+notif.getTitle());
                // Check for permission to post notifications
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                    notificationManager.notify(notif.getNotificationId().hashCode(), builder.build());
                }
            }
        });
    }

}
