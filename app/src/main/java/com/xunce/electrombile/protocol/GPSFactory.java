package com.xunce.electrombile.protocol;

/**
 * Created by lybvinci on 2015/9/28.
 */
public class GPSFactory implements ProtocolFactoryInterface {

    @Override
    public Protocol createProtocol(String jsonString) {
        return new GPSProtocol(jsonString);
    }
}
