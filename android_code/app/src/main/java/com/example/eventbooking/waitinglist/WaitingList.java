
package com.example.eventbooking.waitinglist;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class WaitingList {
    //initialize the variables
    private String eventId;
    private int maxParticipants;
    //private int waitingListLimit;
    private List<String> waitingParticipantIds;
    private List<String> acceptedParticipantIds;
    private List<String> signedUpParticipantIds;
    private List<String> canceledParticipantIds;

    /**
     * Default constructor, initialize 4 related list to the WaitingList object
     * */

    public WaitingList() {
        this.waitingParticipantIds = new ArrayList<>();
        this.acceptedParticipantIds = new ArrayList<>();
        this.signedUpParticipantIds = new ArrayList<>();
        this.canceledParticipantIds = new ArrayList<>();
    }

    /**
     * Constructs a WaitingList bind to it event id
     * initialize the 4 lists and max participant
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
    //getter and setter section starts here
    /**
     * getter of the eventId
     * @return eventId  the ID of current event*/
    public String getEventId() {
        return eventId;
    }
    /**
     * setter of eventId
     * @param eventId  the id of current event */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    /**
     * getter of max participants of current event */
    public int getMaxParticipants() {
        return maxParticipants;
    }
    /**
     * setter of maximum Participants
     * @param maxParticipants  the old maxParticipants of current event*/

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    /**
     * getter of user id that currently in the waiting list
     * @return waitingParticipantIds*/
    public List<String> getWaitingParticipantIds() {
        return waitingParticipantIds;
    }
    /**
     * setter of waiting list participants ids
     * @param waitingParticipantIds  the current event's waiting list */
    public void setWaitingParticipantIds(List<String> waitingParticipantIds) {
        this.waitingParticipantIds = waitingParticipantIds;
    }
    /**
     * getter of accepted Participants ids,
     * accepted means the user elected from the lottery in this case
     * not users that accepted invitations
     * @return acceptedParticipantIds the current selected users id for the event from waiting list*/

    public List<String> getAcceptedParticipantIds() {
        return acceptedParticipantIds;
    }
    /**
     * setter of accepted participant id
     * @param acceptedParticipantIds the old selected Participants*/
    public void setAcceptedParticipantIds(List<String> acceptedParticipantIds) {
        this.acceptedParticipantIds = acceptedParticipantIds;
    }
    /**
     * getter of signed up participant ids, retrive ids of user has accepted the invitation
     * @return signedUpParticipantIds */

    public List<String> getSignedUpParticipantIds() {
        return signedUpParticipantIds;
    }
    /**
     * setter of signed up participant ids
     * @param signedUpParticipantIds the old signed up participant ids of current event */
    public void setSignedUpParticipantIds(List<String> signedUpParticipantIds) {
        this.signedUpParticipantIds = signedUpParticipantIds;
    }
    /**
     * getter of canceled participant ids, user has been selected for the vent but declined invitations
     * @return canceledParticipantIds */

    public List<String> getCanceledParticipantIds() {
        return canceledParticipantIds;
    }
    /**
     * setter of canceled participant Ids
     * @param canceledParticipantIds */

    public void setCanceledParticipantIds(List<String> canceledParticipantIds) {
        this.canceledParticipantIds = canceledParticipantIds;
    }

    // Methods to update the lists

    /**
     * Adds a participant to the waiting list.
     *
     * @param participantId The ID of the participant to add.
     * @return true if the user has been added, false if the user already exist in related list
     */
    public boolean addParticipantToWaitingList(String participantId) {
        //checks if any list contains the user id
        //we allow someone declined the invitation to rejoin the waiting list
        //thus, if condition exclude the canceledParticipantIds
        if (waitingParticipantIds.contains(participantId) ||
                acceptedParticipantIds.contains(participantId) ||
                signedUpParticipantIds.contains(participantId)
        ) {
            waitingParticipantIds.add(participantId);
            return true;
        }return false;
    }


    /**
     * Samples attendees from the waiting list and moves them to the accepted list.
     *
     * @param sampleSize The number of attendees to sample. Should passed max Participant into it
     * @return A list of participant IDs who have been selected.
     */
    public List<String> sampleParticipants(int sampleSize) {
        List<String> selectedParticipants = new ArrayList<>();
        Random random = new Random();

        if (waitingParticipantIds.isEmpty() || sampleSize <= 0) {
            return selectedParticipants; // Return empty list if no participants can be sampled
        }
        //ensures that the sample size wont be greater than the waiting list size

        int actualSampleSize = Math.min(sampleSize, waitingParticipantIds.size());
        //iterate to sample the index inside waiting list,
        // and remove the user Id from the waiting list

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
        //error handling
        if (!acceptedParticipantIds.contains(participantId)) {
            return "Participant is not in the selected list.";
        }
        //ensures the signed Up should not exceed the max participant
        //remove the user form accepted list, added to the signed up list

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
     * This can be called when the result haven't drawn, in the event page user choose to leave waitinglist
     * or when user wish to decline the invitation
     * @param participantId The ID of the participant to cancel.
     * @return A message indicating the result of the cancellation.
     */
    public boolean cancelParticipation(String participantId) {
        //flag to indicate the result
        boolean removed = false;
        //result haven't drawn, user leave waiting list by click "leave" in event page
        if (waitingParticipantIds.remove(participantId)) {
            removed = true;
        }
        //decline invitation
        if (acceptedParticipantIds.remove(participantId)) {
            removed = true;
        }
        //if any list choose to remove the current user, add user to the canceled list

        if (removed) {
            canceledParticipantIds.add(participantId);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Draws a replacement from the waiting list when a participant cancels after signing up.
     * @param repalcementSize how many replacement the organizer want to draw, should not exceed the current avaliable space
     * @return The ID of the participant who has been moved from waiting to selected, or null if none.
     */
    public List<String> drawReplacement(int repalcementSize) {
        // Calculate the number of available spots
        int avaliableSpots = maxParticipants - signedUpParticipantIds.size();

        if (avaliableSpots <= 0 || waitingParticipantIds.isEmpty()) {
            return new ArrayList<>(); // No spots available or no participants to replace
        }

        // Determine how many replacements to draw
        int replacementsToDraw = Math.min(avaliableSpots, waitingParticipantIds.size());
        //drawing result
        return sampleParticipants(replacementsToDraw);
    }

    /**
     * load the 4 lists data from firebase
     * @return return the 4 lists data, if they are not in firebase yet, create the lists then*/

    public Task<DocumentSnapshot> loadFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //path according to the firebase structure
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

    /**
     * update local date into the firebase
     * @return message to indicate the operation result*/
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
