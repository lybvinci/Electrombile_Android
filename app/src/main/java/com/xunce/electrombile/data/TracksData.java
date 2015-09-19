package com.xunce.electrombile.data;

import com.xunce.electrombile.manager.TracksManager.TrackPoint;
import java.util.ArrayList;

/**
 * Created by heyukun on 2015/4/27.
 */
 public  class TracksData {
    public static TracksData _tracksData;
    public ArrayList<ArrayList<TrackPoint>> tracksData;
    private TracksData(){
        tracksData = new ArrayList<ArrayList<TrackPoint>>();
    }

    public static TracksData getInstance(){
        if(_tracksData != null){
            return _tracksData;
        }else{
            _tracksData = new TracksData();
            return _tracksData;
        }
    }
    public  ArrayList<ArrayList<TrackPoint>> getTracksData(){
        return tracksData;
    }

    public void setTracksData(ArrayList<ArrayList<TrackPoint>> data){
        tracksData = data;
    }
}
