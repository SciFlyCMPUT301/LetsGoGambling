package com.example.eventbooking.waitinglist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.R;
import com.example.eventbooking.Events.EventPageFragment.EventFragment;

import java.util.List;

public class OrganizerMenuFragment extends Fragment {
    private static final String ARG_EVENT_ID = "event_id";
    private String eventId;
    private Button viewWaitingListButton;
    private Button sampleAttendeesButton;
    private Button viewCanceledListButton;
    private Button viewAcceptedListButton;
    private Button viewSignedListButton;
    private Button drawReplacementButton;
    private Button backToEventPageButton;
    private int replacementSize;
    private WaitingList waitingList;

    public OrganizerMenuFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of OrganizerMenuFragment with the provided eventId.
     *
     * @param eventId The String ID of the event.
     * @return A new instance of OrganizerMenuFragment.
     */
    public static OrganizerMenuFragment newInstance(String eventId) {
        OrganizerMenuFragment fragment = new OrganizerMenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve eventId from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
//        Log.e("Organizer", "Event found with ID: " + eventId);


        // Initialize the WaitingList instance as a placeholder
        waitingList = new WaitingList(eventId);

        // Load the waiting list data from Firebase
        waitingList.loadFromFirebase().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Waiting list loaded successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to load waiting list from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_waiting_list, container, false);

        // Initialize UI elements
        viewWaitingListButton = rootView.findViewById(R.id.waitingListButton);
        sampleAttendeesButton = rootView.findViewById(R.id.sampleAttendeesButton);
        viewSignedListButton = rootView.findViewById(R.id.SignedParticipantButton);
        viewCanceledListButton = rootView.findViewById(R.id.canceledParticipantButton);
        drawReplacementButton = rootView.findViewById(R.id.DrawReplacementButton);
        backToEventPageButton = rootView.findViewById(R.id.BackToEventButton);

        // Set up listeners
        viewWaitingListButton.setOnClickListener(v -> navigateToViewWaitingList());
        viewCanceledListButton.setOnClickListener(v -> navigateToCanceledList());
        viewSignedListButton.setOnClickListener(v -> navigateToViewSignedList());
        sampleAttendeesButton.setOnClickListener(v -> sampleAttendees());
        drawReplacementButton.setOnClickListener(v -> drawReplacement(replacementSize));
        //backToEventPageButton.setOnClickListener(v -> navigateBackToEventPage());

        return rootView;
    }

    /**
     * Navigates to the ViewWaitingListFragment.
     */
    private void navigateToViewWaitingList() {
        ViewWaitingListFragment fragment = ViewWaitingListFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Navigates to the ViewSignedListFragment.
     */
    private void navigateToViewSignedList() {
        ViewSignedListFragment fragment = ViewSignedListFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Navigates to the ViewCanceledListFragment.
     */
    private void navigateToCanceledList() {
        ViewCanceledListFragment fragment = ViewCanceledListFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Navigates back to the EventFragment.
     */
    //here need to change evenyt fragment eventid type?
//    private void navigateBackToEventPage() {
//        EventFragment fragment = EventFragment.newInstance(eventId);
//        getParentFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .commit();
//    }



    private void sampleAttendees() {
        int maxParticipants = waitingList.getMaxParticipants();
        List<String> selectedParticipants = waitingList.sampleParticipants(maxParticipants);

        if (!selectedParticipants.isEmpty()) {
            Toast.makeText(getContext(), "Sampled attendees: " + selectedParticipants, Toast.LENGTH_SHORT).show();
            waitingList.updateToFirebase().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Sampled attendees updated to Firebase.", Toast.LENGTH_SHORT).show();
                    navigateToViewWaitingList();
                } else {
                    Toast.makeText(getContext(), "Failed to update Firebase with sampled attendees.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "No participants available to sample.", Toast.LENGTH_SHORT).show();
        }
    }

    private void promptReplacementSize() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Replacement Size");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String replacementSizeStr = input.getText().toString().trim();
            if (!replacementSizeStr.isEmpty()) {
                int replacementSize = Integer.parseInt(replacementSizeStr);
                drawReplacement(replacementSize);
            } else {
                Toast.makeText(getContext(), "Replacement size cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void drawReplacement(int replacementSize) {
        List<String> replacements = waitingList.drawReplacement(replacementSize);

        if (!replacements.isEmpty()) {
            Toast.makeText(getContext(), "Replacements drawn: " + replacements, Toast.LENGTH_SHORT).show();
            waitingList.updateToFirebase().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Replacement attendees updated to Firebase.", Toast.LENGTH_SHORT).show();
                    navigateToViewWaitingList();
                } else {
                    Toast.makeText(getContext(), "Failed to update Firebase with replacement attendees.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "No participants available for replacement.", Toast.LENGTH_SHORT).show();
        }
    }






}
