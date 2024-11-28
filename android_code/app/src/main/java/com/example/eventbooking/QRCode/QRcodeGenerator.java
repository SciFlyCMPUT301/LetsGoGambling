package com.example.eventbooking.QRCode;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Map;

import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import org.json.JSONObject;

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

    public QRcodeGenerator() {

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

    public String createQRCodeHash(String textToHash){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(textToHash.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString(); // Return the hex string representation of the hash
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Return null if an error occurs during hashing
        }


    }


    public static Bitmap generateAndSendBackQRCode(String eventId){
        Log.d("QR Code Generator", "Generate and Save: " + eventId);
        QRcodeGenerator temp = new QRcodeGenerator();
        // URL to be encoded into the QR code (example URL with eventId)
        String hashInput = eventId + Calendar.getInstance().getTime();
        String qrCodeHash = temp.createQRCodeHash(hashInput);
        String eventUrl = "eventbooking://eventDetail?eventID=" + eventId + "?hash=" + qrCodeHash;
//        String eventUrl = "eventbooking://eventDetail?eventID=" + event;

        // Generate QR code using the QRcodeGenerator class
        Log.d("QR Code Generator", "Generate and Save URL: " + eventUrl);
        Bitmap qrCodeBitmap = temp.generateQRCode(eventUrl);

        if (qrCodeBitmap != null) {
            Log.d("QR Code Generator", "Got Bit Map");
            return qrCodeBitmap;
//            QRCode.setImageBitmap(qrCodeBitmap);
//            qrCodeGenerator.saveQRCode(qrCodeBitmap, selectedEvent.getEventId());

        } else {
            return null;
        }

    }





}
