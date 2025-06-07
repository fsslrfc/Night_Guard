package com.example.nightguard.message;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nightguard.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MessageMainFragment extends Fragment {

    private EditText mET;
    private Button mBtn;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        mET = view.findViewById(R.id.et_emergency_info);
        mBtn = view.findViewById(R.id.btn_save_info);

        setMessage();
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessage();
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });

        return view;
    }

    private void setMessage() {
        mActivity = getActivity();
        if (mActivity == null) {
            mET.setText("");
            return;
        }
        File fileDir = mActivity.getFilesDir();
        try {
            File file = new File(fileDir, "message.txt");
            if (!file.exists()) {
                mET.setText("");
                return;
            }
            FileInputStream fis = new FileInputStream(file);
            BufferedReader  br = new BufferedReader(new InputStreamReader(fis));
            mET.setText(br.readLine());
            fis.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "紧急信息已重置!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMessage() {
        String info = mET.getText().toString().trim();
        mActivity = getActivity();
        if (mActivity == null) {
            mET.setText("");
            return;
        }
        File fileDir = mActivity.getFilesDir();
        try {
            File file = new File(fileDir, "message.txt");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write((info + "\n").getBytes());
            Toast.makeText(getActivity(), "信息已保存", Toast.LENGTH_SHORT).show();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "保存信息失败", Toast.LENGTH_SHORT).show();
        }
    }

}