package com.example.eventbooking.Testing;

import android.util.Log;

import com.example.eventbooking.Admin.Images.ImageClass;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility.Facility;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.Role;
import com.example.eventbooking.UniversalProgramValues;
import com.example.eventbooking.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The SampleTable class provides methods to generate and manage
 * sample data for users, facilities, events, and images. This class
 * also includes functionality for saving the data to Firebase Firestore.
 */
public class SampleTable {

    public List<User> UserList = new ArrayList<>();
    public List<Facility> FacilityList = new ArrayList<>();
    public List<Event> EventList = new ArrayList<>();
    public List<ImageClass> ImageList = new ArrayList<>();

    private int userUpdateCount;
    private int facilityUpdateCount;
    /**
     * Generates a random location name from a predefined list.
     *
     * @return A random location name.
     */
    private String getRandomLocation() {
        String[] locations = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia", "San Antonio", "San Diego", "Dallas", "San Jose"};
        Random rand = new Random();
        return locations[rand.nextInt(locations.length)];
    }


    /**
     * Generates a random GeoPoint within a given radius.
     *
     * @param baseLatitude The base latitude.
     * @param baseLongitude The base longitude.
     * @param radiusInDegrees The radius in degrees around the base point.
     * @return A GeoPoint object within the specified radius.
     */
    private GeoPoint generateRandomGeoPoint(double baseLatitude, double baseLongitude, double radiusInDegrees) {
        Random random = new Random();

        // Generate random offsets within the radius
        double offsetLatitude = radiusInDegrees * (random.nextDouble() - 0.5);
        double offsetLongitude = radiusInDegrees * (random.nextDouble() - 0.5);

        double randomLatitude = baseLatitude + offsetLatitude;
        double randomLongitude = baseLongitude + offsetLongitude;

        return new GeoPoint(randomLatitude, randomLongitude);
    }


    /**
     * Creates a list of sample users with roles such as admin, organizer, and normal user.
     */
    public void makeUserList() {
        Log.d("Sample Table", "Making Users");
        double baseLatitude = 53.526131057259526;
        double baseLongitude = -113.5260486490807;
        double radiusInDegrees = 0.01;

        Random random = new Random();

        // Create 5 admin users
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setUsername("User" + i);
            user.setDeviceID("deviceID" + i);
            user.setEmail("admin" + i + "@example.com");
            user.setPhoneNumber("555-000" + i);
            user.addRole(Role.ADMIN);
            user.addRole(Role.ORGANIZER);
            user.addRole(Role.ENTRANT);
            user.setLocation(getRandomLocation());
            user.setFacilityAssociated(true);
            if(!UniversalProgramValues.getInstance().getTestingMode()) {
                String profileURL = user.defaultProfilePictureUrl(user.getUsername()).toString();
                user.setdefaultProfilePictureUrl(profileURL);
                user.setProfilePictureUrl(profileURL);
            }
            else{
                user.setdefaultProfilePictureUrl("Testing Profile URL" + i);
                user.setProfilePictureUrl("Testing Profile URL" + i);
            }


            GeoPoint randomGeoPoint = generateRandomGeoPoint(baseLatitude, baseLongitude, radiusInDegrees);
            user.setGeolocation(randomGeoPoint);
            UserList.add(user);
        }

        // Create 10 organizer users
        for (int i = 1; i <= 10; i++) {
            User user = new User();
            user.setUsername("User" + (i + 5));
            user.setDeviceID("deviceID" + (i + 5));
            user.setEmail("organizer" + (i + 5) + "@example.com");
            user.setPhoneNumber("555-010" + (i + 5));
            user.addRole(Role.ORGANIZER);
            user.addRole(Role.ENTRANT);
            user.setLocation(getRandomLocation());
            user.setFacilityAssociated(true);
            if(!UniversalProgramValues.getInstance().getTestingMode()) {
                String profileURL = user.defaultProfilePictureUrl(user.getUsername()).toString();
                user.setdefaultProfilePictureUrl(profileURL);
                user.setProfilePictureUrl(profileURL);
            }
            else{
                user.setdefaultProfilePictureUrl("Testing Profile URL" + (i + 5));
                user.setProfilePictureUrl("Testing Profile URL" + (i + 5));
            }
            GeoPoint randomGeoPoint = generateRandomGeoPoint(baseLatitude, baseLongitude, radiusInDegrees);
            user.setGeolocation(randomGeoPoint);
            UserList.add(user);
        }

