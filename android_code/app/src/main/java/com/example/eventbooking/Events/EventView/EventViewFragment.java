package com.example.eventbooking.Events.EventView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.R;

public class EventViewFragment extends Fragment {

    private String eventId;
    private String deviceId = "deviceID1";
    private String userId = "User1";
    private Event event;
    private ImageView eventPosterImage;
    private TextView eventTitleText, eventDescriptionText;
    private LinearLayout buttonContainer;

    public static EventViewFragment newInstance(String eventID, String deviceID) {
        EventViewFragment fragment = new EventViewFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventID);
        args.putString("deviceId", deviceID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_view, container, false);
        Log.d("In event", "Got into the event");
        // Initialize views
        eventPosterImage = view.findViewById(R.id.event_poster_image);
        eventTitleText = view.findViewById(R.id.event_title_text);
        eventDescriptionText = view.findViewById(R.id.event_description_text);
        buttonContainer = view.findViewById(R.id.button_container);

        // Retrieve eventId and deviceId from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            deviceId = getArguments().getString("deviceId");
            Log.d("In event", "eventid: "+ eventId);
            Log.d("In event", "deviceId: "+ deviceId);
//            String listchoice = getArguments().getString("listAdd");



            // Fetch event data based on eventId
//            event = findEventById(eventId);
            Event.findEventById(eventId, event -> {
//                if (event != null) {
//                    if(listchoice == "Accepted")
//                        event.addAcceptedParticipantId("User1");
//                    if(listchoice == "Waiting")
//                        event.addWaitingParticipantIds("User1");
//                    if(listchoice == "Canceled")
//                        event.addCanceledParticipantIds("User1");
//                    if(listchoice == "SignedUp")
//                        event.addSignedUpParticipantIds("User1");
//                    Log.e("eventId", "Event found with ID: " + event.getEventId());
                    displayEventDetails(event);
                    configureButtons(event, userId);
//                } else {
//                    Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
//                    getActivity().getSupportFragmentManager().popBackStack();
//                }
            }, e -> {
                Toast.makeText(getContext(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().popBackStack();
            });
//            // Populate UI with event details
//            displayEventDetails(event);
//            configureButtons();
        }

        return view;
    }

    private void displayEventDetails(Event event) {
        if (event == null) {
            Log.e("EventViewFragment", "Event is null, cannot display details.");
            Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
            return;  // Exit if event is null to avoid the NullPointerException
        }
        Log.e("Display Event", "Event found with ID: " + event.getEventId());
        eventTitleText.setText(event.getEventTitle());
        eventDescriptionText.setText(event.getDescription());
        Log.e("Done Display Event", "Event found with ID: " + event.getEventId());

        // Load event poster image (assume you have a method to do this)
//        loadImageIntoView(event.getImageUrl(), eventPosterImage);
    }

    private void configureButtons(Event selectedEvent, String selectedUserId) {
        // Clear existing buttons
        buttonContainer.removeAllViews();
        Log.d("Event View", "Button add: " + selectedEvent + ", " +
                selectedEvent.getAcceptedParticipantIds().contains(selectedUserId));
        if (selectedEvent.getAcceptedParticipantIds().contains(selectedUserId)) {
            // User is in the accepted list
            addButton("Sign Up", v -> {
                selectedEvent.signUpParticipant(selectedUserId);  // Moves user to signed-up list
                updateEventInFirestore(selectedEvent);
            });
            addButton("Reject", v -> {
                selectedEvent.cancelParticipant(selectedUserId);  // Moves user to canceled list
                updateEventInFirestore(selectedEvent);
            });
        } else if (!selectedEvent.getWaitingParticipantIds().contains(selectedUserId) &&
                !selectedEvent.getSignedUpParticipantIds().contains(selectedUserId) &&
                !selectedEvent.getCanceledParticipantIds().contains(selectedUserId)) {
            // User is not in any list
            addButton("Waitlist", v -> {
                selectedEvent.addWaitingParticipantIds(selectedUserId);  // Adds user to waiting list
                updateEventInFirestore(selectedEvent);
            });
        }

        // Add Cancel button to go back
        addButton("Cancel", v -> getActivity().getSupportFragmentManager().popBackStack());
    }

    private void addButton(String text, View.OnClickListener listener) {
        Button button = new Button(getContext());
        button.setText(text);
        button.setOnClickListener(listener);
        buttonContainer.addView(button);
    }

    private void updateEventInFirestore(Event event) {
        event.saveEventDataToFirestore().addOnSuccessListener(aVoid -> {
            Toast.makeText(getContext(), "Event updated successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to update event", Toast.LENGTH_SHORT).show();
        });
    }
}
