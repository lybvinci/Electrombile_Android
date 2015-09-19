package com.xunce.electrombile.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.xunce.electrombile.R;
import com.xunce.electrombile.manager.CmdCenter;
import com.xunce.electrombile.view.RadarView;

import java.util.Random;

public class FindActivity extends Activity {

    private static final String TAG = "FindActivity";
    private RadarView radarView;
    private boolean isFinding = false;
    private RatingBar ratingBar;
    private ProgressDialog progressDialog;
    private CmdCenter mCenter;
    private MyReceiver receiver;
    private byte firstByteSearch = 0x00;
    private byte secondByteSearch = 0x00;
    Handler refreshRatingBar = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            float next = (float) msg.obj;
            Log.e("", "next:" + next);
            ratingBar.setRating(next);
        }
    };
    Thread myThread = new Thread() {
        Random rand = new Random();

        @Override
        public void run() {
            while (true) {
                float f = rand.nextFloat() * 10;
                Log.e("", "next:" + f);
                Message msg = Message.obtain();
                msg.obj = f;
                refreshRatingBar.sendMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        initView();
        initEvent();
    }

    private void initView() {
        radarView = (RadarView) findViewById(R.id.radarView);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        progressDialog = new ProgressDialog(this);
        mCenter = CmdCenter.getInstance(this);
    }

    private void initEvent() {
        myThread.start();
        progressDialog.setMessage("正在配置...");
        registerBroadCast();
    }

    private void registerBroadCast() {
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.xunce.electrombile.find");
        try {
            registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startFind(View view) {
        Button button = (Button) view;
        if (!isFinding) {
            if (FragmentActivity.pushService != null) {
                byte[] serial = mCenter.getSerial(firstByteSearch, secondByteSearch);
                FragmentActivity.pushService.sendMessage1(mCenter.cFindEle(serial));
                progressDialog.show();
            }
            radarView.start();
            button.setText("停止找车");
        } else {
            radarView.stop();
            button.setText("开始找车");
        }
        isFinding = !isFinding;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        radarView.destoryThread();
        unregisterReceiver(receiver);
    }

    //取消等待框，并且刷新界面
    private void cancelDialog(String data) {
        progressDialog.dismiss();
        String[] s1 = data.split(":");

    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "find接收调用");
            Bundle bundle = intent.getExtras();
            String data = bundle.getString("data");
            Log.i(TAG, data);
            cancelDialog(data);
        }
    }


}
