package com.xunce.electrombile.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVUser;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.BindingActivity;
import com.xunce.electrombile.activity.AboutActivity;
import com.xunce.electrombile.activity.HelpActivity;
import com.xunce.electrombile.activity.account.LoginActivity;
import com.xunce.electrombile.service.GPSDataService;
import com.xunce.electrombile.xpg.common.useful.NetworkUtils;
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;


public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    private static String TAG = "SettingsFragment:";
    private Context m_context;
   // private LinearLayout btnPhoneNumber;
    private LinearLayout btnBind;
    private LinearLayout btnAbout;
    private LinearLayout btnHelp;
  //  private LinearLayout login_again;
    private Button btnLogout;

    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
      //  Log.i(TAG, "onCreateView called!");
        initView();

		return inflater.inflate(R.layout.settings_fragment, container, false);
	}

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
     //   view.findViewById(R.id.layout_phone_number).setOnClickListener(this);
        view.findViewById(R.id.layout_bind).setOnClickListener(this);
        view.findViewById(R.id.layout_about).setOnClickListener(this);
        view.findViewById(R.id.layout_help).setOnClickListener(this);
        view.findViewById(R.id.btn_logout).setOnClickListener(this);
      //  view.findViewById(R.id.layout_login_again).setOnClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        m_context = activity;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.layout_bind:
                //systemBtnClicked();
                if(NetworkUtils.isNetworkConnected(m_context)){
                    if(setManager.getIMEI().isEmpty()) {
                  //      Log.i(TAG, "clicked item layout_relieve_bind");
                        setManager.cleanDevice();
                        Intent intentStartBinding = new Intent(m_context, BindingActivity.class);
                        startActivity(intentStartBinding);
                    }else{
                        System.out.println(setManager.getIMEI() +"aaaaaaaaaaa");
                        ToastUtils.showShort(m_context,"设备已绑定");
                    }
                }else{
                    ToastUtils.showShort(m_context,"网络连接错误");
                    }
                break;
            case R.id.layout_help:
                Intent intentHelp = new Intent(m_context, HelpActivity.class);
                startActivity(intentHelp);

                break;
            case R.id.btn_logout:
                AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("退出登录")
                        .setMessage("退出登录将清除所有已有账户及已经绑定的设备\n确定退出么？")
                        .setPositiveButton("否",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                    }
                                }).setNegativeButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setManager.cleanAll();
                                Intent intentStartLogin = new Intent(m_context, LoginActivity.class);
                                startActivity(intentStartLogin);
                                getActivity().stopService(new Intent(m_context, GPSDataService.class));
                                GPSDataService.isRunning = false;
                                AVUser.logOut();
                                getActivity().finish();
                            }
                        }).create();
                dialog.show();
                break;
            case R.id.layout_about:
                Intent intentAbout = new Intent(m_context, AboutActivity.class);
                startActivity(intentAbout);
                break;
            default:
                break;
        }
    }
    private void initView() {
        //btnPhoneNumber = (LinearLayout)getActivity().findViewById(R.id.layout_phone_number);
        btnBind = (LinearLayout)getActivity().findViewById(R.id.layout_bind);
        btnAbout = (LinearLayout)getActivity().findViewById(R.id.layout_about);
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
