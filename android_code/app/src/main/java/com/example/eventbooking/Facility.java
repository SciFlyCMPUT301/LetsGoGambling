package com.example.eventbooking;

import com.google.android.gms.tasks.Task;
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

    public List<String> getAllEvents() {
        return allEvents;
    }

    public Task<Void> saveFacilityProfile() {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Facility name must be provided.");
        }
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", name);
        facilityData.put("address", address);
        facilityData.put("organizer", organizer);
        facilityData.put("location", location != null ? location.toString() : null);
        facilityData.put("allEvents", allEvents); // Store associated events

        // Save data under the "facilities" collection
        return db.collection("Facilities").document(name)
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

    public void associateEvent(String eventName) {
        // Check if the facility document exists
        db.collection("facilities").document(name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        // Document exists, proceed with the update
                        updateEventInFacility(eventName);
                    } else {
                        // Document doesn't exist, create it with the event
                        System.out.println("Facility document not found. Creating document with event.");
                        createFacilityWithEvent(eventName);
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

            db.collection("facilities").document(name)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> System.out.println("Facility updated successfully with event."))
                    .addOnFailureListener(e -> System.out.println("Error updating facility: " + e.getMessage()));
        }
    }

    private void createFacilityWithEvent(String eventName) {
        allEvents.add(eventName);
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", name);
        facilityData.put("address", address);
        facilityData.put("organizer", organizer);
        facilityData.put("allEvents", allEvents);

        db.collection("facilities").document(name)
                .set(facilityData)
                .addOnSuccessListener(aVoid -> System.out.println("Facility created successfully with initial event."))
                .addOnFailureListener(e -> System.out.println("Error creating facility: " + e.getMessage()));
    }
}

