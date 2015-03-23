package com.xunce.electrombile.Base;

import android.app.Application;
import com.baidu.mapapi.SDKInitializer;

/**
 * Created by jk on 2015/3/23.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initBaiduSDK();
    }

    private void initBaiduSDK() {
        SDKInitializer.initialize(this);
    }
}
