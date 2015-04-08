package com.xunce.electrombile.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xunce.electrombile.R;

import java.util.List;

public class BindingActivity extends Activity implements View.OnClickListener {
    private Button bind_btn;
    private TextView equipment_info;
    private TextView jump_bind;
    private Button bindSuccess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("绑定设备");
        setContentView(R.layout.activity_binding);
        initView();
    }

    private void initView(){
        bind_btn = (Button) findViewById(R.id.bind_btn);
        equipment_info = (TextView) findViewById(R.id.equipment_info);
        jump_bind = (TextView) findViewById(R.id.jump_bind);
        bindSuccess = (Button) findViewById(R.id.bindSuccess);
        jump_bind.setOnClickListener(this);
        bind_btn.setOnClickListener(this);
        bindSuccess.setOnClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_binding, menu);
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
            case R.id.bind_btn:
                Intent intent1 = new Intent(BindingActivity.this,ScannerActivity.class);
                startActivityForResult(intent1, 0x01);
                break;
            case R.id.jump_bind:
                //第一次登陆
                if(FragmentActivity.ISSTARTED == false) {
                    Intent intent2 = new Intent(BindingActivity.this, FragmentActivity.class);
                    startActivity(intent2);
                }
                this.finish();
                break;
            case R.id.bindSuccess:
                Intent intent3 = new Intent(BindingActivity.this,MainActivity.class);
                startActivity(intent3);
                this.finish();
                break;
            default:break;
        }
    }

    /**
     * 扫描结果处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x01 && resultCode == 0x02 && data != null) {
            if (data.getExtras().containsKey("result")) {
                equipment_info.setText(data.getExtras().getString("result"));
                bind_btn.setVisibility(View.INVISIBLE);
                bindSuccess.setVisibility(View.VISIBLE);
            }
        }else{
            equipment_info.setText("扫描失败");
        }
    }
}
