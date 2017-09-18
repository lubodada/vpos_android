package com.ven.pos;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnyssj.pos.R;
import com.ven.pos.BarcodeScan.BarcodeScan;
import com.ven.pos.Util.BaseActivity;
import com.ven.pos.Util.HttpConnection;
import com.ven.pos.Util.UtilTool;

public class MemberInfoActivity extends BaseActivity implements OnClickListener {

	private Button search_member_btn;
	private Button search_member_charge_btn;
	private Button search_member_card_btn;
	private EditText serach_member_phone;
	private TextView search_member_result_name;
	private TextView search_member_result_phone;
	private TextView search_member_result_balance;
	private TextView search_member_result_level;
	private TextView search_member_result_score;
	private ImageView search_member_result_headPic;
	private LinearLayout member_detail_linearLayout;
	private String headPicUrl;
	private String uid = " ";

	private SoundPool soundpool = null;

	private int soundid = 0;
	BarcodeScan barcodeScan; // 扫码枪

	private static final int REFRESH_UI = 100;
	private static final int REFRESH_PIC = 101;
	private static final int TOAST_SHOW = 102;
	public static final int REFRESH_CARD = 103;

	private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO Auto-generated method stub
			barcodeScan.PlaySound();

			byte[] barcode = intent.getByteArrayExtra("barocode");
			// byte[] barcode = intent.getByteArrayExtra("barcode");
			int barocodelen = intent.getIntExtra("length", 0);
			byte temp = intent.getByteExtra("barcodeType", (byte) 0);
			android.util.Log.i("debug", "----codetype--" + temp);

			serach_member_phone.setText(new String(barcode, 0, barocodelen));

			soundpool.play(soundid, 1, 1, 0, 0, 1);

