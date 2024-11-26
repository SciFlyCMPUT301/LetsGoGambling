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

public class ImageAdapter extends ArrayAdapter<Map<String, String>> {
    private Context context;
    private ArrayList<Map<String, String>> imageList;

    public ImageAdapter(Context context, ArrayList<Map<String, String>> imageList) {
        super(context, R.layout.item_image, imageList);
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.image_adapter_layout, parent, false);
        }

        Map<String, String> imageData = imageList.get(position);

        ImageView imageView = convertView.findViewById(R.id.image_view);
        TextView sourceTextView = convertView.findViewById(R.id.source_id);
        TextView collectionTextView = convertView.findViewById(R.id.collection);

        // Load the image URL into the ImageView using Picasso
        Picasso.get().load(imageData.get("imageUrl")).into(imageView);

        // Set the source and collection information
        sourceTextView.setText(imageData.get("source"));
        collectionTextView.setText(imageData.get("collection"));

        return convertView;
    }
}