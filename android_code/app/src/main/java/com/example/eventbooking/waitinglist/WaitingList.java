package com.example.eventbooking.waitinglist;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaitingList{
    private String eventId;
    private List<String> entrantIds;
    /**
     * constructor
     * pass the eventId to the waiting list
     * create new arrayList as a place holder for entrant */
    public WaitingList(String eventId){
        this.eventId = eventId;
        this.entrantIds = new ArrayList<>();
    }
    //join waiting list
    //should have a click listener
    public boolean join(String entrantId){
        if(!entrantIds.contains(entrantId)){
            entrantIds.add(entrantId);
            return True;
        }
        return False; //already joined
    }

    //leave the waiting list
    public boolean leave(String entrantId){
        return entrantIds.remove(entrantId);
    }
    //Get all entrantIds
    public List<String> getEntrantIds(){
        return new ArrayList<>(entrantIds);
    }

    //get size of the waitinglist
    public int getSize(){
        return entrantIds.size();
    }

}
