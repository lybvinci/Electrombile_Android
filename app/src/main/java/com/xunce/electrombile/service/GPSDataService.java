package com.xunce.electrombile.service;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.avos.avoscloud.LogUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiDeviceListener;
import com.xunce.electrombile.Base.config.Configs;
import com.xunce.electrombile.Base.config.JsonKeys;
import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.Base.sdk.SettingManager;
import com.xunce.electrombile.activity.AlarmActivity;
import com.xunce.electrombile.fragment.BaseFragment;
import com.xunce.electrombile.fragment.SwitchFragment;
import com.xunce.electrombile.xpg.common.useful.JSONUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lybvinci on 2015/4/25.
 */
public class GPSDataService extends Service{

    private final static String TAG = "GPSDataService";
    private CmdCenter mCenter;
    private ConcurrentHashMap<String, Object> deviceDataMap;
    private XPGWifiDevice mXpgWifiDevice;
    private SettingManager setManager;
    LatLng pointOld = null;
    private LatLng pointNew;

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

        //手动获取数据
        SHOUDONGREC,
        //手动刷新时间
        SHOUDONGTIME,
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"服务已启动");
        setManager = new SettingManager(this);
        mCenter = CmdCenter.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mXpgWifiDevice = BaseFragment.mXpgWifiDevice;
        if(mXpgWifiDevice != null) {
            Log.i(TAG,"设备正常启动后台");
            mXpgWifiDevice.setListener(deviceListener);
        }
        Handler.sendEmptyMessage(handler_key.SHOUDONGTIME.ordinal());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private Handler Handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler_key key = handler_key.values()[msg.what];
            switch (key) {
                case RECEIVED:
                    Log.i("switchfragment", "RECEIVED XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                    if (deviceDataMap.get("data") != null) {
                        Log.i("info", (String) deviceDataMap.get("data"));
                        String data = (String) deviceDataMap.get("data");
                        HashMap<String, String> hm = mCenter.parseAllData(data);
                        float latData = mCenter.parseGPSData(hm.get(JsonKeys.LAT));
                        float longData = mCenter.parseGPSData(hm.get(JsonKeys.LONG));
                        Log.i(TAG,latData + "PPPPP");
                        Log.i(TAG,longData + "OOOO");
                        LatLng pointNewTemp = new LatLng(latData, longData);
                        LatLng pointNew = mCenter.convertPoint(pointNewTemp);
                        double distance = 0;
                        if(pointOld != null) {
                            distance = DistanceUtil.getDistance(pointOld, pointNew);
                            Log.i(TAG,distance + "LLLL");
                        }
                        if(pointOld == null && mCenter.alarmFlag) {
                            pointOld = pointNew;
                        }
                        if ((!hm.get(JsonKeys.ALARM).equals("0") || distance > 100) && mCenter.alarmFlag && AlarmActivity.instance == null) {
                            pointOld = null;
                            wakeUpAndUnlock(GPSDataService.this);
                            Intent intent = new Intent(GPSDataService.this, AlarmActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplication().startActivity(intent);
                        }
                    }
                    break;
                case SHOUDONGREC:
                    LogUtil.log.i( "SHOUDONGREC");
                    double distance = 0;
                    if(pointOld != null) {
                        distance = DistanceUtil.getDistance(pointOld, pointNew);
                        Log.i(TAG,distance + "LLLL");
                    }
                    if(pointOld == null && mCenter.alarmFlag) {
                        pointOld = pointNew;
                    }
                    if (distance > 500 && mCenter.alarmFlag && AlarmActivity.instance == null) {
                        pointOld = null;
                        wakeUpAndUnlock(GPSDataService.this);
                        Intent intent = new Intent(GPSDataService.this, AlarmActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(intent);
                        mCenter.alarmFlag = false;
                    }
                    Handler.sendEmptyMessage(handler_key.SHOUDONGTIME.ordinal());
                    break;
                case SHOUDONGTIME:
                    LogUtil.log.i( "SHOUDONGTIME");
                    timeGetData();
                  //  updateLocation();
                    break;
            }
        }
    };

    /**
     * XPGWifiDeviceListener
     * <p/>
     * 设备属性监听器。 设备连接断开、获取绑定参数、获取设备信息、控制和接受设备信息相关.
     */
    private XPGWifiDeviceListener deviceListener = new XPGWifiDeviceListener() {

        @Override
        public void didReceiveData(XPGWifiDevice device,
                                   ConcurrentHashMap<String, Object> dataMap, int result) {
            GPSDataService.this.didReceiveData(device, dataMap, result);
        }
    };

    private void didReceiveData(XPGWifiDevice device,
                                ConcurrentHashMap<String, Object> dataMap, int result) {
        this.deviceDataMap = dataMap;
        Log.i("switchFragment", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        Log.i("device:::", device.toString());
        Log.i("dataMap:::", dataMap.toString());
        Log.i("result", result + "");
        Handler.sendEmptyMessage(handler_key.RECEIVED.ordinal());
    }

    public void wakeUpAndUnlock(Context context){
        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();
    }

    //手动获取数据
    public void updateLocation(){
        final String httpAPI = "http://api.gizwits.com/app/devdata/" + setManager.getDid() + "/latest";
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(httpAPI);
                get.addHeader("Content-Type", "application/json");
                get.addHeader("X-Gizwits-Application-Id", Configs.APPID);
                try {
                    HttpResponse response = client.execute(get);
                    if(response.getStatusLine().getStatusCode() == 200){
                        String resultJson = EntityUtils.toString(response.getEntity());
                        String resultLong = JSONUtils.ParseJSON(JSONUtils.ParseJSON(resultJson, "attr"), "long");
                        String resultLat = JSONUtils.ParseJSON(JSONUtils.ParseJSON(resultJson, "attr"), "lat");
                        float fLat = mCenter.parseGPSData(resultLat);
                        float fLong = mCenter.parseGPSData(resultLong);
                        pointNew= mCenter.convertPoint(new LatLng(fLat, fLong));
                        LogUtil.log.i( "GPSDDDDDDDDDDDDDDDD" + pointNew.toString());
                        //向主线程发出消息，地图定位成功
                        Handler.sendEmptyMessage(handler_key.SHOUDONGREC.ordinal());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Handler.sendEmptyMessage(handler_key.SHOUDONGTIME.ordinal());
                }
            }
        }).start();
    }

    private void timeGetData(){

        new Thread() {
            public void run() {
                try {

                    sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    updateLocation();
                }
            }
        }.start();
    }

}

