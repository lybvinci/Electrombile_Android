package com.xunce.electrombile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;

import com.xunce.electrombile.Base.utils.StringUtils;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.account.LoginActivity;
import com.xunce.electrombile.xpg.common.system.IntentUtils;
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
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //判断是否有账号登陆
                    if (StringUtils.isEmpty(setManager.getUserName())) {
                        //未有账号登陆
                        IntentUtils.getInstance().startActivity(SplashActivity.this,LoginActivity.class);
                        SplashActivity.this.finish();
                    } else {
                        //有账号登陆
                        Intent intent = new Intent(SplashActivity.this, FragmentActivity.class);
                        startActivity(intent);
                        SplashActivity.this.finish();
                    }
                }
            }, 1000);
        }
    }

}
