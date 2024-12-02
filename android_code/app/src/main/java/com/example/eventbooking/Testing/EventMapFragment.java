package com.example.eventbooking.Testing;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.example.eventbooking.waitinglist.OrganizerMenuFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class EventMapFragment extends Fragment {

    private MapView mapView;
    private EditText eventIdEditText;
    private Button activateButton;
    private Button navigateToOrganizer;
    private Spinner listSpinner;
    private FirebaseFirestore db;
    private String selectedListType = "Waitlist";
    private List<User> userList;
    private String eventID;
    private Event selectedEvent = null;

    public static EventMapFragment newInstance(String eventID) {
        EventMapFragment fragment = new EventMapFragment();
        Bundle args = new Bundle();
        args.putString("eventId", eventID);
        fragment.setArguments(args);
        return fragment;
    }

    public static EventMapFragment newInstance(Event selectedEvent) {
        EventMapFragment fragment = new EventMapFragment();
        Bundle args = new Bundle();
        args.putParcelable("selectedEvent", selectedEvent);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Important: Initialize the osmdroid configuration
        Context ctx = getActivity();
        Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
        Log.d("Map", "Launched Fragment");

        View view = inflater.inflate(R.layout.fragment_event_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        eventIdEditText = view.findViewById(R.id.eventIdEditText);
        activateButton = view.findViewById(R.id.activateButton);
        listSpinner = view.findViewById(R.id.listSpinner);
        navigateToOrganizer = view.findViewById(R.id.backToOrganizer);

        userList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Initialize the MapView
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        mapView.setMultiTouchControls(true);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        listSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                selectedListType = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedListType = "Waitlist";
            }
        });

        if (getArguments() != null) {
            Log.d("Map", "getting args");
            if(getArguments().getString("eventId") != null){
                eventID = getArguments().getString("eventId");
                Log.d("Map", "Got EventID");
            }

            if(getArguments().getParcelable("selectedEvent") != null){
                eventIdEditText.setVisibility(View.GONE);
                eventID = null;
                selectedEvent = getArguments().getParcelable("selectedEvent");
                Log.d("Map", "Got selected Event");
            }

            loadEventUsers(eventID, selectedListType);
        }

        activateButton.setOnClickListener(v -> {
            String eventId;
            if(selectedEvent == null)
                eventId = eventIdEditText.getText().toString().trim();
            else{
                eventId = selectedEvent.getEventId();
            }
            if (eventId.isEmpty()) {
                Toast.makeText(getContext(), "Please enter an Event ID", Toast.LENGTH_SHORT).show();
                return;
            }
            loadEventUsers(eventId, selectedListType);
        });

        navigateToOrganizer.setOnClickListener(v -> navigateToOrganizer());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume(); // Needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause(); // Needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDetach(); // Prevent memory leaks
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
        if(eventID != null){
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
        else{
            List<String> userIds;
            switch (listType) {
                case "Accepted List":
                    userIds = selectedEvent.getSignedUpParticipantIds();
                    break;
                case "Waitlist":
                    userIds = selectedEvent.getWaitingParticipantIds();
                    break;
                case "Declined List":
                    userIds = selectedEvent.getDeclinedParticipantIds();
                    break;
                case "Cancelled List":
                    userIds = selectedEvent.getCanceledParticipantIds();
                    break;
                case "Selected List":
                    userIds = selectedEvent.getAcceptedParticipantIds();
                    break;
                default:
                    userIds = new ArrayList<>();
            }
            addMarkersForUsers(userIds);
        }

    }

    private void addMarkersForUsers(List<String> userIds) {
        Log.d("Event Map", "Marker start");
        mapView.getOverlays().clear(); // Clear previous markers

        for (String userId : userIds) {
            Log.d("Event Map", "UserID String: " + userId);
            loadUserFromFirebase(userId, user -> {
                com.google.firebase.firestore.GeoPoint location = user.getGeolocation();
                Log.d("Event Map", "User Geopoint: " + location);
                if (location != null) {
                    GeoPoint position = new GeoPoint(location.getLatitude(), location.getLongitude());

                    Marker marker = new Marker(mapView);
                    marker.setPosition(position);
                    marker.setTitle(user.getUsername());
                    marker.setSnippet("Email: " + user.getEmail() + "\nPhone: " + user.getPhoneNumber());
                    marker.setOnMarkerClickListener((m, mapView) -> {
                        showUserDialog(user);
                        return true;
                    });
                    mapView.getOverlays().add(marker);

                    // Move the camera to the marker position
                    IMapController mapController = mapView.getController();
                    mapController.setZoom(14.0);
                    mapController.setCenter(position);
                } else {
                    Log.w("EventMapFragment", "User " + user.getUsername() + " does not have a geolocation.");
                }
            }, e -> Log.e("EventMapFragment", "Failed to load user: " + userId, e));
        }

        mapView.invalidate();
    }

    private void showUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(user.getUsername());
        builder.setMessage("Email: " + user.getEmail() + "\nPhone: " + user.getPhoneNumber() + "\nUserID: " + user.getDeviceID());
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void navigateToOrganizer(){
        OrganizerMenuFragment organizerMenuFragment = OrganizerMenuFragment.newInstance(selectedEvent);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, organizerMenuFragment)
                .addToBackStack(null)
                .commit();
    }
}