package com.xunce.electrombile.protocol;

/**
 * Created by lybvinci on 2015/9/28.
 */
public class CmdFactory implements ProtocolFactoryInterface {

    @Override
    public Protocol createProtocol(String jsonString) {
        return new CmdProtocol(jsonString);
    }
}
