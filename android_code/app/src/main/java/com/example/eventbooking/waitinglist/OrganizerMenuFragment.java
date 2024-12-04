package com.example.eventbooking.waitinglist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventCreate.EventEditFragment;
import com.example.eventbooking.Events.EventPageFragment.OragnizerEventFragment;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Notification;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.R;
import com.example.eventbooking.Events.EventPageFragment.EventFragment;
import com.example.eventbooking.Testing.EventMapFragment;
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.notification.MyNotificationManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.eventbooking.Events.EventData.Event;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

/**
 * The {@code OrganizerEventDetailFragment} class displays detailed information
 * about a specific event for organizers. It allows navigation to an organizer menu
 * for further management of the event.
 */
@SuppressWarnings("all")
public class OrganizerMenuFragment extends Fragment {
    //initialize variables
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    private Button viewWaitingListButton;
    private Button sampleAttendeesButton;
    private Button viewCanceledListButton;
    private Button viewAcceptedListButton;
    private Button viewSignedListButton;
    private Button drawReplacementButton;
    private Button backToButton;
    private Button generateQRCode;
    private Button eventMap;
    private ImageView QRImage;
    private ImageView posterImageView;
    private QRcodeGenerator qrCodeGenerator;
    private int replacementSize;
    private WaitingList waitingList;
    private int maxParticipant;
    private Button removePosterButton;
    private Button uploadPosterButton;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Event currentEvent = null;
    private Button CancelNonSignUp;
    private MyNotificationManager notificationManager;

