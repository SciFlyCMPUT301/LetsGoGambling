package com.example.eventbooking.profile;

/**
 * The EntrantProfile class represents the profile information of an event entrant,
 * including personal details such as name, email, phone number, and notification preferences.
 * This class is used to manage and display an entrant's profile within the system.
 */
public class EntrantProfile {
    private String name;
    private String email;
    private String phoneNumber;
    private boolean notificationsEnabled;
    /**
     * Default constructor for EntrantProfile.
     * Initializes an empty entrant profile with notifications disabled by default.
     */

    public EntrantProfile() {}

    /**
     * Constructs an EntrantProfile with specified name, email, and phone number.
     * Notifications are disabled by default upon profile creation.
     *
     * @param name The name of the entrant.
     * @param email The email of the entrant.
     * @param phoneNumber The phone number of the entrant.
     */

    public EntrantProfile(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.notificationsEnabled = false;// notifications are disabled by default
    }

    // Getters and setters
    // getting and setting the name of the entrant
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    // getting and setting the email of the entrant
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    // getting and setting the phone number of the entrant

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    /**
     * Checks if notifications are enabled for the entrant.
     *
     * @return True if notifications are enabled, false otherwise.
     */

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    /**
     * Sets the notification preferences for the entrant.
     *
     * @param enabled True to enable notifications, false to disable notifications.
     */

    public void setNotificationsEnabled(boolean enabled) {
        this.notificationsEnabled = enabled;
    }
    /**
     * Returns a string representation of the EntrantProfile object, including all profile details.
     * This can be useful for debugging or displaying the profile in logs.
     *
     * @return A string representation of the entrant profile.
     */
    @Override
    public String toString() {
        return "EntrantProfile{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", notificationsEnabled=" + notificationsEnabled +
                '}';
    }
}
