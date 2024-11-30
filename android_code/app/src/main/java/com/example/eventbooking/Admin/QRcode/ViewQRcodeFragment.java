package com.example.eventbooking.Admin.QRcode;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
/**
 * Fragment for viewing and managing QR codes associated with events.
 * This fragment allows admins to view QR codes, select an event, and remove its associated QR code.
 */

public class ViewQRcodeFragment extends Fragment {
    private FirebaseFirestore db;
    private Button adminGoBack, removeButton;
    private QRcodeViewAdapter qrcodeAdapter;
    private ArrayList<Event> qrcodeList;
    private ListView qrcodeListView;
    private Event selectedQRcode = null;
    /**
     * Called to create the fragment's view and initialize UI components.
     * Also loads QR codes from Firestore and sets up listeners for button actions.
     *
     * @param inflater           The LayoutInflater object to inflate views.
     * @param container          The parent view group.
     * @param savedInstanceState The previous saved instance state.
     * @return The view for the fragment.
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_qrcode, container, false);

        db = FirebaseFirestore.getInstance();
        qrcodeList = new ArrayList<>();

        // Initialize ListView and adapter
        qrcodeListView = view.findViewById(R.id.qrcode_list);
        qrcodeAdapter = new QRcodeViewAdapter(getContext(), qrcodeList);
        qrcodeListView.setAdapter(qrcodeAdapter);

        adminGoBack = view.findViewById(R.id.admin_go_back);
        removeButton = view.findViewById(R.id.remove_qrcode);

        // Load QR codes from Firebase
        loadQRcodeFromFirebase();

        adminGoBack.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .commit();
        });

        // Set item click listener for ListView
        qrcodeListView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            selectedQRcode = qrcodeList.get(position);
            Toast.makeText(getContext(), "Selected: " + selectedQRcode.getEventId(), Toast.LENGTH_SHORT).show();
        });

        // Handle Remove Button Click
        removeButton.setOnClickListener(v -> {
            if (selectedQRcode != null) {
                removeQRcode(selectedQRcode);
            } else {
                Toast.makeText(getContext(), "Please select a QR code to remove.", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
    /**
     * Loads QR codes from Firebase Firestore's "Events" collection.
     * Filters events with non-empty QR code hash and updates the list.
     */
    private void loadQRcodeFromFirebase() {
        db.collection("Events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                Event event = document.toObject(Event.class);
                                if (event != null) {
                                    Log.d("QR code Admin", "Event found and getting ID");
//                                    event.setEventId(document.getId()); // Set eventId from document ID
                                    //qrcodeList.add(event);
                                    // Only add events with non-null and non-empty qrcodehash
                                    Log.d("QR code Admin", "EventID: " + event.getEventId());
                                    Log.d("QR code Admin", "Hash: " + event.getQRcodeHash());
                                    if (event.getQRcodeHash() != null && !event.getQRcodeHash().isEmpty()) {
                                        qrcodeList.add(event);
                                    }
                                }
                            }
                            qrcodeAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    /**
     * Removes the QR code associated with the selected event from Firestore.
     * The QR code is removed by setting the "qrcodehash" field to null.
     *
     * @param qrcode The event object representing the QR code to be removed.
     */
    private void removeQRcode(Event qrcode) {
        if (qrcode.getEventId() == null || qrcode.getEventId().isEmpty()) {
            Toast.makeText(getContext(), "Invalid QR Code. Cannot delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventId = qrcode.getEventId();
        db.collection("Events").document(eventId)
                .update("qrcodehash", null) // Remove the qrcodehash field
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "QR Code successfully removed.", Toast.LENGTH_SHORT).show();

                    // Remove the item from the list and update the adapter
                    qrcodeList.remove(qrcode);
                    qrcodeAdapter.notifyDataSetChanged();

                    Log.d("RemoveQRcode", "QR Code hash removed for Event ID " + eventId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to remove QR Code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("RemoveQRcode", "Failed to remove QR Code hash for Event ID " + eventId, e);
                });
    }
}
