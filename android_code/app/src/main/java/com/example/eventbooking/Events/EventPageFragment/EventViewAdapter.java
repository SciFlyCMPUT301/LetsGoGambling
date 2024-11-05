package com.example.eventbooking.Events.EventPageFragment;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.Facility;
import com.example.eventbooking.R;

import java.util.ArrayList;
import java.util.List;


/// TODO:
//Change this to event_adapter_layout


public class EventViewAdapter extends ArrayAdapter<Event> {

    private Context context;
    private List<Event> eventList;
    private String userId = "User1";

    //constructor, call on creation
    public EventViewAdapter(@NonNull Context context, ArrayList<Event> eventList) {
        super(context, R.layout.event_adapter_view, eventList);
        this.context = context;
        this.eventList = eventList;
    }

    //called when rendering the list
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


        deviceID.setText(event.getEventId());
        username.setText(event.getEventTitle());
        dateJoined.setText("Today");

        String status;
        if (event.getAcceptedParticipantIds().contains(userId)) {
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


//        //get the image associated with this property
//        int imageID = context.getResources().getIdentifier(property.getImage(), "drawable", context.getPackageName());
//        image.setImageResource(imageID);

        return view;
    }
}
