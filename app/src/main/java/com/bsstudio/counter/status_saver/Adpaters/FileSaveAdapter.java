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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bsstudio.counter.status_saver.Models.Status;
import com.bumptech.glide.Glide;
import com.bsstudio.counter.status_saver.R;

import java.util.List;

public class FileSaveAdapter extends RecyclerView.Adapter<FileSaveAdapter.MyHolder> {

    private final List<Status> imagesList;
    private Context context;

    Status status;

    public FileSaveAdapter(List<Status> imagesList) {
        this.imagesList = imagesList;
    }




    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.savedfile, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {


        holder.save.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_delete__24));
        holder.share.setVisibility(View.VISIBLE);
        holder.save.setVisibility(View.VISIBLE);

        final Status status = imagesList.get(position);

        if (status.isApi30()) {
            Glide.with(context).load(status.getDocumentFile().getUri()).into(holder.imageView);
        } else {
            Glide.with(context).load(status.getFile()).into(holder.imageView);
        }

//        if (status.isVideo())
//            Glide.with(context).asBitmap().load(status.getFile()).into(holder.imageView);
////            holder.imageView.setImageBitmap(status.getThumbnail());
//        else {
//            if(status.isApi30()) {
//                Glide.with(context).load(status.getDocumentFile().getUri()).into(holder.imageView);
//            } else  {
//                Glide.with(context).load(status.getFile()).into(holder.imageView);
//            }
//        }

        holder.save.setOnClickListener(view -> {
            if (status.getFile().delete()) {
                // Remove the downloaded status from SharedPreferences
                SharedPreferences.Editor editor = context.getSharedPreferences("toggle_states", Context.MODE_PRIVATE).edit();
                editor.remove("downloaded_" + position);
                editor.apply();

                imagesList.remove(position);
                notifyDataSetChanged();


                // Update the download state of the remaining images


                Toast.makeText(context, "File Deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Unable to Delete File", Toast.LENGTH_SHORT).show();
            }

        });


        holder.share.setOnClickListener(v -> {

            Intent shareIntent = new Intent(Intent.ACTION_SEND);

            if (status.isVideo())
                shareIntent.setType("image/mp4");
            else
                shareIntent.setType("image/jpg");

            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + status.getFile().getAbsolutePath()));
            context.startActivity(Intent.createChooser(shareIntent, "Share image"));

        });

        LayoutInflater inflater = LayoutInflater.from(context);
        final View view1 = inflater.inflate(R.layout.video_view, null);

        holder.imageView.setOnClickListener(v -> {

            if (status.isVideo()) {

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
                videoView.setVideoURI(Uri.fromFile(status.getFile()));
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

            } else {

                final AlertDialog.Builder alertD = new AlertDialog.Builder(context);
                LayoutInflater inflater1 = LayoutInflater.from(context);
                View view = inflater1.inflate(R.layout.image_view, null);
                alertD.setView(view);

                ImageView imageView = view.findViewById(R.id.img);
                if (status.isApi30()) {
                    Glide.with(context).load(status.getDocumentFile().getUri()).into(imageView);
                } else {
                    Glide.with(context).load(status.getFile()).into(imageView);
                }

                AlertDialog alert = alertD.create();
                alert.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;
                alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alert.show();

            }

        });

    }



    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public ImageView save, share;
        public ImageView imageView;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.ivThumbnail);
            save = itemView.findViewById(R.id.save);
            share = itemView.findViewById(R.id.share);
        }
    }


}
