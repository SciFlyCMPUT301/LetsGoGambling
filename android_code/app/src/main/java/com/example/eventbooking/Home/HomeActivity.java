package com.example.eventbooking.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventbooking.Admin.AdminActivity;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventPageFragment.EventActivity;
import com.example.eventbooking.Events.EventView.EventViewActivity;
import com.example.eventbooking.Login.LoginActivity;
import com.example.eventbooking.R;
import com.example.eventbooking.Testing.TestFragment;
import com.example.eventbooking.profile.ProfileActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private int someInteger = 42; // Example integer to pass
    private Button eventCreateButton, eventButton, notificationButton, profileButton, testButton;
    private Button activityEvent, activityEventView, activityAdmin, activityProfile, activityLogin;
    private TextView integerTextView, pageName, activityTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        boolean isProcessing = getIntent().getBooleanExtra("isProcessing", false);
        Log.d("Home Activity", "Home Activity Launched");
        String sourceActivity = getIntent().getStringExtra("source_activity");
        Log.d("Home Activity", "Activity launched by: " + sourceActivity);
//        if (isProcessing) {
//            new Handler().postDelayed(() -> {
//                setContentView(R.layout.activity_home);
//            }, 3000);
//        } else {
//            setContentView(R.layout.activity_home);
//        }
        setContentView(R.layout.activity_home);
        // Launch HomeViewFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_fragment_container, new HomeViewFragment())
                    .commit();
        }
//        initalizeUI();

//        eventCreateButton.setOnClickListener(v -> {
//            Intent intent = new Intent(this, EventCreateActivity.class);
//            startActivity(intent);
//        });

//        eventButton.setOnClickListener(v -> {
//            Intent intent = new Intent(this, EventActivity.class);
//            startActivity(intent);
//        });
//
//        testButton.setOnClickListener(v -> {
//            hideUI();
//            getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.home_fragment_container, new TestFragment())
//                        .commit();
//
//        });
//
////        notificationButton.setOnClickListener(v -> {
////            Intent intent = new Intent(this, NotificationActivity.class);
////            startActivity(intent);
////        });
//
//        profileButton.setOnClickListener(v -> {
//            Intent intent = new Intent(this, ProfileActivity.class);
//            startActivity(intent);
//        });
//
//        activityEvent.setOnClickListener(v -> {
//            Intent intent = new Intent(this, EventActivity.class);
//            startActivity(intent);
//        });
//
//        activityEventView.setOnClickListener(v -> {
//            Intent intent = new Intent(this, EventViewActivity.class);
//            startActivity(intent);
//        });
//
//        activityAdmin.setOnClickListener(v -> {
//            Intent intent = new Intent(this, AdminActivity.class);
//            startActivity(intent);
//        });
//
//        activityLogin.setOnClickListener(v -> {
//            Intent intent = new Intent(this, LoginActivity.class);
//            startActivity(intent);
//        });
//
//        // Load up to 3 events with "deviceID2"
//        loadEventsWithParticipant("deviceID2");
    }

    private void loadEventsWithParticipant(String deviceID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events")
                .whereArrayContains("participantIds", deviceID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Event> events = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            Event event = doc.toObject(Event.class);
                            events.add(event);
                        }
                        displayEvents(events.subList(0, Math.min(events.size(), 3)));
                    } else {
                        Log.e("HomeActivity", "Error loading events", task.getException());
                    }
                });
    }

    private void displayEvents(List<Event> events) {
        for (Event event : events) {
            Log.d("HomeActivity", "Event: " + event.getEventTitle());
            // Add UI logic to display events here
        }
    }

    private void showUI(){
        // To not have to do this, create a HomeNavigationFragment and call that
        // Instead of this garbage
        integerTextView.setVisibility(View.VISIBLE);
        pageName.setVisibility(View.VISIBLE);
        eventCreateButton.setVisibility(View.VISIBLE);
        eventButton.setVisibility(View.VISIBLE);
        notificationButton.setVisibility(View.VISIBLE);
        profileButton.setVisibility(View.VISIBLE);
        testButton.setVisibility(View.VISIBLE);
        activityEvent.setVisibility(View.VISIBLE);
        activityEventView.setVisibility(View.VISIBLE);
        activityAdmin.setVisibility(View.VISIBLE);
        activityProfile.setVisibility(View.VISIBLE);
        activityLogin.setVisibility(View.VISIBLE);
        activityTitle.setVisibility(View.VISIBLE);
        // Set Integer
        integerTextView.setVisibility(View.VISIBLE);
    }

    private void hideUI(){
        // To not have to do this, create a HomeNavigationFragment and call that
        // Instead of this garbage
        integerTextView.setVisibility(View.GONE);
        pageName.setVisibility(View.GONE);
        eventCreateButton.setVisibility(View.GONE);
        eventButton.setVisibility(View.GONE);
        notificationButton.setVisibility(View.GONE);
        profileButton.setVisibility(View.GONE);
        testButton.setVisibility(View.GONE);
        activityEvent.setVisibility(View.GONE);
        activityEventView.setVisibility(View.GONE);
        activityAdmin.setVisibility(View.GONE);
        activityProfile.setVisibility(View.GONE);
        activityLogin.setVisibility(View.GONE);
        activityTitle.setVisibility(View.GONE);
        // Set Integer
        integerTextView.setVisibility(View.GONE);

    }

    private void initalizeUI(){
        // Initialize UI components
        integerTextView = findViewById(R.id.home_integer_text);
        pageName = findViewById(R.id.home_title);
        eventCreateButton = findViewById(R.id.button_event_create);
        eventButton = findViewById(R.id.button_event);
        notificationButton = findViewById(R.id.button_notification);
        profileButton = findViewById(R.id.button_profile);
        testButton = findViewById(R.id.button_test);
        activityEvent = findViewById(R.id.event_activity);
        activityEventView = findViewById(R.id.event_view_activity);
        activityAdmin = findViewById(R.id.admin_activity);
        activityProfile = findViewById(R.id.profile_activity);
        activityLogin = findViewById(R.id.login_activity);
        activityTitle = findViewById(R.id.activity_title);
        // Set Integer
        integerTextView.setText("Integer: " + someInteger);
    }
}
