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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.Base.sdk.SettingManager;
import com.xunce.electrombile.activity.AlarmActivity;
import com.xunce.electrombile.xpg.common.useful.NetworkUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by lybvinci on 2015/4/25.
 */
public class GPSDataService extends Service{

    private final static String TAG = "GPSDataService";
    private CmdCenter mCenter;
    private SettingManager setManager;
    LatLng pointOld = null;
    private LatLng pointNew;
    private float fLat;
    private float fLong;
    private String date;

   //线程是否启动
    public static boolean isRunning = false;

    private final String KET_LONG = "lon";
    private final String KET_LAT = "lat";

    //handler 处理事件
    private enum handler_key {
        /** 更新UI界面 */
        UPDATE_UI,
        /** 显示警告*/
        ALARM,
        /** 设备断开连接 */
        DISCONNECTED,
        /** 获取设备状态 */
        GET_STATUE,
        //手动获取数据
        SHOUDONGREC,
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "服务已启动");
        setManager = new SettingManager(this);
        mCenter = CmdCenter.getInstance(this);
        //   Log.i(TAG,"设备正常启动后台");
        if(!isRunning)
            getData.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //START_STICKY是service被kill后自动重写创建。
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Intent localIntent = new Intent();
        localIntent.setClass(this,GPSDataService.class);
        this.startService(localIntent);
    }


    private Handler Handler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            handler_key key = handler_key.values()[msg.what];
            switch (key) {
                case SHOUDONGREC:
                    receivedManual();
                    break;
            }
        }
    };

    //手动拉取数据，进行判断
    private void receivedManual() {
      //  LogUtil.log.i( "SHOUDONGREC");
        double distance = 0;
        if(pointOld != null) {
            distance = Math.abs(DistanceUtil.getDistance(pointOld, pointNew));
        //    Log.i(TAG, distance + "LLLL");
        }
        if(pointOld == null && setManager.getAlarmFlag()) {
            pointOld = pointNew;
        }
        if (distance > 500 && setManager.getAlarmFlag() && AlarmActivity.instance == null) {
            pointOld = null;
            wakeUpAndUnlock(this);
            Intent intent = new Intent(this, AlarmActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(intent);
            setManager.setAlarmFlag(false);
        }

        //发送广播，通知主界面
        Intent intent = new Intent();
        intent.putExtra("LAT",fLat);
        intent.putExtra("LONG",fLong);
        intent.putExtra("DATE",date);
        intent.setAction("com.xunce.electrombile.service");
        sendBroadcast(intent);
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


    Thread getData = new Thread(){
        @Override
        public void run() {
            isRunning = true;
            while(true){
                if(NetworkUtils.isNetworkConnected(GPSDataService.this)) {
                    if(!setManager.getDid().isEmpty()) {
                        getLatestData();
                        try {
                            sleep(45000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            sleep(45000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    try {
                        sleep(45000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public void getLatestData(){
        AVQuery<AVObject> query = new AVQuery<AVObject>("GPS");
        query.setLimit(1);
        query.whereEqualTo("did", setManager.getDid());
        query.whereLessThan("createdAt", Calendar.getInstance().getTime());
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
               // Log.i(TAG, e + "");
                if (e == null) {
                    AVObject avObject = avObjects.get(0);
                    fLat = mCenter.parseGPSData((float) avObject.getDouble(KET_LAT));
                    fLong = mCenter.parseGPSData((float) avObject.getDouble(KET_LONG));
                    SimpleDateFormat sdfWithSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sdfWithSecond.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
                    date = sdfWithSecond.format(avObject.getCreatedAt());

                    pointNew = mCenter.convertPoint(new LatLng(fLat, fLong));
                 //   LogUtil.log.i("GPSDDDDDDDDDDDDDDDD" + pointNew.toString());
                    Handler.sendEmptyMessage(handler_key.SHOUDONGREC.ordinal());
                }
            }
        });
    }
}

