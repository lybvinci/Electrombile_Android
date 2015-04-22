package com.xunce.electrombile.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.RecordActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MaptabFragment extends Fragment {

    private static String TAG = "MaptabFragment:";

    //播放线程消息类型
    private final int CHANGEPOINT = 1;
    private final int PAUSEPLAY = 2;

    //获取位置信息的http接口
    private final String httpBase= "http://electrombile.huakexunce.com/position";
    public static MapView mMapView;
    private BaiduMap mBaiduMap;
    Button btnChengeMode;
    Button btnLocation;
    Button btnRecord;
    Button btnPause;
    //public BDLocationListener myListener;

    //maptabFragment 维护一组坐标数据
    private List<LatLng> dataList;

    private List<LatLng> resultLine;
    PlayRecordThread m_threadnew;

    //电动车标志
    Marker markerMobile;
    MarkerOptions option2;

    private boolean isPlaying = false;
    //
    //int i = 0


    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate called!");
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(this.getActivity().getApplicationContext());

        //myListener = new MyLocationListener();

        dataList = new ArrayList<LatLng>();
        dataList.add(new LatLng(30.5171, 114.4392));
        dataList.add(new LatLng(30.5272, 114.4493));
        dataList.add(new LatLng(30.5173, 114.4394));
        dataList.add(new LatLng(30.5174, 114.4395));
        dataList.add(new LatLng(30.5175, 114.4396));

    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView called!");
		View view = inflater.inflate(R.layout.map_fragment, container, false);
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        //mMapView.getCont

        btnChengeMode = (Button)view.findViewById(R.id.btn_changeMode);
        btnChengeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //int res = mLocationClient.requestLocation();
                //Log.e("", "request location result:" + res);
                drawLine();
                pausePlay();
            }
        });

        btnPause = (Button)view.findViewById(R.id.btn_pause);
        btnPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(m_threadnew == null) return;
                if(btnPause.getText().equals("暂停"))
                    btnPause.setText("开始");
                else
                    btnPause.setText("暂停");
                pausePlay();
            }
        });

        btnLocation = (Button)view.findViewById(R.id.btn_location);
        btnLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(mBaiduMap != null){
                    //LatLng point = getLatestLocation();
                    //locateMobile(point);
                }
            }
        });

        btnRecord = (Button)view.findViewById(R.id.btn_record);
        btnRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(),RecordActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        //if(mLocationClient == null){
            /**
             * map init
             */
            // 开启定位图层
           // mBaiduMap.setMyLocationEnabled(true);
           // mLocationClient = new LocationClient(getActivity().getApplicationContext());     //声明LocationClient类
            //mLocationClient.registerLocationListener( myListener );    //注册监听函数
            //LocationClientOption option = new LocationClientOption();
            //option.setOpenGps(true);// 打开gps
            //option.setCoorType("bd09ll"); // 设置坐标类型
            //option.setScanSpan(1000);
            //mLocationClient.setLocOption(option);

            //设置我当前位置的模式
            //mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(LocationMode.NORMAL, true, null));

            /**
             * 显示车的位置
             */
            //定义Maker坐标点
            //leacloud服务器清空，暂时自定义数据代替
            LatLng point = new LatLng(30.5171, 114.4392);
            Log.e(point.latitude + "", point.longitude + "");

            //构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_gcoding);
            //构建MarkerOption，用于在地图上添加Marker
            option2 = new MarkerOptions()
                    .position(point)
                    .icon(bitmap);
            //在地图上添加Marker，并显示
            markerMobile = (Marker)mBaiduMap.addOverlay(option2);

            //将电动车位置移至中心
            MapStatus mMapStatus = new MapStatus.Builder()
                    .target(point)
                    .zoom(mBaiduMap.getMapStatus().zoom / (float)1.3)
                    .build();
            //float a = mBaiduMap.getMapStatus().zoom;
            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            mBaiduMap.setMapStatus(mMapStatusUpdate);
        }
    //}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		((TextView)getView().findViewById(R.id.tvTop)).setText("地图");
	}
