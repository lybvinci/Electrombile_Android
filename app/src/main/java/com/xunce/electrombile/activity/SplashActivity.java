package com.xunce.electrombile.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.avos.avoscloud.AVUser;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.account.LoginActivity;
import com.xunce.electrombile.xpg.common.useful.NetworkUtils;

import im.fir.sdk.FIR;


public class SplashActivity extends BaseActivity {

   // private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
      //  iv = (ImageView) findViewById(R.id.iv);
       // set();
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
                        /*if(currentUser.isMobilePhoneVerified()) {
                            Intent intent = new Intent(SplashActivity.this, FragmentActivity.class);
                            startActivity(intent);
                            SplashActivity.this.finish();
                        }else{
                            currentUser.deleteInBackground(new DeleteCallback() {
                                @Override
                                public void done(AVException e) {
                                    if(e == null){
                                        Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                                        startActivity(intent);
                                        SplashActivity.this.finish();
                                    }
                                }
                            });
                        }*/
                        FIR.addCustomizeValue("user", currentUser.getUsername());
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

//    public void set(){
//        AnimationSet animationSet = new AnimationSet(false);
//        RotateAnimation rotateAnimation = new RotateAnimation(0.0f,360.0f,
//                Animation.RELATIVE_TO_SELF,0.5f,
//                Animation.RELATIVE_TO_SELF,0.5f);
//        rotateAnimation.setDuration(1500);
//        rotateAnimation.setRepeatCount(1);
//        rotateAnimation.setRepeatMode(Animation.RESTART);
//        ScaleAnimation scaleAnimation = new ScaleAnimation(0.1f,2.0f,0.1f,2.0f,
//                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//        scaleAnimation.setDuration(1500);
//        scaleAnimation.setRepeatCount(1);
//        scaleAnimation.setRepeatMode(Animation.RESTART);
//        animationSet.addAnimation(scaleAnimation);
//        animationSet.addAnimation(rotateAnimation);
//        iv.startAnimation(animationSet);
//
//    }


}
