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
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.xunce.electrombile.Base.config.Configs;
import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.Base.sdk.SettingManager;
import com.xunce.electrombile.Base.utils.TracksManager;
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
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.xunce.electrombile.Base.utils.TracksManager.TrackPoint;
import com.xunce.electrombile.xpg.common.useful.JSONUtils;

public class MaptabFragment extends Fragment {

    private static String TAG = "MaptabFragment:";

    //播放线程消息类型
    private final int CHANGEPOINT = 1;
    private final int LOCATEMESSAGE = 2;

    //获取位置信息的http接口
    private final String httpBase= "http://api.gizwits.com/app/devdata/";
    public static MapView mMapView;
    private BaiduMap mBaiduMap;
    Button btnChengeMode;
    Button btnLocation;
    Button btnRecord;
    Button btnPlayOrPause;
    Button btnClearTrack;

    //maptabFragment 维护一组历史轨迹坐标列表
    public static List<TrackPoint> trackDataList;

    PlayRecordThread m_playThread;

    //电动车标志
    Marker markerMobile;
    MarkerOptions option2;

    //轨迹图层
    Overlay tracksOverlay;

    //正在播放轨迹标志
    public boolean isPlaying = false;

    private CmdCenter mCenter;
    SettingManager settingManager;




    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate called!");
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(this.getActivity().getApplicationContext());

        trackDataList = new ArrayList<TrackPoint>();
        settingManager = new SettingManager(getActivity().getApplicationContext());

        mCenter = CmdCenter.getInstance(getActivity().getApplicationContext());
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView called!");
		View view = inflater.inflate(R.layout.map_fragment, container, false);

