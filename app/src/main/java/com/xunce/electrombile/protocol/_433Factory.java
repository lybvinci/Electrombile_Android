package com.xunce.electrombile.protocol;

/**
 * Created by lybvinci on 2015/9/28.
 */
public class _433Factory implements ProtocolFactoryInterface {

    @Override
    public Protocol createProtocol(String jsonString) {
        return new _433Protocol(jsonString);
    }
}
