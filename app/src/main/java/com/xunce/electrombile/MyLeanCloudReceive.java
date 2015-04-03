package com.xunce.electrombile;

import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.LogUtil;
import com.avos.avospush.notification.NotificationCompat;
import com.xunce.electrombile.activity.AlarmActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by heyukun on 2015/4/2.
 */
public class MyLeanCloudReceive extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        LogUtil.log.d(TAG, "Get Broadcat");
//        try {
//            String action = intent.getAction();
//            String channel = intent.getExtras().getString("com.avos.avoscloud.Channel");
//            JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
//
//            Log.d(TAG, "got action " + action + " on channel " + channel + " with:");
//            Iterator itr = json.keys();
//            while (itr.hasNext()) {
//                String key = (String) itr.next();
//                Log.d(TAG, "..." + key + " => " + json.getString(key));
//            }
//        } catch (JSONException e) {
//            Log.d(TAG, "JSONException: " + e.getMessage());
//        }
//    }
@Override
public void onReceive(Context context, Intent intent) {
    try {
        if (intent.getAction().equals("com.xunce.electrombile.push.action")) {
            //获取消息内容
            JSONObject json = new JSONObject(intent.getExtras().getString("com.avos.avoscloud.Data"));
            final String message = json.getString("alert");
//            Intent resultIntent = new Intent(AVOSCloud.applicationContext, FragmentActivity.class);
//            PendingIntent pendingIntent =
//                    PendingIntent.getActivity(AVOSCloud.applicationContext, 0, resultIntent,
//                            PendingIntent.FLAG_UPDATE_CURRENT);
//            NotificationCompat.Builder mBuilder =
//                    new NotificationCompat.Builder(AVOSCloud.applicationContext)
//                            .setSmallIcon(R.drawable.notification)
//                            .setContentTitle(
//                                    AVOSCloud.applicationContext.getResources().getString(R.string.app_name))
//                            .setContentText(message)
//                            .setTicker(message);
//            mBuilder.setContentIntent(pendingIntent);
//            mBuilder.setAutoCancel(true);
//
//            int mNotificationId = 10086;
//            NotificationManager mNotifyMgr =
//                    (NotificationManager) AVOSCloud.applicationContext
//                            .getSystemService(
//                                    Context.NOTIFICATION_SERVICE);
//            mNotifyMgr.notify(mNotificationId, mBuilder.build());


            //启动警报界面
            Intent intent2 = new Intent(context,AlarmActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);

        }
    } catch (Exception e) {

    }
}
}