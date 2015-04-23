package com.xunce.electrombile.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiDeviceListener;
import com.xtremeprog.xpgconnect.XPGWifiSDKListener;
import com.xtremeprog.xpgconnect.XPGWifiSSID;
import com.xunce.electrombile.Base.sdk.CmdCenter;
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
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by heyukun on 2015/3/24.
 */
public class FragmentActivity extends android.support.v4.app.FragmentActivity implements SwitchFragment.GPSDataChangeListener{
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

    //SDK 机制云相关
    private CmdCenter mCenter;
    private XPGWifiDevice mXpgWifiDevice;
    private ConcurrentHashMap<String, Object> deviceDataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        //mCenter = CmdCenter.getInstance(this.getApplicationContext());
        mCenter = CmdCenter.getInstance(getApplicationContext());
        // 每次返回activity都要注册一次sdk监听器，保证sdk状态能正确回调
        mCenter.getXPGWifiSDK().setListener(sdkListener);
        // 每次返回activity都要注册一次sdk监听器，保证sdk状态能正确回调
        mXpgWifiDevice = BaseActivity.mXpgWifiDevice;
        if(mXpgWifiDevice != null)
            mXpgWifiDevice.setListener(deviceListener);

        m_FMer = getSupportFragmentManager();

        initView();
        initData();

        dealBottomButtonsClickEvent();

