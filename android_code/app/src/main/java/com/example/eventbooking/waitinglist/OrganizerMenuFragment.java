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
            eventId = getArguments().getString("eventId");
        }
        Log.e("Organizer", "Event found with ID: " + eventId);


        // Initialize the WaitingList instance as a placeholder
        waitingList = new WaitingList(eventId);

        //just for testing
        // For testing purposes, add hardcoded participant IDs
        waitingList.getWaitingParticipantIds().add("participant1");
        waitingList.getWaitingParticipantIds().add("participant2");

// Update to Firebase (if necessary)
        waitingList.updateToFirebase().addOnSuccessListener(aVoid -> {
            // Data updated successfully
        }).addOnFailureListener(e -> {
            // Handle error
        });


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

        qrCodeGenerator = new QRcodeGenerator(getContext());

        // Initialize UI elements
        viewWaitingListButton = rootView.findViewById(R.id.waitingListButton);
        sampleAttendeesButton = rootView.findViewById(R.id.sampleAttendeesButton);
        viewSignedListButton = rootView.findViewById(R.id.SignedParticipantButton);
        viewCanceledListButton = rootView.findViewById(R.id.canceledParticipantButton);
        drawReplacementButton = rootView.findViewById(R.id.DrawReplacementButton);
        backToEventPageButton = rootView.findViewById(R.id.BackToEventButton);
        generateQRCode = rootView.findViewById(R.id.generate_qr_code);
        QRImage = rootView.findViewById(R.id.QR_image);

        // Set up listeners
        viewWaitingListButton.setOnClickListener(v -> navigateToViewWaitingList());
        viewCanceledListButton.setOnClickListener(v -> navigateToCanceledList());
        viewSignedListButton.setOnClickListener(v -> navigateToViewSignedList());
        sampleAttendeesButton.setOnClickListener(v -> sampleAttendees());
        drawReplacementButton.setOnClickListener(v -> drawReplacement(replacementSize));
        //backToEventPageButton.setOnClickListener(v -> navigateBackToEventPage());

        generateQRCode.setOnClickListener(v -> generateAndDisplayQRCode(eventId));

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


    /**
     * This function will get the QR code associated with the event to be scanned and displayed when scanned
     * Once the QR code is generated then we display the QR code
     */
    private void generateAndDisplayQRCode(String eventID) {
        // URL to be encoded into the QR code (example URL with eventId)
        String eventUrl = "eventbooking://eventDetail?eventID=" + eventId;

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
