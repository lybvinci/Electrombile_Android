package com.xunce.electrombile.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xunce.electrombile.R;


public class SettingsFragment extends Fragment {

    private static String TAG = "SettingsFragment:";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView called!");
		return inflater.inflate(R.layout.settings_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		((TextView)getView().findViewById(R.id.tvTop)).setText("设置");
	}
}
