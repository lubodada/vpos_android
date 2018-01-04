package com.ven.pos.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class LogUtil {
    public static final int NONLOG = 0;
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;

    private static int level = ERROR;
    private static String TAG = "";
    private static String filemane = "loginfo";
    private static Boolean PRINT = false;

    public static void v(String msg) {
        if (level >= VERBOSE) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getTagInfo(stackTraceElement);
            Log.v(tag, msg);
            if (PRINT) {
                print(tag + "&" + msg);
            }

        }
    }

    public static void d(String msg) {
        if (level >= DEBUG) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getTagInfo(stackTraceElement);
            Log.d(tag, msg);
            if (PRINT) {
                print(tag + "&" + msg);
            }

        }
    }

    public static void i(String msg) {
        if (level >= INFO) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getTagInfo(stackTraceElement);
            Log.i(tag, msg);
            if (PRINT) {
                print(tag + "&" + msg);
            }

        }
    }

    public static void w(String msg) {
        if (level >= WARN) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getTagInfo(stackTraceElement);
            Log.w(tag, msg);
            if (PRINT) {
                print(tag + "&" + msg);
            }

        }
    }

    public static void e(String msg) {
        if (level >= ERROR) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getTagInfo(stackTraceElement);
            Log.e(tag, msg);
            if (PRINT) {
                print(tag + "&" + msg);
            }

        }
    }

    public static void t(String msg, Throwable tr) {
        if (level >= ERROR) {
            StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
            String tag = getTagInfo(stackTraceElement);
            Log.e(tag, msg, tr);
            if (PRINT) {
                print(tag + "&" + msg, tr);
            }

        }
    }

    public static void t(Throwable tr) {
        t("", tr);
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[3];
        String tag = getTagInfo(stackTraceElement);
        Log.i(tag, msg);
        if (PRINT) {
            print("Toast:" + tag + "&" + msg);
        }
    }

    public static void toast(Context context, int resurceId) {
        String msg = context.getResources().getString(resurceId);
        toast(context, msg);
    }


    public static void setLevel(int level) {
        LogUtil.level = level;
    }

    public static void setTAG(String tAG) {
        TAG = tAG;
    }

    public static void setFilemane(String filemane) {
        LogUtil.filemane = filemane;
    }

    private static String getTagInfo(StackTraceElement stackTraceElement) {

        String tag = "%s.%s(Line:%d)"; // 占位符
        String callerClazzName = stackTraceElement.getClassName(); // 获取到类名
        callerClazzName = callerClazzName.substring(callerClazzName
                .lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, stackTraceElement.getMethodName(),
                stackTraceElement.getLineNumber()); // 替换
        if (TAG != "") {
            tag = TAG + tag;
        }
        return tag;
    }


    /**
     * 打印
     *
     * @param msg
     */
    private static void print(String msg) {
        Date currentTime = new Date();
        String currentTimeStamp = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss").format(currentTime);

        String filename = filemane + ".txt";
        String localPath = Environment.getExternalStorageDirectory().getPath() + "/" + filemane;
        File file1 = new File(localPath);
        if (!file1.exists()) {
            file1.mkdirs();
        }
        String msagement = currentTimeStamp + "&" + msg + "\n";
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(
                    localPath + "/" + filename));
            bos.write(msagement);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打印
     *
     * @param msg
     */
    private static void print(String msg, Throwable tr) {
        print(msg + "&" + "Throwable:" + tr.getMessage());
    }
}
