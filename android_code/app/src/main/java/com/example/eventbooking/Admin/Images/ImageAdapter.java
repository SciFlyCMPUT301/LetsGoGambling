package com.example.eventbooking.Admin.Images;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventbooking.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;
/**
 * Custom adapter for displaying images and associated metadata in a ListView or GridView.
 * Each item includes an image loaded from a URL, along with its source, document ID, and collection name.
 */
public class ImageAdapter extends ArrayAdapter<Map<String, String>> {
    private Context context;
    private ArrayList<Map<String, String>> imageList;
    /**
     * Constructs a new ImageAdapter.
     *
     * @param context   The context in which the adapter is operating, typically the activity or fragment.
     * @param imageList A list of maps, where each map contains image data (URL, source, document ID, and collection).
     */


    public ImageAdapter(Context context, ArrayList<Map<String, String>> imageList) {
        super(context, R.layout.item_image, imageList);
        this.context = context;
        this.imageList = imageList;
    }
    /**
     * Provides a view for an adapter item in the list.
     *
     * @param position    The position of the item in the data set.
     * @param convertView The recycled view to reuse, if available. If null, a new view will be created.
     * @param parent      The parent ViewGroup that this view will be attached to.
     * @return A view corresponding to the data at the specified position.
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // Inflate a new view if no reusable view is available
            convertView = LayoutInflater.from(context).inflate(R.layout.image_adapter_layout, parent, false);
        }
        // Retrieve the image data for the current position
        Map<String, String> imageData = imageList.get(position);
        // Initialize UI components for the item view
        ImageView imageView = convertView.findViewById(R.id.image_view);
        TextView sourceTextView = convertView.findViewById(R.id.source_id);
        TextView nameTextView = convertView.findViewById(R.id.source_name);
        TextView collectionTextView = convertView.findViewById(R.id.collection);

        // Load the image URL into the ImageView using Picasso
        Picasso.get().load(imageData.get("imageUrl")).into(imageView);

        // Set the source and collection information in the respective TextViews
        sourceTextView.setText(imageData.get("source"));
        nameTextView.setText(imageData.get("documentId"));
        collectionTextView.setText(imageData.get("collection"));

        return convertView;
    }
}