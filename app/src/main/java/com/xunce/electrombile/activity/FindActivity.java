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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.xunce.electrombile.R;
import com.xunce.electrombile.manager.CmdCenter;


public class FindActivity extends Activity {

    private static final String TAG = "FindActivity";
    private boolean isFinding = false;
    private RatingBar ratingBar;
    Handler refreshRatingBar = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            float next = (float) msg.obj;
            Log.e("", "next:" + next);
            ratingBar.setRating(next);
        }
    };
    private ImageView scanner;
    private ProgressDialog progressDialog;
    private CmdCenter mCenter;
    private MyReceiver receiver;
    private byte firstByteSearch = 0x00;
    private byte secondByteSearch = 0x00;
    private byte firstByteStop = 0x00;
    private byte secondByteStop = 0x00;
    private Animation operatingAnim;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        initView();
        initEvent();
    }

    private void initView() {
        scanner = (ImageView) findViewById(R.id.iv_scanner);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        progressDialog = new ProgressDialog(this);
        mCenter = CmdCenter.getInstance(this);
    }

    private void initEvent() {
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
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
            if (operatingAnim != null) {
                scanner.startAnimation(operatingAnim);
            }
            button.setText("停止找车");
        } else {
            if (FragmentActivity.pushService != null) {
                byte[] serial = mCenter.getSerial(firstByteStop, secondByteStop);
                FragmentActivity.pushService.sendMessage1(mCenter.cStopFindEle(serial));
                progressDialog.show();
            }
            //radarView.stop();
            scanner.clearAnimation();
            button.setText("开始找车");
        }
        isFinding = !isFinding;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    //取消等待框，并且刷新界面
    private void cancelDialog(int data) {
        progressDialog.dismiss();
        float rating = (float) (data / 200.0);
        ratingBar.setRating(rating);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "find接收调用");
            Bundle bundle = intent.getExtras();
            int data = bundle.getInt("data");
            Log.i(TAG, data + "");
            cancelDialog(data);
        }
    }


}
