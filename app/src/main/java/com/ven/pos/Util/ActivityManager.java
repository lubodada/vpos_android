package com.ven.pos.Util;

import java.util.ArrayList;

import android.app.Activity;

import com.ven.pos.MainActivity;

public class ActivityManager {

	private ArrayList<Activity> activitieslist = new ArrayList<Activity>();
	private ArrayList<Activity> delActivitieslist = new ArrayList<Activity>();
	private static ActivityManager instance;

	private ActivityManager() {
	}

	public static synchronized ActivityManager getInstance() {
		if (instance == null) {
			instance = new ActivityManager();
		}
		return instance;
	}

	public void addActivity(Activity activity) {

		delActivitieslist.clear();
		for (Activity ac : activitieslist) {
			if (ac != null
					&& ac.getClass().getName()
							.equals(activity.getClass().getName())) {
				// activity.finish();
				// removeActivity(ac);
				// 加入删除列表
				delActivitieslist.add(ac);
			}
		}
		// 删除掉
		for (Activity ac : delActivitieslist) {
			removeActivity(ac);
		}

		activitieslist.add(activity);
	}

	public void removeActivity(Activity activity) {
		activitieslist.remove(activity);
	}

	public void exit() {
		for (Activity activity : activitieslist) {
			try {
				if (activity != null) {
					activity.finish();
				}
			} finally {
				// System.exit(0);
			}

		}
	}

	public void closeAllActivityNoincludeMain() {
		for (Activity activity : activitieslist) {
			try {
				if (activity != null
						&& activity.getClass().getName() != MainActivity.class
								.getName()) {
					activity.finish();
				}
			} finally {
				// System.exit(0);
			}

		}

	}

}
