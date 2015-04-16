package com.xunce.electrombile.Base;

import android.app.Application;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.avos.avoscloud.AVOSCloud;


/**
 * Created by jk on 2015/3/23.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //initial the Baidu map SDK
        initBaiduSDK();

        //initial LeanCloud SDK
        AVOSCloud.initialize(this, "5wk8ccseci7lnss55xfxdgj9xn77hxg3rppsu16o83fydjjn",
                "yovqy5zy16og43zwew8i6qmtkp2y6r9b18zerha0fqi5dqsw");
        Log.e("", "has application");
    }

    private void initBaiduSDK() {
        SDKInitializer.initialize(this);
    }
}
