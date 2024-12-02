package com.example.eventbooking.Events.EventData;


import static android.app.PendingIntent.getActivity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.UniversalProgramValues;

import com.example.eventbooking.waitinglist.WaitingList;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * The Event class represents an event in the system, encapsulating details such as title,
 * description, location, maximum participants, and lists for participant statuses (e.g., accepted,
 * canceled). This class provides methods for managing event data and interacting with Firebase
 * services for storage, retrieval, and updates.
 */
public class Event implements Parcelable {
    private String eventId;
    private String eventTitle;
    private String description;
    private String imageUrl; // URL of the event image in Firebase Storage
    private long timestamp; // Event time in milliseconds
    private String eventPosterURL;
    private String defaultEventPosterURL;
    private boolean geolocationRequired;

    private String address;
    private String location;
    private int maxParticipants; // limit number of entrants
    private int waitingListLimit;
    private List<String> waitingparticipantIds;
    private List<String> acceptedParticipantIds;
    private List<String> canceledParticipantIds;
    private List<String> signedUpParticipantIds;
    private List<String> enrolledParticipantIds;
    private List<String> declinedParticipantIds;
    private WaitingList waitingList;
    private String organizerId;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String qrcodehash;
    /**
     * Default constructor that initializes Firebase Firestore and Storage instances, as well as
     * participant lists.
     */
    public Event() {
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            storage = FirebaseStorage.getInstance();
            db = FirebaseFirestore.getInstance();
        }

