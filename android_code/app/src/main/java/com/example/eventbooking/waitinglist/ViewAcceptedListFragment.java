package com.example.eventbooking.waitinglist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.R;

import java.util.List;

public class ViewAcceptedListFragment extends Fragment {

    private ListView acceptedListView;
    private Button backButton;
    private String eventId;
    private WaitingList waitingList;

    public static ViewAcceptedListFragment newInstance(String eventId) {
        ViewAcceptedListFragment fragment = new ViewAcceptedListFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the eventId from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
        }

        // Initialize WaitingList with only the event ID
        waitingList = new WaitingList(eventId);

        // Load data from Firebase for this WaitingList instance
        waitingList.loadFromFirebase().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                displayAcceptedList();
            } else {
                Toast.makeText(getContext(), "Failed to load waiting list data from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_selected_participants, container, false);

        // Initialize UI elements
        acceptedListView = rootView.findViewById(R.id.accepted_list_view);
        backButton = rootView.findViewById(R.id.back_button);

        // Set up back button listener
        backButton.setOnClickListener(v -> navigateBackToOrganizerMenu());

        return rootView;
    }

    private void displayAcceptedList() {
        List<String> acceptedParticipantIds = waitingList.getAcceptedParticipantIds();
        if (acceptedParticipantIds != null && !acceptedParticipantIds.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_list_item_1, acceptedParticipantIds);
            acceptedListView.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "No participants have been accepted.", Toast.LENGTH_SHORT).show();
            acceptedListView.setAdapter(null);
        }
    }

    private void navigateBackToOrganizerMenu() {
        OrganizerMenuFragment fragment = OrganizerMenuFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
