package com.example.eventbooking;

import android.util.Log;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility;
import com.example.eventbooking.User;
import com.example.eventbooking.Role;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;
/**
 * Class is designed to generate random data to be used however wanted, either locally or uploaded
 * to firebase and used as such
 */
public class DataGenerator {

    private List<User> userList;
    private List<Facility> facilityList;
    private List<Event> eventList;

    private FirebaseFirestore db;

    /**
     * Constructor to make sure that all references are not null when being called
     */
    public DataGenerator() {
        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        facilityList = new ArrayList<>();
        eventList = new ArrayList<>();
    }

    /**
     * Secondary constructor to be called only if desired but will almost always be called right
     * after the constructor
     */
    public void generateAndUploadData() {
        generateUsers();
        generateFacilities();
        generateEvents();
        uploadData();
    }

    /**
     * Randomly generating users first as without users we cannot have a facility or events
     * No rhyme or reason to generating, mearly random
     */
    private void generateUsers() {
        userList.clear();
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setPhoneNumber("555-000" + i);
            user.setDeviceID(UUID.randomUUID().toString());

            // Assign roles
            if (i <= 2) {
                user.addRole(Role.ADMIN);
            } else if (i <= 5) {
                user.addRole(Role.ORGANIZER);
            } else {
                user.addRole(Role.ENTRANT);
            }

            userList.add(user);
        }
    }
    /**
     * Randomly generating facilities next and have to pair it to a user, cannot be more facilities
     * then users so the number is hard set for now, other generation techniques will come later
     */
    private void generateFacilities() {
        facilityList.clear();
        for (int i = 3; i <= 5; i++) {
            User organizer = userList.get(i - 1);
            Facility facility = new Facility();
            facility.setName("Facility" + (i - 2));
            facility.setAddress("Address of Facility" + (i - 2));
            facility.setOrganizer(organizer.getUsername());
            facility.setFacilityID("Facility"+(i - 2));
            Log.d("HAHAHAHAHAH", "Facility ID: " + facility.getFacilityID());

            facilityList.add(facility);
        }
    }
    /**
     * Lastly generating events to be used, these need to be associated with a facility
     * associating User -> Facility, the user is then an organizer
     * Then
     * associating User -> Event, the user organized the event
     *
     * Can also in the future link Event -> Facility to reduce searching time?
     * Might also just generate local User, Facility, and Events upon login
     */
    private void generateEvents() {
        eventList.clear();
        Random random = new Random();
        for (int i = 1; i <= 5; i++) {
            Event event = new Event();
            event.setEventId("Event" + i);
            event.setEventTitle("Event Title " + i);
            event.setDescription("Description for event " + i);
            event.setTimestamp(System.currentTimeMillis() + i * 86400000); // Next few days
            event.setMaxParticipants(10);

            // Assign location from facilities
            if (!facilityList.isEmpty()) {
                Facility facility = facilityList.get(random.nextInt(facilityList.size()));
                event.setLocation(facility.getLocation());
                facility.associateEvent(facility.getFacilityID(), event.getEventId());
            }

            // Assign organizer
            User organizer = userList.get(random.nextInt(3) + 2); // Users 3 to 5 are organizers
            event.setOrganizerId(organizer.getUsername());

            // Assign participants
            for (int j = 6; j <= 10; j++) {
                User entrant = userList.get(j - 1);
                event.signUpParticipant(entrant.getUsername());
            }

            eventList.add(event);
        }
    }

    private void uploadData() {
        // Step 1: Upload Users
        List<Task<Void>> userUploadTasks = new ArrayList<>();
        for (User user : userList) {
            Task<Void> task = user.saveUserDataToFirestore()
                    .addOnSuccessListener(aVoid -> System.out.println("User " + user.getUsername() + " uploaded."))
                    .addOnFailureListener(e -> System.out.println("Error uploading user " + user.getUsername() + ": " + e.getMessage()));
            userUploadTasks.add(task);
        }

        // Wait for all users to upload before uploading facilities
        Tasks.whenAllComplete(userUploadTasks).addOnSuccessListener(tasks -> {
            // Step 2: Upload Facilities
            List<Task<Void>> facilityUploadTasks = new ArrayList<>();
            for (Facility facility : facilityList) {
                Task<Void> task = facility.saveFacilityProfile()
                        .addOnSuccessListener(aVoid -> System.out.println("Facility " + facility.getName() + " uploaded."))
                        .addOnFailureListener(e -> System.out.println("Error uploading facility " + facility.getName() + ": " + e.getMessage()));
                facilityUploadTasks.add(task);
            }

            // Wait for all facilities to upload before uploading events
            Tasks.whenAllComplete(facilityUploadTasks).addOnSuccessListener(tasks2 -> {
                // Step 3: Upload Events
                List<Task<Void>> eventUploadTasks = new ArrayList<>();
                for (Event event : eventList) {
                    Task<Void> task = event.saveEventDataToFirestore()
                            .addOnSuccessListener(aVoid -> System.out.println("Event " + event.getEventId() + " uploaded."))
                            .addOnFailureListener(e -> System.out.println("Error uploading event " + event.getEventId() + ": " + e.getMessage()));
                    eventUploadTasks.add(task);
                }

                // Final confirmation after all events are uploaded
                Tasks.whenAllComplete(eventUploadTasks).addOnSuccessListener(tasks3 -> {
                    System.out.println("All data uploaded successfully!");
                }).addOnFailureListener(e -> System.out.println("Error uploading events: " + e.getMessage()));
            }).addOnFailureListener(e -> System.out.println("Error uploading facilities: " + e.getMessage()));
        }).addOnFailureListener(e -> System.out.println("Error uploading users: " + e.getMessage()));
    }
}
