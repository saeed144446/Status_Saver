package com.bsstudio.counter.status_saver.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bsstudio.counter.status_saver.Adpaters.ViewPagerAdapter;
import com.bsstudio.counter.status_saver.R;

import java.util.ArrayList;

public class ImageviewActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private Button btnSave, btnShare, btnRepost;
    ViewPagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageview_activity);

        viewPager = findViewById(R.id.viewPager);
        btnSave = findViewById(R.id.btnSave);
        btnShare = findViewById(R.id.btnShare);
        btnRepost = findViewById(R.id.btnRepost);

        ArrayList<Uri> imageUris = getIntent().getParcelableArrayListExtra("imageUris");

        // Set up the ViewPager adapter
      adapter = new ViewPagerAdapter(this, getSupportFragmentManager());
        adapter.setImageUris(imageUris);
        viewPager.setAdapter(adapter);


        // Set up the button clicks
        btnSave.setOnClickListener(v -> {
            // Save image logic here
        });

        btnShare.setOnClickListener(v -> {
            // Share image logic here
        });

        btnRepost.setOnClickListener(v -> {
            // Repost image logic here
        });
    }
}