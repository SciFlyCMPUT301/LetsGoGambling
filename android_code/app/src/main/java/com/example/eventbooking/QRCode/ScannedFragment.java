package com.example.eventbooking.QRCode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.eventbooking.R;

import org.w3c.dom.Text;
/**
 * ScannedFragment displays the result of a scanned QR code.
 * It shows the extracted event ID and provides a button to open the QR scanner again.
 */
public class ScannedFragment extends Fragment {
    private static final String ARG_EVENT_ID = "eventId";
    private String eventId;
    private TextView scanView;
    private Button scannerOpenButton;

    /**
     * Creates a new instance of ScannedFragment with the provided event ID.
     *
     * @param scannedData the scanned event ID to display in this fragment
     * @return a new instance of ScannedFragment
     */
    public static ScannedFragment newInstance(String scannedData) {
        ScannedFragment fragment = new ScannedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EVENT_ID, scannedData);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Initializes the fragment and retrieves the event ID from the arguments.
     *
     * @param savedInstanceState if non-null, this fragment is being re-constructed from a previous saved state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            eventId = getArguments().getString(ARG_EVENT_ID);
        }
    }
    /**
     * Inflates the fragment layout, initializes UI elements, and displays the scanned event ID.
     * Sets up a button to open the QR scanner.
     *
     * @param inflater           the LayoutInflater object used to inflate views in the fragment
     * @param container          the parent view that the fragment's UI should be attached to
     * @param savedInstanceState if non-null, this fragment is being re-constructed from a previous saved state
     * @return the root View for the fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrcode_scanned, container, false);
        // Display the scanned event ID in the TextView
        scanView = view.findViewById(R.id.scanned_value);
        scanView.setText(eventId);
        // Set up button to navigate back to the QR scanner
        scannerOpenButton = view.findViewById(R.id.scanQrBtn);
        scannerOpenButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CameraFragment())
                    .commit();
        });

        return view;
    }
}
