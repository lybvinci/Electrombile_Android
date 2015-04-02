package com.xunce.electrombile.Base;

import android.app.Application;
import android.util.Log;

import com.avos.avoscloud.AVObject;
import com.baidu.mapapi.SDKInitializer;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVAnalytics;


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
        AVOSCloud.initialize(this, "gqd0m4ytyttvluk1tnn0unlvmdg8h4gxsa2ga159nwp85fks",
                "7gd2zom3ht3vx6jkcmaamm1p2pkrn8hdye2pn4qjcwux1hl1");
        Log.e("", "has application");
    }

    private void initBaiduSDK() {
        SDKInitializer.initialize(this);
    }
}
