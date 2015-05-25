package com.xunce.electrombile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.avos.avoscloud.AVUser;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.account.LoginActivity;
import com.xunce.electrombile.xpg.common.useful.NetworkUtils;
import android.os.Handler;


public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Context context = this;
        if(!NetworkUtils.isNetworkConnected(this)){
            NetworkUtils.networkDialogNoCancel(context);
        }else {
            final AVUser currentUser = AVUser.getCurrentUser();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (currentUser != null) {
                        Intent intent = new Intent(SplashActivity.this, FragmentActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    }
                }
            }, 2000);
        }
    }

}
