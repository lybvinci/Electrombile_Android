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

import com.xunce.electrombile.Base.sdk.CmdCenter;
import com.xunce.electrombile.R;
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;

import java.util.ArrayList;

public class addSosActivity extends Activity {

    private EditText et_addSOS;
    private ListView lv_SOS;
    private CmdCenter mCenter;

    private static MyAdapter mAdapter;
    private static ArrayList<String> arrayListSOS;

    private byte firstByteSOSAdd = 0x00;
    private byte secondByteSOSAdd = 0x00;
    private byte firstByteSOSDelete = 0x00;
    private byte secondByteSOSDelete = 0x00;
    private byte firstByteSearch = 0x00;
    private byte secondByteSearch = 0x00;
    public static ProgressDialog SOSWaitDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sos);
        mCenter = CmdCenter.getInstance(this);
        initView();
        if(FragmentActivity.pushService != null){
            byte[] serial = mCenter.getSerial(firstByteSearch, secondByteSearch);
            FragmentActivity.pushService.sendMessage1(mCenter.cSOSSearch(serial));
            SOSWaitDialog.show();
        }

    }

    private void initView() {
        et_addSOS = (EditText) findViewById(R.id.et_SOS);
        lv_SOS = (ListView) findViewById(R.id.lv_SOS);
        arrayListSOS = new ArrayList<String>();
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
        arrayListSOS.add(phone);
        StringBuilder sb = new StringBuilder();
        for (int i =0;i< arrayListSOS.size();i++){
            sb.append(arrayListSOS.get(i));
            sb.append("-");
        }

        byte[] serial = mCenter.getSerial(firstByteSOSAdd, secondByteSOSAdd);
        for(int i=1;i< arrayListSOS.size();i++){
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
                        String phone = arrayListSOS.get(position);
                        arrayListSOS.remove(arrayListSOS.get(position));
                        if(arrayListSOS.isEmpty()){
                        }else {
                            StringBuilder sb = new StringBuilder();
                            for (int i =0;i< arrayListSOS.size();i++){
                                sb.append(arrayListSOS.get(i));
                                sb.append("-");
                            }
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
            if(arrayListSOS != null) {
                return arrayListSOS.size();
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
            if(arrayListSOS == null)
                return null;
            View mView;
            if(view == null) {
                LayoutInflater inflater = addSosActivity.this.getLayoutInflater();
                mView = inflater.inflate(R.layout.listview_sos, null);
            }else{
                mView = view;
            }
            TextView tvPhone = (TextView) mView.findViewById(R.id.tv_sos);
            tvPhone.setText(arrayListSOS.get(i));
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

    public static void cancelDialog(String data){
        SOSWaitDialog.dismiss();
        String[] s1 = data.split(":");
        if(s1.length>1) {
            String[] s2 = s1[1].split(",");
            if(s2.length>0) {
                for (String s : s2) {
                    if(s.length()>=11)
                        arrayListSOS.add(s);
                }
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    public static void cancelDialog(){
        SOSWaitDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        SOSWaitDialog = null;
        mAdapter = null;
        super.onDestroy();
    }
}
