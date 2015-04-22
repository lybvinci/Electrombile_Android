package com.xunce.electrombile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.account.LoginActivity;

/**
 * Created by pc on 2015/3/28.
 */
public class UserInfoActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinfo);
    }


    public void quitLogin(View view) {
     //   AVUser.logOut();             //清除缓存用户对象
        //  AVUser currentUser = AVUser.getCurrentUser(); // 现在的currentUser是null了
        Intent intent = new Intent(UserInfoActivity.this,LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}
