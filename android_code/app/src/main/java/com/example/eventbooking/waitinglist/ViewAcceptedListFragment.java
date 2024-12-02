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
import com.example.eventbooking.UniversalProgramValues;

import java.util.List;
/**
 * A fragment that displays the list of participants who have been accepted for an event.
 * This fragment retrieves the accepted participants associated with a given event ID, displays them in a
 * list view, and provides navigation back to the organizer's menu.
 */
public class ViewAcceptedListFragment extends Fragment {

    private ListView acceptedListView;
    private Button backButton;
    private String eventId;
    private WaitingList waitingList;
    /**
     * Creates a new instance of ViewAcceptedListFragment with the specified event ID.
     * Sets up the fragment's arguments to include the provided event ID.
     *
     * @param eventId The ID of the event for which the accepted participant list is to be displayed.
     * @return A new instance of ViewAcceptedListFragment with the event ID argument set.
     */
    public static ViewAcceptedListFragment newInstance(String eventId) {
        ViewAcceptedListFragment fragment = new ViewAcceptedListFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Initializes the fragment when it is created. Retrieves the event ID from the fragment's arguments,
     * initializes a  WaitingList instance with the event ID, and attempts to load data from Firebase.
     * If data loading is successful, it displays the list of accepted participants; otherwise, it shows an
     * error message.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the
     *                           state. This parameter is null when the fragment is first created.
     */

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
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            waitingList.loadFromFirebase().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    displayAcceptedList();
                } else {
                    Toast.makeText(getContext(), "Failed to load waiting list data from Firebase.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            waitingList.setWaitingParticipantIds(UniversalProgramValues.getInstance().queryEvent(eventId).getWaitingParticipantIds());
            waitingList.setAcceptedParticipantIds(UniversalProgramValues.getInstance().queryEvent(eventId).getAcceptedParticipantIds());
            waitingList.setSignedUpParticipantIds(UniversalProgramValues.getInstance().queryEvent(eventId).getSignedUpParticipantIds());
            waitingList.setCanceledParticipantIds(UniversalProgramValues.getInstance().queryEvent(eventId).getCanceledParticipantIds());

        }

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
        displayAcceptedList();
        return rootView;
    }
    /**
     * Displays the list of accepted participants in the UI. This method retrieves the list of
     * accepted participant IDs from the  WaitingList instance and sets it as the data source
     * for the ListView displaying accepted participants. If there are no accepted participants,
     * it shows a message indicating that no participants have been accepted and clears the adapter.
     */
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
    /**
     * Navigates back to the organizer menu by replacing the current fragment with an instance of
     * OrganizerMenuFragment This method is triggered when the back button is clicked, allowing
     * the user to return to the main organizer view for event management.
     */
    //not wroking now

    private void navigateBackToOrganizerMenu() {
        OrganizerMenuFragment fragment = OrganizerMenuFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
