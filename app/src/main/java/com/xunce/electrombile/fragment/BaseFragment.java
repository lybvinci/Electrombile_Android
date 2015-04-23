package com.xunce.electrombile.fragment;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiDeviceListener;
import com.xtremeprog.xpgconnect.XPGWifiSDKListener;
import com.xunce.electrombile.Base.config.Configs;
import com.xunce.electrombile.Base.config.JsonKeys;
import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.Base.sdk.SettingManager;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.FragmentActivity;
import com.xunce.electrombile.xpg.common.useful.JSONUtils;
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lybvinci on 2015/4/23.
 */
public class BaseFragment extends Fragment {
    protected enum loginHandler_key{
        START_LOGIN,
        SUCCESS,
        FAILED,
        LOGIN,
    }

    Handler loginHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            loginHandler_key key = loginHandler_key.values()[msg.what];
            switch (key){
                case START_LOGIN:
                    ToastUtils.showShort(getActivity().getApplicationContext(), "正在登陆设备");
                    mCenter.getXPGWifiSDK().getBoundDevices(setManager.getUid(),setManager.getToken(), Configs.PRODUCT_KEY);
                    break;
                case SUCCESS:
                    ToastUtils.showShort(getActivity().getApplicationContext(),"登陆设备成功");
                    mCenter.getXPGWifiSDK().setListener(sdkListener);
                    if(mXpgWifiDevice != null)
                        mXpgWifiDevice.setListener(deviceListener);
                    break;
                case FAILED:
                    ToastUtils.showShort(getActivity().getApplicationContext(),"设备登陆失败,请重新绑定设备");
                    break;
                case LOGIN:
                    loginDevice();
                    break;
            }
        }
    };

    protected CmdCenter mCenter;
    protected static XPGWifiDevice mXpgWifiDevice;
    protected ConcurrentHashMap<String, Object> deviceDataMap;
    protected SettingManager setManager;
    protected static List<XPGWifiDevice> devicesList = new ArrayList<XPGWifiDevice>();
    protected NotificationManager manager = FragmentActivity.manager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setManager = new SettingManager(getActivity().getApplicationContext());
        mCenter = CmdCenter.getInstance(getActivity().getApplicationContext());
        if(mXpgWifiDevice == null)
            loginHandler.sendEmptyMessage(loginHandler_key.START_LOGIN.ordinal());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.cancel(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mXpgWifiDevice != null)
            mXpgWifiDevice.setListener(deviceListener);
        mCenter.getXPGWifiSDK().setListener(sdkListener);
    }


    //handler 处理事件
    protected enum handler_key {
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

    protected HashMap<String, String> GPS_Data;
    protected Handler fragmentHandler = new Handler(){
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            handler_key key = handler_key.values()[msg.what];
            switch (key) {
                case RECEIVED:
                    Log.i("switchfragment", "RECEIVED XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                    if (deviceDataMap.get("data") != null) {
                        //通过键值解析json 分成下面的数据
                        String data = (String) deviceDataMap.get("data");
                        String mGPStatus = JSONUtils.ParseJSON(data,JsonKeys.GPSSTATUS);
                        String mAlarm = JSONUtils.ParseJSON(data,JsonKeys.ALARM);
                        String mLat = JSONUtils.ParseJSON(data,JsonKeys.LAT);
                        String mLong = JSONUtils.ParseJSON(data,JsonKeys.LONG);
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
                        byte[] binary = (byte[])deviceDataMap.get("binary");
                        String touChuanData = mCenter.cParseString(binary);
                        if(touChuanData == null)
                            break;
                        Log.i("CHUANTOUDATA::::", touChuanData + "xxxxxxxx");
                        if(touChuanData.equals("SET_TIMER_OK")){
                            ToastUtils.showShort(getActivity().getApplicationContext(),"GPS定时发送设置成功");
                        }
                        else if(touChuanData.equals("SET_SOS_OK")){
                            ToastUtils.showShort(getActivity().getApplicationContext(),"管理员设置成功");
                        }
                        else if(touChuanData.equals("DEL_SOS_OK")){
                            ToastUtils.showShort(getActivity().getApplicationContext(),"删除管理员成功");
                        }
                        else if(touChuanData.equals("SET_SAVING_OK")){
                            ToastUtils.showShort(getActivity().getApplicationContext(),"模式设置成功");
                        }
                        else if(touChuanData.equals("RESET_OK")){
                            ToastUtils.showShort(getActivity().getApplicationContext(),"重启设备成功");
                        }
                        else{
                            GPS_Data = new HashMap<String, String>();
                            GPS_Data = mCenter.parseGps(touChuanData);
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
        public void didLogin(XPGWifiDevice device, int result) {
            super.didLogin(device, result);

        }

        @Override
        public void didDeviceOnline(XPGWifiDevice device, boolean isOnline) {
            BaseFragment.this.didDeviceOnline(device, isOnline);  //didDeviceOnline(device, isOnline);
        }

        @Override
        public void didDisconnected(XPGWifiDevice device) {
            BaseFragment.this.didDisconnected(device);
        }

        @Override
        public void didReceiveData(XPGWifiDevice device,
                                   ConcurrentHashMap<String, Object> dataMap, int result) {
            BaseFragment.this.didReceiveData(device, dataMap, result);
        }

    };
    /**
     * 登陆设备
     *            the xpg wifi device
     */
    protected void loginDevice() {

        Log.i("绑定设备列表",devicesList.toString());
        for (int i = 0; i < devicesList.size(); i++) {
            XPGWifiDevice device = devicesList.get(i);
            if (device != null ) {
                mXpgWifiDevice = device;
                mXpgWifiDevice.setListener(deviceListener);
                mXpgWifiDevice.login(setManager.getUid(), setManager.getToken());
                loginHandler.sendEmptyMessage(loginHandler_key.SUCCESS.ordinal());
                break;
            }else{
                loginHandler.sendEmptyMessage(loginHandler_key.LOGIN.ordinal());

            }

        }
    }
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
        Log.i("switchFragment","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        Log.i("device:::",device.toString());
        Log.i("dataMap:::",dataMap.toString());
        Log.i("result",result+"");
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
        ToastUtils.showLong(getActivity().getApplicationContext(),"设备连接断开，请重连");
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
    /**
     * XPGWifiSDKListener
     * <p/>
     * sdk监听器。 配置设备上线、注册登录用户、搜索发现设备、用户绑定和解绑设备相关.
     */
    protected XPGWifiSDKListener sdkListener = new XPGWifiSDKListener() {

        @Override
        public void didDiscovered(int error, List<XPGWifiDevice> devicesList) {
            BaseFragment.this.didDiscovered(error, devicesList);
        }

        @Override
        public void didUnbindDevice(int error, String errorMessage, String did) {
            BaseFragment.this.didUnbindDevice(error, errorMessage, did);
        }

    };


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
        if(error == 0){
            ToastUtils.showShort(getActivity().getApplicationContext(),"设备解除绑定成功");
            mXpgWifiDevice = null;
            setManager.cleanDevice();
        }
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
        if(error == 0) {
            if(devicesList == null)
                loginHandler.sendEmptyMessage(loginHandler_key.FAILED.ordinal());
            this.devicesList = devicesList;
            Log.i("设备列表", devicesList.toString());
            loginHandler.sendEmptyMessage(loginHandler_key.LOGIN.ordinal());
        }else{
            loginHandler.sendEmptyMessage(loginHandler_key.FAILED.ordinal());
        }
    }

    protected void relieveBind(){
        mCenter.cUnbindDevice(setManager.getUid(),setManager.getToken(),setManager.getDid(),setManager.getPassCode());
    }
}
