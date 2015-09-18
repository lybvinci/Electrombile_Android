package com.xunce.electrombile.utils.useful;

/**
 * <P>
 * byte数组操作类，包含byte数组的输出，格式化，转换等方法。
 * <P>
 * 
 * @author Lien Li
 * @version 1.00
 */
public class ByteUtils {
	/**
	 * 将指定字符串src，以每两个字符分割转换为16进制形式\n 如："2B44EFD9" to byte[]{0x2B, 0×44,
	 * 0xEF,0xD9}
	 * 
	 * @param src
	 *            String 传入的字符串
	 * @return byte[] 返回的数组
	 */
	public static byte[] HexString2Bytes(String src) {
		int leng = src.length() / 2;
		byte[] ret = new byte[leng];
		byte[] tmp = src.getBytes();
		for (int i = 0; i < leng; i++) {
			ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
		}
		return ret;
	}
    //代码转自：java int 与 byte转换
    public static byte[] toByteArray(int iSource, int iArrayLen) {
        byte[] bLocalArr = new byte[iArrayLen];
        for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
            bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
        }
        return bLocalArr;
    }

    //判断int大小从而转化成byte[];
    public static byte[] judgeLength(int length){
        if(length <= 127){
            byte[] len = toByteArray(length, 1);
            return len;
        }
        if(length >= 128 && length <= 16383){
            byte[] len= toByteArray(length,2);
            return len;
        }
        if(length >= 16384 && length <= 2097151){
            byte[] len = toByteArray(length,3);
            return len;
        }
        if(length >= 2097152 && length <= 268435455){
            byte[] len = toByteArray(length,4);
            return len;
        }
        return null;
    }
	/**
	 * 将两个ASCII字符合成一个字节； 如："EF" to 0xEF
	 * 
	 * @param src0
	 *            ASCII字符1
	 * @param src1
	 *            ASCII字符2
	 * @return byte
	 */
	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
				.byteValue();
		_b0 = (byte) (_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
				.byteValue();
		byte ret = (byte) (_b0 ^ _b1);
		return ret;
	}

	/**
	 * 将指定byte数组以16进制的形式打印到控制台
	 * 
	 * @param hint
	 *            标签
	 * @param b
	 *            需要打印的数组
	 */
	public static void printHexString(String hint, byte[] b) {
		System.out.print(hint);
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			System.out.print(hex.toUpperCase() + " ");
		}
		System.out.println("");
	}

	/**
	 * 将指定byte数组转换为16进制的形式
	 * 
	 * @param b
	 *            传入的数组
	 * @return String
	 */
	public static String Bytes2HexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			ret += hex.toUpperCase() + " ";
		}
		return ret;
	}

	/**
	 * 将指定int转换为16进制的形式
	 * 
	 * @param i
	 *            指定的整数
	 * @return String
	 */
	public static String int2HaxString(int i) {
		String hex = Integer.toHexString(i);
		if (hex.length() == 1) {
			hex = "0" + hex;
		}
		return hex;
	}

	/**
	 * 讲指定的short按位取值
	 * 
	 * @param n
	 *            指定的字节
	 * @param index
	 *            位数下标
	 * @return boolean
	 * */
	public static boolean getBitFromShort(int n, int index) {
		if (((n >> index) & 0x1) > 0)
			return true;
		else
			return false;
	}

	/**
	 * 十六进制转字符串
	 *
	 * @param hexString
	 *            十六进制字符串
	 * @param encodeType
	 *            编码类型4：Unicode，2：普通编码
	 * @return 字符串
	 */
	public static String hexStringToString(String hexString, int encodeType) {
		String result = "";
		int max = hexString.length() / encodeType;
		for (int i = 0; i < max; i++) {
			char c = (char) ByteUtils.hexStringToAlgorism(hexString
					.substring(i * encodeType, (i + 1) * encodeType));
			result += c;
		}
		return result;
	}
	/**
	 * 十六进制字符串装十进制
	 *
	 * @param hex
	 *            十六进制字符串
	 * @return 十进制数值
	 */
	public static int hexStringToAlgorism(String hex) {
		hex = hex.toUpperCase();
		int max = hex.length();
		int result = 0;
		for (int i = max; i > 0; i--) {
			char c = hex.charAt(i - 1);
			int algorism = 0;
			if (c >= '0' && c <= '9') {
				algorism = c - '0';
			} else {
				algorism = c - 55;
			}
			result += Math.pow(16, max - i) * algorism;
		}
		return result;
	}

	public static byte[] arrayCat(byte[] buf1,byte[] buf2)
	{
		byte[] bufret=null;
		int len1=0;
		int len2=0;
		if(buf1!=null)
			len1=buf1.length;
		if(buf2!=null)
			len2=buf2.length;
		if(len1+len2>0)
			bufret=new byte[len1+len2];
		if(len1>0)
			System.arraycopy(buf1,0,bufret,0,len1);
		if(len2>0)
			System.arraycopy(buf2,0,bufret,len1,len2);
		return bufret;
	}
}
