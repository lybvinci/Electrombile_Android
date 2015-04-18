package com.xunce.electrombile.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.xunce.electrombile.Base.utils.Historys;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.account.LoginActivity;
import com.xunce.electrombile.fragment.MaptabFragment;
import com.xunce.electrombile.fragment.SettingsFragment;
import com.xunce.electrombile.fragment.SwitchFragment;
import com.xunce.electrombile.widget.ActionItem;
import com.xunce.electrombile.widget.TitlePopup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;
import android.widget.Toast;

import com.xunce.electrombile.widget.TitlePopup.OnItemOnClickListener;
/**
 * Created by heyukun on 2015/3/24.
 */
public class FragmentActivity extends android.support.v4.app.FragmentActivity{
    private static String TAG = "FragmentActivity:";
    public static boolean ISSTARTED = false;
    //设置菜单条目
    final private  int SETTINGS_ITEM1 = 0;
    final private  int SETTINGS_ITEM2 = 1;
    final private  int SETTINGS_ITEM3 = 2;
    final private  int SETTINGS_ITEM4 = 3;

    private static FragmentManager m_FMer;
    private SwitchFragment switchFragment;
    private MaptabFragment maptabFragment;
    private SettingsFragment settingsFragment;
    public static String THE_INSTALLATION_ID;
    private ImageButton btnSettings = null;
    private TitlePopup titlePopup;
    RadioButton rbSwitch;
    RadioButton rbMap;

    //退出使用
    private boolean isExit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        m_FMer = getSupportFragmentManager();

        initView();
        initData();

        dealBottomButtonsClickEvent();

        showNotification();
        Historys.put(this);
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

        //设置按钮监听函数
        btnSettings = (ImageButton)findViewById(R.id.title_btn);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titlePopup.show(view);
            }
        });

        titlePopup.setItemOnClickListener(new OnItemOnClickListener() {
            @Override
            public void onItemClick(ActionItem item, int position) {
                switch(position){
                    case SETTINGS_ITEM1:
                        Log.i(TAG, "clicked item 1");
                        break;
                    case SETTINGS_ITEM2:
                        Log.i(TAG, "clicked item 2");
                        Intent intentStartBinding = new Intent(FragmentActivity.this,BindingActivity.class);
                        startActivity(intentStartBinding);
                        break;
                    case SETTINGS_ITEM3:
                        Log.i(TAG, "clicked item 3");
                        break;
                    case SETTINGS_ITEM4:
                        Log.i(TAG, "clicked item 4");
                     //   AVUser.logOut();             //清除缓存用户对象
                        //启动登陆activity
                        Intent intentStartLogin = new Intent(FragmentActivity.this,LoginActivity.class);
                        startActivity(intentStartLogin);
                        //关闭当前activity
                        FragmentActivity.this.finish();
                        break;
                    default:
                        break;
                }
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
        //ft.addToBackStack("switchFragment");

        maptabFragment = new MaptabFragment();
        ft.add(R.id.fragmentRoot, maptabFragment, "mapFragment");
        ft.hide(maptabFragment);
        //ft.addToBackStack("mapFragment");

        settingsFragment = new SettingsFragment();
        ft.add(R.id.fragmentRoot, settingsFragment, "settingsFragment");
        ft.hide(settingsFragment);
        //ft.addToBackStack("settingsFragment");

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

                //界面切换
                rbSwitch.setChecked(true);
                rbMap.setChecked(false);
                rbSwitch.setTextColor(getResources().getColor(R.color.blue));
                rbMap.setTextColor(Color.BLACK);


                //从backstack中弹出
                //popAllFragmentsExceptTheBottomOne();

                FragmentTransaction ft = m_FMer.beginTransaction();
                ft.show(switchFragment);
                ft.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
                //ft.hide(settingsFragment);
                ft.hide(maptabFragment);
                ft.commit();

                //停止更新位置信息
                maptabFragment.pauseMapUpdate();
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

                //从backstack中弹出
                //popAllFragmentsExceptTheBottomOne();

                FragmentTransaction ft = m_FMer.beginTransaction();
                ft.hide(switchFragment);
                //ft.hide(settingsFragment);
                ft.show(maptabFragment);
                //ft.addToBackStack("mapFragment");
                ft.commit();

                //开始更新位置信息
                maptabFragment.resumeMapUpdate();
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
        exit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_set, menu);

        return true;

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        ISSTARTED = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        ISSTARTED = false;
        super.onDestroy();
    }

    //显示常驻通知栏
    void showNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.icon,"安全宝",System.currentTimeMillis());
        //下面这句用来自定义通知栏
        //notification.contentView = new RemoteViews(getPackageName(),R.layout.notification);
        Intent intent = new Intent(this,FragmentActivity.class);
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        PendingIntent contextIntent = PendingIntent.getActivity(this,0,intent,0);
        notification.setLatestEventInfo(getApplicationContext(),"安全宝","正在保护您的电动车",contextIntent);
        notificationManager.notify(R.string.app_name, notification);
    }
    //取消显示常驻通知栏
    void cancelNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.app_name);
    }

    /**
     * 重复按下返回键退出app方法
     */
    public void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(),
                    "退出程序", Toast.LENGTH_SHORT).show();
            exitHandler.sendEmptyMessageDelayed(0, 2000);
        } else {

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
            Historys.exit();
        }
    }

    /** The handler. to process exit()*/
    private Handler exitHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            isExit = false;
        };
    };

}
