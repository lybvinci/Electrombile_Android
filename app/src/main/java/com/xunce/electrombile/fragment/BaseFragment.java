package com.xunce.electrombile.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiDeviceListener;
import com.xtremeprog.xpgconnect.XPGWifiSDKListener;
import com.xunce.electrombile.Base.config.Configs;
import com.xunce.electrombile.Base.config.JsonKeys;
import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.Base.sdk.SettingManager;
import com.xunce.electrombile.activity.AlarmActivity;
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;

import com.xunce.electrombile.activity.BaseActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lybvinci on 2015/4/24.
 * 实现机智云的所有回调接口，监听器。
 * 解除绑定功能：
 * 1.需要先绑定设备，如果界面先出现正在登陆设备，而未出现登陆成功，就可能是之前解除绑定过，否则不会出现。
 * 2.解除绑定功能需要先使用unbindDevice函数，解除绑定，在didunbindDevice里得到结果
 * 3.同时还需要调用disconnnect函数，断开连接。如果不断开连接，是不能重新绑定设备的。
 * 4.当解除绑定时，如果出现解除绑定成功，就可以重新绑定新设备了。
 * 5当解除绑定时出现设备断开连接，则表明解除绑定操作失败，重新打开app，重新解除绑定操作……（我也不知道怎么操作才能正常解除绑定）
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
//                        if(getData == null) {
//                            getData = new Thread() {
//                                @Override
//                                public void run() {
//                                    while (true) {
//                                        while (isStart) {
//                                            updateLocation();
//                                            try {
//                                                sleep(60000);
//                                            } catch (InterruptedException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//                                    }
//                                }
//                            };
//                            getData.start();
//                        }
//                        //if(getData.isAlive())
//                            getData.start();
//                            Log.i("", "+__+_P+_++_+");
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
        public static  XPGWifiDevice mXpgWifiDevice;
        protected ConcurrentHashMap<String, Object> deviceDataMap;
        protected SettingManager setManager;
        protected static List<XPGWifiDevice> devicesList = new ArrayList<XPGWifiDevice>();
        protected GPSDataChangeListener mGpsChangedListener;

        @Override
        public void onCreate(Bundle saveInstanceState){
            super.onCreate(saveInstanceState);
            setManager = new SettingManager(getActivity().getApplicationContext());
            mCenter = CmdCenter.getInstance(getActivity().getApplicationContext());
            mXpgWifiDevice = BaseActivity.mXpgWifiDevice;
            if(mXpgWifiDevice == null && setManager.getDid() !=null && setManager.getPassCode() !=null)
                loginHandler.sendEmptyMessage(loginHandler_key.START_LOGIN.ordinal());
        }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
//        SHOUDONGREC,
   //     SHOUDONGTIME,
        }

        protected HashMap<String, String> GPS_Data;
    protected LatLng pointOld = null;
    private LatLng pointNew;

    protected Handler fragmentHandler = new Handler(){
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                handler_key key= handler_key.values()[msg.what];
                switch (key){
                    case RECEIVED:
                        Log.i("switchfragment", "RECEIVED XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
                        if (deviceDataMap.get("data") != null) {
                            receivedMQTTData();
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
//                            byte[] binary = (byte[])deviceDataMap.get("binary");
//                            String touChuanData = mCenter.cParseString(binary);
//                            if(touChuanData == null)
//                                break;
//                            Log.i("CHUANTOUDATA::::", touChuanData + "xxxxxxxx");
//                            if(touChuanData.equals("SET_TIMER_OK")){
//                                ToastUtils.showShort(getActivity().getApplicationContext(),"GPS定时发送设置成功");
//                            }
//                            else if(touChuanData.equals("SET_SOS_OK")){
//                                ToastUtils.showShort(getActivity().getApplicationContext(),"管理员设置成功");
//                            }
//                            else if(touChuanData.equals("DEL_SOS_OK")){
//                                ToastUtils.showShort(getActivity().getApplicationContext(),"删除管理员成功");
//                            }
//                            else if(touChuanData.equals("SET_SAVING_OK")){
//                                ToastUtils.showShort(getActivity().getApplicationContext(),"模式设置成功");
//                            }
//                            else if(touChuanData.equals("RESET_OK")){
//                                ToastUtils.showShort(getActivity().getApplicationContext(),"重启设备成功");
//                            }
//                            else{
//                                GPS_Data = new HashMap<String, String>();
//                                GPS_Data = mCenter.parseGps(touChuanData);
//                            }

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

        protected void receivedMQTTData() {
            Log.i("info", (String) deviceDataMap.get("data"));
            String data = (String) deviceDataMap.get("data");
            HashMap<String,String> hm = mCenter.parseAllData(data);
            if(hm.get(JsonKeys.LAT) != null && hm.get(JsonKeys.LONG) != null) {
                float latData = mCenter.parseGPSData(hm.get(JsonKeys.LAT));
                float longData = mCenter.parseGPSData(hm.get(JsonKeys.LONG));
                Log.i(TAG, latData + "PPPPP");
                Log.i(TAG, longData + "OOOO");
                LatLng pointNewTemp = new LatLng(latData, longData);
                pointNew = mCenter.convertPoint(pointNewTemp);
                double distance = 0;
                if (pointOld != null) {
                    distance = DistanceUtil.getDistance(pointOld, pointNew);
                    Log.i(TAG, distance + "PPPPP");
                }
                if( pointOld == null && mCenter.alarmFlag) {
                    pointOld = pointNew;
                    Log.i(TAG, mCenter.alarmFlag + "    PPPPP");
                }
                if ((!hm.get(JsonKeys.ALARM).equals("0") || distance > 100)
                        && mCenter.alarmFlag
                        && AlarmActivity.instance == null) {
                    pointOld = null;
                    Intent intent = new Intent(getActivity().getApplicationContext(), AlarmActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                mGpsChangedListener.gpsCallBack(pointNew);
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
                    setManager.setDid(mXpgWifiDevice.getDid());
                    mXpgWifiDevice.setListener(deviceListener);
                    mXpgWifiDevice.login(setManager.getUid(), setManager.getToken());

                 //   updateLocation();
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
//            Log.i("switchFragment","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//            Log.i("device:::",device.toString());
//            Log.i("dataMap:::",dataMap.toString());
//            Log.i("result",result+"");
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
//            isStart = true;
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
           //     mCenter.cDisconnect(mXpgWifiDevice);
                mXpgWifiDevice = null;
                setManager.cleanDevice();
            }else{
                ToastUtils.showShort(getActivity().getApplicationContext(),"设备解除绑定失败");
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


    public interface GPSDataChangeListener{
        public void gpsCallBack(LatLng desLat);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mGpsChangedListener = (GPSDataChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement GPSDataChangeListener");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

