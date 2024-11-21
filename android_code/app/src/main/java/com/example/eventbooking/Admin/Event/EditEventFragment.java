package com.example.eventbooking.Admin.Event;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment allows the administrator to edit an existing event. The administrator can update
 * event details, manage participants, and delete the event from the Firebase Firestore database.
 * It includes options to edit the event's title, description, location, and maximum participants,
 * as well as to view and modify participant lists by categories such as Waiting, Accepted, Canceled, and Signed Up.
 *
 * <p>The fragment uses a {@link Spinner} to switch between participant lists and provides
 * controls for adding, removing, saving, or deleting an event.</p>
 *
 * <p>Dependencies include Firebase Firestore for database interactions and a custom
 * {@link Event} class representing the event data structure.</p>
 */
public class EditEventFragment extends Fragment {
    private Spinner listSelectorSpinner;
    private EditText eventTitleEditText, eventDescriptionEditText, maxParticipantsEditText, eventLocationEditText, organiserIDEditText
            , participantEditText;
    private Button saveButton, cancelButton, addParticipantButton, removeParticipantButton, removeEventButton;
    private ListView participantsListView;
    private Event selectedEvent;
    private ArrayAdapter<String> participantsAdapter;

    private FirebaseFirestore db;
    private List<String> selectedList = null;
    private List<String> updatedWaitingList;
    private List<String> updatedAcceptedList;
    private List<String> updatedCanceledList;
    private List<String> updatedSignedUpList;

    /**
     *Constructor for the EditEventFragment class.
     * @param selectedEvent
     */
    public EditEventFragment(Event selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    /**
     *Inflates the layout and initializes views and listeners for editing event details.
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
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);
        db = FirebaseFirestore.getInstance();
        // Initialize views
        listSelectorSpinner = view.findViewById(R.id.spinner_list_selector);
        eventTitleEditText = view.findViewById(R.id.edit_text_event_title);
        eventDescriptionEditText = view.findViewById(R.id.edit_text_event_description);
        maxParticipantsEditText = view.findViewById(R.id.edit_text_max_participants);
        eventLocationEditText = view.findViewById(R.id.edit_text_event_location);
        organiserIDEditText = view.findViewById(R.id.edit_organizer_text);
        participantEditText = view.findViewById(R.id.edit_participant);

        removeEventButton = view.findViewById(R.id.button_remove_event);
        saveButton = view.findViewById(R.id.button_save);
        cancelButton = view.findViewById(R.id.button_cancel);
        participantsListView = view.findViewById(R.id.participants_list_view);
        addParticipantButton = view.findViewById(R.id.button_add_participant);
        removeParticipantButton = view.findViewById(R.id.button_remove_participant);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                new String[]{"Waiting List", "Accepted List", "Canceled List", "Signed Up List"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listSelectorSpinner.setAdapter(adapter);

        if (selectedEvent != null) {
            populateEventDetails();
        }

        listSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateParticipantsList(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle when no item is selected
            }
        });

        saveButton.setOnClickListener(v -> saveEvent());

        cancelButton.setOnClickListener(v -> goBackToViewEvents());

        addParticipantButton.setOnClickListener(v -> addParticipant());

        removeParticipantButton.setOnClickListener(v -> removeParticipant());

        removeEventButton.setOnClickListener(v->removeEvent());
        return view;
    }


    /**
     * Populates the UI with details of the selected event, allowing for existing data to be displayed and modified.
     */
    private void populateEventDetails() {
        if (selectedEvent != null) {
            eventTitleEditText.setText(selectedEvent.getEventTitle());
            eventDescriptionEditText.setText(selectedEvent.getDescription());
            eventLocationEditText.setText(selectedEvent.getLocation());
            maxParticipantsEditText.setText(String.valueOf(selectedEvent.getMaxParticipants()));
            organiserIDEditText.setText(selectedEvent.getOrganizerId());

            updatedWaitingList = new ArrayList<>(selectedEvent.getWaitingParticipantIds());
            updatedAcceptedList = new ArrayList<>(selectedEvent.getAcceptedParticipantIds());
            updatedCanceledList = new ArrayList<>(selectedEvent.getCanceledParticipantIds());
            updatedSignedUpList = new ArrayList<>(selectedEvent.getSignedUpParticipantIds());
        }
    }

    /**
     * Saves the modified event details to the Firebase Firestore database. Checks for valid input
     * and displays appropriate messages based on the success or failure of the update operation.
     */
    private void saveEvent() {
        String newTitle = eventTitleEditText.getText().toString().trim();
        String newDescription = eventDescriptionEditText.getText().toString().trim();
        String newLocation = eventLocationEditText.getText().toString().trim();
        String maxParticipantsStr = maxParticipantsEditText.getText().toString().trim();
        String newOrganizerID = organiserIDEditText.getText().toString().trim();
        if (TextUtils.isEmpty(newTitle) || TextUtils.isEmpty(newDescription) || TextUtils.isEmpty(newLocation) ||
                TextUtils.isEmpty(maxParticipantsStr) || TextUtils.isEmpty(newOrganizerID)) {
            Toast.makeText(getContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        int newMaxParticipants = Integer.parseInt(maxParticipantsStr);

        // Update the event data
        selectedEvent.updateEventData(newTitle, newDescription, newLocation, newMaxParticipants, newOrganizerID,
                        updatedWaitingList, updatedAcceptedList, updatedCanceledList, updatedSignedUpList)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event updated successfully", Toast.LENGTH_SHORT).show();
                    goBackToViewEvents();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update event", Toast.LENGTH_SHORT).show());
    }

    /**
     * Returns the user to the view events screen by replacing this fragment with the ViewEventsFragment.
     */
    private void goBackToViewEvents() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ViewEventsFragment())
                .commit();
    }

    /**
     *Updates the participant list displayed in the ListView based on the selected category in the spinner.
     *
     * @param position
     */
    private void updateParticipantsList(int position) {
        selectedList = null;
        switch (position) {
            case 0:
                selectedList = selectedEvent.getWaitingParticipantIds();
                break;
            case 1:
                selectedList = selectedEvent.getAcceptedParticipantIds();
                break;
            case 2:
                selectedList = selectedEvent.getCanceledParticipantIds();
                break;
            case 3:
                selectedList = selectedEvent.getSignedUpParticipantIds();
                break;
        }

        if (selectedList != null) {
            participantsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, selectedList);
            participantsListView.setAdapter(participantsAdapter);
        }
    }

