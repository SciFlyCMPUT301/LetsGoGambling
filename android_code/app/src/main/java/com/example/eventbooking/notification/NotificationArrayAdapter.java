package com.example.eventbooking.notification;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventbooking.Notification;
import com.example.eventbooking.R;

import java.util.List;
/**
 * Adapter for displaying a list of notifications in a ListView.
 * Binds the data from a list of notifications to views in the list.
 */
public class NotificationArrayAdapter extends ArrayAdapter<Notification> {
    private Context context;
    private List<Notification> notificationList;
    /**
     * Constructs a NotificationArrayAdapter to manage the list of notifications.
     *
     * @param context          The context in which the adapter is being created (typically an Activity or Service).
     * @param notificationList The list of Notification objects to be displayed.
     */
    public NotificationArrayAdapter(@NonNull Context context, List<Notification> notificationList) {
        super(context, R.layout.user_adapter_layout, notificationList);
        this.context = context;
        this.notificationList = notificationList;
    }
    /**
     * Returns the view for a specific item in the list.
     *
     * @param position    The position of the item within the list.
     * @param convertView Reusable view that can be recycled.
     * @param parent      The parent view group that this view will be attached to.
     * @return A View object representing the item in the list.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the Notification object at the specified position
        Notification notification = notificationList.get(position);
        // Inflate a new view for the list item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.notification_adapter_layout, null);
        // Find the TextViews to display the notification's title and text
        TextView notificationTitleView = view.findViewById(R.id.notification_title);
        TextView notificationTextView = view.findViewById(R.id.notification_text);
        // Set the title and text for the notification
        notificationTitleView.setText(notification.getTitle());
        notificationTextView.setText(notification.getText());
        // Return the view for the list item
        return view;
    }
}