        this.waitingList= new WaitingList();
        //waiting list constructor will handle these 4 list
        this.waitingparticipantIds = new ArrayList<>();
        this.acceptedParticipantIds = new ArrayList<>();
        this.canceledParticipantIds = new ArrayList<>();
        this.signedUpParticipantIds = new ArrayList<>();
        this.enrolledParticipantIds = new ArrayList<>();
        this.declinedParticipantIds = new ArrayList<>();

    }
    /**
     * Constructs an Event with a given event ID.
     *
     * @param eventID The unique identifier of the event.
     */

    public Event(int eventID) {
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        this.waitingList=new WaitingList();
    }
    /**
     * Constructs an Event in test mode without Firebase dependencies.
     *
     * @param testMode Whether the event is in test mode.
     */

    public Event(Boolean testMode){
        this.waitingList= new WaitingList();
        //waiting list constructor will handle these 4 list
        this.waitingparticipantIds = new ArrayList<>();
        this.acceptedParticipantIds = new ArrayList<>();
        this.canceledParticipantIds = new ArrayList<>();
        this.signedUpParticipantIds = new ArrayList<>();
        this.enrolledParticipantIds = new ArrayList<>();
        this.declinedParticipantIds = new ArrayList<>();
    }

    /**
     * Constructs an Event with a specific Firestore database instance.
     *
     * @param db The Firestore database instance.
     */
    public Event(FirebaseFirestore db) {
        this.db = db;
        this.waitingList= new WaitingList();
        //waiting list constructor will handle these 4 list
        this.waitingparticipantIds = new ArrayList<>();
        this.acceptedParticipantIds = new ArrayList<>();
        this.canceledParticipantIds = new ArrayList<>();
        this.signedUpParticipantIds = new ArrayList<>();
        this.enrolledParticipantIds = new ArrayList<>();
        this.declinedParticipantIds = new ArrayList<>();
    }
    /**
     * Constructs an Event with Firestore and FirebaseStorage dependencies.
     *
     * @param db      The Firestore database instance.
     * @param storage The Firebase storage instance.
     */
    public Event(FirebaseFirestore db, FirebaseStorage storage) {
        this.db = db;
        this.storage = storage;
        this.waitingparticipantIds = new ArrayList<>();
        this.acceptedParticipantIds = new ArrayList<>();
        this.canceledParticipantIds = new ArrayList<>();
        this.signedUpParticipantIds = new ArrayList<>();
        this.enrolledParticipantIds = new ArrayList<>();
        this.declinedParticipantIds = new ArrayList<>();
    }
    /**
     * Constructs an Event with specific parameters.
     *
     * @param eventId          The unique identifier of the event.
     * @param eventTitle       The title of the event.
     * @param description      The description of the event.
     * @param imageUrl         The URL of the event image.
     * @param timestamp        The timestamp of the event.
     * @param locationstr      The location of the event.
     * @param maxParticipants  The maximum number of participants allowed.
     * @param organizerId      The ID of the organizer for this event.
     */

    public Event(String eventId, String eventTitle, String description, String imageUrl, long timestamp, String locationstr, int maxParticipants, String organizerId) {
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.description = description;
        this.eventPosterURL = imageUrl;
        this.timestamp = timestamp;

        //this.location = location;
        this.maxParticipants = maxParticipants;
        this.waitingList=new WaitingList(eventId);
        this.waitingparticipantIds = new ArrayList<>();
        this.acceptedParticipantIds = new ArrayList<>();
        this.canceledParticipantIds = new ArrayList<>();
        this.signedUpParticipantIds = new ArrayList<>();
//        this.waitingList = new WaitingList(eventId);
        this.organizerId = organizerId;
        this.storage = FirebaseStorage.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }


    /**
     * Gets the event ID.
     *
     * @return The event ID.
     */
    public String getEventId() { return eventId; }
    /**
     * Sets the event ID.
     *
     * @param eventId The event ID to set.
     */
    public void setEventId(String eventId) { this.eventId = eventId; }
    /**
     * get event id
     * @return eventTitle*/
    public String getEventTitle() { return eventTitle; }
    /**
     * set eventTitle
     * @param eventTitle*/
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    /**
     * get event description
     * @return description*/
    public String getDescription() { return description; }
    /**
     * set description
     * @param description*/
    public void setDescription(String description) { this.description = description; }
    /**
     * get image URL
     * @return imageUrl*/
    public String getImageUrl() { return imageUrl; }
    /**
     * set Image url
     * @param imageUrl */
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    /**
     * get time stamp
     *@return timestamp */
    public long getTimestamp() { return timestamp; }
    /**
     * set Time stamp
     * @param timestamp */
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    /**
     * checks geolocation
     * @return geolocationRequired */
    public boolean isGeolocationRequired() {
        return geolocationRequired;
    }
    /**
     * get geolocation
     * @param geolocationRequired  */
    public void setGeolocationRequired(boolean geolocationRequired) {
        this.geolocationRequired = geolocationRequired;
    }
    /**
     * get address
     * @return address */
    public String getAddress() { return address; }
    /**
     * set address
     * @param new_location
     * @return address, new location */
    public void setAddress(String new_location) { this.address = new_location; }
    /**
     * get Max participant
     * @return maxParticipants*/
    public int getMaxParticipants() { return maxParticipants; }
    /**
     * set Max participant
     * @param maxParticipants */
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }
    /**
     * get current waiting list of the event
     * @return waitingList*/
    public WaitingList getWaitingList() { return waitingList; }
    /**
     * get organizer Id
     * @return organizer Id*/
    public String getOrganizerId() { return organizerId; }
    /**
     * set organizer id
     * @param organizerId */
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }
    /**
     * get location
     * @return location */
    public String getLocation() {
        return location;
    }
    /**
     * set location
     * @param location */
    public void setLocation(String location) {
        this.location = location;
    }
    /**
     * get qr code hashed
     * @return qrcodehash
     */
    public String getQRcodeHash() {
        return qrcodehash;
    }

    /**
     * set qr code hashed
     * @param QRcodeHash
     */
    public void setQRcodeHash(String QRcodeHash) {
        this.qrcodehash = QRcodeHash;
    }

    /**
     * get defaultEventPosterURL
     * @return defaultEventPosterURL
     */

    public String getDefaultEventPosterURL() {
        return defaultEventPosterURL;
    }

    /**
     * set defaultEventPosterURL
     * @param defaultEventPosterURL
     */
    public void setDefaultEventPosterURL(String defaultEventPosterURL) {
        this.defaultEventPosterURL = defaultEventPosterURL;
    }

    /**
     * get eventPosterURL
     * @return eventPosterURL
     */

    public String getEventPosterURL() {
        return eventPosterURL;
    }
    /**
     * set eventPictureUrl
     * @param eventPictureUrl
     */
    public void setEventPosterURL(String eventPictureUrl) {
        this.eventPosterURL = eventPictureUrl;
    }

    public int getWaitingListLimit() {
        return waitingListLimit;
    }

    public void setWaitingListLimit(int waitingListLimit) {
        this.waitingListLimit = waitingListLimit;
    }

    /**
     * Adding new information here for the different lists that are in the event
     * the first part is getters, the last bit are moving from one list to another,
     * can do the same thing with the WaitingList class but leaving these as is for now */
    public List<String> getAcceptedParticipantIds() {
        return acceptedParticipantIds;
    }
    public void addAcceptedParticipantId(String participantId){acceptedParticipantIds.add(participantId);}
    public void setAcceptedParticipantIds(List <String> acceptedList){
        if(acceptedList != null)
            this.acceptedParticipantIds = new ArrayList<>(acceptedList);
        else
            this.acceptedParticipantIds = new ArrayList<>();
    }
    public List<String> getCanceledParticipantIds() {
        return canceledParticipantIds;
    }
    public void addCanceledParticipantIds(String participantId){canceledParticipantIds.add(participantId);}
    public void setCanceledParticipantIds(List <String> canceledList){
        if(canceledList != null)
            this.canceledParticipantIds = new ArrayList<>(canceledList);
        else
            this.canceledParticipantIds = new ArrayList<>();
    }
    public List<String> getSignedUpParticipantIds() {
        return signedUpParticipantIds;
    }
    public void addSignedUpParticipantIds(String participantId){signedUpParticipantIds.add(participantId);}
    public void setSignedUpParticipantIds(List <String> signedUpList){
        if(signedUpList != null)
            this.signedUpParticipantIds = new ArrayList<>(signedUpList);
        else
            this.signedUpParticipantIds = new ArrayList<>();
    }
    public List<String> getWaitingParticipantIds() {
        return waitingparticipantIds;
    }
    public void addWaitingParticipantIds(String participantId){waitingparticipantIds.add(participantId);}
    public void setWaitingParticipantIds(List <String> waitingList){
        this.waitingparticipantIds = new ArrayList<>();
        if(waitingList != null)
            this.waitingparticipantIds = new ArrayList<>(waitingList);
        else
            this.waitingparticipantIds = new ArrayList<>();
    }
    public void removeWaitingParticipantId(String participantId){
        if(waitingparticipantIds.contains(participantId)){
            waitingparticipantIds.remove(participantId);
        }
    }
    /**
     * Accepts a participant, moving their ID to the accepted list and removing them from the waiting list.
     *
     * @param entrantId The ID of the participant to accept.
     */
    public void acceptParticipant(String entrantId) {
        if (!acceptedParticipantIds.contains(entrantId)) {
            acceptedParticipantIds.add(entrantId);
            waitingparticipantIds.remove(entrantId);
        }
    }
    /**
     * Cancels a participant's entry, moving their ID to the canceled list and removing them from the accepted list.
     *
     * @param entrantId The ID of the participant to cancel.
     */

    public void cancelParticipant(String entrantId) {
        if (!canceledParticipantIds.contains(entrantId)) {
            canceledParticipantIds.add(entrantId);
            acceptedParticipantIds.remove(entrantId);
        }
    }
    /**
     * Signs up a participant by adding their ID to the signed-up list and removing them from the accepted list.
     *
     * @param entrantId The ID of the participant to sign up.
     */

    public void signUpParticipant(String entrantId) {
        if (!signedUpParticipantIds.contains(entrantId)) {
            signedUpParticipantIds.add(entrantId);
            acceptedParticipantIds.remove(entrantId);

        }
    }


    /**
     * Adds a participant to the waiting list if the maximum participant count is not reached.
     *
     * @param entrantId The ID of the participant to add.
     */
    public void addParticipant(String entrantId){
        if(!waitingparticipantIds.contains(entrantId)&&waitingparticipantIds.size()<maxParticipants){
            waitingparticipantIds.add(entrantId);
        }
    }
    /**
     * Removes a participant from the waiting list.
     *
     * @param entrantId The ID of the participant to remove.
     */
    public void removeParticipant(String entrantId){
        if(waitingparticipantIds.contains(entrantId)){
            waitingparticipantIds.remove(entrantId);}
    }
    /**
     * Generates a URL for the event poster.
     *
     * @return A URL string for the event poster.
     * @throws IllegalArgumentException If the event ID is invalid.
     */
    public String createEventPosterUrl() {
        if (eventId != null && !eventId.isEmpty()) {
            return "https://firebasestorage.googleapis.com/v0/b/YOUR_FIREBASE_PROJECT_ID/o/Events%2F"
                    + eventId + "%2Fervent_poster%2Fervent_poster_" + System.currentTimeMillis() + ".jpg?alt=media";
        } else {
            throw new IllegalArgumentException("Event ID is invalid");
        }
    }
    /**
     * Saves event data to Firestore.
     *
     * @return A Task representing the save operation.
     */

    public Task<Void> saveEventDataToFirestore() {
//        Log.d("Event", "Saving Event to Firestore");
        Map<String, Object> eventData = new HashMap<>();

        Task<Void> saveTask;

        if (eventId == null) {
            Log.d("Event", "EventID null");
            // Generate a new Event ID and then save the data
            saveTask = getNewEventID()
                    .continueWithTask(newEventIDTask -> {
                        if (!newEventIDTask.isSuccessful() || newEventIDTask.getResult() == null) {
                            throw new Exception("Failed to generate new Event ID");
                        }
                        String new_eventID = newEventIDTask.getResult();
                        Log.d("Event", "New EventID: " + new_eventID);
                        eventData.put("eventId", new_eventID);
                        this.eventId = new_eventID;

                        // Populate remaining fields
                        populateEventData(eventData);

                        // Save the data to Firestore
                        return db.collection("Events").document(new_eventID).set(eventData);
                    });
        } else {
//            Log.d("Event", "EventID: " + eventId);
            eventData.put("eventId", eventId);
            populateEventData(eventData);
            saveTask = db.collection("Events").document(eventId).set(eventData);
        }

        return saveTask
                .addOnSuccessListener(
                        aVoid -> {
                            // Log.d("Event", "Event data successfully saved to Firestore.");
                        }
                )
                .addOnFailureListener(e -> {
                    // Log.e("Event", "Error saving event data to Firestore", e);
                });
    }

    /**
     * Populates event data into the given map for Firebase storage.
     *
     * @param eventData Map to be populated with event properties.
     */

    private void populateEventData(Map<String, Object> eventData) {
        eventData.put("eventTitle", eventTitle);
        eventData.put("description", description);
        eventData.put("imageUrl", imageUrl);
        eventData.put("timestamp", timestamp);
        eventData.put("location", address);
        eventData.put("maxParticipants", maxParticipants);
        eventData.put("waitingListLimit", waitingListLimit);
        eventData.put("waitingparticipantIds", waitingparticipantIds);
        eventData.put("acceptedParticipantIds", acceptedParticipantIds);
        eventData.put("canceledParticipantIds", canceledParticipantIds);
        eventData.put("signedUpParticipantIds", signedUpParticipantIds);
        eventData.put("declinedParticipantIds", declinedParticipantIds);
        eventData.put("organizerId", organizerId);
        eventData.put("eventPosterURL", eventPosterURL);
        eventData.put("defaultEventPosterURL", defaultEventPosterURL);
        eventData.put("geolocationRequired", geolocationRequired);
        if(qrcodehash == null){
            QRcodeGenerator qrCodeGenerator = new QRcodeGenerator();
            String hashInput = eventId + Calendar.getInstance().getTime();
            String qrCodeHash = qrCodeGenerator.createQRCodeHash(hashInput);
            this.qrcodehash = qrCodeHash;
        }

        eventData.put("qrcodehash", qrcodehash);
    }

    /**
     * Updates event data in Firebase Firestore.
     *
     * @param newTitle                  New title for the event.
     * @param newDescription            New description for the event.
     * @param newLocation               New location for the event.
     * @param newMaxParticipants        New maximum participant count.
     * @param newOrganizerId            New organizer ID.
     * @param newWaitingparticipantIds  Updated waiting list of participants.
     * @param newAcceptedParticipantIds Updated list of accepted participants.
     * @param newCanceledParticipantIds Updated list of canceled participants.
     * @param newSignedUpParticipantIds Updated list of signed-up participants.
     */
    public Task<Void> updateEventData(String newTitle, String newDescription, String newLocation, int newMaxParticipants, String newOrganizerId,
                                      List<String> newWaitingparticipantIds, List<String> newAcceptedParticipantIds,
                                      List<String> newCanceledParticipantIds, List<String> newSignedUpParticipantIds) {
        // Update the event properties with the new values
        this.eventTitle = newTitle;
        this.description = newDescription;
        this.location = newLocation;
        this.address = newLocation;
        this.maxParticipants = newMaxParticipants;
        this.organizerId = newOrganizerId;
        this.waitingparticipantIds = newWaitingparticipantIds;
        this.acceptedParticipantIds = newAcceptedParticipantIds;
        this.canceledParticipantIds = newCanceledParticipantIds;
        this.signedUpParticipantIds = newSignedUpParticipantIds;


        // Prepare the updated event data map
        Map<String, Object> eventData = new HashMap<>();
        populateEventData(eventData);

        // Save or update the event data in Firestore
        return db.collection("Events").document(eventId)
                .set(eventData)
                .addOnSuccessListener(aVoid -> {
//                    Log.d("Event", "Event data successfully updated in Firestore.");
                })
                .addOnFailureListener(e -> {
//                    Log.e("Event", "Error updating event data in Firestore: " + e.getMessage());
                });
    }
    /**
     * Generates a new event ID based on the number of existing events in Firestore.
     *
     * @return A Task containing the newly generated event ID as a String.
     */

    public Task<String> getNewEventID() {
        // Retrieve all documents in the "Events" collection
        Task<QuerySnapshot> queryTask = db.collection("Events").get();
        if (queryTask == null) {
            // Handle case where Firestore query fails
            return Tasks.forException(new NullPointerException("Firestore query returned null"));
        }
        // Continue the task to generate a new event ID
        return queryTask.continueWith(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                // Throw an exception if the task fails or result is null
                throw task.getException() != null
                        ? task.getException()
                        : new Exception("Failed to fetch event count");
            }
            // Create a unique event ID by appending the current count of events
            int newIdNumber = task.getResult().size() + 1;
            return "eventID" + newIdNumber;
        });
    }
    /**
     * Retrieves an event by its ID from Firestore.
     *
     * @param eventId           The ID of the event to retrieve.
     * @param onSuccessListener Callback invoked when the event is successfully retrieved.
     * @param onFailureListener Callback invoked when an error occurs during retrieval.
     */
    public static void findEventById(String eventId, OnSuccessListener<Event> onSuccessListener, OnFailureListener onFailureListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Convert the Firestore document to an Event object
                        Event event = documentSnapshot.toObject(Event.class);
                        onSuccessListener.onSuccess(event);
                    } else {
                        // Return null if the event is not found
                        onSuccessListener.onSuccess(null); // Event not found
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    /**
     * Adds a user to the declined participant list for the event.
     *
     * @param userId The ID of the user to add to the declined participant list.
     */
    public void addDeclinedParticipantId(String userId) {
        // Check if the user is already in the declined list
        if (!declinedParticipantIds.contains(userId)) {
            // Add the user ID to the declined list if not present
            declinedParticipantIds.add(userId);
        }
    }

    /**
     * Retrieves the list of declined participant IDs.
     *
     * @return A list of IDs of participants who declined the event.
     */
    public List<String> getDeclinedParticipantIds() {
        return declinedParticipantIds;
    }

    /**
     * Sets a mock Firestore instance for testing purposes.
     *
     * @param mockFirestore The mocked Firestore instance to use.
     */
    public void setFirestore(FirebaseFirestore mockFirestore) {
        db = mockFirestore;
    }


    /**
     * Retrieves events related to a specific user.
     * A user-related event means the user is listed in one of the event's participant lists.
     *
     * @param userId     The ID of the user whose events are to be retrieved.
     * @param onSuccess  Callback invoked with the list of user-related events on success.
     * @param onFailure  Callback invoked if an error occurs during retrieval.
     */
    public static void getUserEvents(String userId, OnSuccessListener<List<Event>> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> userEvents = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Event event = new Event();
                        event = doc.toObject(Event.class);
//                        if(event.getWaitingParticipantIds() == null)
//                            event.setWaitingParticipantIds(new ArrayList<>());

                        Log.d("Events", "UserID: " + userId);
                        Log.d("Events", "eventID: " + event.getEventId());
                        Log.d("Events", "Wait: " + event.getWaitingParticipantIds().size());
                        Log.d("Events", "Accpet: " + event.getAcceptedParticipantIds().size());
                        Log.d("Events", "canale: " + event.getCanceledParticipantIds().size());
                        Log.d("Events", "sign: " + event.getSignedUpParticipantIds().size());

                        if (event != null &&
                                (event.getAcceptedParticipantIds().contains(userId)
                                        || event.getWaitingParticipantIds().contains(userId)
                                        || event.getCanceledParticipantIds().contains(userId)
                                        || event.getSignedUpParticipantIds().contains(userId))) {
                            // Add the event to the user's list if related
                            userEvents.add(event);
                        }
                    }
                    onSuccess.onSuccess(userEvents);
                })
                .addOnFailureListener(onFailure); // Handle errors
    }

    /**
     * Retrieves all events from Firestore.
     *
     * @param onSuccess Callback invoked with the list of all events on success.
     * @param onFailure Callback invoked if an error occurs during retrieval.
     */
    public static void getAllEvents(OnSuccessListener<List<Event>> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Query Firestore for all events in the "Events" collection
        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> userEvents = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            // Add each event to the list
                            Log.d("Event", "Adding event");
                            userEvents.add(event);
                        }
                    }
                    onSuccess.onSuccess(userEvents);
                })
                .addOnFailureListener(onFailure); // HANDLE ERRORS
    }
    /**
     * Retrieves all events organized by a specific user.
     *
     * @param userId    The ID of the organizer whose events are to be retrieved.
     * @param onSuccess Callback invoked with the list of organizer events on success.
     * @param onFailure Callback invoked if an error occurs during retrieval.
     */
    public static void getOrganizerEvents(String userId, OnSuccessListener<List<Event>> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Query Firestore for events where the organizer ID matches the user ID
        db.collection("Events")
                .whereEqualTo("organizerId",userId) // Query by organizer ID
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> userEvents = new ArrayList<>();
                    // Add all events organized by the user to the list
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            userEvents.add(event); // Add event if it belongs to the organizer
                        }
                    }
                    // Invoke the success callback with the organizer's event list
                    onSuccess.onSuccess(userEvents);
                })
                .addOnFailureListener(onFailure); //HANDLE ERRORS
    }

    /**
     * This function deletes all events in Firebase where the organizerID matches the given ID.
     *
     * @param organizerID The ID of the organizer whose events need to be deleted.
     * @param onSuccess Callback to be invoked if the deletion succeeds.
     * @param onFailure Callback to be invoked if the deletion fails.
     */
    public static void deleteEventsByOrganizer(String organizerID, OnSuccessListener<Void> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events")
                .whereEqualTo("organizerID", organizerID)  // Query to find events with the specified organizerID
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        // Delete the event document
                        db.collection("Events")
                                .document(doc.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Event", "Event with ID " + doc.getId() + " deleted successfully.");
                                })
                                .addOnFailureListener(onFailure);
                    }
                    onSuccess.onSuccess(null);
                })
                .addOnFailureListener(onFailure);
    }

    //poster stuff
    //generat edeafult poster
    /**
     * Generates a default poster for the event as a bitmap image.
     * The poster features the first letter of the event title or a default "E" if the title is not provided.
     *
     * @param eventTitle The title of the event. If null or empty, "E" is used as the default.
     * @return A Bitmap image representing the generated poster.
     */
    public Bitmap generateDefaultPoster(String eventTitle) {
        String letter = (eventTitle == null || eventTitle.isEmpty()) ? "E" : String.valueOf(eventTitle.charAt(0)).toUpperCase();
        int size = 300; // Larger size for poster
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));
        canvas.drawRect(0, 0, size, size, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        float yPos = (canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText(letter, size / 2, yPos, paint);


        return bitmap;
    }
    /**
     * Generates a default poster for the given event title and uploads it to Firebase Storage.
     * The poster is saved in the "default_posters" folder.
     *
     * @param eventTitle The title of the event. If null or empty, a default "E" will be used for the poster.
     * @return A Task<Void> representing the completion of the upload process. The task fails if the upload or poster generation fails.
     */
    public Task<Void> uploadDefaultPoster(String eventTitle) {
        Bitmap bitmap = generateDefaultPoster(eventTitle);

        // Convert the bitmap to a byte array
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // Upload to Firebase Storage
        return uploadPosterToFirebaseStorage(byteArray, "default_posters");
    }
    /**
     * Uploads a poster image to Firebase Storage and updates the event's Firestore document with the URL.
     *
     * @param imageBytes The byte array representing the poster image.
     * @param folderName The folder in Firebase Storage where the poster will be uploaded.
     * @return A Task that completes when the upload and Firestore update are successful.
     * @throws Exception If any error occurs during upload or Firestore update.
     */
    public Task<Void> uploadPosterToFirebaseStorage(byte[] imageBytes, String folderName) {
        String posterFileName = folderName + "/" + UUID.randomUUID().toString() + ".png";
        StorageReference posterRef = storage.getReference().child(posterFileName);


        return posterRef.putBytes(imageBytes)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return posterRef.getDownloadUrl();
                })
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    Uri downloadUri = task.getResult();
                    String downloadUrl = downloadUri.toString();
                    this.defaultEventPosterURL = downloadUrl;
                    this.eventPosterURL = downloadUrl;


                    // Save the updated URL to Firestore
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("eventPosterURL", downloadUrl);
                    updates.put("defaultEventPosterURL", downloadUrl);
