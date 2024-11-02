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

public class ViewCanceledListFragment extends Fragment {
    private ListView canceledListView;
    private Button backButton;
    private String eventId;
    private WaitingList waitingList;

    public static ViewCanceledListFragment newInstance(String eventId) {
        ViewCanceledListFragment fragment = new ViewCanceledListFragment();
        Bundle args = new Bundle();
        args.putString("event_id", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewCanceledListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the eventId from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString("event_id");
        }

        // Initialize the WaitingList instance with only the event ID
        waitingList = new WaitingList(eventId);

        // Load data from Firebase for this WaitingList instance
        waitingList.loadFromFirebase().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                displayCanceledList();
            } else {
                Toast.makeText(getContext(), "Failed to load waiting list data from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_canceled_participant, container, false);

        // Initialize UI elements
        canceledListView = rootView.findViewById(R.id.canceled_list_view);
        backButton = rootView.findViewById(R.id.button_back_to_menu);

        // Set up back button listener
        backButton.setOnClickListener(v -> navigateBackToOrganizerMenu());

        return rootView;
    }

    private void displayCanceledList() {
        List<String> canceledParticipantIds = waitingList.getCanceledParticipantIds();
        if (canceledParticipantIds != null && !canceledParticipantIds.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_list_item_1, canceledParticipantIds);
            canceledListView.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "No participants have canceled.", Toast.LENGTH_SHORT).show();
            canceledListView.setAdapter(null);
        }
    }

    private void navigateBackToOrganizerMenu() {
        OrganizerMenuFragment fragment = OrganizerMenuFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
