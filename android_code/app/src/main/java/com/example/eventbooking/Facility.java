package com.example.eventbooking;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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

    // Getters and Setters for fields
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public Location getLocation() { return location; }
    public void setLocation(Location location) { this.location = location; }

    public List<String> getAllEvents() { return allEvents; }

    // Method to save or create a facility profile
    public Task<Void> saveFacilityProfile() {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Facility name must be provided.");
        }
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", name);
        facilityData.put("address", address);
        facilityData.put("organizer", organizer);
        facilityData.put("location", location != null ? location.toString() : null);
        facilityData.put("allEvents", allEvents);

        return db.collection("facilities").document(name)
                .set(facilityData)
                .addOnSuccessListener(aVoid -> System.out.println("Facility data successfully written to Firestore!"))
                .addOnFailureListener(e -> System.out.println("Error writing facility data to Firestore: " + e.getMessage()));
    }

    // Method for administrators to remove the organizer from the facility
    public void deleteFacility() {
        if (name != null && !name.isEmpty()) {
            // Create a map to update the organizer field to null
            Map<String, Object> updates = new HashMap<>();
            updates.put("organizer", null);

            // Update the facility document, setting the organizer to null
            facilitiesRef.document(name)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> System.out.println("Organizer removed successfully from facility."))
                    .addOnFailureListener(e -> System.out.println("Error removing organizer from facility: " + e.getMessage()));
        } else {
            throw new IllegalArgumentException("Facility name is invalid.");
        }
    }

    // Organizer method to associate an event with a facility
    public boolean associateEvent(String eventName) {
        // Check if the event is already in the allEvents list
        if (allEvents.contains(eventName)) {
            System.out.println("Event already associated with this facility.");
            return true;
        }
        return false;
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

    public String getFacilityID() {
        return 0;
    }
}
