package com.bsstudio.counter.status_saver.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bsstudio.counter.status_saver.R;


public class SettingsFragment extends Fragment {

    private LinearLayout shareApp,privacyPolicy,rateUs,autoSave,notification;

    private Switch autoDownloadSwitch;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_settings, container, false);

        shareApp=view.findViewById(R.id.share);
        privacyPolicy=view.findViewById(R.id.privacypolicy);
        shareApp=view.findViewById(R.id.share);
        rateUs=view.findViewById(R.id.rateus);

        shareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "Check This App: https://play.google.com/store/apps/details?id=" + getContext().getPackageName());
                shareIntent.setType("text/plain");
                startActivity(shareIntent);

            }});
    privacyPolicy.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://bsgamesstudio.blogspot.com/p/bs-games-studio-privacy-policy.html"));
            startActivity(intent);

        }
    });
    rateUs.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("market://details?id=" + getContext().getPackageName()))));
            } catch (ActivityNotFoundException e1) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(("http://play.google.com/store/apps/details?id=" + getContext().getPackageName()))));
                } catch (ActivityNotFoundException e2) {
                    Toast.makeText(getContext(), "You don't have any app that can open this link", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });




        return view;
    }




}