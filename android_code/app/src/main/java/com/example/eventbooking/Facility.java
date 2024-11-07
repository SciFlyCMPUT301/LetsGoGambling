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

public class Facility {
    private String facilityID;
    private String name;
    private String address;
    private String organizer;
    private String eventName;
//    private Location location;
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

    /**
     * Constructor for Facility without facilityID.
     *
     * @param name       the name of the facility
     * @param address    the address of the facility
     * @param description the description of the facility
     * @param organizer  the organizer of the facility
     */
    public Facility(String name, String address, String description, String organizer) {
        this.name = name;
        this.address = address;
        this.organizer = organizer;
        this.db = FirebaseFirestore.getInstance();
        this.facilitiesRef = db.collection("facilities");
        this.allEvents = new ArrayList<>();
    }


    /**
     * Getters and Setters for the given fields that can be easily set or we want to get
     */
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

    public String getOrganizer() { return organizer; }

    public void setOrganizer(String organizer) { this.organizer = organizer; }

//    public Location getLocation() { return location; }
//    public void setLocation(Location location) { this.location = location; }

    public String getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(String facilityID) {
        this.facilityID = facilityID;
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

    /**
     * Saves the facility profile to Firestore.
     *
     * @return a Task representing the save operation
     * @throws IllegalArgumentException if facility ID is null or empty
     */
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
                .addOnSuccessListener(aVoid -> System.out.println("Facility data successfully written to Firestore!"))
                .addOnFailureListener(e -> System.out.println("Error writing facility data to Firestore: " + e.getMessage()));
    }

    // Method for administrators to remove the organizer from the facility

    /**
     * Deletes the facility by setting its organizer to null, allowing administrators to disassociate the organizer.
     *
     * @throws IllegalArgumentException if facility name is invalid
     */
    public void deleteFacility() {
        if (facilityID != null && !facilityID.isEmpty()) {
            // Create a map to update the organizer field to null
            Map<String, Object> updates = new HashMap<>();
            updates.put("organizer", null); // Set the 'organizer' field to null

            // Update the facility document, setting the organizer to null
            facilitiesRef.document(facilityID)
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

    /**
     * Associates an event with a facility, creating the facility document if it doesn't exist.
     *
     * @param selectedfacilityID the facility ID to associate with
     * @param eventID            the event ID to associate
     */
    public void associateEvent(String selectedfacilityID, String eventID) {

        // Check if the facility document exists
        db.collection("Facilities").document(facilityID)
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

    // Organizer method to associate an event with a facility

    /**
     * Checks if an event is already associated with the facility.
     *
     * @param eventName the event name to check
     * @return true if the event is already associated, otherwise false
     */
    public boolean associateEvent(String eventName) {

        // Check if the event is already in the allEvents list
        if (allEvents.contains(eventName)) {
            System.out.println("Event already associated with this facility.");
            return true;
        }
        return false;
    }

    /**
     * Updates the facility by adding a new event if it is not already associated.
     *
     * @param eventName the event name to add
     */
    private void updateEventInFacility(String eventName) {
        if (allEvents == null) {
            allEvents = new ArrayList<>();
        }
        if (!allEvents.contains(eventName)) {
            allEvents.add(eventName);

            Map<String, Object> updates = new HashMap<>();
            updates.put("allEvents", allEvents);

            db.collection("facilities").document(name)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> System.out.println("Facility updated successfully with event."))
                    .addOnFailureListener(e -> System.out.println("Error updating facility: " + e.getMessage()));
        }
    }

    /**
     * Creates a new facility with an initial event if it does not already exist.
     *
     * @param eventName the event name to associate initially
     */
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

    /**
     * Generates a new unique facility ID.
     *
     * @return the new facility ID
     */
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

