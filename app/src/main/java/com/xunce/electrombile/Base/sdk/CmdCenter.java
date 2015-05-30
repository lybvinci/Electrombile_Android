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

import com.avos.avoscloud.LogUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.xunce.electrombile.Base.config.JsonKeys;
import com.xunce.electrombile.xpg.common.useful.ByteUtils;
import com.xunce.electrombile.xpg.common.useful.JSONUtils;

import java.util.HashMap;

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
	 * The m center.
	 */
	private static CmdCenter mCenter;

	/**
	 * The m setting manager.
	 */
	private SettingManager mSettingManager;

    //报警用的标志位


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
	}

    private byte[] packetOrder(byte[] cmd,byte[] serial,String order,String remarks){
        order = order + remarks;
		//char frameHead = 0x2A55;
		byte[] frameHead = {-0x56,0x55};
        byte[] orderByte = order.getBytes();
        int length = 2 + orderByte.length;
		byte[] len ={(byte) (length>>8 & 0xff),(byte)(length & 0xff)};
		byte[] orderData = ByteUtils.arrayCat(frameHead,cmd);
		orderData = ByteUtils.arrayCat(orderData,len);
		orderData = ByteUtils.arrayCat(orderData,serial);
		orderData = ByteUtils.arrayCat(orderData,orderByte);
		//String len1 = len;
		//byte[] len ={(byte)(length>>8&0xFF),(byte)(length & 0xFF)};

//      //  String orderData1 = String.valueOf(frameHead);
		//String orderData1 = ByteUtils.hexStringToString(frameHead,2);
//		// String orderData2 = String.valueOf(cmd);
//		String orderData2 = ByteUtils.Bytes2HexString(cmd);
//       // String orderData3 = String.valueOf(len);
//		String orderData3 = ByteUtils.Bytes2HexString(len);
//		//String orderData4 = String.valueOf(serial);
//		String orderData4 = ByteUtils.Bytes2HexString(serial);
//		StringBuilder sb = new StringBuilder();
//		sb.append(frameHead);
//		sb.append(cmd);
//		sb.append(len);
//		sb.append(serial);
//		sb.append(order);
//		sb.append(orderData1);
//		sb.append(orderData2);
//		sb.append(orderData3);
//		sb.append(orderData4);
//		sb.append(order);
	//	String orderData = sb.toString();
       // String orderData = orderData1 + orderData2 + orderData3 +orderData4 + order;
		Log.i("OrderData:::",orderData.toString());
        return orderData;
    }


    //解析gps数据
	//返回经度
    public float parsePushServiceLat(byte[] mData){
		return (mData[9]&0xFF        |
				(mData[8]&0xFF) << 8  |
				(mData[7]&0xFF) << 16 |
				(mData[6]&0xFF) << 24 );
	}
	public int parsePushServiceLongInt(byte[] mData){
		return (mData[9]&0xFF        |
				(mData[8]&0xFF) << 8  |
				(mData[7]&0xFF) << 16 |
				(mData[6]&0xFF) << 24 );
	}
	//返回纬度
	public float parsePushServiceLong(byte[] mData){
		return (mData[13]&0xFF        |
				(mData[12]&0xFF) << 8  |
				(mData[11]&0xFF) << 16 |
				(mData[10]&0xFF) << 24 );
	}
	public int parsePushServiceLat2(byte[] mData){
		return (mData[13]&0xFF        |
				(mData[12]&0xFF) << 8  |
				(mData[11]&0xFF) << 16 |
				(mData[10]&0xFF) << 24 );
	}
	//返回方向
	public String parsePushServiceDirection(byte[] mData){
		return String.valueOf(mData[14]) + String.valueOf(mData[15]);
	}
	//返回速度
	public int parsePushServiceSpeed(byte[] mData){
		return Integer.valueOf(mData[16]);
	}
	//返回是否是GPS定位
	public boolean parsePushServiceIsGPS(byte[] mData){
		return Boolean.valueOf(String.valueOf(mData[17]));
	}
	//返回时间
