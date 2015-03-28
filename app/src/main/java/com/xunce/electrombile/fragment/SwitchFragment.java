package com.xunce.electrombile.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.xunce.electrombile.R;
import com.xunce.electrombile.network.JsonManager;

import java.util.PriorityQueue;


public class SwitchFragment extends Fragment implements OnClickListener {

    private static String TAG = "SwitchFragment:";
    private JsonManager jsonManager= new JsonManager();
    private boolean systemState = false;
    private boolean alarmState = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_SystemState).setOnClickListener(this);
        view.findViewById(R.id.btn_RemoteAlarm).setOnClickListener(this);
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
            default:
                break;
        }
    }

    public void systemBtnClicked(){
        String key[] = {"switch"};
        int value[] = {0};
        Button btnSystem = (Button)getActivity().findViewById(R.id.btn_SystemState);
        if(systemState == false){
            value[0] = 1;
        }else{
            value[0] = 0;
        }
        int res = jsonManager.requestHttp("http://electrombile.huakexunce.com/config", key, value);
        Log.i(TAG, res + "");
        switch(res){
            case 1:
                if(systemState == false){
                    btnSystem.setBackgroundColor(Color.YELLOW);
                    systemState = true;
                    break;
                }else{
                    btnSystem.setBackgroundResource(R.drawable.common_btn_normal);
                    systemState = false;
                    break;
                }
            default:{
                Toast.makeText(getActivity().getApplicationContext(), "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
                btnSystem.setBackgroundResource(R.drawable.common_btn_normal);
                break;
            }
        }
    }

    public void remoteAlarmClicked(){
        String key[] = {"ring"};
        int value[] = {0};
        Button btnAlarm = (Button)getActivity().findViewById(R.id.btn_RemoteAlarm);
        if(systemState == false){
            value[0] = 1;
            //btnAlarm.setBackgroundColor(Color.LTGRAY);
        }else{
            value[0] = 0;
            //btnAlarm.setBackgroundColor(Color.LTGRAY);
        }
        int res = jsonManager.requestHttp("http://electrombile.huakexunce.com/config", key, value);
        Log.i(TAG, res + "");
        switch(res){
            case 1 :{
                if (systemState == false) {
                    btnAlarm.setBackgroundColor(Color.YELLOW);
                    systemState = true;
                    break;
                } else {
                    btnAlarm.setBackgroundResource(R.drawable.common_btn_normal);
                    systemState = false;
                    break;
                }
            }
            default:{
                Toast.makeText(getActivity().getApplicationContext(), "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
                btnAlarm.setBackgroundResource(R.drawable.common_btn_normal);
                break;
            }
        }
    }
}
