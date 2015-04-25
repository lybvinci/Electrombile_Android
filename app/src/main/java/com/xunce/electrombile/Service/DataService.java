package com.xunce.electrombile.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by heyukun on 2015/4/25.
 */
public class DataService extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
