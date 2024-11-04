package com.example.eventbooking;

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
 * import the android one use Android.os.UserManager*/
public class UserManager {
    private static UserManager instance;
    private User currentUser;
    private Facility userFacility;
    private List<Event> organizerEvents;

    private UserManager(){
        organizerEvents = new ArrayList<>();
    }

    public static synchronized UserManager getInstance(){
        if(instance == null){
            instance = new UserManager();
        }
        return instance;
    }
    public void setCurrentUser(User user){
        this.currentUser= user;

        findUserFacility();
    }

    private void findUserFacility() {
        FirestoreAccess.getInstance().getUserFacility(currentUser.getDeviceID()).addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                this.userFacility = doc.toObject(Facility.class);
                findFacilityEvents();
            }
        });
    }

    private void findFacilityEvents() {
        FirestoreAccess.getInstance().getOrganizerEvents(currentUser.getDeviceID()).addOnSuccessListener(queryDocumentSnapshots -> {
           if (!queryDocumentSnapshots.isEmpty()) {
               for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                   organizerEvents.add(document.toObject(Event.class));
               }
           }
        });
    }

    public User getCurrentUser(){
        return currentUser;
    }

}
