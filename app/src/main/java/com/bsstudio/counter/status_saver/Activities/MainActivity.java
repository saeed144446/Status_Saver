package com.bsstudio.counter.status_saver.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import com.bsstudio.counter.status_saver.Fragment.SavedFragment;
import com.bsstudio.counter.status_saver.Fragment.SettingsFragment;
import com.bsstudio.counter.status_saver.Fragment.StatusFragment;
import com.bsstudio.counter.status_saver.Utils.General;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.bsstudio.counter.status_saver.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView Direct_chat, ShareApp;
    private static final int NOTIFICATION_REQUEST_PERMISSIONS = 4;

    private static final int REQUEST_PERMISSIONS = 1234;

    private static final String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @SuppressLint("InlinedApi")
    private static final String[] NOTIFICATION_PERMISSION = {
            android.Manifest.permission.POST_NOTIFICATIONS
    };

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    getContentResolver().takePersistableUriPermission(
                            data.getData(),
                            Intent.FLAG_GRANT_READ_URI_PERMISSION |
                                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    );
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bsstudio.counter.status_saver.R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
       // Direct_chat = findViewById(R.id.direct_chat);
        ShareApp = findViewById(R.id.share_app);



        if(savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().replace(R.id.container,new StatusFragment()).commit();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // Check and request necessary permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(NOTIFICATION_PERMISSION, NOTIFICATION_REQUEST_PERMISSIONS);
        }

        initializeAppDir();

       /* Direct_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Direct_Chat_Activtiy.class);
                startActivity(intent);
            }
        });*/

        ShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String appPackageName =getPackageName();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out the App at: https://play.google.com/store/apps/details?id=" + appPackageName);
                sendIntent.setType("text/plain");
               startActivity(sendIntent);

            }
        });
    }

    private void initializeAppDir() {
        if (General.APP_DIR == null || General.APP_DIR.isEmpty()) {
            General.APP_DIR = getExternalFilesDir("StatusDownloader").getPath();
            Log.d("App Path", General.APP_DIR);
        }
    }

    StatusFragment statusFragment=new StatusFragment();
    SavedFragment savedFragment=new SavedFragment();
    SettingsFragment settingsFragment=new SettingsFragment();

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.status) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, statusFragment)
                    .commit();

            return true;

        } else if (id == R.id.saved) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, savedFragment)
                    .commit();

            return true;
        } else if (id == R.id.setting) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, settingsFragment)
                    .commit();

            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            if (arePermissionsDenied()) {
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            }
        }
    }

    private boolean arePermissionsDenied() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return getContentResolver().getPersistedUriPermissions().size() <= 0;
        }

        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestPermissionQ();
                return;
            }
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestPermissionQ() {
        StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        Intent intent = sm.getPrimaryStorageVolume().createOpenDocumentTreeIntent();
        String startDir = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses";
        Uri uri = intent.getParcelableExtra("android.provider.extra.INITIAL_URI");
        String scheme = uri.toString().replace("/root/", "/document/") + "%3A" + startDir;
        uri = Uri.parse(scheme);

        Log.d("URI", uri.toString());
        intent.putExtra("android.provider.extra.INITIAL_URI", uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        activityResultLauncher.launch(intent);
    }
}
