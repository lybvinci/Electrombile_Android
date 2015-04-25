package com.xunce.electrombile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.Base.utils.Historys;
import com.xunce.electrombile.R;
import com.xunce.electrombile.Updata.UpdateAppService;
import com.xunce.electrombile.fragment.MaptabFragment;
import com.xunce.electrombile.fragment.SettingsFragment;
import com.xunce.electrombile.fragment.SwitchFragment;
import com.xunce.electrombile.service.GPSDataService;
import com.xunce.electrombile.widget.ActionItem;
import com.xunce.electrombile.widget.TitlePopup;
import android.view.ViewGroup.LayoutParams;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


/**
 * Created by heyukun on 2015/3/24.
 */

public class FragmentActivity extends android.support.v4.app.FragmentActivity implements SwitchFragment.GPSDataChangeListener{
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
    private TitlePopup titlePopup;
    public static NotificationManager manager;
    Handler MyHandler;
    RadioButton rbSwitch;
    RadioButton rbMap;
    RadioButton rbSettings;
    boolean isupde;int a=0;
    //退出使用
    private boolean isExit = false;

    protected CmdCenter mCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        mCenter = CmdCenter.getInstance(this);
        m_FMer = getSupportFragmentManager();
        MyHandler=new Handler()
        {
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                Bundle bundle=msg.getData();
                isupde=bundle.getBoolean("isupdate");
                a=bundle.getInt("want");
                if (isupde) {
                    updata();isupde=false;
                }
            }
        };
        Checkversion();
        initNotificaton();
        initView();
        initData();

        dealBottomButtonsClickEvent();


        //showNotification();
        Historys.put(this);
    }
    /**
     * 界面初始化
     */
    private void initView() {
        initFragment();
        rbSwitch = (RadioButton) findViewById(R.id.rbSwitch);
        rbMap = (RadioButton) findViewById(R.id.rbMap);
        rbSettings = (RadioButton)findViewById(R.id.rbSettings);
        //实例化标题栏弹窗
        titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

//        //设置按钮监听函数
//        btnSettings = (ImageButton) findViewById(R.id.title_btn);
//        btnSettings.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                titlePopup.show(view);
//            }
//        });
//
//        titlePopup.setItemOnClickListener(new OnItemOnClickListener() {
//            @Override
//            public void onItemClick(ActionItem item, int position) {
//                switch (position) {
//                    case SETTINGS_ITEM1:
//                        Log.i(TAG, "clicked item 1");
//                        break;
//                    case SETTINGS_ITEM2:
//                        Log.i(TAG, "clicked item 2");
//                        Intent intentStartBinding = new Intent(FragmentActivity.this, BindingActivity.class);
//                        startActivity(intentStartBinding);
//                        break;
//                    case SETTINGS_ITEM3:
//                        Log.i(TAG, "clicked item 3");
//                        break;
//                    case SETTINGS_ITEM4:
//                        Log.i(TAG, "clicked item 4");
//                     //   AVUser.logOut();             //清除缓存用户对象
//                        //启动登陆activity
//                        Intent intentStartLogin = new Intent(FragmentActivity.this, LoginActivity.class);
//                        startActivity(intentStartLogin);
//                        //关闭当前activity
//                        FragmentActivity.this.finish();
//                        break;
//                    default:
//                        break;
//                }
//            }
//        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //给标题栏弹窗添加子类
        titlePopup.addAction(new ActionItem(this, "权限设置"));
        titlePopup.addAction(new ActionItem(this, "绑定设备"));
        titlePopup.addAction(new ActionItem(this, "使用帮助"));
        titlePopup.addAction(new ActionItem(this, "退出登录"));
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
                if (m_FMer.findFragmentByTag("switchFragment") != null &&
                        m_FMer.findFragmentByTag("switchFragment").isVisible()) {
                    return;
                }

                //界面切换
                rbSwitch.setChecked(true);
                rbSwitch.setTextColor(getResources().getColor(R.color.blue));
                rbMap.setChecked(false);
                rbMap.setTextColor(Color.BLACK);
                rbSettings.setChecked(false);
                rbSettings.setTextColor(Color.BLACK);


                //从backstack中弹出
                //popAllFragmentsExceptTheBottomOne();

                FragmentTransaction ft = m_FMer.beginTransaction();
                ft.show(switchFragment);
                ft.hide(settingsFragment);
                ft.hide(maptabFragment);
                ft.commit();

                //停止更新位置信息
                maptabFragment.pauseMapUpdate();
            }
        });

        findViewById(R.id.rbMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (m_FMer.findFragmentByTag("mapFragment").isVisible()) {
                    Log.e("", "map clicked");
                    return;
                }
                rbMap.setChecked(true);
                rbMap.setTextColor(getResources().getColor(R.color.blue));
                rbSwitch.setChecked(false);
                rbSwitch.setTextColor(Color.BLACK);
                rbSettings.setChecked(false);
                rbSettings.setTextColor(Color.BLACK);

                //从backstack中弹出
                //popAllFragmentsExceptTheBottomOne();

                FragmentTransaction ft = m_FMer.beginTransaction();
                ft.hide(switchFragment);
                ft.hide(settingsFragment);
                ft.show(maptabFragment);
                //ft.addToBackStack("mapFragment");
                ft.commit();

                //开始更新位置信息
                maptabFragment.resumeMapUpdate();
            }
        });

        findViewById(R.id.rbSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_FMer.findFragmentByTag("settingsFragment").isVisible()){
                    Log.e("", "set clicked");
                    return;
                }

                rbMap.setChecked(false);
                rbMap.setTextColor(Color.BLACK);
                rbSwitch.setChecked(false);
                rbSwitch.setTextColor(Color.BLACK);
                rbSettings.setChecked(true);
                rbSettings.setTextColor(getResources().getColor(R.color.blue));

                FragmentTransaction ft = m_FMer.beginTransaction();
                ft.hide(switchFragment);
                ft.show(settingsFragment);
                ft.hide(maptabFragment);
                ft.commit();
            }
        });
    }

    /**
     * 检查更新
     */
    public void updata() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("软件升级")
                .setMessage("发现新版本,建议立即更新使用.")
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent updateIntent = new Intent(FragmentActivity.this, UpdateAppService.class);
                        startService(updateIntent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
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

    /**
     * 处理返回按钮
     */
    @Override
    public void onBackPressed() {
        //this.finish();
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
        startService(new Intent(FragmentActivity.this, GPSDataService.class));
        Log.i("退出","ooooooooo");
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

    public void initNotificaton() {
        manager = (NotificationManager) getSystemService(Activity.NOTIFICATION_SERVICE);
    }

    public void Checkversion() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg=new Message();
                Bundle bundle=new Bundle();
                boolean isupdate;
                String baseUrl = "http://fir.im/api/v2/app/version/%s?token=%s";
                String checkUpdateUrl = String.format(baseUrl, "5531cb8eddfef0bb3e000a78", "6d5d9e60e56f11e492cf97620aa3a7444608b774");
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
                            if (firVersionCode > currentVersionCode) {
                                //需要更新
                                Log.i("infox", "need update");
                                bundle.putInt("want",1);
                                bundle.putBoolean("isupdate",true);
                            } else if (firVersionCode == currentVersionCode) {
                                //如果本地app的versionCode与FIR上的app的versionCode一致，则需要判断versionName.
                                if (!currentVersionName.equals(firVersionName)) {
                                    Log.i("infox", "need update");
                                bundle.putInt("want",1);
                                bundle.putBoolean("isupdate",true);
                                }
                            } else {
                                //不需要更新,当前版本高于FIR上的app版本.
                                Log.i("infox", " no need update");
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

    @Override
    public void gpsCallBack(float lat, float lon) {
        //传递数据给地图的Fragment
        Log.i(TAG, lat + "aaaa");
        Log.i(TAG, lon + "qqqq");
        LatLng sourcePoint = new LatLng(lat, lon);
        LatLng desPoint = mCenter.convertPoint(sourcePoint);
        maptabFragment.locateMobile(desPoint);
    }



}
