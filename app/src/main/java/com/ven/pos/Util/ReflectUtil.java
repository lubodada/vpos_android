package com.ven.pos.Util;

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by fengshuopeng on 2017/5/31.
 */

public class ReflectUtil {
    public static String TAG = "huiyinPos";
    public static boolean isHFPos;

    public static String getSystemProperty(String name) {
        Object obj = reflectInvoke("android.os.SystemProperties", "get", new Class[]{String.class}, name);
        if (obj != null)
            return (String) obj;
        else
            return null;
    }

    public static Object reflectInvoke(String className, String method, Class[] parameterTypes, Object args) {
        try {
            Class mClass = Class.forName(className);
            Object cInstance = mClass.newInstance();
            Method mMethod = mClass.getMethod(method, parameterTypes);
            return mMethod.invoke(cInstance, args);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean getWizarType() {
        String model = getSystemProperty("ro.product.model");
        if (TextUtils.isEmpty(model))
            isHFPos = false;
        Log.i(TAG, "ro.product.model:" + model);

        model = model.toUpperCase();
        if (model.equals("WIZARPOS 1") || model.equals("WIZARPOS_1")) {
            isHFPos = true;
        } else if (model.equals("WIZARHAND Q1") || model.equals("WIZARHAND_Q1")) {
            isHFPos = true;
        } else if (model.equals("WIZARHAND_Q2") || model.equals("Q2")) {
            isHFPos = true;
        } else {
            isHFPos = false;
        }
        Log.i(TAG, "wizartype:" + model);

        return isHFPos;
    }
}
