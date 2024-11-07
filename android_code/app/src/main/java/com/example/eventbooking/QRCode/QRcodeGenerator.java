package com.example.eventbooking.QRCode;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
/**
 * QRcodeGenerator is a utility class for generating and saving QR codes.
 * It uses the zxing library to generate QR codes in Bitmap format and provides methods
 * to save the generated QR code as a PNG file on the device's external storage.
 */
public class QRcodeGenerator {
    private Context context;

    /**
     * Constructs a QRcodeGenerator with the specified context.
     * @param context
     */
    public QRcodeGenerator(Context context) {
        this.context = context;
    }



    /**
     * Generates a QR code as a Bitmap based on the provided content string.
     *
     * @param content the content to encode into the QR code
     * @return a Bitmap containing the generated QR code, or null if an error occurs
     */
    public Bitmap generateQRCode(String content) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 300, 300);
            BarcodeEncoder encoder = new BarcodeEncoder();
            // Convert the BitMatrix to a Bitmap and return it
            return encoder.createBitmap(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if QR code generation fails
        }
    }

    /**
     * Saves the provided QR code Bitmap as a PNG file on the device's external storage.
     *
     * @param bitmap  the QR code Bitmap to save
     * @param eventID a unique event ID used to name the file
     */
    public void saveQRCode(Bitmap bitmap, String eventID) {
        // Define filename based on the event ID
        String filename = "eventbooking_qrcode-" + eventID + ".png";
        try {
            // Create file in the external files directory
            File file = new File(context.getExternalFilesDir(null), filename);
            OutputStream stream = new FileOutputStream(file);
            // Compress and write the Bitmap to the file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace(); // Handle I/O errors
        }
    }
}
