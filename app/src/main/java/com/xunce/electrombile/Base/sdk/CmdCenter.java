/**
 * Project Name:XPGSdkV4AppBase
 * File Name:CmdCenter.java
 * Package Name:com.gizwits.framework.sdk
 * Date:2015-1-27 14:47:19
 * Copyright (c) 2014~2015 Xtreme Programming Group, Inc.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.xunce.electrombile.Base.sdk;

import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.xunce.electrombile.Base.config.Configs;
import com.xunce.electrombile.Base.config.JsonKeys;
import com.xtremeprog.xpgconnect.XPGWifiDevice;
import com.xtremeprog.xpgconnect.XPGWifiSDK;
import com.xtremeprog.xpgconnect.XPGWifiSDK.XPGWifiConfigureMode;
import com.xunce.electrombile.xpg.common.useful.JSONUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.xunce.electrombile.xpg.common.useful.ByteUtils.Bytes2HexString;
import static com.xunce.electrombile.xpg.common.useful.ByteUtils.HexString2Bytes;
import static com.xunce.electrombile.xpg.common.useful.ByteUtils.judgeLength;


/**
 * ClassName: Class CmdCenter.
 * 控制指令类
 * @author lyb
 */
public class CmdCenter {
	
	/** The Constant TAG. */
	private static final String TAG = "CmdCenter";

	/**
	 * The xpg wifi sdk.
	 */
	private static XPGWifiSDK xpgWifiGCC;

	/**
	 * The m center.
	 */
	private static CmdCenter mCenter;

	/**
	 * The m setting manager.
	 */
	private SettingManager mSettingManager;

    //报警用的标志位
    public static boolean alarmFlag = false;

	/**
	 * Instantiates a new cmd center.
	 * 
	 * @param c
	 *            the c
	 */
	private CmdCenter(Context c) {
		if (mCenter == null) {
			init(c);
		}
	}

	/**
	 * Gets the single instance of CmdCenter.
	 * 
	 * @param c
	 *            the c
	 * @return single instance of CmdCenter
	 */
	public static CmdCenter getInstance(Context c) {
		if (mCenter == null) {
			mCenter = new CmdCenter(c);
		}
		return mCenter;
	}

	/**
	 * Inits the.
	 * 
	 * @param c
	 *            the c
	 */
	private void init(Context c) {
		mSettingManager = new SettingManager(c);

		xpgWifiGCC = XPGWifiSDK.sharedInstance();

	}

	/**
	 * Gets the XPG wifi sdk.
	 * 
	 * @return the XPG wifi sdk
	 */
	public XPGWifiSDK getXPGWifiSDK() {
		return xpgWifiGCC;
	}

	// =================================================================
	//
	// 关于账号的指令
	//
	// =================================================================

	/**
	 * 注册账号.
	 * 
	 * @param phone
	 *            注册手机号
	 * @param code
	 *            验证码
	 * @param password
	 *            注册密码
	 */
	public void cRegisterPhoneUser(String phone, String code, String password) {
		xpgWifiGCC.registerUserByPhoneAndCode(phone, password, code);
	}

	/**
	 * C register mail user.
	 *
	 * @param mailAddr the mail addr
	 * @param password the password
	 */
	public void cRegisterMailUser(String mailAddr, String password) {
		xpgWifiGCC.registerUserByEmail(mailAddr, password);
	}

	/**
	 * 匿名登录
	 * <p/>
	 * 如果一开始不需要直接注册账号，则需要进行匿名登录.
	 */
	public void cLoginAnonymousUser() {
		xpgWifiGCC.userLoginAnonymous();
	}

	/**
	 * 账号注销.
	 */
	public void cLogout() {
		Log.e(TAG, "cLogout:uesrid=" + mSettingManager.getUid());
		xpgWifiGCC.userLogout(mSettingManager.getUid());
	}

	/**
	 * 账号登陆.
	 * 
	 * @param name
	 *            用户名
	 * @param psw
	 *            密码
	 */
	public void cLogin(String name, String psw) {
		xpgWifiGCC.userLoginWithUserName(name, psw);
	}

	/**
	 * 忘记密码.
	 * 
	 * @param phone
	 *            手机号
	 * @param code
	 *            验证码
	 * @param newPassword
	 *            the new password
	 */
	public void cChangeUserPasswordWithCode(String phone, String code,
			String newPassword) {
		xpgWifiGCC.changeUserPasswordByCode(phone, code, newPassword);
	}

