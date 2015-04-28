package com.xunce.electrombile.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xunce.electrombile.R;

/**
 * Created by heyukun on 2015/4/24.
 */
public class HelpActivity extends Activity{

    Button returnBtn;
    Button feadbackBtn;
    TextView tv_appInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        returnBtn = (Button)findViewById(R.id.btn_returnFromFelp);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        feadbackBtn = (Button)findViewById(R.id.btn_feadBack);
        feadbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "安全宝客户端 - 信息反馈");
                    intent.putExtra(Intent.EXTRA_TEXT, "我的建议：");
                    intent.setData(Uri.parse("mailto:support@huakexunce.com"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "发送成功，谢谢您的反馈", Toast.LENGTH_SHORT).show();
                }
            }
        });
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_ACTIVITIES);
            tv_appInfo = (TextView)findViewById(R.id.tv_appInfo);
            tv_appInfo.setText("安全宝 V" + pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
