package com.example.eventbooking.profile;

/**
 * The EntrantProfile class represents the profile information of an event entrant,
 * including personal details such as name, email, phone number, and notification preferences.
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
     * Notifications are disabled by default.
     * @param name of the entrant
     * @param email of the entrant
     * @param phoneNumber of the entrant
     */

    public EntrantProfile(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.notificationsEnabled = false;
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
     * sets notification preferences for the entrant
     * @param enabled true to enable notifications and false for disabling them
     */

    public void setNotificationsEnabled(boolean enabled) {
        this.notificationsEnabled = enabled;
    }
    /**
     * Returns a string representation of the EntrantProfile object, including all profile details.
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