//<<<<<<< HEAD
//	public int parsePushServiceTime(byte[] mData) {
//		return (mData[5] & 0xFF |
//				(mData[4] & 0xFF) << 8 |
//				(mData[3] & 0xFF) << 16 |
//				(mData[2] & 0xFF) << 24);
//=======
	public int parsePushServiceTime(byte[] mData){
		return (mData[5]&0xFF        |
                (mData[4]&0xFF) << 8  |
                (mData[3]&0xFF) << 16 |
                (mData[2]&0xFF) << 24 );
	}

    public float parseGPSData(float gps){
        int x =(int) gps/60;
        float y = gps - 60*x;
        y = y/60;
        return x+y;
    }

    public int parseGPSDataToInt(float gps){
        int x =(int) gps/60;
        float y = gps - 60*x;
        y = y/60;
        return (int)(x+y);
    }

    public LatLng convertPoint(LatLng sourcePoint){
        CoordinateConverter cdc = new CoordinateConverter();
        cdc.from(CoordinateConverter.CoordType.GPS);
        cdc.coord(sourcePoint);
        LatLng desPoint = cdc.convert();
        return desPoint;
    }

	//3 、GPRS 定时发送设置
	public void cGprsSend(byte[] serial){
		byte[] data = packetOrder(new byte[]{ 0x00,0x01},serial,JsonKeys.GPRS_SEND,"");
		Log.i("定时发送设置", data.toString());
		//发送数据
		//xpgWifiDevice.write(data);
	}

	//6 、设置 SOS 管理员
	public void cSOSManagerAdd(String phoneNumber){
		phoneNumber = phoneNumber + "#";
	//	String data = packetOrder(JsonKeys.SOS_ADD,phoneNumber);
		//xpgWifiDevice.write(data);
	}

	//7 、删除 SOS  管理员
	public void cSOSManagerDelete(String phoneNumber){
		phoneNumber = phoneNumber + "#";
	//	String data = packetOrder(JsonKeys.SOS_DELETE,phoneNumber);
		//xpgWifiDevice.write(data);
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
	public void cModeSet0(){
	//	String data = packetOrder(JsonKeys.MODE_SET_0,"");
		//xpgWifiDevice.write(data);
	}
	//模式二
	public void cModeSet1(){
	//	String data = packetOrder(JsonKeys.MODE_SET_1,"");
		//xpgWifiDevice.write(data);
	}
	//模式三
	public void cModeSet2(){
	//	String data = packetOrder(JsonKeys.MODE_SET_2,"");
	//	xpgWifiDevice.write(data);
	}
	//模式四
	public void cModeSet3(){
	//	String data = packetOrder(JsonKeys.MODE_SET_3,"");
	//	xpgWifiDevice.write(data);
	}

	//13 添加电子围栏
	public byte[] cFenceAdd(byte[] serial){
		byte[] data = packetOrder(new byte[]{ 0x00,0x01},serial,JsonKeys.FENCE_SET_1,"");
		return data;
	}

	//14删除电子围栏
	public byte[] cFenceDelete(byte[] serial){
		byte[] data = packetOrder(new byte[]{ 0x00,0x01},serial,JsonKeys.FENCE_DELETE,"");
		return data;
	}
	//查询电子围栏
	public byte[] cFenceSearch(byte[] serial){
		byte[] data = packetOrder(new byte[]{ 0x00,0x03},serial,"FENCE,1?","");
		return data;
	}
	//测试报警
	public byte[] cTest(byte[] serial){
		byte[] data = packetOrder(new byte[]{ 0x00,-1},serial,"AA","");
		return data;
	}

	//测试GPS
	public byte[] cTestGPS(byte[] serial){
		byte[] data = packetOrder(new byte[]{ 0x00,-2},serial,"AA","");
		return data;
	}

	//21 重启设备
	public void cResetDevice(){
	//	String data = packetOrder(JsonKeys.RESET,"");
		//xpgWifiDevice.write(data);
	}

	//25 查询经纬度
	public void cWhere(){
	//	String data = packetOrder(JsonKeys.WHERE,"");
		//xpgWifiDevice.write(data);
	}

	public byte[] getSerial(byte firstByte,byte secondByte){
		if(secondByte == 127){
			secondByte = 0x00;
			if(firstByte == 127){
				firstByte =0x00;
			}else {
				firstByte++;
			}
		}else{
			secondByte ++;
		}
		return new byte[]{firstByte,secondByte};
	}
}
