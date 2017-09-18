/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.ven.pos.qscanbar;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnyssj.pos.R;
import com.ven.pos.TitleBar;
import com.ven.pos.Payment.PaymentMainActivity;
import com.ven.pos.Util.BaseActivity;
import com.ven.pos.Util.PlaySoundPool;

/* Import ZBar Class files */

public class PayCameraActivity extends BaseActivity {

	public static final String IntentSCANTYPE = "scanType";
	public static final String RetCustomerNo = "customerNo";

	public static final int AliQSCAN = 1; // 支付宝扫描
	public static final int WxQSCAN = 2; // 微信扫描
	public static final int CONSUME_QSCAN = 3; // 会员扫描
	public static final int CHANGE_CHECK = 4; // 兑换验证

	public static final int MEMBER_READ = 100;

	private static PayCameraActivity payCameraActivity;

	private Camera mCamera;
	private CameraPreview mPreview;
	private Handler autoFocusHandler;
	private boolean canCamera = true;
	private PlaySoundPool playSoundPool;
	TextView scanText;
	// Button scanButton;

	EditText search_member;
	Button search_member_btn;
	LinearLayout search_ll;

	ImageScanner scanner;

	private boolean barcodeScanned = false;
	private boolean previewing = true;

	static {
		System.loadLibrary("iconv");
	}

	/**
	 * 用Handler来更新UI
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MEMBER_READ: {

				final int scanType = getIntent().getExtras().getInt(
						IntentSCANTYPE);

				QuitTask(scanType, (String) msg.obj,
						PaymentMainActivity.CONSUMENUM);

				playSoundPool.play(1, 0);
			}
				break;

			}
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TitleBar.setTitleBar(this, "返回", "二维码扫描", "", null);

		setContentView(R.layout.pay_qcode_main);

		payCameraActivity = this;
		search_member = (EditText) findViewById(R.id.search_member);
		search_member_btn = (Button) findViewById(R.id.search_member_btn);
		search_ll = (LinearLayout) findViewById(R.id.search_ll);

		try {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			autoFocusHandler = new Handler();
			mCamera = getCameraInstance();

			/* Instance barcode scanner */
			scanner = new ImageScanner();
			scanner.setConfig(0, Config.X_DENSITY, 3);
			scanner.setConfig(0, Config.Y_DENSITY, 3);

			mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
			FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
			preview.addView(mPreview);

			scanText = (TextView) findViewById(R.id.scanText);
			scanText.setText(R.string.msg_default_status);

			final int scanType = getIntent().getExtras().getInt(IntentSCANTYPE);
			switch (scanType) {
			case AliQSCAN: // 支付宝扫描
				scanText.setText(R.string.msg_qcode_ali_status);
				break;
			case WxQSCAN: // 微信扫描
				scanText.setText(R.string.msg_qcode_weixin_status);
				break;
			case CONSUME_QSCAN: // 会员扫描
				search_ll.setVisibility(View.VISIBLE);
				// scanText.setInputType(InputType.TYPE_CLASS_NUMBER);
				scanText.setText(R.string.msg_qcode_cusume_status);

				break;
			case CHANGE_CHECK: // 兑换验证
				scanText.setText(R.string.msg_qcode_exchange_status);
				break;
			}

			playSoundPool = new PlaySoundPool(getApplicationContext());
			playSoundPool.loadSfx(R.raw.beep, 1);
		} catch (Exception ex) {
			ex.printStackTrace();
			Toast.makeText(this, "扫描二维码错误，请检查摄像头是否正常", Toast.LENGTH_LONG)
					.show();
		}

		// scanButton = (Button)findViewById(R.id.ScanButton);

		// scanButton.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// if (barcodeScanned) {
		// barcodeScanned = false;
		// scanText.setText("Scanning...");
		// mCamera.setPreviewCallback(previewCb);
		// mCamera.startPreview();
		// previewing = true;
		// mCamera.autoFocus(autoFocusCB);
		// }
		// }
		// });
		search_member_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String memnum = search_member.getText().toString().trim();
				if (!memnum.equals("")) {
					Intent intent = new Intent();
					intent.putExtra(RetCustomerNo, memnum);
					setResult(PaymentMainActivity.CONSUMENUM, intent);
					finish();
				}
			}
		});
	}

	public void onPause() {
		super.onPause();
		releaseCamera();

	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
		} catch (Exception e) {
		}
		return c;
	}

	private void releaseCamera() {
		if (mCamera != null) {
			previewing = false;
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	private Runnable doAutoFocus = new Runnable() {
		public void run() {
			if (previewing)
				mCamera.autoFocus(autoFocusCB);
		}
	};

	private void QuitTask(final int scanType, final String resultString,
			int returnNo) {
		Intent intent = new Intent();
		intent.putExtra(IntentSCANTYPE, scanType);
		intent.putExtra(RetCustomerNo, resultString);
		setResult(returnNo, intent);
		finish();
	};

	private void ResetCamera() {
		// previewing = true;
		// mCamera.setPreviewCallback( previewCb );
		canCamera = true;
		// mCamera.startPreview();
		scanText.setText(R.string.msg_default_status);
	};

	PreviewCallback previewCb = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {

			if (!canCamera) {
				return;
			}

			Camera.Parameters parameters = camera.getParameters();
			Size size = parameters.getPreviewSize();

			Image barcode = new Image(size.width, size.height, "Y800");
			barcode.setData(data);

			int result = scanner.scanImage(barcode);

			if (result != 0) {

				canCamera = false;

				playSoundPool.play(1, 0);

				SymbolSet syms = scanner.getResults();
				String rs = "";
				for (Symbol sym : syms) {
					rs = sym.getData();

					barcodeScanned = true;
				}
				final int scanType = getIntent().getExtras().getInt(
						IntentSCANTYPE);
				final String resultString = rs;

				new Thread() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(500);
							QuitTask(scanType, resultString, RESULT_OK);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.out.println("thread error...");
						}
					}
				}.start();

				//
				// Intent resultIntent = new Intent();
				// Bundle bundle = new Bundle();
				// bundle.putString("result", resultString);
				// resultIntent.putExtras(bundle);
				// setResult(3, resultIntent);
				// finish();
			}
		}
	};

	// Mimic continuous auto-focusing
	AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			autoFocusHandler.postDelayed(doAutoFocus, 1000);
		}
	};
}