	/**
	 * 修改密码.
	 * 
	 * @param token
	 *            令牌
	 * @param oldPsw
	 *            旧密码
	 * @param newPsw
	 *            新密码
	 */
	public void cChangeUserPassword(String token, String oldPsw, String newPsw) {
		xpgWifiGCC.changeUserPassword(token, oldPsw, newPsw);
	}

	/**
	 * 根据邮箱修改密码.
	 *
	 * @param email            邮箱地址
	 */
	public void cChangePassworfByEmail(String email) {
		xpgWifiGCC.changeUserPasswordByEmail(email);
	}

	/**
	 * 请求向手机发送验证码.
	 * 
	 * @param phone
	 *            手机号
	 */
	public void cRequestSendVerifyCode(String phone) {
		xpgWifiGCC.requestSendVerifyCode(phone);
	}

	/**
	 * 发送airlink广播，把需要连接的wifi的ssid和password发给模块。.
	 * 
	 * @param wifi
	 *            wifi名字
	 * @param password
	 *            wifi密码
	 */
	public void cSetAirLink(String wifi, String password) {
		xpgWifiGCC.setDeviceWifi(wifi, password,
				XPGWifiConfigureMode.XPGWifiConfigureModeAirLink, 60);
	}

	/**
	 * softap，把需要连接的wifi的ssid和password发给模块。.
	 * 
	 * @param wifi
	 *            wifi名字
	 * @param password
	 *            wifi密码
	 */
	public void cSetSoftAp(String wifi, String password) {
		xpgWifiGCC.setDeviceWifi(wifi, password,
                XPGWifiConfigureMode.XPGWifiConfigureModeSoftAP, 30);
	}

	/**
	 * 绑定后刷新设备列表，该方法会同时获取本地设备以及远程设备列表.
	 * 
	 * @param uid
	 *            用户名
	 * @param token
	 *            令牌
	 */
	public void cGetBoundDevices(String uid, String token) {
		xpgWifiGCC.getBoundDevices(uid, token, Configs.PRODUCT_KEY);
		// xpgWifiSdk.getBoundDevices(uid, token);
	}

	/**
	 * 绑定设备.
	 * 
	 * @param uid
	 *            用户名
	 * @param token
	 *            密码
	 * @param did
	 *            did
	 * @param passcode
	 *            passcode
	 * @param remark
	 *            备注
	 */
	public void cBindDevice(String uid, String token, String did,
			String passcode, String remark) {

		xpgWifiGCC.bindDevice(uid, token, did, passcode, remark);
	}

	// =================================================================
	//
	// 关于控制设备的指令
	//
	// =================================================================

