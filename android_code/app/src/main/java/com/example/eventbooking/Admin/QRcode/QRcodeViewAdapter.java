package com.example.eventbooking.Admin.QRcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.QRCode.QRcodeGenerator;
import com.example.eventbooking.R;

import java.util.List;

public class QRcodeViewAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> qrcodeList;

    public QRcodeViewAdapter(Context context, List<Event> qrcodeList) {
        super(context, R.layout.qrcode_adapter_layout, qrcodeList);
        this.context = context;
        this.qrcodeList = qrcodeList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.qrcode_adapter_layout, parent, false);
        }

        Event qrcode = qrcodeList.get(position);

        ImageView qrCodeImageView = convertView.findViewById(R.id.qrcode_image_view);
        TextView eventIDTextView = convertView.findViewById(R.id.qrcode_event_id);
        TextView eventTitleTextView = convertView.findViewById(R.id.qrcode_event_title);
        TextView hashedqrcodeTextView = convertView.findViewById(R.id.hashed_qrcode_event);

        // Bind data
        eventIDTextView.setText(qrcode.getEventId());
        eventTitleTextView.setText(qrcode.getEventTitle());
        hashedqrcodeTextView.setText(qrcode.getQRcodeHash());

        // Generate and set the QR code bitmap
        Bitmap qrCodeBitmap = new QRcodeGenerator(context).generateQRCode(qrcode.getQRcodeHash());
        if (qrCodeBitmap != null) {
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
        } else {
            qrCodeImageView.setImageResource(R.drawable.placeholder_image_foreground); // Placeholder if QR code fails
        }

        return convertView;
    }
}
