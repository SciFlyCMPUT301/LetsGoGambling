package com.example.eventbooking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.util.ArrayList;
import java.util.List;

public class UserTest {
    @Mock
    private StorageReference mockStorageReference;

    @Mock
    private FirebaseFirestore mockDb;

    private User user;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        user = new User(mockStorageReference, mockDb); // Inject mocks
    }

    @Test
    public void testGetAndSetUsername() {
        user.setUsername("testUser");
        assertEquals("testUser", user.getUsername());
    }

    @Test
    public void testGetAndSetDeviceID() {
        user.setDeviceID("device123");
        assertEquals("device123", user.getDeviceID());
    }

    @Test
    public void testGetAndSetEmail() {
        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    public void testGetAndSetPhoneNumber() {
        user.setPhoneNumber("123-456-7890");
        assertEquals("123-456-7890", user.getPhoneNumber());
    }

    @Test
    public void testGetAndSetProfilePictureUrl() {
        user.setProfilePictureUrl("http://example.com/pic.png");
        assertEquals("http://example.com/pic.png", user.getProfilePictureUrl());
    }

    @Test
    public void testGetAndSetAddress() {
        user.setAddress("456 Banana Blvd.");
        assertEquals("456 Banana Blvd.", user.getAddress());
    }

    @Test
    public void testGetAndSetLocation() {
        user.setLocation("New Facility");
        assertEquals("New Facility", user.getLocation());
    }

    @Test
    public void testGetAndSetAdminLevel() {
        user.setAdminLevel(true);
        assertTrue(user.isAdminLevel());
        user.setAdminLevel(false);
        assertFalse(user.isAdminLevel());
    }

    @Test
    public void testGetAndSetFacilityAssociated() {
        user.setFacilityAssociated(true);
        assertTrue(user.isFacilityAssociated());
        user.setFacilityAssociated(false);
        assertFalse(user.isFacilityAssociated());
    }

    @Test
    public void testGetAndSetNotificationAsk() {
        user.setNotificationAsk(true);
        assertTrue(user.isNotificationAsk());
        user.setNotificationAsk(false);
        assertFalse(user.isNotificationAsk());
    }

    @Test
    public void testGetAndSetGeolocationAsk() {
        user.setGeolocationAsk(true);
        assertTrue(user.isGeolocationAsk());
        user.setGeolocationAsk(false);
        assertFalse(user.isGeolocationAsk());
    }

    @Test
    public void testGetAndSetRoles() {
        List<String> roles = new ArrayList<>();
        roles.add("ADMIN");
        roles.add("USER");

        user.setRoles(roles);
        assertEquals(roles, user.getRoles());
        assertTrue(user.getRoles().contains("ADMIN"));
        assertTrue(user.getRoles().contains("USER"));
    }

    @Test
    public void testAddAndRemoveRole() {
        user.addRole("USER");
        assertTrue(user.getRoles().contains("USER"));

        user.removeRole("USER");
        assertFalse(user.getRoles().contains("USER"));
    }

    @Test
    public void testHasRole() {
        user.addRole("USER");
        assertTrue(user.hasRole("USER"));
        assertFalse(user.hasRole("ADMIN"));
    }
}
