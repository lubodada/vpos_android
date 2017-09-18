package com.ven.pos.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.ven.pos.LoginActivity;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

	private static final String TAG = CrashHandler.class.getSimpleName();
	private Context mContext;
	private static volatile CrashHandler instance;
	private Thread.UncaughtExceptionHandler defalutHandler;
	private DateFormat formatter = new SimpleDateFormat(
			"yyyy-MM-dd_HH-mm-ss.SSS", Locale.CHINA);

	private CrashHandler() {

	}

	/**
	 * 获得单例
	 * 
	 * @return 单例
	 */
	public static CrashHandler getInstance() {
		if (instance == null) {
			synchronized (CrashHandler.class) {
				if (instance == null) {
					instance = new CrashHandler();
				}
			}
		}
		return instance;
	}

	public void init(Context context) {
		mContext = context.getApplicationContext();
		defalutHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 获取系统默认的UncaughtException处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
		// 设置该CrashHandler为程序的默认处理器
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		boolean hasHandle = handleException(ex);
		// 是否处理
		if (!hasHandle && defalutHandler != null) {
			defalutHandler.uncaughtException(thread, ex);
			// 如果用户没有处理则让系统默认的异常处理器来处理
		} else {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			ActivityManager.getInstance().exit();
			// android.os.Process.killProcess(android.os.Process.myPid());
			// System.exit(1);

			restartApp();
		}

	}

	public void restartApp() {
		Intent intent = new Intent(mContext, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid()); // 结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
	}

	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();

				ex.printStackTrace();

				String err = "[" + ex.getMessage() + "]";
				Toast.makeText(mContext, "程序出现异常,1秒后自动重起", Toast.LENGTH_LONG)
						.show();
				Looper.loop();
			}
		}.start();
		String str = collectDeviceInfo(ex);
		// 收集设备参数信息,日志信息
		saveCrashInfoToFile(str);
		// 保存日志文件
		return true;
	}

	/**
	 * 收集设备信息，日志信息
	 * 
	 * @param ex
	 *            Throwable
	 * @return 收集的信息
	 */
	private String collectDeviceInfo(Throwable ex) {
		Log.e(TAG, "collectDeviceInfo:" + ex.getMessage());
		StringBuilder builder = new StringBuilder();

		StackTraceElement[] stackArray = ex.getStackTrace();
		for (int i = 0; i < stackArray.length; i++) {
			StackTraceElement element = stackArray[i];
			builder.append(element.toString() + "\n");
		}
		return builder.toString();
	}

	/**
	 * 保存出错信息
	 * 
	 * @param error
	 *            待保存的出错信息
	 */
	private void saveCrashInfoToFile(String error) {
		Log.e(TAG, "saveCrashInfoToFile:" + error);

		Date currentTime = new Date();
		String currentTimeStamp = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss")
				.format(currentTime);
		String filename = currentTimeStamp + ".txt";
		String localPath = Environment.getExternalStorageDirectory().getPath()
				+ "/vpos";
		File file1 = new File(localPath);
		if (!file1.exists()) {
			file1.mkdirs();
		}
		try {
			BufferedWriter bos = new BufferedWriter(new FileWriter(localPath
					+ "/" + filename));
			bos.write(error);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
