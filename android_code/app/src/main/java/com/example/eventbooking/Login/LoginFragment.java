package com.example.eventbooking.Login;

import android.content.Intent;
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

import androidx.appcompat.widget.Toolbar;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Facility;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.MainActivity;
import com.example.eventbooking.QRCode.ScannedFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.Role;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.example.eventbooking.firebase.FirestoreAccess;
import com.example.eventbooking.notification.NotificationFragment;
import com.example.eventbooking.profile.ProfileEntrantFragment;
import com.example.eventbooking.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Fragment that is shown on app open. Gets deviceId and checks if the
 * user is new.
 */
public class LoginFragment extends Fragment {

    private TextView deviceIdText, welcomeText;
    private BottomNavigationView nav;
    private NavigationView sidebar;
    public static boolean isLoggedIn = false;
    private boolean isTestMode = false;
    private String eventIdFromQR;

    private Button testModeButton, normalButton;
    private LinearLayout testModeLayout, normalLoginLayout;
    private EditText documentIdInput, usernameInput;
    private Button setByDocumentIdButton, setByUsernameButton, setToDeviceID1;

    private Handler handler;


    public void setEventId(String eventId) {
        this.eventIdFromQR = eventId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("LoginOnCreate", "Before Argument");
        if (getArguments() != null) {
            eventIdFromQR = getArguments().getString("eventIdFromQR");
            Log.d("LoginOnCreate", "After Argument " + eventIdFromQR);
        }
    }

