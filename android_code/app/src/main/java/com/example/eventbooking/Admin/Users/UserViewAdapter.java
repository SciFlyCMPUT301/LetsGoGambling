package com.example.eventbooking.Admin.Users;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
/**
 * UserViewAdapter is a custom ArrayAdapter to display a list of User objects
 * in a ListView. Each list item displays the user's device ID, username, date joined,
 * and profile picture (if available).
 */
public class UserViewAdapter extends ArrayAdapter<User> {


    private Context context;
    private List<User> userList;
    /**
     * Constructor to initialize the adapter with context and user list data.
     *
     * @param context  The current context, used to inflate the layout.
     * @param userList The list of User objects to display in the ListView.
     */
    //constructor, call on creation
    public UserViewAdapter(@NonNull Context context, List<User> userList) {
        super(context, R.layout.user_adapter_layout, userList);
        this.context = context;
        this.userList = userList;
    }
    /**
     * Provides a view for each user in the list. This method inflates the layout
     * and binds data (device ID, username, date joined, profile picture) to the views.
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible (this is not used in this case).
     * @param parent      The parent view that this view will eventually be attached to.
     * @return The View corresponding to the data at the specified position.
     */
    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        // Get the user object for the current position
        User user = userList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);// The context used to access system services and inflate layouts
        View view = inflater.inflate(R.layout.user_adapter_layout, null); // The list of users to be displayed in the ListView

        ImageView userImage = (ImageView) view.findViewById(R.id.user_image);
        TextView deviceID = (TextView) view.findViewById(R.id.device_id);
        TextView username = (TextView) view.findViewById(R.id.username);
        TextView dateJoined = (TextView) view.findViewById(R.id.date_joined);
        deviceID.setText(user.getDeviceID());
        username.setText(user.getUsername());
// For now, displaying a placeholder text "Today" for the date joined.
// In a real app, this could be dynamically set to the actual date the user joined.
        dateJoined.setText("Today");

// Using Picasso to load the user's profile picture from the provided URL.
// Picasso handles caching and efficient image loading.
        String profilePictureUrl = user.getProfilePictureUrl();
        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
            Picasso.get()
                    .load(profilePictureUrl) // URL of the image
                    .placeholder(R.drawable.placeholder_image_foreground) // Placeholder image while loading
                    .error(R.drawable.placeholder_image_foreground) // Error image if loading fails
                    .into(userImage); // ImageView to load the image into
        }

        return view;
    }





}