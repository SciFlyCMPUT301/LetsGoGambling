package com.example.eventbooking.Admin.QRcode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.Event.ViewEventsFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
public class EditQRcodeFragment extends Fragment {
    private TextView eventTitleText, eventDescriptionText, maxParticipantsText, eventLocationText, organiserIDText;
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
    public EditQRcodeFragment(Event selectedEvent) {
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
        eventTitleText = view.findViewById(R.id.view_text_event_title);
        eventDescriptionText = view.findViewById(R.id.view_text_event_description);
        maxParticipantsText = view.findViewById(R.id.view_text_max_participants);
        eventLocationText = view.findViewById(R.id.view_text_event_location);
        organiserIDText = view.findViewById(R.id.view_organizer_text);

        removeEventButton = view.findViewById(R.id.button_remove_event);
        cancelButton = view.findViewById(R.id.button_cancel);


        if (selectedEvent != null) {
            populateEventDetails();
        }


        cancelButton.setOnClickListener(v -> goBackToViewEvents());

        removeEventButton.setOnClickListener(v->removeEvent());
        return view;
    }


    /**
     * Populates the UI with details of the selected event, allowing for existing data to be displayed and modified.
     */
    private void populateEventDetails() {
        if (selectedEvent != null) {
            eventTitleText.setText(selectedEvent.getEventTitle());
            eventDescriptionText.setText(selectedEvent.getDescription());
            eventLocationText.setText(selectedEvent.getLocation());
            maxParticipantsText.setText(String.valueOf(selectedEvent.getMaxParticipants()));
            organiserIDText.setText(selectedEvent.getOrganizerId());

        }
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