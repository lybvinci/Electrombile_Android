package com.xunce.electrombile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.xunce.electrombile.R;
import com.xunce.electrombile.universalTool.ToastUtil;


public class LoginActivity extends Activity implements View.OnClickListener {

    EditText username;
    EditText password;
    TextView findPassword;
    TextView register;
    Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        Intent intent = getIntent();
        if(intent != null) {
            String tel = intent.getStringExtra("tel");
            String pwd = intent.getStringExtra("pwd");
            username.setText(tel);
            password.setText(pwd);
        }
    }

    private void initView(){
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
        findPassword = (TextView) findViewById(R.id.btn_findpwd);
        register = (TextView) findViewById(R.id.btn_register);
        login = (Button) findViewById(R.id.btn_login);
        login.setOnClickListener(this);
        findPassword.setOnClickListener(this);
        register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_login:
                //do
                String login_username = username.getText().toString();
                String login_pwd = password.getText().toString();
                if("".equals(login_username) || "".equals(login_pwd)){
                    ToastUtil.showToast(getApplicationContext(), "用户名密码不能为空", 1000);
                }
                else{
                    AVUser.logInInBackground(login_username, login_pwd, new LogInCallback<AVUser>() {
                        @Override
                        public void done(AVUser avUser, AVException e) {
                            if(avUser != null){
                                Intent intent = new Intent(LoginActivity.this,FragmentActivity.class);
                                startActivity(intent);
                                LoginActivity.this.finish();
                                ToastUtil.showToast(getApplicationContext(), "登陆成功", 1000);
                            }else{
                                ToastUtil.showToast(getApplicationContext(), "用户名或密码错误", 1000);
                            }
                        }
                    });
                }
                break;
            case R.id.btn_findpwd:
                //do intent another activity
                Intent intent1 = new Intent(LoginActivity.this,ForgetActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_register:
                //do intent another activity
                Intent intent2 = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent2);
                this.finish();
                break;
            default:break;

        }
    }
}
