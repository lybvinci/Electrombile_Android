package com.xunce.electrombile.fragment;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiDeviceListener;
import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.BaseActivity;
import com.xunce.electrombile.activity.FragmentActivity;
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;


public class SwitchFragment extends Fragment implements OnClickListener {

    private static String TAG = "SwitchFragment:";
    private final int IS_FINISH = 1;
    private boolean systemState = false;
    private boolean alarmState = false;

    NotificationManager manager = FragmentActivity.manager;

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.cancel(1);
    }

    private String[] SWITCHKEY = {
            "switch",
            "ring"
    };


    private Button btnAlarm;
    private Button btnSystem;
    private Button btnTest;

    private CmdCenter mCenter;
    private XPGWifiDevice mXpgWifiDevice;
    private ConcurrentHashMap<String, Object> deviceDataMap;
    //  public static XPGWifiDevice mXpgWifiDevice;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        //       setManager = new SettingManager(getApplicationContext());
        mCenter = CmdCenter.getInstance(getActivity().getApplicationContext());
        // 每次返回activity都要注册一次sdk监听器，保证sdk状态能正确回调
        mXpgWifiDevice = BaseActivity.mXpgWifiDevice;
        if (mXpgWifiDevice != null)
            mXpgWifiDevice.setListener(deviceListener);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_SystemState).setOnClickListener(this);
        //view.findViewById(R.id.btn_RemoteAlarm).setOnClickListener(this);
        //view.findViewById(R.id.btn_test).setOnClickListener(this);
       // btnAlarm = (Button) getActivity().findViewById(R.id.btn_RemoteAlarm);
        btnSystem = (Button) getActivity().findViewById(R.id.btn_SystemState);
        //btnTest = (Button) getActivity().findViewById(R.id.btn_test);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView called!");
        return inflater.inflate(R.layout.switch_fragment, container, false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_SystemState:
                systemBtnClicked();
                break;
//            case R.id.btn_RemoteAlarm:
//                remoteAlarmClicked();
//                break;
//            case R.id.btn_test:
//                testBtnClicked();
            default:
                break;
        }
    }

    //handler 处理事件
    private enum handler_key {

//<<<<<<< H//EAD
//        if(systemState == fals//e){
//            value[0] = 1;manager.cancel(1);changeNotificaton//();
//            btnSystem.setBackgroundResource(R.drawable.common_btn_presse//d);
//        }el//se{
//            value[0] = 0; manager.cancel(1);initNotificaton//();
//            btnSystem.setBackgroundResource(R.drawable.common_btn_presse//d);
//      //  }
//        final int finalValue[] = val//ue;
//        new Thread(new Runnable(//) {
//            @Overr//ide
//            public void run(//) {
//                requestHttp("http://electrombile.huakexunce.com/config", key, finalValu//e);
//=======
        /**
         * 更新UI界面
         */
        UPDATE_UI,

        /**
         * 显示警告
         */
        ALARM,

        /**
         * 设备断开连接
         */
        DISCONNECTED,

        /**
         * 接收到设备的数据
         */
        RECEIVED,

        /**
         * 获取设备状态
         */
        GET_STATUE,
    }

    private HashMap<String, String> GPS_Data;
    private Handler fragmentHandler = new Handler() {
        public void handMessage(Message msg) {
            super.handleMessage(msg);
            handler_key key = handler_key.values()[msg.what];
            switch (key) {
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
                    if (deviceDataMap.get("binary") != null) {
                        String binary = (String) deviceDataMap.get("binary");
                        Log.i("info", binary);
                        String ChuanTouData = mCenter.cParseString(binary);
                        if (ChuanTouData == "SET_TIMER_OK") {
                            ToastUtils.showShort(getActivity().getApplicationContext(), "GPS定时发送设置成功");
                        } else if (ChuanTouData == "SET_SOS_OK") {
                            ToastUtils.showShort(getActivity().getApplicationContext(), "管理员设置成功");
                        } else if (ChuanTouData == "DEL_SOS_OK") {
                            ToastUtils.showShort(getActivity().getApplicationContext(), "删除管理员成功");
                        } else if (ChuanTouData == "SET_SAVING_OK") {
                            ToastUtils.showShort(getActivity().getApplicationContext(), "模式设置成功");
                        } else if (ChuanTouData == "RESET_OK") {
                            ToastUtils.showShort(getActivity().getApplicationContext(), "重启设备成功");
                        } else {
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
            this.didDeviceOnline(device, isOnline);
        }

        @Override
        public void didDisconnected(XPGWifiDevice device) {
            this.didDisconnected(device);
        }

        @Override
        public void didReceiveData(XPGWifiDevice device,
                                   ConcurrentHashMap<String, Object> dataMap, int result) {
            this.didReceiveData(device, dataMap, result);
        }

    };

    /**
     * 接收指令回调
     * <p/>
     * sdk接收到模块传入的数据回调该接口.
     *
     * @param device  设备对象
     * @param dataMap json数据表
     * @param result  状态代码
     */
    protected void didReceiveData(XPGWifiDevice device,
                                  ConcurrentHashMap<String, Object> dataMap, int result) {
        this.deviceDataMap = dataMap;
        fragmentHandler.sendEmptyMessage(handler_key.RECEIVED.ordinal());
    }

    /**
     * 设备上下线通知.
     *
     * @param device   设备对象
     * @param isOnline 上下线状态
     */
    protected void didDeviceOnline(XPGWifiDevice device, boolean isOnline) {

    }

    /**
     * 断开连接回调接口.
     *
     * @param device 设备对象
     */
    protected void didDisconnected(XPGWifiDevice device) {

    }

    public void systemBtnClicked() {

        if(!isNetworkAvailable()){
            Toast.makeText(getActivity().getApplicationContext(), "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
            return;
        }
        final String key[] = {SWITCHKEY[0]};
        int value[] = {0};

        if(systemState == false){
            systemState = true;

            //更改通知栏状态
            manager.cancel(1);
            changeNotificaton();

            value[0] = 1;
            //btnSystem.setBackgroundResource(R.drawable.common_btn_pressed);
            btnSystem.setBackgroundColor(Color.YELLOW);

        }else{
            systemState = false;

            //更改通知栏状态
            manager.cancel(1);
            initNotificaton();

            value[0] = 0;
            btnSystem.setBackgroundResource(R.drawable.common_btn_normal);
        }
//        final int finalValue[] = value;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                requestHttp("http://electrombile.huakexunce.com/config", key, finalValue);
//            }
//        }).start();


    }

    public void remoteAlarmClicked() {

        final String key[] = {SWITCHKEY[1]};
        int value[] = {0};

        if (alarmState == false) {
            value[0] = 1;
            btnAlarm.setBackgroundResource(R.drawable.common_btn_pressed);
        } else {
            value[0] = 0;
            btnAlarm.setBackgroundResource(R.drawable.common_btn_pressed);
        }
        final int finalValue[] = value;
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestHttp("http://electrombile.huakexunce.com/config", key, finalValue);
            }
        }).start();
    }

    public void testBtnClicked() {

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public void requestHttp(final String url, final String[] key, final int[] value) {
        int status = 0;
        DefaultHttpClient mHttpClient = new DefaultHttpClient();
        HttpPut mPut = new HttpPut(url);

        //handle key, value
        JSONObject param = new JSONObject();
        int size = key.length;
        for (int i = 0; i < size; i++) {
            try {
                param.put(key[i], value[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //bind  param to entity
        try {
            StringEntity se = new StringEntity(param.toString(), HTTP.UTF_8);
            mPut.setEntity(se);
        } catch (UnsupportedEncodingException e1) {
            // TODOAuto-generated catch block
            e1.printStackTrace();
        }
        try {
            //Socket timeout 6s
            mHttpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 6000);

            // connect timeout 6s
            mHttpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 6000);

            //execute post
            HttpResponse response = mHttpClient.execute(mPut);

            //handle response
            int res = response.getStatusLine().getStatusCode();
            if (res == 200) {
                status = 1;
            } else if (res == 404) {
                status = 404;
            } else if (res == 500) {
                status = 500;
            }
        } catch (ClientProtocolException e) {
            // TODOAuto-generated catchblock
            e.printStackTrace();
            status = 900;
        } catch (ConnectTimeoutException e) {
            // TODOAuto-generated catchblock
            e.printStackTrace();
            status = 901;
        } catch (InterruptedIOException e) {
            // TODOAuto-generated catchblock
            e.printStackTrace();
            status = 902;
        } catch (IOException e) {
            // TODOAuto-generated catchblock
            e.printStackTrace();
            status = 903;
        }

        Message msg = Message.obtain();
        if(systemState == false)
            msg.arg1 = 1;
        else
            msg.arg1 = 0;
        //msg.arg1 = status;
        msg.obj = key[0];
        msg.what = IS_FINISH;
        httpPutHandler.sendMessage(msg);

    }

    private Handler httpPutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Button handleBtn = null;

            if (msg.what == IS_FINISH) {
                switch (msg.arg1) {
                    case 1: {   //put操作是否成功
                        //根据message中的参数判断
                        chengeStateWhenSuc(msg.obj.toString());
                        break;
                    }
                    default: {
                        restoreStateWhenFail(msg.obj.toString());
                        break;
                    }
                }
            }
        }
    };

    private void chengeStateWhenSuc(String keyString) {
        Button btn = null;
        btn = btnSystem;
        btn.setBackgroundColor(Color.YELLOW);
//        if (keyString.equals(SWITCHKEY[0])) {
//            btn = btnSystem;
//            if (systemState == false) {
//                btn.setBackgroundColor(Color.YELLOW);
//                systemState = true;
//            } else {
//                btn.setBackgroundResource(R.drawable.common_btn_normal);
//                systemState = false;
//            }
//        } else if (keyString.equals(SWITCHKEY[1])) {
//            btn = btnAlarm;
//            if (alarmState == false) {
//                btn.setBackgroundColor(Color.YELLOW);
//                alarmState = true;
//            } else {
//                btn.setBackgroundResource(R.drawable.common_btn_normal);
//                alarmState = false;
//            }
//        }
    }

    private void restoreStateWhenFail(String keyString) {
        if (keyString.equals(SWITCHKEY[0])) {
            Toast.makeText(getActivity().getApplicationContext(), "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
            btnSystem.setBackgroundResource(R.drawable.common_btn_normal);
        } else if (keyString.equals(SWITCHKEY[1])) {
            Toast.makeText(getActivity().getApplicationContext(), "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
            btnAlarm.setBackgroundResource(R.drawable.common_btn_normal);
        }
    }

    /**
     * 通知栏启动
     */
    public void changeNotificaton() {
        manager = (NotificationManager) getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.logo, "防盗系统已启动", System.currentTimeMillis());
        Intent intent = new Intent(getActivity().getApplicationContext(), FragmentActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setLatestEventInfo(getActivity().getApplicationContext(), "安全宝", "防盗系统已启动", pi);
        notification.flags = Notification.FLAG_NO_CLEAR;
        manager.notify(1, notification);
    }

    public void initNotificaton() {
        manager = (NotificationManager) getActivity().getSystemService(Activity.NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.logo, "防盗系统已关闭", System.currentTimeMillis());
        Intent intent = new Intent(getActivity().getApplicationContext(), FragmentActivity.class);
        PendingIntent pi = PendingIntent.getActivity(getActivity().getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setLatestEventInfo(getActivity().getApplicationContext(), "安全宝", "危险，防盗系统未启动", pi);
        notification.flags = Notification.FLAG_NO_CLEAR;
        manager.notify(1, notification);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(mXpgWifiDevice != null)
            mXpgWifiDevice.setListener(deviceListener);
    }
}
