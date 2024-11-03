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

public class ViewSignedListFragment extends Fragment {
    private ListView signedUpListView;
    private Button backButton;

    private String eventId;
    private WaitingList waitingList;

    public static ViewSignedListFragment newInstance(String eventId) {
        ViewSignedListFragment fragment = new ViewSignedListFragment();
        Bundle args = new Bundle();
        args.putString("event_id", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewSignedListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the eventId from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString("event_id");
        }

        // Initialize WaitingList with only the event ID
        waitingList = new WaitingList(eventId);

        // Load data from Firebase for this WaitingList instance
        waitingList.loadFromFirebase().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                displaySignedUpList();
            } else {
                Toast.makeText(getContext(), "Failed to load waiting list data from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_signedup_participant, container, false);

        // Initialize UI elements
        signedUpListView = rootView.findViewById(R.id.signed_list_view);
        backButton = rootView.findViewById(R.id.back_button);

        // Set up back button listener
        backButton.setOnClickListener(v -> navigateBackToOrganizerMenu());

        return rootView;
    }

    private void displaySignedUpList() {
        List<String> signedUpParticipantIds = waitingList.getSignedUpParticipantIds();
        if (signedUpParticipantIds != null && !signedUpParticipantIds.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_list_item_1, signedUpParticipantIds);
            signedUpListView.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "No participants have signed up.", Toast.LENGTH_SHORT).show();
            signedUpListView.setAdapter(null);
        }
    }

    private void navigateBackToOrganizerMenu() {
        OrganizerMenuFragment fragment = OrganizerMenuFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}