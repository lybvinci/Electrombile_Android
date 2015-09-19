package com.xunce.electrombile.utils.device;

import android.os.Environment;

/**
 * Created by lybvinci on 2015/9/19.
 */
public class SDCardUtils {
    /**
     * 检查是否存在SDCard
     * @return
     */
    public static boolean hasSdcard(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }
}
