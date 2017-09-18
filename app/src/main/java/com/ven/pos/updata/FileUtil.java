package com.ven.pos.updata;

import java.io.File;
import java.io.IOException;

import android.os.Environment;

/**
 * 类描述：FileUtil
 * 
 * @author hexiaoming
 * @version
 */
public class FileUtil {

	public static File updateDir = null;
	public static File updateFile = null;
	/*********** 保存升级APK的目录 ***********/
	public static final String KonkaApplication = "konkaUpdateApplication";

	public static boolean isCreateFileSucess;

	/**
	 * 方法描述：createFile方法
	 * 
	 * @param String
	 *            app_name
	 * @return
	 * @see FileUtil
	 */
	public static void createFile(String app_name) {

		if (android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment
				.getExternalStorageState())) {
			isCreateFileSucess = true;

			updateDir = new File(Environment.getExternalStorageDirectory()
					+ "/" + KonkaApplication + "/");
			updateFile = new File(updateDir + "/" + app_name + ".apk");

			if (!updateDir.exists()) {
				updateDir.mkdirs();
			}
			if (!updateFile.exists()) {
				try {
					updateFile.createNewFile();
				} catch (IOException e) {
					isCreateFileSucess = false;
					e.printStackTrace();
				}
			}

		} else {
			isCreateFileSucess = false;
		}
	}
}