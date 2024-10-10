package com.example.eventbooking;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_STORAGE = 112;
    private QRcodeGenerator qrGenerator;
    private ImageView qrCodeImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        Bitmap qrCode = QRcodeGenerator.generateQRCode("eventbooking://open");
//
//
//        Intent intent = getIntent();
//        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
//            Uri uri = intent.getData();
//            if (uri != null) {
//                // Handle the URI accordingly
//                String path = uri.getPath();
//                // Perform actions based on the path or query parameters
//            }
//        }

// Initialize views
        qrCodeImageView = findViewById(R.id.qr_code_image_view);

        if (qrCodeImageView == null) {
            Toast.makeText(this, "QR Code ImageView not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize QR code generator
        qrGenerator = new QRcodeGenerator(this);

        // Generate QR code bitmap
        Bitmap qrCodeBitmap = qrGenerator.generateQRCode("eventbooking://open");

        if (qrCodeBitmap != null) {
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
            // Save QR code bitmap to device (optional)
            qrGenerator.saveQRCode(qrCodeBitmap);
        } else {
            Toast.makeText(this, "Failed to generate QR code.", Toast.LENGTH_SHORT).show();
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with writing
            } else {
                // Permission denied, inform the user
                Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
            }
        }
    }


}