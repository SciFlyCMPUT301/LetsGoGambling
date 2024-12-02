package com.example.eventbooking.Events.EventCreate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.R;
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;
import com.squareup.picasso.Picasso;

public class EventEditFragment extends Fragment {
    private EditText editTitle, editDescription, editLocation, editMaxParticipants, editMaxWaitlistSize;
    private Switch geolocationSwitch;
    private Button editButton, uploadPosterButton, removePosterButton, saveButton, backButton, moveToMoreOrganizerDetail;
    private ImageView eventPoster;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImageUri;
    private boolean isEditing = false;

    private String currentUserID;
    private Event editEvent;
    private QRcodeGenerator qrCodeGenerator;

    /**
     * Default constructor for the EventEditFragment.
     */
    public EventEditFragment(){}

    /**
     * Creates a new instance of EventCreateFragment, primarily for testing.
     *
     * @param selectedEvent is the event that we want to view and edit
     * @return A new instance of EventCreateFragment.
     */
    public static EventEditFragment newInstance(Event selectedEvent) {
        EventEditFragment fragment = new EventEditFragment();
        Bundle args = new Bundle();
        args.putParcelable("selected event", selectedEvent);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is created. Retrieves the testing flag from arguments if provided.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the integer from arguments
        if (getArguments() != null) {
            editEvent = getArguments().getParcelable("selected event");
        }
        qrCodeGenerator = new QRcodeGenerator(getContext());
    }

