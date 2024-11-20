package com.example.eventbooking.Events.EventView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventPageFragment.EventActivity;
import com.example.eventbooking.Home.HomeActivity;
import com.example.eventbooking.R;

public class EventViewActivity extends AppCompatActivity {
    private String eventId;
    private String deviceId;
    private Event event;
    private ImageView eventPosterImage;
    private TextView eventTitleText, eventDescriptionText;
    private LinearLayout buttonContainer;
    private String source_file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ///TODO: Alter this so it looks nicer
        setContentView(R.layout.activity_event_view);

        // Initialize views
        eventPosterImage = findViewById(R.id.event_poster_image);
        eventTitleText = findViewById(R.id.event_title_text);
        eventDescriptionText = findViewById(R.id.event_description_text);
        buttonContainer = findViewById(R.id.button_container);

        // Get extras
        eventId = getIntent().getStringExtra("eventId");
        deviceId = getIntent().getStringExtra("deviceId");
        source_file = getIntent().getStringExtra("source_file");
        Log.d("Event View Activity", "EventID: " + eventId);
        Log.d("Event View Activity", "DeviceID: " + deviceId);
        Log.d("Event View Activity", "Source File: " + source_file);

        if(source_file != null || source_file.equals("LoginActivity"))
            source_file = "Home";
        else
            source_file = "Event";

        // Fetch event data
        Event.findEventById(eventId, event -> {
            if (event != null) {
                displayEventDetails(event);
                configureButtons(event, deviceId);
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, e -> {
            Toast.makeText(this, "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    /**
     * Displays details of an event in the UI.
     * @param event
     */
    private void displayEventDetails(Event event) {
        if (event == null) {
            Log.e("EventViewFragment", "Event is null, cannot display details.");
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            return;  // Exit if event is null to avoid the NullPointerException
        }
        Log.e("Display Event", "Event found with ID: " + event.getEventId());
        eventTitleText.setText(event.getEventTitle());
        eventDescriptionText.setText(event.getDescription());
        Log.e("Done Display Event", "Event found with ID: " + event.getEventId());

        // Load event poster image (assume you have a method to do this)
        // loadImageIntoView(event.getImageUrl(), eventPosterImage);
    }

    /**
     * Configures buttons based on the user's list status in the event.
     * @param selectedEvent
     * @param selectedUserId
     */
    private void configureButtons(Event selectedEvent, String selectedUserId) {
        // Clear existing buttons
        buttonContainer.removeAllViews();
        Log.d("Event View", "Button add: " + selectedEvent + ", " +
                selectedEvent.getAcceptedParticipantIds().contains(selectedUserId));

        // If the user is in the accepted list
        if (selectedEvent.getAcceptedParticipantIds().contains(selectedUserId)) {
            // Add "Sign Up" button to move user to signed-up list
            addButton("Sign Up", v -> {
                selectedEvent.signUpParticipant(selectedUserId);
                updateEventInFirestore(selectedEvent);
                goBackToSource();
            });

            // Add "Reject" button to move user to canceled list
            addButton("Reject", v -> {
                selectedEvent.cancelParticipant(selectedUserId);
                updateEventInFirestore(selectedEvent);
                goBackToSource();
            });

            // Add "Decline" button to move user to declined list
//            addButton("Decline", v -> {
//                selectedEvent.addDeclinedParticipantId(selectedUserId); // Adds user to declined list
//                updateEventInFirestore(selectedEvent);
//            });
        } else if(selectedEvent.getWaitingParticipantIds().contains(selectedUserId)){
            addButton("Leave Waitlist", v -> {
                selectedEvent.removeWaitingParticipantId(selectedUserId);
                updateEventInFirestore(selectedEvent);
                goBackToSource();
            });
        }

        else if (!selectedEvent.getWaitingParticipantIds().contains(selectedUserId) &&
                !selectedEvent.getSignedUpParticipantIds().contains(selectedUserId) &&
                !selectedEvent.getCanceledParticipantIds().contains(selectedUserId)) {
            // If user is not in any list, add "Waitlist" button to add user to waiting list
            addButton("Waitlist", v -> {
                selectedEvent.addWaitingParticipantIds(selectedUserId);
                updateEventInFirestore(selectedEvent);
                goBackToSource();
            });
        }

        // Add Cancel button to go back
        addButton("Cancel", v -> goBackToSource());
    }

    /**
     * Adds a button to the button container.
     * @param text
     * @param listener
     */
    private void addButton(String text, View.OnClickListener listener) {
        Button button = new Button(this);
        button.setText(text);
        button.setOnClickListener(listener);
        buttonContainer.addView(button);
    }

    /**
     * Updates the event in Firestore.
     * @param event
     */
    private void updateEventInFirestore(Event event) {
        event.saveEventDataToFirestore().addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show();
        });
    }

    private void goBackToSource(){
        if(source_file.equals("Home")){
            startActivity(new Intent(this, HomeActivity.class));
        }
        else{
            startActivity(new Intent(this, EventActivity.class));
        }

    }

}
