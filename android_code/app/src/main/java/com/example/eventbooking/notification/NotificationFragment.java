package com.example.eventbooking.notification;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.Notification;
import com.example.eventbooking.R;
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.profile.ProfileFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that displays the user's notifications and provides buttons to navigate
 * to the ProfileFragment or HomeFragment.
 */
@SuppressWarnings("all")
public class NotificationFragment extends Fragment {
    private List<Notification> notifications;
    /**
     * Inflates the fragment layout and sets up the view components, including buttons for navigation
     * and a ListView to display notifications.
     *
     * @param inflater           The LayoutInflater object to inflate views.
     * @param container          The container that holds the fragment.
     * @param savedInstanceState The saved state of the fragment (not used here).
     * @return The root view of the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        Button profileButton = rootView.findViewById(R.id.button_back_profile);
        profileButton.setOnClickListener(v -> {
            // Replace the current fragment with ProfileFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ProfileFragment())
                    .commit();
        });
        // Set up ListView to display notifications
        ListView notificationListView = rootView.findViewById(R.id.notification_list);
        String currentUserId = UserManager.getInstance().getUserId();
        if(!UniversalProgramValues.getInstance().getTestingMode())
        {
            MyNotificationManager nm = new MyNotificationManager(FirebaseFirestore.getInstance());
            // get user's notifications and display them
            nm.getUserNotifications(currentUserId).addOnSuccessListener(queryDocumentSnapshots -> {
//                List<Notification> notifications = new ArrayList<>();
                notifications = new ArrayList<>();
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    notifications.add(doc.toObject(Notification.class));
                }
                // Set up the adapter for displaying the notifications
                NotificationArrayAdapter adapter = new NotificationArrayAdapter(getContext(), notifications);
                notificationListView.setAdapter(adapter);

                // Set up an on-click listener for each notification
                notificationListView.setOnItemClickListener((parent, view, position, id) -> {
                    Notification selectedNotification = notifications.get(position);
                    selectedNotification.setRead(true);
                    nm.updateNotification(selectedNotification);
// Navigate to EventViewFragment with the selected notification's event
                    Log.d("Notification Fragmnet", "Notification: " + selectedNotification);
                    Log.d("Notification Fragmnet", "Notification: " + selectedNotification.getEventId());
                    EventViewFragment eventViewFragment = EventViewFragment.newInstance(selectedNotification.getEventId(), currentUserId);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, eventViewFragment)
                            .addToBackStack(null) // Ensures returning to HomeFragment
                            .commit();
                });
            });
        }
        // Set up the Back button to navigate to HomeFragment
        Button backButton = rootView.findViewById(R.id.button_back_home);
        backButton.setOnClickListener(v -> {
           // Replace the current fragment with HomeFragment
           getParentFragmentManager().beginTransaction()
                   .replace(R.id.fragment_container, new HomeFragment())
                  .commit();
        });

        Button clearNotificationButton = rootView.findViewById(R.id.button_clear_notifications);
        clearNotificationButton.setOnClickListener(v -> {
            Notification notif = new Notification();
            notif.deleteNotificationsByUserId(UserManager.getInstance().getUserId())
                    .addOnSuccessListener(aVoid -> {
                        List<Notification> notifications = new ArrayList<>();
                        NotificationArrayAdapter adapter = new NotificationArrayAdapter(getContext(), notifications);
                        notificationListView.setAdapter(adapter);
                        Log.d("NotificationManager", "Successfully deleted notifications for userId.");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("NotificationManager", "Failed to delete notifications for userId.", e);
                    });

        });
        // Return the root view to be displayed
        return rootView;
    }
}
