package com.ven.pos;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sourceforge.simcpux.MD5Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.cnyssj.pos.R;
import com.jhj.Agreement.ZYB.CryptTool;
import com.jhj.Agreement.ZYB.Http_PushTask;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.jhj.Agreement.ZYB.Url;
import com.jhj.print.Print_JIFEN;
import com.ven.pos.Payment.ConsumeListView;
import com.ven.pos.Payment.PaymentCashierMainAct;
import com.ven.pos.Payment.PaymentMainActivity;
import com.ven.pos.Payment.PaymentResultActivity;
import com.ven.pos.Util.HttpConnection;
import com.ven.pos.Util.Time;

public class ExchangeActivity extends Activity {
	SharedPreferences_util su = new SharedPreferences_util();
	WebView exchange_web;
	// 积分,兑换金额,商品列表
	String integral, totalFee, PayDetails, shopname, monery, totalMoney;

	String outOrderId;
	private static Handler mHandler = null;
	Time dt = new Time();
	String message;
	String name = "";
	String merchantId, clientId, key, cashierName, phone_num,
			exchange_monery_free;
	int Operater = 11;
	private final String DELAYORDER = "delayorder";
	boolean mark_activity;

	@SuppressWarnings("static-access")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exchange);
		// 商户号
		merchantId = su
				.getPrefString(ExchangeActivity.this, "merchantId", null);
		// 设备终端号
		clientId = su.getPrefString(ExchangeActivity.this, "clientId", null);
		key = su.getPrefString(ExchangeActivity.this, "key", null);
		cashierName = su.getPrefString(ExchangeActivity.this, "cashiername",
				null);
		// 积分数量
		integral = getIntent().getExtras().getString("integral");
		monery = getIntent().getExtras().getString("totalFee");
		totalFee = Double.toString(Double.parseDouble(monery) * 100);
		PayDetails = getIntent().getExtras().getString("PayDetails");
		exchange_monery_free = getIntent().getExtras().getString(
				"exchange_monery_free");
		totalMoney = getIntent().getExtras().getString("TotalMoney");
		exchange_web = (WebView) findViewById(R.id.exchange_web);

		try {
			JSONArray details = new JSONArray(PayDetails);
			for (int i = 0; i < details.length(); i++) {
				name = name + details.getJSONObject(i).getString("name") + ","
						+ details.getJSONObject(i).getString("amount") + ";";
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		outOrderId = CryptTool.getCurrentDate();

		if (!TextUtils.isEmpty(merchantId) || !TextUtils.isEmpty(clientId)) {

			exchange_web
					.loadUrl("http://www.zhangyoobao.cn/merchant/notify/queryIntegralCode?merchantId="
							+ merchantId
							+ "&clientId="
							+ clientId
							+ "&exchange_point="
							+ integral
							+ "&exchange_type="
							+ name.substring(0, name.length() - 1)
							+ "&outOrderId="
							+ outOrderId
							+ "&totalFee="
							+ totalFee.substring(0, totalFee.length() - 2));
		} else {
			toast("没有有效的数据！");
		}
		exchange_web.getSettings().setJavaScriptEnabled(true);
		exchange_web.setWebChromeClient(new WebChromeClient());
		// 提交数据
		// (new SaveTradeInfoThread()).start();
		exchange_web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
					String url_bool = url.substring(0, 59);
					if (url_bool
							.equals("http://www.zhangyoobao.cn/merchant/notify/integral_exchange")) {
						(new QueryThread()).start();

					} else {

					}

				} else {

				}
			}
		});
		mHandler = new Handler() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					// 待付款
					toast("待付款");
					break;
				case 2:
					toast("积分兑换成功");
					skipActivity(outOrderId);
					break;
				case 3:
					toast("获取信息超时！");
					break;
				case 4:
					// 已取消
					mark_activity = false;
					skipActivity(outOrderId);
					toast("兑换失败！");
					break;
				case 5:
					toast(message);
					break;
				case 6:
					toast("获取信息失败！");
					break;
				case 7:
					mark_activity = true;
					(new SaveTradeInfoThread()).start();
					break;
				default:
					break;
				}
			}
		};
	}

	public static void showmessage(int id) {
		if (mHandler != null) {
			Message message = Message.obtain(mHandler, id);
			mHandler.sendMessage(message);
		}
	}

	class QueryThread extends Thread {
		int start, end;

		public void run() {
			start = dt.getTime();
			int i = 0;
			while (i != 7) {
				if (end - start > 60) {
					showmessage(3);
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				// 签名
				String signature = null;
				// utf-8
				String inputCharset = "utf-8";
				// 加密类型
				String signType = "MD5";

				StringBuilder sb = new StringBuilder();// 组装mac加密明文串
				//
				sb.append("inputCharset=").append(inputCharset);
				sb.append("&merchantId=").append(merchantId);
				sb.append("&outOrderId=").append(outOrderId);
				// sb.append("&orderId=").append("201612060926521180321");
				sb.append("&signType=").append(signType);
				sb.append("").append(key);
				try {
					signature = CryptTool.md5Digest(sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}// 进行md5加密(商户自己封装MD5加密工具类，此处只提供
					// 参考)
				Map<String, String> param = new HashMap<String, String>();// 组装请求参数，参数名大小写敏感
				param.put("inputCharset", inputCharset);
				param.put("merchantId", merchantId);
				param.put("outOrderId", outOrderId);
				// param.put("orderId", "201612060926521180321");
				param.put("signature", signature);
				param.put("signType", signType);
				Http_PushTask HP = new Http_PushTask();
				try {
					String result = HP.execute(
							CryptTool.transMapToString(param),
							Url.exchange_query).get(60000,
							TimeUnit.MILLISECONDS);

					JSONObject js = new JSONObject(result);
					if (!TextUtils.isEmpty(result)) {
						if (js.getBoolean("result")) {
							showmessage(js.getInt("orderState"));
							// 处理
							break;
						} else {
							message = js.get("message").toString();
							showmessage(5);
						}

					} else {
						showmessage(6);
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					e.printStackTrace();
				}
				end = dt.getTime();
			}
		}
	}

	public void skipActivity(String outOrderId) {

		Intent intent = new Intent();
		if (!exchange_monery_free.equals("0.0")) {

			// 标记
			intent.putExtra("mark_activity", mark_activity);
			// 还需要的金额
			if (mark_activity) {
				intent.putExtra("TotalMoney", exchange_monery_free);
				intent.putExtra("outOrderId", outOrderId);
				prilay();
			} else {
				intent.putExtra("TotalMoney", totalMoney);
				intent.putExtra("PayDetails", PayDetails);
			}
			// 订单
			intent.setClass(ExchangeActivity.this, PaymentMainActivity.class);
			startActivity(intent);
			finish();
		} else {
			if (null != ConsumeListView.instance().consumelistArray)
				ConsumeListView.instance().consumelistArray.clear();
			intent.setClass(ExchangeActivity.this, PaymentCashierMainAct.class);
			startActivity(intent);
			finish();
			prilay();
		}

	}

	// 保存数据
	class SaveTradeInfoThread extends Thread {
		public void run() {
			Looper.prepare();
			String printTickUrl = GlobalContant.instance().mainUrl
					+getResources().getString(R.string.save_ticket_url);
			// 查询是否有缓存的订单数据
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("token", GlobalContant.instance().token);
			sParaTemp.put("type", "11");
			// PayTradeNo 掌优宝订单号 orderId
			sParaTemp.put("orderid", outOrderId);
			// 对应积分 integral
			sParaTemp.put("money", integral);
			// 积分对应金额 totalFee
			sParaTemp.put("points_real_money", monery);
			// 总价 totalMoney
			sParaTemp.put("total_amount", totalMoney);
			// PayTradeNo
			String strOrderId = outOrderId;
			String sign = MD5Util.MD5Encode(GlobalContant.SECRET_KEY
					+ strOrderId, "UTF-8");
			sParaTemp.put("autograph", sign);
			if (Operater == 2) {
				sParaTemp.put("op", "addcardprice");
			} else if (Operater == 3) {
				sParaTemp.put("op", "server");
			} else {
				sParaTemp.put("op", "memberpay");
			}
			// 商品明细
			sParaTemp.put("items", PayDetails); // 商品列表详情，json串s
			String str1;
			try {
				// 上送商品信息
				str1 = HttpConnection.buildRequest(printTickUrl, sParaTemp);
				// showToast(str1);
				if (str1 == null) {
					PaymentResultActivity.paymentResultActivity
							.showToast("连接失败");
					saveOrderSP(printTickUrl, sParaTemp, outOrderId);
					return;
				}

				JSONTokener jsonParser = new JSONTokener(str1);
				JSONObject json = (JSONObject) jsonParser.nextValue();
				if (!json.getString("status").equals("1")) {
					PaymentResultActivity.paymentResultActivity
							.showToast("保存交易结果失败，请检查网络连接");
					saveOrderSP(printTickUrl, sParaTemp, outOrderId);
					return;
				} else {
					showmessage(2);
				}
			}

			catch (Exception localException1) {
				localException1.printStackTrace();
			}
			Looper.loop();
		}
	}

	private void saveOrderSP(String printTickUrl, Map<String, String> map,
			String id) {
		SharedPreferences sp = ExchangeActivity.this.getSharedPreferences(id,
				MODE_PRIVATE);
		// 存入数据
		Editor editor = sp.edit();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			System.out.println("key= " + entry.getKey() + " and value= "
					+ entry.getValue());
			editor.putString(entry.getKey(), entry.getValue());
		}
		editor.putString("printTickUrl", printTickUrl);
		editor.commit();

		// 保存键值
		SharedPreferences delay = ExchangeActivity.this.getSharedPreferences(
				DELAYORDER, MODE_PRIVATE);
		// 存入数据
		Editor delayedit = delay.edit();
		delayedit.putString(id, id);
		delayedit.commit();
		delaySaveInfo(printTickUrl, map, id);

	}

	private void delaySaveInfo(final String Url, final Map<String, String> map,
			final String id) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					try {
						String str1 = HttpConnection.buildRequest(Url, map);

						if (str1 == null) {
							PaymentResultActivity.paymentResultActivity
									.showToast("连接失败");
							continue;
						}

						JSONTokener jsonParser = new JSONTokener(str1);
						JSONObject json = (JSONObject) jsonParser.nextValue();

						if (!json.getString("status").equals("1")) {
							continue;
						}

						clearOrderSP(id);
						return;

					} catch (Exception localException1) {
						localException1.printStackTrace();
						continue;
					}

				}
			}
		}).start();

	}

	private void clearOrderSP(String id) {
		SharedPreferences sp = ExchangeActivity.this.getSharedPreferences(id,
				MODE_PRIVATE);
		// 存入数据
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
		// 删除键值
		SharedPreferences delay = ExchangeActivity.this.getSharedPreferences(
				DELAYORDER, MODE_PRIVATE);
		Editor delayedit = delay.edit();
		delayedit.remove(id);
		delayedit.commit();
	}

	private void prilay() {
		if (Build.MANUFACTURER.contains("BASEWIN")
				|| Build.MANUFACTURER.contains("basewin")) {
			Print_JIFEN pt = new Print_JIFEN();
			/**
			 * @outOrderId 订单号
			 * @number 积分数量
			 * @PaymentMethod 付款方式
			 * @TerminalNumber 终端号
			 * @Time 时间
			 * */
			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss");
			String Time_new = sDateFormat.format(new java.util.Date());
			pt.prilay(ExchangeActivity.this, outOrderId, integral, "积分兑换",
					cashierName, clientId, Time_new);
		} else {
			toast(Build.MANUFACTURER.toString());
			toast("未检测到打印机！无法进行打印打印");
		}
	}

	public void toast(String str) {
		Toast.makeText(ExchangeActivity.this, str, Toast.LENGTH_SHORT).show();
	}
}
