package com.example.eventbooking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FacilityTest {
    @Mock
    private FirebaseFirestore mockDb;

    @Mock
    private CollectionReference mockFacilitiesRef;

    private Facility facility;

    @Before
    public void setUp() {
        // Mock FirebaseFirestore and CollectionReference to prevent real Firebase initialization
        MockitoAnnotations.initMocks(this);
        facility = new Facility(mockDb, mockFacilitiesRef);
    }

    @Test
    public void testGetAndSetName() {
        facility.setName("Community Center");
        assertEquals("Community Center", facility.getName());
    }

    @Test
    public void testGetAndSetAddress() {
        facility.setAddress("123 Main St.");
        assertEquals("123 Main St.", facility.getAddress());
    }

    @Test
    public void testGetAndSetOrganizer() {
        facility.setOrganizer("John Doe");
        assertEquals("John Doe", facility.getOrganizer());
    }

    @Test
    public void testGetAndSetFacilityID() {
        facility.setFacilityID("facility123");
        assertEquals("facility123", facility.getFacilityID());
    }

    @Test
    public void testGetAndSetEventName() {
        facility.setEvent("Annual Meeting");
        assertEquals("Annual Meeting", facility.getEvent());
    }

    @Test
    public void testGetAndSetAllEvents() {
        List<String> events = new ArrayList<>();
        events.add("Event1");
        events.add("Event2");
        facility.setAllEvents(events);
        assertEquals(events, facility.getAllEvents());
        assertTrue(facility.getAllEvents().contains("Event1"));
        assertTrue(facility.getAllEvents().contains("Event2"));
    }

    @Test
    public void testAddAndRemoveAllEventsItem() {
        facility.addAllEventsItem("Event1");
        assertTrue(facility.getAllEvents().contains("Event1"));

        facility.removeAllEventsItem("Event1");
        assertFalse(facility.getAllEvents().contains("Event1"));
    }
}
