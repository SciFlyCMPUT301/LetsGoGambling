package com.example.eventbooking;

//package com.example.eventbooking.Events.EventData;

//package com.example.eventbooking.Events.EventData;

//import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.provider.Settings;

//import androidx.test.ext.junit.runners.AndroidJUnit4;

import androidx.annotation.NonNull;

import com.example.eventbooking.Events.EventData.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



import org.junit.runner.RunWith;

/**
 * Instrumented tests for the Event class.
 */
//@RunWith(AndroidJUnit4.class)
//@RunWith(RobolectricTestRunner.class)
//@Config(manifest=Config.NONE)
public class EventTest {

    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private CollectionReference mockCollection;
    @Mock
    private DocumentReference mockDocument;
    @Mock
    private FirebaseStorage mockStorage;
    @Mock
    private StorageReference mockStorageReference;

    private Event event;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockFirestore = mock(FirebaseFirestore.class);
        mockCollection = mock(CollectionReference.class);
        mockDocument = mock(DocumentReference.class);


        // Mock Firestore and CollectionReference
        when(mockFirestore.collection("Events")).thenReturn(mockCollection);
        when(mockCollection.document(anyString())).thenReturn(mockDocument);

        Task<Void> mockSetTask = mock(Task.class);

        when(mockSetTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> onSuccessListener = invocation.getArgument(0);
            onSuccessListener.onSuccess(null); // Simulate successful Firestore write
            return mockSetTask;
        });

        // Simulate addOnFailureListener behavior
        when(mockSetTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgument(0);
            onFailureListener.onFailure(new Exception("Simulated Firestore failure")); // Simulate failure
            return mockSetTask;
        });

        // Ensure mockDocument.set() returns the mocked Task
        when(mockDocument.set(anyMap())).thenReturn(mockSetTask);

        when(mockCollection.get()).thenAnswer(invocation -> {
            Task<QuerySnapshot> mockQueryTask = mock(Task.class);
            when(mockQueryTask.isSuccessful()).thenReturn(true);
            QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
            when(mockQueryTask.getResult()).thenReturn(mockSnapshot);
            when(mockSnapshot.size()).thenReturn(100); // Simulate 100 existing documents
            return mockQueryTask;
        });

        // Mock DocumentReference.set() behavior
//        when(mockDocument.set(anyMap())).thenAnswer(invocation -> {
//            Map<String, Object> data = invocation.getArgument(0);
//            Task<Void> mockSetTask = mock(Task.class);
//            when(mockSetTask.isSuccessful()).thenReturn(true);
//            return mockSetTask;
//        });

        // Mock QuerySnapshot Task
//        Task<QuerySnapshot> mockQueryTask = mock(Task.class);
//        QuerySnapshot mockSnapshot = mock(QuerySnapshot.class);
//        when(mockQueryTask.isSuccessful()).thenReturn(true);
//        when(mockQueryTask.getResult()).thenReturn(mockSnapshot);
//        when(mockSnapshot.size()).thenReturn(100); // Mock 100 existing documents
//        when(mockCollection.get()).thenReturn(mockQueryTask);
//
//        // Mock CollectionReference.document behavior
//        when(mockCollection.document(anyString())).thenReturn(mockDocument);
//
//        // Mock DocumentReference.set behavior
//        Task<Void> mockSetTask = mock(Task.class);
//        when(mockSetTask.isSuccessful()).thenReturn(true);
//        when(mockDocument.set(anyMap())).thenReturn(mockSetTask);



        // Create Event instance with mocked Firestore and Storage
        event = new Event(mockFirestore, mockStorage);
        event.setEventId("eventID100");
    }

    @After
    public void tearDown() {

        // Example: Reset Mockito mocks
        Mockito.reset(mockFirestore, mockDocument, mockCollection);
        event = null;
        mockFirestore = null;
        mockCollection = null;
        mockDocument = null;
    }


