package com.example.eventbooking.QRCode;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.eventbooking.Login.LoginFragment;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.example.eventbooking.R;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
/**
 * CameraFragment is responsible for initiating a QR code scan using the device's camera.
 * Once a QR code is scanned, the fragment processes the result and navigates
 * to another fragment (ScannedFragment) with the extracted event ID.
 */
public class CameraFragment extends Fragment {
    private static final int REQUEST_CODE_SCAN = 100;

    /**
     * Inflates the fragment layout and sets up the QR scan button.
     * When the scan button is clicked, it initiates a QR code scan.
     *
     * @param inflater           the LayoutInflater object used to inflate views in the fragment
     * @param container          the parent view that the fragment's UI should be attached to
     * @param savedInstanceState if non-null, this fragment is being re-constructed from a previous saved state
     * @return the root View for the fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        // Initialize the scan button to trigger QR code scanning
        Button scanButton = view.findViewById(R.id.button_scan);
        scanButton.setOnClickListener(v -> {
            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Scan a QR code");
            integrator.setCameraId(0); // Use a specific camera of the device
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(true);
            integrator.initiateScan();
        });

        return view;
    }
    /**
     * Handles the result of the QR code scan.
     * If a QR code is successfully scanned, it extracts the event ID from the scanned data
     * and navigates to the ScannedFragment with the event ID.
     *
     * @param requestCode the integer request code originally supplied to startActivityForResult
     * @param resultCode  the integer result code returned by the child activity
     * @param data        an Intent that carries result data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Process the scan result
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // Successfully scanned QR code; extract event ID and navigate
                String scannedData = result.getContents();
                String eventId = extractEventIdFromQR(scannedData);
                navigateToScannedFragment(eventId);

            } else {
                Toast.makeText(getActivity(), "Scan failed or canceled", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    /**
     * Extracts the event ID from the scanned QR code data.
     * Assumes the QR code contains a URL with the parameter "eventID".
     *
     * @param scannedData the string data retrieved from the scanned QR code
     * @return the extracted event ID, or null if not found
     */
    private String extractEventIdFromQR(String scannedData) {
        // Assuming the QR code contains a URL like app://eventDetail?eventID=12345
        String[] parts = scannedData.split("eventID=");
        if (parts.length > 1) {
            return parts[1];
        }
        return null;
    }
    /**
     * Navigates to ScannedFragment with the provided event ID.
     * Replaces the current fragment in the container with ScannedFragment.
     *
     * @param eventId the event ID to pass to ScannedFragment
     */
    public void navigateToScannedFragment(String eventId) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ScannedFragment scannedFragment = ScannedFragment.newInstance(eventId);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, scannedFragment)
                .commit();
    }
}

