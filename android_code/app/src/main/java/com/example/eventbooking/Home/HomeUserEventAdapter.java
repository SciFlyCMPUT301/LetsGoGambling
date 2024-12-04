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

/**
 * HomeUserEventAdapter is a custom adapter for displaying event information in a list view.
 * It binds event data to the views for each list item.
 */
@SuppressWarnings("all")
public class HomeUserEventAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> eventList;
    private String currentUserId;

    /**
     * Constructor for HomeUserEventAdapter.
     *
     * @param context        The context of the current activity.
     * @param eventList      List of events to display.
     * @param currentUserId  The ID of the current user.
     */
    public HomeUserEventAdapter(@NonNull Context context, List<Event> eventList, String currentUserId) {
        super(context, R.layout.event_adapter_layout, eventList);
        this.context = context;
        this.eventList = eventList;
        this.currentUserId = currentUserId;
    }

    /**
     * Returns the view for a specific item in the list.
     * This method inflates the item layout and populates it with data from the event.
     *
     * @param position      The position of the event in the list.
     * @param convertView   Reusable view that can be recycled.
     * @param parent        The parent view group.
     * @return The view for the event at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Event event = eventList.get(position);
        // Inflating the layout for each list item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.event_adapter_layout, null);
        // Initializing views
        TextView eventIdView = view.findViewById(R.id.event_id);
        TextView eventTitleView = view.findViewById(R.id.event_title);
        TextView userListNameView = view.findViewById(R.id.user_list_name);
        // Setting the event's ID and title in the views
        eventIdView.setText(event.getEventId());
        eventTitleView.setText(event.getEventTitle());

        // Determine the user's membership status in the event
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
        // Display the user's membership status in the event
        userListNameView.setText("List: " + listName);

        return view;
    }
    /**
     * Updates the list of events displayed in the adapter.
     * This method clears the current list and adds the new events.
     *
     * @param events The new list of events to display.
     */
    public void updateEvents(List<Event> events) {
        clear();
        addAll(events);
        notifyDataSetChanged();
    }
}
