package com.example.eventbooking;

import static org.junit.Assert.*;

import com.example.eventbooking.profile.EntrantProfile;

import org.junit.Before;
import org.junit.Test;

public class EntrantProfileTest {

    private EntrantProfile entrantProfile;

    @Before
    public void setUp() {
        entrantProfile = new EntrantProfile("John Doe", "john.doe@example.com", "1234567890");
    }

    @Test
    public void testConstructor() {
        assertNotNull("EntrantProfile object should not be null", entrantProfile);
        assertEquals("John Doe", entrantProfile.getName());
        assertEquals("john.doe@example.com", entrantProfile.getEmail());
        assertEquals("1234567890", entrantProfile.getPhoneNumber());
        assertFalse("Notifications should be disabled by default", entrantProfile.isNotificationsEnabled());
    }

    @Test
    public void testSetAndGetName() {
        entrantProfile.setName("Jane Doe");
        assertEquals("Jane Doe", entrantProfile.getName());
    }

    @Test
    public void testSetAndGetEmail() {
        entrantProfile.setEmail("jane.doe@example.com");
        assertEquals("jane.doe@example.com", entrantProfile.getEmail());
    }

    @Test
    public void testSetAndGetPhoneNumber() {
        entrantProfile.setPhoneNumber("0987654321");
        assertEquals("0987654321", entrantProfile.getPhoneNumber());
    }

    @Test
    public void testNotificationsEnabled() {
        entrantProfile.setNotificationsEnabled(true);
        assertTrue("Notifications should be enabled", entrantProfile.isNotificationsEnabled());

        entrantProfile.setNotificationsEnabled(false);
        assertFalse("Notifications should be disabled", entrantProfile.isNotificationsEnabled());
    }

    @Test
    public void testToString() {
        String expected = "EntrantProfile{name='John Doe', email='john.doe@example.com', phoneNumber='1234567890', notificationsEnabled=false}";
        assertEquals(expected, entrantProfile.toString());
    }
}
