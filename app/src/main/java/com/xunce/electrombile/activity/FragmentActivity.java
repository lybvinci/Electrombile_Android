package com.xunce.electrombile.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.xunce.electrombile.R;
import com.xunce.electrombile.fragment.MaptabFragment;
import com.xunce.electrombile.fragment.SettingsFragment;
import com.xunce.electrombile.fragment.SwitchFragment;

/**
 * Created by heyukun on 2015/3/24.
 */
public class FragmentActivity extends android.support.v4.app.FragmentActivity {
    private static String TAG = "FragmentActivity:";
    private static FragmentManager m_FMer;
    private SwitchFragment switchFragment;
    private MaptabFragment maptabFragment;
    private SettingsFragment settingsFragment;
    public static String THE_INSTALLATION_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        m_FMer = getSupportFragmentManager();
        initFragment();

        dealBottomButtonsClickEvent();

        initLeanCloud();
    }

    /**
     * leancloue注册
     */
    private void initLeanCloud(){
        PushService.setDefaultPushCallback(this, FragmentActivity.class);
        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
        //PushService.subscribe(this, "publicheyukun", FragmentActivity.class);
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            public void done(AVException e) {
                if (e == null) {
                    // 保存成功
                    THE_INSTALLATION_ID = AVInstallation.getCurrentInstallation().getInstallationId();
                    Log.i(TAG, "installationId:" + THE_INSTALLATION_ID);
                    // 关联  installationId 到用户表等操作……
                } else {
                    // 保存失败，输出错误信息
                }
            }
        });
    }
    /**
     * 初始化首个Fragment
     */
    private void initFragment(){
        FragmentTransaction ft = m_FMer.beginTransaction();
        switchFragment = new SwitchFragment();
        ft.add(R.id.fragmentRoot, switchFragment, "switchFragment");
        ft.addToBackStack("switchFragment");

        maptabFragment = new MaptabFragment();
        ft.add(R.id.fragmentRoot, maptabFragment, "mapFragment");
        ft.hide(maptabFragment);
        ft.addToBackStack("mapFragment");

        settingsFragment = new SettingsFragment();
        ft.add(R.id.fragmentRoot, settingsFragment, "settingsFragment");
        ft.hide(settingsFragment);
        ft.addToBackStack("settingsFragment");

        ft.commit();

    }

    /**
     * 处理底部点击事件
     */
    private void dealBottomButtonsClickEvent(){
        findViewById(R.id.rbSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_FMer.findFragmentByTag("switchFragment") != null &&
                        m_FMer.findFragmentByTag("switchFragment").isVisible()){
                    return;
                }
                FragmentTransaction ft = m_FMer.beginTransaction();
                ft.show(switchFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
                ft.hide(settingsFragment);
                ft.hide(maptabFragment);
                ft.commit();
                Log.i("Fragment size:", m_FMer.getBackStackEntryCount() + "");
                popAllFragmentsExceptTheBottomOne();
            }
        });

        findViewById(R.id.rbMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_FMer.findFragmentByTag("mapFragment").isVisible()){
                    Log.e("", "map clicked");
                    return;
                }
                Log.i("Fragment size:", m_FMer.getBackStackEntryCount() + "");
                popAllFragmentsExceptTheBottomOne();
                Log.i("after pop ,size:", m_FMer.getBackStackEntryCount() + "");
                FragmentTransaction ft = m_FMer.beginTransaction();
                ft.hide(switchFragment);
                ft.hide(settingsFragment);
                ft.show(maptabFragment);
                ft.addToBackStack("mapFragment");
                ft.commit();
            }
        });

        findViewById(R.id.rbSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_FMer.findFragmentByTag("settingsFragment").isVisible()){
                    Log.e("", "set clicked");
                    return;
                }
                popAllFragmentsExceptTheBottomOne();
                FragmentTransaction ft = m_FMer.beginTransaction();
                ft.hide(switchFragment);
                ft.show(settingsFragment);
                ft.hide(maptabFragment);
                ft.addToBackStack("settingsFragment");
                ft.commit();
            }
        });
    }

    /**
     * 从back stack弹出所有的fragment，保留首页的那个
     */
    public static void popAllFragmentsExceptTheBottomOne() {
        for (int i = 0, count = m_FMer.getBackStackEntryCount() - 1; i < count; i++) {
            m_FMer.popBackStack();
        }
    }

    /**
     * 处理返回按钮
     */
    @Override
    public void onBackPressed() {
        if(m_FMer.findFragmentByTag("switchFragment")!=null && m_FMer.findFragmentByTag("switchFragment").isVisible()) {
            FragmentActivity.this.finish();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onPause() {
        if(MaptabFragment.mMapView != null)
            MaptabFragment.mMapView.setVisibility(View.INVISIBLE);
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(MaptabFragment.mMapView != null)
            MaptabFragment.mMapView.setVisibility(View.VISIBLE);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
