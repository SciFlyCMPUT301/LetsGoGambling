package com.example.eventbooking.Admin.Facility;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.eventbooking.Facility.Facility;
import com.example.eventbooking.R;

import java.util.ArrayList;
import java.util.List;
/**
 * FacilityViewAdapter is a custom ArrayAdapter for displaying facility data in a ListView.
 * Each item in the list represents a facility and displays relevant details such as:
 * <ul>
 *   <li>Facility ID</li>
 *   <li>Facility name</li>
 *   <li>Joining date (placeholder set as "Today")</li>
 * </ul>
 *
 * The adapter inflates the `facility_adapter_layout` to render each facility item.
 */
public class FacilityViewAdapter extends ArrayAdapter<Facility> {

    private Context context;
    private List<Facility> facilityList;

    /**
     * Constructor for the FacilityViewAdapter.
     *
     * @param context The current context, used for inflating the layout.
     * @param facilityList The list of facilities to be displayed in the ListView.
     */
    public FacilityViewAdapter(@NonNull Context context, ArrayList<Facility> facilityList) {
        super(context, R.layout.user_adapter_layout, facilityList);
        this.context = context;
        this.facilityList = facilityList;
    }

    //called when rendering the list

    /**
     * Returns the view for a specific item in the ListView.
     *
     * @param position The position of the item in the list.
     * @param convertView The old view to reuse, if possible (can be null).
     * @param parent The parent ViewGroup that this view will be attached to.
     * @return The View corresponding to the data at the specified position.
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the facility at the current position
        Facility facility = facilityList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.facility_adapter_layout, null);
        // Bind data to the views
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