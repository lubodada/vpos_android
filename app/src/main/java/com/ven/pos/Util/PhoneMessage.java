package com.ven.pos.Util;

import android.content.Context;
import android.telephony.TelephonyManager;

public class PhoneMessage {

    @SuppressWarnings("static-access")
    public static String Imei(Context context, int num) {
        String message = null;
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(context.TELEPHONY_SERVICE);
        if (num == 1) {
            message = tm.getDeviceId().trim();
        } else if (num == 2) {
            message = tm.getLine1Number();// �ֻ����
        } else if (num == 3) {
            message = tm.getSimSerialNumber();// IMEI
        } else if (num == 4) {
            message = tm.getSubscriberId();// IMSI
        }
        return message;
    }

}
