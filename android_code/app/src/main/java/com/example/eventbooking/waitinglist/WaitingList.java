

package com.example.eventbooking.waitinglist;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
//todo add waitinglist limit

public class WaitingList {
    private String eventId;
    private int maxParticipants;
    //private int waitingListLimit;

    private List<String> waitingParticipantIds;
    private List<String> acceptedParticipantIds;
    private List<String> signedUpParticipantIds;
    private List<String> canceledParticipantIds;
    private List<String> enrolledParticipantIds;

    // Default constructor required for calls to DataSnapshot.getValue(WaitingList.class)
    public WaitingList() {
        this.waitingParticipantIds = new ArrayList<>();
        this.acceptedParticipantIds = new ArrayList<>();
        this.signedUpParticipantIds = new ArrayList<>();
        this.canceledParticipantIds = new ArrayList<>();
    }

    /**
     * Constructs a WaitingList with the specified parameters.
     *
     * @param eventId             The ID of the event.
     *
     *
     */
    public WaitingList(String eventId) {
        this.eventId = eventId;
        this.maxParticipants = maxParticipants;
        //this.waitingListLimit = waitingListLimit;
        this.waitingParticipantIds = new ArrayList<>();
        this.acceptedParticipantIds = new ArrayList<>();
        this.signedUpParticipantIds = new ArrayList<>();
        this.canceledParticipantIds = new ArrayList<>();
    }

    // Getters and Setters for all fields

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

//    public int getWaitingListLimit() {
//        return waitingListLimit;
//    }
//
//    public void setWaitingListLimit(int waitingListLimit) {
//        this.waitingListLimit = waitingListLimit;
//    }

    public List<String> getWaitingParticipantIds() {
        return waitingParticipantIds;
    }

    public void setWaitingParticipantIds(List<String> waitingParticipantIds) {
        this.waitingParticipantIds = waitingParticipantIds;
    }

    public List<String> getAcceptedParticipantIds() {
        return acceptedParticipantIds;
    }

    public void setAcceptedParticipantIds(List<String> selectedParticipantIds) {
        this.acceptedParticipantIds = selectedParticipantIds;
    }

    public List<String> getSignedUpParticipantIds() {
        return signedUpParticipantIds;
    }

    public void setSignedUpParticipantIds(List<String> signedUpParticipantIds) {
        this.signedUpParticipantIds = signedUpParticipantIds;
    }

    public List<String> getCanceledParticipantIds() {
        return canceledParticipantIds;
    }

    public void setCanceledParticipantIds(List<String> canceledParticipantIds) {
        this.canceledParticipantIds = canceledParticipantIds;
    }

    // Methods to update the lists

    /**
     * Adds a participant to the waiting list.
     *
     * @param participantId The ID of the participant to add.
     * @return A message indicating the result of the operation.
     */
    public boolean addParticipantToWaitingList(String participantId) {
        if (waitingParticipantIds.contains(participantId) ||
                acceptedParticipantIds.contains(participantId) ||
                signedUpParticipantIds.contains(participantId)
        ) {
            waitingParticipantIds.add(participantId);
            return true;
        }return false;
    }

//        if (waitingParticipantIds.size() < waitingListLimit) {
//            waitingParticipantIds.add(participantId);
//            return "Participant added to the waiting list.";
//        } else {
//            return "Waiting list is full. Unable to add participant.";
//        }


    /**
     * Samples attendees from the waiting list and moves them to the selected list.
     *
     * @param sampleSize The number of attendees to sample.
     * @return A list of participant IDs who have been selected.
     */
    public List<String> sampleParticipants(int sampleSize) {
        List<String> selectedParticipants = new ArrayList<>();
        Random random = new Random();

        if (waitingParticipantIds.isEmpty() || sampleSize <= 0) {
            return selectedParticipants; // Return empty list if no participants can be sampled
        }

        int actualSampleSize = Math.min(sampleSize, waitingParticipantIds.size());

        for (int i = 0; i < actualSampleSize; i++) {
            // Randomly select a participant from the waiting list
            int index = random.nextInt(waitingParticipantIds.size());
            String participantId = waitingParticipantIds.remove(index);
            acceptedParticipantIds.add(participantId);
            selectedParticipants.add(participantId);
        }

        return selectedParticipants;
    }

    /**
     * Participant confirms their attendance and moves from selected to signed up.
     *
     * @param participantId The ID of the participant confirming attendance.
     * @return A message indicating the result of the operation.
     */
    public String participantSignsUp(String participantId) {
        if (!acceptedParticipantIds.contains(participantId)) {
            return "Participant is not in the selected list.";
        }

        if (signedUpParticipantIds.size() < maxParticipants) {
            acceptedParticipantIds.remove(participantId);
            signedUpParticipantIds.add(participantId);
            return "Participant has confirmed attendance.Event hasn't full";
        } else {
            return "Event is full. Unable to confirm attendance.";
        }
    }

    /**
     * Cancels a participant's participation, removing them from any lists they are on.
     *
     * @param participantId The ID of the participant to cancel.
     * @return A message indicating the result of the cancellation.
     */
    public boolean cancelParticipation(String participantId) {
        boolean removed = false;

        if (waitingParticipantIds.remove(participantId)) {
            removed = true;
        }
        if (acceptedParticipantIds.remove(participantId)) {
            removed = true;
        }

        if (removed) {
            canceledParticipantIds.add(participantId);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Draws a replacement from the waiting list when a participant cancels after signing up.
     *
     * @return The ID of the participant who has been moved from waiting to selected, or null if none.
     */
    public List<String> drawReplacement(int repalcementSize) {
        // Calculate the number of available spots
        int availableSpots = maxParticipants - signedUpParticipantIds.size();

        if (availableSpots <= 0 || waitingParticipantIds.isEmpty()) {
            return new ArrayList<>(); // No spots available or no participants to replace
        }

        // Determine how many replacements to draw
        int replacementsToDraw = Math.min(availableSpots, waitingParticipantIds.size());

        return sampleParticipants(replacementsToDraw);
    }

    //save and load waiting list data from firebase?

    public Task<DocumentSnapshot> loadFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("Events").document(eventId);

        return eventRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                // Initialize lists with data from Firebase
                waitingParticipantIds = (List<String>) snapshot.get("waitingparticipantIds");
                acceptedParticipantIds = (List<String>) snapshot.get("acceptedParticipantIds");
                signedUpParticipantIds = (List<String>) snapshot.get("signedUpParticipantIds");
                canceledParticipantIds = (List<String>) snapshot.get("canceledParticipantIds");

                // Ensure lists are not null
                if (waitingParticipantIds == null) waitingParticipantIds = new ArrayList<>();
                if (acceptedParticipantIds == null) acceptedParticipantIds = new ArrayList<>();
                if (signedUpParticipantIds == null) signedUpParticipantIds = new ArrayList<>();
                if (canceledParticipantIds == null) canceledParticipantIds = new ArrayList<>();
            }
        });
    }


    // Update the waiting list data to Firebase
    public Task<Void> updateToFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("Events").document(eventId);

        Map<String, Object> updates = Map.of(
                "waitingparticipantIds", waitingParticipantIds,
                "acceptedParticipantIds", acceptedParticipantIds,
                "signedUpParticipantIds", signedUpParticipantIds,
                "canceledParticipantIds", canceledParticipantIds
        );

        return eventRef.update(updates).addOnSuccessListener(aVoid -> {
            System.out.println("Waiting list data successfully synchronized with Firebase.");
        }).addOnFailureListener(e -> {
            System.out.println("Failed to update waiting list data in Firebase: " + e.getMessage());
        });
    }



}