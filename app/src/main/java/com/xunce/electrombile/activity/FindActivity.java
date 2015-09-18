package com.xunce.electrombile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import com.xunce.electrombile.R;
import com.xunce.electrombile.view.RadarView;

import java.util.Random;

public class FindActivity extends Activity {

    private RadarView radarView;
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
    }

    private void initEvent() {
        myThread.start();
    }

    public void startFind(View view) {
        Button button = (Button) view;
        if (!isFinding) {
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
    }
}
