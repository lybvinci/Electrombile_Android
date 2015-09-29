package com.xunce.electrombile.protocol;

/**
 * Created by lybvinci on 2015/9/28.
 */
public class GPSProtocol extends Protocol {
    protected int timestamp;
    protected float lat;
    protected float lng;

    public GPSProtocol(String tmp) {
        super(tmp);
    }

    @Override
    public float getLng() {
        lng = Float.parseFloat(keyForValue(JsonKeys.LNG));
        return lng;
    }

    @Override
    public int getTimestamp() {
        timestamp = Integer.parseInt(keyForValue(JsonKeys.TIMESTAMP));
        return timestamp;
    }

    @Override
    public float getLat() {
        lat = Float.parseFloat(keyForValue(JsonKeys.LAT));
        return lat;
    }


}
