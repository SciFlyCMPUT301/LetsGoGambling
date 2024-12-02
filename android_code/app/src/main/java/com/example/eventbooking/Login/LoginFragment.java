package com.example.eventbooking.Login;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.MainActivity;
import com.example.eventbooking.R;
import com.example.eventbooking.Role;
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.firebase.FirestoreAccess;
import com.example.eventbooking.notification.MyNotificationManager;
import com.example.eventbooking.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment responsible for managing the login process.
 * It handles user authentication, device ID handling, and navigation based on user status.
 * It also allows test mode for easier testing scenarios.
 */
public class LoginFragment extends Fragment {

    private TextView deviceIdText, welcomeText;
    private BottomNavigationView nav;
    private NavigationView sidebar;
    public static boolean isLoggedIn = false;
    private boolean isTestMode = false;
    private boolean UITestMode = false;
    private String deviceId;
    private String eventIdFromQR;

    private Button testModeButton, normalButton;
    private LinearLayout testModeLayout, normalLoginLayout;
    private EditText documentIdInput, usernameInput;
    private Button setByDocumentIdButton, setByUsernameButton, setToDeviceID1;

    private Handler handler;
    /**
     * Sets the event ID for QR Code based navigation.
     * @param eventId The event ID from the QR code scan.
     */
    public void setEventId(String eventId) {
        this.eventIdFromQR = eventId;
    }
    /**
     * Sets the UI test mode flag.
     * @param UITestModeOption The flag indicating whether the UI should be in test mode.
     */
    public void setUITestMode(Boolean UITestModeOption) {
        this.UITestMode = UITestModeOption;
    }

