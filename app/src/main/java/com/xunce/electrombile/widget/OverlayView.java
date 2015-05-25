package com.xunce.electrombile.widget;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.xunce.electrombile.R;

/**
 * Created by heyukun on 2015/4/1.
 */
public class OverlayView{
    static Button btn = null;
    public static ViewGroup mOverlay;
    private static WindowManager.LayoutParams params;
    public static void show(final Context context, String number, int layout){
        params = getShowingParams();
        WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mOverlay = (ViewGroup)inflater.inflate(layout, null);
        wm.addView(mOverlay, params);
    }

    /**
     * 挂断电话
     */
    static public void hide(Context ctx){
        if (mOverlay != null) {
                WindowManager wm = (WindowManager)ctx
                        .getSystemService(Context.WINDOW_SERVICE);
            LayoutInflater inflater = (LayoutInflater)ctx
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup mOverlay2 = (ViewGroup)inflater.inflate(R.layout.receiver_income, null);
            mOverlay2.removeAllViews();
            wm.removeView(mOverlay);
                //wm.removeViewImmediate(mOverlay);
            //    Log.e("", "closed?");
            mOverlay = null;
        }
    }
    /**
     * 获取显示参数
     *
     * @return
     */
    private static WindowManager.LayoutParams getShowingParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        // TYPE_TOAST TYPE_SYSTEM_OVERLAY 在其他应用上层 在通知栏下层 位置不能动鸟
        // TYPE_PHONE 在其他应用上层 在通知栏下层
        // TYPE_PRIORITY_PHONE TYPE_SYSTEM_ALERT 在其他应用上层 在通知栏上层 没试出来区别是啥
        // TYPE_SYSTEM_ERROR 最顶层(通过对比360和天天动听歌词得出)
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = 400;
        params.x = 0;
        params.y = 0;
        params.format = PixelFormat.RGBA_8888;// value = 1
        params.gravity = Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        return params;
    }


}
