package com.example.eventbooking;

import android.util.Log;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.firebase.FirestoreAccess;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
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
    public void setCurrentUser(User user){
        this.currentUser = user;
        Log.d("UserManager", user.getDeviceID() + user.getUsername());

        findUserFacility();
        findUserEvents();
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

    public User getCurrentUser(){
        return currentUser;
    }

    public Facility getUserFacility() {
        return userFacility;
    }

    public List<Event> getOrganizerEvents() {
        return organizerEvents;
    }

    public List<Event> getUserEvents() {
        return userEvents;
    }
}
