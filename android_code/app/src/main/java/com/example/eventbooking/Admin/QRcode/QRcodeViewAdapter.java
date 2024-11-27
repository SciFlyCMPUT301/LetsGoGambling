package com.example.eventbooking.Admin.QRcode;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.R;
import com.squareup.picasso.Picasso;

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

        eventIDTextView.setText(qrcode.getEventId());
        eventTitleTextView.setText(qrcode.getEventTitle());

        String qrCodeUrl = qrcode.getQrcodehash();
        if (qrCodeUrl != null && !qrCodeUrl.isEmpty()) {
            Picasso.get()
                    .load(qrCodeUrl)
                    .placeholder(R.drawable.placeholder_image_foreground)
                    .error(R.drawable.placeholder_image_foreground)
                    .into(qrCodeImageView);
        } else {
            qrCodeImageView.setImageResource(R.drawable.placeholder_image_foreground);
        }

        return convertView;
    }
}