    /**
     * Creates a new instance of LoginFragment with the provided event ID.
     * @param eventIdFromQR The event ID obtained from a QR code.
     * @return A new instance of LoginFragment.
     */
    public static LoginFragment newInstance(String eventIdFromQR) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString("eventID", eventIdFromQR);
        fragment.setArguments(args);
        return fragment;
    }
    /**
     * Initializes the UI elements and sets up event listeners for login buttons.
     * It also checks if the app is running in test mode or normal mode.
     * @param inflater The LayoutInflater object to inflate the view.
     * @param container The container for the fragment's view.
     * @param savedInstanceState Saved state from a previous instance.
     * @return The root view of the login fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Login Fragment", "Login Fragment Launch");
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ((MainActivity) getActivity()).hideNavigationUI();
        Log.d("Login Fragment", "Hid Main Activity UI");
        Log.d("Login Fragment", "Create View Testing: " + UniversalProgramValues.getInstance().getTestingMode());

        initializeUI(rootView);
        Log.d("LoginFragment", "Initalized Login UI Main Create");
        testModeButton.setOnClickListener(v -> enterTestMode());
        normalButton.setOnClickListener(v -> startNormalMode());
        setByDocumentIdButton.setOnClickListener(v -> setUserByDocumentId());
        setByUsernameButton.setOnClickListener(v -> setUserByUsername());
        setToDeviceID1.setOnClickListener(v -> setUserToDeviceID1());
        Log.d("LoginFragment", "Buttons Set up");

        return rootView;
    }
    /**
     * Initializes UI components based on whether the app is in test mode or normal mode.
     * @param rootView The root view of the fragment.
     */
    private void initializeUI(View rootView) {
        isTestMode = UniversalProgramValues.getInstance().getTestingMode();
        if(isTestMode){
            deviceId = UniversalProgramValues.getInstance().getDeviceID();
        }
        else{
            deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        if(getArguments() != null){
            if(getArguments().getString("eventID") != null)
                eventIdFromQR = getArguments().getString("eventID");
        }

        Log.d("LoginFragment", "Initializing UI");
        Log.d("LoginFragment", "Is Testing: " + UniversalProgramValues.getInstance().getTestingMode());
        testModeButton = rootView.findViewById(R.id.button_test_mode);
        normalButton = rootView.findViewById(R.id.button_normal);
        testModeLayout = rootView.findViewById(R.id.test_mode_layout);
        normalLoginLayout = rootView.findViewById(R.id.normal_login_layout);
        documentIdInput = rootView.findViewById(R.id.input_document_id);
        usernameInput = rootView.findViewById(R.id.input_username);
        setByDocumentIdButton = rootView.findViewById(R.id.button_set_by_document_id);
        setByUsernameButton = rootView.findViewById(R.id.button_set_by_username);
        setToDeviceID1 = rootView.findViewById(R.id.button_set_to_deviceID1);
        deviceIdText = rootView.findViewById(R.id.text_login_deviceid);
        welcomeText = rootView.findViewById(R.id.text_login_welcome);
        normalLoginLayout.setVisibility(View.GONE);
        testModeLayout.setVisibility(View.GONE);
        Log.d("LoginFragment", "Initalized Login UI");
    }
    /**
     * Enables test mode and shows the test mode UI elements.
     */
    private void enterTestMode() {
        isTestMode = true;
        testModeLayout.setVisibility(View.VISIBLE);
        testModeButton.setVisibility(View.GONE);
        normalButton.setVisibility(View.GONE);
    }
    /**
     * Starts the normal login mode by retrieving the device ID and checking the user's status.
     */
    private void startNormalMode() {
        Log.d("LoginFragment", "Normal Mode Pressed");
        isTestMode = false;
        testModeButton.setVisibility(View.GONE);
        normalButton.setVisibility(View.GONE);
        testModeLayout.setVisibility(View.GONE);
        normalLoginLayout.setVisibility(View.VISIBLE);
        String newDeviceId;
        newDeviceId = deviceId;
        deviceIdText.setText(newDeviceId);
        Log.d("LoginFragment", "DeviceID: " + newDeviceId);
        if(!UniversalProgramValues.getInstance().getTestingMode()) {
            FirestoreAccess.getInstance().getUser(newDeviceId).addOnSuccessListener(snapshot -> {
                if (!snapshot.exists()) {
                    handleNewUser(newDeviceId);
                } else {
                    User passingUser = snapshot.toObject(User.class);
                    handleReturningUser(passingUser, newDeviceId);
                }
            }).addOnFailureListener(e -> Log.d("LoginFragment", "Firestore access failed: " + e.getMessage()));
        }else{
            if(!UniversalProgramValues.getInstance().getExistingLogin()){
                handleNewUser(UniversalProgramValues.getInstance().getDeviceID());
            }
            else{
                handleReturningUser(UniversalProgramValues.getInstance().getSingle_user(), UniversalProgramValues.getInstance().getDeviceID());
            }

        }
    }
    /**
     * Handles the scenario when a new user is detected, and directs them to the profile creation screen.
     * @param deviceId The device ID of the new user.
     */
    private void handleNewUser(String deviceId) {
        Log.d("LoginFragment", "New user, no snapshot found.");
        Log.d("LoginFragment", "New userID: " + deviceId);
        welcomeText.setText("Welcome new user");
        ProfileFragment profileFragment = ProfileFragment.newInstance(true, eventIdFromQR, deviceId);
        User newUser = new User();
        newUser.setDeviceID(deviceId);
        UserManager.getInstance().setCurrentUser(newUser);
        handler = new Handler();
        handler.postDelayed(() -> {
            if (isAdded() && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).showNavigationUI();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, profileFragment)
                        .commitAllowingStateLoss();
            } else {
                Log.e("LoginFragment", "Fragment is not attached or MainActivity is null.");
            }
        }, 3000);
    }
    /**
     * Handles the scenario when a returning user is detected and directs them to the appropriate screen.
     * @param user The returning user.
     * @param deviceId The device ID of the returning user.
     */
    private void handleReturningUser(User user, String deviceId) {
//        Log.d("LoginFragment", "Returning User: " + user.getUsername());
        UserManager.getInstance().setCurrentUser(user);
        if(user.isGeolocationAsk()){
            UserManager.getInstance().updateGeolocation();
        }

        if(user.hasRole(Role.ADMIN)){
            ((MainActivity) getActivity()).loadAdminSidePanel();
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
        }
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            new MyNotificationManager(FirebaseFirestore.getInstance()).notifyUserUnread(user.getDeviceID(), getContext());
        }
        new Handler().postDelayed(() -> {
            if (eventIdFromQR != null) {
                ((MainActivity) getActivity()).showNavigationUI();
                EventViewFragment eventFragment = EventViewFragment.newInstance(eventIdFromQR, deviceId);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, eventFragment)
                        .commit();
            } else {
                ((MainActivity) getActivity()).showNavigationUI();
                HomeFragment homeFragment = HomeFragment.newInstance(deviceId);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, homeFragment)
                        .commit();
            }
        }, 3000);
    }
    /**
     * Sets the user by document ID for testing purposes.
     */
    private void setUserByDocumentId() {
        String documentId = documentIdInput.getText().toString().trim();
        if (documentId.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a Document ID", Toast.LENGTH_SHORT).show();
            return;
        }

        FirestoreAccess.getInstance().getUser(documentId).addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                User user = snapshot.toObject(User.class);
                user.saveUserDataToFirestore();
                UserManager.getInstance().setCurrentUser(user);
                if(user.isGeolocationAsk()){
                    UserManager.getInstance().updateGeolocation();
                }
                Toast.makeText(getActivity(), "User set by Document ID", Toast.LENGTH_SHORT).show();
                handleReturningUser(user, user.getDeviceID());
            } else {
                Toast.makeText(getActivity(), "Document ID not found", Toast.LENGTH_SHORT).show();
                handleNewUser(documentId);
            }
        });
    }
    /**
     * Sets the user to the default device ID for testing purposes.
     */
    private void setUserToDeviceID1() {
        FirestoreAccess.getInstance().getUser("deviceID1").addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Log.d("Login Activity", "User Snapshot Exists");
                User user = snapshot.toObject(User.class);
                deviceId = user.getDeviceID();
                UserManager.getInstance().setCurrentUser(user);
                Log.d("Login Activity", "User Snapshot deviceID: " + user.getDeviceID());
                handleReturningUser(user, user.getDeviceID());
            } else {
                Toast.makeText(getActivity(), "Device ID not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Sets the user by username for testing purposes.
     */
    private void setUserByUsername() {
        String username = usernameInput.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a Username", Toast.LENGTH_SHORT).show();
            return;
        }

        FirestoreAccess.getInstance().getUsersByUsername(username).addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                User user = querySnapshot.getDocuments().get(0).toObject(User.class);
                user.saveUserDataToFirestore();
                UserManager.getInstance().setCurrentUser(user);
                if(user.isGeolocationAsk()){
                    UserManager.getInstance().updateGeolocation();
                }
                Toast.makeText(getActivity(), "User set by Username", Toast.LENGTH_SHORT).show();
                handleReturningUser(user, user.getDeviceID());
            } else {
                Toast.makeText(getActivity(), "Username not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**
     * Called when the view of the fragment is destroyed.
     * This is an appropriate place to clean up any resources or cancel any pending operations.
     * In this case, the method cancels all pending callbacks and messages from the handler
     * to avoid memory leaks or unexpected behavior after the fragment's view is destroyed.
     */

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null); // Cancel all pending callbacks
        }
    }


}