        // Create 15 normal users
        for (int i = 1; i <= 15; i++) {
            User user = new User();
            user.setUsername("User" + (i + 15));
            user.setDeviceID("deviceID" + (i + 15));
            user.setEmail("user" + (i + 15) + "@example.com");
            user.setPhoneNumber("555-020" + (i + 15));
            user.addRole(Role.ENTRANT);
            user.setLocation(getRandomLocation());
            if(!UniversalProgramValues.getInstance().getTestingMode()) {
                String profileURL = user.defaultProfilePictureUrl(user.getUsername()).toString();
                user.setdefaultProfilePictureUrl(profileURL);
                user.setProfilePictureUrl(profileURL);
            }
            else{
                user.setdefaultProfilePictureUrl("Testing Default Profile URL" + (i + 15));
                user.setProfilePictureUrl("Testing Profile URL" + (i + 15));
            }
            GeoPoint randomGeoPoint = generateRandomGeoPoint(baseLatitude, baseLongitude, radiusInDegrees);
            user.setGeolocation(randomGeoPoint);
            UserList.add(user);
        }
        Log.d("User list", "Done List");
    }

    /**
     * Generates a list of sample facilities and associates them with organizer users.
     */
    public void makeFacilityList() {
        Log.d("Sample Table", "Making Facility");
        List<User> organizers = new ArrayList<>();
        // Collect organizers from the UserList
        for (User user : UserList) {
            if (user.hasRole(Role.ORGANIZER)) {
                organizers.add(user);
            }
        }

        // Create facilities and associate them with organizers
        for (int i = 0; i < organizers.size(); i++) {
            User organizer = organizers.get(i);
            Facility facility = new Facility();
            facility.setName("Facility" + (i + 1));
            facility.setFacilityID("facilityID" + (i + 1));
            facility.setAddress("Address of Facility" + (i + 1));
            facility.setOrganizer(organizer.getDeviceID());
            FacilityList.add(facility);
            organizer.setFacilityAssociated(true);
        }

//        // Create additional facilities without events or organizers
//        for (int i = 11; i <= 15; i++) {
//            Facility facility = new Facility();
//            facility.setName("Facility" + i);
//            facility.setFacilityID("Facility" + (i + 1));
//            facility.setAddress("Address of Facility" + i);
//            facility.setOrganizer(null); // No organizer
//            FacilityList.add(facility);
//        }
    }

    /**
     * Generates a list of sample events, randomly assigning facilities, participants, and statuses.
     */
    public void makeEventList() {
        Log.d("Sample Table", "Making event");
        Random random = new Random();
        List<User> organizer_list = new ArrayList<>();
        for (User user : UserList) {
            if (user.hasRole(Role.ORGANIZER)) {
                // Adding them twice so we can just remove as we go
                organizer_list.add(user);
                organizer_list.add(user);
            }
        }
        Log.d("Sample Table", "Organizer list length: " + organizer_list.size());

        // Create 30 events
        for (int i = 1; i <= 30; i++) {
            Event event = new Event();
            event.setEventId("eventID" + i);
            event.setEventTitle("Event Title " + i);
            event.setDescription("Description for event " + i);
            event.setTimestamp(System.currentTimeMillis() + i * 100000);
            event.setMaxParticipants(20);
            event.setOrganizerId(organizer_list.get(i-1).getDeviceID());
            event.setEventPosterURL("Event Picture URL" + i);
            if(UniversalProgramValues.getInstance().getTestingMode()){
                event.setDefaultEventPosterURL("https://fastly.picsum.photos/id/1033/200/300.jpg?hmac=856_WOyaGXSjI4FWe3_NCHU7frPtAEJaHnAJja5TMNk");
                event.setEventPosterURL("https://fastly.picsum.photos/id/532/200/200.jpg?hmac=PPwpqfjXOagQmhd_K7H4NXyA4B6svToDi1IbkDW2Eos");
            }


            QRcodeGenerator qrCodeGenerator = new QRcodeGenerator();
            String hashInput = event.getEventId() + Calendar.getInstance().getTime();
            String qrCodeHash = qrCodeGenerator.createQRCodeHash(hashInput);
            event.setQRcodeHash(qrCodeHash);

            // Assign a facility to the event (if applicable)
            if (!FacilityList.isEmpty() && random.nextBoolean()) {
                Facility facility = FacilityList.get(random.nextInt(FacilityList.size()));
                event.setLocation(facility.getAddress());
                facility.associateEvent(event.getEventId(), true);
            }

            // Assign participants with random statuses
            List<User> entrants = new ArrayList<>();
            for (User user : UserList) {
                if (user.hasRole(Role.ENTRANT)) {
                    entrants.add(user);
                }
            }

            // Randomly shuffle entrants and select up to 10 participants
            Collections.shuffle(entrants);
            int numParticipants = Math.min(entrants.size(), 10);

            for (int j = 0; j < numParticipants; j++) {
                User entrant = entrants.get(j);
                int status = random.nextInt(4);  // 0: accepted, 1: signed-up, 2: canceled, 3: waitlist
                switch (status) {
                    case 0: event.addAcceptedParticipantId(entrant.getDeviceID()); break;
                    case 1: event.addSignedUpParticipantIds(entrant.getDeviceID()); break;
                    case 2: event.addCanceledParticipantIds(entrant.getDeviceID()); break;
                    case 3: event.addWaitingParticipantIds(entrant.getDeviceID()); break;
                }
            }

            // Add the event to the EventList
            EventList.add(event);
        }
    }

    /**
     * Function to generate a bunch of images for UI testing
     */
    public void makeImageList(){
        for(int i = 0; i < UserList.size(); i++){

            ImageClass newImageDefault = new ImageClass(UserList.get(i).getdefaultProfilePictureUrl(),
                    "User: " + UserList.get(i).getUsername(),
                    UserList.get(i).getDeviceID(),
                    "Users");
            if(!Objects.equals(UserList.get(i).getProfilePictureUrl(), UserList.get(i).getdefaultProfilePictureUrl())){
                ImageClass newImageProfile = new ImageClass(UserList.get(i).getProfilePictureUrl(),
                        "User: " + UserList.get(i).getUsername(),
                        UserList.get(i).getDeviceID(),
                        "Users");
                ImageList.add(newImageProfile);
            }
            ImageList.add(newImageDefault);
        }

        for(int i = 0; i < EventList.size(); i++){
            ImageClass newImage = new ImageClass(EventList.get(i).getEventPosterURL(),
                    "Event: " + EventList.get(i).getEventTitle(),
                    EventList.get(i).getEventId(),
                    "Events");
            ImageList.add(newImage);
        }
    }

    /**
     * Saves the generated data (users, facilities, and events) to Firebase Firestore.
     *
     * @param onSuccess The callback to execute if the data is successfully saved.
     * @param onFailure The callback to execute if an error occurs during saving.
     */
    public void saveDataToFirebase(Runnable onSuccess, OnFailureListener onFailure) {
        AtomicInteger pendingWrites = new AtomicInteger(UserList.size() + FacilityList.size() + EventList.size());
        AtomicInteger failures = new AtomicInteger(0);

        // Save users to Firestore
        for (User user : UserList) {
            String imageLink = "https://example.com/default-profile-picture.jpg";
            user.setProfilePictureUrl(imageLink);
            user.saveUserDataToFirestore()
                    .addOnSuccessListener(aVoid -> checkCompletion(pendingWrites, failures, onSuccess, onFailure))
                    .addOnFailureListener(e -> {
                        failures.incrementAndGet();
                        onFailure.onFailure(e);
                        checkCompletion(pendingWrites, failures, onSuccess, onFailure);
                    });
        }

        // Save facilities to Firestore
        for (Facility facility : FacilityList) {
            facility.saveFacilityProfile()
                    .addOnSuccessListener(aVoid -> checkCompletion(pendingWrites, failures, onSuccess, onFailure))
                    .addOnFailureListener(e -> {
                        failures.incrementAndGet();
                        onFailure.onFailure(e);
                        checkCompletion(pendingWrites, failures, onSuccess, onFailure);
                    });
        }

        // Save events to Firestore
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

    /**
     * Helper method to check if all Firebase writes are completed.
     *
     * @param pendingWrites The number of pending writes.
     * @param failures The number of failures.
     * @param onSuccess The callback to execute on success.
     * @param onFailure The callback to execute on failure.
     */
    private void checkCompletion(AtomicInteger pendingWrites, AtomicInteger failures, Runnable onSuccess, OnFailureListener onFailure) {
        if (pendingWrites.decrementAndGet() == 0) {
            if (failures.get() == 0) {
                onSuccess.run();
            } else {
                onFailure.onFailure(new Exception("One or more errors occurred during data saving."));
            }
        }
    }

    // Methods to retrieve the lists of users, facilities, and events

    /**
     * Returns the list of sample users.
     *
     * @return The list of users.
     */
    public List<User> getUserList() {
        return UserList;
    }

    /**
     * Returns the list of sample facilities.
     *
     * @return The list of facilities.
     */
    public List<Facility> getFacilityList() {
        return FacilityList;
    }

    /**
     * Returns the list of sample events.
     *
     * @return The list of events.
     */
    public List<Event> getEventList() {
        return EventList;
    }

    /**
     * Returns the list of sample images.
     *
     * @return The list of imageclass.
     */
    public List<ImageClass> getImageList(){
        return ImageList;
    }

    // Methods to update specific users, facilities, or events

    /**
     * Updates a specific user's information.
     *
     * @param updatedUser The updated user object.
     */
    public void updateUser(User updatedUser) {
        for (int i = 0; i < UserList.size(); i++) {
            if (UserList.get(i).getUsername().equals(updatedUser.getUsername())) {
                UserList.set(i, updatedUser);
                updatedUser.saveUserDataToFirestore();
                break;
            }
        }
    }

    /**
     * Updates a specific facility's information.
     *
     * @param updatedFacility The updated facility object.
     */
    public void updateFacility(Facility updatedFacility) {
        for (int i = 0; i < FacilityList.size(); i++) {
            if (FacilityList.get(i).getName().equals(updatedFacility.getName())) {
                FacilityList.set(i, updatedFacility);
                updatedFacility.saveFacilityProfile();
                break;
            }
        }
    }

    /**
     * Updates a specific event's information.
     *
     * @param updatedEvent The updated event object.
     */
    public void updateEvent(Event updatedEvent) {
        for (int i = 0; i < EventList.size(); i++) {
            if (EventList.get(i).getEventId().equals(updatedEvent.getEventId())) {
                EventList.set(i, updatedEvent);
                updatedEvent.saveEventDataToFirestore();
                break;
            }
        }
    }

    // Additional methods for retrieving objects by specific identifiers

    /**
     * Retrieves a user by their username.
     *
     * @param username The username of the user to retrieve.
     * @return The user with the given username, or null if not found.
     */
    public User getUserByUsername(String username) {
        for (User user : UserList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Retrieves a facility by its name.
     *
     * @param name The name of the facility to retrieve.
     * @return The facility with the given name, or null if not found.
     */
    public Facility getFacilityByName(String name) {
        for (Facility facility : FacilityList) {
            if (facility.getName().equals(name)) {
                return facility;
            }
        }
        return null;
    }

    /**
     * Retrieves an event by its ID.
     *
     * @param eventId The ID of the event to retrieve.
     * @return The event with the given ID, or null if not found.
     */
    public Event getEventById(String eventId) {
        for (Event event : EventList) {
            if (event.getEventId().equals(eventId)) {
                return event;
            }
        }
        return null;
    }

    /**
     * Resets the lists of users, facilities, events, and images.
     */
    public void resetLists(){
        UserList = new ArrayList<>();
        EventList = new ArrayList<>();
        FacilityList = new ArrayList<>();
    }


}