        initView(view);
        return view;
    }

    private void initView(View v) {
        mMapView = (MapView) v.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        //开始/暂停播放按钮
        btnPlayOrPause = (Button)v.findViewById(R.id.btn_play_or_pause);
        btnPlayOrPause.setVisibility(View.INVISIBLE);
        btnPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //若没有播放线程，先创建
                if(m_playThread == null){
                    m_playThread = new PlayRecordThread(1000);
                    m_playThread.start();
                }

                //若在暂停状态，则开始(继续)播放
                if(m_playThread.PAUSE) {
                    btnPlayOrPause.setText("暂停");
                    continuePlay();
                }
                else {
                    btnPlayOrPause.setText("开始");
                    pausePlay();
                }
            }
        });

        //定位电动车按钮
        btnLocation = (Button)v.findViewById(R.id.btn_location);
        btnLocation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(mBaiduMap != null){
                    //LatLng point = getLatestLocation();
                    updateLocation();
                }
            }
        });

        //历史记录按钮
        btnRecord = (Button)v.findViewById(R.id.btn_record);
        btnRecord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                clearData();

                Intent intent = new Intent(getActivity().getApplicationContext(),RecordActivity.class);
                startActivity(intent);
            }
        });

        //退出查看历史轨迹按钮
        btnClearTrack = (Button)v.findViewById(R.id.btn_cancel_track);
        btnClearTrack.setVisibility(View.INVISIBLE);
        btnClearTrack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                clearData();

            }
        });
    }

    private void clearData() {
        //清除轨迹
        if(tracksOverlay != null)
            tracksOverlay.remove();
        //结束播放线程
        if(m_playThread != null){
            m_playThread.isTimeToDie = true;
        }
        m_playThread = null;

        //电动车标志回到当前位置
        updateLocation();

        //清除轨迹数据
        trackDataList.clear();

        //退出播放轨迹模式
        exitPlayTrackMode();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

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
                    .zoom(mBaiduMap.getMapStatus().zoom * new Double(1.5).floatValue())
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
		//((TextView)getView().findViewById(R.id.tvTop)).setText("地图");
	}

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

        //检查历史轨迹列表，若不为空，则需要绘制轨迹
        if(trackDataList.size() > 0){
            if(tracksOverlay != null) tracksOverlay.remove();
            locateMobile(trackDataList.get(0).point);
            enterPlayTrackMode();
            drawLine();
        }
    }
    @Override
    public void onPause() {
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        Log.i(TAG, "onPause called!");
        //mMapView.setVisibility(View.INVISIBLE);
        mMapView.onPause();
        super.onPause();
    }

    private void enterPlayTrackMode(){
        isPlaying = true;
        btnClearTrack.setVisibility(View.VISIBLE);
        btnPlayOrPause.setVisibility(View.VISIBLE);
    }
    private void exitPlayTrackMode(){
        isPlaying = false;
        btnClearTrack.setVisibility(View.INVISIBLE);
        btnPlayOrPause.setVisibility(View.INVISIBLE);
        btnPlayOrPause.setText("开始");
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
    public void locateMobile(LatLng point){
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
    }



    //return longitude and latitude data,if no data, returns null
    public void updateLocation(){
        final String httpAPI = "http://api.gizwits.com/app/devdata/" + settingManager.getDid() + "/latest";
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(httpAPI);
                get.addHeader("Content-Type", "application/json");
                get.addHeader("X-Gizwits-Application-Id", Configs.APPID);
                LatLng point;
                try {
                    HttpResponse response = client.execute(get);
                    if(response.getStatusLine().getStatusCode() == 200){
                        String resultJson = EntityUtils.toString(response.getEntity());
                        String resultLong = JSONUtils.ParseJSON(JSONUtils.ParseJSON(resultJson, "attr"), "long");
                        String resultLat = JSONUtils.ParseJSON(JSONUtils.ParseJSON(resultJson, "attr"), "lat");
                        float fLat = mCenter.parseGPSData(resultLat);
                        float fLong = mCenter.parseGPSData(resultLong);
                        LatLng p = mCenter.convertPoint(new LatLng(fLat, fLong));
                        point= mCenter.convertPoint(new LatLng(fLat, fLong));
                        //向主线程发出消息，地图定位成功
                        Message msg = Message.obtain();
                        msg.what = LOCATEMESSAGE;
                        msg.obj = point;
                        playHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    //向主线程发出消息，地图定位成功
                    Message msg = Message.obtain();
                    msg.what = LOCATEMESSAGE;
                    msg.obj = null;
                    playHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    private void drawLine(){
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        for(TrackPoint tp:trackDataList){
            points.add(tp.point);
        }
        //构建用户绘制多边形的Option对象
        OverlayOptions polylineOption = new PolylineOptions()
                .points(points)
                .width(5)
                .color(0xAA00FF00);
        //在地图上添加多边形Option，用于显示
        tracksOverlay = mBaiduMap.addOverlay(polylineOption);
    }

    private class PlayRecordThread extends Thread{
        public  boolean PAUSE = true;
        int periodMilli = 1000;
        boolean isTimeToDie = false;

        public PlayRecordThread(int period){
            periodMilli = period;
        }

        @Override
        public void run() {
            super.run();
            while(!isTimeToDie) {
                for (TrackPoint pt : trackDataList) {
                    if(isTimeToDie) return;
                    //暂停播放
                    synchronized (PlayRecordThread.class) {
                        while (PAUSE) ;
                    }
                    //向主线程发出消息，移动地图中心点
                    Message msg = Message.obtain();
                    msg.what = CHANGEPOINT;
                    msg.obj = pt.point;
                    playHandler.sendMessage(msg);

                    try {
                        //TODO::periodMilli可变，更改速度
                        synchronized (PlayRecordThread.class) {
                            sleep(periodMilli);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //每次循环结束，检查列表（可能被主线程清空）
                    if(trackDataList.isEmpty()) return;
                }
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
                case LOCATEMESSAGE:{
                    if(msg.obj!= null){
                        locateMobile((LatLng) msg.obj);
                        break;
                    }else{
                        Toast.makeText(getActivity().getApplicationContext(),
                                "定位数据获取失败，请重试或检查网络",Toast.LENGTH_LONG).show();
                    }

                }

            }
        }
    };

    private void pausePlay(){
        if(m_playThread!= null)
            m_playThread.PAUSE = true;
    };

    private void continuePlay(){
            m_playThread.PAUSE = false;
    };
}
