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

public class CameraFragment extends Fragment {
    private static final int REQUEST_CODE_SCAN = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {

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

    private String extractEventIdFromQR(String scannedData) {
        // Assuming the QR code contains a URL like app://eventDetail?eventID=12345
        String[] parts = scannedData.split("eventID=");
        if (parts.length > 1) {
            return parts[1];
        }
        return null;
    }

    public void navigateToScannedFragment(String eventId) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ScannedFragment scannedFragment = ScannedFragment.newInstance(eventId);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, scannedFragment)
                .commit();
    }
}

