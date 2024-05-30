package com.bsstudio.counter.status_saver.Adpaters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.bsstudio.counter.status_saver.Fragment.ImageFragment;
import com.bsstudio.counter.status_saver.Fragment.VideoFragment;

public class PagerAdapter extends FragmentPagerAdapter {

    private final int totalTabs;

    public PagerAdapter(@NonNull FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 0)
            return new ImageFragment();

        if (position == 1)
            return new VideoFragment();

        return null;

    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
