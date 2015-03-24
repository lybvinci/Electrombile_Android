package com.xunce.electrombile.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.xunce.electrombile.R;
import com.xunce.electrombile.fragment.MaptabFragment;
import com.xunce.electrombile.fragment.SettingsFragment;
import com.xunce.electrombile.fragment.SwitchFragment;

/**
 * Created by heyukun on 2015/3/24.
 */
public class FragmentActivity extends android.support.v4.app.FragmentActivity {
    private static FragmentManager m_FMer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_fragment);

        m_FMer = getSupportFragmentManager();
        initFragment();

        dealBottomButtonsClickEvent();
    }

    /**
     * 初始化首个Fragment
     */
    private void initFragment(){
        FragmentTransaction ft = m_FMer.beginTransaction();
        SwitchFragment switchFragment = new SwitchFragment();
        ft.add(R.id.fragmentRoot, switchFragment, "switchFragment");
        ft.addToBackStack("switchFragment");
        ft.commit();

    }

    /**
     * 处理底部点击事件
     */
    private void dealBottomButtonsClickEvent(){
        findViewById(R.id.rbSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_FMer.findFragmentByTag("switchFragment") != null && m_FMer.findFragmentByTag("switchFragment").isVisible()){
                    return;
                }
                popAllFragmentsExceptTheBottomOne();
            }
        });

        findViewById(R.id.rbMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAllFragmentsExceptTheBottomOne();
                FragmentTransaction ft = m_FMer.beginTransaction();
                ft.hide(m_FMer.findFragmentByTag("switchFragment"));
                MaptabFragment mf = new MaptabFragment();
                ft.add(R.id.fragmentRoot, mf, "mapFragment");
                ft.addToBackStack("mapFragment");
                ft.commit();
            }
        });

        findViewById(R.id.rbSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popAllFragmentsExceptTheBottomOne();
                FragmentTransaction ft = m_FMer.beginTransaction();
                ft.hide(m_FMer.findFragmentByTag("switchFragment"));
                SettingsFragment mf = new SettingsFragment();
                ft.add(R.id.fragmentRoot, mf, "settingsFragment");
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
        if(m_FMer.findFragmentByTag("weiXinFragment")!=null && m_FMer.findFragmentByTag("weiXinFragment").isVisible()) {
            FragmentActivity.this.finish();
        } else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
