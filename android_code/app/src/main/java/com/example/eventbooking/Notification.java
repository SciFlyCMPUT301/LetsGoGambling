package com.example.eventbooking;
/**
 * Model class representing a notification intended for a user.
 * Notifications provide information related to a specific event 
 * and are associated with a user account.
 */
public class Notification {

    // Unique identifier for the notification
    private String notificationId;

    // ID of the user the notification is intended for
    private String userId;

    // ID of the event the notification is related to
    private String eventId;

    // Notification content
    private String text;

    // Notification title
    private String title;

    // Flag indicating whether the notification has been read
    private boolean read;

    /**
     * Default constructor for creating an empty Notification instance.
     */
    public Notification() {}

    /**
     * Parameterized constructor for creating a new Notification instance
     * with the specified properties.
     *
     * @param eventId ID of the associated event.
     * @param text    Content of the notification.
     * @param title   Title of the notification.
     * @param userId  ID of the user the notification is for.
     */
    public Notification(String eventId, String text, String title, String userId) {
        this.eventId = eventId;
        this.text = text;
        this.title = title;
        this.userId = userId;
        this.read = false; // Notifications are unread by default
    }

    /**
     * Gets the ID of the event associated with this notification.
     *
     * @return The event ID.
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the ID of the event associated with this notification.
     *
     * @param eventId The event ID.
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Checks if the notification has been read.
     *
     * @return True if the notification has been read; otherwise, false.
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Marks the notification as read or unread.
     *
     * @param read True to mark as read; false to mark as unread.
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Gets the content of the notification.
     *
     * @return The notification content.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the content of the notification.
     *
     * @param text The notification content.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the title of the notification.
     *
     * @return The notification title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the notification.
     *
     * @param title The notification title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the ID of the user the notification is intended for.
     *
     * @return The user ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user the notification is intended for.
     *
     * @param userId The user ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the unique ID of the notification.
     *
     * @return The notification ID.
     */
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * Sets the unique ID of the notification.
     *
     * @param notificationId The notification ID.
     */
    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}








