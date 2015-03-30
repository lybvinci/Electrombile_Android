package com.xunce.electrombile.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.xunce.electrombile.R;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MaptabFragment extends Fragment {

    private static String TAG = "MaptabFragment:";
    public static MapView mMapView = null;
    private BaiduMap mBaiduMap;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    BitmapDescriptor mCurrentMarker;
    Button btnChengeMode = null;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate called!");
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(this.getActivity().getApplicationContext());
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView called!");
		View view = inflater.inflate(R.layout.map_fragment, container, false);
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        if(mLocationClient != null)mLocationClient.requestLocation();
        if(btnChengeMode == null){
            btnChengeMode = (Button)view.findViewById(R.id.btn_changeMode);
            btnChengeMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int res = mLocationClient.requestLocation();
                    Log.e("", "aaa:" + res);
                }
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        if(mLocationClient == null){
            // 开启定位图层
            mBaiduMap.setMyLocationEnabled(true);
            mLocationClient = new LocationClient(getActivity().getApplicationContext());     //声明LocationClient类
            mLocationClient.registerLocationListener( myListener );    //注册监听函数
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);// 打开gps
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setScanSpan(1000);
            mLocationClient.setLocOption(option);
            mLocationClient.start();

            //定义Maker坐标点
            LatLng point = getLatestLocation(getHttp("http://electrombile.huakexunce.com/position"));
            Log.e(point.latitude + "", point.longitude + "");
//构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_gcoding);
//构建MarkerOption，用于在地图上添加Marker
            OverlayOptions option2 = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
//在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option2);
        }
    }
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		((TextView)getView().findViewById(R.id.tvTop)).setText("地图");
	}

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.e("", "fffffk");
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            Log.e(locData.latitude + "", locData.longitude + "");
            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.FOLLOWING, true, null));
        }
    }

    public JSONArray getHttp(final String httpBase){
        FutureTask<JSONArray> task = new FutureTask<JSONArray>(
                new Callable<JSONArray>() {
                    @Override
                    public JSONArray call() throws Exception {
                        HttpClient client = new DefaultHttpClient();
                        HttpGet get = new HttpGet(httpBase);
                        try {
                            HttpResponse response = client.execute(get);
                            if(response.getStatusLine().getStatusCode() == 200){
                                String result = EntityUtils.toString(response.getEntity());
                                return new JSONArray(result);
                            }
                            return null;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                });

        new Thread(task).start();

        try {
            //wait the result of http get, if wait for more than 5 secs, return null
            return task.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (TimeoutException e) {
            return null;
        }
    }

    //return longitude and latitude data,if no data, returns null
    public LatLng getLatestLocation(JSONArray m_JSONArray){
        LatLng location;
        JSONObject jsonObject;
        if(m_JSONArray == null){
            Log.e("","m_JSONArray is null" );
            return null;
        }
        try {
            jsonObject = m_JSONArray.getJSONObject(m_JSONArray.length() - 1);
            double longt = jsonObject.getDouble("longitude");
            double lat = jsonObject.getDouble("latitude");
            location = new LatLng(lat,longt);
            return location;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView called!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
    }
    @Override
    public void onResume() {

        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        Log.i(TAG, "onResume called!");
        mMapView.setVisibility(View.VISIBLE);
        mMapView.onResume();
        super.onResume();
//        Timer timer = new Timer();// 实例化Timer类
//        timer.schedule(new TimerTask() {
//            public void run() {
//                mMapView.setVisibility(View.VISIBLE);
//            }
//        }, 1000);// 这里百毫秒

    }
    @Override
    public void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        Log.i(TAG, "onPause called!");
        //mMapView.setVisibility(View.INVISIBLE);
        mMapView.onPause();
        super.onPause();
    }
}
