package com.ven.pos.BarcodeScan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.device.scanner.configuration.Triggering;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import com.ven.pos.GlobalContant;

public class BarcodeScan {

	private final static String SCAN_ACTION = "urovo.rcv.message";// 扫描结束action

	private int type;
	private int outPut;

	private Vibrator mVibrator;
	private ScanManager mScanManager;
	private SoundPool soundpool = null;
	private int soundid;
	private String barcodeStr;
	private boolean isScaning = false;
	private Context context;
	private BroadcastReceiver mScanReceiver;

	// private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// // TODO Auto-generated method stub
	// isScaning = false;
	// soundpool.play(soundid, 1, 1, 0, 0, 1);
	//
	// mVibrator.vibrate(100);
	//
	// byte[] barcode = intent.getByteArrayExtra("barocode");
	// //byte[] barcode = intent.getByteArrayExtra("barcode");
	// int barocodelen = intent.getIntExtra("length", 0);
	// byte temp = intent.getByteExtra("barcodeType", (byte) 0);
	// android.util.Log.i("debug", "----codetype--" + temp);
	// barcodeStr = new String(barcode, 0, barocodelen);
	// }
	//
	// };

	public BarcodeScan(Context context, BroadcastReceiver broadcastReceiver) {
		if (GlobalContant.MACHINE != GlobalContant.MACHINE_I9000S) {
			return;
		}
		if (GlobalContant.bClose9000SScan)
			return;

		this.context = context;
		this.mScanReceiver = broadcastReceiver;

		initScan();

	}

	private void initScan() {
		// TODO Auto-generated method stub
		try {

			if (GlobalContant.MACHINE != GlobalContant.MACHINE_I9000S) {
				return;
			}

			if (GlobalContant.bClose9000SScan)
				return;

			mScanManager = new ScanManager();
			mScanManager.openScanner();

			mScanManager.switchOutputMode(0);
			soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
			soundid = soundpool.load("/etc/Scan_new.ogg", 1);
			soundid = soundpool.load(context, com.cnyssj.pos.R.raw.beep, 1);

			mScanManager.setTriggerMode(Triggering.HOST);
		}

		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// 扫描
	public void scan(BroadcastReceiver broadcastReceiver) {
		if (mScanManager == null)
			return;

		mScanReceiver = broadcastReceiver;

		mScanManager.stopDecode();
		isScaning = true;
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mScanManager.startDecode();
	}

	public void PlaySound() {
		isScaning = false;
		soundpool.play(soundid, 1, 1, 0, 0, 1);
	}

	// 设置连续扫描
	public void setScanType(Triggering triggering) {
		if (null != mScanManager && mScanManager.getTriggerMode() != triggering)
			mScanManager.setTriggerMode(triggering);

	}

	public void onPause() {
		// TODO Auto-generated method stub
		if (mScanManager == null)
			return;

		if (mScanManager != null) {
			mScanManager.stopDecode();
			isScaning = false;
		}
		context.unregisterReceiver(mScanReceiver);
	}

	public void onResume() {
		// TODO Auto-generated method stub
		if (mScanManager == null)
			return;

		IntentFilter filter = new IntentFilter();
		filter.addAction(SCAN_ACTION);
		context.registerReceiver(mScanReceiver, filter);
	}
}
