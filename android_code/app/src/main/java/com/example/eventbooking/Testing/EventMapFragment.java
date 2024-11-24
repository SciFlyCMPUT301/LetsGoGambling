package com.example.eventbooking.Testing;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Events.EventView.EventViewFragment;
import com.example.eventbooking.Home.HomeUserEventAdapter;
import com.example.eventbooking.User;
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

public class EventMapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private EditText eventIdEditText;
    private Button activateButton;
    private Spinner listSpinner;
    private FirebaseFirestore db;
    private String selectedListType = "Waitlist";
    private List<User> userList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        eventIdEditText = view.findViewById(R.id.eventIdEditText);
        activateButton = view.findViewById(R.id.activateButton);
        listSpinner = view.findViewById(R.id.listSpinner);


        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        userList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

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

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        googleMap.setOnMarkerClickListener(marker -> {
            // Show popup with user details
            User user = (User) marker.getTag(); // Assume User object is attached to the marker
            if (user != null) {
                showUserPopup(user);
            }
            return true;
        });
    }

    private void showUserPopup(User user) {
        // Example: Show a Toast. Replace this with a custom dialog or bottom sheet.
        Toast.makeText(getContext(), "User: " + user.getUsername() + "\nEmail: " + user.getEmail(), Toast.LENGTH_LONG).show();
    }

    private void setupMarkers() {

        // Example here to dynamically allocated the data based on users or whatever
//        for (MyLocationData data : locationList) {
//            LatLng position = new LatLng(data.getLatitude(), data.getLongitude());
//            googleMap.addMarker(new MarkerOptions()
//                    .position(position)
//                    .title(data.getName())
//                    .snippet(data.getDescription()));
//        }

//        if (!locationList.isEmpty()) {
//            LatLng firstLocation = new LatLng(locationList.get(0).getLatitude(), locationList.get(0).getLongitude());
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(firstLocation)
//                    .zoom(14) // Adjust zoom level
//                    .build();
//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        }
////
//
//
//        // Example marker at a specific location
//        LatLng location1 = new LatLng(53.5261, -113.5260);
//        googleMap.addMarker(new MarkerOptions()
//                .position(location1)
//                .title("Marker 1")
//                .snippet("This is marker 1's info."));
//
//        LatLng location2 = new LatLng(53.5270, -113.5250);
//        googleMap.addMarker(new MarkerOptions()
//                .position(location2)
//                .title("Marker 2")
//                .snippet("This is marker 2's info."));
//
//        // Move the camera to the first marker
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(location1)
//                .zoom(14) // Zoom level
//                .build();
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

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
