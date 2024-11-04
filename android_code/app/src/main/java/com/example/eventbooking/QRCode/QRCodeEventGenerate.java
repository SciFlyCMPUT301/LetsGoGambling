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
public class QRCodeEventGenerate extends Fragment {
    private QRcodeGenerator qrCodeGenerator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.qr_code_event_generate, container, false);

        qrCodeGenerator = new QRcodeGenerator(getContext());
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


    private String generateHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
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