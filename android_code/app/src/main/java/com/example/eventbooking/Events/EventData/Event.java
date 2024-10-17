package com.example.eventbooking.Events.EventData;

import com.example.eventbooking.Location;

public class Event {

    private String eventId;
    private String eventTitle;
    private String description;
    private String imageUrl; // URL of the event image in Firebase Storage
    private long timestamp; // Event time in milliseconds
    private Location location;

    public Event() {}

    public Event(String eventId, String eventTitle, String description, String imageUrl, long timestamp, Location location) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.location = location;
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

}
