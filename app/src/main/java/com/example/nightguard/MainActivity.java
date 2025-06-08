package com.example.nightguard;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.nightguard.call.CallMainFragment;
import com.example.nightguard.contact.ContactMainFragment;
import com.example.nightguard.message.MessageMainFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private ViewPager2 mVP;
    private TabLayout mTL;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private FragmentAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.layout_main);
        initTab();
        initFragmentFactory();
        initAdapter();
    }

    private void initTab() {
        TabLayout mTL = findViewById(R.id.TL);
        mTL.addTab(mTL.newTab().setText("紧急信息"));
        mTL.addTab(mTL.newTab().setText("紧急呼叫"));
        mTL.addTab(mTL.newTab().setText("紧急联系人"));
    }

    private String initIntent() {
        Intent intent = getIntent();
        return intent.getStringExtra("pwd");
    }

    private void initFragmentFactory() {
        mFragmentList.add(initFragment("message"));
        mFragmentList.add(initFragment("call"));
        mFragmentList.add(initFragment("contact"));
    }

    private Fragment initFragment(String message) {
        switch (message){
            case "message":
                return new MessageMainFragment();
            case "call":
                Fragment callFragment = new CallMainFragment();
                Bundle bundle = new Bundle();
                bundle.putString("pwd", initIntent());
                callFragment.setArguments(bundle);
                return  callFragment;
            case "contact":
                return new ContactMainFragment();
        }
        return null;
    }

    private void initAdapter() {
        mVP = findViewById(R.id.VP);
        mAdapter = new FragmentAdapter(getSupportFragmentManager(), getLifecycle(), mFragmentList);
        mVP.setAdapter(mAdapter);
        mTL = findViewById(R.id.TL);
        new TabLayoutMediator(mTL, mVP, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("紧急信息");
                        break;
                    case 1:
                        tab.setText("紧急呼叫");
                        break;
                    case 2:
                        tab.setText("紧急联系人");
                        break;
                }
            }
        }).attach();
    }
}
