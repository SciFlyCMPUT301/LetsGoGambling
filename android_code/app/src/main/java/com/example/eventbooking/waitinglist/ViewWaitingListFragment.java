package com.example.eventbooking.waitinglist;

import android.os.Bundle;
import android.util.Log;
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

public class ViewWaitingListFragment extends Fragment {

    private ListView waitingListView;
    private Button backButton;
    private String eventId;
    private WaitingList waitingList;
    /**
     * Creates a new instance of ViewWaitingListFragment with the specified event ID.
     * Sets up the fragment's arguments to include the provided event ID.
     *
     * @param eventId The ID of the event for which the joined waiting list participant list is to be displayed.
     * @return A new instance of ViewAcceptedListFragment with the event ID argument set.
     */

    public static ViewWaitingListFragment newInstance(String eventId) {
        ViewWaitingListFragment fragment = new ViewWaitingListFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * empty constructor*/
    public ViewWaitingListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the eventId from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
        }
        Log.d("Waiting List View", "EventID: " + eventId);
        // Initialize WaitingList instance with only the event ID
        waitingList = new WaitingList(eventId);

        // Load data from Firebase for this WaitingList instance
        waitingList.loadFromFirebase().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                displayWaitingList();
            } else {
                Toast.makeText(getContext(), "Failed to load waiting list data from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wiating_list_view, container, false);

        // Initialize UI elements
        waitingListView = rootView.findViewById(R.id.waiting_list_view);
        backButton = rootView.findViewById(R.id.back_button);
        Log.d("Waiting List View", "On Create View EventID: " + eventId);
        // Set up back button listener
        backButton.setOnClickListener(v -> navigateBackToOrganizerMenu());

        return rootView;
    }
    /**
     * Displays the list of waitinglist participants in the UI. This method retrieves the list of
     * waitinglist  participant IDs from the  WaitingList instance and sets it as the data source
     * for the ListView displaying waitinglist participants.
     * it shows a message indicating that no participants have been joined and clears the adapter.
     */
    private void displayWaitingList() {
        List<String> waitingParticipantIds = waitingList.getWaitingParticipantIds();
        if (waitingParticipantIds != null && !waitingParticipantIds.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_list_item_1, waitingParticipantIds);
            waitingListView.setAdapter(adapter);
        } else {
            Toast.makeText(getContext(), "No participants on the waiting list.", Toast.LENGTH_SHORT).show();
            waitingListView.setAdapter(null);
        }
    }
    /**
     * Navigates back to the organizer menu by replacing the current fragment with an instance of
     * OrganizerMenuFragment This method is triggered when the back button is clicked, allowing
     *  the user to return to the main organizer view for event management.*/
    //not working right now
    private void navigateBackToOrganizerMenu() {
        OrganizerMenuFragment fragment = OrganizerMenuFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
