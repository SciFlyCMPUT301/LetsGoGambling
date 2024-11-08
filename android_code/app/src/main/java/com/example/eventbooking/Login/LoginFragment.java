package com.example.eventbooking.Login;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.example.eventbooking.profile.ProfileEntrantFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

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

    TextView deviceIdText;
    TextView welcomeText;
    BottomNavigationView nav;
    public static boolean isLoggedIn = false;
    private String eventIdFromQR;


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

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
//        DrawerLayout drawerLayout = getActivity().findViewById(R.id.toolbar);
//        drawerLayout.setVisibility(View.GONE);
        nav = getActivity().findViewById(R.id.bottom_navigation);
        nav.setVisibility(View.GONE);

        NavigationView sidebar = getActivity().findViewById(R.id.nav_view);
//        if (sidebar != null) {
        sidebar.setVisibility(View.GONE);
//        }

        // tool bar hiding possible???
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);

        deviceIdText = rootView.findViewById(R.id.text_login_deviceid);
        welcomeText = rootView.findViewById(R.id.text_login_welcome);
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // Test code here to fake the device ID
        final String tempval = "deviceID1";
//        deviceId = tempval;

//        deviceIdText.setText(deviceId);
//
//        Log.d("Login", deviceId);
        deviceIdText.setText(tempval);
        Log.d("Login", tempval);
        FirestoreAccess fs = FirestoreAccess.getInstance();
//        fs.getUser(deviceId).addOnSuccessListener(snapshot -> {
        fs.getUser(tempval).addOnSuccessListener(snapshot -> {
            Log.d("Login New User", "New User found");
            //nav.setVisibility(View.VISIBLE);
            if (!snapshot.exists()) { // if new user
                welcomeText.setText("Welcome new user");
                //testing
//                User user = new User(deviceId, "Alex", "a@b.com", "9312-303", new HashSet<>());
//                fs.addUser(user).addOnSuccessListener(result -> {
//                    Log.d("Login", "added user successfully");
//                });
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLoggedIn = true;
//                        drawerLayout.setVisibility(View.VISIBLE);
                        nav.setVisibility(View.VISIBLE);
                        sidebar.setVisibility(View.VISIBLE);
                        toolbar.setVisibility(View.VISIBLE);
                        if (eventIdFromQR != null) {
                            // If the user is new and QR code was scanned, go to ProfileCreation then ScannedFragment
                            Log.d("Login Fragment", "New User going to Profile Entrant QR code");
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container,
                                            ProfileEntrantFragment.newInstance
                                                    (true, eventIdFromQR, tempval)) // replace with create new user fragment
                                    .commit();
                        } else {
                            // If the user is new and no QR code scanned, just go to ProfileCreation
                            Log.d("Login Fragment", "New User going to Profile Entrant no QR code");
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container,
                                            ProfileEntrantFragment.newInstance
                                                    (true, null, tempval)) // replace with create new user fragment
                                    .commit();
                        }
                    }
                }, 3000);
            } else { // returning user
                User user = snapshot.toObject(User.class);
                Log.d("Login", "User profile " + user.getProfilePictureUrl());
                Log.d("Login", "Retrieved user "+user.getUsername());
                UserManager.getInstance().setCurrentUser(user);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLoggedIn = true;
//                        drawerLayout.setVisibility(View.VISIBLE);
                        nav.setVisibility(View.VISIBLE);
                        sidebar.setVisibility(View.VISIBLE);
                        toolbar.setVisibility(View.VISIBLE);
                        Log.d("Login", "EventID from QR code " + eventIdFromQR);
                        if (eventIdFromQR != null) {


                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, EventViewFragment.newInstance(eventIdFromQR, tempval))
//                                    .replace(R.id.fragment_container, EventViewFragment.newInstance(eventIdFromQR, deviceId))
                                    .addToBackStack(null)
                                    .commit();


//                            getParentFragmentManager().beginTransaction()
//                                    .replace(R.id.fragment_container, ScannedFragment.newInstance(eventIdFromQR))
//                                    .commit();
                        } else {
//                            getParentFragmentManager().beginTransaction()
//                                    .replace(R.id.fragment_container, HomeFragment.newInstance(deviceId))
//                                    .commit();
                            getParentFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, HomeFragment.newInstance(tempval))
                                    .commit();

                        }

                    }
                }, 3000);
            }
        });

        return rootView;
    }
}