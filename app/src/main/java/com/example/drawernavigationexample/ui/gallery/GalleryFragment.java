package com.example.drawernavigationexample.ui.gallery;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.drawernavigationexample.QRcodeGenerator;
import com.example.drawernavigationexample.R;
//import com.example.drawernavigationexample.utils.QRCodeGenerator;

public class GalleryFragment extends Fragment {

    private Spinner fragmentSelectorSpinner;
    private Button generateCodeButton;
    private ImageView qrCodeImageView;
    private String selectedFragment;
    private QRcodeGenerator qrGenerator;
    private TextView additionalInfoTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        // Initialize UI elements
        fragmentSelectorSpinner = root.findViewById(R.id.fragment_selector_spinner);
        generateCodeButton = root.findViewById(R.id.generate_code_button);
        qrCodeImageView = root.findViewById(R.id.qr_code_image_view);

        // Set up the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.fragment_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fragmentSelectorSpinner.setAdapter(adapter);

        // Handle Spinner selection
        fragmentSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedFragment = parent.getItemAtPosition(position).toString().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedFragment = "home"; // Default selection
            }
        });

        // Set default selected fragment
        selectedFragment = fragmentSelectorSpinner.getSelectedItem().toString().toLowerCase();
        String Additional_information = "&additionalinfo=100347373";
        // Handle Generate Code button click
        generateCodeButton.setOnClickListener(v -> {
            String deepLink = "yourapp://open?fragment=" + selectedFragment+ Additional_information;
            qrGenerator = new QRcodeGenerator(getContext());
            Bitmap qrCodeBitmap = qrGenerator.generateQRCode(deepLink);

            if (qrCodeBitmap != null) {
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
            } else {
                // Handle QR code generation failure
            }
        });


        additionalInfoTextView = root.findViewById(R.id.additional_info_text_view_gallery);
        Bundle arguments = getArguments();
        if (arguments != null) {
            int additionalInfo = arguments.getInt("additionalInfo", -1); // Default to -1 if not found
            if (additionalInfo != -1) {
                // Display the additional information
                additionalInfoTextView.setText("Additional Info: " + additionalInfo);
            }
        }


        return root;
    }
}
