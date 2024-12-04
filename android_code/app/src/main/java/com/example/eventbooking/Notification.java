package com.example.eventbooking;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class for holding information about a notification that
 * should be shown to a user. Has references to the user it should be
 * shown to and the event the notification is related to.
 */
public class Notification {
    private String notificationId;
    private String userId;
    private String eventId;

    private String text;
    private String title;
    private boolean read;

    public Notification() {}

    public Notification(String eventId, String text, String title, String userId) {
        this.eventId = eventId;
        this.text = text;
        this.title = title;
        this.userId = userId;
        this.read = false;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    /**
     * Deletes documents in the "Notifications" collection where the userId field matches the specified string.
     *
     * @param userId The userId to match.
     * @return A Task<Void> that completes when all matching documents are deleted.
     */
    public Task<Void> deleteNotificationsByUserId(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        return db.collection("Notifications")
                .whereEqualTo("userId", userId)
                .get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        List<Task<Void>> deleteTasks = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            deleteTasks.add(document.getReference().delete());
                        }
                        // Combine all delete tasks into one
                        return Tasks.whenAll(deleteTasks);
                    } else {
                        throw task.getException();
                    }
                });
    }

}
