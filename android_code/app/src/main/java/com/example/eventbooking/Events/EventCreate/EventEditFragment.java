package com.example.eventbooking.Events.EventCreate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.R;

public class EventEditFragment extends Fragment {
    private EditText editTitle, editDescription, editLocation, editMaxParticipants, editMaxWaitlistSize;
    private Switch geolocationSwitch;
    private Button editButton, uploadPosterButton, removePosterButton, saveButton, backButton;
    private ImageView eventPoster;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Uri selectedImageUri;
    private boolean isEditing = false;


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

        editButton.setOnClickListener(v -> toggleEditMode());
        uploadPosterButton.setOnClickListener(v -> uploadPoster());
        removePosterButton.setOnClickListener(v -> removePoster());
        saveButton.setOnClickListener(v -> saveEventDetails());
        backButton.setOnClickListener(v -> goBackToHome());

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
}
