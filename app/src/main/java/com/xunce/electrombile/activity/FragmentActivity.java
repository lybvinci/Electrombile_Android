package com.xunce.electrombile.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.xunce.electrombile.R;
import com.xunce.electrombile.fragment.MaptabFragment;
import com.xunce.electrombile.fragment.SettingsFragment;
import com.xunce.electrombile.fragment.SwitchFragment;
import com.xunce.electrombile.widget.ActionItem;
import com.xunce.electrombile.widget.TitlePopup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;

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
    private ImageButton btnSettings = null;
    private TitlePopup titlePopup;
    RadioButton rbSwitch;
    RadioButton rbMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        m_FMer = getSupportFragmentManager();

        initView();
        initData();

        dealBottomButtonsClickEvent();

        initLeanCloud();
    }

    /**
     * leancloue注册
     */
    private void initLeanCloud(){
        PushService.setDefaultPushCallback(this, FragmentActivity.class);
        // 订阅频道，当该频道消息到来的时候，打开对应的 Activity
        PushService.subscribe(this, "publicheyukun", FragmentActivity.class);
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
     * 界面初始化
     */
    private void initView(){
        initFragment();
        rbSwitch = (RadioButton)findViewById(R.id.rbSwitch);
        rbMap = (RadioButton)findViewById(R.id.rbMap);

        //实例化标题栏弹窗
        titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        btnSettings = (ImageButton)findViewById(R.id.title_btn);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titlePopup.show(view);
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData(){
        //给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(this, "权限设置"));
        titlePopup.addAction(new ActionItem(this, "绑定设备"));
        titlePopup.addAction(new ActionItem(this, "使用帮助"));
        titlePopup.addAction(new ActionItem(this, "退出登录"));
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

                rbSwitch.setChecked(true);
                rbMap.setChecked(false);
                rbSwitch.setTextColor(getResources().getColor(R.color.blue));
                rbMap.setTextColor(Color.BLACK);
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
                rbMap.setChecked(true);
                rbMap.setTextColor(getResources().getColor(R.color.blue));
                rbSwitch.setChecked(false);
                rbSwitch.setTextColor(Color.BLACK);
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

//        findViewById(R.id.rbSettings).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(m_FMer.findFragmentByTag("settingsFragment").isVisible()){
//                    Log.e("", "set clicked");
//                    return;
//                }
//                popAllFragmentsExceptTheBottomOne();
//                FragmentTransaction ft = m_FMer.beginTransaction();
//                ft.hide(switchFragment);
//                ft.show(settingsFragment);
//                ft.hide(maptabFragment);
//                ft.addToBackStack("settingsFragment");
//                ft.commit();
//            }
//        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_set, menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.e("", "fadf");
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.e("", "fadf");
            return true;
        }

        return super.onOptionsItemSelected(item);
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