//                    updates.put("imageUrl", downloadUrl);


                    return db.collection("Events").document(eventId).update(updates);
                });
    }
    /**
     * Uploads a custom poster image to Firebase Storage and updates the event's image URL in Firestore.
     *
     * @param imageUri The URI of the custom poster image to upload.
     * @return A Task<Void> representing the completion of the upload process. The task fails if either the upload
     *         or Firestore update fails.
     */
    public Task<Void> uploadCustomPoster(Uri imageUri) {
        StorageReference posterRef = storage.getReference().child("event_posters/" + UUID.randomUUID().toString() + ".png");


        return posterRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return posterRef.getDownloadUrl();
                })
                .continueWithTask(downloadUriTask -> {
                    if (!downloadUriTask.isSuccessful()) {
                        throw downloadUriTask.getException();
                    }
                    this.eventPosterURL = downloadUriTask.getResult().toString();
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("eventPosterURL", this.eventPosterURL);


                    return db.collection("Events").document(eventId).update(updates);
                })
                .addOnSuccessListener(aVoid -> Log.d("Event", "Poster uploaded and URL updated successfully"))
                .addOnFailureListener(e -> Log.e("Event", "Failed to upload poster or update URL", e));
    }

    // Update event poster
    public void updateEventPoster(Uri newPosterUri) {
        uploadCustomPoster(newPosterUri);
    }

    /**
     * Deletes the specified poster from Firebase Storage and resets the event poster to the default.
     *
     * @param posterUrl The URL of the poster to delete.
     * @return A Task<Void> representing the completion of the deletion process. The task fails if the poster
     *         deletion or default poster upload fails.
     */
    public Task<Void> deleteSelectedPosterFromFirebase(String posterUrl) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(posterUrl);

        return storageRef.delete()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    this.imageUrl = defaultEventPosterURL;
                    return uploadDefaultPoster(eventTitle);
                })
                .addOnSuccessListener(aVoid -> Log.d("FirebaseStorage", "Poster deleted and reset to default successfully."))
                .addOnFailureListener(e -> Log.e("FirebaseStorage", "Failed to delete or reset poster.", e));
    }
    /**
     * Checks if the current event poster is the default poster.
     *
     * @return true if the event poster URL matches the default poster URL, false otherwise.
     */
    public boolean isDefaultPoster() {
        return imageUrl != null && imageUrl.equals(defaultEventPosterURL);
    }
    /**
     * Generates a new QR code hash for the event and saves the event data to Firestore.
     * If the application is in testing mode, updates the hash locally instead.
     */
    public void getNewEventQRHash(){
        QRcodeGenerator qrCodeGenerator = new QRcodeGenerator();
        this.qrcodehash = qrCodeGenerator.createQRCodeHash(eventId);
        if(!UniversalProgramValues.getInstance().getTestingMode()){
            saveEventDataToFirestore()
                    .addOnSuccessListener(aVoid -> {

                        Log.d("Event", "Event successfully saved!");
                    })
                    .addOnFailureListener(e -> Log.e("Event", "Error saving event", e));
        }
        else{
            for(int i = 0; i < UniversalProgramValues.getInstance().getEventList().size(); i++){
                if(Objects.equals(UniversalProgramValues.getInstance().getEventList().get(i).getEventId(), eventId)){
                    UniversalProgramValues.getInstance().getEventList().get(i).setQRcodeHash(qrcodehash);
                }
            }

        }

    }
    /**
     * Represents an event with details such as title, description, participants, and organizer information.
     * Implements Parcelable to enable passing Event objects between Android components.
     */

    protected Event(Parcel in) {
        eventId = in.readString();
        eventTitle = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        timestamp = in.readLong();
        eventPosterURL = in.readString();
        defaultEventPosterURL = in.readString();
        address = in.readString();
        location = in.readString();
        maxParticipants = in.readInt();
        waitingparticipantIds = in.createStringArrayList();
        acceptedParticipantIds = in.createStringArrayList();
        canceledParticipantIds = in.createStringArrayList();
        signedUpParticipantIds = in.createStringArrayList();
        enrolledParticipantIds = in.createStringArrayList();
        declinedParticipantIds = in.createStringArrayList();
        waitingList = in.readParcelable(WaitingList.class.getClassLoader());
        organizerId = in.readString();
        qrcodehash = in.readString();
    }
    /**
     * Writes the Event object's data to a Parcel.
     *
     * @param dest  The Parcel in which the data should be written.
     * @param flags Additional flags about how the object should be written.
     */

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventId);
        dest.writeString(eventTitle);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeLong(timestamp);
        dest.writeString(eventPosterURL);
        dest.writeString(defaultEventPosterURL);
        dest.writeString(address);
        dest.writeString(location);
        dest.writeInt(maxParticipants);
        dest.writeStringList(waitingparticipantIds);
        dest.writeStringList(acceptedParticipantIds);
        dest.writeStringList(canceledParticipantIds);
        dest.writeStringList(signedUpParticipantIds);
        dest.writeStringList(enrolledParticipantIds);
        dest.writeStringList(declinedParticipantIds);
        dest.writeString(organizerId);
        dest.writeString(qrcodehash);
    }
    /**
     * Describes the contents of the Parcelable instance.
     *
     * @return An integer indicating the content type (default is 0).
     */

    @Override
    public int describeContents() {
        return 0;
    }
    /**
     * A public static field that generates instances of the Event class from a Parcel.
     */

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        /**
         * Creates a new Event instance from the data in the Parcel.
         *
         * @param in The Parcel containing the Event data.
         * @return A new Event object.
         */
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }
        /**
         * Creates a new array of the Event class.
         *
         * @param size The size of the array to be created.
         * @return An array of Event objects.
         */
        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}


