package com.xunce.electrombile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.xunce.electrombile.R;
import com.xunce.electrombile.universalTool.ToastUtil;

public class ValidateActivity extends Activity implements View.OnClickListener {

    private EditText validation;
    private Button get_validation;
    private Button Ok;
    private String tel;
    private String pwd;
    private int jiShu = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.validation));
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

    private class TimeCount extends CountDownTimer{

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {  //计时过程显示
            get_validation.setClickable(false);
            get_validation.setText(l/1000+getString(R.string.second));
        }

        @Override
        public void onFinish() {
            get_validation.setText(getString(R.string.reValidated));
            get_validation.setClickable(true);
        }
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.sendValidation:
                // do get validation
                if(jiShu == 0) {
                    jiShu++;
                    AVUser user = new AVUser();
                    user.setUsername(tel);
                    user.setPassword(pwd);
                    user.setMobilePhoneNumber(tel);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                LogUtil.log.i(getString(R.string.registerSuccess));
                                ToastUtil.showToast(getApplicationContext(), getString(R.string.registerSuccess), 1000);
                                Ok.setClickable(true);
                                TimeCount time = new TimeCount(120000, 1000);
                                time.start();
                            } else {
                                LogUtil.log.i(e.toString());
                                LogUtil.log.i(getString(R.string.phoneNumberIsRegistered));
                                ToastUtil.showToast(getApplicationContext(), getString(R.string.phoneNumberIsRegistered), 1000);
                            }
                        }
                    });
                }else{
                    AVUser.requestMobilePhoneVerifyInBackground(tel,new RequestMobileCodeCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                LogUtil.log.i(getString(R.string.sendAgain));
                                Ok.setClickable(true);
                                TimeCount time = new TimeCount(120000, 1000);
                                time.start();
                            } else {
                                LogUtil.log.i(e.toString());
                                jiShu = 0;
                            }
                        }
                    });
                }
                break;
            case R.id.validationOk:
                // do register
                String smsCode = validation.getText().toString();
                if("".equals(smsCode)){
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.validateCodeNull), 1000);
                }else{
                    AVUser.verifyMobilePhoneInBackground(smsCode,new AVMobilePhoneVerifyCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e == null) {
                                LogUtil.log.i(getString(R.string.validateSuccess));
                                ToastUtil.showToast(getApplicationContext(), getString(R.string.validateSuccess), 1000);
                                Ok.setClickable(false);
                                Intent intent = new Intent(ValidateActivity.this,BindingActivity.class);
                                startActivity(intent);
                                ValidateActivity.this.finish();
                            }else if(AVException.CONNECTION_FAILED == e.getCode()){
                                ToastUtil.showToast(getApplicationContext(), getString(R.string.networkError), 1000);
                            }
                            else{
                                ToastUtil.showToast(getApplicationContext(), getString(R.string.validatedFailed), 1000);
                            }
                        }
                    });
                }
                break;
            default:break;
        }
    }
}