    /**
     * Inflates the fragment's layout and initializes UI elements.
     *
     * @param inflater           LayoutInflater used to inflate the layout
     * @param container          ViewGroup container in which the fragment is placed
     * @param savedInstanceState Bundle containing saved state data (if any)
     * @return the root View for the fragment's layout
     */
    // Inflate the layout and display the integer
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_edit, container, false);
        initializeUI(view);
        currentUserID = UserManager.getInstance().getUserId();
        displayEventDetails(editEvent);


        return view;
    }



    private void initializeUI(View view) {
        editTitle = view.findViewById(R.id.edit_event_title);
        editDescription = view.findViewById(R.id.edit_event_description);
        editLocation = view.findViewById(R.id.edit_event_location);
        editMaxParticipants = view.findViewById(R.id.edit_max_participant);
        editMaxWaitlistSize = view.findViewById(R.id.edit_max_waitlist_size);
        geolocationSwitch = view.findViewById(R.id.geolocation_switch);
        eventPoster = view.findViewById(R.id.event_poster_image);

        editButton = view.findViewById(R.id.button_edit_event);
        uploadPosterButton = view.findViewById(R.id.button_upload_poster);
        removePosterButton = view.findViewById(R.id.button_remove_poster);
        saveButton = view.findViewById(R.id.button_save_event);
        backButton = view.findViewById(R.id.button_back_home);
        moveToMoreOrganizerDetail = view.findViewById(R.id.button_navigate_to_menu);

        editButton.setOnClickListener(v -> toggleEditMode());
        uploadPosterButton.setOnClickListener(v -> launchTestImagePicker());
        removePosterButton.setOnClickListener(v -> removePoster());
        saveButton.setOnClickListener(v -> saveEventDetails());
        backButton.setOnClickListener(v -> goBackToOrganizerPage());
        moveToMoreOrganizerDetail.setOnClickListener(v -> navigateToOrganizerMenu());

        setEditMode(false);
    }

    private void toggleEditMode() {
        isEditing = !isEditing;
        setEditMode(isEditing);
    }

    private void setEditMode(boolean enable) {
        editTitle.setEnabled(enable);
        editDescription.setEnabled(enable);
        editLocation.setEnabled(enable);
        editMaxParticipants.setEnabled(enable);
        editMaxWaitlistSize.setEnabled(enable);
        geolocationSwitch.setEnabled(enable);

        uploadPosterButton.setVisibility(enable ? View.VISIBLE : View.GONE);
        removePosterButton.setVisibility(enable ? View.VISIBLE : View.GONE);
        saveButton.setVisibility(enable ? View.VISIBLE : View.GONE);
        editButton.setText(enable ? "Cancel" : "Edit");
    }


    /**
     * Launches a test image picker to simulate selecting an image for upload.
     * <p>
     * Directly uses a predefined image URI for testing purposes and initiates the upload process.
     */
    private void launchTestImagePicker() {
        Uri selectedImageUri = Uri.parse(UniversalProgramValues.getInstance().getUploadProfileURL());
        uploadPoster(selectedImageUri);
    }

    /**
     * upload the poster the user has choose and display to let users see the format
     * @param imageUri
     * */

    private void uploadPoster(Uri imageUri) {
        if (editEvent == null) {
            Toast.makeText(getContext(), "Event not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload the custom poster
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            editEvent.uploadCustomPoster(imageUri)
                    .addOnSuccessListener(aVoid -> {
                        displayCurrentPoster(); // Display the updated poster
                        Toast.makeText(getContext(), "Custom poster uploaded successfully.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to upload custom poster.", Toast.LENGTH_SHORT).show();
                        Log.e("OrganizerMenuFragment", "Error uploading poster", e);
                    });
        }
        else{
            editEvent.setEventPosterURL(String.valueOf(imageUri));
            UniversalProgramValues.getInstance().queryEvent(editEvent.getEventId()).setEventPosterURL(String.valueOf(imageUri));
//            this.imageUrl
        }

    }


    /**
     * Displays the current poster image.
     * <p>
     * If there is a custom poster associated with the current event, it displays that poster.
     * Otherwise, the default placeholder image will be shown.
     */
    private void displayCurrentPoster() {
        if (editEvent == null) {
            eventPoster.setImageResource(R.drawable.placeholder_image_foreground);
            return;
        }

        String posterUrl = editEvent.getImageUrl();
        if (posterUrl == null || posterUrl.isEmpty()) {
            eventPoster.setImageResource(R.drawable.placeholder_image_foreground);
        } else {
            Picasso.get()
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_image_foreground)
                    .error(R.drawable.error_image_foreground)
                    .into(eventPoster);
        }
    }

    /**
     * Removes the custom poster for the current event and replaces it with the default poster.
     * <p>
     * Handles both production mode and testing mode scenarios.
     * <p>
     * In production, it removes the custom poster from Firebase, uploads the default poster,
     * and updates the display. In testing mode, it directly updates the local values.
     */
    private void removePoster() {
        if (editEvent == null) {
            Toast.makeText(getContext(), "Event not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!UniversalProgramValues.getInstance().getTestingMode()) {
            editEvent.deleteSelectedPosterFromFirebase(editEvent.getEventPosterURL())
                    .addOnSuccessListener(aVoid -> {
                        editEvent.uploadDefaultPoster(editEvent.getEventTitle())
                                .addOnSuccessListener(defaultPoster -> {
                                    displayCurrentPoster();
                                    Toast.makeText(getContext(), "Custom poster removed. Default poster is now active.", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Failed to reset poster to default.", Toast.LENGTH_SHORT).show();
                                    Log.e("OrganizerMenuFragment", "Error resetting poster to default", e);
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to remove custom poster.", Toast.LENGTH_SHORT).show();
                        Log.e("OrganizerMenuFragment", "Error removing poster", e);
                    });
        }
        else{
            editEvent.setEventPosterURL(editEvent.getDefaultEventPosterURL());
            UniversalProgramValues.getInstance().queryEvent(editEvent.getEventId()).setEventPosterURL(editEvent.getDefaultEventPosterURL());
        }
    }


    /**
     * Navigates to the OrganizerMenuFragment, which allows the event organizer to manage the event.
     */
    private void navigateToOrganizerMenu() {
        Log.d("Organizer Event Detail", "Navigate to menu");
        OrganizerMenuFragment organizerMenuFragment = OrganizerMenuFragment.newInstance(editEvent.getEventId());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, organizerMenuFragment)
                .addToBackStack(null)
                .commit();
    }

    private void goBackToOrganizerPage(){
        Log.d("Organizer Event Detail", "Navigate to menu");
        OrganizerMenuFragment organizerMenuFragment = OrganizerMenuFragment.newInstance(editEvent.getEventId());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, organizerMenuFragment)
                .addToBackStack(null)
                .commit();
    }


    /**
     * Displays the event details on the screen, such as title, description, and location.
     *
     * @param event The event object containing the event details to display.
     */
    private void displayEventDetails(Event event) {
        if (event == null) {
            Log.e("OrganizerEventDetail", "Event is null, cannot display details.");
            return;
        }

        editTitle.setText(event.getEventTitle());
        editDescription.setText(event.getDescription());
        editLocation.setText(event.getLocation());
        editMaxParticipants.setText(event.getMaxParticipants());
        geolocationSwitch.setChecked(event.isGeolocationRequired());
        displayCurrentPoster();

//        editMaxWaitlistSize.setText(event.get)
//        geolocationSwitch = view.findViewById(R.id.geolocation_switch);
//        eventPoster = view.findViewById(R.id.event_poster_image);
//
//        editButton = view.findViewById(R.id.button_edit_event);
//        uploadPosterButton = view.findViewById(R.id.button_upload_poster);
//        removePosterButton = view.findViewById(R.id.button_remove_poster);
//        saveButton = view.findViewById(R.id.button_save_event);
//        backButton = view.findViewById(R.id.button_back_home);
//        moveToMoreOrganizerDetail = view.findViewById(R.id.button_navigate_to_menu);
//
//        eventTitleText.setText(event.getEventTitle());
//        eventDescriptionText.setText(event.getDescription());
//        eventLocationText.setText(event.getLocation()); // Assuming a "location" field is available.

    }

    private void saveEventDetails() {
        editEvent.setEventTitle(editTitle.getText().toString().trim());
        editEvent.setDescription(editDescription.getText().toString().trim());
        editEvent.setLocation(editLocation.getText().toString().trim());
        editEvent.setMaxParticipants(Integer.parseInt(editMaxParticipants.getText().toString().trim()));
        editEvent.setGeolocationRequired(geolocationSwitch.isChecked());

        editEvent.saveEventDataToFirestore()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Event saved successfully.", Toast.LENGTH_SHORT).show();
                    requireActivity().onBackPressed();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to save event details.", Toast.LENGTH_SHORT).show());
    }


}
