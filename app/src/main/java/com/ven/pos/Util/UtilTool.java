package com.ven.pos.Util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.cnyssj.pos.R;
import com.ven.pos.GlobalContant;
import com.ven.pos.LoginActivity;

public class UtilTool {
    public static final String BACK_ACTION_RECEIVER_SREING = "POS_BACK_ACTION";

    public static void HideSoftInputFromWindow(Activity paramActivity) {
        InputMethodManager localInputMethodManager = (InputMethodManager) paramActivity
                .getSystemService("input_method");
        if ((localInputMethodManager != null)
                && (localInputMethodManager.isActive())) {
        }
        try {
            localInputMethodManager.hideSoftInputFromWindow(paramActivity
                    .getCurrentFocus().getApplicationWindowToken(), 2);
            return;
        } catch (Exception localException) {
        }
    }

    public static double chanageNumEnd(double paramDouble) {
        return Double.parseDouble(new DecimalFormat("#0.00")
                .format(paramDouble));
    }

    public static String chanageNumEndTwo(double paramDouble) {
        return new DecimalFormat("#0.00").format(paramDouble);
    }

    public static boolean cheakStringLength(String paramString, int paramInt) {
        return paramString.length() <= paramInt;
    }

    public static boolean checkApkExist(Context paramContext, String paramString) {
        if ((paramString == null) || ("".equals(paramString))) {
        }
        while (paramContext.getPackageManager()
                .queryIntentActivities(new Intent(paramString), 32).size() == 0) {
            return false;
        }
        return true;
    }

    public static boolean checkPhone(String paramString) {
        return Pattern.compile("^1\\d{10}$").matcher(paramString).matches();
    }

    public static String deductZeroAndPoint(String paramString) {
        if (paramString.indexOf(".") > 0) {
            paramString = paramString.replaceAll("0+?$", "").replaceAll("[.]$",
                    "");
        }
        return paramString;
    }

