package com.example.eventbooking.Facility;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventbooking.R;

import java.util.ArrayList;
import java.util.List;

public class FacilityViewAdapter extends ArrayAdapter<Facility> {
    private Context context;
    private List<Facility> facilityList;

    public FacilityViewAdapter(@NonNull Context context, ArrayList<Facility> facilityList) {
        super(context, R.layout.facility_adapter_layout2, facilityList);
        this.context = context;
        this.facilityList = facilityList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        Facility facility = facilityList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.facility_adapter_layout2, null);

        TextView facilityID = (TextView) view.findViewById(R.id.facility_id);
        TextView facilityname = (TextView) view.findViewById(R.id.facility_name);
        TextView organzier = (TextView) view.findViewById(R.id.facility_organizer);
        facilityID.setText(facility.getFacilityID());
        facilityname.setText(facility.getName());
        organzier.setText(facility.getOrganizer());

        return view;
    }
}
