package com.example.eventbooking.Admin.Images;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eventbooking.R;
import com.example.eventbooking.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageAdapter extends ArrayAdapter<ImageClass> {
    private Context context;
//    private ArrayList<Map<String, String>> imageList;
    private List <ImageClass> imageList;
    private ImageClass selectedImage;

    public ImageAdapter(Context context, List <ImageClass> imageList) {
        super(context, R.layout.item_image, imageList);
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.image_adapter_layout, parent, false);
        }

//        Map<String, String> imageData = imageList.get(position);

        selectedImage = imageList.get(position);

        ImageView imageView = convertView.findViewById(R.id.image_view);
        TextView sourceTextView = convertView.findViewById(R.id.source_id);
        TextView nameTextView = convertView.findViewById(R.id.source_name);
        TextView collectionTextView = convertView.findViewById(R.id.collection);

        // Load the image URL into the ImageView using Picasso
//        Picasso.get().load(imageData.get("imageUrl")).into(imageView);
        Picasso.get().load(selectedImage.getImageUrl()).into(imageView);

        // Set the source and collection information
        sourceTextView.setText(selectedImage.getSource());
        nameTextView.setText(selectedImage.getDocumentId());
        collectionTextView.setText(selectedImage.getCollection());

        return convertView;
    }
}