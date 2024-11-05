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

public class ScannedFragment extends Fragment {
    private static final String ARG_SCANNED_DATA = "scanned_data";
    private String scannedData;
    private TextView scanView;
    private Button scannerOpenButton;

    public static ScannedFragment newInstance(String scannedData) {
        ScannedFragment fragment = new ScannedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SCANNED_DATA, scannedData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String scannedData = getArguments().getString(ARG_SCANNED_DATA);
            // Use the scanned data as needed
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrcode_scanned, container, false);

        scanView = view.findViewById(R.id.scanned_value);
        scanView.setText(scannedData);

        // Opens the scanned app with some functionality
        scannerOpenButton = view.findViewById(R.id.scanQrBtn);
        scannerOpenButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new CameraFragment())
                    .commit();
        });

        return view;
    }
}
