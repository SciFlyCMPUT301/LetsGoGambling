package com.example.eventbooking.Admin.QRcode;

import android.os.Bundle;
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
import com.example.eventbooking.Admin.Event.EditEventFragment;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewQRcodeFragment extends Fragment {
    private FirebaseFirestore db;
    private Button adminGoBack;
    private QRcodeViewAdapter qrcodeAdapter;
    private ArrayList<Event> qrcodeList;
    private ListView qrcodeListView;

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

        // Load QR codes from Firebase
        loadQRcodeFromFirebase();

        adminGoBack.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new AdminFragment())
                    .commit();
        });

        qrcodeListView.setOnItemClickListener((AdapterView<?> parent, View v, int position, long id) -> {
            Event selectedQRcode = qrcodeList.get(position);
            openqrcodeDetailsFragment(selectedQRcode);
        });

        return view;
    }

    private void loadQRcodeFromFirebase() {
        db.collection("Events").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                Event event = document.toObject(Event.class);
                                if (event != null) {
                                    event.setEventId(document.getId()); // Set eventId from document ID
                                    qrcodeList.add(event);
                                }
                            }
                            qrcodeAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to load events", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openqrcodeDetailsFragment(Event selectedEvent) {
        EditEventFragment detailFragment = new EditEventFragment(selectedEvent);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }
}
