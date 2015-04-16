package com.xunce.electrombile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.xunce.electrombile.R;
import com.xunce.electrombile.UniversalTool.ToastUtil;

public class ForgetActivity extends Activity implements View.OnClickListener {
    private EditText validation_edt;
    private EditText newPwd;
    private Button validationOk;
    private Button sendValidation;
    private EditText phoneNum;
    private String telNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forget);
        initView();
    }

    private void initView(){
        validation_edt = (EditText)findViewById(R.id.validation_forget);
        newPwd = (EditText) findViewById(R.id.newPwd);
        phoneNum = (EditText)findViewById(R.id.phoneNum);
        validationOk = (Button) findViewById(R.id.validation_ok);
        sendValidation =(Button) findViewById(R.id.validation_send);
        validationOk.setOnClickListener(this);
        sendValidation.setOnClickListener(this);
     }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.validation_ok:
                telNum = phoneNum.getText().toString();
                String validationCode = validation_edt.getText().toString();
                final String newPassword = newPwd.getText().toString();
                if("".equals(telNum) ||
                        "".equals(validationCode)
                        ||telNum.length() != 11
                        ||"".equals(newPassword)){
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.pleaseReview), 1000);
                }else{
                    AVUser.resetPasswordBySmsCodeInBackground(validationCode,newPassword,new UpdatePasswordCallback() {
                        @Override
                        public void done(AVException e) {
                            if( e == null){
                                ToastUtil.showToast(getApplicationContext(), getString(R.string.passwordChangeSuccess), 1000);
                                Intent intent = new Intent(ForgetActivity.this,LoginActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("tel",telNum);
                                bundle.putString("pwd",newPassword);
                                intent.putExtras(bundle);
                                ForgetActivity.this.finish();
                            }else{
                                ToastUtil.showToast(getApplicationContext(), getString(R.string.validatedFailed), 1000);
                                LogUtil.log.i(e.toString());
                            }
                        }
                    });
                }
                break;
            case R.id.validation_send:
                telNum = phoneNum.getText().toString();
                if("".equals(telNum) || telNum.length() != 11){
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.phoneError), 1000);
                }else{
                    AVUser.requestPasswordResetBySmsCodeInBackground(telNum,new RequestMobileCodeCallback() {
                        @Override
                        public void done(AVException e) {
                            if(e == null) {
                                LogUtil.log.i(getString(R.string.sendSuccess));
                                TimeCount time = new TimeCount(120000, 1000);
                                time.start();
                            }else{
                                LogUtil.log.i(e.toString());
                            }
                        }
                    });
                    }
                break;
            default: break;
                }
    }

    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {  //计时过程显示
            sendValidation.setClickable(false);
            sendValidation.setText(l/1000+getString(R.string.second));
        }

        @Override
        public void onFinish() {
            sendValidation.setText(getString(R.string.reValidated));
            sendValidation.setClickable(true);
        }
    }


}
