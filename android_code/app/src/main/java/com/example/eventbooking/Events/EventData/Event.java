package com.example.eventbooking.Events.EventData;

import android.net.Uri;
import android.os.UserManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.eventbooking.Location;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.example.eventbooking.Role;
import com.example.eventbooking.User;

import com.example.eventbooking.waitinglist.WaitingList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The Event class represents an event in the system, encapsulating details such as title,
 * description, location, maximum participants, and lists for participant statuses (e.g., accepted,
 * canceled). This class provides methods for managing event data and interacting with Firebase
 * services for storage, retrieval, and updates.
 */
public class Event {
    private String eventId;
    private String eventTitle;
    private String description;
    private String imageUrl; // URL of the event image in Firebase Storage
    private long timestamp; // Event time in milliseconds
//    private Location location;

    private String address;
    private String location;
    private int maxParticipants; // limit number of entrants
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
    /**
     * Default constructor that initializes Firebase Firestore and Storage instances, as well as
     * participant lists.
     */

    public Event() {
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        this.waitingList= new WaitingList();
        //waiting list constructor will handle these 4 list
        this.waitingparticipantIds = new ArrayList<>();
        this.acceptedParticipantIds = new ArrayList<>();
        this.canceledParticipantIds = new ArrayList<>();
        this.signedUpParticipantIds = new ArrayList<>();
        this.enrolledParticipantIds = new ArrayList<>();
        this.declinedParticipantIds = new ArrayList<>();

    }

    public Event(int eventID) {
        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        this.waitingList=new WaitingList();
    }

    /**
     * Secondary constructor
     *
     * @param db
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
        this.imageUrl = imageUrl;
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


    /** get event id
     * @return eventId*/
    public String getEventId() { return eventId; }
    /**
     * set event id
     * @param eventId*/
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
     * @param timestamp*/
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

//    public Location getLocation() { return location; }
//    public void setLocation(Location new_location) { this.location = new_location; }

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
     * Adding new information here for the different lists that are in the event
     * the first part is getters, the last bit are moving from one list to another,
     * can do the same thing with the WaitingList class but leaving these as is for now */
    public List<String> getAcceptedParticipantIds() {
        return acceptedParticipantIds;
    }
    public void addAcceptedParticipantId(String participantId){acceptedParticipantIds.add(participantId);}
    public List<String> getCanceledParticipantIds() {
        return canceledParticipantIds;
    }
    public void addCanceledParticipantIds(String participantId){canceledParticipantIds.add(participantId);}
    public List<String> getSignedUpParticipantIds() {
        return signedUpParticipantIds;
    }
    public void addSignedUpParticipantIds(String participantId){signedUpParticipantIds.add(participantId);}
    public List<String> getWaitingParticipantIds() {
        return waitingparticipantIds;
    }
    public void addWaitingParticipantIds(String participantId){waitingparticipantIds.add(participantId);}
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

    public String createEventPosterUrl() {
        if (eventId != null && !eventId.isEmpty()) {
            return "https://firebasestorage.googleapis.com/v0/b/YOUR_FIREBASE_PROJECT_ID/o/Events%2F"
                    + eventId + "%2Fervent_poster%2Fervent_poster_" + System.currentTimeMillis() + ".jpg?alt=media";
        } else {
            throw new IllegalArgumentException("Event ID is invalid");
        }
    }
    /**
     * Saves event data to Firestore with event details and participant lists.
     *
     * @return A task representing the asynchronous save operation.
     */

    public Task<Void> saveEventDataToFirestore() {
        Log.d("Event", "Saving Event to Firestore");
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
            Log.d("Event", "EventID: " + eventId);
            eventData.put("eventId", eventId);
            populateEventData(eventData);
            saveTask = db.collection("Events").document(eventId).set(eventData);
        }

