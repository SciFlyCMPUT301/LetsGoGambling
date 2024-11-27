package com.example.eventbooking.notification;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility;
import com.example.eventbooking.Notification;
import com.example.eventbooking.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationArrayAdapter extends ArrayAdapter<Notification> {
    private Context context;
    private List<Notification> notificationList;

    public NotificationArrayAdapter(@NonNull Context context, List<Notification> notificationList) {
        super(context, R.layout.user_adapter_layout, notificationList);
        this.context = context;
        this.notificationList = notificationList;
    }

    /**
     * Returns the view for a specific item in the list.
     *
     * @param position    Position of the event in the list.
     * @param convertView Reusable view.
     * @param parent      Parent view group.
     * @return View for the list item.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Notification notification = notificationList.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.notification_adapter_layout, null);

        TextView notificationTitleView = view.findViewById(R.id.notification_title);
        TextView notificationTextView = view.findViewById(R.id.notification_text);

        notificationTitleView.setText(notification.getTitle());
        notificationTextView.setText(notification.getText());

        return view;
    }
}
