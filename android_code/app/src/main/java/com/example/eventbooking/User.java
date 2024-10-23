package com.example.eventbooking;

import java.util.Set;
import java.util.HashSet;
public class User {

    private String username;
    private String deviceID;//changed from int to string here
    private String email;
    private String phoneNumber;
    // profile picture
    private String profileImageUrl;
    private Location location; // this is for facilities
    private boolean adminLevel;
    private boolean facilityAssociated;
    private boolean notificationAsk;
    private boolean geolocationAsk;

    private Set<String> roles;


    public User() {
        //init roles to avoid null pointer exception
        this.roles = new HashSet<>();
    }

    public User(String deviceID, String username, String email, String profileImageUrl, Set<String> roles) {
        this.deviceID = deviceID;
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.roles = new HashSet<>();
        this.roles.add(Role.ENTRANT); //set default role to be entrant
    }

    public String getDeviceID() { return deviceID; }
    public void setDeviceID(String deviceID) { this.deviceID = deviceID; }

    public String getUserame() { return username; }
    public void setUserame(String name) { this.username = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public Set<String> getRoles() {return roles;}
    public void setRoles(Set<String> roles) {this.roles = roles;}

    public boolean hasRole(String role){
        return roles != null && roles.contains(role); //check if it already has a role
    }
    //add role
    public void addRole(String role){
        if(roles==null){
            roles = new HashSet<>();
        }
        roles.add(role);
    }
}
