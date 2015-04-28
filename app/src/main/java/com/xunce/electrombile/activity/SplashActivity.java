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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.xunce.electrombile.Base.utils.StringUtils;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.account.LoginActivity;
import com.xunce.electrombile.xpg.common.system.IntentUtils;

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
        Log.i("","" + isNetWorkAvailable(this));
        if(!isNetWorkAvailable(this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.networkErrorSet))
                    .setCancelable(false)
                    .setTitle(getString(R.string.networkSet))
                    .setPositiveButton(getString(R.string.networkSettings), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = null;
                            if (Build.VERSION.SDK_INT > 10) {
                                intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                            } else {
                                intent = new Intent();
                                ComponentName componentName = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
                                intent.setComponent(componentName);
                                intent.setAction("android.intent.action.VIEW");
                            }
                            context.startActivity(intent);
                        }
                    })
                    .setNegativeButton(getString(R.string.skip), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(SplashActivity.this, FragmentActivity.class);
                            context.startActivity(intent);
                            SplashActivity.this.finish();
                        }
                    }).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isNetWorkAvailable(Activity activity){
        Context context = activity.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null){
            return false;
        }else{
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if(networkInfo != null && networkInfo.length > 0){
                for(int i=0;i < networkInfo.length;i++){
                    if(networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
