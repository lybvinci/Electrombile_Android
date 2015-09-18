package com.xunce.electrombile.fragment;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.avos.avoscloud.LogUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.xunce.electrombile.Base.utils.StringUtils;
import com.xunce.electrombile.Data.WeatherData;
import com.xunce.electrombile.R;
import com.xunce.electrombile.UniversalTool.VibratorUtil;
import com.xunce.electrombile.activity.FragmentActivity;
import com.xunce.electrombile.xpg.common.useful.JSONUtils;
import com.xunce.electrombile.xpg.common.useful.NetworkUtils;
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;

public class SwitchFragment extends BaseFragment implements OnGetGeoCoderResultListener {

    private static String TAG = "SwitchFragment";
    private final int IS_FINISH = 1;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private Context m_context;
    private boolean systemState = false;
    private boolean alarmState = false;
    private boolean ToggleButtonState;
    //缓存view
    private View rootView;
    private Button btnAlarm;
    private ToggleButton btnSystem;
    private Button btnTest;
    private ImageView iv_SystemState;
    //textview 设置当前位置
    private TextView switch_fragment_tvLocation;
    private LocationTVClickedListener locationTVClickedListener;
    //设置时出现的进度框
    private ProgressDialog setAlarmDialog;
    private TextView tvWeather;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        m_context = activity;
        try {
            locationTVClickedListener = (LocationTVClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnArticleSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        // 初始化搜索模块，注册事件监听
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);


        mLocationClient = new LocationClient(m_context);     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        mLocationClient.start();
        //设置报警进度框初始化
        setAlarmDialog = new ProgressDialog(m_context);
        setAlarmDialog.setMessage("正在设置，请稍后......");
    }

    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSystem = (ToggleButton) getActivity().findViewById(R.id.btn_SystemState);

        tvWeather = (TextView) getActivity().findViewById(R.id.weather);

