package com.example.eventbooking.Events.EventPageFragment;

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


/// TODO:
//Change this to event_adapter_layout

/**
 * EventViewAdapter is a custom ArrayAdapter used for displaying events in a ListView.
 * Each item in the list represents an event with details such as event ID, title, date joined,
 * and the user's list status in relation to the event (e.g., Accepted, Waiting).
 */
public class EventViewAdapter extends ArrayAdapter<Event> {

    private Context context;
    private List<Event> eventList;
    private String userId = "User1";
    private boolean test;

    //constructor, call on creation
    /**
     * Constructor for EventViewAdapter.
     *
     * @param context   the context in which the adapter is used
     * @param eventList the list of Event objects to be displayed
     * @param test      boolean indicating if the adapter is in test mode
     */
    public EventViewAdapter(@NonNull Context context, ArrayList<Event> eventList, boolean test) {
        super(context, R.layout.event_adapter_view, eventList);
        this.context = context;
        this.eventList = eventList;
        this.test = test;
    }

    //called when rendering the list


    /**
     * Returns the view for a specific item in the list.
     *
     * @param position    the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent      the parent view that this view will be attached to
     * @return the View for the specified position in the list
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        Event event = eventList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.event_adapter_view, null);
//        Log.d("Event Adapter", "asdasdasdasdasd");

        TextView deviceID = (TextView) view.findViewById(R.id.event_id);
        TextView username = (TextView) view.findViewById(R.id.event_name);
        TextView dateJoined = (TextView) view.findViewById(R.id.date_joined);
        TextView listStatus = (TextView) view.findViewById(R.id.user_list_status);
        ImageView eventPoster = view.findViewById(R.id.event_poster);


        deviceID.setText(event.getEventId());
        username.setText(event.getEventTitle());
        dateJoined.setText("Today");

        String status;
        if(test == false){
            status = "";
        } else if (event.getAcceptedParticipantIds().contains(userId)) {
            status = "Accepted List";
        } else if (event.getCanceledParticipantIds().contains(userId)) {
            status = "Canceled List";
        } else if (event.getSignedUpParticipantIds().contains(userId)) {
            status = "Signed Up List";
        } else if (event.getWaitingParticipantIds().contains(userId)) {
            status = "Waiting List";
        } else {
            status = "Not in Any List";
        }

        // Set the list status text
        listStatus.setText(status);

        String posterUrl = event.getImageUrl();
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Picasso.get()
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_event_poster_placeholder) // Placeholder while loading
                    .error(R.drawable.ic_event_poster_placeholder) // Error image if loading fails
                    .into(eventPoster); // ImageView to load the image into
        } else {
            // Set a placeholder image if no URL is provided
            eventPoster.setImageResource(R.drawable.ic_event_poster_placeholder);
        }


//        //get the image associated with this property
//        int imageID = context.getResources().getIdentifier(property.getImage(), "drawable", context.getPackageName());
//        image.setImageResource(imageID);

        return view;
    }
}
