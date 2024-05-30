package com.bsstudio.counter.status_saver.Adpaters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bsstudio.counter.status_saver.Models.Status;
import com.bsstudio.counter.status_saver.R;
import com.bsstudio.counter.status_saver.Utils.General;
import com.bumptech.glide.Glide;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyHolder> {

    private final List<Status> videoList;
    private final ConstraintLayout container;
    private final SharedPreferences sharedPreferences;
    private Context context;


    public VideoAdapter(List<Status> videoList, ConstraintLayout container, Context context) {
        this.videoList = videoList;
        this.container = container;
        this.sharedPreferences = context.getSharedPreferences("toggle_states", Context.MODE_PRIVATE);
    }




    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.videos_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

         Status status = videoList.get(position);

        if (status.isApi30()) {
//            holder.save.setVisibility(View.GONE);
            Glide.with(context).load(status.getDocumentFile().getUri()).into(holder.imageView);
        } else {
//            holder.save.setVisibility(View.VISIBLE);
            Glide.with(context).load(status.getFile()).into(holder.imageView);
        }

        boolean isDownloaded = sharedPreferences.getBoolean("downloaded_" + position, false);

        if (isDownloaded) {
            holder.save.setImageResource(R.drawable.ic_double_tick20);
        } else {
            holder.save.setImageResource(R.drawable.ic_downlod20);
        }

        holder.save.setOnClickListener(v -> {
            if (isDownloaded) {
                holder.save.setImageResource(R.drawable.ic_downlod20);
                Toast.makeText(context, "Already downloaded", Toast.LENGTH_SHORT).show();
            } else {
                General.copyFile(status, context, container);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("downloaded_" + position, true);
                editor.apply();
                holder.save.setImageResource(R.drawable.ic_double_tick20);
            }
        });



        holder.share.setOnClickListener(v -> {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            shareIntent.setType("image/mp4");
            if (status.isApi30()) {
                shareIntent.putExtra(Intent.EXTRA_STREAM, status.getDocumentFile().getUri());
            } else {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + status.getFile().getAbsolutePath()));
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share image"));

        });

        LayoutInflater inflater = LayoutInflater.from(context);
        final View view1 = inflater.inflate(R.layout.video_view, null);

        holder.imageView.setOnClickListener(v -> {

            final AlertDialog.Builder alertDg = new AlertDialog.Builder(context);

            FrameLayout mediaControls = view1.findViewById(R.id.videoViewWrapper);

            if (view1.getParent() != null) {
                ((ViewGroup) view1.getParent()).removeView(view1);
            }

            alertDg.setView(view1);

            final VideoView videoView = view1.findViewById(R.id.video_full);

            final MediaController mediaController = new MediaController(context, false);

            videoView.setOnPreparedListener(mp -> {

                mp.start();
                mediaController.show(0);
                mp.setLooping(true);
            });

            videoView.setMediaController(mediaController);
            mediaController.setMediaPlayer(videoView);

            if (status.isApi30()) {
                videoView.setVideoURI(status.getDocumentFile().getUri());
            } else {
                videoView.setVideoURI(Uri.fromFile(status.getFile()));
            }
            videoView.requestFocus();

            ((ViewGroup) mediaController.getParent()).removeView(mediaController);

            if (mediaControls.getParent() != null) {
                mediaControls.removeView(mediaController);
            }

            mediaControls.addView(mediaController);

            final AlertDialog alert2 = alertDg.create();

            alert2.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
            alert2.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alert2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            alert2.show();

        });

    }



    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public ImageView  share,save;
        public ImageView imageView;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ivThumbnail);
            save = itemView.findViewById(R.id.save);
            share = itemView.findViewById(R.id.share);
        }
    }
    private boolean isItemDeleted(int position) {
        Status status = videoList.get(position);
        if (status.isApi30()) {
            return !status.getDocumentFile().exists();
        } else {
            return !status.getFile().exists();
        }
    }

}