    /**
     * empty constructor*/
    public OrganizerMenuFragment() {

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

    /**
     * Creates a new instance of OrganizerMenuFragment with the provided eventId.
     *
     * @param selectedEvent Parcelable event to be passed through
     * @return A new instance of OrganizerMenuFragment.
     */
    public static OrganizerMenuFragment newInstance(Event selectedEvent) {
        OrganizerMenuFragment fragment = new OrganizerMenuFragment();
        Bundle args = new Bundle();
        args.putParcelable("selectedEvent", selectedEvent);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Called to initialize the fragment when it is created. Retrieves the `eventId` from the
     * fragment's arguments.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                           this is the state. This parameter is null when the fragment is
     *                           first created.
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("Organizer Menu Fragment", "Launched Fragment");
        super.onCreate(savedInstanceState);
        // Retrieve eventId from arguments
        if (getArguments() != null) {
            if(getArguments().getString("eventId") != null)
                eventId = getArguments().getString("eventId");
            if(getArguments().getParcelable("selectedEvent") != null){
                currentEvent = getArguments().getParcelable("selectedEvent");
                eventId = currentEvent.getEventId();
            }
            Log.d("Organizer Menu Fragment", "Found Event ID: " + eventId);
        }
        if(currentEvent == null){
            if (eventId == null || eventId.isEmpty()) {
                Log.d("Organizer Menu Fragment", "Couldnt Find Event ID");
                Toast.makeText(getContext(), "Event ID is missing", Toast.LENGTH_SHORT).show();
                Log.e("OrganizerMenuFragment", "Event ID is null or empty.");
                getParentFragmentManager().popBackStack(); // Exit the fragment
                return;
            }
        }


        Log.e("Organizer", "Event found with ID: " + eventId);



        //hardcoded waitinglist participant
        //int maxParticipants = 3;
        // Initialize the WaitingList instance as a placeholder
        if(!UniversalProgramValues.getInstance().getTestingMode()) {
            if(currentEvent == null){
                Event.findEventById(eventId, event -> {
                    if (event != null) {
                        Log.d("Organizer Menu Fragment", "Loading Waiting List");
                        currentEvent = event;
                        waitingList = new WaitingList(eventId); // Initialize waitingList
                        waitingList.setMaxParticipants(event.getMaxParticipants());
                        Log.d("Organizer Menu Fragment", "Waiting list event ID: " + waitingList.getEventId());
                        Log.d("Organizer Menu Fragment", "Waiting list max: " + waitingList.getMaxParticipants());

                        waitingList.loadFromFirebase().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Waiting list loaded successfully.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to load waiting list from Firebase.", Toast.LENGTH_SHORT).show();
                            }
                        });


                    } else {
                        Log.d("Organizer Menu Fragment", "Waiting list not made");
                        Toast.makeText(getContext(), "Event not found.", Toast.LENGTH_SHORT).show();
                        getParentFragmentManager().popBackStack();
                    }
                }, e -> {
                    Log.d("Organizer Menu Fragment", "Error Waiting list not made");
                    Toast.makeText(getContext(), "Error fetching event data.", Toast.LENGTH_SHORT).show();
                    Log.e("OrganizerMenuFragment", "Error fetching event", e);
                    getParentFragmentManager().popBackStack();
                });
            }
            else{
                waitingList = new WaitingList(currentEvent.getEventId()); // Initialize waitingList
                waitingList.setMaxParticipants(currentEvent.getMaxParticipants());
                Log.d("Organizer Menu Fragment", "Waiting list event ID: " + waitingList.getEventId());
                Log.d("Organizer Menu Fragment", "Waiting list max: " + waitingList.getMaxParticipants());

                waitingList.loadFromFirebase().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Waiting list loaded successfully.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to load waiting list from Firebase.", Toast.LENGTH_SHORT).show();
                    }
                });
            }



            //just for testing
            pickImageLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                Uri selectedImageUri = data.getData();
                                uploadPoster(selectedImageUri);
                            }
                        }
                    }
            );

            notificationManager = new MyNotificationManager(FirebaseFirestore.getInstance());
        }
        else{
            currentEvent = UniversalProgramValues.getInstance().queryEvent(eventId);
            waitingList = new WaitingList(eventId); // Initialize waitingList
            waitingList.setMaxParticipants(UniversalProgramValues.getInstance().queryEvent(eventId).getMaxParticipants());
            waitingList.setWaitingParticipantIds(UniversalProgramValues.getInstance().queryEvent(eventId).getWaitingParticipantIds());
            waitingList.setAcceptedParticipantIds(UniversalProgramValues.getInstance().queryEvent(eventId).getAcceptedParticipantIds());
            waitingList.setSignedUpParticipantIds(UniversalProgramValues.getInstance().queryEvent(eventId).getSignedUpParticipantIds());
            waitingList.setCanceledParticipantIds(UniversalProgramValues.getInstance().queryEvent(eventId).getCanceledParticipantIds());
        }

    }
    /**
     * binding the UI component to the actual java var
     * set up listeners to trigger functions, interactions
     * @return rootview*/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_waiting_list, container, false);

        qrCodeGenerator = new QRcodeGenerator();

        // Initialize UI elements
        viewWaitingListButton = rootView.findViewById(R.id.waitingListButton);
        sampleAttendeesButton = rootView.findViewById(R.id.sampleAttendeesButton);
        viewSignedListButton = rootView.findViewById(R.id.SignedParticipantButton);
        viewCanceledListButton = rootView.findViewById(R.id.canceledParticipantButton);
        drawReplacementButton = rootView.findViewById(R.id.DrawReplacementButton);
        backToButton = rootView.findViewById(R.id.BackToButton);

        viewAcceptedListButton=rootView.findViewById(R.id.accptedParticipantButton);

        generateQRCode = rootView.findViewById(R.id.generate_qr_code);
        QRImage = rootView.findViewById(R.id.QR_image);
        CancelNonSignUp = rootView.findViewById(R.id.cancel_entrant);
        removePosterButton = rootView.findViewById(R.id.button_remove_poster);
        uploadPosterButton = rootView.findViewById(R.id.button_upload_poster);
        posterImageView = rootView.findViewById(R.id.poster_image_view);
        eventMap = rootView.findViewById(R.id.view_event_map);



        // Set up listeners
        viewWaitingListButton.setOnClickListener(v -> navigateToViewWaitingList());
        viewCanceledListButton.setOnClickListener(v -> navigateToCanceledList());
        viewSignedListButton.setOnClickListener(v -> navigateToViewSignedList());
        viewAcceptedListButton.setOnClickListener(v->navigateToViewAcceptedList());
        sampleAttendeesButton.setOnClickListener(v -> sampleAttendees());
        //drawReplacementButton.setOnClickListener(v -> drawReplacement(replacementSize));
        drawReplacementButton.setOnClickListener(v -> promptReplacementSize());
