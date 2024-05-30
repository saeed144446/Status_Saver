package com.bsstudio.counter.status_saver.Utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

public class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

    private final MediaScannerConnection mMs;
    private final File mFile;

    public MediaScanner(Context context, File f) {
        mFile = f;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }
    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(mFile.getAbsolutePath(), null);

    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        mMs.disconnect();
    }
}
