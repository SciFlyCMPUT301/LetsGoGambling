package com.example.eventbooking.Facility;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.eventbooking.UniversalProgramValues;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Represents a facility in the event booking system.
 *
 * <p>A facility is a location or venue associated with an organizer and optionally linked
 * to events. The class supports managing facility details, associating events, and saving
 * data to Firebase Firestore.</p>
 *
 * <p>The facility must have an organizer when instantiated. Otherwise, it is considered a
 * "floating facility," meaning it is unassociated and cannot be duplicated by other organizers.</p>
 */
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
    private boolean testing = true;

    /**
     * Default constructor for the Facility class.
     *
     * <p>Initializes Firestore and the list of associated events. If testing mode is enabled
     * through {@link UniversalProgramValues}, Firestore initialization is skipped.</p>
     */
    public Facility() {
        // Initialize Firestore and facilities collection reference
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            this.db = FirebaseFirestore.getInstance();
            this.facilitiesRef = db.collection("facilities");
        }

        // Initialize events list to prevent null values
        this.allEvents = new ArrayList<>();
    }

    /**
     * Constructor for dependency injection (useful for testing).
     *
     * @param db            the Firestore database instance
     * @param facilitiesRef the Firestore collection reference for facilities
     */
    // Constructor for dependency injection (useful for testing)
    public Facility(FirebaseFirestore db, CollectionReference facilitiesRef) {
        // Assign Firestore instances if not in testing mode
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            this.db = FirebaseFirestore.getInstance();
            this.facilitiesRef = facilitiesRef;
        }
        // Initialize events list
        this.allEvents = new ArrayList<>();
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

    public String getAddress() { return address; }

    /**
     * Set the address to the input string
     * @param address
     */
    public void setAddress(String address) { this.address = address; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

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

    /**
     * Sets the list of all events associated with this facility.
     *
     * @param allEvents List of event IDs associated with this facility
     */
    public void setAllEvents(List<String> allEvents) {
        this.allEvents = allEvents;
    }
    public List<String> getAllEvents() {
        return allEvents;
    }
    /**
     * Adds an event ID to the list of all events.
     *
     * @param eventID The ID of the event to add
     */
    public void addAllEventsItem(String eventID){
        allEvents.add(eventID);
    }
    /**
     * Removes an event ID from the list of all events.
     *
     * @param eventID The ID of the event to remove
     */
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
     * @throws IllegalArgumentException if facility ID is invalid
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
     * @param eventID the event ID to associate with the facility
     * @param genEvent flag indicating whether the event is being generated or not
     */
    public void associateEvent(String eventID, boolean genEvent) {
        // Check if the facility document exists
        if(genEvent){
            Log.d("Facility", "Adding new Event");
            allEvents.add(eventID);
        }else {
            Log.d("Facility", "Testing: " +UniversalProgramValues.getInstance().getTestingMode());
            if(!UniversalProgramValues.getInstance().getTestingMode()){
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
            else{
                if (allEvents == null)
                    allEvents = new ArrayList<>();
                if (!allEvents.contains(eventID))
                    allEvents.add(eventID);
            }

        }
    }

    // Organizer method to associate an event with a facility

    /**
     * Checks if an event is already associated with the facility.
     *
     * @param eventName the event name to check
     * @return true if the event is already associated, otherwise false
     */
    public boolean hasEvent(String eventName) {
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
     * This function saves a given facility with an event name to link the event to the facility
     * Designed for automating the generation of data
     *
     * @param eventName the event name to associate initially
     */
    private void createFacilityWithEvent(String eventName) {

        allEvents.add(eventName);
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("name", name);
        facilityData.put("address", address);
        facilityData.put("organizer", organizer);
        facilityData.put("allEvents", allEvents);
        String newFacilityID = getNewFacilityID();
        setFacilityID(newFacilityID);
        facilityData.put("facilityID", newFacilityID);

        db.collection("Facilities").document(facilityID)
                .set(facilityData)
                .addOnSuccessListener(aVoid -> System.out.println("Facility created successfully with initial event."))
                .addOnFailureListener(e -> System.out.println("Error creating facility: " + e.getMessage()));
    }

    /**
     * The function sees how many items are inside of the Facilities collection in firebase and
     * then adds one to the number to a string "Facility" such that each facility has a unique ID
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

