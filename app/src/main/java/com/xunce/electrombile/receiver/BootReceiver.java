package com.xunce.electrombile.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xunce.electrombile.service.GPSDataService;

/**
 * Created by lybvinci on 2015/5/1.
 */
public class BootReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent mBootIntent = new Intent(context, GPSDataService.class);
        context.startService(mBootIntent);
    }
}