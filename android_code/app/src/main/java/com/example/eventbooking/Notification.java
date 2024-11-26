package com.example.eventbooking;

public class Notification {
    private String userId;
    private String eventId;

    private String text;
    private String title;
    private boolean read;

    public Notification(String eventId, String text, String title, String userId) {
        this.eventId = eventId;
        this.text = text;
        this.title = title;
        this.userId = userId;
        this.read = false;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
