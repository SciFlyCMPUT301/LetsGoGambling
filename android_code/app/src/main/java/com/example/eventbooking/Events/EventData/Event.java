package com.example.eventbooking.Events.EventData;

import android.net.Uri;
import android.os.UserManager;
import android.provider.ContactsContract;

import com.example.eventbooking.Location;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.example.eventbooking.Role;
import com.example.eventbooking.User;
//import waitinglist
import com.example.eventbooking.waitinglist.WaitingList;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Event {

    private String eventId;
    private String eventTitle;
    private String description;
    private String imageUrl; // URL of the event image in Firebase Storage
    private long timestamp; // Event time in milliseconds
    private Location location;

    private int maxParticipants; // limit number of entrants
    private List<String> participantIds;
    private WaitingList waitingList;
    private String organizerId;
    private FirebaseFirestore db;
    private FirebaseStorage storage;


    public Event() {
        storage = FirebaseStorage.getInstance(); // Initialize Firebase Storage
        db = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    public Event(String eventId, String eventTitle, String description, String imageUrl, long timestamp, Location location, int maxParticipants, String organizerId) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.participantIds = new ArrayList<>();
        this.waitingList = new WaitingList(eventId);
        this.organizerId = organizerId;
        this.storage = FirebaseStorage.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public Location getLocation() { return location; }
    public void setLocation(Location new_location) { this.location = new_location; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public List<String> getParticipantIds() { return participantIds; }
    public void setParticipantIds(List<String> participantIds) { this.participantIds = participantIds; }

    public WaitingList getWaitingList() { return waitingList; }
    public void setWaitingList(WaitingList waitingList) { this.waitingList = waitingList; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    //manage the participants
    public void addParticipant(String entrantId){
        if(!participantIds.contains(entrantId)&&participantIds.size()<maxParticipants){
            participantIds.add(entrantId);
        }
    }
    public void removeParticipant(String entrantId){
        if(participantIds.contains(entrantId)){
        participantIds.remove(entrantId);}
    }

    public String createEventPosterUrl() {
        if (eventId != null && !eventId.isEmpty()) {
            return "https://firebasestorage.googleapis.com/v0/b/YOUR_FIREBASE_PROJECT_ID/o/Events%2F"
                    + eventId + "%2Fervent_poster%2Fervent_poster_" + System.currentTimeMillis() + ".jpg?alt=media";
        } else {
            throw new IllegalArgumentException("Event ID is invalid");
        }
    }

    public void saveEvenDataToFirestore() {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", eventId);
        eventData.put("eventTitle", eventTitle);
        eventData.put("description", description);
        eventData.put("imageUrl", imageUrl);
        eventData.put("timestamp", timestamp);
        eventData.put("location", location != null ? location.toString() : null); // Assuming Location has a toString() method
        eventData.put("maxParticipants", maxParticipants);
        eventData.put("participantIds", participantIds);
        eventData.put("organizerId", organizerId);

        // Save or update the event data in Firestore
        db.collection("Events").document(eventId)
                .set(eventData)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Event data successfully saved to Firestore.");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error saving event data to Firestore: " + e.getMessage());
                });
        }

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

    public void updateEventPosterToFirebase(String newPoster) {
        uploadEventPosterToFirebase(newPoster);
    }

}
