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
/**
 * Adapter class for displaying QR codes associated with events in a ListView.
 * Each list item includes the QR code image, event ID, event title, and the hashed QR code string.
 */

public class QRcodeViewAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> qrcodeList;
    /**
     * Constructs a new QRcodeViewAdapter.
     *
     * @param context    The context of the application or activity.
     * @param qrcodeList A list of Event objects representing the events and their associated QR codes.
     */

    public QRcodeViewAdapter(Context context, List<Event> qrcodeList) {
        super(context, R.layout.qrcode_adapter_layout, qrcodeList);
        this.context = context;
        this.qrcodeList = qrcodeList;
    }
    /**
     * Provides a view for an adapter's data at the specified position in the dataset.
     *
     * @param position    The position of the data item in the dataset.
     * @param convertView The old view to reuse, if possible. If null, a new view is created.
     * @param parent      The parent view that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the layout for the list item if not already created
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.qrcode_adapter_layout, parent, false);
        }
        // Retrieve the Event object for the current position
        Event qrcode = qrcodeList.get(position);
        // Bind UI components
        ImageView qrCodeImageView = convertView.findViewById(R.id.qrcode_image_view);
        TextView eventIDTextView = convertView.findViewById(R.id.qrcode_event_id);
        TextView eventTitleTextView = convertView.findViewById(R.id.qrcode_event_title);
        TextView hashedqrcodeTextView = convertView.findViewById(R.id.hashed_qrcode_event);

        // Populate the text fields with data from the Event object
        eventIDTextView.setText(qrcode.getEventId());
        eventTitleTextView.setText(qrcode.getEventTitle());
        hashedqrcodeTextView.setText(qrcode.getQRcodeHash());

        // Generate a QR code bitmap using the QRcodeGenerator and set it to the ImageView
        Bitmap qrCodeBitmap = new QRcodeGenerator(context).generateQRCode(qrcode.getQRcodeHash());
        if (qrCodeBitmap != null) {
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
        }
        else {
            qrCodeImageView.setImageResource(R.drawable.placeholder_image_foreground); // Placeholder if QR code fails
        }

        return convertView;
    }
}