    public static String getAppVersionName(Context paramContext) {
        String str = "";
        try {
            str = paramContext.getPackageManager().getPackageInfo(
                    paramContext.getPackageName(), 0).versionName;
            if (str != null) {
                int i = str.length();
                if (i > 0) {
                }
            } else {
                return "";
            }
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return str;
    }

    public static float getDivReslut(String paramString1, String paramString2) {
        float f = Float.parseFloat(paramString1)
                / Float.parseFloat(paramString2);
        return Float.parseFloat(new DecimalFormat("0.00").format(f));
    }

    public static String getExpriredtime(int paramInt) {
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.add(12, paramInt);
        return new SimpleDateFormat("yyyyMMddHHmmss").format(localCalendar
                .getTime());
    }

    public static void getFocus(View paramView) {
        paramView.setFocusable(true);
        paramView.setFocusableInTouchMode(true);
        paramView.requestFocus();
        paramView.requestFocusFromTouch();
    }

    public static int getListViewHeight(ListView paramListView) {
        ListAdapter localListAdapter = paramListView.getAdapter();
        if (localListAdapter == null) {
            return 0;
        }
        int i = 0;
        for (int j = 0; ; j++) {
            if (j >= localListAdapter.getCount()) {
                return i + paramListView.getDividerHeight()
                        * (-1 + localListAdapter.getCount());
            }
            View localView = localListAdapter.getView(j, null, paramListView);
            localView.measure(0, 0);
            i += localView.getMeasuredHeight();
        }
    }

    public static String getNowTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static Bitmap getPrintLogo(Bitmap paramBitmap) {
        int i = paramBitmap.getWidth();
        int j = paramBitmap.getHeight();
        float f1 = 150.0F * j / i;
        float f2 = 150.0F / i;
        float f3 = f1 / j;
        Matrix localMatrix = new Matrix();
        localMatrix.postScale(f2, f3);
        return Bitmap.createBitmap(paramBitmap, 0, 0, i, j, localMatrix, true);
    }

    public static String getRandom(int paramInt) {
        StringBuilder localStringBuilder = new StringBuilder();
        for (int i = 0; ; i++) {
            if (i >= paramInt) {
                return localStringBuilder.toString();
            }
            localStringBuilder.append((int) (10.0D * Math.random()));
        }
    }

    public static void hideDialogInput(View paramView) {
        InputMethodManager localInputMethodManager = (InputMethodManager) paramView
                .getContext().getSystemService("input_method");
        if (localInputMethodManager.isActive()) {
            localInputMethodManager.hideSoftInputFromWindow(
                    paramView.getApplicationWindowToken(), 0);
        }
    }

    public static boolean isNetworkConnected(Context paramContext) {
        if (paramContext != null) {
            NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                    .getSystemService("connectivity")).getActiveNetworkInfo();
            if (localNetworkInfo != null) {
                return localNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isNumeric(String paramString) {
        if (paramString.contains(".")) {
            return Pattern.compile("^\\d+\\.\\d+$").matcher(paramString)
                    .matches();
        }
        return Pattern.compile("^\\d+$").matcher(paramString).matches();
    }

    public static String pointNumber(String paramString) {
        if ((paramString.indexOf(".") > 0)
                && (paramString.substring(1 + paramString.indexOf("."))
                .length() > 4)) {
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = Double.valueOf(Double.parseDouble(paramString));
            paramString = String.format("%.4f", arrayOfObject);
        }
        return paramString;
    }

    public static void setListViewHeightBasedOnChildren(ListView paramListView) {
        ListAdapter localListAdapter = paramListView.getAdapter();
        if (localListAdapter == null) {
            return;
        }
        int i = 0;
        for (int j = 0; ; j++) {
            if (j >= localListAdapter.getCount()) {
                ViewGroup.LayoutParams localLayoutParams = paramListView
                        .getLayoutParams();
                localLayoutParams.height = (i + paramListView
                        .getDividerHeight()
                        * (-1 + localListAdapter.getCount()));
                paramListView.setLayoutParams(localLayoutParams);
                return;
            }
            View localView = localListAdapter.getView(j, null, paramListView);
            localView.measure(0, 0);
            i += localView.getMeasuredHeight();
        }
    }

    public static void showBuildDialogInput(AlertDialog paramAlertDialog) {
        paramAlertDialog.getWindow().clearFlags(131080);
        paramAlertDialog.getWindow().setSoftInputMode(4);
    }

    public static void showSoftInputFromWindow(Activity paramActivity) {
        ((InputMethodManager) paramActivity.getSystemService("input_method"))
                .toggleSoftInput(0, 2);
    }

    public static String stringFilter(String paramString)
            throws PatternSyntaxException {
        return Pattern.compile("[^a-zA-Z0-9一-龥_]").matcher(paramString)
                .replaceAll("").trim();
    }

    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */

    public static boolean isNetworkAvailable(Activity activity) {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    System.out.println(i + "===状态==="
                            + networkInfo[i].getState());
                    System.out.println(i + "===类型==="
                            + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String getCurrentActivityName(Context context) {
        android.app.ActivityManager am = (android.app.ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE);

        // get the info from the currently running task
        List<android.app.ActivityManager.RunningTaskInfo> taskInfo = am
                .getRunningTasks(1);

        ComponentName componentInfo = taskInfo.get(0).topActivity;
        return componentInfo.getClassName();
    }

    public static final int CHECKRETURN = 200;

    public static void checkLoginStatusAndReturnLoginActivity(Context context,
                                                              Handler handler) {

        class CheckLoginStatusThread extends Thread {

            private Context context;
            private Handler handler;

            CheckLoginStatusThread(Context context, Handler handler) {
                this.context = context;
                this.handler = handler;
            }

            public void run() {

                if (this.context == null)
                    return;

                Looper.prepare();
                // this.serverUrl = GlobalContant.instance().serverUrl;

                String serverUrl = GlobalContant.instance().mainUrl
                        + this.context.getResources().getString(
                        R.string.check_login_status);

                Map<String, String> sParaTemp = new HashMap<String, String>();
                sParaTemp.put("token", GlobalContant.instance().token);

                String str1 = "";
                try {
                    str1 = HttpConnection.buildRequest(serverUrl, sParaTemp);

                    Log.v("member recv", str1);

                    if (str1 == null) {
                        Toast.makeText(context, "网络连接异常", Toast.LENGTH_LONG)
                                .show();
                        return;
                    }

                    JSONObject json = new JSONObject(str1);
                    // 如果登陆超时，退到登陆界面
                    if (!json.isNull("timeout") && json.getInt("timeout") != 0) {
                        Toast.makeText(context, "您的帐号已在其它地方登陆或已超时",
                                Toast.LENGTH_LONG).show();

                        ActivityManager.getInstance()
                                .closeAllActivityNoincludeMain();
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);

                        return;
                    }

                } catch (Exception localException1) {
                    localException1.printStackTrace();
                    Toast.makeText(context, str1, Toast.LENGTH_LONG).show();
                }

                if (handler != null) {
                    Message localMessage = new Message();
                    localMessage.what = CHECKRETURN;
                    this.handler.sendMessage(localMessage);
                }
                Looper.loop();
            }
        }

        (new CheckLoginStatusThread(context, handler)).start();
    }

    public static void checkWifiAndOpen(Context context) {
        WifiManager mWiFiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (!mWiFiManager.isWifiEnabled()) {
            mWiFiManager.setWifiEnabled(true);
        }

    }

}
