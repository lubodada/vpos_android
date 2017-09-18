package com.cnyssj.pos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apaches.commons.codec.binary.Hex;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jhj.Agreement.ZYB.CryptTool;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.jhj.print.Print_recharge;
import com.ven.pos.TitleBar;
import com.ven.pos.Util.OperatorUtils;
import com.ven.pos.alipay.util.Snippet;

public class RechargeActivity extends Activity {
	TextView tx_phone_monery, tx_recharge;
	TextView tx_one, tx_two, tx_tri, tx_four, tx_five, tx_six, tx_sev,
			tx_eight, tx_nine;
	EditText ed_monery, ed_phone;
	TextView tx_Pay, ed_yue;
	SharedPreferences_util su = new SharedPreferences_util();
	String clientId, cashierName;
	// 识别运营商
	OperatorUtils OU = new OperatorUtils();
	int Operator;
	String Recharge_MONERY, phone_number;
	String outOrderId;

	Dialog dia_recharge;
	Button recharge_qd, recharge_qx;
	TextView recharge_tx_phonenum, recharge_tx_phonemonery;

	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TitleBar.setTitleBar(RechargeActivity.this, "", "话费流量充值", "", null);
		setContentView(R.layout.activity_recharge);
		clientId = su.getPrefString(RechargeActivity.this, "clientId", null);
		cashierName = su.getPrefString(RechargeActivity.this, "cashiername",
				null);
		ed_phone = (EditText) findViewById(R.id.ed_phone);
		ed_phone.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// 此处为得到焦点时的处理内容
				} else {
					phone_number = ed_phone.getText().toString();
					Operator = OU.execute(phone_number);

				}
			}
		});
		dia_recharge = new Dialog(RechargeActivity.this,
				R.style.edit_AlertDialog_style);
		dia_recharge.setContentView(R.layout.activity_start_dialog_recharge);
		recharge_tx_phonenum=(TextView)dia_recharge.findViewById(R.id.phonenum);
		recharge_tx_phonemonery=(TextView)dia_recharge.findViewById(R.id.phonemonery);
		recharge_qd = (Button) dia_recharge.findViewById(R.id.Recharge_qd);
		recharge_qx = (Button) dia_recharge.findViewById(R.id.Recharge_qx);
		recharge_qd.setOnClickListener(myonclicklistener);
		recharge_qx.setOnClickListener(myonclicklistener);

		tx_phone_monery = (TextView) findViewById(R.id.tx_phone_monery);
		tx_recharge = (TextView) findViewById(R.id.tx_recharge);
		tx_phone_monery.setTextColor(Color.RED);
		//
		tx_one = (TextView) findViewById(R.id.tx_one);
		tx_two = (TextView) findViewById(R.id.tx_two);
		tx_tri = (TextView) findViewById(R.id.tx_tri);
		tx_four = (TextView) findViewById(R.id.tx_four);
		tx_five = (TextView) findViewById(R.id.tx_five);
		tx_six = (TextView) findViewById(R.id.tx_six);
		tx_sev = (TextView) findViewById(R.id.tx_sev);
		tx_eight = (TextView) findViewById(R.id.tx_eight);
		tx_nine = (TextView) findViewById(R.id.tx_nine);
		ed_monery = (EditText) findViewById(R.id.ed_monery);
		// 余额
		ed_yue = (TextView) findViewById(R.id.ed_yue);
		ed_monery
				.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							// 此处为得到焦点时的处理内容
							view_change(null);
						} else {
							// 此处为失去焦点时的处理内容
						}
					}
				});

		tx_one.setOnClickListener(myonclicklistener);
		tx_two.setOnClickListener(myonclicklistener);
		tx_tri.setOnClickListener(myonclicklistener);
		tx_four.setOnClickListener(myonclicklistener);
		tx_five.setOnClickListener(myonclicklistener);
		tx_six.setOnClickListener(myonclicklistener);
		tx_sev.setOnClickListener(myonclicklistener);
		tx_eight.setOnClickListener(myonclicklistener);
		tx_nine.setOnClickListener(myonclicklistener);
		//
		tx_phone_monery.setOnClickListener(myonclicklistener);
		tx_recharge.setOnClickListener(myonclicklistener);
		//
		tx_Pay = (TextView) findViewById(R.id.tx_Pay);
		tx_Pay.setOnClickListener(myonclicklistener);
		//
		showmessagedata(200, null);

	}

	View.OnClickListener myonclicklistener = new OnClickListener() {
		Window w;
		WindowManager.LayoutParams lp;

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.tx_one:
				view_change(tx_one);
				Recharge_MONERY = "10";
				break;
			case R.id.tx_two:
				view_change(tx_two);
				Recharge_MONERY = "20";
				ed_monery.clearFocus();
				break;
			case R.id.tx_tri:
				view_change(tx_tri);
				Recharge_MONERY = "30";
				break;
			case R.id.tx_four:
				view_change(tx_four);
				Recharge_MONERY = "50";
				break;
			case R.id.tx_five:
				view_change(tx_five);
				Recharge_MONERY = "100";
				break;
			case R.id.tx_six:
				view_change(tx_six);
				Recharge_MONERY = "200";
				break;
			case R.id.tx_sev:
				view_change(tx_sev);
				Recharge_MONERY = "300";
				break;
			case R.id.tx_eight:
				view_change(tx_eight);
				Recharge_MONERY = "500";
				break;
			case R.id.tx_nine:
				view_change(tx_nine);
				Recharge_MONERY = "1000";
				break;
			case R.id.tx_phone_monery:
				tx_phone_monery.setTextColor(Color.RED);
				tx_recharge.setTextColor(Color.BLACK);
				ed_monery.setHint("请输入充值金额");
				tx_one.setText("10元");
				tx_two.setText("20元");
				tx_tri.setText("30元");
				tx_four.setText("50元");
				tx_five.setText("100元");
				tx_six.setText("200元");
				tx_sev.setText("300元");
				tx_eight.setText("500元");
				tx_nine.setText("1000元");
				ed_phone.clearFocus();
				ed_monery.clearFocus();
				break;
			case R.id.tx_recharge:
				tx_recharge.setTextColor(Color.RED);
				tx_phone_monery.setTextColor(Color.BLACK);
				ed_monery.setHint("请输入充值流量数量");
				tx_one.setText("30M");
				tx_two.setText("100M");
				tx_tri.setText("300M");
				tx_four.setText("500M");
				tx_five.setText("700M");
				tx_six.setText("1000M");
				tx_sev.setText("2000M");
				tx_eight.setText("3000M");
				tx_nine.setText("5000M");
				ed_phone.clearFocus();
				ed_monery.clearFocus();
				break;
			case R.id.tx_Pay:
				String monery = ed_monery.getText().toString();
				if (!monery.equals("")) {
					Recharge_MONERY = monery;
				}
				recharge_tx_phonenum.setText("充值号码："+phone_number);
				recharge_tx_phonemonery.setText("充值金额："+Recharge_MONERY+" 元");
				dia_recharge.show();
				dia_recharge.setCanceledOnTouchOutside(false);
				w = dia_recharge.getWindow();
				lp = w.getAttributes();
				lp.x = 0;
				lp.y = 40;
				dia_recharge.onWindowAttributesChanged(lp);
				break;
			case R.id.Recharge_qd:
				pay_num_phone();
				dia_recharge.cancel();
				break;
			case R.id.Recharge_qx:
				dia_recharge.cancel();
				break;
			}
		}
	};

	private void view_change(TextView text) {

		tx_one.setTextColor(Color.BLACK);
		tx_one.setBackgroundResource(R.anim.round_textview);
		tx_two.setTextColor(Color.BLACK);
		tx_two.setBackgroundResource(R.anim.round_textview);
		tx_tri.setTextColor(Color.BLACK);
		tx_tri.setBackgroundResource(R.anim.round_textview);
		tx_four.setTextColor(Color.BLACK);
		tx_four.setBackgroundResource(R.anim.round_textview);
		tx_five.setTextColor(Color.BLACK);
		tx_five.setBackgroundResource(R.anim.round_textview);
		tx_six.setTextColor(Color.BLACK);
		tx_six.setBackgroundResource(R.anim.round_textview);
		tx_sev.setTextColor(Color.BLACK);
		tx_sev.setBackgroundResource(R.anim.round_textview);
		tx_eight.setTextColor(Color.BLACK);
		tx_eight.setBackgroundResource(R.anim.round_textview);
		tx_nine.setTextColor(Color.BLACK);
		tx_nine.setBackgroundResource(R.anim.round_textview);
		if (text != null) {
			ed_phone.clearFocus();
			ed_monery.clearFocus();
			ed_monery.setText("");
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(ed_monery.getWindowToken(), 0);
			imm.hideSoftInputFromWindow(ed_phone.getWindowToken(), 0);
			text.setTextColor(Color.WHITE);
			text.setBackgroundResource(R.anim.round_textview_color);
		}
	}

	// 充值
	@SuppressLint("DefaultLocale")
	private void pay_num_phone() {
		if (Recharge_MONERY.equals(null)) {
			toast("请选择或者填写充值金额");
			return;
		}
		outOrderId = CryptTool.getCurrentDate()
				+ clientId.substring(clientId.length() - 4, clientId.length());
		String url = "http://120.55.72.146:6219/sXJCWI/getindex.aspx";
		// 签名参数
		StringBuffer s = new StringBuffer();
		s.append("agentid=").append("18354129777");
		s.append("&orderid=").append(outOrderId);
		s.append("&money=").append(Recharge_MONERY);
		s.append("&telephonetype=").append(Operator);// 运营商编码
		s.append("&mobilenum=").append(phone_number);
		s.append("&merchantKey=").append(
				"hjhUYT7678fgdeTGYUHu766PUPGYCW4390IHFvb");

		String szVerifyString = new String(Hex.encodeHex(DigestUtils.md5(s
				.toString()))).toLowerCase();
		try {
			Map<String, String> param = new HashMap<String, String>();

			param.put("agentid", "18354129777");
			param.put("orderid", outOrderId);
			param.put("money", Recharge_MONERY);
			param.put("telephonetype", Integer.toString(Operator));
			param.put("mobilenum", phone_number);
			param.put("verifystring", szVerifyString);
			param.put(
					"szNotifyUrl",
					URLEncoder
							.encode("http://vpos.cnyssj.net/api/phoneCreditRechargeCallback"));
			new Http_PushTask_false().execute(
					CryptTool.transMapToString(param), url);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 用Handler来更新UI
	 */
	private Handler handler = new Handler() {

		@SuppressLint("SimpleDateFormat")
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				toast("接口关闭");
				break;
			case 2:
				toast("IP未被授权");
				break;
			case 3:
				toast("参数不完整");
				break;
			case 4:
				toast("密钥串错误 ");
				break;
			case 5:
				toast("没有对应产品，不支持此金额充值");
				break;
			case 6:
				toast("余额不足");
				break;
			case 7:
				toast("提交失败");
				break;
			case 8:
				toast("充值成功");
				showmessagedata(200, null);
				showmessagedata(201, null);
				break;
			case 10:
				break;
			case 11:
				break;
			case 12:
				toast("不支持此号码");
				break;
			case 200:
				Snippet snippent = new Snippet();
				ed_yue.setText("商户余额：" + snippent.select_yue());

				break;
			case 201:
				Print_recharge pr = new Print_recharge();
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				pr.prilay(RechargeActivity.this, outOrderId, Recharge_MONERY,
						"", cashierName, clientId,
						sDateFormat.format(new java.util.Date()).toString(),
						phone_number);
				break;
			}
		}
	};

	public void showmessagedata(int message, String what) {
		Message localMessage = new Message();
		localMessage.what = message;
		localMessage.obj = what;
		this.handler.sendMessage(localMessage);
	}

	// 非阻塞http
	public class Http_PushTask_false extends AsyncTask<String, Void, String> {
		String retu = null;

		@Override
		protected String doInBackground(String... params) {
			if (params.length > 0) {
				httppost(params[0], params[1]);
			}
			return retu;
		}

		@SuppressWarnings("unchecked")
		private void httppost(String MapList, String url) {
			Map<String, String> param = new HashMap<String, String>();
			param = CryptTool.transStringToMap(MapList);
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			for (String key : param.keySet()) {
				paramList.add(new BasicNameValuePair(key, param.get(key)));
			}
			try {
				HttpEntity requestHttpEntity = new UrlEncodedFormEntity(
						paramList);
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(requestHttpEntity);
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = httpClient.execute(httpPost);
				showResponseResult(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onPostExecute(String result) {

			showmessagedata(Integer.parseInt(result), null);
		}

		private void showResponseResult(HttpResponse response) {
			if (null == response) {
				return;
			}
			HttpEntity httpEntity = response.getEntity();
			try {
				InputStream inputStream = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String result = "";
				String line = "";
				while (null != (line = reader.readLine())) {
					result += line;
				}
				retu = result;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private void toast(String str) {
		Toast.makeText(RechargeActivity.this, str, Toast.LENGTH_LONG).show();
	}
	// private void pay_num_monery() {
	//
	// outOrderId = CryptTool.getCurrentDate()
	// + clientId.substring(clientId.length() - 4, clientId.length());
	// String url = "http://120.55.72.146:6219/sXJCWI/getindex.aspx";
	// url.subSequence(4, 9);
	// // 签名参数
	// StringBuffer s = new StringBuffer();
	// s.append("szAgentId=").append("sdhuifull");
	// s.append("&szOrderId=").append(outOrderId);
	// s.append("&szPhoneNum=").append("13888888888");
	// s.append("&nMoney=").append("10");
	// s.append("&nSortType=").append("1");// 运营商编码
	// s.append("&nProductClass=1");// 1手机2固话3宽带
	// // 全国漫游包
	// s.append("&nProductType=").append("201");// 201 202
	// // 接口提交参数
	// SimpleDateFormat sDateFormat = new SimpleDateFormat(
	// "yyyy-MM-dd HH:mm:ss");
	// String szTimeStamp = sDateFormat.format(new java.util.Date());
	// s.append("&szTimeStamp=").append(szTimeStamp);
	// s.append("&szKey=").append("HOISDH468746soidghpsodfjk");
	//
	// String szVerifyString = new String(Hex.encodeHex(DigestUtils.md5(s
	// .toString()))).toLowerCase();
	// try {
	// Map<String, String> param = new HashMap<String, String>();
	//
	// param.put("szAgentId", "sdhuifull");
	// param.put("szOrderId", outOrderId);
	// param.put("szPhoneNum", "13888888888");
	// param.put("nMoney", "10");
	// param.put("nSortType", "1");
	// param.put("nProductClass", "1");
	// param.put("nProductType", "201");
	//
	// param.put("szTimeStamp", szTimeStamp);
	// param.put("szVerifyString", szVerifyString);
	//
	// new Http_PushTask_false().execute(
	// CryptTool.transMapToString(param), url);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }
}
