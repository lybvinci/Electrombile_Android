package com.xunce.electrombile.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.baidu.mapapi.utils.DistanceUtil;
import com.xunce.electrombile.Base.utils.TracksManager;
import com.xunce.electrombile.Base.utils.TracksManager.TrackPoint;
import com.xunce.electrombile.R;
import com.xunce.electrombile.fragment.MaptabFragment;
import com.xunce.electrombile.xpg.common.useful.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by heyukun on 2015/4/18.
 */
public class RecordActivity extends Activity{

    private final String TAG = "RecordActivity";
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

    //查询的开始和结束时间
    Date startT;
    Date endT;

    //等待对话框
    private ProgressDialog watiDialog;

    //生成动态数组，加入数据
    ArrayList<HashMap<String, Object>> listItem;

    //数据适配器
    SimpleAdapter listItemAdapter;

    boolean firstCallback = true;
    Calendar can;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initView();
        setCustonViewVisibility(false);
        m_listview.setVisibility(View.INVISIBLE);

        tracksManager = new TracksManager();
        can = Calendar.getInstance();
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

                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                GregorianCalendar gcStart = new GregorianCalendar(can.get(Calendar.YEAR), can.get(Calendar.MONTH), can.get(Calendar.DAY_OF_MONTH) - 1);
                startT = gcStart.getTime();
                GregorianCalendar gcEnd = new GregorianCalendar(can.get(Calendar.YEAR), can.get(Calendar.MONTH), can.get(Calendar.DAY_OF_MONTH)  + 1);
                endT = gcEnd.getTime();
//                try {
//                    startT = new SimpleDateFormat("dd/MM/yyyy").parse("24/04/2015").getTime() / 1000;
//                    endT = new SimpleDateFormat("dd/MM/yyyy").parse("25/04/2015").getTime() / 1000;
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                watiDialog = new ProgressDialog(getApplicationContext());
//                watiDialog.setCancelable(false);
//                watiDialog.setMessage("查询中，请稍候...");
//                watiDialog.show();
                findCloud(startT, endT);
            }
        });
        btnTwoDay = (Button)findViewById(R.id.btn_twoday);
        btnTwoDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_listview.setVisibility(View.VISIBLE);
                setCustonViewVisibility(false);

//                try {
//                    startT = new SimpleDateFormat("dd/MM/yyyy").parse("23/04/2015").getTime() / 1000;
//                    endT = new SimpleDateFormat("dd/MM/yyyy").parse("25/04/2015").getTime() / 1000;
//
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
                findCloud(startT, endT);
            }
        });
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


                String startS = ((dpBegin.getDayOfMonth() > 10)?(dpBegin.getDayOfMonth() + ""):("0"+dpBegin.getDayOfMonth())) + "/" +
                        ((dpBegin.getMonth() + 1 > 10)?((dpBegin.getMonth() + 1)+ ""):("0"+(dpBegin.getMonth() + 1))) + "/" + dpBegin.getYear();

                String endS = ((dpEnd.getDayOfMonth() > 10)?(dpEnd.getDayOfMonth() + ""):("0"+dpEnd.getDayOfMonth())) + "/" +
                                ((dpEnd.getMonth() + 1 > 10)?((dpEnd.getMonth() + 1)+ ""):("0"+(dpEnd.getMonth() + 1))) + "/" + dpEnd.getYear();
                Log.i("______", "start:" + startS + "end:" + endS);
//                try {
////                startT = new SimpleDateFormat("dd/MM/yyyy").parse(startS).getTime() / 1000;
////                endT = new SimpleDateFormat("dd/MM/yyyy").parse(endS).getTime() / 1000;
//
//                } catch (ParseException e) {
//                e.printStackTrace();
//                }
                findCloud(startT, endT);
            }
        });

        //绑定Layout里面的ListView
        m_listview = (ListView) findViewById(R.id.listview);

        //生成动态数组，加入数据
        listItem = new ArrayList<HashMap<String, Object>>();
        //生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this,listItem,//数据源
                R.layout.listview_item,//ListItem的XML实现
                //动态数组与ImageItem对应的子项
                new String[] {"ItemTotalTime", "ItemStartTime", "ItemEndTime", "ItemDistance"},
                //,两个TextView ID
                new int[] {R.id.ItemTotalTime,R.id.ItemStartTime, R.id.ItemEndTime, R.id.ItemDistance}
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

    private void findCloud(Date st, Date et) {
        AVQuery<AVObject> query = new AVQuery<AVObject>("GPS");
        query.setLimit(1000);
        query.whereGreaterThan("createdAt", startT);
        query.whereLessThan("createdAt", endT);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> avObjects, AVException e) {
                if(e == null){
                    tracksManager.clearTracks();
                    List<AVObject> gpsData = avObjects;
                    tracksManager.setTranks(gpsData);
                    updateListView();
                    listItemAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void updateListView(){
        Log.i(TAG, "update list View");
        listItem.clear();
        for(int i=0;i<tracksManager.getTracks().size();i++)
        {
            ArrayList<TrackPoint> trackList = tracksManager.getTracks().get(i);
            TrackPoint startP = trackList.get(0);
            TrackPoint endP = trackList.get(trackList.size() - 1);
            Long diff = (endP.time.getTime() - startP.time.getTime()) / 1000;
            long days = diff / (60 * 60 * 24);
            long hours = (diff-days*(60 * 60 * 24))/(60 * 60);
            long minutes = (diff-days*( 60 * 60 * 24)-hours*(60 * 60))/(60);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemTotalTime", "历时:" + days + "天" + hours +"小时" + minutes + "分钟");
            map.put("ItemStartTime", "开始时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startP.time));
            map.put("ItemEndTime", "结束时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endP.time));
            map.put("ItemDistance", "距离:" + DistanceUtil.getDistance(startP.point, endP.point));
            listItem.add(map);
        }

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
