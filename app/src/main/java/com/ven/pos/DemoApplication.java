package com.ven.pos;

import android.os.Build;

import com.basewin.base.application.BaseApplication;
import com.basewin.services.ServiceManager;

/**
 * 
 * @author zhongrenshun
 * @date
 * @version
 * @function
 * @lastmodify
 */
public class DemoApplication extends BaseApplication {
	private static DemoApplication instance = null;

	public static DemoApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		if (Build.MANUFACTURER.contains("basewin")) {

			// 初始化service manager
			// init service manager
			ServiceManager.getInstence().init(getApplicationContext());
		}
	}

}
