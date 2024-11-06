package com.example.eventbooking.waitinglist;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.R;
import com.example.eventbooking.Events.EventPageFragment.EventFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;
import java.util.List;

public class OrganizerMenuFragment extends Fragment {
    //initialize variables
    private static final String ARG_EVENT_ID = "event_id";
    private String eventId;
    private Button viewWaitingListButton;
    private Button sampleAttendeesButton;
    private Button viewCanceledListButton;
    private Button viewAcceptedListButton;
    private Button viewSignedListButton;
    private Button drawReplacementButton;
    private Button backToEventPageButton;
    private Button generateQRCode;
    private ImageView QRImage;
    private QRcodeGenerator qrCodeGenerator;
    private int replacementSize;
    private WaitingList waitingList;
    private int maxParticipant;
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
     * Called to initialize the fragment when it is created. Retrieves the `eventId` from the
     * fragment's arguments.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     *                           this is the state. This parameter is null when the fragment is
     *                           first created.
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve eventId from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
        }


        Log.e("Organizer", "Event found with ID: " + eventId);



        //hardcoded waitinglist participant
        int maxParticipants = 3;
        // Initialize the WaitingList instance as a placeholder
        waitingList = new WaitingList(eventId);
        //hardcode
        waitingList.setMaxParticipants(maxParticipants);

        //just for testing
        // For testing purposes, add hardcoded participant IDs
        waitingList.getWaitingParticipantIds().add("participant1");
        waitingList.getWaitingParticipantIds().add("participant2");
        waitingList.getWaitingParticipantIds().add("participant3");
        waitingList.getWaitingParticipantIds().add("participant4");
        waitingList.getWaitingParticipantIds().add("participant5");
        waitingList.getWaitingParticipantIds().add("participant6");

        //load and update from firebase operations
        waitingList.updateToFirebase().addOnSuccessListener(aVoid -> {
            // Data updated successfully
        }).addOnFailureListener(e -> {
            // Handle error
        });

        waitingList.loadFromFirebase().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Waiting list loaded successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to load waiting list from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * binding the UI component to the actual java var
     * set up listeners to trigger functions, interactions
     * @return rootview*/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_waiting_list, container, false);

        qrCodeGenerator = new QRcodeGenerator(getContext());

        // Initialize UI elements
        viewWaitingListButton = rootView.findViewById(R.id.waitingListButton);
        sampleAttendeesButton = rootView.findViewById(R.id.sampleAttendeesButton);
        viewSignedListButton = rootView.findViewById(R.id.SignedParticipantButton);
        viewCanceledListButton = rootView.findViewById(R.id.canceledParticipantButton);
        drawReplacementButton = rootView.findViewById(R.id.DrawReplacementButton);
        backToEventPageButton = rootView.findViewById(R.id.BackToEventButton);

        viewAcceptedListButton=rootView.findViewById(R.id.accptedParticipantButton);

        generateQRCode = rootView.findViewById(R.id.generate_qr_code);
        QRImage = rootView.findViewById(R.id.QR_image);


        // Set up listeners
        viewWaitingListButton.setOnClickListener(v -> navigateToViewWaitingList());
        viewCanceledListButton.setOnClickListener(v -> navigateToCanceledList());
        viewSignedListButton.setOnClickListener(v -> navigateToViewSignedList());
        viewAcceptedListButton.setOnClickListener(v->navigateToViewAcceptedList());
        sampleAttendeesButton.setOnClickListener(v -> sampleAttendees());
        drawReplacementButton.setOnClickListener(v -> drawReplacement(replacementSize));
        //backToEventPageButton.setOnClickListener(v -> navigateBackToEventPage());

        generateQRCode.setOnClickListener(v -> generateAndDisplayQRCode(eventId));

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
        //retrive the max participant and passed in to the sampleParticipants function
        int maxParticipants = waitingList.getMaxParticipants();
        List<String> selectedParticipants = waitingList.sampleParticipants(maxParticipants);
        //update the result into firebase and output message
        if (!selectedParticipants.isEmpty()) {
            Toast.makeText(getContext(), "Sampled attendees: " + selectedParticipants, Toast.LENGTH_SHORT).show();
            waitingList.updateToFirebase().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Sampled attendees updated to Firebase.", Toast.LENGTH_SHORT).show();
                    navigateToViewAcceptedList(); //jump to the accepted fragment to let organizer see result
                } else {
                    Toast.makeText(getContext(), "Failed to update Firebase with sampled attendees.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            //message for organizer
            Toast.makeText(getContext(), "No participants available to sample.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * pop up window, alert to let organizer input number
     * of partipant they would like to select from waiting list*/
    //This is for part 4, haven't worked yet !

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
    /**
     * function to trigger the draw replacement defined in waiting list
     * output message to organizer about the operation result */

    //belong to part 4, haven't fixed yet

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



    /**
     * This function will get the QR code associated with the event to be scanned and displayed when scanned
     * Once the QR code is generated then we display the QR code
     */
    private void generateAndDisplayQRCode(String eventID) {
        // URL to be encoded into the QR code (example URL with eventId)
        String eventUrl = "app://eventDetail?eventID=" + eventId;

        // Generate QR code using the QRcodeGenerator class
        Bitmap qrCodeBitmap = qrCodeGenerator.generateQRCode(eventUrl);

        if (qrCodeBitmap != null) {
            QRImage.setImageBitmap(qrCodeBitmap);

            qrCodeGenerator.saveQRCode(qrCodeBitmap, eventID);

            Toast.makeText(getContext(), "QR code generated and saved.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to generate QR code.", Toast.LENGTH_SHORT).show();
        }
    }



}
