package com.johngoodstadt.freelancermanageimages.utilsadded;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static android.Manifest.permission.WAKE_LOCK;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class Permissions extends Activity {

    public static final int RequestPermissionCode = 1;

    private Context context;
    // String permissionarray = new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    public Permissions(Context context) {
        this.context = context;
    }
    //////////////start permisson
    public void requestPermission() {

/*         <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.STORAGE" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />*/

        ActivityCompat.requestPermissions((Activity) context, new String[]
                {

                        CAMERA,
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE,
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION,
                        WAKE_LOCK,
                        SYSTEM_ALERT_WINDOW,
                        RECORD_AUDIO
                }, RequestPermissionCode);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean CameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean WriteExternalStorage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean AccessFinelocation = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean AccessCoarseLocation = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean WakeLock = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean SystemAlertWindow = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordAudio = grantResults[7] == PackageManager.PERMISSION_GRANTED;

                    if (CameraPermission && ReadExternalStorage && WriteExternalStorage && AccessFinelocation && AccessCoarseLocation && WakeLock && SystemAlertWindow && RecordAudio) {

                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }



}
