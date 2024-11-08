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
import org.mockito.Mock;
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

/**
 * Instrumented tests for the Event class.
 */
//@RunWith(AndroidJUnit4.class)
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

        // Mock the Firebase components
        when(mockFirestore.collection("Events")).thenReturn(mockCollection);
        when(mockCollection.document(anyString())).thenReturn(mockDocument);

        when(mockStorage.getReference()).thenReturn(mockStorageReference);
        when(mockStorageReference.child(anyString())).thenReturn(mockStorageReference);


        // Mock the Task returned by get() for generating event ID
        Task<QuerySnapshot> mockQueryTask = mock(Task.class);
        when(mockQueryTask.isSuccessful()).thenReturn(true);
        when(mockCollection.get()).thenReturn(mockQueryTask);

        // Mock the Task returned by set() for saving event data
        Task<Void> mockSetTask = mock(Task.class);

        // Configure the mock task to simulate success
        when(mockSetTask.addOnSuccessListener(any())).thenAnswer(invocation -> {
            OnSuccessListener<Void> onSuccessListener = invocation.getArgument(0);
            onSuccessListener.onSuccess(null); // Simulate success callback
            return mockSetTask;
        });

        // Configure the mock task to simulate failure
        when(mockSetTask.addOnFailureListener(any())).thenAnswer(invocation -> {
            OnFailureListener onFailureListener = invocation.getArgument(0);
            onFailureListener.onFailure(new Exception("Simulated Firestore save error"));
            return mockSetTask;
        });

        // Set the mock task to be returned by document.set()
        when(mockDocument.set(anyMap())).thenReturn(mockSetTask);

        // Create a new Event instance with the mocked Firestore
        event = new Event(mockFirestore, mockStorage);
    }



    @Test
    public void testSaveEventDataToFirestore() {
        // Call the method you want to test
        event.saveEventDataToFirestore();

        // Verify that Firestore interactions were called as expected
        verify(mockFirestore, times(2)).collection("Events");  // Expecting two calls due to `getNewEventID` and `saveEventDataToFirestore`
        verify(mockCollection).document(event.getEventId());
        verify(mockDocument).set(anyMap());
    }

    @Test
    public void testParticipantManagement() {
        // Setup and add participants
        event.addWaitingParticipantIds("User1");
        event.addWaitingParticipantIds("User2");
        event.acceptParticipant("User1");
        event.cancelParticipant("User1");
        event.signUpParticipant("User2");

        // Call the method you want to test
        event.saveEventDataToFirestore();

        // Verify the Firestore interactions
        verify(mockFirestore, times(2)).collection("Events");  // Expect two calls due to method structure
        verify(mockDocument).set(anyMap()); // Relax argument match to any Map structure
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

    @Test(expected = IllegalArgumentException.class)
    public void testUploadEventPosterToFirebase_InvalidPath() {
        event.uploadEventPosterToFirebase("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUploadEventPosterToFirebase_MissingEventId() {
        event.setEventId(null);
        event.uploadEventPosterToFirebase("/path/to/valid/picture.jpg");
    }



    @Test
    public void testUpdateEventData() {
        event.setEventId("Event1");
//        event.saveEventDataToFirestore();

        // Prepare updated data
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

        // Call save to simulate the update
        event.saveEventDataToFirestore();

        // Verify that the updated fields were set correctly
        verify(mockDocument).set(argThat(data -> {
            Map<String, Object> mapData = (Map<String, Object>) data;
            return "Updated Event Title".equals(mapData.get("eventTitle")) &&
                    "Updated Description".equals(mapData.get("description")) &&
                    "Updated Location".equals(mapData.get("location")) &&
                    mapData.get("maxParticipants").equals(100) &&
                    mapData.get("organizerId").equals("UpdatedOrganizer");
        }));
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
