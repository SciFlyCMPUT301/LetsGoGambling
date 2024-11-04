package com.example.eventbooking.Login;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.Role;
import com.example.eventbooking.User;
import com.example.eventbooking.firebase.FirestoreAccess;
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

public class LoginFragment extends Fragment {

    TextView deviceIdText;
    TextView welcomeText;
    BottomNavigationView nav;
    public static boolean isLoggedIn = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        nav = getActivity().findViewById(R.id.bottom_navigation);
        nav.setVisibility(View.GONE);

        NavigationView sidebar = getActivity().findViewById(R.id.nav_view);
        if (sidebar != null) {
            sidebar.setVisibility(View.GONE);
        }

        deviceIdText = rootView.findViewById(R.id.text_login_deviceid);
        welcomeText = rootView.findViewById(R.id.text_login_welcome);
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceIdText.setText(deviceId);

        FirestoreAccess fs = new FirestoreAccess();
        fs.getUser(deviceId).addOnSuccessListener(snapshot -> {
            //nav.setVisibility(View.VISIBLE);
            if (!snapshot.exists()) {
                welcomeText.setText("Welcome new user");
                // testing
//                User user = new User(deviceId, "Alex", "a@b.com", "9312-303", new HashSet<>());
//                fs.addUser(user).addOnSuccessListener(result -> {
//                    Log.d("Login", "added user successfully");
//                });
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLoggedIn = true;
                        nav.setVisibility(View.VISIBLE);
                        sidebar.setVisibility(View.VISIBLE);
                        getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, HomeFragment.newInstance(deviceId)) // replace with create new user fragment
                            .commit();
                    }
                }, 3000);
            } else {
                User user = snapshot.toObject(User.class);
                Log.d("Login", "Retrieved user "+user.getUsername());

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isLoggedIn = true;
                        nav.setVisibility(View.VISIBLE);
                        sidebar.setVisibility(View.VISIBLE);
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, HomeFragment.newInstance(deviceId))
                                .commit();
                    }
                }, 3000);
            }
        });

        return rootView;
    }
}
