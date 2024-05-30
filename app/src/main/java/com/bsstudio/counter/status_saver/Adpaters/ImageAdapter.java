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
    import android.widget.ImageView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.constraintlayout.widget.ConstraintLayout;
    import androidx.recyclerview.widget.RecyclerView;

    import com.bumptech.glide.Glide;
    import com.bsstudio.counter.status_saver.Models.Status;
    import com.bsstudio.counter.status_saver.R;
    import com.bsstudio.counter.status_saver.Utils.General;

    import java.util.List;

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyHolder> {


        private final List<Status> imagesList;
        private Context context;
        private final ConstraintLayout container;
        private final SharedPreferences sharedPreferences;



        public ImageAdapter(List<Status> imagesList, ConstraintLayout container, Context context) {
            this.imagesList = imagesList;
            this.container = container;
            this.context = context;

            this.sharedPreferences = this.context.getSharedPreferences("toggle_states", Context.MODE_PRIVATE);
        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            View view = LayoutInflater.from(context).inflate(R.layout.status_item, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {

            final Status status = imagesList.get(position);
            if (status.isApi30()) {
    //            holder.save.setVisibility(View.GONE);
                Glide.with(context).load(status.getDocumentFile().getUri()).into(holder.imageView);
            } else {
    //            holder.save.setVisibility(View.VISIBLE);
                Glide.with(context).load(status.getFile()).into(holder.imageView);
            }
            // Check if the item is downloaded
            boolean isDownloaded = sharedPreferences.getBoolean("downloaded_" + position, false);

            // Set appropriate image resource based on download state
            if (isDownloaded) {
                holder.save.setImageResource(R.drawable.ic_double_tick20);
            } else {
                holder.save.setImageResource(R.drawable.ic_downlod20);
            }

            // Set click listener for the save ImageView
            holder.save.setOnClickListener(v -> {
                if (sharedPreferences.getBoolean("downloaded_" + position, false)) {
                    // Check if the item is deleted
                    if (isItemDeleted(position)) {
                        // If the item is deleted, re-enable download
                        // Remove the downloaded status from SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("downloaded_" + position);
                        editor.apply();

                        // Perform download again
                        General.copyFile(status, context, container);

                        // Save download state to SharedPreferences
                        editor.putBoolean("downloaded_" + position, true);
                        editor.apply();

                        // Update the download button's image to reflect the downloaded state
                        holder.save.setImageResource(R.drawable.ic_double_tick20);
                    } else {
                        Toast.makeText(context, "Already downloaded", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Perform download
                    General.copyFile(status, context, container);

                    // Save download state to SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("downloaded_" + position, true);
                    editor.apply();

                    // Update the download button's image to reflect the downloaded state
                    holder.save.setImageResource(R.drawable.ic_double_tick20);
                }
            });

            holder.share.setOnClickListener(v -> {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);

                shareIntent.setType("image/jpg");

                if (status.isApi30()) {
                    shareIntent.putExtra(Intent.EXTRA_STREAM, status.getDocumentFile().getUri());
                } else {
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + status.getFile().getAbsolutePath()));
                }

                context.startActivity(Intent.createChooser(shareIntent, "Share image"));

            });

            holder.imageView.setOnClickListener(v -> {

                final AlertDialog.Builder alertD = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(R.layout.image_view, null);
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

            });


        }

        @Override
        public int getItemCount() {
            return imagesList.size();
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
            // Get the status object at the specified position
            Status status = imagesList.get(position);

            // Check if the file associated with the status object exists
            if (status.isApi30()) {
                // For Android API 30 and above
                // Check if the DocumentFile associated with the status object exists
                return !status.getDocumentFile().exists();
            } else {
                // For below Android API 30
                // Check if the file associated with the status object exists
                return !status.getFile().exists();
            }
        }

    }
