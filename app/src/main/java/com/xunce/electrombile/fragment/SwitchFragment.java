package com.xunce.electrombile.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiDeviceListener;
import com.xtremeprog.xpgconnect.XPGWifiSDKListener;
import com.xunce.electrombile.Base.config.Configs;
import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.Base.sdk.SettingManager;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.BaseActivity;
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;



public class SwitchFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "SwitchFragment:";
    private final int IS_FINISH = 1;
    private boolean systemState = false;
    private boolean alarmState = false;
    private String[] SWITCHKEY= {
            "switch",
            "ring"
    };
    private GPSDataChangeListener mGpsChangedListener;

    private Button btnAlarm;
    private Button btnSystem;
    private Button btnTest;


    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_SystemState).setOnClickListener(this);
        view.findViewById(R.id.btn_RemoteAlarm).setOnClickListener(this);
        view.findViewById(R.id.btn_test).setOnClickListener(this);
        btnAlarm = (Button)getActivity().findViewById(R.id.btn_RemoteAlarm);
        btnSystem = (Button)getActivity().findViewById(R.id.btn_SystemState);
        btnTest = (Button)getActivity().findViewById(R.id.btn_test);
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView called!");
		return inflater.inflate(R.layout.switch_fragment, container, false);
	}

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btn_SystemState:
                systemBtnClicked();
                break;
            case R.id.btn_RemoteAlarm:
                remoteAlarmClicked();
                break;
            case R.id.btn_test:
                testBtnClicked();
            default:
                break;
        }
    }

    public void systemBtnClicked(){
        mCenter.cGetStatus(mXpgWifiDevice);
      //  mCenter.cGprsSend(mXpgWifiDevice);
        Log.i("发送数据SwitchFragment","qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
    }

    public void remoteAlarmClicked(){
        mCenter.cUnbindDevice(setManager.getUid(),setManager.getToken(),setManager.getDid(),setManager.getPassCode());
        mCenter.cDisconnect(mXpgWifiDevice);
    }

    public void testBtnClicked(){
        mCenter.cGetStatus(mXpgWifiDevice);
    }
    public void   requestHttp(final String url,final String[] key, final int[] value) {
        int status = 0;
        DefaultHttpClient mHttpClient = new DefaultHttpClient();
        HttpPut mPut = new HttpPut(url);

        //handle key, value
        JSONObject param = new JSONObject();
        int size = key.length;
        for (int i = 0; i < size; i++) {
            try {
                param.put(key[i], value[i]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //bind  param to entity
        try {
            StringEntity se = new StringEntity(param.toString(), HTTP.UTF_8);
            mPut.setEntity(se);
        } catch (UnsupportedEncodingException e1) {
            // TODOAuto-generated catch block
            e1.printStackTrace();
        }
        try {
            //Socket timeout 6s
            mHttpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 6000);

            // connect timeout 6s
            mHttpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 6000);

            //execute post
            HttpResponse response = mHttpClient.execute(mPut);

            //handle response
            int res = response.getStatusLine().getStatusCode();
            if (res == 200) {
                status = 1;
            } else if (res == 404) {
                status = 404;
            } else if (res == 500) {
                status = 500;
            }
        } catch (ClientProtocolException e) {
            // TODOAuto-generated catchblock
            e.printStackTrace();
            status = 900;
        } catch (ConnectTimeoutException e) {
            // TODOAuto-generated catchblock
            e.printStackTrace();
            status = 901;
        } catch (InterruptedIOException e) {
            // TODOAuto-generated catchblock
            e.printStackTrace();
            status = 902;
        } catch (IOException e) {
            // TODOAuto-generated catchblock
            e.printStackTrace();
            status = 903;
        }

        Message msg = Message.obtain();
        msg.arg1 = status;
        msg.obj = key[0];
        msg.what = IS_FINISH;
        httpPutHandler.sendMessage(msg);

    }

    private Handler httpPutHandler = new Handler() {
        @Override
        public void handleMessage(Message msg){
            Button handleBtn = null;

            if(msg.what == IS_FINISH){
                switch(msg.arg1){
                    case 1 :{   //put操作是否成功
                        //根据message中的参数判断
                         chengeStateWhenSuc(msg.obj.toString());
                        break;
                    }
                    default:{
                        restoreStateWhenFail(msg.obj.toString());
                        break;
                    }
                }
            }
        }
    };

    private void chengeStateWhenSuc(String keyString){
        Button btn = null;
        if(keyString.equals(SWITCHKEY[0])){
            btn = btnSystem;
            if (!systemState) {
                btn.setBackgroundColor(Color.YELLOW);
                systemState = true;
            } else {
                btn.setBackgroundResource(R.drawable.common_btn_normal);
                systemState = false;
            }
        }else if(keyString.equals(SWITCHKEY[1])){
            btn = btnAlarm;
            if (!alarmState) {
                btn.setBackgroundColor(Color.YELLOW);
                alarmState = true;
            } else {
                btn.setBackgroundResource(R.drawable.common_btn_normal);
                alarmState = false;
            }
        }
    }
    private void restoreStateWhenFail(String keyString){
        if(keyString.equals(SWITCHKEY[0])){
            Toast.makeText(getActivity().getApplicationContext(), "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
            btnSystem.setBackgroundResource(R.drawable.common_btn_normal);
        }else if(keyString.equals(SWITCHKEY[1])){
            Toast.makeText(getActivity().getApplicationContext(), "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
            btnAlarm.setBackgroundResource(R.drawable.common_btn_normal);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public interface GPSDataChangeListener{
        public void gpsCallBack(String lat,String lon);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mGpsChangedListener = (GPSDataChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement GPSDataChangeListener");
        }
    }
}
