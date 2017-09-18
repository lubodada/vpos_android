package com.cnyssj.db;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, LongRunningService.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(i);
	}
}