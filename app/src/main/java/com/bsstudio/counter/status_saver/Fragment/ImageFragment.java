package com.bsstudio.counter.status_saver.Fragment;

import android.content.UriPermission;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bsstudio.counter.status_saver.Adpaters.ImageAdapter;
import com.bsstudio.counter.status_saver.Models.Status;
import com.bsstudio.counter.status_saver.R;
import com.bsstudio.counter.status_saver.Utils.General;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

public class ImageFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private final List<Status> imagesList = new ArrayList<>();
    private ImageAdapter imageAdapter;
    private ConstraintLayout container;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView messageTextView;

    public ImageFragment(Uri uri) {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_image, container, false);



        recyclerView = view.findViewById(R.id.recyclerViewImage);
        progressBar = view.findViewById(R.id.prgressBarImage);
        container = view.findViewById(R.id.image_container);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        messageTextView = view.findViewById(R.id.messageTextImage);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        imageAdapter = new ImageAdapter(imagesList, (ConstraintLayout) container, getContext());


        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_dark),
                ContextCompat.getColor(requireActivity(), android.R.color.holo_green_dark),
                ContextCompat.getColor(requireActivity(), R.color.background_color),
                ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_dark)
        );

        swipeRefreshLayout.setOnRefreshListener(this::getStatus);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), General.GRID_COUNT));
        recyclerView.setAdapter(imageAdapter);

        getStatus();

        return view;

    }

    private void getStatus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            executeNew();
        } else if (General.STATUS_DIRECTORY.exists()) {
        } else {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(R.string.cant_find_whatsapp_dir);
            Toast.makeText(getActivity(), getString(R.string.cant_find_whatsapp_dir), Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }
    }



    private void executeNew() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            List<UriPermission> list = requireActivity().getContentResolver().getPersistedUriPermissions();
            DocumentFile file = DocumentFile.fromTreeUri(requireActivity(), list.get(0).getUri());

            imagesList.clear();

            if (file == null) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                    Toast.makeText(getActivity(), getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
                return;
            }

            DocumentFile[] statusFiles = file.listFiles();

            if (statusFiles.length <= 0) {
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                    Toast.makeText(getActivity(), getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                });
                return;
            }

            for (DocumentFile documentFile : statusFiles) {
                if (Objects.requireNonNull(documentFile.getName()).contains(".nomedia"))
                    continue;

                Status status = new Status(documentFile);

                if (!status.isVideo()) {
                    imagesList.add(status);
                }
            }

            mainHandler.post(() -> {
                if (imagesList.size() <= 0) {
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                } else {
                    messageTextView.setVisibility(View.GONE);
                    messageTextView.setText("");

                    swipeRefreshLayout.setRefreshing(false);
                }

                imageAdapter.notifyItemRangeChanged(0, imagesList.size());
                progressBar.setVisibility(View.GONE);
            });
        });

    }


}
