package com.example.eventbooking;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventbooking.Events.EventData.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.AggregateQuery;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * The Facility class that has to have an organizer associated when we instantiate it, otherwise it
 * is a floating facility. This description means that nobody else can create a similar facility
 * (use case when we ban a facility).
 *
 * Facilities must also have the ability to have an event associated with them when one is created.
 * This is handled by the controller.
 *
 * @since   2024-11-04
 */
public class Facility {
    private String facilityID;
    private String name;
    private String address;
    private String organizer;
    private String eventName;
    private Location location;
    private List<String> allEvents;
    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;

    /**
     * The Facility class that has to have an organizer associated when we instantiate it, otherwise it
     * is a floating facility. This description means that nobody else can create a similar facility
     * (use case when we ban a facility).
     *
     * Facilities must also have the ability to have an event associated with them when one is created.
     * This is handled by the controller.
     *
     * @since   2024-11-04
     */
    public Facility() {
        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");
        // Initialize list to avoid NullPointerException
        allEvents = new ArrayList<>();
    }
    // This constructor is for when we dont have a facility ID
    public Facility(String name, String address, String description, String organizer) {
        this.name = name;
        this.address = address;
        this.organizer = organizer;
        this.db = FirebaseFirestore.getInstance();
        this.facilitiesRef = db.collection("Facilities");
        this.allEvents = new ArrayList<>();
        this.facilityID = getNewFacilityID();
    }

    // Constructor to get the facility details and associate the facilityID with it
    public Facility(String name, String address, String description, String organizer, String facilityID) {
        this.name = name;
        this.address = address;
        this.organizer = organizer;
        this.db = FirebaseFirestore.getInstance();
        this.facilitiesRef = db.collection("Facilities");
        this.allEvents = new ArrayList<>();
        this.facilityID = facilityID;
    }

    public String getFacilityID() {
        return facilityID;
    }
    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
    }

    public String getOrganizer(){
        return organizer;
    }
    public void setOrganizer(String organizer){
        this.organizer = organizer;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public String getEvent(){
        return eventName;
    }
    public void setEvent(String eventName){
        this.eventName = eventName;
    }

    public void setAllEvents(List<String> allEvents) {
        this.allEvents = allEvents;
    }
    public List<String> getAllEvents() {
        return allEvents;
    }
    public void addAllEventsItem(String eventID){
        allEvents.add(eventID);
    }
    public void removeAllEventsItem(String eventID){
        if(allEvents.contains(eventID)){
            allEvents.remove(eventID);
        }
    }

    public Task<Void> saveFacilityProfile() {
        String selected_facilityId = getFacilityID();
        if (selected_facilityId == null || selected_facilityId.isEmpty()) {
            throw new IllegalArgumentException("Facility name must be provided.");
        }
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", name);
//        facilityData.put("address", address);
        facilityData.put("organizer", organizer);
        facilityData.put("location", address);
        facilityData.put("allEvents", allEvents);
        facilityData.put("facilityID", facilityID);

        // Save data under the "facilities" collection
//        Log.d("Facility", facilityID);
        return db.collection("Facilities").document(facilityID)
                .set(facilityData)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Facility data successfully written to Firestore!");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error writing facility data to Firestore: " + e.getMessage());
                });
    }

    public void deleteFacilityProfile() {
        if (name != null && !name.isEmpty()) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("organizer", null); // Set the 'organizer' field to null

            facilitiesRef.document(name)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Facility updated successfully: organizer set to null.");
                    })
                    .addOnFailureListener(e -> {
                        throw new IllegalArgumentException("Error updating facility: " + e.getMessage());
                    });
        }
        else {
            throw new IllegalArgumentException("Facility name is invalid.");
        }
    }

    public void associateEvent(String selectedfacilityID, String eventID) {
        // Check if the facility document exists
        db.collection("Facilities").document(selectedfacilityID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        // Document exists, proceed with the update
                        updateEventInFacility(eventID);
                    } else {
                        // Document doesn't exist, create it with the event
                        System.out.println("Facility document not found. Creating document with event.");
                        createFacilityWithEvent(eventID);
                    }
                })
                .addOnFailureListener(e -> System.out.println("Error checking facility existence: " + e.getMessage()));
    }


    private void updateEventInFacility(String eventName) {
        if (allEvents == null) {
            allEvents = new ArrayList<>();
        }
        if (!allEvents.contains(eventName)) {
            allEvents.add(eventName);

            Map<String, Object> updates = new HashMap<>();
            updates.put("allEvents", allEvents);

            db.collection("Facilities").document(facilityID)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> System.out.println("Facility updated successfully with event."))
                    .addOnFailureListener(e -> System.out.println("Error updating facility: " + e.getMessage()));
        }
    }

    private void createFacilityWithEvent(String eventName) {
        // Making new facility ID
//        Query query = db.collection("Facilities");
//        AggregateQuery countQuery = query.count();


        allEvents.add(eventName);
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", name);
        facilityData.put("address", address);
        facilityData.put("organizer", organizer);
        facilityData.put("allEvents", allEvents);
        String newFacilityID = getNewFacilityID();
        setFacilityID(newFacilityID);
        facilityData.put("facilityID", newFacilityID);

        // old getting new facilityID
//        db.collection("Facilities").get().addOnCompleteListener(new
//            OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()) {
//                        String collectionSize = String.valueOf(task.getResult().size());
//                        setFacilityID(collectionSize);
//                        facilityData.put("facilityID", collectionSize);
//                    }
////                    } else {
////                        Toast.makeTesxt(getContext(),"Error : " +
////                                e.toString(),Toast.LENGHT_LONG).show;
////                    }
//                }
//            });

        db.collection("Facilities").document(facilityID)
                .set(facilityData)
                .addOnSuccessListener(aVoid -> System.out.println("Facility created successfully with initial event."))
                .addOnFailureListener(e -> System.out.println("Error creating facility: " + e.getMessage()));
    }

    private String getNewFacilityID(){
        final String[] facilityIDString = {""};
        db.collection("Facilities").get().addOnCompleteListener(new
        OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    facilityIDString[0] = String.valueOf((task.getResult().size()) +1);
                }
//                    } else {
//                        Toast.makeTesxt(getContext(),"Error : " +
//                                e.toString(),Toast.LENGHT_LONG).show;
//                    }
            }
        });
        facilityIDString[0] = "Facility"+facilityIDString[0];
        return facilityIDString[0];
    }

}

