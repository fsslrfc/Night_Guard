package com.example.nightguard.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;

public class SosService extends Service {
    private CameraManager cameraManager;
    private String cameraId;
    private Timer flashTimer;
    private boolean isTorchOn = false;
    private String LocationInfo;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mClient;

    @Override
    public void onCreate() {
        super.onCreate();
        initLocation();
        startFlash();
        startLocation();
        startSound();
        startMessage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFlash();
        stopLocation();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initLocation() {
        mClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void startFlash() {
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

    private void startLocation() {
        //TODO
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    LocationInfo = "精度:" + location.getLongitude()
                            + "\n纬度:" + location.getLatitude()
                            + "\n高度:" + location.getAltitude()
                            + "\n速度:" + location.getSpeed();
                    Log.d("SosService", "当前位置: " + LocationInfo);
                }
            }
        };

        try {
            mClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        } catch (SecurityException e) {
            Log.e("SosService", "定位权限不足", e);
        }
    }

    private void stopFlash() {
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

    private void stopLocation() {
        if (mClient != null) {
            mClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void startSound() {
        //TODO
    }

    private void startMessage() {
        //TODO


    }

}
