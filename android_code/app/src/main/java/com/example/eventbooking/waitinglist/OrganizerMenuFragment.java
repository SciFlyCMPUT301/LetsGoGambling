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

import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.R;
import com.example.eventbooking.Events.EventPageFragment.EventFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.example.eventbooking.Events.EventData.Event;
import com.squareup.picasso.Picasso;

import java.util.Hashtable;
import java.util.List;

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
    private Button backToEventPageButton;
    private Button generateQRCode;
    private ImageView QRImage;
    private ImageView posterImageView;
    private QRcodeGenerator qrCodeGenerator;
    private int replacementSize;
    private WaitingList waitingList;
    private int maxParticipant;
    private Button removePosterButton;
    private Button uploadPosterButton;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private Event currentEvent;
    private Button CancelNonSignUp;
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
        Log.d("Organizer Menu Fragment", "Launched Fragment");
        super.onCreate(savedInstanceState);
        // Retrieve eventId from arguments
        if (getArguments() != null) {
            eventId = getArguments().getString("eventId");
            Log.d("Organizer Menu Fragment", "Found Event ID: " + eventId);
        }
        if (eventId == null || eventId.isEmpty()) {
            Log.d("Organizer Menu Fragment", "Couldnt Find Event ID");
            Toast.makeText(getContext(), "Event ID is missing", Toast.LENGTH_SHORT).show();
            Log.e("OrganizerMenuFragment", "Event ID is null or empty.");
            getParentFragmentManager().popBackStack(); // Exit the fragment
            return;
        }


        Log.e("Organizer", "Event found with ID: " + eventId);



        //hardcoded waitinglist participant
        //int maxParticipants = 3;
        // Initialize the WaitingList instance as a placeholder
        Event.findEventById(eventId, event -> {
            if (event != null) {
                Log.d("Organizer Menu Fragment", "Loading Waiting List");
                currentEvent = event;
                waitingList = new WaitingList(eventId); // Initialize waitingList
                waitingList.setMaxParticipants(event.getMaxParticipants());
                Log.d("Organizer Menu Fragment", "Waiting list event ID: " + waitingList.getEventId());
                Log.d("Organizer Menu Fragment", "Waiting list max: " + waitingList.getMaxParticipants());

                // Update to Firebase only if waitingList is initialized
                waitingList.updateToFirebase().addOnSuccessListener(aVoid -> {
                    // Data updated successfully
                }).addOnFailureListener(e -> {
                    Log.e("OrganizerMenuFragment", "Failed to update waiting list to Firebase", e);
                });

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


        // waitingList = new WaitingList(eventId);
        //hardcode
        //waitingList.setMaxParticipants(maxParticipants);

        //just for testing
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result ->{
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Intent data = result.getData();
                        if(data != null && data.getData() != null){
                            Uri selectedImageUri = data.getData();
                            uploadPoster(selectedImageUri);
                        }
                    }
                }
        );

        // For testing purposes, add hardcoded participant IDs
//        waitingList.getWaitingParticipantIds().add("participant1");
//        waitingList.getWaitingParticipantIds().add("participant2");
//        waitingList.getWaitingParticipantIds().add("participant3");
//        waitingList.getWaitingParticipantIds().add("participant4");
//        waitingList.getWaitingParticipantIds().add("participant5");
//        waitingList.getWaitingParticipantIds().add("participant6");

//        if (waitingList != null) {
//            waitingList.updateToFirebase().addOnSuccessListener(aVoid -> {
//                // Data updated successfully
//            }).addOnFailureListener(e -> {
//                Log.e("OrganizerMenuFragment", "Failed to update waiting list to Firebase", e);
//            });
//
//            waitingList.loadFromFirebase().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Toast.makeText(getContext(), "Waiting list loaded successfully.", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getContext(), "Failed to load waiting list from Firebase.", Toast.LENGTH_SHORT).show();
//                }
//            });
//        } else {
//            Log.e("OrganizerMenuFragment", "WaitingList is null. Cannot update or load.");
//        }


        //load and update from firebase operations
//        waitingList.updateToFirebase().addOnSuccessListener(aVoid -> {
//            // Data updated successfully
//        }).addOnFailureListener(e -> {
//            // Handle error
//        });

//        waitingList.loadFromFirebase().addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                Toast.makeText(getContext(), "Waiting list loaded successfully.", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getContext(), "Failed to load waiting list from Firebase.", Toast.LENGTH_SHORT).show();
//            }
//        });
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
        CancelNonSignUp = rootView.findViewById(R.id.cancel_entrant);
        removePosterButton = rootView.findViewById(R.id.button_remove_poster);
        uploadPosterButton = rootView.findViewById(R.id.button_upload_poster);
        posterImageView = rootView.findViewById(R.id.poster_image_view);




        // Set up listeners
        viewWaitingListButton.setOnClickListener(v -> navigateToViewWaitingList());
        viewCanceledListButton.setOnClickListener(v -> navigateToCanceledList());
        viewSignedListButton.setOnClickListener(v -> navigateToViewSignedList());
        viewAcceptedListButton.setOnClickListener(v->navigateToViewAcceptedList());
        sampleAttendeesButton.setOnClickListener(v -> sampleAttendees());
        drawReplacementButton.setOnClickListener(v -> drawReplacement(replacementSize));
        backToEventPageButton.setOnClickListener(v -> navigateBackToEventPage());

        generateQRCode.setOnClickListener(v -> generateAndDisplayQRCode(eventId));
        CancelNonSignUp.setOnClickListener(v->cancelEntrant());
        uploadPosterButton.setOnClickListener(v->launchImagePicker());
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

    private void navigateBackToEventPage(){
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new EventFragment())
                .addToBackStack(null)
                .commit();

    }
    //poster stuff
    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void uploadPoster(Uri imageUri) {
        if (currentEvent == null) {
            Toast.makeText(getContext(), "Event not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Upload the custom poster
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
    private void removePoster() {
        if (currentEvent == null) {
            Toast.makeText(getContext(), "Event not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove the custom poster and switch to default
        currentEvent.deleteSelectedPosterFromFirebase(currentEvent.getImageUrl())
                .addOnSuccessListener(aVoid -> {
                    currentEvent.uploadDefaultPoster(currentEvent.getEventTitle())
                            .addOnSuccessListener(defaultPoster -> {
                                displayCurrentPoster(); // Display the default poster
                                Toast.makeText(getContext(), "Custom poster removed. Default poster is now active.", Toast.LENGTH_SHORT).show();
                            }) .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to reset poster to default.", Toast.LENGTH_SHORT).show();
                                Log.e("OrganizerMenuFragment", "Error resetting poster to default", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to remove custom poster.", Toast.LENGTH_SHORT).show();
                    Log.e("OrganizerMenuFragment", "Error removing poster", e);
                });
    }




















    //cancel entrant didnot signup
    //make up a click listener, retrive the accepted list, remove them and add to the cancelled
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


        // Move all accepted participants to the canceled list
        waitingList.getCanceledParticipantIds().addAll(acceptedParticipants);


        // Clear the accepted participants list
        acceptedParticipants.clear();


        // Update Firebase
        waitingList.updateToFirebase().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Non-signed-up participants canceled successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }






}
