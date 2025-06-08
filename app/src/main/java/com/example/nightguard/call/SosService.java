package com.example.nightguard.call;

import android.app.Service;
import android.content.Intent;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.nightguard.R;
import com.example.nightguard.contact.Contact;
import com.example.nightguard.contact.DBHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
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
    private AudioManager mAM;
    private MediaPlayer mMP;
    private int originalVolume;
    private SmsManager mSM;
    private DBHelper mDB;
    private List<Contact> mCL;
    private String mMessage;
    private Boolean flag;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SosService", "调试:开始init");
        init();
        Log.d("SosService", "调试:结束init,开始getMessage");
        getMessage();
        Log.d("SosService", "调试:结束getMessage,开始startFlash");
        startFlash();
        Log.d("SosService", "调试:结束startFlash,开始startMessage");
        startMessage();
        Log.d("SosService", "调试:结束startMessage,开始startSound");
        startSound();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopFlash();
        stopMessage();
        stopSound();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void init() {
        flag = false;
        mClient = LocationServices.getFusedLocationProviderClient(this);
        mAM = (AudioManager) getSystemService(AUDIO_SERVICE);
        mSM = SmsManager.getDefault();
        mDB = new DBHelper(this);
        mCL = mDB.getAllContacts();
        for (Contact contact : mCL) {
            Log.d("SosService", "mCL: " + contact.getPhone());
        }
    }

    private void getMessage() {
        File fileDir = getBaseContext().getFilesDir();
        try {
            File file = new File(fileDir, "message.txt");
            if (!file.exists()) {
                mMessage = "";
                return;
            }
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            mMessage = br.readLine();
            fis.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startFlash() {
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        try {
            cameraId = cameraManager.getCameraIdList()[0];
            flashTimer = new Timer();
            flashTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //如果开发时不希望音量保持最大,将下面一行注释掉
                    mAM.setStreamVolume(AudioManager.STREAM_MUSIC, mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
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

    private void startMessage() {
        Log.d("SosService", "开始发送信息");
        LocationRequest locationRequest = LocationRequest.create()
                .setInterval(5000)
                .setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d("SosService", "开始请求位置信息");

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    Location location = locationResult.getLastLocation();
                    LocationInfo
                            = "\n精度:" + location.getLongitude()
                            + "\n纬度:" + location.getLatitude()
                            + "\n高度:" + location.getAltitude()
                            + "\n速度:" + location.getSpeed();
                    Log.d("SosService", "当前位置: " + LocationInfo);
                    sendMessage();
                }
            }
        };

        try {
            mClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
        } catch (SecurityException e) {
            Log.e("SosService", "定位权限不足", e);
        }
    }

    private void stopMessage() {
        if (mClient != null) {
            mClient.removeLocationUpdates(mLocationCallback);
        }
    }

    private void startSound() {
        originalVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (mMP == null) {
            mMP = MediaPlayer.create(this, R.raw.sos_sound);
            mMP.setLooping(true);
            mMP.start();
        } else {
            Log.e("SosService", "媒体播放器初始化失败!");
        }
    }

    private void stopSound() {
        if (mMP != null) {
            mMP.stop();
            mMP.release();
            mMP = null;
        }
        mAM.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
    }

    private void sendMessage() {
        Log.d("SosService", "开始发送短信");
        try {
            for (Contact contact : mCL) {
                mSM.sendMultipartTextMessage(
                        "+86" + contact.getPhone(),
                        null,
                        mSM.divideMessage(
                                mMessage
                                        + "\n"
                                        + "\n以下是我此刻的定位信息:"
                                        + LocationInfo),
                        null,
                        null);
                Log.d("SosService", "已发送短信给" + contact.getName());
                flag  = true;
            }
            if (flag) {
                Toast.makeText(this, "已发送SOS信息!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("SosService", "发送短信失败", e);
        }
    }

}