			(new QueryThread(serach_member_phone.getText().toString())).start();
			// barcodeStr = new String(barcode, 0, barocodelen);
		}

	};

	/**
	 * 用Handler来更新UI
	 */
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case REFRESH_UI: {
				try {
					onParseJson((String) msg.obj);
				} catch (JSONException e) {
					Toast.makeText(MemberInfoActivity.this, "服务器返回信息错误",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				break;
			}
			case REFRESH_PIC: {
				search_member_result_headPic
						.setImageDrawable((Drawable) (msg.obj));
			}
				break;
			case TOAST_SHOW: {
				Toast.makeText(MemberInfoActivity.this, (String) msg.obj,
						Toast.LENGTH_LONG).show();
			}
				break;
			case REFRESH_CARD: {

				soundpool.play(soundid, 1, 1, 0, 0, 1);
				serach_member_phone.setText((String) msg.obj);

				(new QueryThread(serach_member_phone.getText().toString()))
						.start();
			}
				break;

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TitleBar.setTitleBar(this, "后退", "查找会员", "", null);

		setContentView(R.layout.member_query);

		initView();

		soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
		// soundid = soundpool.load("/etc/Scan_new.ogg", 1);
		soundid = soundpool.load(this, R.raw.beep, 1);

		barcodeScan.scan(mScanReceiver);

		UtilTool.checkLoginStatusAndReturnLoginActivity(this, null);
	}

	private void initView() {
		search_member_btn = (Button) findViewById(R.id.search_member_btn);
		search_member_card_btn = (Button) findViewById(R.id.search_member_card_btn);
		search_member_charge_btn = (Button) findViewById(R.id.search_member_charge_btn);
		serach_member_phone = (EditText) findViewById(R.id.search_member_phone_no);
		search_member_result_name = (TextView) findViewById(R.id.search_member_result_name);
		search_member_result_phone = (TextView) findViewById(R.id.search_member_result_phone);
		search_member_result_balance = (TextView) findViewById(R.id.search_member_result_balance);
		search_member_result_level = (TextView) findViewById(R.id.search_member_result_level);
		search_member_result_score = (TextView) findViewById(R.id.search_member_result_score);
		search_member_result_headPic = (ImageView) findViewById(R.id.search_member_result_headPic);

		member_detail_linearLayout = (LinearLayout) findViewById(R.id.member_detail_linearLayout);
		member_detail_linearLayout.setVisibility(View.GONE);

		search_member_btn.setOnClickListener(this);
		search_member_charge_btn.setOnClickListener(this);
		search_member_card_btn.setOnClickListener(this);

		barcodeScan = new BarcodeScan(this, mScanReceiver);

		// this.serach_member_phone.setInputType(InputType.TYPE_CLASS_NUMBER);
	}

	@Override
	public void onStart() {

		TitleBar.setActivity(this);

		super.onStart();
	}

	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		// OpenIntegralWall.getInstance().onUnbind();
		super.onDestroy();

	}

	@Override
	protected void onPause() {
		super.onPause();
		barcodeScan.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();
		barcodeScan.onResume();
	}

	class QueryThread extends Thread {

		private String userAccount;
		public QueryThread(String paramString1) {
			this.userAccount = paramString1;
		}

		public void run() {

			if (this.userAccount.isEmpty())
				return;

			Looper.prepare();
			// this.serverUrl = GlobalContant.instance().serverUrl;
			String serverUrl = GlobalContant.instance().mainUrl
					+ getResources().getString(R.string.getmember_url);

			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("mobile", userAccount);
			sParaTemp.put("token", GlobalContant.instance().token);

			String str1 = "";
			try {
				str1 = HttpConnection.buildRequest(serverUrl, sParaTemp);

				Log.v("member recv", str1);

				if (str1 == null) {
					// Toast.makeText(MemberInfoActivity.this, "连接失败!",
					// Toast.LENGTH_LONG).show();
					Message localMessage = new Message();
					localMessage.what = TOAST_SHOW;
					localMessage.obj = "查询失败";
					handler.sendMessage(localMessage);
					return;
				}

				JSONObject json = new JSONObject(str1);
				if (json.isNull("msgContent")) {
					// Toast.makeText(MemberInfoActivity.this, "查询失败!",
					// Toast.LENGTH_LONG).show();
					Message localMessage = new Message();
					localMessage.what = TOAST_SHOW;
					localMessage.obj = "查询失败";
					handler.sendMessage(localMessage);
					return;
				}

				Message localMessage = new Message();
				localMessage.what = REFRESH_UI;
				localMessage.obj = json.getString("msgContent");
				handler.sendMessage(localMessage);
				// onParseJson(json.getString("msgContent"));
			}

			catch (Exception localException1) {

				localException1.printStackTrace();
				Toast.makeText(MemberInfoActivity.this, str1, Toast.LENGTH_LONG)
						.show();
			}

			Looper.loop();
		}
	}

	private void onParseJson(String jsonStr) throws JSONException {

		member_detail_linearLayout.setVisibility(View.VISIBLE);

		JSONObject jsonContent = new JSONObject(jsonStr);

		search_member_result_name.setText(jsonContent.getString("name"));
		search_member_result_phone.setText(jsonContent.getString("mobile"));
		search_member_result_balance.setText(jsonContent.getString("credit2"));
		search_member_result_level.setText(jsonContent.getString("level"));
		search_member_result_score.setText(jsonContent.getString("credit1"));

		headPicUrl = jsonContent.getString("avatar");
		uid = jsonContent.getString("uid");

		new Thread(new Runnable() {
			@Override
			public void run() {

				try {

					Drawable drawable = loadImageFromNetwork(headPicUrl);
					// search_member_result_headPic.setImageDrawable(drawable) ;
					Message localMessage = new Message();
					localMessage.what = REFRESH_PIC;
					localMessage.obj = drawable;
					handler.sendMessage(localMessage);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return;
			}
		}).start();

	}

	private Drawable loadImageFromNetwork(String imageUrl) {
		Drawable drawable = null;
		try {
			// 可以在这里通过文件名来判断，是否本地有此图片
			drawable = Drawable.createFromStream(
					new URL(imageUrl).openStream(), "image.jpg");
		} catch (IOException e) {
			Log.d("test", e.getMessage());
		}
		if (drawable == null) {
			Log.d("test", "null drawable");
		} else {
			Log.d("test", "not null drawable");
		}

		return drawable;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == search_member_btn.getId()) {

			member_detail_linearLayout.setVisibility(View.GONE);
			String strPhoneNo = serach_member_phone.getText().toString();

			// if(strPhoneNo.length()!=11){
			// Toast.makeText(this, "请输入正确的手机号", Toast.LENGTH_LONG).show();
			// return;
			// }

			(new QueryThread(strPhoneNo)).start();
		} else if (arg0.getId() == search_member_charge_btn.getId()) {

			Intent intent = new Intent(this, MemberRechargeActivity.class);

			intent.putExtra("uid", uid);
			startActivity(intent);
		} else if (arg0.getId() == search_member_card_btn.getId()) {
			// VenPiccReader.Instance().Init();

			// serach_member_phone.setText( VenPiccReader.Instance().CheckCard()
			// );
		}
	}
}
