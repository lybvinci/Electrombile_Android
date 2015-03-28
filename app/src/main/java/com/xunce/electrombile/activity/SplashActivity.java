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
import com.avos.avoscloud.AVAnalytics;


import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil;
import com.xunce.electrombile.R;

import android.os.Handler;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVOSCloud.initialize(this,
                "5wk8ccseci7lnss55xfxdgj9xn77hxg3rppsu16o83fydjjn",
                "yovqy5zy16og43zwew8i6qmtkp2y6r9b18zerha0fqi5dqsw");
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Context context = this;
        LogUtil.log.i(""+isNetWorkAvailable(this));
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
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            context.startActivity(intent);
                            SplashActivity.this.finish();
                        }
                    }).show();
            // ToastUtil.showToast(this,getString(R.string.networkError),1000);
        }else {
            AVAnalytics.trackAppOpened(getIntent());
            final AVUser currentUser = AVUser.getCurrentUser();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (currentUser != null) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
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

    @Override
    protected void onResume() {
        super.onResume();
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
                    LogUtil.log.i(networkInfo[i].getState().toString());
                    LogUtil.log.i(networkInfo[i].getTypeName());
                    if(networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
