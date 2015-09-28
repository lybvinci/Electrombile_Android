package com.xunce.electrombile.protocol;

/**
 * Created by lybvinci on 2015/9/28.
 */
public interface ProtocolFactoryInterface {
    Protocol createProtocol(String jsonString);
}
