package com.example.eventbooking.notification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.Notification;
import com.example.eventbooking.R;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.profile.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that displays a notification with an integer value and provides buttons to navigate
 * to the ProfileFragment or HomeFragment.
 */
public class NotificationFragment extends Fragment {

    /**
     * Inflates the fragment layout and sets up the view components.
     *
     * @param inflater           The LayoutInflater object to inflate views.
     * @param container          The container that holds the fragment.
     * @param savedInstanceState The saved state of the fragment (not used here).
     * @return The root view of the fragment.
     */

    // Inflate the layout and display the integer
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

        ListView notificationListView = rootView.findViewById(R.id.notification_list);
        NotificationManager nm = new NotificationManager(FirebaseFirestore.getInstance());
        String currentUserId = UserManager.getInstance().getUserId();

        nm.getUserNotifications(currentUserId).addOnSuccessListener(queryDocumentSnapshots -> {
            List<Notification> notifications = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                notifications.add(doc.toObject(Notification.class));
            }

            NotificationArrayAdapter adapter = new NotificationArrayAdapter(getContext(), notifications);
            notificationListView.setAdapter(adapter);

            notificationListView.setOnItemClickListener((parent, view, position, id) -> {
                Notification selectedNotification = notifications.get(position);
                EventViewFragment eventViewFragment = EventViewFragment.newInstance(selectedNotification.getEventId(), currentUserId);

                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, eventViewFragment)
                        .addToBackStack(null) // Ensures returning to HomeFragment
                        .commit();
            });
        });



        // Set up the Back button to navigate to HomeFragment
        Button backButton = rootView.findViewById(R.id.button_back_home);
        backButton.setOnClickListener(v -> {
           // Replace the current fragment with HomeFragment
           getParentFragmentManager().beginTransaction()
                   .replace(R.id.fragment_container, new HomeFragment())
                  .commit();
        });
        // Return the root view to be displayed
        return rootView;
    }
}
