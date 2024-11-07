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
/**
 * ImageAdapter is a custom ArrayAdapter for displaying images in a ListView or GridView.
 * Each item in the view contains an image and its associated link.
 */
public class ImageAdapter extends ArrayAdapter<Image> {
    private Context context;
    private List<ImageClass> imageList;
    /**
     * Constructor for ImageAdapter.
     *
     * @param context   the context in which the adapter is used
     * @param imageList the list of ImageClass objects to be displayed
     */
    public ImageAdapter(@NonNull Context context, List<ImageClass> imageList) {
        super(context, R.layout.item_image);
        this.context = context;
        this.imageList = imageList;
    }

    /**
     * Returns the view for a specific item in the list.
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
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
