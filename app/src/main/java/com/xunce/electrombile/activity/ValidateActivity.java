package com.xunce.electrombile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.xunce.electrombile.R;

public class ValidateActivity extends Activity implements View.OnClickListener {

    private EditText validation;
    private Button get_validation;
    private Button Ok;
    private String tel;
    private String pwd;
    private int jishu = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("验证");
        setContentView(R.layout.activity_validate);
        initView();
    }
    private void initView(){
        validation = (EditText) findViewById(R.id.validation_edt);
        get_validation = (Button) findViewById(R.id.sendValidation);
        Ok = (Button) findViewById(R.id.validationOk);
        get_validation.setOnClickListener(this);
        Ok.setOnClickListener(this);
        Intent intent = getIntent();
        tel = intent.getStringExtra("tel");
        pwd = intent.getStringExtra("pwd");
        LogUtil.log.i(tel + pwd);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_validate, menu);
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

    private class TimeCount extends CountDownTimer{

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {  //计时过程显示
            get_validation.setClickable(false);
            get_validation.setText(l/1000+"秒");
        }

        @Override
        public void onFinish() {
            get_validation.setText("重新验证");
            get_validation.setClickable(true);
        }
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.sendValidation:
                // do get validation
                if(jishu == 0) {
                    jishu++;
                    AVUser user = new AVUser();
                    user.setUsername(tel);
                    user.setPassword(pwd);
                    user.setMobilePhoneNumber(tel);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                LogUtil.log.i("注册成功");
                                Toast.makeText(getApplicationContext(),
                                        "注册成功",
                                        Toast.LENGTH_SHORT)
                                        .show();
                                Ok.setClickable(true);
                                TimeCount time = new TimeCount(120000, 1000);
                                time.start();
                            } else {
                                LogUtil.log.i(e.toString());
                                LogUtil.log.i("手机号已注册");
                                Toast.makeText(getApplicationContext(),
                                        "手机号已注册",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
                }else{
                    AVUser.requestMobilePhoneVerifyInBackground(tel,new RequestMobileCodeCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                LogUtil.log.i("第二次发送");
                                Ok.setClickable(true);
                                TimeCount time = new TimeCount(120000, 1000);
                                time.start();
                            } else {
                                LogUtil.log.i(e.toString());
                                jishu = 0;
                            }
                        }
                    });
                }
                break;
            case R.id.validationOk:
                // do register
                String smsCode = validation.getText().toString();
                if("".equals(smsCode)){
                    Toast.makeText(getApplicationContext(),
                            "验证码不能为空",
                            Toast.LENGTH_SHORT)
                            .show();
                }else{
                    AVUser.verifyMobilePhoneInBackground(smsCode,new AVMobilePhoneVerifyCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e == null) {
                                LogUtil.log.i("验证成功");
                                Toast.makeText(getApplicationContext(),
                                        "验证成功",
                                        Toast.LENGTH_SHORT)
                                        .show();
                                Ok.setClickable(false);
                            }
                            else{
                                Toast.makeText(getApplicationContext(),
                                        "验证码错误",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
                }
                break;
            default:break;
        }
    }
}
