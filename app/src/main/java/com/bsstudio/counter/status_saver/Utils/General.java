package com.bsstudio.counter.status_saver.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;
import com.bsstudio.counter.status_saver.Models.Status;
import com.bsstudio.counter.status_saver.R;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class General {

    public static final File STATUS_DIRECTORY = new File(Environment.getExternalStorageDirectory(), "WhatsApp/Media/.Statuses");
    public static final int GRID_COUNT = 2;
    public static String APP_DIR;

    private static final String CHANNEL_NAME = "GAUTHAM";

    public static void copyFile(Status status, Context context, ConstraintLayout container) {
        File file = new File(General.APP_DIR);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Snackbar.make(container, "Something went wrong", Snackbar.LENGTH_SHORT).show();
                return;
            }
        }

        String fileName;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());

        if (status.isVideo()) {
            fileName = "VID_" + currentDateTime + ".mp4";
        } else {
            fileName = "IMG_" + currentDateTime + ".jpg";
        }

        File destFile = new File(file, fileName);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();

                Uri destinationUri;

                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_DCIM + "/status_saver");

                Uri collectionUri;
                if (status.isVideo()) {
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "video/*");
                    collectionUri = MediaStore.Video.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL_PRIMARY);
                } else {
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "image/*");
                    collectionUri = MediaStore.Images.Media.getContentUri(
                            MediaStore.VOLUME_EXTERNAL_PRIMARY);
                }

                destinationUri = context.getContentResolver().insert(collectionUri, values);

                InputStream inputStream = context.getContentResolver().openInputStream(status.getDocumentFile().getUri());
                OutputStream outputStream = context.getContentResolver().openOutputStream(destinationUri);
                IOUtils.copy(inputStream, outputStream);

                showNotification(context, container, status, fileName, destinationUri);

            } else {
                org.apache.commons.io.FileUtils.copyFile(status.getFile(), destFile);
                destFile.setLastModified(System.currentTimeMillis());
                new MediaScanner(context, file);

                Uri data = FileProvider.getUriForFile(context, "a.gautham.statusdownloader.provider",
                        destFile);

                showNotification(context, container, status, fileName, data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void showNotification(Context context, ConstraintLayout container, Status status,
                                         String fileName, Uri data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeNotificationChannel(context);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (status.isVideo()) {
            intent.setDataAndType(data, "video/*");
        } else {
            intent.setDataAndType(data, "image/*");
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notification =
                new NotificationCompat.Builder(context, CHANNEL_NAME);

        notification.setSmallIcon(R.drawable.ic_download)
                .setContentTitle(fileName)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            notification.setContentText("File Saved to " +
                    Environment.DIRECTORY_DCIM + "/status_saver");
        else
            notification.setContentText("File Saved to " + APP_DIR);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(new Random().nextInt(), notification.build());
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)
           // Snackbar.make(container, "Saved to " + APP_DIR, Snackbar.LENGTH_LONG).show();

           Snackbar.make(container, "Saved to " + Environment.DIRECTORY_DCIM + "/status_saver",
                    Snackbar.LENGTH_LONG).show();
    }

    private static void makeNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(General.CHANNEL_NAME, "Saved", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(true);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
