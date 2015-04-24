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

import com.xunce.electrombile.activity.BaseActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lybvinci on 2015/4/24.
 */
public class BaseFragment extends Fragment{

        private static String TAG = "BaseFragmet:";

        protected enum loginHandler_key{
            START_LOGIN,
            SUCCESS,
            FAILED,
            LOGIN,
        }

        protected Handler loginHandler = new Handler(){
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
        protected XPGWifiDevice mXpgWifiDevice;
        protected ConcurrentHashMap<String, Object> deviceDataMap;
        protected SettingManager setManager;
        protected static List<XPGWifiDevice> devicesList = new ArrayList<XPGWifiDevice>();

        @Override
        public void onCreate(Bundle saveInstanceState){
            super.onCreate(saveInstanceState);
            setManager = new SettingManager(getActivity().getApplicationContext());
            mCenter = CmdCenter.getInstance(getActivity().getApplicationContext());
            mXpgWifiDevice = BaseActivity.mXpgWifiDevice;
            if(mXpgWifiDevice == null && setManager.getDid() !=null && setManager.getPassCode() !=null)
                loginHandler.sendEmptyMessage(loginHandler_key.START_LOGIN.ordinal());

        }

        //handler 处理事件
        protected enum handler_key {

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

        protected HashMap<String, String> GPS_Data;
        protected Handler fragmentHandler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                handler_key key= handler_key.values()[msg.what];
                switch (key){
                    case RECEIVED:
                        Log.i("switchfragment", "RECEIVED XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
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
            Log.i("switchFragment","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
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

        }
        /**
         * 断开连接回调接口.
         *
         * @param device
         *            设备对象
         */
        protected void didDisconnected(XPGWifiDevice device) {
            ToastUtils.showLong(getActivity().getApplicationContext(),"设备连接断开，请重连");
        }


        @Override
        public void onResume() {
            super.onResume();
            if(mXpgWifiDevice != null)
                mXpgWifiDevice.setListener(deviceListener);
            mCenter.getXPGWifiSDK().setListener(sdkListener);
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
                this.devicesList = devicesList;
                Log.i("设备列表", devicesList.toString());
                loginHandler.sendEmptyMessage(loginHandler_key.LOGIN.ordinal());
            }else{
                loginHandler.sendEmptyMessage(loginHandler_key.FAILED.ordinal());
            }
        }



}

