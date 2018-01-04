package com.ven.pos.updata;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class Version_util {
    // 获取版本名称
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    // 版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    public String getAppInfo(Context context) {
        try {
            String pkName = context.getPackageName();
            return pkName;
        } catch (Exception e) {

        }
        return null;
    }
}
