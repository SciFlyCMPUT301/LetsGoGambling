package com.example.eventbooking.Events.EventPageFragment;
import com.example.eventbooking.Admin.Facility.FacilityViewAdapter;
import com.example.eventbooking.Events.EventData.Event;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Facility;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.Home.HomeUserEventAdapter;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.Events.EventView.OrganizerEventDetailFragment;
import com.example.eventbooking.firebase.FirestoreAccess;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;
import com.example.eventbooking.waitinglist.WaitingList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
public class OragnizerEventFragment  extends Fragment{
    private int someInteger = 42; // Example integer to pass
    private String userId;
    //private Button addEvent;

    /**
     * Creates a new instance of HomeFragment with the provided user ID.
     * @param userId
     * @return
     */
//    public static HomeFragment newInstance(String userId) {
//        HomeFragment fragment = new HomeFragment();
//        Bundle args = new Bundle();
//        args.putString("userId", userId);
//        fragment.setArguments(args);
//        return fragment;
//    }
    public static OragnizerEventFragment newInstance(String userId) {
        OragnizerEventFragment fragment = new OragnizerEventFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }



    /**
     * Inflates the fragment layout, initializes UI components, and sets up button click listeners for navigation.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("HomeFragment", "Organizer Event Fragment Launch");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ListView usersEventListView = rootView.findViewById(R.id.user_events_list);
        String currentUserId = UserManager.getInstance().getUserId();

        // Fetch events for the organizer
        Event.getOragnizerEvents(currentUserId, organizerEvents -> {
            HomeUserEventAdapter adapter = new HomeUserEventAdapter(getContext(), organizerEvents, currentUserId);
            usersEventListView.setAdapter(adapter);

            // Set item click listener to navigate to EventViewFragment
            usersEventListView.setOnItemClickListener((parent, view, position, id) -> {
                Event selectedEvent = organizerEvents.get(position);
               // EventViewFragment eventViewFragment = EventViewFragment.newInstance(selectedEvent.getEventId(), currentUserId);
                OrganizerEventDetailFragment eventDetailView = OrganizerEventDetailFragment.newInstance(selectedEvent.getEventId(), currentUserId);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, eventDetailView)
                        .addToBackStack(null) // Ensures returning to OragnizerEventFragment
                        .commit();
            });
        }, e -> {
            Log.e("OragnizerEventFragment", "Failed to fetch events: " + e.getMessage());
        });
        // Button to create a new event
        Button addEventButton = rootView.findViewById(R.id.btn_add_event);
        addEventButton.setVisibility(View.VISIBLE);
        addEventButton.setOnClickListener(v -> {
            EventCreateFragment eventCreateFragment = new EventCreateFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, eventCreateFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return rootView;
    }




}
