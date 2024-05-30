package com.bsstudio.counter.status_saver.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bsstudio.counter.status_saver.R;
import com.bumptech.glide.Glide;


public class ImageViewFragment extends Fragment {

    private Uri imageUri;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_image_view, container, false);

        // Get the ImageView
        ImageView imageView = view.findViewById(R.id.imageView);

        // Display the image
        Glide.with(this).load(imageUri).into(imageView);

        return view;

    }
}