//
//    public class MyLocationListener implements BDLocationListener {
//        @Override
//        public void onReceiveLocation(BDLocation location) {
//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(location.getRadius())
//                            // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(100).latitude(location.getLatitude())
//                    .longitude(location.getLongitude()).build();
//            //mBaiduMap.setMyLocationData(locData);
//            Log.e(locData.latitude + "", locData.longitude + "");
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "onDestroyView called!");
    }

    @Override
    public void onDestroy() {
        // 退出时销毁定位
        //mLocationClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
    @Override
    public void onResume() {

        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        Log.i(TAG, "onResume called!");
        mMapView.setVisibility(View.VISIBLE);
        mMapView.onResume();
        super.onResume();
        //BNMapController.getInstance().onResume();

    }
    @Override
    public void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        Log.i(TAG, "onPause called!");
        //mMapView.setVisibility(View.INVISIBLE);
        mMapView.onPause();
        super.onPause();
    }

    //暂停更新地图
    public void pauseMapUpdate(){
//        if(mLocationClient == null) return;
//        mLocationClient.stop();
    }

    //恢复更新地图
    public void resumeMapUpdate(){
//        if(mLocationClient == null) return;
//        mLocationClient.start();
    }

    //将地图中心移到某点
    private void locateMobile(LatLng point){
        /**
         *设定中心点坐标
         */
        //定义地图状态
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(mBaiduMap.getMapStatus().zoom)
                .build();
        //float a = mBaiduMap.getMapStatus().zoom;
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
        markerMobile.setPosition(point);
        //mBaiduMap.addOverlay(option2);
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
    public LatLng getLatestLocation(){
        LatLng location;
        JSONObject jsonObject;
        JSONArray m_JSONArray = getHttp(httpBase);
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

    private void drawLine(){
        //定义多边形的五个顶点
        List<LatLng> dts = new ArrayList<LatLng>();
        dts.add(new LatLng(30.5171, 114.4392));
        dts.add(new LatLng(30.1272, 114.5493));
        dts.add(new LatLng(30.6373, 114.6394));
        dts.add(new LatLng(30.0474, 114.7395));
        dts.add(new LatLng(30.7575, 114.8396));
        //构建用户绘制多边形的Option对象
        OverlayOptions polylineOption = new PolylineOptions()
                .points(dts)
                .width(5)
                .color(0xAA00FF00);
        //在地图上添加多边形Option，用于显示
        mBaiduMap.addOverlay(polylineOption);

        //TODO::只是点击轨迹后才会画轨迹，不是每次点击按钮就画
        if(!isPlaying) {
            isPlaying = true;
            m_threadnew = new PlayRecordThread(1000);
            m_threadnew.setPoints(dts);
            m_threadnew.start();
        }
    }

    private class PlayRecordThread extends Thread{
        public  boolean pauseFlag = false;
        int periodMilli = 1000;
        List<LatLng> m_points;
        public void setPoints(List<LatLng> points){
            m_points = points;
        }

        public PlayRecordThread(int period){
            periodMilli = period;
        }

        @Override
        public void run() {
            super.run();
            while(true) {
                for (LatLng pt : m_points) {
                    //暂停播放
                    synchronized (PlayRecordThread.class) {
                        while (pauseFlag) ;
                    }
                    ;
                    Message msg = Message.obtain();
                    msg.what = CHANGEPOINT;
                    msg.obj = pt;
                    playHandler.sendMessage(msg);

                    try {
                        //TODO::periodMilli可变，更改速度
                        synchronized (PlayRecordThread.class) {
                            sleep(periodMilli);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //isPlaying = false;

                Message msg = Message.obtain();
                msg.what = PAUSEPLAY;
                playHandler.sendMessage(msg);
            }
        }
    }

    private Handler playHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case CHANGEPOINT:
                    locateMobile((LatLng) msg.obj);
                    break;
                case PAUSEPLAY:
                    pausePlay();
                    btnPause.setText("开始");
                    break;
            }
        }
    };

    private void pausePlay(){
        if(!m_threadnew.pauseFlag)
            m_threadnew.pauseFlag = true;
        else
            m_threadnew.pauseFlag = false;
    };
}
