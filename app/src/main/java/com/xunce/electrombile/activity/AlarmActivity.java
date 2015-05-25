package com.xunce.electrombile.activity;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.xunce.electrombile.R;
import com.xunce.electrombile.UniversalTool.VibratorUtil;


/**
 * Created by heyukun on 2015/4/3.
 */
public class AlarmActivity extends Activity{
    ToggleButton btnWarmComfirm = null;
    AudioManager aManager = null;
    MediaPlayer mPlayer;
    public static AlarmActivity instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        VibratorUtil.Vibrate(this,60000);

        //播放警铃
        mPlayer= MediaPlayer.create(getApplicationContext(), R.raw.alarm);
        mPlayer.setLooping(true);
        mPlayer.start();

        btnWarmComfirm = (ToggleButton) findViewById(R.id.btn_warning_confirm);
        btnWarmComfirm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    //stop alarm
                    VibratorUtil.VibrateCancle(AlarmActivity.this);
                    mPlayer.stop();
                    AlarmActivity.this.finish();
                    AlarmActivity.instance = null;
                }
            }
        });
        instance = this;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mPlayer.stop();
        VibratorUtil.VibrateCancle(AlarmActivity.this);
        AlarmActivity.this.finish();
    }
}
