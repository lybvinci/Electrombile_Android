package com.xunce.electrombile.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.baidu.mapapi.model.LatLng;
import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.Base.sdk.SettingManager;
import com.xunce.electrombile.Base.utils.Historys;
import com.xunce.electrombile.Base.utils.TracksManager;
import com.xunce.electrombile.R;
import com.xunce.electrombile.Updata.UpdateAppService;
import com.xunce.electrombile.fragment.MaptabFragment;
import com.xunce.electrombile.fragment.SettingsFragment;
import com.xunce.electrombile.fragment.SwitchFragment;

import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import com.xunce.electrombile.fragment.SwitchFragment.LocationTVClickedListener;
import com.xunce.electrombile.service.PushService;
import com.xunce.electrombile.xpg.common.device.DeviceUtils;
import com.xunce.electrombile.xpg.common.useful.NetworkUtils;
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;

import java.util.TimeZone;

import io.yunba.android.manager.YunBaManager;


/**
 * Created by heyukun on 2015/3/24.
 */

public class FragmentActivity extends android.support.v4.app.FragmentActivity implements SwitchFragment.GPSDataChangeListener,LocationTVClickedListener {
    private static String TAG = "FragmentActivity:";
    public static boolean ISSTARTED = false;
    //设置菜单条目
    final private int SETTINGS_ITEM1 = 0;
    final private int SETTINGS_ITEM2 = 1;
    final private int SETTINGS_ITEM3 = 2;
    final private int SETTINGS_ITEM4 = 3;

    private static FragmentManager m_FMer;
    private SwitchFragment switchFragment;
    private MaptabFragment maptabFragment;
    private SettingsFragment settingsFragment;
    public static String THE_INSTALLATION_ID;
    private ImageButton btnSettings = null;
    //    public NotificationManager manager;
//    Handler MyHandler;
    RadioButton rbSwitch;
    RadioButton rbMap;
    RadioButton rbSettings;
    boolean isupde;int a=0;
    //退出使用
    private boolean isExit = false;

    protected CmdCenter mCenter;

    //接收广播
    private MyReceiver receiver;
    private SettingManager setManager;
    //推送通知用的
    public static  PushService pushService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        mCenter = CmdCenter.getInstance(this);
        setManager = new SettingManager(this);
        m_FMer = getSupportFragmentManager();