    /**
     * Adds a participant to the currently selected participant list, if the participant name
     * is not already in the list. Displays a message if the input is empty or the participant already exists.
     */
    private void addParticipant() {
        String participant = participantEditText.getText().toString().trim();

        if (TextUtils.isEmpty(participant)) {
            Toast.makeText(getContext(), "Please enter a participant name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedList != null && !selectedList.contains(participant)) {
            // Add participant to the list
            selectedList.add(participant);
            // Add participant to the specific list
            int position = listSelectorSpinner.getSelectedItemPosition();
            switch (position) {
                case 0:
                    updatedWaitingList.add(participant);
                    break;
                case 1:
                    updatedAcceptedList.add(participant);
                    break;
                case 2:
                    updatedCanceledList.add(participant);
                    break;
                case 3:
                    updatedSignedUpList.add(participant);
                    break;
            }
            participantsAdapter.notifyDataSetChanged(); // Refresh the ListView
        } else {
            Toast.makeText(getContext(), "Participant already exists.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Removes a participant from the currently selected participant list, if the participant name
     * exists in the list. Displays a message if the input is empty or the participant is not found.
     */
    // Method to remove a participant
    private void removeParticipant() {
        String participant = participantEditText.getText().toString().trim();

        if (TextUtils.isEmpty(participant)) {
            Toast.makeText(getContext(), "Please enter a participant name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedList != null && selectedList.contains(participant)) {
            selectedList.remove(participant);
            // Remove participant to the specific list
            int position = listSelectorSpinner.getSelectedItemPosition();
            switch (position) {
                case 0:
                    updatedWaitingList.remove(participant);
                    break;
                case 1:
                    updatedAcceptedList.remove(participant);
                    break;
                case 2:
                    updatedCanceledList.remove(participant);
                    break;
                case 3:
                    updatedSignedUpList.remove(participant);
                    break;
            }
            participantsAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getContext(), "Participant not found.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes the selected event from Firebase Firestore. Displays a success message and navigates
     * back to the previous screen upon successful deletion, or shows an error message if the deletion fails.
     */
    private void removeEvent() {
        db.collection("Events").document(selectedEvent.getEventId()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event deleted successfully.", Toast.LENGTH_SHORT).show();
                    db.collection("Facilities").get()
                            .addOnSuccessListener(querySnapshot -> {
                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    List<String> allEvents = (List<String>) doc.get("allEvents");
                                    if (allEvents != null && allEvents.contains(selectedEvent.getEventId())) {
                                        allEvents.remove(selectedEvent.getEventId());
                                        db.collection("Facilities").document(doc.getId())
                                                .update("allEvents", allEvents)
                                                .addOnSuccessListener(aVoid2 -> Log.d("EditEventFragment", "Removed event ID from Facilities: " + doc.getId()))
                                                .addOnFailureListener(e -> Log.e("EditEventFragment", "Failed to update Facilities: " + doc.getId(), e));
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Log.e("EditEventFragment", "Failed to query Facilities collection", e));

                    getActivity().onBackPressed();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to delete event: ", Toast.LENGTH_SHORT).show();
                });
    }


}
