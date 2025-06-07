package com.example.nightguard;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StartActivity extends Activity {
    private String mPWD;
    private EditText mET;
    private Button mBT;

    private final int CODE_LENGTH = 6;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            setContentView(R.layout.layout_start);
            initView();
            initListener();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setContentView(R.layout.layout_start);
                initView();
                initListener();
            } else {
                Toast.makeText(this, "请授予定位权限后重新打开!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else if (requestCode == 2) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, "请授予发送短信权限!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void initView() {
        mET = findViewById(R.id.et_pwd);
        mBT = findViewById(R.id.bt_start);
    }

    private void initListener() {
        mBT.setOnClickListener(v -> {
            mPWD = mET.getText().toString();
            if (mPWD.length() != CODE_LENGTH) {
                Toast.makeText(this, "重新输入!密码长度为6位!", Toast.LENGTH_LONG).show();
                mET.setText("");
            } else {
                Toast.makeText(this, "密码已保存!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("pwd", mPWD);
                startActivity(intent);
            }
        });
    }

}