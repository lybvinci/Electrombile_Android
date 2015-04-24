package com.xunce.electrombile.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;

import com.xunce.electrombile.Base.utils.HTTPUtil;
import com.xunce.electrombile.Base.utils.TracksManager;
import com.xunce.electrombile.R;
import com.xunce.electrombile.fragment.MaptabFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by heyukun on 2015/4/18.
 */
public class RecordActivity extends Activity{

    Button btnCuston;
    Button btnBegin;
    Button btnEnd;
    Button btnOK;
    Button btnOneDay;
    Button btnTwoDay;
    DatePicker dpBegin;
    DatePicker dpEnd;
    ListView m_listview;
    TracksManager tracksManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initView();
        setCustonViewVisibility(false);
        m_listview.setVisibility(View.INVISIBLE);

        tracksManager = new TracksManager();
    }

    private void initView(){
        btnCuston = (Button)findViewById(R.id.btn_custom);
        btnCuston.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!btnBegin.isShown()) {
                    setCustonViewVisibility(true);
                    m_listview.setVisibility(View.INVISIBLE);
                }
                else {
                }
            }
        });
        btnOneDay = (Button)findViewById(R.id.btn_oneday);
        btnOneDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_listview.setVisibility(View.VISIBLE);
                setCustonViewVisibility(false);
                HTTPUtil.HTTPGet("");
            }
        });
        btnTwoDay = (Button)findViewById(R.id.btn_twoday);
        btnBegin = (Button)findViewById(R.id.btn_begin);
        btnEnd = (Button)findViewById(R.id.btn_end);
        dpBegin = (DatePicker)findViewById(R.id.datePicker_begin);
        dpEnd = (DatePicker)findViewById(R.id.datePicker_end);
        btnOK = (Button)findViewById(R.id.btn_OK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    setCustonViewVisibility(false);
                    m_listview.setVisibility(View.VISIBLE);
            }
        });

        initListView();
    }

    private void initListView(){
        //绑定Layout里面的ListView
        m_listview = (ListView) findViewById(R.id.listview);

        //生成动态数组，加入数据
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<10;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemTitle", "Level "+i);
            map.put("ItemText", "Finished in 1 Min 54 Secs, 70 Moves! ");
            listItem.add(map);
        }

        //生成适配器的Item和动态数组对应的元素
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源
                R.layout.listview_item,//ListItem的XML实现
                //动态数组与ImageItem对应的子项
                new String[] {"ItemTitle", "ItemText"},
                //,两个TextView ID
                new int[] {R.id.ItemTitle,R.id.ItemText}
        );

        //添加并且显示
        m_listview.setAdapter(listItemAdapter);

        //添加点击
        m_listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                    MaptabFragment.trackDataList = tracksManager.getTrack(arg2);
                Toast.makeText(getApplicationContext(), "点击第" + arg2 + "个项目", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        //添加长按点击
        m_listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("是否删除此记录");
                menu.add(0, 0, 0, "确定");
                menu.add(0, 1, 0, "取消");
            }

        });

    }


    /**
     * 设置自定义选择界面是否可见
     * @param visible-是否可见
     */
private void setCustonViewVisibility(Boolean visible){
        if(visible){
            btnBegin.setVisibility(View.VISIBLE);
            btnEnd.setVisibility(View.VISIBLE);
            dpBegin.setVisibility(View.VISIBLE);
            dpEnd.setVisibility(View.VISIBLE);
            btnOK.setVisibility(View.VISIBLE);
        }
        else{
            btnBegin.setVisibility(View.INVISIBLE);
            btnEnd.setVisibility(View.INVISIBLE);
            dpBegin.setVisibility(View.INVISIBLE);
            dpEnd.setVisibility(View.INVISIBLE);
            btnOK.setVisibility(View.INVISIBLE);
        }
    }

}
