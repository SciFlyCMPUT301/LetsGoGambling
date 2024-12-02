package com.example.eventbooking.Admin.Event;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
/**
 * Adapter class for displaying a list of events in a ListView or GridView.
 * <p>
 * This adapter binds event data to the provided layout, allowing for customization of each item in the list.
 * It uses the Picasso library to load event images efficiently.
 */
public class EventViewAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> eventList;
    /**
     * Constructs a new EventViewAdapter.
     *
     * @param context   The context in which the adapter is operating.
     * @param eventList The list of Event objects to display.
     */

    public EventViewAdapter(@NonNull Context context, List <Event> eventList) {
        super(context, R.layout.event_adapter_layout, eventList);
        this.context = context;
        this.eventList = eventList;
    }
    /**
     * Provides a view for an AdapterView (ListView or GridView).
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible. If not null, this view will be reused to save memory.
     * @param parent      The parent ViewGroup that this view will be attached to.
     * @return A View corresponding to the data at the specified position.
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        // Get the user object for the current position
        Event event = eventList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.event_adapter_layout2, null);
        // Initialize UI components
        ImageView eventPoster = (ImageView) view.findViewById(R.id.event_poster);
        TextView eventID = (TextView) view.findViewById(R.id.event_id);
        TextView eventName = (TextView) view.findViewById(R.id.event_name);
        TextView dateJoined = (TextView) view.findViewById(R.id.date_joined);
        // Set event details
        eventID.setText(event.getEventId());
        eventName.setText(event.getEventTitle());
        dateJoined.setText("Today"); // placeholder for date

//        //get the image associated with this property
        // Load the event image using Picasso
        String profilePictureUrl = event.getEventPosterURL();
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            Picasso.get()
                    .load(profilePictureUrl) // URL of the image
                    .placeholder(R.drawable.placeholder_image_foreground) // Placeholder image while loading
                    .error(R.drawable.placeholder_image_foreground) // Error image if loading fails
                    .into(eventPoster); // ImageView to load the image into
        }

        return view;
    }

    public void updateEvents(List<Event> events) {
        clear();
        addAll(events);
        notifyDataSetChanged();
    }
}
