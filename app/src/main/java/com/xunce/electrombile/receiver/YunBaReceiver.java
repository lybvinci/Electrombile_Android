package com.xunce.electrombile.receiver;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.xunce.electrombile.Base.sdk.SettingManager;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.AlarmActivity;
import com.xunce.electrombile.activity.FragmentActivity;
import com.xunce.electrombile.xpg.common.device.DeviceUtils;
import com.xunce.electrombile.xpg.common.useful.JSONUtils;

import java.util.Random;

import io.yunba.android.manager.YunBaManager;

/**
 * Created by lybvinci on 2015/5/1.
 */
public class YunBaReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        String topic = intent.getStringExtra(YunBaManager.MQTT_TOPIC);
        String msg = intent.getStringExtra(YunBaManager.MQTT_MSG);
        SettingManager setManager = new SettingManager(context);
        //在这里处理从服务器发布下来的消息， 比如显示通知栏， 打开 Activity 等等
        DeviceUtils.showNotifation(context, topic, msg);
        DeviceUtils.wakeUpAndUnlock(context);
        Intent intentMy = new Intent(context, AlarmActivity.class);
        intentMy.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentMy);
        setManager.setAlarmFlag(false);

        //        StringBuilder showMsg = new StringBuilder();
//        showMsg.append("Received message from server: ")
//                .append(YunBaManager.MQTT_TOPIC)
//                .append(" = ")
//                .append(topic)
//                .append(" ")
//                .append(YunBaManager.MQTT_MSG)
//                .append(" = ")
//                .append(msg);
    }


}