package com.example.eventbooking.waitinglist;




import com.example.eventbooking.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class WaitingList implements Serializable {
    private int maxParticipants;
    private int waitingListLimit; //maximum number of entrant for waiting
    //change to their deviceiD
    private List<String> waitingparticipantIds;
    private List<String> acceptedParticipantIds; //invitation list
    private List<String> canceledParticipantIds;
    private List<String> signedUpParticipantIds; //enrolled
    private Random random;


    //cnstructor
    public WaitingList(String eventId,int maxParticipants, int waitingListLimit) {
        this.maxParticipants = maxParticipants;
        this.waitingListLimit = waitingListLimit;
        this.waitingparticipantIds = new ArrayList<>();
        this.acceptedParticipantIds = new ArrayList<>();
        this.canceledParticipantIds = new ArrayList<>();
        this.signedUpParticipantIds = new ArrayList<>();
        this.random = new Random();

    }
    //setter and getters


    public int getMaxParticipants() {
        return maxParticipants;
    }

    public int getWaitingListLimit() {
        return waitingListLimit;
    }

    public List<String> getWaitingparticipantIds() {
        return waitingparticipantIds;
    }

    public List<String> getAcceptedParticipantIds() {
        return acceptedParticipantIds;
    }

    public List<String> getCanceledParticipantIds() {
        return canceledParticipantIds;
    }

    public List<String> getSignedUpParticipantIds() {
        return signedUpParticipantIds;
    }

    public Random getRandom() {
        return random;
    }

    /**
     * user join the waiting list
     */
    public boolean joinWaitingList(String deviceID) {
        if (waitingListLimit > 0 && waitingparticipantIds.size() >= waitingListLimit) {
            //the waiting list is full
            return false;
        }
        //need to implement this is USer in AnyList thing
        if (!isUserInAnyList(deviceID)) {
            waitingparticipantIds.add(deviceID);
            return true;
        }
        return false; //already in these list of the event
    }

    /**
     * user leave waiting list
     */
    public boolean leaveWaitingList(String deviceID) {
        return waitingparticipantIds.remove(deviceID);
    }

    /**
     * entrant accept the invitations*/
    public boolean acceptedInvitation(String deviceID){
        if(acceptedParticipantIds.contains(deviceID)
                &&signedUpParticipantIds.size()<maxParticipants){
            //we are go through the accpted(after random) participant ids,
            acceptedParticipantIds.remove(deviceID);
            signedUpParticipantIds.add(deviceID);
            return true;
        }
        return false;
    }

    /**
     * entrant declines the invutation*/
    public boolean declineInvitation(String deviceID){
        if(acceptedParticipantIds.contains(deviceID)){
            acceptedParticipantIds.remove(deviceID);
            canceledParticipantIds.add(deviceID);
            return true;
        }
        return false;
    }
    //US 01.05.01: it this just redraw the placement?

    //organizer method
    public List<String> viewWaitingList(){
        return new ArrayList<>(waitingparticipantIds);
    }
    //sets the waiting limit
    public boolean setWaitingListLimit(int limit){
        if(limit>=0){
            this.waitingListLimit=limit;
            return true;
        }
        return false; //invalid expression
    }
    //draw the winners
    //pass args as maxParticipant for first attempt
    //custom number for the second attempt

    public List<String> sampleAttendees(int sampleSize){
        if(sampleSize<= 0 || waitingparticipantIds.isEmpty()){
            return Collections.emptyList();
        }
        List<String> sample  = new ArrayList<>(waitingparticipantIds);
        Collections.shuffle(sample, random);
        int actualSampleSize = Math.min(sampleSize,sample.size());
        List<String> selected = sample.subList(0,actualSampleSize);
        acceptedParticipantIds.addAll(selected);
        waitingparticipantIds.removeAll(selected);
        return new ArrayList<>(selected);

    }

    //oragnizer draws a replacement entrant
    public String drawReplacement(){
        if(waitingparticipantIds.isEmpty()){
            return null;
        }
        Collections.shuffle(waitingparticipantIds,random);
        String replacement=waitingparticipantIds.remove(0);
        acceptedParticipantIds.add(replacement);
        waitingparticipantIds.remove(replacement);
        return replacement;
    }

    //organizer view invited entrants

    public List<String> viewInvitedEntrants(){
        return new ArrayList<>(acceptedParticipantIds);
    }

    //organizer view cancelled entrants
    public List<String> viewCanceledEntrants(){
        return new ArrayList<>(canceledParticipantIds);

    }

    //organizer view signed up
    public List<String> viewSignedUpEntrants(){
        return new ArrayList<>(signedUpParticipantIds);
    }

    //organizer cancels entrant who didn't sign up
    public  void cancelNonSignUp(){
        //this will called last so basically ids left in the accepted list
        List<String> nonSignUp = new ArrayList<>(acceptedParticipantIds);
        acceptedParticipantIds.clear();
        canceledParticipantIds.addAll(nonSignUp);
    }

    //notification stuff can embedded here



    //checks if the user is in any list of teh event
    private boolean isUserInAnyList(String deviceID){
        return waitingparticipantIds.contains(deviceID)
                ||signedUpParticipantIds.contains(deviceID)
                ||canceledParticipantIds.contains(deviceID)
                ||acceptedParticipantIds.contains(deviceID);

    }
}

