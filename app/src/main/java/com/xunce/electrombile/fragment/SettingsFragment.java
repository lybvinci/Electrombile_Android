package com.xunce.electrombile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.BindingActivity;
import com.xunce.electrombile.activity.HelpActivity;
import com.xunce.electrombile.activity.account.LoginActivity;
import com.xunce.electrombile.service.GPSDataService;
import com.xunce.electrombile.xpg.common.useful.NetworkUtils;
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;


public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private static String TAG = "SettingsFragment:";
   // private LinearLayout btnPhoneNumber;
    private LinearLayout btnBind;
 //   private LinearLayout btnRelieveBind;
    private LinearLayout btnHelp;
  //  private LinearLayout login_again;
    private Button btnLogout;

    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView called!");
        initView();

		return inflater.inflate(R.layout.settings_fragment, container, false);
	}

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
     //   view.findViewById(R.id.layout_phone_number).setOnClickListener(this);
        view.findViewById(R.id.layout_bind).setOnClickListener(this);
       // view.findViewById(R.id.layout_relieve_bind).setOnClickListener(this);
        view.findViewById(R.id.layout_help).setOnClickListener(this);
        view.findViewById(R.id.btn_logout).setOnClickListener(this);
      //  view.findViewById(R.id.layout_login_again).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.layout_bind:
                //systemBtnClicked();
                if(NetworkUtils.isNetworkConnected(getActivity().getApplicationContext())){
                    if(mXpgWifiDevice == null) {
                        // if(mXpgWifiDevice.isBind(setManager.getUid())){
                        Log.i(TAG, "clicked item layout_relieve_bind");
                        Intent intentStartBinding = new Intent(getActivity().getApplicationContext(), BindingActivity.class);
                        startActivity(intentStartBinding);
                    }else{
                        ToastUtils.showShort(getActivity().getApplicationContext(),"设备已绑定");
                    }
                }else{
                    ToastUtils.showShort(getActivity().getApplicationContext(),"网络连接错误");
                    }
                break;
//            case R.id.layout_relieve_bind:
//                if(NetworkUtils.isNetworkConnected(getActivity().getApplicationContext())) {
//                    if (mXpgWifiDevice.isConnected()) {
//                        mCenter.cUnbindDevice(setManager.getUid(), setManager.getToken(), setManager.getDid(), setManager.getPassCode());
//                        mCenter.cDisconnect(mXpgWifiDevice);
//                    }else{
//                        ToastUtils.showShort(getActivity().getApplicationContext(), "请尝试连接网络或先绑定设备");
//                    }
//                }else{
//                ToastUtils.showShort(getActivity().getApplicationContext(), "网络连接错误");
//                }
//                break;
//            case R.id.layout_phone_number:
//                mCenter.cGetStatus(mXpgWifiDevice);
//                break;
            case R.id.layout_help:
                Intent intentHelp = new Intent(getActivity().getApplicationContext(), HelpActivity.class);
                startActivity(intentHelp);

                break;
            case R.id.btn_logout:
           //     mCenter.cLogout();
//                setManager.cleanAll();
                setManager.cleanAll();
                Intent intentStartLogin = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intentStartLogin);
                getActivity().stopService(new Intent(getActivity().getApplicationContext(), GPSDataService.class));
                GPSDataService.isRunning = false;
                getActivity().finish();
                break;
//            case R.id.layout_login_again:
//                if(!mXpgWifiDevice.isConnected() && setManager.getDid() !=null && setManager.getPassCode() !=null) {
//                    loginHandler.sendEmptyMessage(loginHandler_key.START_LOGIN.ordinal());
//                }else{
//                    ToastUtils.showShort(getActivity().getApplicationContext(),"未绑定或已登陆设备");
//                }
//                break;
            default:
                break;
        }
    }
    private void initView() {
        //btnPhoneNumber = (LinearLayout)getActivity().findViewById(R.id.layout_phone_number);
        btnBind = (LinearLayout)getActivity().findViewById(R.id.layout_bind);
     //   btnRelieveBind = (LinearLayout)getActivity().findViewById(R.id.layout_relieve_bind);
        btnHelp = (LinearLayout)getActivity().findViewById(R.id.layout_help);
        btnLogout = (Button)getActivity().findViewById(R.id.btn_logout);
  //      login_again = (LinearLayout) getActivity().findViewById(R.id.layout_login_again);
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//((TextView)getView().findViewById(R.id.tvTop)).setText("设置");
	}

}
