package com.xunce.electrombile.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import com.xunce.electrombile.R;


public class SwitchFragment extends Fragment implements OnClickListener {

    private static String TAG = "SwitchFragment:";

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
        Log.i("", "systemBtnClicked clicked");
    }

    public void remoteAlarmClicked(){
        Log.i("", "remoteAlarmClicked clicked");
    }
}
