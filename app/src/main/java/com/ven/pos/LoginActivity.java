package com.ven.pos;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basewin.services.ServiceManager;
import com.cnyssj.pos.R;
import com.cnyssj.widget.ShapeLoadingDialog;
import com.jhj.Agreement.ZYB.Http_PushTask;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.ven.pos.Util.BaseActivity;
import com.ven.pos.Util.CTelephoneInfo;
import com.ven.pos.Util.CrashHandler;
import com.ven.pos.Util.HttpConnection;
import com.ven.pos.Util.Manager_num;
import com.ven.pos.alipay.util.Networkstate;
import com.ven.pos.updata.UpdateService;
import com.ven.pos.updata.Version_util;

public class LoginActivity extends BaseActivity implements OnClickListener {

	public static final String INTAL_LOGIN_FIRST = "firstLogin";

	private static LoginActivity loginActivity;
	private EditText txtUserAccount;
	private EditText txtPassword;
	private CheckBox chxRmpd;
	private CheckBox chxAutoLogin;
	private Button btnLogin;
	SharedPreferences_util su = new SharedPreferences_util();
	// 升级相关
	String result;
	boolean force_update;
	String url;
	String description, manager_num, error_msg;
	Manager_num mn = new Manager_num();
	Button UP_data_qx, UP_data_qd;
	Dialog dia_update;
	TextView et_UP_data;
	Version_util Vn = new Version_util();
	ShapeLoadingDialog shapeLoadingDialog;

	/**
	 * 用Handler来更新UI
	 */
	JSONObject js;
	String key;
	String merchantId;