        switch_fragment_tvLocation = (TextView) getActivity().findViewById(R.id.switch_fragment_tvLocation);
        switch_fragment_tvLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                locationTVClickedListener.locationTVClicked();
            }
        });
        iv_SystemState = (ImageView) getActivity().findViewById(R.id.iv_SystemState);
        if (setManager.getAlarmFlag()) {
            showNotification("安全宝防盗系统已启动");
            iv_SystemState.setBackgroundResource(R.drawable.switch_fragment_zhuangtai1);
            btnSystem.setChecked(false);
            ToggleButtonState = false;
        } else {
            iv_SystemState.setBackgroundResource(R.drawable.switch_fragment_zhuangtai2);
            btnSystem.setChecked(true);
            ToggleButtonState = true;
        }
        btnSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            byte firstByteAdd = 0x00;
            byte secondByteAdd = 0x00;
            byte firstByteDelete = 0x00;
            byte secondByteDelete = 0x00;

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    // Log.i("SBBBBBBBBBBB","sbbbbbbbbbbbbbbbbbbbb");
                    //按下以后，isChecked 就是true 就是已经按下了。
                    //如果有网络
                    if (NetworkUtils.isNetworkConnected(m_context)) {
                        //打开报警
                        if (!setManager.getIMEI().isEmpty()) {
                            //     Log.d(TAG, "device success!");
                            setManager.setAlarmFlag(true);
                            cancelNotification();
                            VibratorUtil.Vibrate(getActivity(), 700);
                            byte[] serial = mCenter.getSerial(firstByteAdd, secondByteAdd);
                            FragmentActivity.pushService.sendMessage1(mCenter.cFenceAdd(serial));
                            ToggleButtonState = true;
                            setManager.setAlarmFlag(true);
                            setAlarmDialog.show();
                        } else {
                            ToastUtils.showShort(m_context, "请先绑定设备");
                            btnSystem.setChecked(true);
                            ToggleButtonState = true;
                            iv_SystemState.setBackgroundResource(R.drawable.switch_fragment_zhuangtai2);
                        }
                    } else {
                        ToastUtils.showShort(m_context, "网络连接失败");
                        btnSystem.setChecked(true);
                        ToggleButtonState = true;
                        iv_SystemState.setBackgroundResource(R.drawable.switch_fragment_zhuangtai2);
                    }
                } else {
                    //  Log.d(TAG, "compoundButton notChecked()");
                    if (!setManager.getIMEI().isEmpty()) {
                        if (NetworkUtils.isNetworkConnected(m_context)) {
                            //关闭报警
                            cancelNotification();
                            setManager.setAlarmFlag(false);
                            byte[] serial = mCenter.getSerial(firstByteDelete, secondByteDelete);
                            FragmentActivity.pushService.sendMessage1(mCenter.cFenceDelete(serial));
                            ToggleButtonState = false;
                            setManager.setAlarmFlag(false);
                            setAlarmDialog.show();

                        } else {
                            ToastUtils.showShort(m_context, "网络连接失败");
                            btnSystem.setChecked(true);
                            ToggleButtonState = true;
                            iv_SystemState.setBackgroundResource(R.drawable.switch_fragment_zhuangtai1);
                        }
                    } else {
                        btnSystem.setChecked(true);
                        ToggleButtonState = true;
                        iv_SystemState.setBackgroundResource(R.drawable.switch_fragment_zhuangtai1);
                        ToastUtils.showShort(m_context, "请等待设备绑定");
                    }
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //   Log.i("BAOJING","CHAKAN");
        if (setManager.getAlarmFlag()) {
            iv_SystemState.setBackgroundResource(R.drawable.switch_fragment_zhuangtai1);
            //false
            //  btnSystem.setChecked(true);
            alarmState = true;
        } else {
            iv_SystemState.setBackgroundResource(R.drawable.switch_fragment_zhuangtai2);
            // btnSystem.setChecked(false);
            alarmState = false;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.switch_fragment, container, false);
        }
        return rootView;
    }

    public void reverserGeoCedec(LatLng pCenter) {
        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                .location(pCenter));
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        LogUtil.log.i("进入位置设置:" + result.getAddress());
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        switch_fragment_tvLocation.setText(result.getAddress().trim());
    }

    //显示常驻通知栏
    public void showNotification(String text) {
        NotificationManager notificationManager = (NotificationManager) m_context.getSystemService(getActivity().
                getApplicationContext()
                .NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.mipmap.ic_launcher, "安全宝", System.currentTimeMillis());
        //下面这句用来自定义通知栏
        //notification.contentView = new RemoteViews(getPackageName(),R.layout.notification);
        Intent intent = new Intent(m_context, FragmentActivity.class);
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        PendingIntent contextIntent = PendingIntent.getActivity(m_context, 0, intent, 0);
        notification.setLatestEventInfo(m_context, "安全宝", text, contextIntent);
        notificationManager.notify(R.string.app_name, notification);
    }

    //取消显示常驻通知栏
    public void cancelNotification() {
        NotificationManager notificationManager = (NotificationManager) m_context.getSystemService(getActivity().
                getApplicationContext()
                .NOTIFICATION_SERVICE);
        notificationManager.cancel(R.string.app_name);
    }

    public void cancelDialog() {
        setAlarmDialog.dismiss();
        if (setManager.getAlarmFlag()) {
            showNotification("安全宝防盗系统已启动");
            iv_SystemState.setBackgroundResource(R.drawable.switch_fragment_zhuangtai1);
            setManager.setAlarmFlag(true);
        } else {
            showNotification("安全宝防盗系统已关闭");
            VibratorUtil.Vibrate(getActivity(), 500);
            iv_SystemState.setBackgroundResource(R.drawable.switch_fragment_zhuangtai2);
            setManager.setAlarmFlag(false);
        }
    }

    @Override
    public void onDestroy() {
        m_context = null;
        mSearch = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((ViewGroup) rootView.getParent()).removeView(rootView);
    }

    private void httpGetWeather(String city) {
        city = city.replace("市", "");
        Log.e(TAG, "city：" + city);
        city = StringUtils.encode(city);
        Log.e(TAG, "city" + city);
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.GET,
                "http://apistore.baidu.com/microservice/weather?cityname=" + city,
                new RequestCallBack<String>() {
                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        Log.e(TAG, "onLoading");
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.i(TAG, StringUtils.decodeUnicode(responseInfo.result));
                        String originData = StringUtils.decodeUnicode(responseInfo.result);
                        WeatherData data = new WeatherData();
                        parseWeatherErr(data, originData);
                    }

                    private void parseWeatherErr(WeatherData data, String originData) {
                        data.errNum = JSONUtils.ParseJSON(originData, "errNum");
                        data.errMsg = JSONUtils.ParseJSON(originData, "errMsg");
                        if ("0".equals(data.errNum) && "success".equals(data.errMsg)) {
                            data.retData = JSONUtils.ParseJSON(originData, "retData");
                            parseRetData(data.retData, data);
                        } else {
                            Log.e(TAG, "fail to get Weather info");
                        }
                    }

                    private void parseRetData(String originData, WeatherData data) {
                        data.city = JSONUtils.ParseJSON(originData, "city");
                        data.time = JSONUtils.ParseJSON(originData, "time");
                        data.weather = JSONUtils.ParseJSON(originData, "weather");
                        data.temp = JSONUtils.ParseJSON(originData, "temp");
                        data.l_tmp = JSONUtils.ParseJSON(originData, "l_tmp");
                        data.h_tmp = JSONUtils.ParseJSON(originData, "h_tmp");
                        data.WD = JSONUtils.ParseJSON(originData, "WD");
                        data.WS = JSONUtils.ParseJSON(originData, "WS");
                        setWeather(data);
                    }

                    private void setWeather(WeatherData data) {
                        tvWeather.setText("城市：" + data.city +
                                "更新时间：" + data.time +
                                "天气状况：" + data.weather +
                                "气温：" + data.temp +
                                "最低气温：" + data.l_tmp +
                                "最高气温：" + data.h_tmp +
                                "风向：" + data.WD +
                                "风速：" + data.WS);
                    }

                    @Override
                    public void onStart() {
                        Log.i(TAG, "start");
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Log.i(TAG, "失败");
                    }
                });
    }


    public interface LocationTVClickedListener {
        void locationTVClicked();
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {


            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                String city = location.getCity();
                httpGetWeather(city);
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                String city = location.getCity();
                httpGetWeather(city);
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                Log.i(TAG, "离线定位成功，离线定位结果也是有效的");
                String city = location.getCity();
                httpGetWeather(city);
            } else {
                Log.e(TAG, "服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            }
        }
    }
}