        //检查版本
        checkVersion();
        //初始化通知栏
        //   initNotificaton();
        //初始化界面
        initView();
        //处理按键事件
        dealBottomButtonsClickEvent();
        //注册广播
        registerBroadCast();
        //判断是否需要开启服务
        startServer();
    }

    private void startServer() {
        if(setManager.getIMEI().isEmpty()){
            AVQuery<AVObject> query = new AVQuery<AVObject>("Bindings");
            final AVUser currentUser = AVUser.getCurrentUser();
            query.whereEqualTo("user",currentUser);
            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> avObjects, AVException e) {
                    if(e == null && avObjects.size() > 0){
                        setManager.setIMEI((String) avObjects.get(0).get("IMEI"));
                        Log.i(TAG + "AAAAAA", setManager.getIMEI());
                        final String topic = "e2link_" + setManager.getIMEI();
                        Log.i(TAG+"SSSSSSSSSS", topic);
                        //启动服务
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent("com.xunce.electrombile.service.PushService.MSG_ACTION");
                                bindService(intent, conn, Context.BIND_AUTO_CREATE);
                                //订阅云巴推送
                                YunBaManager.subscribe(getApplicationContext(), topic, new IMqttActionListener() {

                                    @Override
                                    public void onSuccess(IMqttToken arg0) {
                                        Log.d(TAG, "Subscribe topic succeed");
                                    }

                                    @Override
                                    public void onFailure(IMqttToken arg0, Throwable arg1) {
                                        Log.d(TAG, "Subscribe topic failed");
                                    }
                                });
                            }
                        }).start();

                        Log.d("成功", "查询到" + avObjects.size() + " 条符合条件的数据");
                        ToastUtils.showShort(FragmentActivity.this, "设备登陆成功");
                    }else{
                        Log.d("失败", "查询错误2: " + e.getMessage());
                        ToastUtils.showShort(FragmentActivity.this, "请先绑定设备");
                    }
                }
            });

        }else{
            Log.i(TAG, setManager.getIMEI());
            final String topic = "e2link_" + setManager.getIMEI();
            Log.i(TAG+"SSSSSSSSSS", topic);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent("com.xunce.electrombile.service.PushService.MSG_ACTION");
                    bindService(intent, conn, Context.BIND_AUTO_CREATE);
                    YunBaManager.subscribe(getApplicationContext(),topic, new IMqttActionListener() {

                        @Override
                        public void onSuccess(IMqttToken arg0) {
                            Log.d(TAG, "Subscribe topic succeed");
                        }

                        @Override
                        public void onFailure(IMqttToken arg0, Throwable arg1) {
                            if(arg0 != null)
                                Log.i(arg0.toString(),"XXXX");
                            if(arg1 != null)
                                Log.i("AAAA", arg1.toString());
                            Log.d(TAG, "Subscribe topic failed");
                        }
                    });
                }
            }).start();

            ToastUtils.showShort(this,"登陆成功");
        }
    }

    //my service
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //返回一个MsgService实例
            pushService = ((PushService.MsgBinder)service).getService();

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(!NetworkUtils.isNetworkConnected(this)){
            NetworkUtils.networkDialog(this,true);
        }
    }


    private void registerBroadCast() {
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.xunce.electrombile.service");
        try {
            FragmentActivity.this.registerReceiver(receiver, filter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    Handler MyHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            Bundle bundle=msg.getData();
            isupde=bundle.getBoolean("isupdate");
            a=bundle.getInt("want");
            if (isupde) {
                upData();
                isupde=false;
            }
        }
    };
    /**
     * 界面初始化
     */
    private void initView() {
        initFragment();
        rbSwitch = (RadioButton) findViewById(R.id.rbSwitch);
        rbMap = (RadioButton) findViewById(R.id.rbMap);
        rbSettings = (RadioButton)findViewById(R.id.rbSettings);
        //实例化标题栏弹窗
        //titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }


    /**
     * 初始化首个Fragment
     */
    private void initFragment() {
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
    private void dealBottomButtonsClickEvent() {
        findViewById(R.id.rbSwitch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchTabClicked();

            }
        });

        findViewById(R.id.rbMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapTabClicked();

            }
        });

        findViewById(R.id.rbSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsTabClicked();
            }
        });
    }

    private void settingsTabClicked() {
        if(m_FMer.findFragmentByTag("settingsFragment").isVisible()){
            //   Log.e("", "set clicked");
            return;
        }

        FragmentTransaction ft = m_FMer.beginTransaction();
        ft.hide(switchFragment);
        ft.show(settingsFragment);
        ft.hide(maptabFragment);
        ft.commit();
    }

    private void mapTabClicked() {
        if (m_FMer.findFragmentByTag("mapFragment").isVisible()) {
            //  Log.e("", "map clicked");
            return;
        }
        //从backstack中弹出
        //popAllFragmentsExceptTheBottomOne();

        FragmentTransaction ft = m_FMer.beginTransaction();
        ft.hide(switchFragment);
        ft.hide(settingsFragment);
        ft.show(maptabFragment);
        rbMap.setChecked(true);
        //ft.addToBackStack("mapFragment");
        ft.commit();
    }

    private void switchTabClicked() {
        if (m_FMer.findFragmentByTag("switchFragment") != null &&
                m_FMer.findFragmentByTag("switchFragment").isVisible()) {
            return;
        }

        //界面切换
        //从backstack中弹出
        //popAllFragmentsExceptTheBottomOne();
        FragmentTransaction ft = m_FMer.beginTransaction();
        ft.show(switchFragment);
        ft.hide(settingsFragment);
        ft.hide(maptabFragment);
        ft.commit();
    }

    /**
     * 检查更新
     */
    public void upData() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("软件升级")
                .setMessage("发现新版本,建议立即更新使用.")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent updateIntent = new Intent(FragmentActivity.this, UpdateAppService.class);
                        startService(updateIntent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert.create().show();
    }

    /**
     * 从back stack弹出所有的fragment，保留首页的那个
     */
    public static void popAllFragmentsExceptTheBottomOne() {
        for (int i = 0, count = m_FMer.getBackStackEntryCount() - 1; i < count; i++) {
            m_FMer.popBackStack();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    protected void onPause() {
        super.onPause();
        //  Log.i("退出","ooooooooo");
    }

    @Override
    protected void onResume() {
        ISSTARTED = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        ISSTARTED = false;
        //cancelNotification();
        unregisterReceiver(receiver);
        if(!setManager.getIMEI().isEmpty()) {
            pushService.actionStop(this);
            unbindService(conn);
        }
        if(TracksManager.getTracks() !=null) TracksManager.clearTracks();
        super.onDestroy();
    }


    public void checkVersion() {
        //    Log.i("updata version","aaaaaaaaaaaaa");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg=new Message();
                Bundle bundle=new Bundle();
                boolean isupdate;
                String baseUrl = "http://fir.im/api/v2/app/version/%s?token=%s";
                //test String checkUpdateUrl = String.format(baseUrl, "554331e6bf7f222c2600493b", "39d16f30ebf111e4a2da4efe6522248a4b9d9ed4");

                //下面是正式的
                String checkUpdateUrl = String.format(baseUrl, "553ca95096a9fc5c14001802", "39d16f30ebf111e4a2da4efe6522248a4b9d9ed4");
                HttpClient httpClient = new DefaultHttpClient();
                //请求超时
                httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
                //读取超时
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
                try {
                    HttpGet httpGet = new HttpGet(checkUpdateUrl);
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    int statusCode = httpResponse.getStatusLine().getStatusCode();
                    if (statusCode == HttpStatus.SC_OK) {
                        String firResponse = EntityUtils.toString(httpEntity);
                        JSONObject versionJsonObj = new JSONObject(firResponse);
                        //FIR上当前的versionCode
                        int firVersionCode = Integer.parseInt(versionJsonObj.getString("version"));
                        //FIR上当前的versionName
                        String firVersionName = versionJsonObj.getString("versionShort");
                        PackageManager pm = FragmentActivity.this.getPackageManager();
                        PackageInfo pi = pm.getPackageInfo(FragmentActivity.this.getPackageName(),
                                PackageManager.GET_ACTIVITIES);
                        if (pi != null) {
                            int currentVersionCode = pi.versionCode;
                            String currentVersionName = pi.versionName;
//                            Log.i("当前版本",currentVersionCode+"");
//                            Log.i("查看版本",firVersionCode+"");
                            if (firVersionCode > currentVersionCode) {
                                //需要更新
                                //       Log.i("infox", "need update");
                                bundle.putInt("want",1);
                                bundle.putBoolean("isupdate",true);
                            } else if (firVersionCode == currentVersionCode) {
                                //如果本地app的versionCode与FIR上的app的versionCode一致，则需要判断versionName.
                                if (!currentVersionName.equals(firVersionName)) {
                                    //      Log.i("infox", "need update");
                                    bundle.putInt("want",1);
                                    bundle.putBoolean("isupdate",true);
                                }
                            } else {
                                //不需要更新,当前版本高于FIR上的app版本.
                                //     Log.i("infox", " no need update");
                                bundle.putBoolean("isupdate",false);
                            }
                            msg.setData(bundle);
                            FragmentActivity.this.MyHandler.sendMessage(msg);
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }





    @Override
    public void gpsCallBack(LatLng desLat,TracksManager.TrackPoint trackPoint) {
        //传递数据给地图的Fragment
        //如果正在播放轨迹，则更新位置
        //    Log.i("gpsCallBack","called");
        if(!maptabFragment.isPlaying)
            maptabFragment.locateMobile(trackPoint);
        switchFragment.reverserGeoCedec(desLat);
    }

    @Override
    public void locationTVClicked() {
        mapTabClicked();

    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "我的接收调用了？？？？？？");

            Bundle bundle = intent.getExtras();
            boolean cmdOrGPS = bundle.getBoolean("CMDORGPS");
            if (!cmdOrGPS) {
                Log.i(TAG, "GPS？？？？？？");
                float Flat = bundle.getFloat("LAT");
                float Flong = bundle.getFloat("LONG");
                String date = bundle.getString("DATE");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                TracksManager.TrackPoint trackPoint = null;
                try {
                    trackPoint = new TracksManager.TrackPoint(sdf.parse(date), mCenter.convertPoint(new LatLng(Flat, Flong)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //LatLng point = mCenter.convertPoint(new LatLng(Flat, Flong));
                if (!maptabFragment.isPlaying) {
                    maptabFragment.locateMobile(trackPoint);
                }
                switchFragment.reverserGeoCedec(trackPoint.point);
            } else {
                Log.i(TAG, "弹不出来？？？");
                byte[] cmd = bundle.getByteArray("CMD");
                pushService.sendMessage1(mCenter.cTestGPS(new byte[]{0x00,0x12}));
                // pushService.sendMessage1(mCenter.cTestGPS(new byte[]{0x00,0x12}));
                //pushService.sendMessage1(mCenter.cTest(new byte[]{0x00,0x11}));
                if(cmd[3] == 0x01) {
                    DeviceUtils.showNotifation(FragmentActivity.this, "安全宝", "设置成功");
                    ToastUtils.showShort(FragmentActivity.this, "设置成功");
                }else if(cmd[3] == 0x03){
                    String cmdString = new String(cmd);
                    Log.i(TAG,cmdString);
                    if(cmdString.contains("NONE")){
                        setManager.setAlarmFlag(false);
                    }else{
                        setManager.setAlarmFlag(true);
                    }
                }
            }
        }
    }

    //    //取消显示常驻通知栏
//    void cancelNotification(){
////        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        manager.cancel(R.string.app_name);
//    }
    //    public void initNotificaton() {
//        manager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
//    }


    //显示常驻通知栏
//    public void showNotification(String text){
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification notification = new Notification(R.mipmap.ic_launcher,"安全宝",System.currentTimeMillis());
//        //下面这句用来自定义通知栏
//        //notification.contentView = new RemoteViews(getPackageName(),R.layout.notification);
//        Intent intent = new Intent(this,FragmentActivity.class);
//        notification.flags = Notification.FLAG_ONGOING_EVENT;
//        PendingIntent contextIntent = PendingIntent.getActivity(this,0,intent,0);
//        notification.setLatestEventInfo(getApplicationContext(),"安全宝",text,contextIntent);
//        notificationManager.notify(R.string.app_name, notification);
//    }
    //    /**
//     * 重复按下返回键退出app方法
//     */
//    public void exit() {
//        if (!isExit) {
//            isExit = true;
//            Toast.makeText(getApplicationContext(),
//                    "退出程序", Toast.LENGTH_SHORT).show();
//            exitHandler.sendEmptyMessageDelayed(0, 2000);
//        } else {
//
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            this.startActivity(intent);
//            Historys.exit();
//        }
//    }
//
//    /** The handler. to process exit()*/
//    private Handler exitHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            isExit = false;
//        }
//    };

}