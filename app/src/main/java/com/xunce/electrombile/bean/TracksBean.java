package com.xunce.electrombile.bean;

import com.xunce.electrombile.manager.TracksManager.TrackPoint;

import java.util.ArrayList;

/**
 * Created by heyukun on 2015/4/27.
 */
public class TracksBean {
    public static TracksBean _tracksBean;
    public ArrayList<ArrayList<TrackPoint>> tracksData;

    private TracksBean() {
        tracksData = new ArrayList<ArrayList<TrackPoint>>();
    }

    public static TracksBean getInstance() {
        if (_tracksBean != null) {
            return _tracksBean;
        }else{
            _tracksBean = new TracksBean();
            return _tracksBean;
        }
    }
    public  ArrayList<ArrayList<TrackPoint>> getTracksData(){
        return tracksData;
    }

    public void setTracksData(ArrayList<ArrayList<TrackPoint>> data){
        tracksData = data;
    }
}
