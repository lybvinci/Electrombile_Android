package com.xunce.electrombile.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xunce.electrombile.R;
import com.xunce.electrombile.activity.BindingActivity;
import com.xunce.electrombile.activity.FragmentActivity;
import com.xunce.electrombile.activity.HelpActivity;
import com.xunce.electrombile.activity.account.LoginActivity;


public class SettingsFragment extends Fragment implements View.OnClickListener {

    private static String TAG = "SettingsFragment:";
    private LinearLayout btnPhoneNumber;
    private LinearLayout btnBind;
    private LinearLayout btnRelieveBind;
    private LinearLayout btnHelp;
    private Button btnLogout;

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
        view.findViewById(R.id.layout_phone_number).setOnClickListener(this);
        view.findViewById(R.id.layout_bind).setOnClickListener(this);
        view.findViewById(R.id.layout_relieve_bind).setOnClickListener(this);
        view.findViewById(R.id.layout_help).setOnClickListener(this);
        view.findViewById(R.id.btn_logout).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.layout_bind:
                //systemBtnClicked();
                Log.i(TAG, "clicked item layout_relieve_bind");
                Intent intentStartBinding = new Intent(getActivity().getApplicationContext(), BindingActivity.class);
                startActivity(intentStartBinding);
                break;
            case R.id.layout_relieve_bind:
                //systemBtnClicked();
                break;
            case R.id.layout_phone_number:
                //systemBtnClicked();
                break;
            case R.id.layout_help:
                Intent intentHelp = new Intent(getActivity().getApplicationContext(), HelpActivity.class);
                startActivity(intentHelp);
                break;
            case R.id.btn_logout:
                //systemBtnClicked();
                Intent intentStartLogin = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intentStartLogin);
                //关闭当前activity
                getActivity().finish();
                break;
            default:
                break;
        }
    }
    private void initView() {
        btnPhoneNumber = (LinearLayout)getActivity().findViewById(R.id.layout_phone_number);
        btnBind = (LinearLayout)getActivity().findViewById(R.id.layout_bind);
        btnRelieveBind = (LinearLayout)getActivity().findViewById(R.id.layout_relieve_bind);
        btnHelp = (LinearLayout)getActivity().findViewById(R.id.layout_help);
        btnLogout = (Button)getActivity().findViewById(R.id.btn_logout);
    }

    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		//((TextView)getView().findViewById(R.id.tvTop)).setText("设置");
	}
}
