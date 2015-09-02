package com.xunce.electrombile.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.xunce.electrombile.R;
import com.xunce.electrombile.xpg.ui.utils.ToastUtils;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

public class BindListActivity extends BaseActivity {

    private MyAdapter mAdapter;
    private ListView bind_list;
    private TextView tv_default;
    // private List<AVObject> bindList= null;
    private HashMap<Integer, HashMap<String, String>> bindList = null;
    private ProgressDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bind_list);
        super.onCreate(savedInstanceState);
    }

    //初始化View
    @Override
    public void initViews() {
        bind_list = (ListView) findViewById(R.id.list_view_bind_list);
        tv_default = (TextView) findViewById(R.id.tv_default);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在查询，请稍后...");
    }

    //初始化事件
    @Override
    public void initEvents() {
        bindList = new HashMap<>();
        refreshBindList();
        mAdapter = new MyAdapter();
        bind_list.setAdapter(mAdapter);
    }

    private void refreshBindList() {
        AVUser currentUser = AVUser.getCurrentUser();
        AVQuery<AVObject> query = new AVQuery<>("Bindings");
        query.whereEqualTo("user", currentUser);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    bindList.clear();
                    Log.e("BINDLISTACT", list.size() + "");
                    if (list.size() > 0) {
                        HashMap<String, String> tmpList = new HashMap();
                        int i = 0;
                        for (AVObject tmp : list) {
                            tmpList.clear();
                            if (tmp.get("isAdmin") != null && (boolean) tmp.get("isAdmin")) {
                                tmpList.put("isAdmin", "true");
                                tmpList.put("IMEI", (String) tmp.get("IMEI"));
                            } else {
                                tmpList.put("isAdmin", "false");
                                tmpList.put("IMEI", (String) tmp.get("IMEI"));
                            }
                            bindList.put(i, tmpList);
                            i++;
                        }
                        tv_default.setVisibility(View.GONE);
                    } else {
                        ToastUtils.showShort(BindListActivity.this, "无可用设备");
                    }
                } else {
                    e.printStackTrace();
                    ToastUtils.showShort(BindListActivity.this, "查询错误");
                }
                progressDialog.dismiss();
            }
        });
        progressDialog.show();
    }

    public void addEquip(View view) {
        Intent intent = new Intent(this, BindingActivity.class);
        startActivity(intent);
        this.finish();
    }


    //设备列表
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (bindList != null) {
                return bindList.size();
            } else {
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

            if (bindList == null)
                return null;
            View mView;
            if (view == null) {
                LayoutInflater inflater = BindListActivity.this.getLayoutInflater();
                mView = inflater.inflate(R.layout.listview_bind_list, null);
            } else {
                mView = view;
            }
            TextView tvAdmin = (TextView) mView.findViewById(R.id.tv_admin);
            if (bindList.get(i).get("isAdmin").equals("true")) {
                tvAdmin.setText("主车");
            } else {
                tvAdmin.setText("其他车");
            }
            Button switchBtn = (Button) mView.findViewById(R.id.btn_switch);
            switchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("QQQQQ", i + "aaaaaa");
                    Log.e("QQQQQ", setManager.getIMEI());
                    Log.e("QQQQQ", bindList.get(i).get("IMEI"));
                    if (setManager.getIMEI() == bindList.get(i).get("IMEI")) {
                        ToastUtils.showShort(BindListActivity.this, "正在使用此设备，无须切换~");
                        return;
                    }
                    setManager.setIMEI(bindList.get(i).get("IMEI"));
                    FragmentActivity.fragmentActivity.finish();
                    ToastUtils.showShort(BindListActivity.this, "切换中~");
                    Intent intent = new Intent(BindListActivity.this, FragmentActivity.class);
                    startActivity(intent);
                    BindListActivity.this.finish();
                }
            });
            return mView;
        }
    }

}
