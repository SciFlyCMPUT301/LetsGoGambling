package com.example.eventbooking.Admin.Event;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeUserEventAdapter;
import com.example.eventbooking.MainActivity;
import com.example.eventbooking.R;
import com.example.eventbooking.UniversalProgramValues;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
//    private ArrayList<Event> eventList;
    private ListView eventListView;
    private FirebaseFirestore db;
    private List<Event> allEvents;
    private List<Event> viewEventList;

    private SearchView searchView;
    private Spinner filterSpinner;
    private Boolean waitFlag = true;


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
        View rootview = inflater.inflate(R.layout.fragment_view_events, container, false);
        // Initialize view components
        // Starting from here views
//        backButton = view.findViewById(R.id.button_back_home);
        initalizeUI(rootview);


        if(!UniversalProgramValues.getInstance().getTestingMode()){
            Event.getAllEvents(allEvents -> {
                if (isAdded() && getActivity() instanceof MainActivity) {
                    viewEventList = new ArrayList<>(allEvents);
                    eventAdapter = new EventViewAdapter(getContext(), allEvents);
                    eventListView.setAdapter(eventAdapter);
                    setupSearchFilter();
                }
            }, e -> {
//                Log.e("HomeFragment", "Failed to fetch events: " + e.getMessage());
            });
        }

        else{
            viewEventList = new ArrayList<>(allEvents);
            eventAdapter = new EventViewAdapter(getContext(), allEvents);
            eventListView.setAdapter(eventAdapter);
            setupSearchFilter();

        }

        return rootview;

    }

    /**
     * Loads the list of events from Firebase Firestore. On successful retrieval,
     * it populates the event list and notifies the adapter to update the ListView.
     * Displays a toast message if the loading fails.
     */
//    private void loadEventsFromFirebase() {
//        db.collection("Events").get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        QuerySnapshot snapshot = task.getResult();
//                        if (snapshot != null) {
//                            for (DocumentSnapshot document : snapshot.getDocuments()) {
//                                Event event = document.toObject(Event.class);
//                                allEvents.add(event);
//                            }
//                            eventAdapter.notifyDataSetChanged();
//                        }
//                    } else {
//                        Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }


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


    private void filterEvents(String query, String filter) {
        if (query.isEmpty()) {
            eventAdapter.updateEvents(viewEventList);
            return;
        }

        List<Event> filteredEvents = viewEventList.stream().filter(event -> {
            switch (filter.toLowerCase()) {
                case "title":
                    return event.getEventTitle().toLowerCase().contains(query.toLowerCase());
                case "description":
                    return event.getDescription().toLowerCase().contains(query.toLowerCase());
                case "location":
                    return event.getLocation().toLowerCase().contains(query.toLowerCase());
                default:
                    return false;
            }
        }).collect(Collectors.toList());

        eventAdapter.updateEvents(filteredEvents);
    }

    private void setupSearchFilter() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterEvents(query, filterSpinner.getSelectedItem().toString());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterEvents(newText, filterSpinner.getSelectedItem().toString());
                return true;
            }
        });

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterEvents(searchView.getQuery().toString(), parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }


    private void initalizeUI(View view){
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            db = FirebaseFirestore.getInstance();
//            loadEventsFromFirebase();
        }
        else{
            allEvents = UniversalProgramValues.getInstance().getEventList();
        }

        eventListView = view.findViewById(R.id.event_list);
        adminGoBack = view.findViewById(R.id.admin_go_back);
        searchView = view.findViewById(R.id.search_bar);
        filterSpinner = view.findViewById(R.id.filter_spinner);


//        loadEventsFromFirebase();
        // Set click listener for list items to open event details
        eventListView.setOnItemClickListener((parent, view1, position, id) -> {
            Event selectedEvent = viewEventList.get(position);
            openEventDetailPage(selectedEvent);
        });

        // Set click listener for admin go back button
        adminGoBack.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .commit();
        });
    }



}
