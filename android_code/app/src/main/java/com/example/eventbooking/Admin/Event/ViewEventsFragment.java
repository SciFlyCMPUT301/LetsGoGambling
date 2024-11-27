package com.example.eventbooking.Admin.Event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
/**
 * The ViewEventsFragment class displays a list of events for administrators to manage.
 * It fetches event data from Firebase Firestore and allows navigation to detailed event
 * views for further modifications.
 *
 * <p>This fragment includes buttons for adding new facilities and navigating back to
 * the admin home. Events are displayed in a ListView and loaded asynchronously from the
 * Firestore database.</p>
 */
public class ViewEventsFragment extends Fragment {
    private Button adminGoBack;
    private EventViewAdapter eventAdapter;
    private ArrayList<Event> eventList;
    private ListView eventListView;
    private FirebaseFirestore db;


    /**
     *Inflates the layout for this fragment and initializes views, adapters,
     *and event listeners.
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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_events, container, false);
        // Initialize view components
        // Starting from here views
//        backButton = view.findViewById(R.id.button_back_home);
        eventList = new ArrayList<>();
        eventListView = view.findViewById(R.id.event_list);
        adminGoBack = view.findViewById(R.id.admin_go_back);

        db = FirebaseFirestore.getInstance();

        // Set up adapter for ListView
        eventAdapter = new EventViewAdapter(getContext(), eventList);
        eventListView.setAdapter(eventAdapter);


        loadEventsFromFirebase();
        // Set click listener for list items to open event details
        eventListView.setOnItemClickListener((parent, view1, position, id) -> {
            Event selectedEvent = eventList.get(position);
            openEventDetailPage(selectedEvent);
            });

        // Set click listener for admin go back button
        adminGoBack.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .commit();
        });
        return view;

    }

    /**
     * Loads the list of events from Firebase Firestore. On successful retrieval,
     * it populates the event list and notifies the adapter to update the ListView.
     * Displays a toast message if the loading fails.
     */
    private void loadEventsFromFirebase() {
        db.collection("Events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                Event event = document.toObject(Event.class);
                                eventList.add(event);
                            }
                            eventAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Opens the detail view for the selected event, allowing further edits.
     *
     * @param selectedEvent The event selected by the user, represented as an {@link Event} object.
     */
    private void openEventDetailPage(Event selectedEvent) {
        // Create and navigate to the Event Detail Fragment
        EditEventFragment detailFragment = new EditEventFragment(selectedEvent);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }



}
