package com.example.eventbooking;

public class User {

    private String username;
    private int deviceID;
    private String email;
    private String phoneNumber;
    // profile picture
    private String profileImageUrl;
    private Location location; // this is for facilities
    private boolean adminLevel;
    private boolean facilityAssociated;
    private boolean notificationAsk;
    private boolean geolocationAsk;


    public User() {}

    public User(int deviceID, String username, String email, String profileImageUrl) {
        this.deviceID = deviceID;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    public int getDeviceID() { return deviceID; }
    public void setDeviceID(int deviceID) { this.deviceID = deviceID; }

    public String getUserame() { return username; }
    public void setUserame(String name) { this.username = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }



}
