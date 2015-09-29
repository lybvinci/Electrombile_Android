/**
 * Project Name:XPGSdkV4AppBase
 * File Name:SettingManager.java
 * Package Name:com.gizwits.framework.sdk
 * Date:2015-1-27 14:47:24
 * Copyright (c) 2014~2015 Xtreme Programming Group, Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.xunce.electrombile.manager;

import android.content.Context;
import android.content.SharedPreferences;

// TODO: Auto-generated Javadoc

/**
 * SharePreference处理类.
 *
 * @author Sunny Ding
 */
public class SettingManager {

    public final String ALARMFLAG = "alarmFlag";
    /**
     * The share preferences.
     */
    private final String SHARE_PREFERENCES = "set";

    // =================================================================
    //
    // SharePreference文件中的变量名字列表
    //
    // =================================================================

    // Sharepreference文件的名字
    /**
     * The user name.
     */
    private final String USER_NAME = "username";
    // 用户名
    /**
     * The phone num.
     */
    private final String PHONE_NUM = "phonenumber";
    // 手机号码
    /**
     * The password.
     */
    private final String PASSWORD = "password";
    // 密码
    /**
     * The token.
     */
    private final String TOKEN = "token";
    // 用户名
    //did
    private final String IMEI = "imie";
    private final String PHOTO_FLAG = "photoFlag";
    //TracksBean
    private final String TRACKSDATA = "tracksData";
    //用户的初始位置
    private final String Lat = "lat";
    private final String Longitude = "longitude";

    //添加SOS管理员
    //public final String SOS = "sos";
    /**
     * The spf.
     */
    SharedPreferences spf;
    /**
     * The c.
     */
    private Context c;


    /**
     * Instantiates a new setting manager.
     *
     * @param c the c
     */
    public SettingManager(Context c) {
        this.c = c;
        spf = c.getSharedPreferences(SHARE_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * SharePreference clean.
     */
    public void cleanAll() {
        setToken("");
        setPhoneNumber("");
        setPassword("");
        setUserName("");
        setIMEI("");
        setAlarmFlag(false);
        setInitLocation("", "");
    }

    public void cleanDevice() {
        setIMEI("");
        setInitLocation("", "");
    }

    public void setInitLocation(String lat, String longitude) {
        spf.edit().putString(Lat, lat).commit();
        spf.edit().putString(Longitude, longitude).commit();
    }

    public String getInitLocationLat() {
        return spf.getString(Lat, "");
    }

    public String getInitLocationLongitude() {
        return spf.getString(Longitude, "");
    }

    public boolean getAlarmFlag() {
        return spf.getBoolean(ALARMFLAG, false);
    }

    public void setAlarmFlag(boolean alarmFlag) {
        spf.edit().putBoolean(ALARMFLAG, alarmFlag).commit();
    }

    /**
     * Gets the user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return spf.getString(USER_NAME, "");
    }

    /**
     * Sets the user name.
     *
     * @param name the new user name
     */
    public void setUserName(String name) {
        spf.edit().putString(USER_NAME, name).commit();

    }

    /**
     * Gets the phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        return spf.getString(PHONE_NUM, "");
    }

    /**
     * Sets the phone number.
     *
     * @param phoneNumber the new phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        spf.edit().putString(PHONE_NUM, phoneNumber).commit();
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public String getPassword() {
        return spf.getString(PASSWORD, "");
    }

    /**
     * Sets the password.
     *
     * @param psw the new password
     */
    public void setPassword(String psw) {
        spf.edit().putString(PASSWORD, psw).commit();
    }

    /**
     * Sets the token.
     *
     * @param token the new token
     */
    public void setToken(String token) {
        spf.edit().putString(TOKEN, token).commit();
    }

    public String getIMEI() {
        return spf.getString(IMEI, "");
    }

    public void setIMEI(String did) {
        spf.edit().putString(IMEI, did).commit();
    }

    public int getPersonCenterImage() {
        return spf.getInt(PHOTO_FLAG, 0);
    }

    public void setPersonCenterImage(int did) {
        spf.edit().putInt(PHOTO_FLAG, did).commit();
    }

}
