package com.jhj.Agreement.ZYB;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptTool {

	public CryptTool() {
	}

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString().toUpperCase();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static SecretKey genDESKey(byte[] key_byte) throws Exception {

		SecretKey k = null;
		k = new SecretKeySpec(key_byte, "DESede");
		return k;
	}

	public static SecretKey genDESKey() throws Exception {
		String keyStr = "$1#2@f3&4~6%7!a+*cd(e-h)";
		byte key_byte[] = keyStr.getBytes();// 3DES 24 bytes key
		SecretKey k = null;
		k = new SecretKeySpec(key_byte, "DESede");
		return k;
	}

	public static SecretKey genDESKey(String key) throws Exception {
		String keyStr = key;
		byte key_byte[] = keyStr.getBytes();
		SecretKey k = null;
		k = new SecretKeySpec(key_byte, "DESede");
		return k;
	}

	public static byte[] hexString2ByteArray(String strIn) throws Exception {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;

		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	public static byte[] desDecrypt(SecretKey key, byte[] crypt)
			throws Exception {
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("DESede");
		cipher.init(javax.crypto.Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(crypt);
	}

	public static String desDecrypt(SecretKey key, String crypt)
			throws Exception {
		return byteArrayToHexString(desDecrypt(key, crypt.getBytes("UTF-8")));
	}

	public static byte[] desEncrypt(SecretKey key, byte[] src) throws Exception {
		javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("DESede");
		cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(src);
	}

	public static String desEncrypt(SecretKey key, String src) throws Exception {
		return byteArrayToHexString(desEncrypt(key, src.getBytes("UTF-8")));
	}

	public static byte[] md5Digest(byte[] src) throws Exception {
		java.security.MessageDigest alg = java.security.MessageDigest
				.getInstance("MD5"); // MD5 is 16 bit message digest

		return alg.digest(src);
	}

	public static String md5Digest(String src) throws Exception {
		return byteArrayToHexString(md5Digest(src.getBytes()));
	}

	public static String md5Enc(String src) throws Exception {
		return byteArrayToHexString(md5Digest(src.getBytes()));
	}

	public static String urlEncode(String src) {
		try {
			src = java.net.URLEncoder.encode(src, "UTF-8");

			return src;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return src;
	}

	public static String urlDecode(String value) {
		try {
			return java.net.URLDecoder.decode(value, "UTF-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return value;
	}

	
	public static String getCurrentDate() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(cal.YEAR);
		int month = cal.get(cal.MONTH) + 1;
		int day = cal.get(cal.DAY_OF_MONTH);
		int hour = cal.get(cal.HOUR_OF_DAY);
		int minute = cal.get(cal.MINUTE);
		int second = cal.get(cal.SECOND);
		String cDate = Integer.toString(year);
		if (month < 10) {
			cDate = cDate + "0" + Integer.toString(month);
		} else {
			cDate = cDate + Integer.toString(month);
		}
		if (day < 10) {
			cDate = cDate + "0" + Integer.toString(day);
		} else {
			cDate = cDate + Integer.toString(day);
		}
		if (hour < 10) {
			cDate = cDate + "0" + Integer.toString(hour);
		} else {
			cDate = cDate + Integer.toString(hour);
		}
		if (minute < 10) {
			cDate = cDate + "0" + Integer.toString(minute);
		} else {
			cDate = cDate + Integer.toString(minute);
		}
		if (second < 10) {
			cDate = cDate + "0" + Integer.toString(second);
		} else {
			cDate = cDate + Integer.toString(second);
		}
		return cDate.trim();
	}

	public static String getTodayDate2() {

		Calendar RightNow = Calendar.getInstance();
		return changeDatetoString2(RightNow);
	}

	public static String changeDatetoString2(Calendar cDate) {
		int Year;
		int Month;
		int Day;
		String sDate = "";

		Year = cDate.get(Calendar.YEAR);
		Month = cDate.get(Calendar.MONTH) + 1;
		Day = cDate.get(Calendar.DAY_OF_MONTH);

		sDate = Integer.toString(Year);
		if (Month >= 10) {
			sDate = sDate + Integer.toString(Month);
		} else {
			sDate = sDate + "0" + Integer.toString(Month);
		}
		if (Day >= 10) {
			sDate = sDate + Integer.toString(Day);
		} else {
			sDate = sDate + "0" +

			Integer.toString(Day);
		}
		return sDate;
	}

	public static void main(String[] args) {
		try {

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	public static String transMapToString(Map map) {
		java.util.Map.Entry entry;
		StringBuffer sb = new StringBuffer();
		for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext();) {
			entry = (java.util.Map.Entry) iterator.next();
			sb.append(entry.getKey().toString())
					.append("'")
					.append(null == entry.getValue() ? "" : entry.getValue()
							.toString()).append(iterator.hasNext() ? "^" : "");
		}
		return sb.toString();
	}

	public static Map transStringToMap(String mapString) {
		Map map = new HashMap();
		java.util.StringTokenizer items;
		for (StringTokenizer entrys = new StringTokenizer(mapString, "^"); entrys
				.hasMoreTokens(); map.put(items.nextToken(),
				items.hasMoreTokens() ? ((Object) (items.nextToken())) : null))
			items = new StringTokenizer(entrys.nextToken(), "'");
		return map;
	}

}
