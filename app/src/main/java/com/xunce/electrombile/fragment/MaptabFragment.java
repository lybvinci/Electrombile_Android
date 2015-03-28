package com.xunce.electrombile.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.xunce.electrombile.R;

import java.util.Timer;
import java.util.TimerTask;

public class MaptabFragment extends Fragment {

    private static String TAG = "MaptabFragment:";
    public static MapView mMapView = null;
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
        return view;
    }


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		((TextView)getView().findViewById(R.id.tvTop)).setText("地图");
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
