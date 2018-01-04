package com.ven.pos.Util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;

import java.lang.reflect.Field;
import java.lang.reflect.Method;



/**
 * Created by Administrator on 2016/4/23.
 */
public class Util_Screen {
    private static Context context;
    private static SensorManager sensorManager;
    private static ScreenOrientationSensor screenOrientationSensor;

    public static void screenToFullOrLand(Context context, boolean isFullScreen, boolean isLandScreen) {
        if (isFullScreen)
            ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        else {
            WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
            lp.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            ((Activity) context).getWindow().setAttributes(lp);
            ((Activity) context).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        if (isLandScreen)
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static void screenToLand(Context context, boolean isLandScreen) {
        if (isLandScreen)
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static void screenToZoom(WebSettings webSettings, boolean enableZoom) {
        webSettings.setBuiltInZoomControls(enableZoom);
        webSettings.setSupportZoom(enableZoom);
        webSettings.setUseWideViewPort(enableZoom);
        webSettings.setDisplayZoomControls(enableZoom);
    }

    public enum Enum_BarFontColor {
        White, Black
    }



/*    public static void statusBarToTranslucentAndFits(Activity activity, int statusColor, boolean fitsSystemWindows) {
        if (activity instanceof Framework_Activity) {
            ((Framework_Activity) activity).getRootView().setFitsSystemWindows(fitsSystemWindows);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ((Framework_Activity) activity).getRootView().requestFitSystemWindows();
            }

        }
        statusBarToTranslucent(activity, statusColor, null);
    }*/




    public static class ScreenOrientationSensor {
        public void open() {
            sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_UI);
        }

        public void close() {
            if (sensorManager != null)
                sensorManager.unregisterListener(sensorEventListener);
        }
    }

    public static ScreenOrientationSensor screenOrientationBySensor(Context context) {
        Util_Screen.context = context;
        screenOrientationSensor = new ScreenOrientationSensor();
        return screenOrientationSensor;
    }

    private static SensorEventListener sensorEventListener = new SensorEventListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onSensorChanged(SensorEvent event) {
        /*    if (event.values[0] < 3 && event.values[0] > -3) {
                Util_Screen.screenToFullOrLand(context, false, false);
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else */
            if (event.values[0] >= 7) {
                ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else if (event.values[0] <= -7) {
                ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private static boolean FlymeSetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    private static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

}
