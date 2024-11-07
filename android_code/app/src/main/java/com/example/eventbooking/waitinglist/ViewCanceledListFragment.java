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
    // initialize variables
    private ListView canceledListView;
    private Button backButton;
    private String eventId;
    private WaitingList waitingList;
    /**
     * Creates a new instance of ViewCanceledListFragment with the specified event ID.
     * This method sets up the fragment's arguments to include the provided event ID.
     *
     * @param eventId The ID of the event for which the canceled list is to be displayed.
     * @return A new instance of  ViewCanceledListFragment with the event ID argument set.
     */
    public static ViewCanceledListFragment newInstance(String eventId) {
        ViewCanceledListFragment fragment = new ViewCanceledListFragment();
        Bundle args = new Bundle();
        args.putString("event_id", eventId);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * empty constructor */

    public ViewCanceledListFragment() {
        // Required empty public constructor
    }

    /**
     * Called to initialize the fragment when it is created. This method retrieves the event ID
     * from the fragment's arguments, initializes a waiting list instance with the event ID,
     * and attempts to load data from Firebase. If the data loading is successful, it triggers the
     * display of the canceled list; otherwise, it displays an error message.
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
            eventId = getArguments().getString("event_id");
        }

        // Initialize the WaitingList instance as placeholder with only the event ID
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

    /**
     * Called to create and return the view hierarchy associated with this fragment. This method
     * inflates the layout for the canceled participant view, initializes UI elements, and sets up
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
        View rootView = inflater.inflate(R.layout.fragment_view_canceled_participant, container, false);

        // Initialize UI elements
        canceledListView = rootView.findViewById(R.id.canceled_list_view);
        backButton = rootView.findViewById(R.id.button_back_to_menu);

        // Set up back button listener
        backButton.setOnClickListener(v -> navigateBackToOrganizerMenu());

        return rootView;
    }
    /**
     * Displays the list of canceled participants in the UI. This method retrieves the list of
     * canceled participant IDs from the WaitingList instance and sets it as the data source
     * for the ListView displaying canceled participants. If there are no canceled participants,
     * it shows a message indicating that no participants have canceled and clears the adapter.
     */
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
    //this function not working right now, do it later
    /**
     * when back button clicked it will back to the organizer menu */
    private void navigateBackToOrganizerMenu() {
        OrganizerMenuFragment fragment = OrganizerMenuFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
