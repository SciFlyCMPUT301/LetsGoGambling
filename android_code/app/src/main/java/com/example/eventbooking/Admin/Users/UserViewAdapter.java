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

import java.util.ArrayList;
import java.util.List;

public class UserViewAdapter extends ArrayAdapter<User> {


    private Context context;
    private List<User> userList;

    //constructor, call on creation
    public UserViewAdapter(@NonNull Context context, ArrayList<User> userList) {
        super(context, R.layout.user_adapter_layout, userList);
        this.context = context;
        this.userList = userList;
    }

    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        User user = userList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.user_adapter_layout, null);

        TextView deviceID = (TextView) view.findViewById(R.id.device_id);
        TextView username = (TextView) view.findViewById(R.id.username);
        TextView dateJoined = (TextView) view.findViewById(R.id.date_joined);
        deviceID.setText(user.getDeviceID());
        username.setText(user.getUsername());
        dateJoined.setText("Today");

//        //get the image associated with this property
//        int imageID = context.getResources().getIdentifier(property.getImage(), "drawable", context.getPackageName());
//        image.setImageResource(imageID);

        return view;
    }





}
