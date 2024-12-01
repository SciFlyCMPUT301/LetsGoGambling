package com.example.eventbooking.Admin.Event;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.eventbooking.Admin.AdminFragment;
import com.example.eventbooking.Admin.QRcode.QRcodeViewAdapter;
import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.R;
import com.example.eventbooking.UniversalProgramValues;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class QRCodeFragment extends Fragment {
    private FirebaseFirestore db;
    private Button eventGoBack, removeButton;
    private QRcodeViewAdapter qrcodeAdapter;
    private ArrayList<Event> qrcodeList;
    private ListView qrcodeListView;
    private Event selectedQRcode = null;
    private Event choosenEvent;
    private ImageView QRcode;
    private QRcodeGenerator qrCodeGenerator;



    public QRCodeFragment(Event selectedEvent) {
        this.choosenEvent = selectedEvent;
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_qr_code_details, container, false);
        if(!UniversalProgramValues.getInstance().getTestingMode())
            db = FirebaseFirestore.getInstance();
        eventGoBack = view.findViewById(R.id.admin_go_back_event);
        removeButton = view.findViewById(R.id.remove_qrcode);
        QRcode  = view.findViewById(R.id.qrcode_image_view);
        QRcode.setImageBitmap(qrCodeGenerator.generateAndSendBackQRCode(choosenEvent.getEventId()));
        // Load QR codes from Firebase

        TextView eventIDTextView = view.findViewById(R.id.qrcode_event_id);
        TextView hashedqrcodeTextView = view.findViewById(R.id.hashed_qrcode_event);

        // Bind data
        eventIDTextView.setText(choosenEvent.getEventId());
        hashedqrcodeTextView.setText(choosenEvent.getQRcodeHash());

        eventGoBack.setOnClickListener(v -> {
            EditEventFragment detailFragment = new EditEventFragment(choosenEvent);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });


        // Handle Remove Button Click
        removeButton.setOnClickListener(v -> {
                choosenEvent.getNewEventQRHash();
                QRcode.setImageBitmap(qrCodeGenerator.generateAndSendBackQRCode(choosenEvent.getEventId()));
                hashedqrcodeTextView.setText(choosenEvent.getQRcodeHash());
        });
        return view;
    }

}
