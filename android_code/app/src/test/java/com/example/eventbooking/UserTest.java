package com.example.eventbooking;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import androidx.test.core.app.ApplicationProvider;

import org.mockito.Mockito;

import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class UserTest {
    private FirebaseStorage firebaseStorage;

    @Before
    public void setUp() {
        firebaseStorage = Mockito.mock(FirebaseStorage.class);
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testSetAndGetUsername() {
        User user = new User();
        user.setUsername("TestUser");
        assertEquals("TestUser", user.getUsername());
    }

    @Test
    public void testSetAndGetEmail() {
        User user = new User();
        user.setEmail("test@example.com");
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    public void testSetAndGetPhoneNumber() {
        User user = new User();
        user.setPhoneNumber("1234567890");
        assertEquals("1234567890", user.getPhoneNumber());
    }

    @Test
    public void testSetAndGetProfilePictureUrl() {
        User user = new User();
        user.setProfilePictureUrl("https://example.com/pic.jpg");
        assertEquals("https://example.com/pic.jpg", user.getProfilePictureUrl());
    }

    @Test
    public void testAddRoleAndHasRole() {
        User user = new User();
        user.addRole("ADMIN");
        assertTrue(user.hasRole("ADMIN"));
    }

    @Test
    public void testDefaultProfilePictureUrl() {
        User user = new User();
        user.setUsername("DefaultUser");
        String expectedUrl = "https://firebasestorage.googleapis.com/v0/b/YOUR_FIREBASE_PROJECT_ID/o/default%2FDefaultUser.png?alt=media";
        assertEquals(expectedUrl, user.defaultProfilePictureUrl(user.getUsername()));
    }

    @Test
    public void testUploadProfilePictureToFirebase() {
        User user = new User();
        user.setUsername("TestUser");

        // Mock Firebase Storage
        FirebaseStorage mockStorage = Mockito.mock(FirebaseStorage.class);
        StorageReference mockStorageRef = Mockito.mock(StorageReference.class);
        StorageReference mockProfilePicRef = Mockito.mock(StorageReference.class);
        UploadTask mockUploadTask = Mockito.mock(UploadTask.class);

        Mockito.when(mockStorage.getReference()).thenReturn(mockStorageRef);
        Mockito.when(mockStorageRef.child(Mockito.anyString())).thenReturn(mockProfilePicRef);
        Mockito.when(mockProfilePicRef.putFile(Mockito.any(Uri.class))).thenReturn(mockUploadTask);

//        user.uploadProfilePictureToFirebase(Uri.parse("file://fakepath"));

        // Verify that putFile was called on the correct reference
        Mockito.verify(mockProfilePicRef).putFile(Mockito.any(Uri.class));
    }

    @Test
    public void testDeleteSelectedImageFromFirebase() {
        User user = new User();
        user.setUsername("TestUser");

        // Mock Firebase Storage
        FirebaseStorage mockStorage = Mockito.mock(FirebaseStorage.class);
        StorageReference mockStorageRef = Mockito.mock(StorageReference.class);

        // Mock the URL and behavior
        String selectedImageUrl = "https://example.com/selectedImage.jpg";
        Mockito.when(mockStorage.getReferenceFromUrl(selectedImageUrl)).thenReturn(mockStorageRef);

//        user.deleteSelectedImageFromFirebase(selectedImageUrl);

        // Verify that the delete method was called
        Mockito.verify(mockStorageRef).delete();
    }
}
