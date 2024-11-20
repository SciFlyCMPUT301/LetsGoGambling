package com.example.eventbooking.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventbooking.Events.EventView.EventViewActivity;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeActivity;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.firebase.FirestoreAccess;
import com.example.eventbooking.profile.ProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import android.provider.Settings;

public class LoginActivity extends AppCompatActivity {

    private TextView deviceIdText, welcomeText;
//    private BottomNavigationView nav;
//    private NavigationView sidebar;
    public static boolean isLoggedIn = false;
    private boolean isTestMode = false;
    private String eventIdFromQR;

    private Button testModeButton, normalButton;
    private LinearLayout testModeLayout, normalLoginLayout;
    private EditText documentIdInput, usernameInput;
    private Button setByDocumentIdButton, setByUsernameButton, setToDeviceID1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ///TODO: make this a seperate xml page, too lazy to do it right now
        setContentView(R.layout.activity_login);
//        setContentView(R.layout.fragment_login);
        Log.d("Login Activity", "Activity Launched");
        handleIntent(getIntent());
        initializeUI();



        testModeButton.setOnClickListener(v -> enterTestMode());
        normalButton.setOnClickListener(v -> startNormalMode());

        setByDocumentIdButton.setOnClickListener(v -> setUserByDocumentId());
        setByUsernameButton.setOnClickListener(v -> setUserByUsername());
        setToDeviceID1.setOnClickListener(v -> setUserToDeviceID1());
    }


    private void initializeUI() {
        // Testing mode buttons and views
        Log.d("Login Activity", "Initalizing UI");
        testModeButton = findViewById(R.id.button_test_mode);
        normalButton = findViewById(R.id.button_normal);
        testModeLayout = findViewById(R.id.test_mode_layout);
        normalLoginLayout = findViewById(R.id.normal_login_layout);
        documentIdInput = findViewById(R.id.input_document_id);
        usernameInput = findViewById(R.id.input_username);
        setByDocumentIdButton = findViewById(R.id.button_set_by_document_id);
        setByUsernameButton = findViewById(R.id.button_set_by_username);
        setToDeviceID1 = findViewById(R.id.button_set_to_deviceID1);
        // Setting the normal login fields
        deviceIdText = findViewById(R.id.text_login_deviceid);
        welcomeText = findViewById(R.id.text_login_welcome);
        // Make the normal and testing layouts invisible until button press
        normalLoginLayout.setVisibility(View.GONE);
        testModeLayout.setVisibility(View.GONE);
    }

    private void enterTestMode() {
        isTestMode = true;
        testModeLayout.setVisibility(View.VISIBLE);
        testModeButton.setVisibility(View.GONE);
        normalButton.setVisibility(View.GONE);
    }

    private void handleNewUser(String deviceId) {
        Log.d("Login Activity", "New user, no snapshot found.");
        welcomeText.setText("Welcome new user");

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("isNewUser", true);
        intent.putExtra("deviceId", deviceId);

        if (eventIdFromQR != null) {
            Log.d("Login Activity", "Profile with eventID");
            intent.putExtra("eventId", eventIdFromQR);
        }

        Log.d("Login Activity", "Profile finished intent adding");

        // Use postDelayed to mimic the delay, but move activity launch outside the Runnable
        new Handler().postDelayed(() -> {
            Log.d("Login Activity", "Handler executed, launching ProfileActivity");
            startActivity(intent);
            finish();
        }, 3000);

//        new Handler().postDelayed(() -> {
//            Log.d("Login Activity", "Handler called");
//            isLoggedIn = true;
//            Intent intent = new Intent(this, ProfileActivity.class);
//            intent.putExtra("isNewUser", true);
//            intent.putExtra("deviceId", deviceId);
//            if (eventIdFromQR != null) {
//                Log.d("Login Activity", "Profile with eventID");
//                intent.putExtra("eventId", eventIdFromQR);
//            }
//            Log.d("Login Activity", "Profile finshed intent adding");
//            startActivity(intent);
//            finish();
//
//        }, 3000);
    }



    private void handleReturningUser(User user, String deviceId) {
        Log.d("Login Activity", "Returning User: " + user.getUsername());
        UserManager.getInstance().setCurrentUser(user);
        Log.d("Login Activity", "Returning User, eventID: " + eventIdFromQR);
        new Handler().postDelayed(() -> {
            isLoggedIn = true;
            Intent intent;
            if (eventIdFromQR != null) {
                Log.d("Login Activity", "Event View Activity Launched Returning");
                intent = new Intent(this, EventViewActivity.class);
                intent.putExtra("eventId", eventIdFromQR);
                intent.putExtra("deviceId", deviceId);
                intent.putExtra("source_file", "LoginActivity");
            } else {
                Log.d("Login Activity", "Home Activity Launched Returning");
                intent = new Intent(this, HomeActivity.class);
                intent.putExtra("source_activity", "Login Activity Returning");
            }
            startActivity(intent);
            finish();
        }, 3000);

    }




    /**
     * The below two functions are intended on getting the QR code scanning outside of the app
     * and then passing the data inside of the QR code into the app such that we can use it.
     *
     * handleQRCodeScan gets the data and opens scannedFragment with the data being passed into it.
     * Future iterations should instead use bundles to enable more consistent loading across
     * other navigation pathways
     *
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
//        handleQRCodeScan(intent);
        handleIntent(intent);
    }



    /**
     * Handling of external links and QR code scanning from outside of the app
     *
     * @param intent
     */
    private void handleIntent(Intent intent) {
        boolean loggingin = false;
        if (intent != null && intent.getData() != null) {
            String url = intent.getData().toString();
            Log.d("Login Activity", "Incoming URL: " + url);

            eventIdFromQR = extractEventIdFromUrl(url);

            if (eventIdFromQR != null) {
                Log.d("Login Activity", "Event ID from QR code: " + eventIdFromQR);

                // If the user is already logged in, redirect immediately to ScannedFragment
                if (isLoggedIn) {
                    Log.d("Login Activity", "User logged in");

                    // Commenting this out for the time being so the buttons handle
                    // How we are choosing to navigate the app
//                    Log.d("Moving to Event", "Event: " + eventIdFromQR);
//                    Intent eventIntent = new Intent(this, EventViewActivity.class);
//                    eventIntent.putExtra("eventId", eventIdFromQR);
//                    ///TODO: Change this to be the singleton later
//                    eventIntent.putExtra("deviceId", "User1");
//                    startActivity(eventIntent);
                } else {
                    // Show login screen first
                    Log.d("Login Activity", "User not Logged in");
                }
            } else {
                Log.e("Login Activity", "No event ID found in URL");
            }
        }
        if (!isLoggedIn) {
//            eventIdFromQR = null;
        }
    }


    /**
     * Getting the eventID from the QR code URL string
     *
     * @param url
     * @return string
     */
    private String extractEventIdFromUrl(String url) {
        // Assuming the URL is in the format: eventbooking://eventDetail?eventID=12345
        String[] parts = url.split("eventID=");
        if (parts.length > 1) {
            return parts[1];
        }
        return null;
    }



    private void startNormalMode() {
        Log.d("Login Activity", "Normal Mode Pressed");
        isTestMode = false;
        // Hiding the buttons and ensuring test mode is gone (though it should never be visible)
        testModeButton.setVisibility(View.GONE);
        normalButton.setVisibility(View.GONE);
        testModeLayout.setVisibility(View.GONE);
        normalLoginLayout.setVisibility(View.VISIBLE);
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("Login Activity", "DeviceID: " + deviceId);
        deviceIdText.setText(deviceId);

        FirestoreAccess.getInstance().getUser(deviceId).addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                handleNewUser(deviceId);
            } else {
                User passingUser = snapshot.toObject(User.class);
                handleReturningUser(passingUser, deviceId);
            }
        }).addOnFailureListener(e -> Log.d("LoginActivity", "Firestore access failed: " + e.getMessage()));
    }

    private void setUserByDocumentId() {
        Log.d("Login Activity", "Setting User By ID");
        String documentId = documentIdInput.getText().toString().trim();
        if (documentId.isEmpty()) {
            Toast.makeText(this, "Please enter a Document ID", Toast.LENGTH_SHORT).show();
            return;
        }

        FirestoreAccess.getInstance().getUser(documentId).addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                User user = snapshot.toObject(User.class);
                UserManager.getInstance().setCurrentUser(user);
                Toast.makeText(this, "User set by Document ID", Toast.LENGTH_SHORT).show();
                testModeButton.setVisibility(View.GONE);
                normalButton.setVisibility(View.GONE);
                testModeLayout.setVisibility(View.GONE);
                normalLoginLayout.setVisibility(View.VISIBLE);
                deviceIdText.setText(user.getDeviceID());
                Log.d("Login Activity", "Setting By document ID existing deviceID");
                handleReturningUser(user, user.getDeviceID());
//                testModeLayout.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Document ID not found, make a new user", Toast.LENGTH_SHORT).show();
                testModeButton.setVisibility(View.GONE);
                normalButton.setVisibility(View.GONE);
                testModeLayout.setVisibility(View.GONE);
                normalLoginLayout.setVisibility(View.VISIBLE);
                deviceIdText.setText(documentId);
                Log.d("Login Activity", "Setting By document ID new deviceID");
                handleNewUser(documentId);
            }
        }).addOnFailureListener(e -> {
            Log.e("Login Activity", "Error fetching user by Document ID: " + e.getMessage());
            Toast.makeText(this, "Error fetching user", Toast.LENGTH_SHORT).show();
        });
    }

    private void setUserToDeviceID1(){
        Log.d("Login Activity", "Setting To deviceID1");
        FirestoreAccess.getInstance().getUser("deviceID1").addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                User user = snapshot.toObject(User.class);
                UserManager.getInstance().setCurrentUser(user);
                Toast.makeText(this, "User set by Document ID", Toast.LENGTH_SHORT).show();
                testModeButton.setVisibility(View.GONE);
                normalButton.setVisibility(View.GONE);
                testModeLayout.setVisibility(View.GONE);
                normalLoginLayout.setVisibility(View.VISIBLE);
                deviceIdText.setText(user.getDeviceID());
                handleReturningUser(user, user.getDeviceID());
            } else {
                Toast.makeText(this, "Document ID not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserByUsername() {
        Log.d("Login Activity", "Set User by Username");
        String username = usernameInput.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a Username", Toast.LENGTH_SHORT).show();
            return;
        }

        FirestoreAccess.getInstance().getUsersByUsername(username).addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                User user = querySnapshot.getDocuments().get(0).toObject(User.class);
                UserManager.getInstance().setCurrentUser(user);
                Toast.makeText(this, "User set by Username", Toast.LENGTH_SHORT).show();
                testModeButton.setVisibility(View.GONE);
                normalButton.setVisibility(View.GONE);
                testModeLayout.setVisibility(View.GONE);
                normalLoginLayout.setVisibility(View.VISIBLE);
                deviceIdText.setText(user.getDeviceID());
            } else {
                Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Log.e("Login Activity", "Error fetching user by Username: " + e.getMessage());
            Toast.makeText(this, "Error fetching user", Toast.LENGTH_SHORT).show();
        });
    }
}