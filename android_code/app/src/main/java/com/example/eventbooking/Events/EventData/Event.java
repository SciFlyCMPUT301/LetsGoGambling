package com.example.eventbooking.Events.EventData;

import android.os.UserManager;
import android.provider.ContactsContract;

import com.example.eventbooking.Location;
import java.util.List;

import com.example.eventbooking.Role;
import com.example.eventbooking.User;
//import waitinglist
import com.example.eventbooking.waitinglist.WaitingList;

import java.util.ArrayList;
import java.util.List;

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


    public Event() {}

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
}