    /**
     * Sets up the view for the login. Gets user from firestore and changes text accordingly.
     * If the user is new, takes them to create fragment, otherwise takes them to the home page.
     * If the app was launched via QR Code, take the user to the corresponding event.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return rootView The view to be displayed
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Login Fragment", "Login Fragment Launch");
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
//        DrawerLayout drawerLayout = getActivity().findViewById(R.id.toolbar);
//        drawerLayout.setVisibility(View.GONE);
        ((MainActivity) getActivity()).hideNavigationUI();


        initializeUI(rootView);
        testModeButton.setOnClickListener(v -> enterTestMode());
        normalButton.setOnClickListener(v -> startNormalMode());
        setByDocumentIdButton.setOnClickListener(v -> setUserByDocumentId());
        setByUsernameButton.setOnClickListener(v -> setUserByUsername());
        setToDeviceID1.setOnClickListener(v -> setUserToDeviceID1());

        return rootView;


//        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//
//        // Test code here to fake the device ID
//        final String tempval = "deviceID1";
////        deviceId = tempval;
//
////        deviceIdText.setText(deviceId);
////
////        Log.d("Login", deviceId);
//        deviceIdText.setText(tempval);
//        Log.d("Login", tempval);
//        FirestoreAccess fs = FirestoreAccess.getInstance();
////        fs.getUser(deviceId).addOnSuccessListener(snapshot -> {
//        fs.getUser(tempval).addOnSuccessListener(snapshot -> {
//            Log.d("Login New User", "New User found");
//            //nav.setVisibility(View.VISIBLE);
//            if (!snapshot.exists()) { // if new user
//                welcomeText.setText("Welcome new user");
//                //testing
////                User user = new User(deviceId, "Alex", "a@b.com", "9312-303", new HashSet<>());
////                fs.addUser(user).addOnSuccessListener(result -> {
////                    Log.d("Login", "added user successfully");
////                });
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        isLoggedIn = true;
////                        drawerLayout.setVisibility(View.VISIBLE);
//                        nav.setVisibility(View.VISIBLE);
//                        sidebar.setVisibility(View.VISIBLE);
//                        toolbar.setVisibility(View.VISIBLE);
//                        if (eventIdFromQR != null) {
//                            // If the user is new and QR code was scanned, go to ProfileCreation then ScannedFragment
//                            Log.d("Login Fragment", "New User going to Profile Entrant QR code");
//                            getParentFragmentManager().beginTransaction()
//                                    .replace(R.id.fragment_container,
//                                            ProfileEntrantFragment.newInstance
//                                                    (true, eventIdFromQR, tempval)) // replace with create new user fragment
//                                    .commit();
//                        } else {
//                            // If the user is new and no QR code scanned, just go to ProfileCreation
//                            Log.d("Login Fragment", "New User going to Profile Entrant no QR code");
//                            getParentFragmentManager().beginTransaction()
//                                    .replace(R.id.fragment_container,
//                                            ProfileEntrantFragment.newInstance
//                                                    (true, null, tempval)) // replace with create new user fragment
//                                    .commit();
//                        }
//                    }
//                }, 3000);
//            } else { // returning user
//                User user = snapshot.toObject(User.class);
//                Log.d("Login", "User profile " + user.getProfilePictureUrl());
//                Log.d("Login", "Retrieved user "+user.getUsername());
//                UserManager.getInstance().setCurrentUser(user);
//
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        isLoggedIn = true;
////                        drawerLayout.setVisibility(View.VISIBLE);
//                        nav.setVisibility(View.VISIBLE);
//                        sidebar.setVisibility(View.VISIBLE);
//                        toolbar.setVisibility(View.VISIBLE);
//                        Log.d("Login", "EventID from QR code " + eventIdFromQR);
//                        if (eventIdFromQR != null) {
//
//
//                            getParentFragmentManager().beginTransaction()
//                                    .replace(R.id.fragment_container, EventViewFragment.newInstance(eventIdFromQR, tempval))
////                                    .replace(R.id.fragment_container, EventViewFragment.newInstance(eventIdFromQR, deviceId))
//                                    .addToBackStack(null)
//                                    .commit();
//
//
////                            getParentFragmentManager().beginTransaction()
////                                    .replace(R.id.fragment_container, ScannedFragment.newInstance(eventIdFromQR))
////                                    .commit();
//                        } else {
////                            getParentFragmentManager().beginTransaction()
////                                    .replace(R.id.fragment_container, HomeFragment.newInstance(deviceId))
////                                    .commit();
//                            getParentFragmentManager().beginTransaction()
//                                    .replace(R.id.fragment_container, HomeFragment.newInstance(tempval))
//                                    .commit();
//
//                        }
//
//                    }
//                }, 3000);
//            }
//        });
//
//        return rootView;
    }



    private void initializeUI(View rootView) {
        Log.d("LoginFragment", "Initializing UI");
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
    }

    private void enterTestMode() {
        isTestMode = true;
        testModeLayout.setVisibility(View.VISIBLE);
        testModeButton.setVisibility(View.GONE);
        normalButton.setVisibility(View.GONE);
    }

    private void startNormalMode() {
        Log.d("LoginFragment", "Normal Mode Pressed");
        isTestMode = false;
        testModeButton.setVisibility(View.GONE);
        normalButton.setVisibility(View.GONE);
        testModeLayout.setVisibility(View.GONE);
        normalLoginLayout.setVisibility(View.VISIBLE);
        String newDeviceId = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("LoginFragment", "DeviceID: " + newDeviceId);
        deviceIdText.setText(newDeviceId);

        FirestoreAccess.getInstance().getUser(newDeviceId).addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                handleNewUser(newDeviceId);
            } else {
                User passingUser = snapshot.toObject(User.class);
                handleReturningUser(passingUser, newDeviceId);
            }
        }).addOnFailureListener(e -> Log.d("LoginFragment", "Firestore access failed: " + e.getMessage()));
    }

    private void handleNewUser(String deviceId) {
        Log.d("LoginFragment", "New user, no snapshot found.");
        Log.d("LoginFragment", "New userID: " + deviceId);
        welcomeText.setText("Welcome new user");
        ProfileFragment profileFragment = ProfileFragment.newInstance(true, eventIdFromQR, deviceId);
//        ProfileEntrantFragment profileFragment = ProfileEntrantFragment.newInstance(true, eventIdFromQR, deviceId);
//        new Handler().postDelayed(() -> {
//            ((MainActivity) getActivity()).showNavigationUI();
//            getParentFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, profileFragment)
//                    .commit();
//        }, 3000);
        // Temp setting this for Profile fragment
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

    private void handleReturningUser(User user, String deviceId) {
        Log.d("LoginFragment", "Returning User: " + user.getUsername());

        user.saveUserDataToFirestore();
        UserManager.getInstance().setCurrentUser(user);
        if(user.isGeolocationAsk()){
            UserManager.getInstance().updateGeolocation();
//            user.updateGeolocation();
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



    private void setUserByDocumentId() {
        String documentId = documentIdInput.getText().toString().trim();
        if (documentId.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a Document ID", Toast.LENGTH_SHORT).show();
            return;
        }

        FirestoreAccess.getInstance().getUser(documentId).addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                User user = snapshot.toObject(User.class);
//                if(user.isGeolocationAsk()){
//                    user.updateGeolocation();
//                }
                user.saveUserDataToFirestore();
                UserManager.getInstance().setCurrentUser(user);
                if(user.isGeolocationAsk()){
                    UserManager.getInstance().updateGeolocation();
//            user.updateGeolocation();
                }
                Toast.makeText(getActivity(), "User set by Document ID", Toast.LENGTH_SHORT).show();
                handleReturningUser(user, user.getDeviceID());
            } else {
                Toast.makeText(getActivity(), "Document ID not found", Toast.LENGTH_SHORT).show();
                handleNewUser(documentId);
            }
        });
    }

    private void setUserToDeviceID1() {
        FirestoreAccess.getInstance().getUser("deviceID2").addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                User user = snapshot.toObject(User.class);
                UserManager.getInstance().setCurrentUser(user);
                handleReturningUser(user, user.getDeviceID());
            } else {
                Toast.makeText(getActivity(), "Device ID not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserByUsername() {
        String username = usernameInput.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a Username", Toast.LENGTH_SHORT).show();
            return;
        }

        FirestoreAccess.getInstance().getUsersByUsername(username).addOnSuccessListener(querySnapshot -> {
            if (!querySnapshot.isEmpty()) {
                User user = querySnapshot.getDocuments().get(0).toObject(User.class);
//                if(user.isGeolocationAsk()){
//                    user.updateGeolocation();
//                }
                user.saveUserDataToFirestore();
                UserManager.getInstance().setCurrentUser(user);
                if(user.isGeolocationAsk()){
                    UserManager.getInstance().updateGeolocation();
//            user.updateGeolocation();
                }
                Toast.makeText(getActivity(), "User set by Username", Toast.LENGTH_SHORT).show();
                handleReturningUser(user, user.getDeviceID());
            } else {
                Toast.makeText(getActivity(), "Username not found", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null); // Cancel all pending callbacks
        }
    }

}