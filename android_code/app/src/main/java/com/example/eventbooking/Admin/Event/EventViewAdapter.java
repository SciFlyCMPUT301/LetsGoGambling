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

public class EventViewAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> eventList;

    public EventViewAdapter(@NonNull Context context, ArrayList<Event> eventList) {
        super(context, R.layout.event_adapter_layout, eventList);
        this.context = context;
        this.eventList = eventList;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        // Get the user object for the current position
        Event event = eventList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.event_adapter_layout2, null);

        ImageView eventPoster = (ImageView) view.findViewById(R.id.event_poster);
        TextView eventID = (TextView) view.findViewById(R.id.event_id);
        TextView eventName = (TextView) view.findViewById(R.id.event_name);
        TextView dateJoined = (TextView) view.findViewById(R.id.date_joined);
        eventID.setText(event.getEventId());
        eventName.setText(event.getEventTitle());
        dateJoined.setText("Today");

//        //get the image associated with this property
        String profilePictureUrl = event.getImageUrl();
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            Picasso.get()
                    .load(profilePictureUrl) // URL of the image
                    .placeholder(R.drawable.placeholder_image_foreground) // Placeholder image while loading
                    .error(R.drawable.placeholder_image_foreground) // Error image if loading fails
                    .into(eventPoster); // ImageView to load the image into
        }

        return view;
    }
}
