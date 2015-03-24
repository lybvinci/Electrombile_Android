package com.xunce.electrombile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.xunce.electrombile.R;

public class ForgetActivity extends Activity implements View.OnClickListener {
    private EditText validation_edt;
    private EditText newPwd;
    private Button validationOk;
    private Button sendValidation;
    private EditText phoneNum;
    private TextView inputNewPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("找回密码");
        setContentView(R.layout.activity_forget);
    }

    private void initView(){
        validation_edt = (EditText)findViewById(R.id.validation_edt);
        newPwd = (EditText) findViewById(R.id.newPwd);
        phoneNum = (EditText)findViewById(R.id.phoneNum);
        validationOk = (Button) findViewById(R.id.OkForget_btn);
        sendValidation =(Button) findViewById(R.id.sendValidation);
        inputNewPwd = (TextView)findViewById(R.id.inputNewPwd);
        validationOk.setOnClickListener(this);
        sendValidation.setOnClickListener(this);
     }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.validationOk:

                break;
            case R.id.sendValidation:
                String telNum = phoneNum.getText().toString();
                if("".equals(telNum) || telNum.length() != 11){
                    Toast.makeText(getApplicationContext(),
                            "电话填写错误"
                            ,Toast.LENGTH_SHORT)
                            .show();
                }else{
                    AVOSCloud.requestSMSCodeInBackgroud(telNum, "安全宝", "密码重置", 2, new RequestMobileCodeCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                TimeCount time = new TimeCount(120000, 1000);
                                time.start();

//                                newPwd.setVisibility(View.VISIBLE);
//                                inputNewPwd.setVisibility(View.VISIBLE);
//                                validation_edt.setVisibility(View.INVISIBLE);
//                                sendValidation.setVisibility(View.INVISIBLE);
                            } else {

                            }
                        }
                    });
                }
                break;
            default:
                break;
        }
    }
    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {  //计时过程显示
            sendValidation.setClickable(false);
            sendValidation.setText(l/1000+"秒");
        }

        @Override
        public void onFinish() {
            sendValidation.setText("重新验证");
            sendValidation.setClickable(true);
        }
    }

}
