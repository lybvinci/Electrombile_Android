package com.xunce.electrombile.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xunce.electrombile.Base.config.Configs;
import com.xunce.electrombile.R;
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;

import java.util.List;

public class BindingActivity extends BaseActivity implements View.OnClickListener {
    private Button bind_btn;
    private TextView equipment_info;
    private TextView jump_bind;

    //passCode
    private EditText et_passCode;

    //did
    private EditText et_did;

    //bindSuccess
    private Button bindSuccess;

    //String
    private String did;
    private String passcode;
    /** The progress dialog. */
    private ProgressDialog progressDialog;

    private enum handler_key{
        START_BIND,
        SUCCESS,
        FAILED,
        LOGIN,
        GET_LIST,
    }
    //自动重登的次数
    private int times = 0;
//绑定步骤：
    /*
    * 1.先 获取passcede 和 did  进行startBind
    * 2.接着回调 didBindDevice 如果成功getBoundDevices函数，进行搜索
    * 3.接着回调 didDiscovered 如果成功，会获取一个设备列表
    * 4.接着调用 loginDevice 登陆设备，如果成功 会回调 didlogin函数。
    * 5.接着跳转界面
    */
    //handler
    Handler mHandler = new Handler(){
      public void handleMessage(Message msg){
          super.handleMessage(msg);
          handler_key key = handler_key.values()[msg.what];
          switch (key){
              case GET_LIST:
                  mCenter.getXPGWifiSDK().getBoundDevices(setManager.getUid(),setManager.getToken(), Configs.PRODUCT_KEY);
                  break;
              case LOGIN:
                  loginDevice();
                  break;
              case START_BIND:
                  progressDialog.show();
                  startBind(passcode, did);
                  break;
              case SUCCESS:
               //   ToastUtils.showShort(BindingActivity.this, "添加成功");
                  ToastUtils.showShort(BindingActivity.this, "设备登陆成功");
                  setManager.setPassCode(passcode);
                  progressDialog.cancel();
                  Intent intent = new Intent(BindingActivity.this,FragmentActivity.class);
                  startActivity(intent);
                  finish();
                  break;
              case FAILED:
                  times = 0;
                  bind_btn.setVisibility(View.INVISIBLE);
                  ToastUtils.showShort(BindingActivity.this, "添加失败，请返回重试");
                  progressDialog.cancel();
                  break;
          }
      }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.bindEquipment));
        setContentView(R.layout.activity_binding);
        initView();
    }

    private void initView(){
        bind_btn = (Button) findViewById(R.id.bind_btn);
        equipment_info = (TextView) findViewById(R.id.equipment_info);
        jump_bind = (TextView) findViewById(R.id.jump_bind);

        et_did = (EditText) findViewById(R.id.et_did);
        et_passCode = (EditText) findViewById(R.id.et_passCode);

        bindSuccess = (Button) findViewById(R.id.bindSuccess);
        jump_bind.setOnClickListener(this);
        bind_btn.setOnClickListener(this);
        bindSuccess.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("连接中，请稍候...");
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
                if(!FragmentActivity.ISSTARTED) {
                    Intent intent2 = new Intent(BindingActivity.this, FragmentActivity.class);
                    startActivity(intent2);
                }
                this.finish();
                break;
            case R.id.bindSuccess:
                if(et_passCode != null && et_did != null){
                    passcode = et_passCode.getText().toString();
                    did = et_did.getText().toString();
                    mHandler.sendEmptyMessage(handler_key.START_BIND.ordinal());
                }
                break;
            default:break;
        }
    }

    private void startBind(final String passcode,final String did){
        mCenter.cBindDevice(setManager.getUid(),setManager.getToken(),did,passcode,"");
    }
    /**
     * 扫描结果处理
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x01 && resultCode == 0x02 && data != null) {
            if (data.getExtras().containsKey("result")) {
                String text = data.getExtras().getString("result");
                if (text.contains("product_key=") & text.contains("did=")
                        && text.contains("passcode=")) {
                    did = getParamFomeUrl(text,"did");
                    passcode = getParamFomeUrl(text,"passcode");
                    Log.i("",did+"#######"+passcode);
                    et_did.setText(did);
                    setManager.setDid(did);
                    et_passCode.setText(passcode);
                    bind_btn.setVisibility(View.INVISIBLE);
                    mHandler.sendEmptyMessage(handler_key.START_BIND.ordinal());
                }
            }
        }else{
            equipment_info.setText(getString(R.string.scannerFailed));
        }
    }

    private String getParamFomeUrl(String url, String param) {
        String product_key = "";
        int startindex = url.indexOf(param + "=");
        startindex += (param.length() + 1);
        String subString = url.substring(startindex);
        int endindex = subString.indexOf("&");
        if (endindex == -1) {
            product_key = subString;
        } else {
            product_key = subString.substring(0, endindex);
        }
        return product_key;
    }

    @Override
    protected void didBindDevice(int error, String errorMessage, String did) {
        Log.d("扫描结果", "error=" + error + ";errorMessage=" + errorMessage
                + ";did=" + did);
        if (error == 0) {
            mHandler.sendEmptyMessage(handler_key.GET_LIST.ordinal());
            setManager.setDid(did);
        } else {
            Message message = new Message();
            message.what = handler_key.FAILED.ordinal();
            message.obj = errorMessage;
            mHandler.sendMessage(message);
        }
    }

    /**
     * 登陆设备
     *            the xpg wifi device
     */
    private void loginDevice() {

        Log.i("绑定设备列表",devicesList.toString());
        for (int i = 0; i < devicesList.size(); i++) {
            XPGWifiDevice device = devicesList.get(i);
            if (device != null && device.getDid().equals(setManager.getDid())) {
                mXpgWifiDevice = device;
                mXpgWifiDevice.setListener(deviceListener);
                mXpgWifiDevice.login(setManager.getUid(), setManager.getToken());
                break;
            }else{
                times++;
                if(times < 5)
                    mHandler.sendEmptyMessage(handler_key.LOGIN.ordinal());
            }

        }
    }

    @Override
    protected void didLogin(XPGWifiDevice device, int result) {
        if (result == 0) {
            mXpgWifiDevice = device;
            Log.i("进入login",device.toString());
            mHandler.sendEmptyMessage(handler_key.SUCCESS.ordinal());
        } else {
            mHandler.sendEmptyMessage(handler_key.FAILED.ordinal());
        }

    }

    @Override
    protected void didDiscovered(int error, List<XPGWifiDevice> devicesList) {
        super.didDiscovered(error, devicesList);
        this.devicesList =  devicesList ;
        Log.i("设备列表",devicesList.toString());
        mHandler.sendEmptyMessage(handler_key.LOGIN.ordinal());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        progressDialog.cancel();
        ToastUtils.showShort(this,"绑定失败！\n请重启app重新绑定设备");
    }
}
