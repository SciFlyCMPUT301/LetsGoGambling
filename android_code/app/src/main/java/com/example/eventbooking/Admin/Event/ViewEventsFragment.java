package com.example.eventbooking.Admin.Event;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventPageFragment.EventViewAdapter;
import com.example.eventbooking.R;
import com.example.eventbooking.UserManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewEventsFragment extends Fragment {
    private Button adminGoBack;
    private EventViewAdapter eventAdapter;
    private UserManager userManager;
    private ArrayList<Event> eventList;
    private boolean testing = false;
    private ListView eventListView;
    private Button addFacilityButton, backButton;
    private FirebaseFirestore db;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_events, container, false);

        // Starting from here views
//        backButton = view.findViewById(R.id.button_back_home);
        eventList = new ArrayList<>();
        eventListView = view.findViewById(R.id.event_list);
        addFacilityButton = view.findViewById(R.id.add_event_button);
        adminGoBack = view.findViewById(R.id.admin_go_back);

        db = FirebaseFirestore.getInstance();

        // Set up adapter for ListView
        eventAdapter = new EventViewAdapter(getContext(), eventList, false);
        eventListView.setAdapter(eventAdapter);
        loadEventsFromFirebase();

        eventListView.setOnItemClickListener((parent, view1, position, id) -> {
            Event selectedEvent = eventList.get(position);
            openEventDetailPage(selectedEvent);
            });


        adminGoBack.setOnClickListener(v -> {
            // Navigate back to HomeFragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .commit();
        });
        return view;

    }

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

    private void openEventDetailPage(Event selectedEvent) {
        // Create and navigate to the Event Detail Fragment
        EditEventFragment detailFragment = new EditEventFragment(selectedEvent);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }



}
