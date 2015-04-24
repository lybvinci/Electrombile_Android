package com.xunce.electrombile.fragment;

import android.app.Activity;
import android.graphics.Color;
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
import com.xtremeprog.xpgconnect.XPGWifiSDKListener;
import com.xunce.electrombile.Base.config.Configs;
import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.Base.sdk.SettingManager;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.BaseActivity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;



public class SwitchFragment extends Fragment implements OnClickListener {

    private static String TAG = "SwitchFragment:";
    private final int IS_FINISH = 1;
    private boolean systemState = false;
    private boolean alarmState = false;
    private String[] SWITCHKEY= {
            "switch",
            "ring"
    };
    private GPSDataChangeListener mGpsChangedListener;

    private enum loginHandler_key{
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
                    ToastUtils.showShort(getActivity().getApplicationContext(),"正在登陆设备");
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
    private Button btnAlarm;
    private Button btnSystem;
    private Button btnTest;

    private CmdCenter mCenter;
    private XPGWifiDevice mXpgWifiDevice;
    private ConcurrentHashMap<String, Object> deviceDataMap;
    private SettingManager setManager;
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_SystemState).setOnClickListener(this);
        view.findViewById(R.id.btn_RemoteAlarm).setOnClickListener(this);
        view.findViewById(R.id.btn_test).setOnClickListener(this);
        btnAlarm = (Button)getActivity().findViewById(R.id.btn_RemoteAlarm);
        btnSystem = (Button)getActivity().findViewById(R.id.btn_SystemState);
        btnTest = (Button)getActivity().findViewById(R.id.btn_test);
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
        switch (id){
            case R.id.btn_SystemState:
                systemBtnClicked();
                break;
            case R.id.btn_RemoteAlarm:
                remoteAlarmClicked();
                break;
            case R.id.btn_test:
                testBtnClicked();
            default:
                break;
        }
    }

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
                            mGpsChangedListener.gpsCallBack(GPS_Data.get("Lat"),GPS_Data.get("Lon"));
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
            SwitchFragment.this.didDeviceOnline(device, isOnline);  //didDeviceOnline(device, isOnline);
        }

        @Override
        public void didDisconnected(XPGWifiDevice device) {
            SwitchFragment.this.didDisconnected(device);
        }

        @Override
        public void didReceiveData(XPGWifiDevice device,
                                   ConcurrentHashMap<String, Object> dataMap, int result) {
            SwitchFragment.this.didReceiveData(device, dataMap, result);
        }

    };

    /**
     * 登陆设备
     *            the xpg wifi device
     */
    private void loginDevice() {

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

    public void systemBtnClicked(){
        mCenter.cGetStatus(mXpgWifiDevice);
      //  mCenter.cGprsSend(mXpgWifiDevice);
        Log.i("发送数据SwitchFragment","qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
    }

    public void remoteAlarmClicked(){
        mCenter.cUnbindDevice(setManager.getUid(),setManager.getToken(),setManager.getDid(),setManager.getPassCode());
        mCenter.cDisconnect(mXpgWifiDevice);
    }

    public void testBtnClicked(){
        mCenter.cGetStatus(mXpgWifiDevice);
    }
    public void   requestHttp(final String url,final String[] key, final int[] value) {
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
        msg.arg1 = status;
        msg.obj = key[0];
        msg.what = IS_FINISH;
        httpPutHandler.sendMessage(msg);

    }

    private Handler httpPutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            Button handleBtn = null;

            if(msg.what == IS_FINISH){
                switch(msg.arg1){
                    case 1 :{   //put操作是否成功
                        //根据message中的参数判断
                         chengeStateWhenSuc(msg.obj.toString());
                        break;
                    }
                    default:{
                        restoreStateWhenFail(msg.obj.toString());
                        break;
                    }
                }
            }
        }
    };

    private void chengeStateWhenSuc(String keyString){
        Button btn = null;
        if(keyString.equals(SWITCHKEY[0])){
            btn = btnSystem;
            if (!systemState) {
                btn.setBackgroundColor(Color.YELLOW);
                systemState = true;
            } else {
                btn.setBackgroundResource(R.drawable.common_btn_normal);
                systemState = false;
            }
        }else if(keyString.equals(SWITCHKEY[1])){
            btn = btnAlarm;
            if (!alarmState) {
                btn.setBackgroundColor(Color.YELLOW);
                alarmState = true;
            } else {
                btn.setBackgroundResource(R.drawable.common_btn_normal);
                alarmState = false;
            }
        }
    }
    private void restoreStateWhenFail(String keyString){
        if(keyString.equals(SWITCHKEY[0])){
            Toast.makeText(getActivity().getApplicationContext(), "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
            btnSystem.setBackgroundResource(R.drawable.common_btn_normal);
        }else if(keyString.equals(SWITCHKEY[1])){
            Toast.makeText(getActivity().getApplicationContext(), "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
            btnAlarm.setBackgroundResource(R.drawable.common_btn_normal);
        }
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
    private XPGWifiSDKListener sdkListener = new XPGWifiSDKListener() {

        @Override
        public void didDiscovered(int error, List<XPGWifiDevice> devicesList) {
            SwitchFragment.this.didDiscovered(error, devicesList);
        }

        @Override
        public void didUnbindDevice(int error, String errorMessage, String did) {
            SwitchFragment.this.didUnbindDevice(error, errorMessage, did);
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

    public interface GPSDataChangeListener{
        public void gpsCallBack(String lat,String lon);
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
}
