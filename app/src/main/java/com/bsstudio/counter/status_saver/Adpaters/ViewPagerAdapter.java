package com.bsstudio.counter.status_saver.Adpaters;

import android.content.Context;
import android.net.Uri;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bsstudio.counter.status_saver.Fragment.ImageFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<Uri> imageUris = new ArrayList<>();

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
    }

    public void setImageUris(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    @Override
    public Fragment getItem(int position) {
        return new ImageFragment(imageUris.get(position));
    }

    @Override
    public int getCount() {
        return imageUris.size();
    }
}