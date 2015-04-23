package com.xunce.electrombile.Base.utils;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyukun on 2015/4/22.
 */
public class TracksManager {
    private ArrayList<ArrayList<LatLng>> tracks;

    public TracksManager(){
        tracks = new ArrayList<ArrayList<LatLng>>();
        ArrayList<LatLng> dts = new ArrayList<LatLng>();
        dts.add(new LatLng(30.5171, 114.4392));
        dts.add(new LatLng(30.1272, 114.5493));
        dts.add(new LatLng(30.6373, 114.6394));
        dts.add(new LatLng(30.0474, 114.7395));
        dts.add(new LatLng(30.7575, 114.8396));

        tracks.add(dts);
    }

    public ArrayList<LatLng> getTrack(int position){
        return tracks.get(position);
    }
}
