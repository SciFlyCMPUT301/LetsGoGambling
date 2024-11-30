package com.example.eventbooking;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;



import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

import com.example.eventbooking.waitinglist.WaitingList;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WaitingListTest {
    private WaitingList waitingList;
    private final List<String> initialUserIds = Arrays.asList("user1", "user2", "user3");

    @Before
    public void setUp() {
        // Manually create a new instance of WaitingList for each test
        waitingList = new WaitingList("event123");
        waitingList.setMaxParticipants(3);
        waitingList.setWaitingParticipantIds(new ArrayList<>(initialUserIds));
    }


    @Test
    public void testGetAndSetEventId() {
        waitingList.setEventId("newEvent456");
        assertEquals("newEvent456", waitingList.getEventId());
    }

    @Test
    public void testGetAndSetMaxParticipants() {
        waitingList.setMaxParticipants(5);
        assertEquals(5, waitingList.getMaxParticipants());
    }

    @Test
    public void testAddParticipantToWaitingList() {
        assertTrue(waitingList.addParticipantToWaitingList("user4"));
        assertTrue(waitingList.getWaitingParticipantIds().contains("user4"));

        // Trying to add the same user should return false
        assertFalse(waitingList.addParticipantToWaitingList("user1"));
    }

    @Test
    public void testSampleParticipants() {
        waitingList.addParticipantToWaitingList("user4");
        waitingList.addParticipantToWaitingList("user5");

        List<String> sampled = waitingList.sampleParticipants(2);
        assertEquals(2, sampled.size());
        assertTrue(waitingList.getAcceptedParticipantIds().containsAll(sampled));
        assertEquals(3, waitingList.getWaitingParticipantIds().size());
    }

    @Test
    public void testParticipantSignsUp() {
        waitingList.addParticipantToWaitingList("user4");
        waitingList.sampleParticipants(1);
        String sampledUser = waitingList.getAcceptedParticipantIds().get(0);

        String result = waitingList.participantSignsUp(sampledUser);
        assertEquals("Participant has confirmed attendance.Event hasn't full", result);
        assertTrue(waitingList.getSignedUpParticipantIds().contains(sampledUser));
        assertFalse(waitingList.getAcceptedParticipantIds().contains(sampledUser));
    }





    @Test
    public void testDrawReplacement() {
        waitingList.sampleParticipants(2);
        waitingList.participantSignsUp("user1");
        waitingList.participantSignsUp("user2");

//        waitingList.cancelParticipation("user1");
        List<String> replacements = waitingList.drawReplacement(1);

        assertEquals(1, replacements.size());
        assertTrue(waitingList.getAcceptedParticipantIds().contains(replacements.get(0)));
    }

    @Test
    public void testGetAndSetWaitingParticipantIds() {
        List<String> newWaitingList = Arrays.asList("newUser1", "newUser2");
        waitingList.setWaitingParticipantIds(newWaitingList);
        assertEquals(newWaitingList, waitingList.getWaitingParticipantIds());
    }

    @Test
    public void testGetAndSetAcceptedParticipantIds() {
        List<String> newAcceptedList = Arrays.asList("acceptedUser1", "acceptedUser2");
        waitingList.setAcceptedParticipantIds(newAcceptedList);
        assertEquals(newAcceptedList, waitingList.getAcceptedParticipantIds());
    }

    @Test
    public void testGetAndSetSignedUpParticipantIds() {
        List<String> newSignedUpList = Arrays.asList("signedUpUser1", "signedUpUser2");
        waitingList.setSignedUpParticipantIds(newSignedUpList);
        assertEquals(newSignedUpList, waitingList.getSignedUpParticipantIds());
    }

    @Test
    public void testGetAndSetCanceledParticipantIds() {
        List<String> newCanceledList = Arrays.asList("canceledUser1", "canceledUser2");
        waitingList.setCanceledParticipantIds(newCanceledList);
        assertEquals(newCanceledList, waitingList.getCanceledParticipantIds());
    }
}