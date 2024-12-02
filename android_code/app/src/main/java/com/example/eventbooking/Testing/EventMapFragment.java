package com.example.eventbooking.Testing;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeUserEventAdapter;
import com.example.eventbooking.User;
import com.example.eventbooking.UserManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.CameraPosition;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.eventbooking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
/**
 * Fragment for displaying a Google Map with user locations for an event.
 * Allows users to view and interact with participant markers based on selected list types.
 */
public class EventMapFragment extends Fragment implements OnMapReadyCallback {
    /** MapView for displaying the Google Map */
    private MapView mapView;
    /** GoogleMap instance for interacting with the map */
    private GoogleMap googleMap;
    /** EditText for entering the Event ID */
    private EditText eventIdEditText;
    /** Button to activate and load users for the specified Event ID */
    private Button activateButton;
    /** Spinner to select the list type (e.g., Waitlist, Accepted List) */
    private Spinner listSpinner;
    /** FirebaseFirestore instance for database interaction */
    private FirebaseFirestore db;
    /** Selected list type (e.g., "Waitlist") */
    private String selectedListType = "Waitlist";
    /** List of users for the selected event */
    private List<User> userList;
    /** Event ID for which users are being displayed */
    private String eventID;
    /**
     * Creates a new instance of EventMapFragment with the specified Event ID.
     *
     * @param eventID The ID of the event.
     * @return A new instance of EventMapFragment.
     */
    public static EventMapFragment newInstance(String eventID) {
        EventMapFragment fragment = new EventMapFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventID);
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate the fragment layout
        View view = inflater.inflate(R.layout.fragment_event_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        eventIdEditText = view.findViewById(R.id.eventIdEditText);
        activateButton = view.findViewById(R.id.activateButton);
        listSpinner = view.findViewById(R.id.listSpinner);

// Set up the MapView and load the map asynchronously
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        userList = new ArrayList<>();
        // Initialize the Firestore database and user list
        db = FirebaseFirestore.getInstance();
        // Listener for the list spinner to update selectedListType based on user choice
        listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedListType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedListType = "Waitlist";
            }
        });
        // If fragment arguments contain an event ID, load users for that event
        if(getArguments() != null){
            eventID = getArguments().getString("eventId");
            loadEventUsers(eventID, selectedListType);
        }
        // Listener for the activate button to load users for the specified event ID
        activateButton.setOnClickListener(v -> {
            String eventId = eventIdEditText.getText().toString().trim();
            if (eventId.isEmpty()) {
                Toast.makeText(getContext(), "Please enter an Event ID", Toast.LENGTH_SHORT).show();
                return;
            }
            loadEventUsers(eventId, selectedListType);
        });

        return view;
    }
    /**
     * Callback for when the map is ready.
     *
     * @param map The GoogleMap instance.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setOnMarkerClickListener(marker -> {
            // Listener for marker clicks to show user details in a popup
            User user = (User) marker.getTag(); // Assume User object is attached to the marker
            if (user != null) {
                showUserPopup(user);
            }
            return true;
        });
    }
    /**
     * Displays a popup with user details when a marker is clicked.
     *
     * @param user The User object attached to the marker.
     */
    private void showUserPopup(User user) {
        // Example: Show a Toast. Replace this with a custom dialog or bottom sheet.
        Toast.makeText(getContext(), "User: " + user.getUsername() + "\nEmail: " + user.getEmail(), Toast.LENGTH_LONG).show();
    }

    //private void setupMarkers() {
    //}

    private void showMarkerPopup(Marker marker) {
        new AlertDialog.Builder(getContext())
                .setTitle(marker.getTitle())
                .setMessage(marker.getSnippet())
                .setPositiveButton("Close", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    /**
     * Loads a user from Firestore by their ID.
     *
     * @param userId   The ID of the user to load.
     * @param onSuccess Callback for successful user loading.
     * @param onFailure Callback for failure in user loading.
     */

    private void loadUserFromFirebase(String userId, OnSuccessListener<User> onSuccess, OnFailureListener onFailure) {
        Log.d("Event Map", "Load UserID");
        db.collection("Users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                Log.d("Event Map", "Success Load UserID " + user.getDeviceID());
                onSuccess.onSuccess(user);
            } else {
                Log.w("EventMapFragment", "User not found: " + userId);
                onFailure.onFailure(new Exception("User not found"));
            }
        }).addOnFailureListener(onFailure);
    }
    /**
     * Loads event users from Firestore and adds markers to the map.
     *
     * @param eventId  The ID of the event.
     * @param listType The type of user list to load (e.g., "Waitlist").
     */

    private void loadEventUsers(String eventId, String listType) {
        db.collection("Events").document(eventId).get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                Toast.makeText(getContext(), "Event not found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the list of user IDs based on the selected list type
            List<String> userIds;
            switch (listType) {
                case "Accepted List":
                    userIds = (List<String>) documentSnapshot.get("acceptedParticipantIds");
                    break;
                case "Waitlist":
                    userIds = (List<String>) documentSnapshot.get("waitingparticipantIds");
                    break;
                case "Declined List":
                    userIds = (List<String>) documentSnapshot.get("canceledParticipantIds");
                    break;
                case "Cancelled List":
                    userIds = (List<String>) documentSnapshot.get("canceledParticipantIds");
                    break;
                case "Selected List":
                    userIds = (List<String>) documentSnapshot.get("signedUpParticipantIds");
                    break;
                default:
                    userIds = new ArrayList<>();
            }

            if (userIds == null || userIds.isEmpty()) {
                Toast.makeText(getContext(), "No users found in the selected list", Toast.LENGTH_SHORT).show();
                return;
            }

            // Filter and add markers for users
            addMarkersForUsers(userIds);

        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to load event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    /**
     * Adds markers for users to the map based on their locations.
     *
     * @param userIds List of user IDs to add markers for.
     */
    private void addMarkersForUsers(List<String> userIds) {
        Log.d("Event Map", "Marker start");
        googleMap.clear(); // Clear previous markers

        for (String userId : userIds) {
            Log.d("Event Map", "UserID String: " + userId);
            loadUserFromFirebase(userId, user -> {
                GeoPoint location = user.getGeolocation();
                Log.d("Event Map", "User Geopoint: " + location);
                if (location != null) {
                    LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(user.getUsername())
                            .snippet("Email: " + user.getEmail() + "\nPhone: " + user.getPhoneNumber()));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(position)
                            .zoom(14) // Zoom level
                            .build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    Log.w("EventMapFragment", "User " + user.getUsername() + " does not have a geolocation.");
                }
            }, e -> Log.e("EventMapFragment", "Failed to load user: " + userId, e));
        }


        // Set marker click listener
        googleMap.setOnMarkerClickListener(marker -> {
            Toast.makeText(getContext(), marker.getSnippet(), Toast.LENGTH_LONG).show();
            return true;
        });
    }


}
