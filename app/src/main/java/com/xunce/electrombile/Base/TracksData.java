package com.xunce.electrombile.Base;

import com.xunce.electrombile.Base.utils.TracksManager.TrackPoint;
import java.util.ArrayList;

/**
 * Created by heyukun on 2015/4/27.
 */
 public  class TracksData {
    private TracksData(){
        tracksData = new ArrayList<ArrayList<TrackPoint>>();
    }
    public ArrayList<ArrayList<TrackPoint>> tracksData;
    public static TracksData _tracksData;
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
