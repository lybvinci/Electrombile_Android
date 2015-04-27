package com.xunce.electrombile.Base.utils;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import com.avos.avoscloud.AVObject;
import com.baidu.mapapi.model.LatLng;
import com.xunce.electrombile.Base.sdk.CmdCenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by heyukun on 2015/4/22.
 */
public class TracksManager {
    //private ArrayList<ArrayList<LatLng>> tracks;
    private final String TAG = "TracksManager";
    private  ArrayList<ArrayList<TrackPoint>> tracks;

    private final String KET_TIME = "createdAt";
    private final String KET_LONG = "lon";
    private final String KET_LAT = "lat";
    private final long MAX_TIMEINRVAL = 10 * 60;//十分钟
    private CmdCenter mCenter;

    public  static class TrackPoint {
        public Date time;
        public LatLng point;
        public TrackPoint(Date t, LatLng p){
            time = t;
            point = p;
        }
        public TrackPoint(Date t, double lat, double lon){
            time = t;
            point = new LatLng(lat, lon);
        }
    }
    public TracksManager(Context context){
//
//        tracks.add(dts);
        tracks = new ArrayList<ArrayList<TrackPoint>>();
        mCenter = CmdCenter.getInstance(context);
    }

    public ArrayList<TrackPoint> getTrack(int position){
        return tracks.get(position);
    }

    public void setTracksData(ArrayList<ArrayList<TrackPoint>> data){
        tracks = data;
    }

    public  ArrayList<ArrayList<TrackPoint>> getTracks(){
        return tracks;
    }

    public void clearTracks(){
        tracks.clear();
    }

    public boolean isOutOfHubei(LatLng point){
            return !((point.longitude > 108) && (point.longitude < 116) && (point.latitude > 29) && (point.latitude < 33));
    }

    public void setTranks(List<AVObject> objects){
        Log.i("Track managet-----", "setTranks" + objects.size());
        if(objects == null) return;
        AVObject lastSavedObject = null;
        ArrayList<TrackPoint> dataList = new ArrayList<TrackPoint>();
        for(AVObject thisObject: objects){


            //如果本次循环数据跟上一个已保存的数据坐标相同，则跳过
            if(lastSavedObject != null && (thisObject.getDouble(KET_LAT) == lastSavedObject.getDouble(KET_LAT))
                    && (thisObject.getDouble(KET_LONG) == lastSavedObject.getDouble(KET_LONG))){
//                if(dataList.size() <= 1){
//                    LatLng pTmp = mCenter.convertPoint(new LatLng(mCenter.parseGPSData((float)thisObject.getDouble(KET_LAT)), mCenter.parseGPSData((float)thisObject.getDouble(KET_LONG))));
//                    dataList.add(new TrackPoint(thisObject.getCreatedAt(), pTmp));
//                }
                Log.i("******", thisObject.getDouble(KET_LAT) + "==?" + lastSavedObject.getDouble(KET_LAT));
                continue;
            }

            //如果下一个数据与上一个已保存的数据时间间隔小于MAX_TIMEINRVAL
            if(lastSavedObject != null &&((thisObject.getCreatedAt().getTime() - lastSavedObject.getCreatedAt().getTime()) / 1000 >= MAX_TIMEINRVAL)){
                Log.e("stilllllll point", "");
                if(dataList.size() > 1) {
                    tracks.add(dataList);
                }
                    dataList = new ArrayList<TrackPoint>();
                    lastSavedObject = thisObject;
            }

            //打印当前点信息
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
            Log.i("ddd", "objid:" + thisObject.getObjectId() +
                    "lat:" + thisObject.getDouble(KET_LAT) +
                    "lon" + thisObject.getDouble(KET_LONG) +
                    "time" + sdf.format(thisObject.getCreatedAt().getTime()));

            double lat = thisObject.getDouble(KET_LAT);
            double lon = thisObject.getDouble(KET_LONG);

            //百度地图的LatLng类对输入有限制，如果longitude过大，则会导致结果不正确
            LatLng oldPoint = new LatLng(mCenter.parseGPSData((float)lat), mCenter.parseGPSData((float)lon));
            LatLng bdPoint = mCenter.convertPoint(oldPoint);


            TrackPoint p = new TrackPoint(thisObject.getCreatedAt(), bdPoint);
            if(isOutOfHubei(bdPoint)){
                Log.i(TAG, "out range");
                continue;
            }
            dataList.add(p);
            lastSavedObject = thisObject;

        }

        Log.i(TAG, "tracks size:" + tracks.size());
    }
}
