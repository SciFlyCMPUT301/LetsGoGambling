package com.example.eventbooking;

import android.util.Log;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility.Facility;
import com.example.eventbooking.QRCode.QRcodeGenerator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UniversalProgramValues {
    private static UniversalProgramValues instance;
    private User single_user;
    private Event single_event;
    private Facility single_facility;
    private List<Event> eventList;
    private List<User> userList;
    private List<Facility> facilityList;
    private Boolean testingMode = false;
    private String deviceID;
    private String deletePhotoURL;
    private Boolean deletedOnlyUploaded;
    private Boolean existingLogin;
    /**
     * Singleton pattern, allows for a static instance across the whole app
     * @return the instance of UserManager
     */
    public static synchronized UniversalProgramValues getInstance(){
        if(instance == null){
            instance = new UniversalProgramValues();
        }
        return instance;
    }



    public void resetInstance(){
        single_user = null;
        single_event = null;
        single_facility = null;
        eventList = new ArrayList<>();
        userList = new ArrayList<>();
        facilityList = new ArrayList<>();
        testingMode = false;
        deviceID = null;
        deletePhotoURL = null;
        deletedOnlyUploaded = false;
    }

    public boolean getTestingMode(){
        return testingMode;
    }
    public void setTestingMode(Boolean mode){
        this.testingMode = mode;
    }
    public String getDeviceID() {
        return deviceID;
    }
    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     * Should only be called once; Initializes UserManager and fills
     * out all the user's data
     * @param user the current user
     */
    public void setCurrentUser(User user) {
        if (user == null) {
            this.single_user = null;
            return;
        }
        this.single_user = userDup(user);
    }

    public User getSingle_user(){
        return single_user;
    }

    public void setUserList(List <User> importList){
        userList = new ArrayList<>();
        for(int i = 0; i<importList.size(); i++){
            userList.add(userDup(importList.get(i)));
        }
    }

    public User userDup(User user){
        User dup_user = new User();
        dup_user.setDeviceID(user.getDeviceID());
        dup_user.setUsername(user.getUsername());
        dup_user.setEmail(user.getEmail());
        dup_user.setPhoneNumber(user.getPhoneNumber());
        dup_user.setProfilePictureUrl(user.getProfilePictureUrl());
        dup_user.setdefaultProfilePictureUrl(user.getProfilePictureUrl());
        dup_user.setLocation(user.getLocation());
        dup_user.setAddress(user.getAddress());
        dup_user.setAdminLevel(user.isAdminLevel());
        dup_user.setFacilityAssociated(user.isFacilityAssociated());
        dup_user.setNotificationAsk(user.isNotificationAsk());
        dup_user.setGeolocationAsk(user.isGeolocationAsk());
        dup_user.setRoles(new ArrayList<>(user.getRoles()));
        return dup_user;
    }

    public List<User> getUserList(){
        return userList;
    }


    public void setCurrentEvent(Event event){
        if (event == null) {
            this.single_event = null;
            return;
        }
        this.single_event = eventDup(event);
    }

    public Event getCurrentEvent(){
        return single_event;
    }

    public void setEventList(List <Event> importList){
        eventList = new ArrayList<>();
        for(int i = 0; i<importList.size(); i++){
            eventList.add(eventDup(importList.get(i)));
        }
    }

    public Event eventDup(Event event){
        Event dup_event = new Event();
        dup_event.setEventId(event.getEventId());
        dup_event.setEventTitle(event.getEventTitle());
        dup_event.setDescription(event.getDescription());
        dup_event.setEventPictureUrl(event.getEventPictureUrl());
        dup_event.setDefaultEventpictureurl(event.getDefaultEventpictureurl());
        dup_event.setTimestamp(event.getTimestamp());
        dup_event.setAddress(event.getAddress());
        dup_event.setMaxParticipants(event.getMaxParticipants());
        dup_event.setOrganizerId(event.getOrganizerId());
        dup_event.setQRcodeHash(event.getQRcodeHash());
        dup_event.setAcceptedParticipantIds(event.getAcceptedParticipantIds());
        dup_event.setWaitingParticipantIds(event.getWaitingParticipantIds());
        dup_event.setCanceledParticipantIds(event.getCanceledParticipantIds());
        dup_event.setSignedUpParticipantIds(event.getSignedUpParticipantIds());
        return dup_event;
    }

    public List<Event> getEventList(){
        return eventList;
    }

    public void setDeleteFirebaseImage(String imageUrl){
        deletePhotoURL = imageUrl;
        if(single_user.getProfilePictureUrl().equals(deletePhotoURL))
            deletedOnlyUploaded = true;
        if(single_user.getdefaultProfilePictureUrl().equals(deletePhotoURL))
            deletedOnlyUploaded = false;
        if(deletedOnlyUploaded)
            single_user.setProfilePictureUrl(single_user.getdefaultProfilePictureUrl());
    }

    public Boolean getExistingLogin() {
        return existingLogin;
    }
    public void setExistingLogin(Boolean existingLogin) {
        this.existingLogin = existingLogin;
    }
}
