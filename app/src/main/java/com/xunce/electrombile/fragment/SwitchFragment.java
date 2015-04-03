package com.xunce.electrombile.fragment;

import android.content.DialogInterface;
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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVPush;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.SendCallback;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.FragmentActivity;

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
import java.util.PriorityQueue;
import java.util.Vector;


public class SwitchFragment extends Fragment implements OnClickListener {

    private static String TAG = "SwitchFragment:";
    private final int IS_FINISH = 1;
    private boolean systemState = false;
    private boolean alarmState = false;
    private String[] SWITCHKEY= {
            "switch",
            "ring"
    };


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
        final String key[] = {SWITCHKEY[0]};
        int value[] = {0};

        if(systemState == false){
            value[0] = 1;
            btnSystem.setBackgroundResource(R.drawable.common_btn_pressed);
        }else{
            value[0] = 0;
            btnSystem.setBackgroundResource(R.drawable.common_btn_pressed);
        }
        final int finalValue[] = value;
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestHttp("http://electrombile.huakexunce.com/config", key, finalValue);
            }
        }).start();


    }

    public void remoteAlarmClicked(){

        final String key[] = {SWITCHKEY[1]};
        int value[] = {0};

        if(alarmState == false){
            value[0] = 1;
            btnAlarm.setBackgroundResource(R.drawable.common_btn_pressed);
        }else {
            value[0] = 0;
            btnAlarm.setBackgroundResource(R.drawable.common_btn_pressed);
        }
        final int finalValue[] = value;
        new Thread(new Runnable() {
            @Override
            public void run() {
                requestHttp("http://electrombile.huakexunce.com/config", key, finalValue);
            }
        }).start();
    }

    public void testBtnClicked(){
        AVPush push = new AVPush();

        AVQuery<AVInstallation> query = AVInstallation.getQuery();
        query.whereEqualTo("installationId", AVInstallation.getCurrentInstallation()
                .getInstallationId());
        push.setQuery(query);
        String message = new String("您的电动车正在被盗!");
        String channel = new String("publicheyukun");
        push.setChannel(channel.trim());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "com.xunce.electrombile.push.action");
            jsonObject.put("alert", channel.trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        push.setData(jsonObject);
        push.setPushToAndroid(true);
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(AVException e) {
                //Toast.makeText(getApplicationContext(), "send successfully", Toast.LENGTH_SHORT);
            }
        });
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
            if (systemState == false) {
                btn.setBackgroundColor(Color.YELLOW);
                systemState = true;
            } else {
                btn.setBackgroundResource(R.drawable.common_btn_normal);
                systemState = false;
            }
        }else if(keyString.equals(SWITCHKEY[1])){
            btn = btnAlarm;
            if (alarmState == false) {
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
}
