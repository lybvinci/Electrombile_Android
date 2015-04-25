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
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.xunce.electrombile.R;
import com.xunce.electrombile.xpg.common.useful.NetworkUtils;

public class SwitchFragment extends BaseFragment {

    private static String TAG = "SwitchFragment:";
    private final int IS_FINISH = 1;
    private boolean systemState = false;
    private boolean alarmState = false;

    private String[] SWITCHKEY = {
            "switch",
            "ring"
    };


    private Button btnAlarm;
    private ToggleButton btnSystem;
    private Button btnTest;


    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSystem = (ToggleButton) getActivity().findViewById(R.id.btn_SystemState);
        btnSystem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    mCenter.alarmFlag = true;
                    mCenter.cGetStatus(mXpgWifiDevice);
                    //  mCenter.cGprsSend(mXpgWifiDevice);
                    Log.i("发送数据SwitchFragment","qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
                }else{
                    mCenter.alarmFlag =false;
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView called!");
        return inflater.inflate(R.layout.switch_fragment, container, false);
    }

//    @Override
//    public void onClick(View view) {
//        int id = view.getId();
//        switch (id) {
////            case R.id.btn_SystemState:
////                systemBtnClicked();
////                break;
//
//            default:
//                break;
//        }
//    }

    public void systemBtnClicked(){
        mCenter.alarmFlag = true;
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

//    public interface GPSDataChangeListener{
//        public void gpsCallBack(String lat,String lon);
//    }


}
