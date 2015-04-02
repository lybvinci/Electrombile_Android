package com.xunce.electrombile;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xunce.electrombile.widget.OverlayView;


/**
 * Created by heyukun on 2015/4/1.
 */
public class IncomingReceiver extends BroadcastReceiver {
    private static final String TAG = "IncomingReceiver";
    private static String incomingNumber = null;
    private Button btnWarmConfirm = null;
    @Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
        switch (tm.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.e(TAG, incomingNumber);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        showWindow(context, incomingNumber, R.layout.receiver_income);
                    }
                }, 100);
                break;
            case TelephonyManager.CALL_STATE_IDLE:// �Ҷϵ绰
                closeWindow(context);
                break;
        }

//        //btnWarmConfirm = getA
//        btnWarmConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e(TAG, "ffffffffffffffffk");
//            }
//        });
    }

    private void closeWindow(Context ctx){
        OverlayView.hide(ctx);
    }
    private void showWindow(Context ctx, String number, int layout) {
        OverlayView.show(ctx, number, layout);
    }
}
