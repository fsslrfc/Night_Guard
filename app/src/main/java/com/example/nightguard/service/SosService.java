package com.example.nightguard.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;

import java.util.Timer;
import java.util.TimerTask;

public class SosService extends Service {
    private CameraManager cameraManager;
    private String cameraId;
    private Timer flashTimer;
    private boolean isTorchOn = false;
    private LocationManager mLM;
    private String LocationInfo;
    private LocationListener mLoListener;
    private FusedLocationProviderClient mClient;

    @Override
    public void onCreate() {
        super.onCreate();

        initLocation();
        startSos();

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            cameraId = cameraManager.getCameraIdList()[0];
            flashTimer = new Timer();
            flashTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        isTorchOn = !isTorchOn;
                        cameraManager.setTorchMode(cameraId, isTorchOn);
                    } catch (CameraAccessException | SecurityException e) {
                        Log.e("SosService", "闪光灯控制失败", e);
                        if (flashTimer != null) {
                            flashTimer.cancel();
                            flashTimer = null;
                        }
                    }
                }
            }, 0, 500);
        } catch (CameraAccessException e) {
            Log.e("SosService", "相机访问异常", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (flashTimer != null) {
            flashTimer.cancel();
            flashTimer = null;
        }

        if (cameraManager != null && cameraId != null) {
            try {
                cameraManager.setTorchMode(cameraId, false);
                isTorchOn = false;
            } catch (CameraAccessException e) {
                Log.e("SosService", "无法关闭闪光灯", e);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initLocation() {
        mLM = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!mLM.isProviderEnabled(LocationManager.GPS_PROVIDER) && !mLM.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SosService.this.startActivity(intent);
            Toast.makeText(SosService.this, "请开启定位权限!", Toast.LENGTH_LONG).show();
        } else {
             mLoListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    LocationInfo = "精度:" + location.getLongitude()
                            + "\n纬度:" + location.getLatitude()
                            + "\n高度:" + location.getAltitude()
                            + "\n速度:" + location.getSpeed();
                }
            };


        }

    }

    private void startSos() {


    }

}
