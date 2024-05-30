package com.bsstudio.counter.status_saver.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bsstudio.counter.status_saver.Adpaters.FileSaveAdapter;
import com.bsstudio.counter.status_saver.Models.Status;
import com.bsstudio.counter.status_saver.Utils.General;
import com.bsstudio.counter.status_saver.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavedFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private final List<Status> savedFilesList = new ArrayList<>();
    private final Handler handler = new Handler();
    private FileSaveAdapter filesAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView no_files_found;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(com.bsstudio.counter.status_saver.R.layout.fragment_saved, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFiles);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutFiles);
        progressBar = view.findViewById(R.id.progressBar);
        no_files_found = view.findViewById(R.id.no_files_found);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(requireActivity(), android.R.color.holo_orange_dark),
                ContextCompat.getColor(requireActivity(), android.R.color.holo_green_dark),
                ContextCompat.getColor(requireActivity(), R.color.background_color),
                ContextCompat.getColor(requireActivity(), android.R.color.holo_blue_dark)
        );

        swipeRefreshLayout.setOnRefreshListener(this::getFiles);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), General.GRID_COUNT));

        getFiles();
        return view;
    }

    private void getFiles() {
        final File app_dir = new File(General.APP_DIR);

        if (app_dir.exists() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            no_files_found.setVisibility(View.GONE);

            new Thread(() -> {
                File[] savedFiles;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    File f = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM) + File.separator + "status_saver");
                    savedFiles = f.listFiles();
                } else {
                    savedFiles = app_dir.listFiles();
                }
                savedFilesList.clear();

                if (savedFiles != null && savedFiles.length > 0) {
                    Arrays.sort(savedFiles);
                    for (File file : savedFiles) {
                        Status status = new Status(file, file.getName(), file.getAbsolutePath());
                        savedFilesList.add(status);
                    }

                    handler.post(() -> {
                        filesAdapter = new FileSaveAdapter(savedFilesList);
                        recyclerView.setAdapter(filesAdapter);
                        filesAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    });
                } else {
                    handler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        no_files_found.setVisibility(View.VISIBLE);
                    });
                }
                swipeRefreshLayout.setRefreshing(false);
            }).start();
        } else {
            no_files_found.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
