package com.example.eventbooking.Home;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventbooking.Events.EventData.Event;
import com.example.eventbooking.R;

import java.util.List;


import androidx.annotation.NonNull;

public class HomeUserEventAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> eventList;
    private String currentUserId;

    /**
     * Constructor for HomeUserEventAdapter.
     *
     * @param context      The context of the current activity.
     * @param eventList    List of events to display.
     * @param currentUserId The ID of the current user.
     */
    public HomeUserEventAdapter(@NonNull Context context, List<Event> eventList, String currentUserId) {
        super(context, R.layout.event_adapter_layout, eventList);
        this.context = context;
        this.eventList = eventList;
        this.currentUserId = currentUserId;
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
        Event event = eventList.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.event_adapter_layout, null);

        TextView eventIdView = view.findViewById(R.id.event_id);
        TextView eventTitleView = view.findViewById(R.id.event_title);
        TextView userListNameView = view.findViewById(R.id.user_list_name);

        eventIdView.setText(event.getEventId());
        eventTitleView.setText(event.getEventTitle());

        // Determine the user's list membership
        String listName = "Unknown";
        if (event.getAcceptedParticipantIds().contains(currentUserId)) {
            listName = "Accepted";
        } else if (event.getWaitingParticipantIds().contains(currentUserId)) {
            listName = "Waiting";
        } else if (event.getCanceledParticipantIds().contains(currentUserId)) {
            listName = "Canceled";
        } else if (event.getSignedUpParticipantIds().contains(currentUserId)) {
            listName = "Signed Up";
        }

        userListNameView.setText("List: " + listName);

        return view;
    }

    public void updateEvents(List<Event> events) {
        clear();
        addAll(events);
        notifyDataSetChanged();
    }
}
