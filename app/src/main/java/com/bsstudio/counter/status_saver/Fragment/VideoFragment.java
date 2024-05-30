package com.bsstudio.counter.status_saver.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriPermission;
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

import com.bsstudio.counter.status_saver.Adpaters.VideoAdapter;
import com.bsstudio.counter.status_saver.Models.Status;
import com.bsstudio.counter.status_saver.R;
import com.bsstudio.counter.status_saver.Utils.General;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class VideoFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private final List<Status> videoList = new ArrayList<>();
    private VideoAdapter videoAdapter;
    private ConstraintLayout container;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView messageTextView;
    private SharedPreferences sharedPreferences;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_video, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewVideo);
        progressBar = view.findViewById(R.id.prgressBarVideo);
        container = view.findViewById(R.id.videos_container);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        messageTextView = view.findViewById(R.id.messageTextVideo);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());



        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Retrieve the saved state of the switch button


        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_dark)
                , ContextCompat.getColor(requireActivity(), android.R.color.holo_green_dark),
                ContextCompat.getColor(requireActivity(), R.color.background_color),
                ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_dark));

        swipeRefreshLayout.setOnRefreshListener(this::getStatus);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), General.GRID_COUNT));

        getStatus();

        super.onViewCreated(view, savedInstanceState);

        return view;


    }

    private void getStatus() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            executeNew();

        } else if (General.STATUS_DIRECTORY.exists()) {

            executeOld();

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

            videoList.clear();

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
                Status status = new Status(documentFile);

                if (status.isVideo()) {
                    videoList.add(status);
                }
            }

            mainHandler.post(() -> {

                if (videoList.size() <= 0) {
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                } else {
                    messageTextView.setVisibility(View.GONE);
                    messageTextView.setText("");
                }

                videoAdapter = new VideoAdapter(videoList, container,getContext());
                recyclerView.setAdapter(videoAdapter);
                videoAdapter.notifyItemRangeChanged(0, videoList.size());
                progressBar.setVisibility(View.GONE);
            });

            swipeRefreshLayout.setRefreshing(false);

        });
    }

    private void executeOld() {

        Executors.newSingleThreadExecutor().execute(() -> {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            File[] statusFiles = General.STATUS_DIRECTORY.listFiles();
            videoList.clear();

            if (statusFiles != null && statusFiles.length > 0) {

                Arrays.sort(statusFiles);
                for (File file : statusFiles) {
                    Status status = new Status(file, file.getName(), file.getAbsolutePath());

                    if (status.isVideo()) {
                        videoList.add(status);
                    }

                }

                mainHandler.post(() -> {

                    if (videoList.size() <= 0) {
                        messageTextView.setVisibility(View.VISIBLE);
                        messageTextView.setText(R.string.no_files_found);
                    } else {
                        messageTextView.setVisibility(View.GONE);
                        messageTextView.setText("");
                    }

                     videoAdapter = new VideoAdapter(videoList, container, getContext());

                    recyclerView.setAdapter(videoAdapter);
                    videoAdapter.notifyItemRangeChanged(0, videoList.size());
                    progressBar.setVisibility(View.GONE);
                });

            } else {

                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    messageTextView.setVisibility(View.VISIBLE);
                    messageTextView.setText(R.string.no_files_found);
                    Toast.makeText(getActivity(), getString(R.string.no_files_found), Toast.LENGTH_SHORT).show();
                });

            }
            swipeRefreshLayout.setRefreshing(false);

        });

    }


}