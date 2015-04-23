package com.xunce.electrombile.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.Base.sdk.SettingManager;
import com.xunce.electrombile.R;
import com.xunce.electrombile.xpg.common.useful.NetworkUtils;

public class SwitchFragment extends BaseFragment implements OnClickListener {

    private static String TAG = "SwitchFragment:";
    private final int IS_FINISH = 1;
    private boolean systemState = false;
    private boolean alarmState = false;

    private String[] SWITCHKEY = {
            "switch",
            "ring"
    };
    private GPSDataChangeListener mGpsChangedListener;

    private Button btnAlarm;
    private Button btnSystem;
    private Button btnTest;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setManager = new SettingManager(getActivity().getApplicationContext());
        mCenter = CmdCenter.getInstance(getActivity().getApplicationContext());
        loginHandler.sendEmptyMessage(loginHandler_key.START_LOGIN.ordinal());

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_SystemState).setOnClickListener(this);
        btnSystem = (Button) getActivity().findViewById(R.id.btn_SystemState);
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
        switch (id) {
            case R.id.btn_SystemState:
                systemBtnClicked();
                break;
//            case R.id.btn_RemoteAlarm:
//                remoteAlarmClicked();
//                break;
//            case R.id.btn_test:
//                testBtnClicked();
            default:
                break;
        }
    }

    public void systemBtnClicked() {

        if(!NetworkUtils.isNetworkConnected(getActivity().getApplicationContext())){
            Toast.makeText(getActivity().getApplicationContext(), "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
            return;
        }
        final String key[] = {SWITCHKEY[0]};
        int value[] = {0};

        if(!systemState){
            systemState = true;

            //更改通知栏状态
            manager.cancel(1);
            changeNotificaton();

            value[0] = 1;
            //btnSystem.setBackgroundResource(R.drawable.common_btn_pressed);
            btnSystem.setBackgroundColor(Color.YELLOW);

        }else{
            systemState = false;

            //更改通知栏状态
            manager.cancel(1);
            initNotificaton();

            value[0] = 0;
            btnSystem.setBackgroundResource(R.drawable.common_btn_normal);
        }
    }

    public void remoteAlarmClicked() {

//        final String key[] = {SWITCHKEY[1]};
//        int value[] = {0};
//
//        if (alarmState == false) {
//            value[0] = 1;
//            btnAlarm.setBackgroundResource(R.drawable.common_btn_pressed);
//        } else {
//            value[0] = 0;
//            btnAlarm.setBackgroundResource(R.drawable.common_btn_pressed);
//        }
//        final int finalValue[] = value;
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                requestHttp("http://electrombile.huakexunce.com/config", key, finalValue);
//            }
//        }).start();
    }

    public void testBtnClicked() {

    }

    private void chengeStateWhenSuc(String keyString) {
        Button btn = null;
        btn = btnSystem;
        btn.setBackgroundColor(Color.YELLOW);
    }

    private void restoreStateWhenFail(String keyString) {
        if (keyString.equals(SWITCHKEY[0])) {
            Toast.makeText(getActivity().getApplicationContext(), "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
            btnSystem.setBackgroundResource(R.drawable.common_btn_normal);
        } else if (keyString.equals(SWITCHKEY[1])) {
            Toast.makeText(getActivity().getApplicationContext(), "网络错误，请检查网络设置", Toast.LENGTH_SHORT).show();
            btnAlarm.setBackgroundResource(R.drawable.common_btn_normal);
        }
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
