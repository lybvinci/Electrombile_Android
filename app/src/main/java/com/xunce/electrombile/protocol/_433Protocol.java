package com.xunce.electrombile.protocol;

/**
 * Created by lybvinci on 2015/9/28.
 */
public class _433Protocol extends Protocol {
    protected int timestamp;
    protected int intensity;

    public _433Protocol(String tmp) {
        super(tmp);
    }

    @Override
    public int getTimestamp() {
        timestamp = Integer.parseInt(keyForValue(JsonKeys.TIMESTAMP));
        return timestamp;
    }

    @Override
    public int getIntensity() {
        intensity = Integer.parseInt(keyForValue(JsonKeys.INTENSITY));
        return intensity;
    }

}
