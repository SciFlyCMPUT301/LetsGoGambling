package com.example.eventbooking.QRCode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.eventbooking.R;

import java.security.MessageDigest;

// Garbage code, will fix this later
/**
 * QRCodeEventGenerate is a Fragment that allows users to input text to generate a QR code.
 * The generated QR code is displayed within the fragment.
 * The QR code can also be saved for later use if needed.
 */
public class QRCodeEventGenerate extends Fragment {
    private QRcodeGenerator qrCodeGenerator;
    /**
     * Inflates the fragment layout, initializes UI elements, and sets up the QR code generator.
     *
     * @param inflater           the LayoutInflater object used to inflate views in the fragment
     * @param container          the parent view that the fragment's UI should be attached to
     * @param savedInstanceState if non-null, this fragment is being re-constructed from a previous saved state
     * @return the root View for the fragment's layout
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qr_code_event_generate, container, false);
        // Initialize the QR code generator with the context
        qrCodeGenerator = new QRcodeGenerator(getContext());
        // Initialize UI components
        Button generateButton = view.findViewById(R.id.button_generate);
        EditText inputField = view.findViewById(R.id.edit_text_input);

        generateButton.setOnClickListener(v -> {
            String inputText = inputField.getText().toString();
            Bitmap qrBitmap = qrCodeGenerator.generateQRCode(inputText);
//            qrCodeGenerator.saveQRCode(qrBitmap); // Save the QR code if needed

            ImageView qrCodeImageView = view.findViewById(R.id.image_qrcode);
            qrCodeImageView.setImageBitmap(qrBitmap);
        });

        return view;
    }

    /**
     * Generates a SHA-256 hash for a given input string.
     * This method is used for hashing input data, which can be useful for unique QR code generation.
     *
     * @param input the input string to hash
     * @return the SHA-256 hash of the input as a hexadecimal string, or null if an error occurs
     */
    private String generateHash(String input) {
        try {
            // Initialize SHA-256 MessageDigest
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            // Convert bytes to hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}