@Test
public void testSaveEventDataToFirestore() {
    // Ensure eventId is null to trigger getNewEventID
    event.setEventId("MockEventID123");

    // Spy on the Event object to mock getNewEventID
    Event spyEvent = spy(event);
    doAnswer(invocation -> {
        spyEvent.setEventId("MockEventID123"); // Simulate generating a new event ID
        return null; // Since we're bypassing actual Task execution
    }).when(spyEvent).getNewEventID();

    // Call the method under test
    spyEvent.saveEventDataToFirestore();

    // Verify that the eventId was set
    assertEquals("MockEventID123", spyEvent.getEventId());

    // Ensure Firestore interactions were triggered
    verify(mockCollection, times(1)).document("MockEventID123");
    verify(mockDocument, times(1)).set(anyMap());

    // Print success for clarity
    System.out.println("Test passed: Event ID was set and Firestore interaction verified.");
}


    @Test
    public void testParticipantManagement() {
        // Initialize event fields
        event.setEventId("eventID100"); // Simulate a new event
        event.setEventTitle("Test Event");
        event.setMaxParticipants(10);

        // Simulate participant management
        event.addWaitingParticipantIds("User1");
        event.addWaitingParticipantIds("User2");
        event.acceptParticipant("User1");
        event.cancelParticipant("User1");
        event.signUpParticipant("User2");

        // Save event data
        event.saveEventDataToFirestore();

        // Verify Firestore interactions
        verify(mockFirestore, times(1)).collection("Events");
        verify(mockDocument).set(anyMap());
    }

    @Test
    public void testCreateEventPosterUrl() {
        event.setEventId("TestEvent123");
        String posterUrl = event.createEventPosterUrl();
        assertNotNull("Poster URL should not be null", posterUrl);
        assertTrue("Poster URL should contain the event ID", posterUrl.contains("TestEvent123"));
    }

    @Test
    public void testAddParticipant() {
        event.setMaxParticipants(2);
        event.addParticipant("User1");
        event.addParticipant("User2");
        event.addParticipant("User3");  // Should not be added as max participants is reached

        List<String> waitingList = event.getWaitingParticipantIds();
        assertEquals(2, waitingList.size());
        assertTrue(waitingList.contains("User1"));
        assertTrue(waitingList.contains("User2"));
        assertFalse(waitingList.contains("User3"));
    }

    @Test
    public void testRemoveParticipant() {
        event.setMaxParticipants(5);
        event.addParticipant("User1");
        event.addParticipant("User2");
        event.removeParticipant("User1");

        List<String> waitingList = event.getWaitingParticipantIds();
        assertFalse(waitingList.contains("User1"));
        assertTrue(waitingList.contains("User2"));
    }

//    @Test
//    public void testUploadEventPosterToFirebase_Valid() {
//        event.setEventId("Event1");
//        // Mock the Uri.fromFile method to avoid the Stub! error
//        Uri mockUri = mock(Uri.class);
////        when(Uri.fromFile(any(File.class))).thenReturn(mockUri);
//
//        // Set the event ID and provide a valid file path
//        event.setEventId("Event1");
//        String validPath = "/path/to/valid/picture.jpg";
//
//        // Mock the storage reference and behavior
//        when(mockStorage.getReference()).thenReturn(mockStorageReference);
//        when(mockStorageReference.child(anyString())).thenReturn(mockStorageReference);
//
//        // Execute the method
//        event.uploadEventPosterToFirebase(validPath);
//
//        // Verify that putFile was called with the mocked Uri
//        verify(mockStorageReference).putFile(mockUri);
//    }

//    @Test(expected = IllegalArgumentException.class)
//    public void testUploadEventPosterToFirebase_InvalidPath() {
//        event.uploadEventPosterToFirebase("");
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testUploadEventPosterToFirebase_MissingEventId() {
//        event.setEventId(null);
//        event.uploadEventPosterToFirebase("/path/to/valid/picture.jpg");
//    }



    @Test
    public void testUpdateEventData() {
        // Call updateEventData with test data
        event.updateEventData(
                "Updated Event Title",
                "Updated Description",
                "Updated Location",
                100,
                "UpdatedOrganizer",
                List.of("User1", "User2"),
                List.of("User3"),
                List.of("User4"),
                List.of("User5")
        );

        // Capture the data passed to Firestore
        ArgumentCaptor<Map<String, Object>> argumentCaptor = ArgumentCaptor.forClass(Map.class);
        verify(mockDocument).set(argumentCaptor.capture());

        // Retrieve the captured data
        Map<String, Object> eventData = argumentCaptor.getValue();
        // Verify the captured data matches the expected values
        assertNotNull(eventData);
        assertEquals("Updated Event Title", eventData.get("eventTitle"));
        assertEquals("Updated Description", eventData.get("description"));
        assertEquals("Updated Location", eventData.get("location"));
        assertEquals(100, eventData.get("maxParticipants"));
        assertEquals("UpdatedOrganizer", eventData.get("organizerId"));
        assertEquals(List.of("User1", "User2"), eventData.get("waitingparticipantIds"));
        assertEquals(List.of("User3"), eventData.get("acceptedParticipantIds"));
        assertEquals(List.of("User4"), eventData.get("canceledParticipantIds"));
        assertEquals(List.of("User5"), eventData.get("signedUpParticipantIds"));
    }









    @Test
    public void testAddDeclinedParticipant() {
        // Add a declined participant
        event.addDeclinedParticipantId("User1");

        // Save event data to Firestore
        event.saveEventDataToFirestore();

        // Verify that `declinedParticipantIds` contains "User1" in the saved data
        verify(mockDocument).set(argThat(data -> {
            if (!(data instanceof Map)) {
                return false;
            }
            Map<String, Object> mapData = (Map<String, Object>) data;

            // Debugging: Log the map to see the actual data
            System.out.println("Saved event data: " + mapData);

            // Check if the declinedParticipantIds list is present and contains "User1"
            List<?> declinedParticipants = (List<?>) mapData.getOrDefault("declinedParticipantIds", List.of());
            return declinedParticipants.contains("User1");
        }));
    }





}
