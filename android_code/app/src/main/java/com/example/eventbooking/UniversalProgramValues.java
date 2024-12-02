package com.example.eventbooking;

import android.util.Log;

import com.example.eventbooking.Admin.Images.ImageClass;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility.Facility;
import com.example.eventbooking.waitinglist.WaitingList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UniversalProgramValues {
    private static UniversalProgramValues instance;
    private User single_user;
    private Event single_event;
    private Facility single_facility;
    private List<Event> eventList;
    private List<User> userList;
    private List<Facility> facilityList;
    private List<ImageClass> imageList;
    private static Boolean testingMode = false;
    private String deviceID;
    private String deletePhotoURL;
    private String uploadProfileURL;
    private Boolean deletedOnlyUploaded;
    private Boolean existingLogin;
    /**
     * Singleton pattern, allows for a static instance across the whole app
     * @return the instance of UserManager
     */
    public static synchronized UniversalProgramValues getInstance(){
        Log.d("Universal Values", "Insance called");
        Log.d("Universal Values", "Testing mode: " + testingMode);
        if(instance == null){
            Log.d("Universal Values", "Insance Created");
//            Log.d("Universal Values", "Testing mode: " + testingMode);
            instance = new UniversalProgramValues();
            testingMode = false;
        }
        return instance;
    }



    public void resetInstance(){
        instance = null;
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

    public String getUploadProfileURL() {
        return uploadProfileURL;
    }

    public void setUploadProfileURL(String uploadProfileURL) {
        this.uploadProfileURL = uploadProfileURL;
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
        User dup_user = new User(true);
        dup_user.setDeviceID(user.getDeviceID());
        dup_user.setUsername(user.getUsername());
        dup_user.setEmail(user.getEmail());
        dup_user.setPhoneNumber(user.getPhoneNumber());
        dup_user.setProfilePictureUrl(user.getProfilePictureUrl());
        dup_user.setdefaultProfilePictureUrl(user.getdefaultProfilePictureUrl());
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

    public void removeSpecificUser(String userID){
        for(int i = 0; i < userList.size(); i++){
            if(Objects.equals(userList.get(i).getDeviceID(), userID)){
                userList.remove(i);
                return;
            }
        }
    }

    public User queryUser(String deviceId){
        for(int i = 0; i < userList.size(); i++){
            if(Objects.equals(userList.get(i).getDeviceID(), deviceId)){
                return userList.get(i);
            }
        }
        return null;
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

    public String getSpecificEventHash(String eventID){
        for(int i = 0; i < eventList.size(); i++){
            if(Objects.equals(eventList.get(i).getEventId(), eventID)){
                return eventList.get(i).getQRcodeHash();
            }
        }
        return null;
    }

    public void removeSpecificEvent(String eventID){
        for(int i = 0; i < eventList.size(); i++){
            if(Objects.equals(eventList.get(i).getEventId(), eventID)){
                eventList.remove(i);
                return;
            }
        }
    }

    public void changeAllEventsOrganizer(String organizer){
        for(int i = 0; i < eventList.size(); i++){
            eventList.get(i).setOrganizerId(organizer);
        }
    }

    public Event queryEvent(String eventId){
        for(int i = 0; i < eventList.size(); i++){
            if(Objects.equals(eventList.get(i).getEventId(), eventId)){
                return eventList.get(i);
            }
        }
        return null;
    }

    public void updateEventWaitlist(String eventId, WaitingList waitlist){
        for(int i = 0; i < eventList.size(); i++){
            if(Objects.equals(eventList.get(i).getEventId(), eventId)){
                eventList.get(i).setAcceptedParticipantIds(waitlist.getAcceptedParticipantIds());
                eventList.get(i).setCanceledParticipantIds(waitlist.getCanceledParticipantIds());
                eventList.get(i).setWaitingParticipantIds(waitlist.getWaitingParticipantIds());
                eventList.get(i).setSignedUpParticipantIds(waitlist.getSignedUpParticipantIds());
                return;
            }
        }
    }


    public void setCurrentFacility(Facility facility){
        if (facility == null) {
            this.single_facility = null;
            return;
        }
        this.single_facility = facilityDup(facility);
    }

    public Facility getCurrentFacility(){
        return single_facility;
    }

    public void setFacilityList(List <Facility> importList){
        facilityList = new ArrayList<>();
        for(int i = 0; i<importList.size(); i++){
            facilityList.add(facilityDup(importList.get(i)));
        }
    }

    public Facility facilityDup(Facility facility){
        Facility dup_facility = new Facility();
        dup_facility.setFacilityID(facility.getFacilityID());
        dup_facility.setAddress(facility.getAddress());
        dup_facility.setName(facility.getName());
        dup_facility.setOrganizer(facility.getOrganizer());
        dup_facility.setAllEvents(facility.getAllEvents());
        return dup_facility;
    }

    public List<Facility> getFacilityList(){
        return facilityList;
    }

    public Boolean removeSpecificFacility(String facilityID){
        for(int i = 0; i < facilityList.size(); i++){
            if(Objects.equals(facilityList.get(i).getFacilityID(), facilityID)){
                facilityList.remove(i);
                return true;
            }
        }
        return false;
    }

    public Boolean doesFacilityExist(String facilityID){
        for(int i = 0; i < facilityList.size(); i++){
            if(Objects.equals(facilityList.get(i).getFacilityID(), facilityID)){
                return true;
            }
        }
        return false;
    }

    public Facility queryFacility(String facilityId){
        for(int i = 0; i < facilityList.size(); i++){
            if(Objects.equals(facilityList.get(i).getFacilityID(), facilityId)){
                return facilityList.get(i);
            }
        }
        return null;
    }

    public Boolean queryFacilityOrganizer(String organizerId){
        for(int i = 0; i < facilityList.size(); i++){
            if(Objects.equals(facilityList.get(i).getOrganizer(), organizerId)){
                return true;
            }
        }
        return false;
    }

    public Facility queryFacilityByOrganizer(String organizerId){
        for(int i = 0; i < facilityList.size(); i++){
            if(Objects.equals(facilityList.get(i).getOrganizer(), organizerId)){
                return facilityList.get(i);
            }
        }
        return null;
    }


    public void setImageList(List <ImageClass> importList){
        imageList = new ArrayList<>();
        for(int i = 0; i<importList.size(); i++){
            imageList.add(imageDup(importList.get(i)));
        }
    }

    public ImageClass imageDup(ImageClass image){
        ImageClass dup_image = new ImageClass(image.getImageUrl(),
                image.getSource(),
                image.getDocumentId(),
                image.getCollection());
        return dup_image;
    }

    public List<ImageClass> getImageList(){
        return imageList;
    }

    public Boolean removeSpecificImage(String imageURL){
        for(int i = 0; i < imageList.size(); i++){
            if(Objects.equals(imageList.get(i).getImageUrl(), imageURL)){
                imageList.remove(i);
                return true;
            }
        }
        return false;
    }

    public Boolean doesImageExist(String imageURL){
        for(int i = 0; i < imageList.size(); i++){
            if(Objects.equals(imageList.get(i).getImageUrl(), imageURL)){
                return true;
            }
        }
        return false;
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
