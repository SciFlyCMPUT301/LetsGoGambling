package com.example.eventbooking;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class TrialTestingClass {

    private FirebaseFirestore mockFirestore;
    private CollectionReference mockCollection;
    private DocumentReference mockDocument;
    private DocumentSnapshot mockDocumentSnapshot;

    @Before
    public void setUp() {
        mockFirestore = mock(FirebaseFirestore.class);
        mockCollection = mock(CollectionReference.class);
        mockDocument = mock(DocumentReference.class);
        mockDocumentSnapshot = mock(DocumentSnapshot.class);
        // Mock Firestore's collection method
        when(mockFirestore.collection("users")).thenReturn(mockCollection);

        // Mock collection's document method
        when(mockCollection.document("user123")).thenReturn(mockDocument);

        // Mock document's get() method
        when(mockDocument.get()).thenReturn(Tasks.forResult(mockDocumentSnapshot));

        // Mock DocumentSnapshot's behavior
        when(mockDocumentSnapshot.exists()).thenReturn(true);
        when(mockDocumentSnapshot.get("name")).thenReturn("John Doe");
    }

    @Test
    public void testFetchUserData() {
        // Arrange mock data
        Task<DocumentSnapshot> task = mockFirestore.collection("users")
                .document("user123")
                .get();

        try {
            DocumentSnapshot documentSnapshot = task.getResult();

            // Assert that the name is returned correctly
            assertTrue(documentSnapshot.exists());
            assertEquals("John Doe", documentSnapshot.get("name"));
        } catch (Exception e) {
            fail("Test failed due to exception: " + e.getMessage());
        }
    }

}
