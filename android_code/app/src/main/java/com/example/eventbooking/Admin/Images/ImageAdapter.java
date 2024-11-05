package com.example.eventbooking.Admin.Images;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;

import com.example.eventbooking.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<Image> {
    private Context context;
    private List<ImageClass> imageList;

    public ImageAdapter(@NonNull Context context, List<ImageClass> imageList) {
        super(context, R.layout.item_image);
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.image_adapter_layout, parent, false);
        }

        ImageClass currentImage = imageList.get(position);

        ImageView imageView = convertView.findViewById(R.id.image);
        TextView linkTextView = convertView.findViewById(R.id.image_link);

        // Load the image using Picasso
//        Picasso.get().load(currentImage.getImageUrl()).into(imageView);
//        linkTextView.setText(currentImage.getImageUrl());

        return convertView;
    }
}
