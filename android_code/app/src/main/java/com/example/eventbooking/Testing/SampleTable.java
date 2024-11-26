package com.example.eventbooking.Testing;

import android.util.Log;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility;
import com.example.eventbooking.Role;
import com.example.eventbooking.User;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class SampleTable {
    public List<User> UserList = new ArrayList<>();
    public List<Facility> FacilityList = new ArrayList<>();
    public List<Event> EventList = new ArrayList<>();


    private int userUpdateCount;
    private int facilityUpdateCount;

    public void makeUserList() {
        // Create 5 admin users
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setUsername("User" + i);
            user.setDeviceID("deviceID" + i);
            user.setEmail("admin" + i + "@example.com");
            user.setPhoneNumber("555-000" + i);
            user.addRole(Role.ADMIN);
            user.addRole(Role.ENTRANT);
            UserList.add(user);
        }

        // Create 10 organizer users
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUsername("User" + i);
            user.setDeviceID("deviceID" + (i + 5));
            user.setEmail("organizer" + i + "@example.com");
            user.setPhoneNumber("555-010" + i);
            user.addRole(Role.ORGANIZER);
            user.addRole(Role.ENTRANT);
            UserList.add(user);
        }

        // Create 15 normal users
        for (int i = 1; i <= 15; i++) {
            User user = new User();
            user.setUsername("User" + i);
            user.setDeviceID("deviceID" + (i + 15));
            user.setEmail("user" + i + "@example.com");
            user.setPhoneNumber("555-020" + i);
            // ENTRANT role is added by default in User constructor
            user.addRole(Role.ENTRANT);
            UserList.add(user);
        }
        Log.d("User list", "Done List");
    }

    public void makeFacilityList() {
        // Get organizers from UserList
        List<User> organizers = new ArrayList<>();
        for (User user : UserList) {
            if (user.hasRole(Role.ORGANIZER)) {
                organizers.add(user);
            }
        }

        // Create facilities and associate with organizers
        for (int i = 0; i < organizers.size(); i++) {
            User organizer = organizers.get(i);
            Facility facility = new Facility();
            facility.setName("Facility" + (i + 1));
            facility.setFacilityID("Facility" + (i + 1));
            facility.setAddress("Address of Facility" + (i + 1));
            facility.setOrganizer(organizer.getUsername());
            FacilityList.add(facility);
            // Optionally, set facility in organizer if needed
            organizer.setFacilityAssociated(true);
        }

        // Create facilities without events or organizers
        for (int i = 11; i <= 15; i++) {
            Facility facility = new Facility();
            facility.setName("Facility" + i);
            facility.setFacilityID("Facility" + (i + 1));
            facility.setAddress("Address of Facility" + i);
            facility.setOrganizer(null); // No organizer
            FacilityList.add(facility);
        }
    }

    public void makeEventList() {
        Random random = new Random();

        // Create 30 events
        for (int i = 1; i <= 30; i++) {
            Event event = new Event();
            event.setEventId("event" + i);
            event.setEventTitle("Event Title " + i);
            event.setDescription("Description for event " + i);
            event.setTimestamp(System.currentTimeMillis() + i * 100000);
            event.setMaxParticipants(20);

            // Assign to a facility (some facilities have events, some don't)
            if (!FacilityList.isEmpty()) {
                if (random.nextBoolean()) {
                    Facility facility = FacilityList.get(random.nextInt(FacilityList.size()));
                    Log.d("Sample Table", "Event ID: " + "event" + i);
                    event.setLocation(facility.getAddress());
                    facility.associateEvent(event.getEventId(), true);
                }
            }

            // Assign participants with different statuses
            List<User> entrants = new ArrayList<>();
            Log.d("Event list", "Making Entrant");

            // Filter users with the ENTRANT role
            for (User user : UserList) {
                if (user.hasRole(Role.ENTRANT)) {
                    entrants.add(user);
                }
            }

            // Log how many entrants we have
            Log.d("Event list", "Making Entrant: " + entrants.size());

            // Randomly shuffle entrants and select up to 10 participants
            Collections.shuffle(entrants);
            int numParticipants = Math.min(entrants.size(), 10);

            // Assign participants to the event
            for (int j = 0; j < numParticipants; j++) {
                User entrant = entrants.get(j);
                Log.d("Event list", "deviceID: " + entrant.getDeviceID());

                // Assign a random status to the participant
                int status = random.nextInt(4); // 0: accepted, 1: signed-up, 2: canceled, 3: waitlist
                switch (status) {
                    case 0:
                        event.addAcceptedParticipantId(entrant.getDeviceID());
                        break;
                    case 1:
                        event.addSignedUpParticipantIds(entrant.getDeviceID());
                        break;
                    case 2:
                        event.addCanceledParticipantIds(entrant.getDeviceID());
                        break;
                    case 3:
                        event.addWaitingParticipantIds(entrant.getDeviceID());
                        break;
                }
            }

            // Add the event to the EventList
            EventList.add(event);
        }
    }

    public void saveDataToFirebase(Runnable onSuccess, OnFailureListener onFailure) {
        AtomicInteger pendingWrites = new AtomicInteger(UserList.size() + FacilityList.size() + EventList.size());
        AtomicInteger failures = new AtomicInteger(0);

        // Save users
        for (User user : UserList) {
            String image_link = "https://firebasestorage.googleapis.com/v0/b/letsgogambling-9ebb8.appspot.com/o/images%2Fb8d07a65-6ab2-4ba3-8b92-724e4803e468.jpg?alt=media&token=b24f29a0-98eb-4597-a218-40f494bd3ad3";
            user.setdefaultProfilePictureUrl(image_link);
            user.setProfilePictureUrl(image_link);
            user.saveUserDataToFirestore()
                    .addOnSuccessListener(aVoid -> checkCompletion(pendingWrites, failures, onSuccess, onFailure))
                    .addOnFailureListener(e -> {
                        failures.incrementAndGet();
                        onFailure.onFailure(e);
                        checkCompletion(pendingWrites, failures, onSuccess, onFailure);
                    });
        }

        // Save facilities
        for (Facility facility : FacilityList) {
            facility.saveFacilityProfile()
                    .addOnSuccessListener(aVoid -> checkCompletion(pendingWrites, failures, onSuccess, onFailure))
                    .addOnFailureListener(e -> {
                        failures.incrementAndGet();
                        onFailure.onFailure(e);
                        checkCompletion(pendingWrites, failures, onSuccess, onFailure);
                    });
        }

        // Save events
        for (Event event : EventList) {
            event.saveEventDataToFirestore()
                    .addOnSuccessListener(aVoid -> checkCompletion(pendingWrites, failures, onSuccess, onFailure))
                    .addOnFailureListener(e -> {
                        failures.incrementAndGet();
                        onFailure.onFailure(e);
                        checkCompletion(pendingWrites, failures, onSuccess, onFailure);
                    });
        }

    }

    private void checkCompletion(AtomicInteger pendingWrites, AtomicInteger failures, Runnable onSuccess, OnFailureListener onFailure) {
        if (pendingWrites.decrementAndGet() == 0) {
            if (failures.get() == 0) {
                onSuccess.run();
            } else {
                onFailure.onFailure(new Exception("One or more errors occurred during data saving."));
            }
        }
    }

    // Methods to retrieve lists
    public List<User> getUserList() {
        return UserList;
    }

    public List<Facility> getFacilityList() {
        return FacilityList;
    }

    public List<Event> getEventList() {
        return EventList;
    }

    // Methods to update specific users, facilities, events
    public void updateUser(User updatedUser) {
        for (int i = 0; i < UserList.size(); i++) {
            if (UserList.get(i).getUsername().equals(updatedUser.getUsername())) {
                UserList.set(i, updatedUser);
                updatedUser.saveUserDataToFirestore();
                break;
            }
        }
    }

    public void updateFacility(Facility updatedFacility) {
        for (int i = 0; i < FacilityList.size(); i++) {
            if (FacilityList.get(i).getName().equals(updatedFacility.getName())) {
                FacilityList.set(i, updatedFacility);
                updatedFacility.saveFacilityProfile();
                break;
            }
        }
    }

    public void updateEvent(Event updatedEvent) {
        for (int i = 0; i < EventList.size(); i++) {
            if (EventList.get(i).getEventId().equals(updatedEvent.getEventId())) {
                EventList.set(i, updatedEvent);
                updatedEvent.saveEventDataToFirestore();
                break;
            }
        }
    }

    // Additional methods for demonstration
    public User getUserByUsername(String username) {
        for (User user : UserList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public Facility getFacilityByName(String name) {
        for (Facility facility : FacilityList) {
            if (facility.getName().equals(name)) {
                return facility;
            }
        }
        return null;
    }

    public Event getEventById(String eventId) {
        for (Event event : EventList) {
            if (event.getEventId().equals(eventId)) {
                return event;
            }
        }
        return null;
    }
}
