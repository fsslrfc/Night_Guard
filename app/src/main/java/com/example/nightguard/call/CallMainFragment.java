package com.example.nightguard.call;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nightguard.R;

public class CallMainFragment extends Fragment {
    private FrameLayout mFL_sos1;
    private FrameLayout mFL_sos2;
    private EditText mEt_pwd;
    private Button mBt_confirm;
    private Button mBt_test;
    private Button mBt_sos;
    private String PWD;
    private Boolean flag  = false;

    public CallMainFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);
        initPWD();
        initView(view);
        initListener();
        return view;
    }

    private void initPWD() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            PWD = bundle.getString("pwd");
        }
    }

    private void initView(View view) {
        mFL_sos1 = view.findViewById(R.id.FL_sos1);
        mFL_sos2 = view.findViewById(R.id.FL_sos2);
        mEt_pwd = view.findViewById(R.id.et_pwd);
        mBt_confirm = view.findViewById(R.id.bt_confirm);
        mBt_test = view.findViewById(R.id.btn_test);
        mBt_sos = view.findViewById(R.id.btn_sos);
        mFL_sos1.setVisibility(View.VISIBLE);
        mFL_sos2.setVisibility(View.GONE);
    }

    private void initListener() {
        mBt_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartSos();
                mFL_sos1.setVisibility(View.GONE);
                mFL_sos2.setVisibility(View.VISIBLE);
            }
        });
        mBt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PWD.equals(mEt_pwd.getText().toString())) {
                    StopSos();
                    Toast.makeText(getContext(), "呼叫已结束!", Toast.LENGTH_SHORT).show();
                    mFL_sos1.setVisibility(View.VISIBLE);
                    mFL_sos2.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getContext(), "密码错误!", Toast.LENGTH_SHORT).show();
                }
                mEt_pwd.setText("");
                View view = getView();
                if(view != null&&view.getWindowToken()!=null){
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if(imm != null){
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }


        });
        mBt_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag) {
                    Toast.makeText(getActivity(), "正在测试中,若无结果提示请耐心等待或重试...", Toast.LENGTH_SHORT).show();
                    StartSos();
                    flag = true;
                }
                else{
                    StopSos();
                    flag = false;
                }
            }
        });
    }

    private void StartSos() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.startService(new Intent(getActivity(), SosService.class));
        }
    }

    private void StopSos() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.stopService(new Intent(getActivity(), SosService.class));
        }
    }
}
