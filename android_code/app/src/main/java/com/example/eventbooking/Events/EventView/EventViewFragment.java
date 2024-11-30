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
import com.example.eventbooking.Events.EventPageFragment.EventFragment;
import com.example.eventbooking.MainActivity;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.profile.ProfileEntrantFragment;
import com.example.eventbooking.profile.ProfileFragment;

/**
 * EventViewFragment is a fragment that displays details of an event.
 */
public class EventViewFragment extends Fragment {

    private String eventId;
    private String deviceId = null;
//    private String userId = "User1";
    private Event selectedEvent;
    private ImageView eventPosterImage;
    private TextView eventTitleText, eventDescriptionText;
    private LinearLayout buttonContainer;
    private Boolean testMode = false;
    private String returnToFragment = null;

    /**
     * Creates a new instance of EventViewFragment.
     * @param eventID
     * @param deviceID
     * @return
     */
    public static EventViewFragment newInstance(String eventID, String deviceID) {
        EventViewFragment fragment = new EventViewFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventID);
        args.putString("deviceId", deviceID);
        fragment.setArguments(args);
        return fragment;
    }


    public static EventViewFragment newInstance(Event selectedEvent, String deviceID) {
        EventViewFragment fragment = new EventViewFragment();
        Bundle args = new Bundle();
        args.putParcelable("selectedEvent", selectedEvent);
        args.putString("deviceId", deviceID);
//        args.putString("returnFragment", returnFragment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_view, container, false);

        // Initialize views
        eventPosterImage = view.findViewById(R.id.event_poster_image);
        eventTitleText = view.findViewById(R.id.event_title_text);
        eventDescriptionText = view.findViewById(R.id.event_description_text);
        buttonContainer = view.findViewById(R.id.button_container);
        testMode = UniversalProgramValues.getInstance().getTestingMode();
        // Retrieve eventId and deviceId from arguments
        if (getArguments() != null) {
            if(getArguments().getString("eventId") != null)
                eventId = getArguments().getString("eventId");
            if(getArguments().getString("deviceId") != null)
                deviceId = getArguments().getString("deviceId");

            if(deviceId == null)
                deviceId = UserManager.getInstance().getUserId();
//            String listchoice = getArguments().getString("listAdd");

//            if(getArguments().getString("returnFragment") != null){
//                returnToFragment = getArguments().getString("returnFragment");
//            }

            // Fetch event data based on eventId
            if(!testMode){
                Event.findEventById(eventId, event -> {
                    if (event != null) {
                        selectedEvent = event;
                        Log.e("eventId", "Event found with ID: " + selectedEvent.getEventId());
                        displayEventDetails(selectedEvent);
                        configureButtons(deviceId);
                    } else {
                        Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }, e -> {
                    Toast.makeText(getContext(), "Error fetching event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                });
            }
            else{
//                selectedEvent = getArguments().getParcelable("selectedEvent");
                selectedEvent = UniversalProgramValues.getInstance().getCurrentEvent();
                displayEventDetails(selectedEvent);
                configureButtons(deviceId);
            }

        }

        return view;
    }

    /**
     * Displays details of an event in the UI.
     * @param event
     */
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
        // loadImageIntoView(event.getImageUrl(), eventPosterImage);
    }

    /**
     * Configures buttons based on the user's list status in the event.
     * @param selectedUserId
     */
    private void configureButtons(String selectedUserId) {
        Log.d("Event View Fragment", "Button Add Start");
        // Clear existing buttons
        buttonContainer.removeAllViews();
        Log.d("Event View", "Button add: " + selectedEvent + ", " +
                selectedEvent.getAcceptedParticipantIds().contains(selectedUserId));

        // If the user is in the accepted list
        if (selectedEvent.getAcceptedParticipantIds().contains(selectedUserId)) {
            Log.d("Event View Fragment", "Add Sign Up");
            // Add "Sign Up" button to move user to signed-up list
            addButton("Sign Up", v -> {
                selectedEvent.signUpParticipant(selectedUserId);
                if(!UniversalProgramValues.getInstance().getTestingMode())
                    updateEventInFirestore(selectedEvent);
                goBackToEventFragment();
            });

            // Add "Reject" button to move user to canceled list
            addButton("Reject", v -> {
                selectedEvent.cancelParticipant(selectedUserId);
                if(!UniversalProgramValues.getInstance().getTestingMode())
                    updateEventInFirestore(selectedEvent);
                goBackToEventFragment();
            });

            // Add "Decline" button to move user to declined list
//            addButton("Decline", v -> {
//                selectedEvent.addDeclinedParticipantId(selectedUserId); // Adds user to declined list
//                updateEventInFirestore(selectedEvent);
//            });
        } else if(selectedEvent.getWaitingParticipantIds().contains(selectedUserId)){
            Log.d("Event View Fragment", "Add WaitList");
            addButton("Leave Waitlist", v -> {
                selectedEvent.removeWaitingParticipantId(selectedUserId);
                if(!UniversalProgramValues.getInstance().getTestingMode())
                    updateEventInFirestore(selectedEvent);
                goBackToEventFragment();
            });
        }

        else if (!selectedEvent.getWaitingParticipantIds().contains(selectedUserId) &&
                !selectedEvent.getSignedUpParticipantIds().contains(selectedUserId) &&
                !selectedEvent.getCanceledParticipantIds().contains(selectedUserId)) {
            Log.d("Event View Fragment", "Not In Any List");
            // If user is not in any list, add "Waitlist" button to add user to waiting list
            addButton("Join Waitlist", v -> {
                if(selectedEvent.isGeolocationRequired()){
                    if(!UniversalProgramValues.getInstance().getTestingMode())
                        showGeolocationWarningDialog(selectedEvent, selectedUserId);
                }else{
                    selectedEvent.addWaitingParticipantIds(selectedUserId);
                    if(!UniversalProgramValues.getInstance().getTestingMode())
                        updateEventInFirestore(selectedEvent);
                    goBackToEventFragment();
                }
            });
        }

        // Add Cancel button to go back
        addButton("Cancel", v -> getActivity().getSupportFragmentManager().popBackStack());
    }

    /**
     * Adds a button to the button container.
     * @param text
     * @param listener
     */
    private void addButton(String text, View.OnClickListener listener) {
        Button button = new Button(getContext());
        button.setText(text);
        button.setOnClickListener(listener);
        buttonContainer.addView(button);
    }

    /**
     * Updates the event in Firestore.
     * @param event
     */
    private void updateEventInFirestore(Event event) {
        if(!testMode){
            event.saveEventDataToFirestore().addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(), "Event updated successfully", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Failed to update event", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private boolean isGPSEnabled() {
        MainActivity mainActivity = (MainActivity) getActivity();
        return mainActivity != null && mainActivity.isGPSEnabled();
    }


    private void showGeolocationWarningDialog(Event selectedEvent, String selectedUserId) {
        new android.app.AlertDialog.Builder(requireContext())
                .setTitle("Geolocation Requirement")
                .setMessage("This event requires geolocation to join the waiting list. If enabled, you'll be added to the waiting list.")
                .setPositiveButton("OK", (dialog, which) -> {
                    if (isGPSEnabled()) {
                        // If GPS is enabled, directly join the waiting list
                        selectedEvent.addWaitingParticipantIds(selectedUserId);
                        updateEventInFirestore(selectedEvent);
                        goBackToEventFragment();
                    } else {
                        // Redirect to the profile if GPS is not enabled
                        navigateToProfile();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) ->
                        Toast.makeText(getContext(), "Geolocation is required to join the waiting list.", Toast.LENGTH_SHORT).show())
                .show();
    }

    private void navigateToProfile() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ProfileEntrantFragment())
                .addToBackStack(null)
                .commit();
    }


    /**
     * Goes back to the EventFragment.
     */
    private void goBackToEventFragment(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .addToBackStack(null)
                .commit();
    }
}