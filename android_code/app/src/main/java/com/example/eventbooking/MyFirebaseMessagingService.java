package com.example.eventbooking;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Called when a new FCM message is received.
     *
     * @param message The received FCM message.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        // Log the sender of the message
        Log.d("FCM", "Message received from: " + message.getFrom());

        // Log the notification body if present
        if (message.getNotification() != null) {
            Log.d("FCM", "Notification Message Body: " + message.getNotification().getBody());
        }
    }

    /**
     * Called when a new FCM registration token is generated for the device.
     *
     * @param token The new FCM token.
     */
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        // Log the new token
        Log.d("FCM", "New token: " + token);

        // Optionally, you can send this token to your server for managing device-specific messaging
    }
}