        showNotification();
        Historys.put(this);
    }

    /**
     * XPGWifiSDKListener
     * <p/>
     * sdk监听器。 配置设备上线、注册登录用户、搜索发现设备、用户绑定和解绑设备相关.
     */
    private XPGWifiSDKListener sdkListener = new XPGWifiSDKListener() {

        @Override
        public void didBindDevice(int error, String errorMessage, String did) {
            FragmentActivity.this.didBindDevice(error, errorMessage, did);
        }

        @Override
        public void didChangeUserEmail(int error, String errorMessage) {
            FragmentActivity.this.didChangeUserEmail(error, errorMessage);
        }

        @Override
        public void didChangeUserPassword(int error, String errorMessage) {
            FragmentActivity.this.didChangeUserPassword(error, errorMessage);
        }

        @Override
        public void didChangeUserPhone(int error, String errorMessage) {
            FragmentActivity.this.didChangeUserPhone(error, errorMessage);
        }

        @Override
        public void didDiscovered(int error, List<XPGWifiDevice> devicesList) {

            FragmentActivity.this.didDiscovered(error, devicesList);
        }

        @Override
        public void didGetSSIDList(int error, List<XPGWifiSSID> ssidInfoList) {
            FragmentActivity.this.didGetSSIDList(error, ssidInfoList);
        }

        @Override
        public void didRegisterUser(int error, String errorMessage, String uid,
                                    String token) {
            FragmentActivity.this.didRegisterUser(error, errorMessage, uid, token);
        }

        @Override
        public void didRequestSendVerifyCode(int error, String errorMessage) {
            FragmentActivity.this.didRequestSendVerifyCode(error, errorMessage);
        }

        @Override
        public void didSetDeviceWifi(int error, XPGWifiDevice device) {
            FragmentActivity.this.didSetDeviceWifi(error, device);
        }

        @Override
        public void didUnbindDevice(int error, String errorMessage, String did) {
            FragmentActivity.this.didUnbindDevice(error, errorMessage, did);
        }

        @Override
        public void didUserLogin(int error, String errorMessage, String uid,
                                 String token) {
            FragmentActivity.this.didUserLogin(error, errorMessage, uid, token);
        }

        @Override
        public void didUserLogout(int error, String errorMessage) {
            FragmentActivity.this.didUserLogout(error, errorMessage);
        }

    };
    /**
     * 用户登出回调借口.
     *
     * @param error
     *            结果代码
     * @param errorMessage
     *            错误信息
     */
    protected void didUserLogout(int error, String errorMessage) {

    }

    /**
     * 用户登陆回调接口.
     *
     * @param error
     *            结果代码
     * @param errorMessage
     *            错误信息
     * @param uid
     *            用户id
     * @param token
     *            授权令牌
     */
    protected void didUserLogin(int error, String errorMessage, String uid,
                                String token) {

    }

    /**
     * 设备解除绑定回调接口.
     *
     * @param error
     *            结果代码
     * @param errorMessage
     *            错误信息
     * @param did
     *            设备注册id
     */
    protected void didUnbindDevice(int error, String errorMessage, String did) {

    }

    /**
     * 设备配置结果回调.
     *
     * @param error
     *            结果代码
     * @param device
     *            设备对象
     */
    protected void didSetDeviceWifi(int error, XPGWifiDevice device) {

    }

    /**
     * 请求手机验证码回调接口.
     *
     * @param error
     *            结果代码
     * @param errorMessage
     *            错误信息
     */
    protected void didRequestSendVerifyCode(int error, String errorMessage) {

    }

    /**
     * 注册用户结果回调接口.
     *
     * @param error
     *            结果代码
     * @param errorMessage
     *            错误信息
     * @param uid
     *            the 用户id
     * @param token
     *            the 授权令牌
     */
    protected void didRegisterUser(int error, String errorMessage, String uid,
                                   String token) {
        // TODO Auto-generated method stub

    }

    /**
     * 获取ssid列表回调接口.
     *
     * @param error
     *            结果代码
     * @param ssidInfoList
     *            ssid列表
     */
    protected void didGetSSIDList(int error, List<XPGWifiSSID> ssidInfoList) {

    }

    /**
     * 搜索设备回调接口.
     *
     * @param error
     *            结果代码
     * @param devicesList
     *            设备列表
     */
    protected void didDiscovered(int error, List<XPGWifiDevice> devicesList) {

    }

    /**
     * 更换注册手机号码回调接口.
     *
     * @param error
     *            结果代码
     * @param errorMessage
     *            错误信息
     */
    protected void didChangeUserPhone(int error, String errorMessage) {

    }

    /**
     * 更换密码回调接口.
     *
     * @param error
     *            结果代码
     * @param errorMessage
     *            错误信息
     */
    protected void didChangeUserPassword(int error, String errorMessage) {

    }

    /**
     * 更换注册邮箱.
     *
     * @param error
     *            结果代码
     * @param errorMessage
     *            错误信息
     */
    protected void didChangeUserEmail(int error, String errorMessage) {

    }

    /**
     * 绑定设备结果回调.
     *
     * @param error
     *            结果代码
     * @param errorMessage
     *            错误信息
     * @param did
     *            设备注册id
     */
    protected void didBindDevice(int error, String errorMessage, String did) {

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
        if(mXpgWifiDevice != null)
            mXpgWifiDevice.setListener(deviceListener);
        mCenter.getXPGWifiSDK().setListener(sdkListener);
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
        }
    };



    //handler 处理事件
    private enum handler_key {

        /** 更新UI界面 */
        UPDATE_UI,

        /** 显示警告*/
        ALARM,

        /** 设备断开连接 */
        DISCONNECTED,

        /** 接收到设备的数据 */
        RECEIVED,

        /** 获取设备状态 */
        GET_STATUE,
    }

    private HashMap<String, String> GPS_Data;
    private Handler fragmentHandler = new Handler(){
        public void handMessage(Message msg){
            super.handleMessage(msg);
            handler_key key= handler_key.values()[msg.what];
            switch (key){
                case RECEIVED:
                    if (deviceDataMap.get("data") != null) {
                        Log.i("info", (String) deviceDataMap.get("data"));
                    }
                    if (deviceDataMap.get("alters") != null) {
                        Log.i("info", (String) deviceDataMap.get("alters"));
                        // 返回主线程处理报警数据刷新
                    }
                    if (deviceDataMap.get("faults") != null) {
                        Log.i("info", (String) deviceDataMap.get("faults"));
                        // 返回主线程处理错误数据刷新
                    }
                    if(deviceDataMap.get("binary") != null){
                        byte[] binary = (byte[]) deviceDataMap.get("binary");
                        Log.i("info:::::", binary.toString());
                        String ChuanTouData = mCenter.cParseString(binary);
                        if(ChuanTouData.equals("SET_TIMER_OK")){
                            ToastUtils.showShort(FragmentActivity.this, "GPS定时发送设置成功");
                        }
                        else if(ChuanTouData.equals("SET_SOS_OK")){
                            ToastUtils.showShort(FragmentActivity.this,"管理员设置成功");
                        }
                        else if(ChuanTouData.equals("DEL_SOS_OK")){
                            ToastUtils.showShort(FragmentActivity.this,"删除管理员成功");
                        }
                        else if(ChuanTouData.equals("SET_SAVING_OK")){
                            ToastUtils.showShort(FragmentActivity.this,"模式设置成功");
                        }
                        else if(ChuanTouData.equals("RESET_OK")){
                            ToastUtils.showShort(FragmentActivity.this,"重启设备成功");
                        }
                        else{
                            GPS_Data = new HashMap<String, String>();
                            GPS_Data = mCenter.parseGps(ChuanTouData);
                        }

                    }
                    break;
                case GET_STATUE:
                    break;
                case UPDATE_UI:
                    break;
                case ALARM:
                    break;
                case DISCONNECTED:
                    break;
            }
        }
    };
    /**
     * XPGWifiDeviceListener
     * <p/>
     * 设备属性监听器。 设备连接断开、获取绑定参数、获取设备信息、控制和接受设备信息相关.
     */
    protected XPGWifiDeviceListener deviceListener = new XPGWifiDeviceListener() {

        @Override
        public void didDeviceOnline(XPGWifiDevice device, boolean isOnline) {
            FragmentActivity.this.didDeviceOnline(device, isOnline);
        }

        @Override
        public void didDisconnected(XPGWifiDevice device) {
            FragmentActivity.this.didDisconnected(device);
        }

        @Override
        public void didReceiveData(XPGWifiDevice device,
                                   ConcurrentHashMap<String, Object> dataMap, int result) {
            FragmentActivity.this.didReceiveData(device, dataMap, result);
        }

    };
    /**
     * 接收指令回调
     * <p/>
     * sdk接收到模块传入的数据回调该接口.
     *
     * @param device
     *            设备对象
     * @param dataMap
     *            json数据表
     * @param result
     *            状态代码
     */
    protected void didReceiveData(XPGWifiDevice device,
                                  ConcurrentHashMap<String, Object> dataMap, int result) {
        this.deviceDataMap = dataMap;
        Log.i("FragmentActivity","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        Log.i("device:::",device.toString());
        Log.i("dataMap:::",dataMap.toString());
        Log.i("result",result+"");
        fragmentHandler.sendEmptyMessage(handler_key.RECEIVED.ordinal());
    }
    /**
     * 设备上下线通知.
     *
     * @param device
     *            设备对象
     * @param isOnline
     *            上下线状态
     */
    protected void didDeviceOnline(XPGWifiDevice device, boolean isOnline) {
        Log.i("设备在线",isOnline + "");
    }
    /**
     * 断开连接回调接口.
     *
     * @param device
     *            设备对象
     */
    protected void didDisconnected(XPGWifiDevice device) {
        Log.i("设备未连接",device + "");
    }

    @Override
    public void gpsCallBack(String lat, String lon) {
        //传递数据给地图的Fragment
    }
}
