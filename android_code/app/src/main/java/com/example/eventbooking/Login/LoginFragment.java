package com.example.eventbooking.Login;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventCreate.EventCreateFragment;
import com.example.eventbooking.Home.HomeFragment;
import com.example.eventbooking.R;
import com.example.eventbooking.firebase.FirestoreAccess;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

public class LoginFragment extends Fragment {

    TextView deviceIdText;
    TextView welcomeText;
    DocumentSnapshot snapshot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        deviceIdText = rootView.findViewById(R.id.text_login_deviceid);
        welcomeText = rootView.findViewById(R.id.text_login_welcome);
        String deviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceIdText.setText(deviceId);

        FirestoreAccess fs = new FirestoreAccess();


        fs.getUser(deviceId, documentSnapshot -> {
            snapshot = documentSnapshot;
        });

        if (snapshot == null || !snapshot.exists()) {
            welcomeText.setText("Welcome new user");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment()) // replace with create new user fragment
                            .commit();
                }
            }, 3000);
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment())
                            .commit();
                }
            }, 3000);
        }

        return rootView;
    }
}
