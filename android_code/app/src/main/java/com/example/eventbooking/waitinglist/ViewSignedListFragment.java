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
 * A fragment that displays the list of participants who have signed up for an event.
 * This fragment retrieves a waiting list associated with an event ID, displays the list of signed-up
 * participants, and provides navigation back to the organizer's menu.
 */
@SuppressWarnings("all")
public class ViewSignedListFragment extends Fragment {
    private ListView signedUpListView;
    private Button backButton;

    private String eventId;
    private WaitingList waitingList;
    /**
     * Creates a new instance of ViewSignedListFragment with the specified event ID.
     * Sets up the fragment's arguments to include the provided event ID.
     *
     * @param eventId The ID of the event for which the signed-up participant list is to be displayed.
     * @return A new instance of ViewSignedListFragment with the event ID argument set.
     */
    public static ViewSignedListFragment newInstance(String eventId) {
        ViewSignedListFragment fragment = new ViewSignedListFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventId);
        fragment.setArguments(args);
        return fragment;
    }

    public ViewSignedListFragment() {
        // Required empty public constructor
    }
    /**
     * Called to initialize the fragment when it is created. This method retrieves the event ID
     * from the fragment's arguments, initializes a  WaitingList instance with the event ID,
     * and attempts to load data from Firebase. If data loading is successful, it triggers the display
     * of the signed-up participant list; otherwise, it displays an error message.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                           this is the state. This parameter is null when the fragment is
     *                           first created.
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
                    displaySignedUpList();
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
    /**
     * Called to create and return the view hierarchy associated with this fragment. This method
     * inflates the layout for the signed-up participant view, initializes UI elements, and sets up
     * a listener for the back button to navigate back to the organizer menu.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     *                  The fragment should not add the view itself, but this can be used to generate
     *                  the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here.
     * @return The root view for the fragment's UI, or null.
     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_signedup_participant, container, false);

        // Initialize UI elements
        signedUpListView = rootView.findViewById(R.id.signed_list_view);
        backButton = rootView.findViewById(R.id.back_button);

        // Set up back button listener
        backButton.setOnClickListener(v -> navigateBackToOrganizerMenu());
        displaySignedUpList();
        return rootView;
    }
    /**
     * Displays the list of signed-up participants in the UI. This method retrieves the list of
     * signed-up participant IDs from the  WaitingList nstance and sets it as the data source
     * for the ListView displaying signed-up participants. If there are no signed-up participants,
     * it shows a message indicating that no participants have signed up and clears the adapter.
     */

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
    /**
     * Navigates back to the organizer menu. When the back button is clicked, this method replaces
     * the current fragment with an instance of OrganizerMenuFragment.
     */

    private void navigateBackToOrganizerMenu() {
        OrganizerMenuFragment fragment = OrganizerMenuFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