        return saveTask
                .addOnSuccessListener(aVoid -> Log.d("Event", "Event data successfully saved to Firestore."))
                .addOnFailureListener(e -> Log.e("Event", "Error saving event data to Firestore", e));
    }


    private void populateEventData(Map<String, Object> eventData) {
        eventData.put("eventTitle", eventTitle);
        eventData.put("description", description);
        eventData.put("imageUrl", imageUrl);
        eventData.put("timestamp", timestamp);
        eventData.put("location", address);
        eventData.put("maxParticipants", maxParticipants);
        eventData.put("waitingparticipantIds", waitingparticipantIds);
        eventData.put("acceptedParticipantIds", acceptedParticipantIds);
        eventData.put("canceledParticipantIds", canceledParticipantIds);
        eventData.put("signedUpParticipantIds", signedUpParticipantIds);
        eventData.put("declinedParticipantIds", declinedParticipantIds);
        eventData.put("organizerId", organizerId);
    }

    /**
     * update event data to the firebase
     * @param newTitle
     * @param newDescription
     * @param newLocation
     * @param newMaxParticipants
     * @param newOrganizerId
     * @param newWaitingparticipantIds
     * @param newAcceptedParticipantIds
     * @param newCanceledParticipantIds
     * @param newSignedUpParticipantIds
     * @return
     */
    public Task<Void> updateEventData(String newTitle, String newDescription, String newLocation, int newMaxParticipants, String newOrganizerId,
                                      List<String> newWaitingparticipantIds, List<String> newAcceptedParticipantIds,
                                      List<String> newCanceledParticipantIds, List<String> newSignedUpParticipantIds) {
        // Update the event properties with the new values
        this.eventTitle = newTitle;
        this.description = newDescription;
        this.location = newLocation;
        this.maxParticipants = newMaxParticipants;
        this.organizerId = newOrganizerId;
        this.waitingparticipantIds = newWaitingparticipantIds;
        this.acceptedParticipantIds = newAcceptedParticipantIds;
        this.canceledParticipantIds = newCanceledParticipantIds;
        this.signedUpParticipantIds = newSignedUpParticipantIds;
        // Prepare the updated event data map
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", eventId);
        eventData.put("eventTitle", eventTitle);
        eventData.put("description", description);
        eventData.put("location", location);
        eventData.put("maxParticipants", maxParticipants);
        eventData.put("waitingparticipantIds", waitingparticipantIds);
        eventData.put("acceptedParticipantIds", acceptedParticipantIds);
        eventData.put("canceledParticipantIds", canceledParticipantIds);
        eventData.put("signedUpParticipantIds", signedUpParticipantIds);
        eventData.put("organizerId", organizerId);

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

    /***
     * upload the evnet poster to the firebase
     * @param picture
     */
    public void uploadEventPosterToFirebase(String picture) {
        if (eventId == null || eventId.isEmpty()) {
            throw new IllegalArgumentException("Event ID must be set before uploading an event poster.");
        }
        if (picture == null || picture.isEmpty()) {
            throw new IllegalArgumentException("Invalid picture path.");
        }

        StorageReference storageRef = storage.getReference();

        // Create a unique filename for the picture
        String fileName = "event_poster_" + System.currentTimeMillis() + ".jpg";
        StorageReference posterRef = storageRef.child("Events/" + eventId + "/event_poster/" + fileName);

        // Convert the picture path to a Uri
        Uri fileUri = Uri.fromFile(new File(picture));

        // Start the upload task
        UploadTask uploadTask = posterRef.putFile(fileUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL after the upload is successful
            posterRef.getDownloadUrl().addOnSuccessListener(uri -> {
                imageUrl = uri.toString();
                System.out.println("Event Poster uploaded successfully: " + imageUrl);
                Map<String, Object> updates = new HashMap<>();
                updates.put("imageUrl", imageUrl);

                db.collection("Events").document(eventId)
                        .update(updates)
                        .addOnSuccessListener(aVoid -> {
                            System.out.println("Image URL successfully updated in Firestore.");
                        })
                        .addOnFailureListener(e -> {
                            System.out.println("Error updating image URL in Firestore: " + e.getMessage());
                        });
            }).addOnFailureListener(exception -> {
                throw new IllegalArgumentException("Failed to retrieve download URL from Firebase.", exception);
            });
        }).addOnFailureListener(exception -> {
            throw new IllegalArgumentException("Failed to upload the event poster to Firebase.", exception);
        });
    }

    /**
     * update the event poster to firebase
     * @param newPoster
     */
    public void updateEventPosterToFirebase(String newPoster) {
        uploadEventPosterToFirebase(newPoster);
    }

    /**
     * geenrate new event id
     * @return
     */
    public Task<String> getNewEventID() {
        return db.collection("Events").get()
                .continueWith(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        int newIdNumber = task.getResult().size() + 1;
                        return "eventID" + newIdNumber;
                    } else {
                        throw task.getException() != null ? task.getException() : new Exception("Failed to fetch event count");
                    }
                });
    }


    /**
     * retrive the event id from firebase
     * @param eventId
     * @param onSuccessListener
     * @param onFailureListener
     */
    public static void findEventById(String eventId, OnSuccessListener<Event> onSuccessListener, OnFailureListener onFailureListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events").document(eventId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Event event = documentSnapshot.toObject(Event.class);
                        onSuccessListener.onSuccess(event);
                    } else {
                        onSuccessListener.onSuccess(null); // Event not found
                    }
                })
                .addOnFailureListener(onFailureListener);
    }

    /**
     * add user to the declined list
     * @param userId
     */
    // Method to add a user to the declined list
    public void addDeclinedParticipantId(String userId) {
        if (!declinedParticipantIds.contains(userId)) {
            declinedParticipantIds.add(userId);
        }
    }

    /**
     * get user ids from the decline participant
     * @return declinedparticipantIds
     */
    // Getter for declined participant IDs (optional, if needed elsewhere in code)
    public List<String> getDeclinedParticipantIds() {
        return declinedParticipantIds;
    }

    /**
     * Manually setting the firestore
     *
     * @param mockFirestore
     */
    public void setFirestore(FirebaseFirestore mockFirestore) {
        db = mockFirestore;
    }


    /**
     * This function is more of a stop gap measure for the HomeFragment in displaying the Users Events
     * A Event that relates to the User means that the User is in some list for the event
     *
     * @param userId
     * @param onSuccess
     * @param onFailure
     */
    public static void getUserEvents(String userId, OnSuccessListener<List<Event>> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> userEvents = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event != null &&
                                (event.getAcceptedParticipantIds().contains(userId)
                                        || event.getWaitingParticipantIds().contains(userId)
                                        || event.getCanceledParticipantIds().contains(userId)
                                        || event.getSignedUpParticipantIds().contains(userId))) {
                            userEvents.add(event);
                        }
                    }
                    onSuccess.onSuccess(userEvents);
                })
                .addOnFailureListener(onFailure);
    }

    //get organizer event
    public static void getOragnizerEvents(String userId, OnSuccessListener<List<Event>> onSuccess, OnFailureListener onFailure) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Events")
                .whereEqualTo("organizerId",userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Event> userEvents = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Event event = doc.toObject(Event.class);
                        if (event != null) {
                            userEvents.add(event);
                        }
                    }
                    onSuccess.onSuccess(userEvents);
                })
                .addOnFailureListener(onFailure);
    }
}
