package com.xunce.electrombile.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xunce.electrombile.R;


public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText telNumber;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView(){
        telNumber = (EditText) findViewById(R.id.telnumber_edt);
        password = (EditText) findViewById(R.id.regi_password);
        Button registerOk = (Button) findViewById(R.id.regiOk_btn);
        TextView login_register = (TextView) findViewById(R.id.login_regi);
        registerOk.setOnClickListener(this);
        login_register.setOnClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.regiOk_btn:
                //do register sucess
                String tel = telNumber.getText().toString();
                String pwd = password.getText().toString();
                if(tel.length() != 11){
                    Toast.makeText(getApplicationContext(),
                            "手机号码格式错误",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                else if("".equals(tel) || "".equals(pwd)){
                    Toast.makeText(getApplicationContext(),
                            "用户名和密码不能为空",
                            Toast.LENGTH_SHORT)
                            .show();
                }else{
                    Intent intent = new Intent(RegisterActivity.this,ValidateActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("tel",tel);
                    bundle.putString("pwd",pwd);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                break;
            case R.id.login_regi:
                //do login in
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                this.finish();
                break;
            default:break;
        }
    }
}
