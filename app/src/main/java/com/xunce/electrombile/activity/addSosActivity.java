package com.xunce.electrombile.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.LogUtil;
import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.Base.sdk.SettingManager;
import com.xunce.electrombile.R;
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;

import java.util.ArrayList;

public class addSosActivity extends Activity {

    private EditText et_addSOS;
    private ListView lv_SOS;
    private SettingManager settingManager;
    private MyAdapter mAdapter;
    private ArrayList<String> arrayList;
    private CmdCenter mCenter;
    //
    byte firstByteSOSAdd = 0x00;
    byte secondByteSOSAdd = 0x00;
    byte firstByteSOSDelete = 0x00;
    byte secondByteSOSDelete = 0x00;
    public static ProgressDialog SOSWaitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sos);
        settingManager = new SettingManager(this);
        mCenter = CmdCenter.getInstance(this);
        initView();

    }

    private void initView() {
        et_addSOS = (EditText) findViewById(R.id.et_SOS);
        lv_SOS = (ListView) findViewById(R.id.lv_SOS);
        String mOriginData =  settingManager.getSOS();
        arrayList = new ArrayList<String>();
        if(!mOriginData.isEmpty()){
            String[] data = mOriginData.split("-");
            for(int i =0;i<data.length;i++) {
                arrayList.add(data[i]);
            }
        }
        mAdapter = new MyAdapter();
        lv_SOS.setAdapter(mAdapter);
        SOSWaitDialog = new ProgressDialog(this);
        SOSWaitDialog.setMessage(getString(R.string.wait_for_i_miss));
    }


    public void addSOS(View view){
        String phone = et_addSOS.getText().toString().trim();
        if(phone.isEmpty()){
            ToastUtils.showShort(this,getString(R.string.input_true_text));
            return ;
        }
        if(phone.length() != 11){
            ToastUtils.showShort(this,getString(R.string.input_yes_phonenumber));
            return ;
        }
        if(FragmentActivity.pushService == null){
            ToastUtils.showShort(this,getString(R.string.init_failed));
            return ;
        }
        et_addSOS.setText("");
        arrayList.add(phone);
        StringBuilder sb = new StringBuilder();
        for (int i =0;i<arrayList.size();i++){
            sb.append(arrayList.get(i));
            sb.append("-");
        }
        settingManager.setSOS(sb.toString());

        byte[] serial = mCenter.getSerial(firstByteSOSAdd, secondByteSOSAdd);
        for(int i=1;i<arrayList.size();i++){
            phone = "," + phone;
        }
        FragmentActivity.pushService.sendMessage1(mCenter.cSOSManagerAdd(serial,phone));
        SOSWaitDialog.show();
        mAdapter.notifyDataSetChanged();
    }

    public void showInfo(final int position) {
        AlertDialog dialog2 = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_contact_person))
                .setMessage(getString(R.string.delete_this_person))
                .setPositiveButton(getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setNegativeButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String phone = arrayList.get(position);
                        arrayList.remove(arrayList.get(position));
                        if(arrayList.isEmpty()){
                            settingManager.setSOS("");
                        }else {
                            StringBuilder sb = new StringBuilder();
                            for (int i =0;i<arrayList.size();i++){
                                sb.append(arrayList.get(i));
                                sb.append("-");
                            }
                            settingManager.setSOS(sb.toString());
                        }
                        byte[] serial = mCenter.getSerial(firstByteSOSDelete, secondByteSOSDelete);
                        FragmentActivity.pushService.sendMessage1(mCenter.cSOSManagerDelete(serial,phone));
                        SOSWaitDialog.show();
                        mAdapter.notifyDataSetChanged();
                    }
                }).create();
        dialog2.show();
    }


    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            if(arrayList != null) {
                return arrayList.size();
            }else{
                return 0;
            }

        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if(arrayList == null)
                return null;
            View mView;
            if(view == null) {
                LayoutInflater inflater = addSosActivity.this.getLayoutInflater();
                mView = inflater.inflate(R.layout.listview_sos, null);
            }else{
                mView = view;
            }
            TextView tvPhone = (TextView) mView.findViewById(R.id.tv_sos);
            tvPhone.setText(arrayList.get(i));
            Button delete = (Button) mView.findViewById(R.id.delete_button);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showInfo(i);
                }
            });
            return mView;
        }
    }

    public static void cancleDialog(){
        SOSWaitDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        SOSWaitDialog = null;
        super.onDestroy();
    }
}
