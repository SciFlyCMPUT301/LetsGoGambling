package com.example.eventbooking.Admin.Event;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.R;
import com.example.eventbooking.UniversalProgramValues;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
/**
 * Fragment for displaying and managing QR codes associated with a specific event.
 * <p>
 * This fragment allows the administrator to view the QR code details of an event, generate a new QR code,
 * and navigate back to the event edit screen. It uses a custom QR code generator to display or update QR codes.
 */

public class QRCodeFragment extends Fragment {
    private FirebaseFirestore db;
    private Button eventGoBack, removeButton;
    private ArrayList<Event> qrcodeList;
    private ListView qrcodeListView;
    private Event selectedQRcode = null;
    private Event choosenEvent;
    private ImageView QRcode;
    private QRcodeGenerator qrCodeGenerator;

    /**
     * Constructor for QRCodeFragment.
     *
     * @param selectedEvent The event for which the QR code details will be displayed.
     */

    public QRCodeFragment(Event selectedEvent) {
        this.choosenEvent = selectedEvent;
    }
    /**
     * Called to create the fragment's view hierarchy.
     *
     * @param inflater  The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view to which the fragment's UI should be attached.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for the QR code details fragment
        View view = inflater.inflate(R.layout.fragment_event_qr_code_details, container, false);
        if(!UniversalProgramValues.getInstance().getTestingMode())
            db = FirebaseFirestore.getInstance();
        eventGoBack = view.findViewById(R.id.admin_go_back_event);
        removeButton = view.findViewById(R.id.remove_qrcode);
        QRcode  = view.findViewById(R.id.qrcode_image_view);
        // Display the QR code for the chosen event
        QRcode.setImageBitmap(qrCodeGenerator.generateAndSendBackQRCode(choosenEvent.getEventId()));
        // Load QR codes from Firebase
        // Initialize TextViews for displaying event details
        TextView eventIDTextView = view.findViewById(R.id.qrcode_event_id);
        TextView hashedqrcodeTextView = view.findViewById(R.id.hashed_qrcode_event);

        // Bind event data to UI components
        eventIDTextView.setText(choosenEvent.getEventId());
        hashedqrcodeTextView.setText(choosenEvent.getQRcodeHash());
        // Set up the "Go Back" button to navigate to the event edit screen
        eventGoBack.setOnClickListener(v -> {
            EditEventFragment detailFragment = new EditEventFragment(choosenEvent);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });


        // Handle "Remove" button click to regenerate the QR code
        removeButton.setOnClickListener(v -> {
            // Generate a new QR code hash for the event and update the UI
                choosenEvent.getNewEventQRHash();
                QRcode.setImageBitmap(qrCodeGenerator.generateAndSendBackQRCode(choosenEvent.getEventId()));
                hashedqrcodeTextView.setText(choosenEvent.getQRcodeHash());
        });
        return view;
    }

}
