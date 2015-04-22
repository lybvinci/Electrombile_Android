package com.xunce.electrombile.Base;

import android.app.Application;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.xtremeprog.xpgconnect.XPGWifiSDK;
import com.xunce.electrombile.Base.config.Configs;


/**
 * Created by jk on 2015/3/23.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //initial the Baidu map SDK
        initBaiduSDK();
        XPGWifiSDK.sharedInstance().startWithAppID(getApplicationContext(),
                Configs.APPID);
        // 设定日志打印级别,日志保存文件名，是否在后台打印数据.
        XPGWifiSDK.sharedInstance().setLogLevel(Configs.LOG_LEVEL,
                "BassApp.log", Configs.DEBUG);

    }

    private void initBaiduSDK() {
        SDKInitializer.initialize(this);
    }
}
