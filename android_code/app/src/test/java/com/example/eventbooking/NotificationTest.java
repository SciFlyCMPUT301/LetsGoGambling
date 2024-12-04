package com.example.eventbooking;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NotificationTest {

    private Notification notification;

    @Before
    public void setUp() {
        // This method is called before each test
        notification = new Notification("event123", "Event text", "Event title", "user123", "eventHash");
    }

    @Test
    public void testConstructor() {
        // Test constructor initialization
        assertNotNull(notification);
        assertEquals("event123", notification.getEventId());
        assertEquals("Event text", notification.getText());
        assertEquals("Event title", notification.getTitle());
        assertEquals("user123", notification.getUserId());
        assertFalse(notification.isRead()); // default value is false
    }

    @Test
    public void testGettersAndSetters() {
        // Test setters and getters
        notification.setNotificationId("notif123");
        notification.setEventId("event456");
        notification.setText("Updated text");
        notification.setTitle("Updated title");
        notification.setUserId("user456");
        notification.setRead(true);

        assertEquals("notif123", notification.getNotificationId());
        assertEquals("event456", notification.getEventId());
        assertEquals("Updated text", notification.getText());
        assertEquals("Updated title", notification.getTitle());
        assertEquals("user456", notification.getUserId());
        assertTrue(notification.isRead());
    }

    @Test
    public void testDefaultConstructor() {
        // Test default constructor
        Notification defaultNotification = new Notification();
        assertNull(defaultNotification.getEventId());
        assertNull(defaultNotification.getText());
        assertNull(defaultNotification.getTitle());
        assertNull(defaultNotification.getUserId());
        assertFalse(defaultNotification.isRead()); // default value should be false
    }

    @Test
    public void testSetRead() {
        // Test changing read status
        notification.setRead(true);
        assertTrue(notification.isRead());

        notification.setRead(false);
        assertFalse(notification.isRead());
    }

    @Test
    public void testSetText() {
        // Test changing text
        String newText = "New notification text";
        notification.setText(newText);
        assertEquals(newText, notification.getText());
    }

    @Test
    public void testSetTitle() {
        // Test changing title
        String newTitle = "New notification title";
        notification.setTitle(newTitle);
        assertEquals(newTitle, notification.getTitle());
    }

    @Test
    public void testSetUserId() {
        // Test changing userId
        String newUserId = "newUserId";
        notification.setUserId(newUserId);
        assertEquals(newUserId, notification.getUserId());
    }

    @Test
    public void testSetNotificationId() {
        // Test setting notificationId
        String newNotificationId = "notif123";
        notification.setNotificationId(newNotificationId);
        assertEquals(newNotificationId, notification.getNotificationId());
    }
}

