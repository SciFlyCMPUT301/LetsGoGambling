package com.example.eventbooking;

import static android.app.PendingIntent.getActivity;
import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.firebase.FirestoreAccess;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * this is the custom UserManager class
 * its just for adjust the User's role
 * be careful when import, import this one follow the path
 * import the android one use Android.os.UserManager
 */
public class UserManager {
    private static UserManager instance;
    private User currentUser;
    private Facility userFacility;
    private List<Event> organizerEvents;
    private List<Event> userEvents;
    private List<Event> eventDatabase;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentBestLocation;
    private Context context;
    private GeoPoint geoLocation;

    private UserManager(){
        organizerEvents = new ArrayList<>();
        userEvents = new ArrayList<>();
    }

    /**
     * Singleton pattern, allows for a static instance across the whole app
     * @return the instance of UserManager
     */
    public static synchronized UserManager getInstance(){
        if(instance == null){
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Should only be called once; Initializes UserManager and fills
     * out all the user's data
     * @param user the current user
     */
    public void setCurrentUser(User user) {
        if (user == null) {
            this.currentUser = null;
            return;
        }

        // Initialize a new User instance and copy fields from the input user
        this.currentUser = new User();
        this.currentUser.setDeviceID(user.getDeviceID());
        this.currentUser.setUsername(user.getUsername());
        this.currentUser.setEmail(user.getEmail());
        this.currentUser.setPhoneNumber(user.getPhoneNumber());
        this.currentUser.setProfilePictureUrl(user.getProfilePictureUrl());
        this.currentUser.setdefaultProfilePictureUrl(user.getProfilePictureUrl());
        this.currentUser.setLocation(user.getLocation());
        this.currentUser.setAddress(user.getAddress());
        this.currentUser.setAdminLevel(user.isAdminLevel());
        this.currentUser.setFacilityAssociated(user.isFacilityAssociated());
        this.currentUser.setNotificationAsk(user.isNotificationAsk());
        this.currentUser.setGeolocationAsk(user.isGeolocationAsk());

        // Make a deep copy of the roles list to avoid sharing references
        this.currentUser.setRoles(new ArrayList<>(user.getRoles()));

        Log.d("UserManager", "User set with ID: " + currentUser.getDeviceID() + " and Name: " + currentUser.getUsername());

        // Optionally load related data if needed
         findUserFacility();
        // findUserEvents();
    }

    /**
     * Queries and gets all events the user is in any list for
     */
    private void findUserEvents() {
        FirestoreAccess.getInstance().getUserEvents(currentUser.getDeviceID()).addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    userEvents.add(document.toObject(Event.class));
                    Log.d("UserManager", this.userEvents.toString());
                }
            }
        });

    }

    /**
     * Queries and gets the user's facility if it exists
     */
    private void findUserFacility() {
        FirestoreAccess.getInstance().getUserFacility(currentUser.getDeviceID()).addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                this.userFacility = doc.toObject(Facility.class);
                Log.d("UserManager", this.userFacility.toString());
                findOrganizerEvents();
            }
        });
    }

    /**
     * Queries and gets all events the user is an organizer of, if any
     */
    private void findOrganizerEvents() {
        FirestoreAccess.getInstance().getOrganizerEvents(currentUser.getDeviceID()).addOnSuccessListener(queryDocumentSnapshots -> {
           if (!queryDocumentSnapshots.isEmpty()) {
               for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                   organizerEvents.add(document.toObject(Event.class));
                   Log.d("UserManager", organizerEvents.toString());
               }
           }
        });
    }

    /**
     * Checks to see if a facility exists for the current user or not
     * @return boolean
     */
    public boolean userHasFacility() {
        if(userFacility != null)
            return true;
        return false;
    }
    /**getter of current user
     * @return the current User */
    public User getCurrentUser(){
        return currentUser;
    }
    /**
     * getter of user facility
     * @return userFacility */

    public Facility getUserFacility() {
        return userFacility;
    }
    /**
     * getter of organizer events
     * @return  organizerEvents*/
    //delete this later, no usage
    public List<Event> getOrganizerEvents() {
        return organizerEvents;
    }
    /**
     * getter of user events, they were designed for
     * restrict permissions for entrant and organizer
     * @return userEvents*/
    //might delete later
    public List<Event> getUserEvents() {
        return userEvents;
    }

    public String getUserId() { return currentUser.getDeviceID(); }

    /**
     * Updates the user's geolocation and returns it as a GeoPoint.
     * If location is unavailable, returns a default GeoPoint (1,1).
     *
     * @return A GeoPoint object with the current location or a default point (1,1).
     */
    public GeoPoint getNewGeolocation() {


        final GeoPoint[] geoPoint = {new GeoPoint(1.0, 1.0)}; // Default GeoPoint
        try {
            fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    Location location = task.getResult();
                    geoPoint[0] = new GeoPoint(location.getLatitude(), location.getLongitude());
                    Log.d("Geolocation", "Location updated: " + geoPoint[0].toString());
                } else {
                    Log.w("Geolocation", "Failed to retrieve location. Returning default GeoPoint (1,1).");
                }
            });
        } catch (SecurityException e) {
            Log.e("Geolocation", "Location permissions are missing. Returning default GeoPoint (1,1).", e);
        }

        return geoPoint[0];
    }

//    public void updateGeolocation() {
//        LocationListener locationListener = new LocationListener() {
//            @Override
//            public void onLocationChanged(android.location.Location location) {
//                //get the attributes of current location using the location variable
//                //example, location.getLatitude()
//
////                location.getLatitude();
////                location.getLongitude();
//            }
//
//            @Override
//            public void onProviderEnabled(String provider) {
//
//            }
//
//            @Override
//            public void onProviderDisabled(String provider) {
//
//            }
//        };
//        LocationManager locationManager =
//                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
////        LocationProvider provider =
////                locationManager.getProvider(LocationManager.GPS_PROVIDER);
//
//        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
//        currentUser.setGeolocation(getNewGeolocation());
//        Log.d("User Manager", "Updated Geopoint to be: " + currentUser.getGeolocation());
//        currentUser.saveUserDataToFirestore().addOnSuccessListener(aVoid -> {
//        }).addOnFailureListener(e -> Log.d("User Manager", "Failed to Update Geopoint"));
//        UserManager.getInstance().setCurrentUser(currentUser);
//
//    }

//    stuff.mit.edu/afs/sipb/project/android/docs/training/basics/location/currentlocation.html


    public void setFusedLocationClient(FusedLocationProviderClient client) {
        this.fusedLocationClient = client;
    }

    public void setContext (Context newContext){
        this.context = newContext;
    }

    public void setGeolocation(GeoPoint newPoint){
        this.geoLocation = newPoint;
    }

    public void updateGeolocation() {
        currentUser.setGeolocation(geoLocation);
        Log.d("User Manager", "Updated Geopoint to be: " + currentUser.getGeolocation());
        currentUser.saveUserDataToFirestore().addOnSuccessListener(aVoid -> {
        }).addOnFailureListener(e -> Log.d("User Manager", "Failed to Update Geopoint"));
        UserManager.getInstance().setCurrentUser(currentUser);
    }



}