	/**
	 * 发送指令.
	 *
	 * @param xpgWifiDevice            the xpg wifi device
	 * @param key the key
	 * @param value the value
	 */
	public void cWrite(XPGWifiDevice xpgWifiDevice, String key, Object value) {

		try {
			final JSONObject jsonSend = new JSONObject();
			JSONObject jsonParam = new JSONObject();
			jsonSend.put("cmd", 1);
			jsonParam.put(key, value);
			jsonSend.put(JsonKeys.KEY_ACTION, jsonParam);
			Log.i("sendjson", jsonSend.toString());
			xpgWifiDevice.write(jsonSend.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取设备状态.
	 *
	 * @param xpgWifiDevice            the xpg wifi device
	 */
	public void cGetStatus(XPGWifiDevice xpgWifiDevice) {
		JSONObject json = new JSONObject();
		try {
			json.put("cmd", 2);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		xpgWifiDevice.write(json.toString());
	}

    private String packetOrder(String order,String remarks){
        order = order + remarks;
        char[] frameHead = {0x0000,0x0003};
        byte[] flags = {0x00};
        char[] cmd = {0x0090};
        byte[] orderByte = order.getBytes();
        int length = flags.length + cmd.length + orderByte.length;
        byte[] len = judgeLength(length);
        String orderData1 = new String(frameHead);
        String orderData2 = new String(len);
        String orderData3 = new String(flags);
        String orderData4 = new String(cmd);
        String orderData = orderData1 + orderData2 + orderData3 + orderData4 + order;
        Log.i("OrderData:::",orderData);
        return orderData;
    }

	/**
	 * 断开连接.
	 * 
	 * @param xpgWifiDevice
	 *            the xpg wifi device
	 */
	public void cDisconnect(XPGWifiDevice xpgWifiDevice) {
		xpgWifiDevice.disconnect();
     //   xpgWifiDevice = null;
	}

	/**
	 * 解除绑定.
	 * 
	 * @param uid
	 *            the uid
	 * @param token
	 *            the token
	 * @param did
	 *            the did
	 * @param passCode
	 *            the pass code
	 */
	public void cUnbindDevice(String uid, String token, String did,
			String passCode) {
        mSettingManager.cleanDevice();
        xpgWifiGCC.unbindDevice(uid, token, did, passCode);

	}

	/**
	 * 更新备注.
	 * 
	 * @param uid
	 *            the uid
	 * @param token
	 *            the token
	 * @param did
	 *            the did
	 * @param passCode
	 *            the pass code
	 * @param remark
	 *            the remark
	 */
	public void cUpdateRemark(String uid, String token, String did,
			String passCode, String remark) {
		xpgWifiGCC.bindDevice(uid, token, did, passCode, remark);
	}

    //安全宝相关

    //3 、GPRS 定时发送设置
    public void cGprsSend(XPGWifiDevice xpgWifiDevice){
        Log.i("CmdCenterAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",xpgWifiDevice.toString() +"qqqqqqqqqqqqqqqqqqqqqqq");
        String data = packetOrder(JsonKeys.GPRS_SEND,"");
        Log.i("定时发送设置",data);
        xpgWifiDevice.write(data);
    }

    //6 、设置 SOS 管理员
    public void cSOSManagerAdd(XPGWifiDevice xpgWifiDevice, String phoneNumber){
        phoneNumber = phoneNumber + "#";
        String data = packetOrder(JsonKeys.SOS_ADD,phoneNumber);
        xpgWifiDevice.write(data);
    }

    //7 、删除 SOS  管理员
    public void cSOSManagerDelete(XPGWifiDevice xpgWifiDevice, String phoneNumber){
        phoneNumber = phoneNumber + "#";
        String data = packetOrder(JsonKeys.SOS_DELETE,phoneNumber);
        xpgWifiDevice.write(data);
    }

    //10 工作模式设置
     /*
    *  0# 追踪模式：GPS 一直开启；
    *  1# 智能省电：设备静止时 GPS 关闭，运动或被查询位置时，GPS 会开启； （有些设备有传感器检测自身运动状态）
    *  2# 睡眠模式：被查询位置时，GPS 会开启；
    *  3# 冬眠模式：GPS 一直关闭；
    *  若设置成功，设备会回复：SET SAVING OK
    */
    //模式一
    public void cModeSet0(XPGWifiDevice xpgWifiDevice){
        String data = packetOrder(JsonKeys.MODE_SET_0,"");
        xpgWifiDevice.write(data);
    }
    //模式二
    public void cModeSet1(XPGWifiDevice xpgWifiDevice){
        String data = packetOrder(JsonKeys.MODE_SET_1,"");
        xpgWifiDevice.write(data);
    }
    //模式三
    public void cModeSet2(XPGWifiDevice xpgWifiDevice){
        String data = packetOrder(JsonKeys.MODE_SET_2,"");
        xpgWifiDevice.write(data);
    }
    //模式四
    public void cModeSet3(XPGWifiDevice xpgWifiDevice){
        String data = packetOrder(JsonKeys.MODE_SET_3,"");
        xpgWifiDevice.write(data);
    }

    //13 添加电子围栏
    public void cFenceAdd(XPGWifiDevice xpgWifiDevice){
        String data = packetOrder(JsonKeys.FENCE_SET_1,"");
        xpgWifiDevice.write(data);
    }

    //14删除电子围栏
    public void cFenceDelete(XPGWifiDevice xpgWifiDevice){
        String data = packetOrder(JsonKeys.FENCE_DELETE,"");
        xpgWifiDevice.write(data);
    }

    //21 重启设备
    public void cResetDevice(XPGWifiDevice xpgWifiDevice){
        String data = packetOrder(JsonKeys.RESET,"");
        xpgWifiDevice.write(data);
    }

    //25 查询经纬度
    public void cWhere(XPGWifiDevice xpgWifiDevice){
        String data = packetOrder(JsonKeys.WHERE,"");
        xpgWifiDevice.write(data);
    }


    //解析收到的字符串 分解成命令
    public String cParseString(byte[] binary) {
        String str1 = Bytes2HexString(binary);
        Log.i("CmdCenter.....",str1);
        String s = str1.replaceAll(" ", "");
        Log.i("CmdCenter.....",s);
        byte[] buf1 = HexString2Bytes(s);
        String parseString = buf1.toString();
        Log.i("CmdCenter.....",parseString);
        if (parseString.contains("SET TIMER OK")) {
            return "SET_TIMER_OK";
        }
        if (parseString.contains("SET SOS OK")) {
            return "SET_SOS_OK";
        }
        if (parseString.contains("DEL SOS OK")) {
            return "DEL_SOS_OK";
        }
        if (parseString.contains("SET SAVING OK")) {
            return "SET_SAVING_OK";
        }
        if (parseString.contains("RESET OK")) {
            return "RESET_OK";
        }
        if(parseString.contains("Lat:")) {
            Pattern p = Pattern.compile("Lat:.*");
            Matcher m = p.matcher(parseString);
            if (m.find()) {
                String data = m.group();
                Log.i("gpsData...",data);
                return data;
            }
        }
        return null;
    }

    //解析gps数据
    public HashMap<String, String> parseAllData(String data){
        if(data != null ) {
            String entity0 = JSONUtils.ParseJSON(data,"entity0");
            Log.i("entity0.....",entity0);
            HashMap<String, String> hm = new HashMap<String, String>();
            String GPSStatus = JSONUtils.ParseJSON(entity0, JsonKeys.GPSSTATUS);
            String alarm = JSONUtils.ParseJSON(entity0, JsonKeys.ALARM);
            Log.i("alarm.....",alarm);
            String ci = JSONUtils.ParseJSON(entity0, JsonKeys.CI);
            String course = JSONUtils.ParseJSON(entity0, JsonKeys.COURSE);
            String lac = JSONUtils.ParseJSON(entity0, JsonKeys.LAC);
            String lat = JSONUtils.ParseJSON(entity0, JsonKeys.LAT);
            Log.i("lat.....",lat);
            String longitude = JSONUtils.ParseJSON(entity0, JsonKeys.LONG);
           // Log.i("longitude.....",longitude);
            String mcc = JSONUtils.ParseJSON(entity0, JsonKeys.MCC);
            String mnc = JSONUtils.ParseJSON(entity0, JsonKeys.MNC);
            String speed = JSONUtils.ParseJSON(entity0, JsonKeys.SPEED);
            hm.put(JsonKeys.GPSSTATUS,GPSStatus);
            hm.put(JsonKeys.ALARM,alarm);
            hm.put(JsonKeys.CI,ci);
            hm.put(JsonKeys.COURSE,course);
            hm.put(JsonKeys.LAC,lac);
            hm.put(JsonKeys.LAT,lat);
            hm.put(JsonKeys.LONG,longitude);
            hm.put(JsonKeys.MCC,mcc);
            hm.put(JsonKeys.MNC,mnc);
            hm.put(JsonKeys.SPEED,speed);
            return hm;
        }else{
            return null;
        }

//        String Lat = null;
//        String Lon = null;
//        String Course = null;
//        String Speed = null;
//        String DateTime = null;
//        Pattern pLat = Pattern.compile("(Lat:)(\\.*)(,)");
//        Pattern pLon = Pattern.compile("(Lon:)(\\.*)(,)");
//        Pattern pCourse = Pattern.compile("(Course:)(\\.*)(,)");
//        Pattern pSpeed = Pattern.compile("(Speed:)(\\.*)(,)");
//        Pattern pDateTime = Pattern.compile("(DateTime:)(\\.*)");
//        Matcher m = pLat.matcher(data);
//        if(m.find()){
//            Lat = m.group(2);
//        }
//        m = pLon.matcher(data);
//        if(m.find()){
//            Lon = m.group(2);
//        }
//        m = pCourse.matcher(data);
//        if(m.find()){
//            Course = m.group(2);
//        }
//        m = pSpeed.matcher(data);
//        if(m.find()){
//            Speed = m.group(2);
//        }
//        m = pDateTime.matcher(data);
//        if(m.find()){
//            DateTime = m.group(2);
//        }
//        if(Lat != null
//                && Lon != null
//                && Course != null
//                && Speed != null
//                && DateTime != null) {
//            HashMap<String, String> hm = new HashMap<String, String>();
//            hm.put("Lat", Lat);
//            hm.put("Lon", Lon);
//            hm.put("Course",Course);
//            hm.put("Speed",Speed);
//            hm.put("DateTime", DateTime);
//            return hm;
//        }
//        else{
//            return null;
//        }
    }

    //解析GPS数据
    public float parseGPSData(String gps){
        float data = Float.parseFloat(gps);
        int x =(int) data/60;
        float y = data - 60*x;
        y = y/60;
        return x+y;
    }

    public float parseGPSData(float gps){
        int x =(int) gps/60;
        float y = gps - 60*x;
        y = y/60;
        return x+y;
    }

    public LatLng convertPoint(LatLng sourcePoint){
        CoordinateConverter cdc = new CoordinateConverter();
        cdc.from(CoordinateConverter.CoordType.GPS);
        cdc.coord(sourcePoint);
        LatLng desPoint = cdc.convert();
        return desPoint;
    }

    public void cSwitchOn(XPGWifiDevice xpgWifiDevice, boolean isOn) {
		cWrite(xpgWifiDevice, JsonKeys.ON_OFF, isOn);
		cGetStatus(xpgWifiDevice);
	}





	// =================================================================
	//
	// 智能云空调控制相关
	//
	// =================================================================
//	/**
//	 * C switch on.
//	 *
//	 * @param xpgWifiDevice the xpg wifi device
//	 * @param isOn the is on
//	 */
//	public void cSwitchOn(XPGWifiDevice xpgWifiDevice, boolean isOn) {
//		cWrite(xpgWifiDevice, JsonKeys.ON_OFF, isOn);
//		cGetStatus(xpgWifiDevice);
//	}
//
//	/**
//	 * C set shake.
//	 *
//	 * @param xpgWifiDevice the xpg wifi device
//	 * @param isOn the is on
//	 */
//	public void cSetShake(XPGWifiDevice xpgWifiDevice, boolean isOn) {
//		cWrite(xpgWifiDevice, JsonKeys.FAN_SHAKE, isOn);
//		cGetStatus(xpgWifiDevice);
//	}
//
//	/**
//	 * C mode.
//	 *
//	 * @param xpgWifiDevice the xpg wifi device
//	 * @param mode the mode
//	 */
//	public void cMode(XPGWifiDevice xpgWifiDevice, int mode) {
//		cWrite(xpgWifiDevice, JsonKeys.MODE, mode);
//		cGetStatus(xpgWifiDevice);
//	}
//
//	/**
//	 * C fan speed.
//	 *
//	 * @param xpgWifiDevice the xpg wifi device
//	 * @param fanSpeed the fan speed
//	 */
//	public void cFanSpeed(XPGWifiDevice xpgWifiDevice, int fanSpeed) {
//		cWrite(xpgWifiDevice, JsonKeys.FAN_SPEED, fanSpeed);
//		cGetStatus(xpgWifiDevice);
//	}
//
//	/**
//	 * C time on.
//	 *
//	 * @param xpgWifiDevice the xpg wifi device
//	 * @param time the time
//	 */
//	public void cTimeOn(XPGWifiDevice xpgWifiDevice, int time) {
//		cWrite(xpgWifiDevice, JsonKeys.TIME_ON, time);
//		cGetStatus(xpgWifiDevice);
//	}
//
//	/**
//	 * C time off.
//	 *
//	 * @param xpgWifiDevice the xpg wifi device
//	 * @param time the time
//	 */
//	public void cTimeOff(XPGWifiDevice xpgWifiDevice, int time) {
//		cWrite(xpgWifiDevice, JsonKeys.TIME_OFF, time);
//		cGetStatus(xpgWifiDevice);
//	}
//
//	/**
//	 * C set temp.
//	 *
//	 * @param xpgWifiDevice the xpg wifi device
//	 * @param templature the templature
//	 */
//	public void cSetTemp(XPGWifiDevice xpgWifiDevice, int templature) {
//		cWrite(xpgWifiDevice, JsonKeys.SET_TEMP, templature);
//		cGetStatus(xpgWifiDevice);
//	}

}