//        backToButton.setOnClickListener(v -> navigateBackToOrganizerPage());
        backToButton.setOnClickListener(v -> navigateBackToEventEdit());

        eventMap.setOnClickListener(v -> navigateToMapView());

        generateQRCode.setOnClickListener(v -> generateAndDisplayQRCode());
        CancelNonSignUp.setOnClickListener(v->cancelEntrant());
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            uploadPosterButton.setOnClickListener(v->launchImagePicker());
        }
        else{
            uploadPosterButton.setOnClickListener(v->launchTestImagePicker());
        }

        removePosterButton.setOnClickListener(v->removePoster());

        displayCurrentPoster();

        return rootView;
    }

    /**
     * Navigates to the ViewWaitingListFragment.
     * after click the button view waitinglist it will bring user to the new pages
     *
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
     * view of signed up list of current fragment
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
     * view the canceled list of user Id of current event, jump to new page
     */
    private void navigateToCanceledList() {
        ViewCanceledListFragment fragment = ViewCanceledListFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    /**
     * Navigate to see the user Ids who has been selected for current event */
    private void navigateToViewAcceptedList() {
        ViewAcceptedListFragment fragment = ViewAcceptedListFragment.newInstance(eventId);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
    /**
     * triggers the function defined in the waiting list class
     * should let organizers to draw winners selcted from the waiting list
     * after the result being drawned it will navigate to the accepted participant fragment
     * */

    private void sampleAttendees() {
        int maxParticipants = waitingList.getMaxParticipants();
        List<String> selectedParticipants = waitingList.sampleParticipants(maxParticipants);
        //update the result into firebase and output message
        if (!selectedParticipants.isEmpty() && !UniversalProgramValues.getInstance().getTestingMode()) {
            sampleAttendeesButton.setEnabled(false);
            Toast.makeText(getContext(), "Sampled attendees: " + selectedParticipants, Toast.LENGTH_SHORT).show();
            waitingList.updateToFirebase().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Notify selected participants
                    String notifText = "You have been selected for an event! Please sign up here.";
                    String notifTitle = "You were selected!";
                    for (String user : selectedParticipants) {
                        String eventId = currentEvent.getEventId();
                        String eventHash = currentEvent.getQRcodeHash();
                        Notification notification = new Notification(eventId, notifText, notifTitle, user);
                        notificationManager.createNotificationWithEventDetails(notification, eventId, eventHash, getActivity());
//                        Notification notif = new Notification(eventId, notifText, notifTitle, user);
//                        notificationManager.createNotification(notif);
//                        String eventUrl = "eventbooking://eventDetail?eventID=" + eventId + "?hash=" + currentEvent.getQRcodeHash();
//                        Notification notif = new Notification(eventId, notifText, notifTitle, user);
//                            notificationManager.createNotification(notif);
//                        notificationManager.createNotificationWithUrl(notif, eventUrl, getContext());
                    }

                    // Notify non-selected participants (those who were not chosen)
                    List<String> allParticipants = waitingList.getWaitingParticipantIds();  // Get all participants on the waiting list
                    allParticipants.removeAll(selectedParticipants);  // Remove selected participants to get the non-selected ones
                    String lossNotifText = "Unfortunately, you were not selected for the event this time.";
                    String lossNotifTitle = "You were not selected!";
                    for (String user : allParticipants) {
                        String eventId = currentEvent.getEventId();
                        String eventHash = currentEvent.getQRcodeHash();
                        Notification notification = new Notification(eventId, notifText, notifTitle, user);
                        notificationManager.createNotificationWithEventDetails(notification, eventId, eventHash, getActivity());
//                        Notification notif = new Notification(eventId, lossNotifText, lossNotifTitle, user);
//                        notificationManager.createNotification(notif);
//                        String eventUrl = "eventbooking://eventDetail?eventID=" + eventId + "?hash=" + currentEvent.getQRcodeHash();
//                        Notification notif = new Notification(eventId, notifText, notifTitle, user);
//                            notificationManager.createNotification(notif);
//                        notificationManager.createNotificationWithUrl(notif, eventUrl, getContext());
                    }

                    Toast.makeText(getContext(), "Sampled attendees updated to Firebase.", Toast.LENGTH_SHORT).show();
                    navigateToViewAcceptedList(); // Navigate to the accepted list fragment
                } else {
                    Toast.makeText(getContext(), "Failed to update Firebase with sampled attendees.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if(!selectedParticipants.isEmpty() && UniversalProgramValues.getInstance().getTestingMode()){
            sampleAttendeesButton.setEnabled(false);
            UniversalProgramValues.getInstance().updateEventWaitlist(eventId, waitingList);
        }
        else {
            //message for organizer
            Toast.makeText(getContext(), "No participants available to sample.", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * pop up window, alert to let organizer input number
     * of partipant they would like to select from waiting list*/


    private void promptReplacementSize() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter Replacement Size");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String replacementSizeStr = input.getText().toString().trim();
            if (!replacementSizeStr.isEmpty()) {
                try {
                    int replacementSize = Integer.parseInt(replacementSizeStr);

                    // Validate replacement size
                    int availableSpots = waitingList.getMaxParticipants() - waitingList.getSignedUpParticipantIds().size();
                    if (replacementSize <= 0) {
                        Toast.makeText(getContext(), "Replacement size must be greater than 0.", Toast.LENGTH_SHORT).show();
                    } else if (replacementSize > availableSpots) {
                        Toast.makeText(getContext(), "Replacement size exceeds available spots. Max: " + availableSpots, Toast.LENGTH_SHORT).show();
                    } else if (replacementSize > waitingList.getWaitingParticipantIds().size()) {
                        Toast.makeText(getContext(), "Not enough participants in the waiting list. Max: " + waitingList.getWaitingParticipantIds().size(), Toast.LENGTH_SHORT).show();
                    } else {
                        drawReplacement(replacementSize); // Pass validated size to drawReplacement
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Please enter a valid number.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Replacement size cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * function to trigger the draw replacement defined in waiting list
     * output message to organizer about the operation result */



    private void drawReplacement(int replacementSize) {
        List<String> replacements = waitingList.drawReplacement(replacementSize);

        if (!replacements.isEmpty()) {
            Toast.makeText(getContext(), "Replacements drawn: " + replacements, Toast.LENGTH_SHORT).show();
            if(!UniversalProgramValues.getInstance().getTestingMode()){
                waitingList.updateToFirebase().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Notify selected participants for replacement
                        String notifText = "You have been selected for an event! Please sign up here.";
                        String notifTitle = "You were selected!";
                        for (String user : replacements) {
                            String eventId = currentEvent.getEventId();
                            String eventHash = currentEvent.getQRcodeHash();
                            Log.d("Notification", "EventID: " + eventId);
                            Log.d("Notification", "Event Hash: " + eventId);
                            Log.d("Notification", "Noti Text: " + eventId);
                            Log.d("Notification", "Noti TItle: " + eventId);
                            Notification notification = new Notification(eventId, notifText, notifTitle, user);
                            notificationManager.createNotificationWithEventDetails(notification, eventId, eventHash, getActivity());
//                            String hashInput = eventId + Calendar.getInstance().getTime();
//                            String qrCodeHash = qrCodeGenerator.createQRCodeHash(hashInput);
//                            String eventUrl = "eventbooking://eventDetail?eventID=" + eventId + "?hash=" + currentEvent.getQRcodeHash();
//
//                            Notification notif = new Notification(eventId, notifText, notifTitle, user);
//                            notificationManager.createNotificationWithUrl(notif, eventUrl, getContext());
//                            notificationManager.createNotification(notif);
                        }
                        // Notify non-selected participants (those who were not chosen for replacement)
                        List<String> allParticipants = waitingList.getWaitingParticipantIds();  // Get all participants on the waiting list
                        allParticipants.removeAll(replacements);  // Remove selected replacements to get the non-selected ones
                        String lossNotifText = "Unfortunately, you were not selected for the event this time.";
                        String lossNotifTitle = "You were not selected!";
                        for (String user : allParticipants) {
                            String eventId = currentEvent.getEventId();
                            String eventHash = currentEvent.getQRcodeHash();
                            Notification notification = new Notification(eventId, notifText, notifTitle, user);
                            notificationManager.createNotificationWithEventDetails(notification, eventId, eventHash, getActivity());
//                            String eventUrl = "eventbooking://eventDetail?eventID=" + eventId + "?hash=" + currentEvent.getQRcodeHash();
//                            Notification notif = new Notification(eventId, lossNotifText, lossNotifTitle, user);
//                            notificationManager.createNotification(notif);
//                            notificationManager.createNotificationWithUrl(notif, eventUrl, getContext());
                        }
                        Toast.makeText(getContext(), "Replacement attendees updated to Firebase.", Toast.LENGTH_SHORT).show();
                        navigateToViewAcceptedList();
                    } else {
                        Toast.makeText(getContext(), "Failed to update Firebase with replacement attendees.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                UniversalProgramValues.getInstance().updateEventWaitlist(eventId, waitingList);
            }

        } else {
            Toast.makeText(getContext(), "No participants available for replacement.", Toast.LENGTH_SHORT).show();
        }
    }




    /**
     * This function will get the QR code associated with the event to be scanned and displayed when scanned
     * Once the QR code is generated then we display the QR code
     */
    private void generateAndDisplayQRCode() {
        // URL to be encoded into the QR code (example URL with eventId)
        String eventUrl = "eventbooking://eventDetail?eventID=" + currentEvent.getEventId() + "?hash=" + currentEvent.getQRcodeHash();

//        String eventUrl = "eventbooking://eventDetail?eventID=" + eventId;

        // Generate QR code using the QRcodeGenerator class
        Bitmap qrCodeBitmap = qrCodeGenerator.generateQRCode(eventUrl);

        if (qrCodeBitmap != null) {
            QRImage.setImageBitmap(qrCodeBitmap);

//            qrCodeGenerator.saveQRCode(qrCodeBitmap, currentEvent.getEventId());

            Toast.makeText(getContext(), "QR code generated and saved.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to generate QR code.", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * navigate back to the event list that was created by the current organizer
     */

    private void navigateBackToOrganizerPage(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OragnizerEventFragment())
                .addToBackStack(null)
                .commit();
    }


    /**
     * navigate back to the event list that was created by the current organizer
     */

    private void navigateBackToEventEdit(){
        EventEditFragment editEventFragment = EventEditFragment.newInstance(currentEvent);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editEventFragment)
                .addToBackStack(null)
                .commit();
    }
    /**
     * initialize the intent to let user choose images */
    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }
    /**
     * upload the poster the user has choose and display to let users see the format
     * @param imageUri
     * */

    private void uploadPoster(Uri imageUri) {
        if (currentEvent == null) {
            Toast.makeText(getContext(), "Event not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload the custom poster
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            currentEvent.uploadCustomPoster(imageUri)
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
            currentEvent.setEventPosterURL(String.valueOf(imageUri));
            UniversalProgramValues.getInstance().queryEvent(currentEvent.getEventId()).setEventPosterURL(String.valueOf(imageUri));
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
        if (currentEvent == null) {
            posterImageView.setImageResource(R.drawable.placeholder_image_foreground);
            return;
        }

        String posterUrl = currentEvent.getImageUrl();
        if (posterUrl == null || posterUrl.isEmpty()) {
            posterImageView.setImageResource(R.drawable.placeholder_image_foreground);
        } else {
            Picasso.get()
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_image_foreground)
                    .error(R.drawable.error_image_foreground)
                    .into(posterImageView);
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
        if (currentEvent == null) {
            Toast.makeText(getContext(), "Event not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!UniversalProgramValues.getInstance().getTestingMode()) {
            if(Objects.equals(currentEvent.getEventPosterURL(), currentEvent.getDefaultEventPosterURL())){
                currentEvent.deleteSelectedPosterFromFirebase(currentEvent.getImageUrl())
                        .addOnSuccessListener(aVoid -> {
                            currentEvent.uploadDefaultPoster(currentEvent.getEventTitle())
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
        }
        else{
            currentEvent.setEventPosterURL(currentEvent.getDefaultEventPosterURL());
            UniversalProgramValues.getInstance().queryEvent(currentEvent.getEventId()).setEventPosterURL(currentEvent.getDefaultEventPosterURL());
        }
    }

    /**
     * Cancels participation of accepted entrants who did not sign up for the event.
     * <p>
     * Moves all participants from the accepted list to the canceled list and sends notifications to affected users.
     * Updates Firebase in production mode or local data in testing mode.
     */
    private void cancelEntrant() {
        if (waitingList == null) {
            Toast.makeText(getContext(), "Waiting list not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> acceptedParticipants = waitingList.getAcceptedParticipantIds();
        if (acceptedParticipants.isEmpty()) {
            Toast.makeText(getContext(), "No non-signed-up participants to cancel.", Toast.LENGTH_SHORT).show();
            return;
        }

        waitingList.getCanceledParticipantIds().addAll(acceptedParticipants);
        acceptedParticipants.clear();

        if (!UniversalProgramValues.getInstance().getTestingMode()) {
            waitingList.updateToFirebase().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String notifText = "You have been removed from the accepted participant list.";
                    String notifTitle = "Removed from event list";
                    for (String user : waitingList.getCanceledParticipantIds()) {
                        String eventId = currentEvent.getEventId();
                        String eventHash = currentEvent.getQRcodeHash();
                        Notification notification = new Notification(eventId, notifText, notifTitle, user);
                        notificationManager.createNotificationWithEventDetails(notification, eventId, eventHash, getActivity());
//                        Notification notif = new Notification(eventId, notifText, notifTitle, user);
//                        notificationManager.createNotification(notif);
//                        String eventUrl = "eventbooking://eventDetail?eventID=" + eventId + "?hash=" + currentEvent.getQRcodeHash();
//                        Notification notif = new Notification(eventId, notifText, notifTitle, user);
//                        notificationManager.createNotification(notif);
//                        notificationManager.createNotificationWithUrl(notif, eventUrl, getContext());
                    }
                    Toast.makeText(getContext(), "Non-signed-up participants canceled successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to update Firebase.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            UniversalProgramValues.getInstance().updateEventWaitlist(eventId, waitingList);
        }
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

    private void navigateToMapView(){

        Log.d("Organizer Menu", "Nav to Map: " + currentEvent.getEventId());
        EventMapFragment eventMapFragment = EventMapFragment.newInstance(currentEvent);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, eventMapFragment)
                .addToBackStack(null) // Ensures returning to HomeFragment
                .commit();
//        Bundle bundle = new Bundle();
//        bundle.putString("eventId", "eventID5");
//        EventMapFragment eventMapFragment = new EventMapFragment();
////        eventMapFragment.setArguments(bundle);
//        getActivity().getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, new OragnizerEventFragment())
//                .addToBackStack(null)
//                .commit();
    }


}

