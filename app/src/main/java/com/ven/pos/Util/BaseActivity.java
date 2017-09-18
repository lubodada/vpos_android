package com.ven.pos.Util;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityManager.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ActivityManager.getInstance().removeActivity(this);
	}

	
}
