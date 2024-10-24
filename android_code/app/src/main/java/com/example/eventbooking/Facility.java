package com.example.eventbooking;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Facility {
    private String name;
    private String address;
    private String organizer;
    private String eventName;
    private Location location;
    private List<String> allEvents = new ArrayList<>();
    private FirebaseFirestore db;
    private CollectionReference facilitiesRef;

    public Facility() {
        // Initialize Firebase instances
        db = FirebaseFirestore.getInstance();
        facilitiesRef = db.collection("facilities");
        // Initialize list to avoid NullPointerException
        allEvents = new ArrayList<>();
    }
    public Facility(String name, String address, String description, String organizer) {
        this.name = name;
        this.address = address;
        this.organizer = organizer;
        this.db = FirebaseFirestore.getInstance();
        this.facilitiesRef = db.collection("facilities");
        this.allEvents = new ArrayList<>();
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


    public String getEvent(){
        return eventName;
    }

    public void setEvent(String eventName){
        this.eventName = eventName;
    }

    public List<String> getAllEvents() {
        return allEvents;
    }

    public void saveFacilityProfile() {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Facility name must be provided.");
        }
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", name);
        facilityData.put("address", address);
        facilityData.put("organizer", organizer);
        facilityData.put("allEvents", allEvents); // Store associated events

        // Save data under the "facilities" collection
        db.collection("facilities").document(name)
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

    // Associate Event with Facility (with inline success and failure handling)
    public void associateEvent(String eventName) {
        if (!allEvents.contains(eventName)) {
            allEvents.add(eventName);
            Map<String, Object> updates = new HashMap<>();
            updates.put("allEvents", allEvents);
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
            throw new IllegalArgumentException("Event is already associated with this facility.");
        }
    }
}

