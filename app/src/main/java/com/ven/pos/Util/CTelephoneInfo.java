package com.ven.pos.Util;

import java.lang.reflect.Method;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class CTelephoneInfo {
	private static final String TAG = CTelephoneInfo.class.getSimpleName();
	private String imeiSIM1;// IMEI

	private String iDataConnected2 = "0";// sim2
	private static CTelephoneInfo CTelephoneInfo;
	private static Context mContext;

	private CTelephoneInfo() {
	}

	public synchronized static CTelephoneInfo getInstance(Context context) {
		if (CTelephoneInfo == null) {
			CTelephoneInfo = new CTelephoneInfo();
		}
		mContext = context;
		return CTelephoneInfo;
	}

	public String getImeiSIM1() {
		return imeiSIM1;
	}

	public boolean isDataConnected2() {
		if (TextUtils.equals(iDataConnected2, "2")
				|| TextUtils.equals(iDataConnected2, "1"))
			return true;
		else
			return false;
	}

	public void setCTelephoneInfo() {
		TelephonyManager telephonyManager = ((TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE));
		//
		CTelephoneInfo.imeiSIM1 = telephonyManager.getDeviceId();
		;

		try {
			CTelephoneInfo.imeiSIM1 = getOperatorBySlot(mContext,
					"getDeviceIdGemini", 0);

			CTelephoneInfo.iDataConnected2 = getOperatorBySlot(mContext,
					"getDataStateGemini", 1);
		} catch (GeminiMethodNotFoundException e) {
			e.printStackTrace();
			try {
				CTelephoneInfo.imeiSIM1 = getOperatorBySlot(mContext,
						"getDeviceId", 0);

				CTelephoneInfo.iDataConnected2 = getOperatorBySlot(mContext,
						"getDataState", 1);
			} catch (GeminiMethodNotFoundException e1) {
				e1.printStackTrace();
			}
		}

	}

	private static String getOperatorBySlot(Context context,
			String predictedMethodName, int slotID)
			throws GeminiMethodNotFoundException {
		String inumeric = null;
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			Class<?> telephonyClass = Class.forName(telephony.getClass()
					.getName());
			Class<?>[] parameter = new Class[1];
			parameter[0] = int.class;
			Method getSimID = telephonyClass.getMethod(predictedMethodName,
					parameter);
			Object[] obParameter = new Object[1];
			obParameter[0] = slotID;
			Object ob_phone = getSimID.invoke(telephony, obParameter);
			if (ob_phone != null) {
				inumeric = ob_phone.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeminiMethodNotFoundException(predictedMethodName);
		}
		return inumeric;
	}

	private static class GeminiMethodNotFoundException extends Exception {

		private static final long serialVersionUID = -3241033488141442594L;

		public GeminiMethodNotFoundException(String info) {
			super(info);
		}
	}

}