	private Handler handler = new Handler() {
		@SuppressWarnings("static-access")
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case GlobalContant.CloseProgressDialog: {
				shapeLoadingDialog.dismiss();
				break;
			}
			case GlobalContant.ShowProgressDialog: {
				shapeLoadingDialog.setLoadingText("正在加载数据...");
				shapeLoadingDialog.show();
				shapeLoadingDialog.setCanceledOnTouchOutside(false);
				shapeLoadingDialog.setkeycodeback(false);

				break;
			}
			case GlobalContant.ShowErrorDialog: {
				String getResult1 = (String) msg.obj;
				Toast.makeText(LoginActivity.loginActivity, getResult1,
						Toast.LENGTH_LONG).show();
				break;
			}
			case 1:
				(new UpdateVersion()).start();
				break;
			case 2:
				// onToast("版本一致，无需升级！");
				break;
			case 3:
				if (force_update) {
					UP_data_qx
							.setBackgroundResource(R.drawable.button_oncleck_item);
				}
				dia_update.setCancelable(false);
				Window w;
				WindowManager.LayoutParams lp;
				dia_update.show();
				dia_update.setCanceledOnTouchOutside(false);
				w = dia_update.getWindow();
				lp = w.getAttributes();
				lp.x = 0;
				lp.y = 40;
				dia_update.onWindowAttributesChanged(lp);
				et_UP_data.setText(description);
				break;
			case 4:
				try {
					su.setPrefString(LoginActivity.this, "merchantId",
							js.getString("merchantId"));
					su.setPrefString(LoginActivity.this, "key",
							js.getString("key"));
				} catch (JSONException e) {

					e.printStackTrace();
				}
			case 5:
				if (Networkstate.isNetworkAvailable(LoginActivity.this)) {
					(new downLoad()).start();
				} else {
					onToast("无可用网络！请联网后重试！");
				}
				break;
			case 6:
				onToast(error_msg);
				break;
			}
		}
	};

	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		String name = Build.MANUFACTURER;
		if (name.contains("BASEWIN") || name.contains("basewin")) {
			ServiceManager.getInstence().init(getApplicationContext());
		}
		loginActivity = this;

		// 商户号
		// if (su.getPrefString(LoginActivity.this, "merchantId", null) == null
		// || su.getPrefString(LoginActivity.this, "key", null) == null
		// || su.getPrefString(LoginActivity.this, "clientId", null) == null
		// || su.getPrefString(LoginActivity.this, "enable_recharge", null) ==
		// null) {
		// 获取配置文档

		if (name.contains("BASEWIN") || name.contains("basewin")) {

			try {
				manager_num = ServiceManager.getInstence().getDeviceinfo()
						.getSN();
				showmessage(5);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			CTelephoneInfo telephonyInfo = CTelephoneInfo.getInstance(this);
			telephonyInfo.setCTelephoneInfo();
			manager_num = telephonyInfo.getImeiSIM1();
			if (manager_num == null) {
				onToast("未能识别设备!");
			} else {
				showmessage(5);
			}
			// }

		}

		setContentView(R.layout.login);
		initView();
		setListeners();
		initControl();
		CrashHandler.getInstance().init(this);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);

		// 发送更新指令
		if (Networkstate.isNetworkAvailable(LoginActivity.this)) {
			showmessage(1);
		} else {
			onToast("无可用网络！请联网后重试！");
		}

	}

	private void initView() {
		shapeLoadingDialog = new ShapeLoadingDialog(LoginActivity.this);
		this.txtUserAccount = ((EditText) findViewById(R.id.login_account_edt));
		this.txtPassword = ((EditText) findViewById(R.id.androidclient_login_pwd_edt));
		this.chxRmpd = ((CheckBox) findViewById(R.id.login_remberpassword_chx));
		this.chxAutoLogin = ((CheckBox) findViewById(R.id.login_autoLogin_chx));
		this.btnLogin = ((Button) findViewById(R.id.androidclient_login_login_btn));

		this.txtUserAccount.setInputType(InputType.TYPE_CLASS_NUMBER);
		dia_update = new Dialog(LoginActivity.this,
				R.style.edit_AlertDialog_style);
		dia_update.setContentView(R.layout.activity_start_dialog_update);
		et_UP_data = (TextView) dia_update.findViewById(R.id.et_UP_data);
		UP_data_qx = (Button) dia_update.findViewById(R.id.UP_data_qx);
		UP_data_qd = (Button) dia_update.findViewById(R.id.UP_data_qd);
		UP_data_qx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!force_update) {
					dia_update.cancel();
				} else {
					onToast("大哥，新版本可美了，你得升级啊！");
				}
			}
		});

		UP_data_qd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dia_update.cancel();
				ShowProDlg();
				shapeLoadingDialog.setLoadingText("正在下载...");

				// 跳转service下载程序
				Intent intent = new Intent(LoginActivity.this,
						UpdateService.class);
				intent.putExtra("Key_App_Name", "翼开店超市版");
				intent.putExtra("Key_Down_Url", url);
				startService(intent);
			}
		});
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void setListeners() {

		this.btnLogin.setOnClickListener(this);
		this.chxRmpd.setOnClickListener(this);
		this.chxAutoLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == this.btnLogin.getId()) {
			login();

		} else if (v.getId() == this.chxRmpd.getId()) {
			if (!this.chxRmpd.isChecked()) {
				this.chxAutoLogin.setChecked(false);
			}
		} else if (v.getId() == this.chxAutoLogin.getId()) {
			if (chxAutoLogin.isChecked()) {
				this.chxRmpd.setChecked(true);
			}
		}
	}

	private void login() {
		String phone = loginActivity.txtUserAccount.getText().toString().trim();
		if ("".equals(phone)) {
			loginActivity.ShowMsgBox("手机号不能为空!");
			return;
		}

		if (phone.length() != 11) {
			loginActivity.ShowMsgBox("请输入正确的手机号!");
			return;
		}

		String pwd = loginActivity.txtPassword.getText().toString().trim();
		if ("".equals(pwd)) {
			loginActivity.ShowMsgBox("密码长度不能为空!");
			return;
		}
		loginActivity.Login(loginActivity.txtUserAccount.getText().toString()
				.trim(), loginActivity.txtPassword.getText().toString().trim());
	}

	private void initControl() {

		SharedPreferences localSharedPreferences = getSharedPreferences(
				GlobalContant.SharedPreferenceUseInfo, 0);
		try {
			String userAccount = localSharedPreferences.getString(
					GlobalContant.SharedPreferencesUserAccount, "");
			String password = localSharedPreferences.getString(
					GlobalContant.SharedPreferencesPassword, "");
			boolean isCheckRmpd = localSharedPreferences.getBoolean(
					GlobalContant.SharedPreferencesRememberPwd, false);
			boolean isAutoLogin = localSharedPreferences.getBoolean(
					GlobalContant.SharedPreferencesAutoLogin, false);

			txtUserAccount.setText(userAccount);
			txtPassword.setText(password);
			chxRmpd.setChecked(isCheckRmpd);
			chxAutoLogin.setChecked(isAutoLogin);

			boolean bFirstLogin = this.getIntent().getBooleanExtra(
					INTAL_LOGIN_FIRST, true);

			if (isAutoLogin && bFirstLogin) {
				login();
			}

		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	protected void ShowMsgBox(String paramString1) {
		Message localMessage = new Message();
		localMessage.what = GlobalContant.ShowErrorDialog;
		localMessage.obj = paramString1;
		this.handler.sendMessage(localMessage);
	}

	protected void ShowMsgBox(String paramString1, String paramString2,
			String paramString3,
			DialogInterface.OnClickListener paramOnClickListener) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(paramString2);
		builder.setIcon(android.R.drawable.ic_dialog_info);

		TextView inputEdit = new TextView(this);
		inputEdit.setText(paramString1);
		builder.setPositiveButton(paramString3, paramOnClickListener);
		builder.setView(inputEdit);
		builder.show();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private void onToast(String strTip) {
		Toast.makeText(loginActivity, strTip, Toast.LENGTH_LONG).show();
	}

	public void ShowProDlg() {
		Message localMessage = new Message();
		localMessage.what = GlobalContant.ShowProgressDialog;
		this.handler.sendMessage(localMessage);
	}

	public void DismissProDlg() {
		Message localMessage = new Message();
		localMessage.what = GlobalContant.CloseProgressDialog;
		this.handler.sendMessage(localMessage);
	}

	// 发送handle
	public void showmessage(int message) {
		Message localMessage = new Message();
		localMessage.what = message;
		this.handler.sendMessage(localMessage);
	}

	public void SaveUserInfo(String userAccount, String userPassword) {
		SharedPreferences localSharedPreferences = getSharedPreferences(
				GlobalContant.SharedPreferenceUseInfo, 0);
		try {
			localSharedPreferences
					.edit()
					.putString(GlobalContant.SharedPreferencesUserAccount,
							userAccount).commit();
			localSharedPreferences
					.edit()
					.putBoolean(GlobalContant.SharedPreferencesRememberPwd,
							this.chxRmpd.isChecked()).commit();
			localSharedPreferences
					.edit()
					.putBoolean(GlobalContant.SharedPreferencesAutoLogin,
							this.chxAutoLogin.isChecked()).commit();
			if (this.chxRmpd.isChecked()) {
				String str = userPassword;
				localSharedPreferences
						.edit()
						.putString(GlobalContant.SharedPreferencesPassword, str)
						.commit();
				return;
			} else {
				localSharedPreferences.edit()
						.putString(GlobalContant.SharedPreferencesPassword, "")
						.commit();
			}
			return;
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	class LoginThread extends Thread {
		private String passWord;
		private String userAccount;

		public LoginThread(String paramString2, String paramString3) {
			this.userAccount = paramString2;
			this.passWord = paramString3;
		}

		@SuppressWarnings("static-access")
		public void run() {

			try {
				GlobalContant.instance().mainUrl = "http://cnyssj.net/vpos/";
			}

			catch (Exception localException1) {
				Looper.prepare();
				loginActivity.btnLogin.setEnabled(true);
				loginActivity.DismissProDlg();
				localException1.printStackTrace();
				Toast.makeText(LoginActivity.loginActivity, "查询失败",
						Toast.LENGTH_LONG).show();
				Looper.loop();
				return;
			}

			if (GlobalContant.instance().mainUrl.isEmpty()) {
				Looper.prepare();
				loginActivity.btnLogin.setEnabled(true);
				loginActivity.DismissProDlg();
				Toast.makeText(LoginActivity.loginActivity, "查询失败",
						Toast.LENGTH_LONG).show();
				Looper.loop();
				return;
			}

			try {

				Map<String, String> sParaTemp = new HashMap<String, String>();
				sParaTemp.put("username", userAccount);
				sParaTemp.put("password", passWord);

				String str1 = HttpConnection.buildRequest(
						GlobalContant.instance().mainUrl
								+ getResources().getString(R.string.login_url),
						sParaTemp);

				Looper.prepare();
				loginActivity.DismissProDlg();

				if (str1 == null) {
					// loginActivity.ShowMsgBox("连接失败!");
					Looper.prepare();
					Toast.makeText(loginActivity, "登陆到平台出错", Toast.LENGTH_LONG)
							.show();
					Looper.loop();
					loginActivity.btnLogin.setEnabled(true);
					// loginActivity.DismissProDlg();
					return;
				}

				JSONTokener jsonParser = new JSONTokener(str1);
				JSONObject json = (JSONObject) jsonParser.nextValue();

				if (!json.getString("status").equals("1")) {
					loginActivity
							.ShowMsgBox(GlobalContant.instance().errorMsg.errors
									.get(json.getString("status")));
					loginActivity.btnLogin.setEnabled(true);
					// loginActivity.DismissProDlg();
					return;
				} else {
					if (!TextUtils.isEmpty(result)) {
						loginActivity.SaveUserInfo(this.userAccount,
								this.passWord);
						GlobalContant.instance().token = json
								.getString("token");
						Log.e("token", json.getString("token"));

						su.setPrefString(LoginActivity.this, "token",
								json.getString("token"));
						GlobalContant.instance().companyName = json
								.getString("businessname");
						if (GlobalContant.instance().companyName.equals("")) {
							onToast("公司名称不能为空");
						}
						GlobalContant.instance().userName = json
								.getString("nickname");

						Intent intent = new Intent(loginActivity,
								MainActivity.class);
						loginActivity.startActivity(intent);

						finish();
						overridePendingTransition(android.R.anim.fade_in,
								android.R.anim.fade_out);

						MobclickAgent.onKillProcess(loginActivity);
						loginActivity.DismissProDlg();

					} else {
						onToast("无法获取版本信息！请检查网络后重试！");
						showmessage(1);
					}
				}
			}

			catch (Exception localException1) {
				loginActivity.btnLogin.setEnabled(true);
				loginActivity.DismissProDlg();
				localException1.printStackTrace();

				Toast.makeText(LoginActivity.loginActivity, "登陆失败",
						Toast.LENGTH_LONG).show();
			}
			Looper.loop();
		}
	}

	private void Login(String userAccount, String userPwd) {
		ShowProDlg();

		new LoginThread(userAccount, userPwd).start();
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}
		if (keyCode == event.KEYCODE_HOME) {
			return true;
		}
		if (keyCode == event.KEYCODE_MENU) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class UpdateVersion extends Thread {
		@SuppressWarnings("static-access")
		@Override
		public void run() {
			try {
				Http_PushTask HP = new Http_PushTask();
				// Vn.getAppInfo(LoginActivity.this)
				result = HP.execute(
						"",
						"http://cnyssj.net/auto_update/?" + "app_name="
								+ "com.cnyssj.pos").get();
				JSONObject js = new JSONObject(result);
				if (!TextUtils.isEmpty(result)) {
					// 获取版本
					js.getString("version");
					String version = Vn.getVersionName(LoginActivity.this);
					if (version.equals(js.getString("version"))) {
						// showmessage(2);
						return;
					} else {
						force_update = js.getBoolean("force_update");
						url = js.getString("url");
						description = js.getString("description");
						showmessage(3);
						return;
					}
				} else {
					onToast("数据获取失败！");
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				e1.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	class downLoad extends Thread {
		@SuppressWarnings("static-access")
		@Override
		public void run() {

			Http_PushTask HP = new Http_PushTask();
			try {
				String result = HP.execute(
						"",
						"http://121.41.113.42/ykdconfig/?" + "app_key="
								+ manager_num)
						.get(10000, TimeUnit.MILLISECONDS);
				js = new JSONObject(result);
				// onToast(result);
				if (!TextUtils.isEmpty(result)) {
					if (js.getBoolean("success")) {
						su.setPrefString(LoginActivity.this, "merchantId",
								js.getString("merchantId"));
						su.setPrefString(LoginActivity.this, "key",
								js.getString("key"));
						// 商户名称
						su.setPrefString(LoginActivity.this, "cashiername",
								js.getString("merchantName"));
						su.setPrefString(LoginActivity.this, "clientId",
								js.getString("deviceNumber"));
						// 话费状态
						su.setPrefboolean(LoginActivity.this,
								"enable_recharge",
								js.getBoolean("enable_recharge"));
					} else {
						error_msg = js.getString("error_msg");
						showmessage(6);
					}
				} else {
					onToast("数据获取失败！");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				onToast("获取商户号失败，请检查网络连接后重试!");
				e.printStackTrace();
				return;
			}

		}
	}
}
