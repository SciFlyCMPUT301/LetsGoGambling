package com.example.eventbooking.Admin.Facility;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventbooking.Facility;
import com.example.eventbooking.R;
import com.example.eventbooking.User;

import java.util.ArrayList;
import java.util.List;

public class FacilityViewAdapter extends ArrayAdapter<Facility> {

    private Context context;
    private List<Facility> facilityList;

    //constructor, call on creation
    public FacilityViewAdapter(@NonNull Context context, ArrayList<Facility> facilityList) {
        super(context, R.layout.user_adapter_layout, facilityList);
        this.context = context;
        this.facilityList = facilityList;
    }

    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        Facility facility = facilityList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.facility_adapter_layout, null);

        TextView deviceID = (TextView) view.findViewById(R.id.facility_id);
        TextView username = (TextView) view.findViewById(R.id.facility_name);
        TextView dateJoined = (TextView) view.findViewById(R.id.date_joined);
        deviceID.setText(facility.getFacilityID());
        username.setText(facility.getName());
        dateJoined.setText("Today");

//        //get the image associated with this property
//        int imageID = context.getResources().getIdentifier(property.getImage(), "drawable", context.getPackageName());
//        image.setImageResource(imageID);

        return view;
    }
}