/**
 * Project Name:XPGSdkV4AppBase
 * File Name:JsonKeys.java
 * Package Name:com.gizwits.framework.config
 * Date:2015-1-27 14:47:10
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
package com.xunce.electrombile.Base.config;

// TODO: Auto-generated Javadoc
/**
 * 
 * ClassName: Class JsonKeys. <br/>
 * Json对应字段表<br/>
 * 
 * @author Lien
 */
public class JsonKeys {

	/** 产品名. */
	public final static String PRODUCT_NAME = "安全宝";

	/** 实体字段名，代表对应的项目. */
	public final static String KEY_ACTION = "electrombile";

    //GPRS 定时发送设置
    public final static String GPRS_SEND = "TIMER,30#";

    // 设置 SOS  管理员  增加号码需要在打包函数的remarks里面设置电话号码，以#结束
    public final static String SOS_ADD = "SOS,A,";

    //7 、删除 SOS  管理员  删除号码需要在打包函数的remarks里面设置电话号码，以#结束
    public final static String SOS_DELETE = "SOS,D,";

    //10 、工作模式设置
    /*
    *  0# 追踪模式：GPS 一直开启；
    *  1# 智能省电：设备静止时 GPS 关闭，运动或被查询位置时，GPS 会开启； （有些设备有传感器检测自身运动状态）
    *  2# 睡眠模式：被查询位置时，GPS 会开启；
    *  3# 冬眠模式：GPS 一直关闭；
    *  若设置成功，设备会回复：SET SAVING OK
    */
    public final static String MODE_SET_0 = "SAVING,0#";
    public final static String MODE_SET_1 = "SAVING,1#";
    public final static String MODE_SET_2 ="SAVING,2#";
    public final static String MODE_SET_3 = "SAVING,3#";

    //13 、添加电子围栏FENCE,< 围栏编号 >,< 围栏标记 >,<围栏参数 1>,< 围栏参数 2>,< 围栏参数3>,< 围栏参数 4>#
    //设定 1 号圆形围栏，OR 报警方式：出围栏报警；以当前点为圆心，半径 500 米。
    public final static String FENCE_SET_1 = "FENCE,1,OR,,,500#";

    //14 、删除电子围栏
    public final static String FENCE_DELETE = "FENCE,0#";

    //21 、重启设备    设置成功，设备会回复：RESET OK
    public final static String RESET = "RESET#";

    //25 、查询经纬度  设备会回复：Lat:N22.55787,Lon:E113.93509,Course:0.00,Speed:0.00km/h,DateTime:2014-12-12 07:32:133426
    public final static String WHERE = "WHERE#";


//	/** 开关. */
//	public final static String ON_OFF = "switch";
//
//	/** 定时开机. */
//	public final static String TIME_ON = "on_timing";
//
//	/** 定时关机. */
//	public final static String TIME_OFF = "off_timing";
//	/**
//	 * 模式 0.制冷, 1.送风, 2.除湿, 3.自动
//	 */
//	public final static String MODE = "mode";
//
//	/** 设定温度 16 - 30. */
//	public final static String SET_TEMP = "set_temp";
//	/**
//	 * 风速 0.低风, 1.中风, 2.高风
//	 */
//	public final static String FAN_SPEED = "fan_speed";
//
//	/** 摆风. */
//	public final static String FAN_SHAKE = "fan_swing";
//
//	/** 室内温度 -10 - 35. */
//	public final static String ROOM_TEMP = "room_temp";
//
//	/** 停机报警. */
//	public final static String ALARM_SHUTDOWM = "alert_shutdown";
//
//	/** 水满报警. */
//	public final static String ALARM_FULL = "alert_full";
//
//	/** 室温故障. */
//	public final static String FAULT_ROOMTEMP = "fault_roomtemp";
}
