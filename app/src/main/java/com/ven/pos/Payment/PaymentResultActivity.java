package com.ven.pos.Payment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sourceforge.simcpux.MD5Util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnyssj.db.DBManager;
import com.cnyssj.db.util.Order;
import com.cnyssj.pos.R;
import com.cnyssj.widget.ShapeLoadingDialog;
import com.jhj.Agreement.ZYB.CryptTool;
import com.jhj.Agreement.ZYB.Http_PushTask;
import com.jhj.Agreement.ZYB.Http_Query_ZYB;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.jhj.Agreement.ZYB.Url;
import com.jhj.print.Print;
import com.ven.pos.GlobalContant;
import com.ven.pos.ITitleBarLeftClick;
import com.ven.pos.TitleBar;
import com.ven.pos.Util.ActivityManager;
import com.ven.pos.Util.BaseActivity;
import com.ven.pos.Util.HttpConnection;
import com.ven.pos.Util.Time;
import com.ven.pos.Util.UtilTool;

public class PaymentResultActivity extends BaseActivity {
	public class GetPayReq {

		String getPayTypeName() {
			return "";
		}
	}

	private Context context;
	public static final String IntentPayType = "PayType";
	public static final String IntentPayResult = "PayResult";
	public static final String IntentPayErrMsg = "errorMsg";
	public static final String IntentPayMoney = "PayMoney";
	public static final String IntentPayTradeNo = "PayTradeNo";
	public static final String IntentPayTime = "PayTime";
	public static final String IntentPayDetails = "PayDetails";
	public static final String IntentDiscount = "discount"; // 最终折扣价
	public static final String IntentSncodeid = "sncodeid"; // 使用的优惠券ID
	public static final String IntentUid = "uid"; // 用户ID
	public static final String IntentBeizhu = "beizhu"; // 备注
	public static final String IntentOperater = "oper"; // 操作类型 1:消费 2:充值
	SharedPreferences_util su = new SharedPreferences_util();
	private static final String DELAYORDER = "delayorder";
	public static PaymentResultActivity paymentResultActivity;
	// 打印机
	Print pt = new Print();
	Http_Query_ZYB HQ = new Http_Query_ZYB();
	Time dt = new Time();
	DBManager DBM;
	private ImageView pay_notice_img;
	private TextView pay_notice_msg_tv, pay_notice_money_tv,
			pay_notice_orderno_tv, pay_notice_ordertime_tv,
			pay_notice_paytype_tv, pay_notice_msg_error_tv;

	private Button print_ticket_ll;
	// 弹出框

	String str_monery;
	// 交易码
	String result_r;
	// 店名
	String cashierName;
	// 设备终端号
	String clientId;
	// 掌优宝支付类型
	int orderType;
	// 原平台支付类型
	int PaymentMethod;
	// 商品列表
	String PayDetails;
	// 订单号
	String outOrderId;
	// 掌优宝平台订单号
	String order;
	// 下单时间
	String Time_new;
	// 消费类型
	int Operater;
	String merchantId, keyString;
	// 支付开关
	boolean Payment_switch;
	Dialog dia_errorcode;
	Window w;
	WindowManager.LayoutParams lp;
	Button dia_error_qd, dia_error_qx, dia_error_cx;

	List<QueryThread> all_thread = new ArrayList<QueryThread>();

	ShapeLoadingDialog shapeLoadingDialog;
	//
	List<Order> myorder = new ArrayList<Order>();

	@SuppressWarnings("static-access")
	public void onCreate(Bundle paramBundle) {

		super.onCreate(paramBundle);

		TitleBar.setTitleBar(this, "", "支付结果", "完成", new EndClick());
		context = this;
		// 检查是否异地登陆
		UtilTool.checkLoginStatusAndReturnLoginActivity(this, this.handler);
		setContentView(R.layout.pay_result);
		initView();
		// 弹出框
		Payment_switch = getIntent().getBooleanExtra("Payment_switch", false);

		str_monery = getIntent().getExtras().getString("str_monery");
		PayDetails = getIntent().getExtras().getString("PayDetails");

		outOrderId = getIntent().getExtras().getString("outOrderId");
		// 原平台支付类型
		PaymentMethod = getIntent().getExtras().getInt("PaymentMethod");
		if (Payment_switch) {
			// 掌优宝支付类型
			// outOrderId="";
			orderType = getIntent().getExtras().getInt("orderType");
			// 交易码
			result_r = getIntent().getExtras().getString("result_r");
			Operater = getIntent().getExtras().getInt("IntentOperater");
			// 开始支付
			showmessage(5);
		} else {
			Time_new = getIntent().getExtras().getString("Time_new");
			order = outOrderId;
			result_r = "无（非扫码方式）";
			showmessage(7);
		}
		// 设备终端号
		clientId = su.getPrefString(PaymentResultActivity.this, "clientId",
				null);
		merchantId = su.getPrefString(PaymentResultActivity.this, "merchantId",
				null);
		cashierName = su.getPrefString(PaymentResultActivity.this,
				"cashiername", null);
		keyString = su.getPrefString(PaymentResultActivity.this, "key", null);
	}

	// Titlebar按钮监听
	private class EndClick implements ITitleBarLeftClick {
		public void click() {
			ActivityManager.getInstance().closeAllActivityNoincludeMain();
			// if (null != ConsumeListView.instance().consumelistArray)
			// ConsumeListView.instance().consumelistArray.clear();
			Intent intent = new Intent(PaymentResultActivity.this,
					PaymentCashierMainAct.class);
			// finish();
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
		}
	}

	public void initView() {
		shapeLoadingDialog = new ShapeLoadingDialog(PaymentResultActivity.this);

		paymentResultActivity = this;

		dia_errorcode = new Dialog(PaymentResultActivity.this,
				R.style.edit_AlertDialog_style);
		dia_errorcode.setContentView(R.layout.activity_pay_error);
		dia_error_qd = (Button) dia_errorcode.findViewById(R.id.payerror_qd);
		dia_error_qx = (Button) dia_errorcode.findViewById(R.id.payerror_qx);
		dia_error_cx = (Button) dia_errorcode.findViewById(R.id.payerror_cx);
		dia_error_qd.setOnClickListener(new OnClickListener() {
			//
			@Override
			public void onClick(View v) {
				showmessage(7);
				dia_errorcode.dismiss();
			}
		});
		dia_error_qx.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showmessage(9);
				dia_errorcode.dismiss();
			}
		});
		dia_error_cx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				(new QueryThread()).start();
			}
		});

		this.pay_notice_img = (ImageView) findViewById(R.id.pay_notice_img);
		this.pay_notice_msg_tv = (TextView) findViewById(R.id.pay_notice_msg_tv);
		this.pay_notice_money_tv = (TextView) findViewById(R.id.pay_notice_money_tv);
		this.pay_notice_orderno_tv = (TextView) findViewById(R.id.pay_notice_orderno_tv);
		this.pay_notice_ordertime_tv = (TextView) findViewById(R.id.pay_notice_ordertime_tv);
		this.pay_notice_paytype_tv = (TextView) findViewById(R.id.pay_notice_paytype_tv);
		this.pay_notice_msg_error_tv = (TextView) findViewById(R.id.pay_notice_error_msg_tv);
		this.print_ticket_ll = (Button) findViewById(R.id.print_ticket_text);
		this.pay_notice_msg_tv.setVisibility(View.INVISIBLE);
		// 打印按钮
		this.print_ticket_ll.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("static-access")
			public void onClick(View paramAnonymousView) {
				String print = su.getPrefString(PaymentResultActivity.this,
						"print", null);
				if (Build.MANUFACTURER.contains("BASEWIN")
						|| Build.MANUFACTURER.contains("basewin")) {
					try {
						if (print != null) {
							JSONObject jsonObject = new JSONObject(print);
							String orderId = jsonObject.getString("orderId")
									.toString();
							String monery = jsonObject.getString("monery")
									.toString();
							String paycode = jsonObject.getString("paycode")
									.toString();
							String PaymentMethod = jsonObject.getString(
									"PaymentMethod").toString();
							String clientId = jsonObject.getString("clientId")
									.toString();
							String Time = jsonObject.getString("Time")
									.toString();
							String shanghm = jsonObject.getString("shanghm")
									.toString();
							Print pt = new Print();
							pt.prilay(PaymentResultActivity.this, orderId,
									monery, paycode, PaymentMethod, shanghm,
									clientId, Time);
						} else {
							showToast("没有有效的交易数据");
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					showToast(Build.MANUFACTURER.toString());
					showToast("未检测到打印机！无法进行打印打印");
				}
			}
		});

	}

	protected void onDestroy() {
		super.onDestroy();
	}

	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.prnt.message");
	}

	protected void onPause() {
		super.onPause();
	}

	public void onStart() {
		TitleBar.setActivity(this);
		super.onStart();
	}

	@Override
	protected void onStop() {
		for (QueryThread thread : all_thread) {
			thread.done();
		}
		super.onStop();
	}

	/**
	 * 用Handler来更新UI
	 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@SuppressLint("SimpleDateFormat")
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
				break;
			}
			case UtilTool.CHECKRETURN: {
				break;
			}
			case 1:
				showToast("待付款！");
				updata_shop_items_paymentstatus(order, 1);
				break;
			case 3:
				showToast("交易超时！");
				showmessage(8);
				updata_shop_items_paymentstatus(order, 1);
				break;
			case 4:
				showToast("已取消！");
				updata_shop_items_paymentstatus(order, 1);
				break;
			case 5:
				// 交易
				ShowProDlg();
				pay(Integer.parseInt(str_monery), result_r, cashierName,
						clientId, orderType, PaymentMethod);
				break;
			case 6:
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Time_new = sDateFormat.format(new java.util.Date());
				// 查询
				(new QueryThread()).start();
				break;
			case 7:
				// 支付成功
				// 保存数据,并发送数据
				updata_shop_items_paymentstatus(outOrderId, 3);
				updata_order_goods(outOrderId, 3);
				pay_notice_msg_tv.setVisibility(View.VISIBLE);
				// 清除列表数据
				showToast("支付成功！！！！");
				if (null != ConsumeListView.instance().consumelistArray)
					ConsumeListView.instance().consumelistArray.clear();
				//
				(new SaveTradeInfoThread()).start();

				pay_notice_money_tv.setText(Double.toString(Double
						.parseDouble(str_monery) / 100));
				pay_notice_orderno_tv.setText(order);
				pay_notice_ordertime_tv.setText(Time_new);
				if (PaymentMethod == PaymentMainActivity.ALIPAYWAY) {
					pay_notice_paytype_tv.setText("支付宝");
				} else if (PaymentMethod == PaymentMainActivity.WEIXINPAYWAY) {
					pay_notice_paytype_tv.setText("微信支付");
				} else if (PaymentMethod == PaymentMainActivity.CASHPAYWAY) {
					pay_notice_paytype_tv.setText("现金支付");
				} else if (PaymentMethod == PaymentMainActivity.CONSUME_PAYWAY) {
					pay_notice_paytype_tv.setText("会员卡支付");
				} else if (PaymentMethod == PaymentMainActivity.YIZHIFU) {
					pay_notice_paytype_tv.setText("翼支付");
				} else if (PaymentMethod == PaymentMainActivity.JIFEN) {
					pay_notice_paytype_tv.setText("积分兑换");
				}
				if (Build.MANUFACTURER.contains("BASEWIN")
						|| Build.MANUFACTURER.contains("basewin")) {

					pt.prilay(PaymentResultActivity.this, order, Double
							.toString(Double.parseDouble(str_monery) / 100),
							result_r, Paymode(PaymentMethod), cashierName,
							clientId, Time_new);
				} else {
					showToast("未检测到打印机！无法进行打印打印");
				}
				break;
			case 8:
				// 支付失败
				DismissProDlg();
				dia_errorcode.show();
				dia_errorcode.setCanceledOnTouchOutside(false);
				w = dia_errorcode.getWindow();
				lp = w.getAttributes();
				lp.x = 0;
				lp.y = 40;
				dia_errorcode.onWindowAttributesChanged(lp);
				break;

			case 9:
				pay_notice_img.setBackgroundResource(R.drawable.ps_icon_fail);
				pay_notice_msg_tv.setText(R.string.pay_fails);
				pay_notice_msg_tv.setTextColor(getResources().getColor(
						R.color.fail_text_green));
				pay_notice_msg_error_tv.setText("");
				print_ticket_ll.setVisibility(View.INVISIBLE);
				break;
			}
		}
	};

	// 发送handle
	public void showmessage(int message) {
		Message localMessage = new Message();
		localMessage.what = message;
		this.handler.sendMessage(localMessage);
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

	// 保存数据
	class SaveTradeInfoThread extends Thread {

		public void run() {
			Looper.prepare();
			boolean sinkactivity = getIntent().getBooleanExtra("mark_activity",
					false);
			if (sinkactivity) {

			}
			String printTickUrl = GlobalContant.instance().mainUrl
					+ getResources().getString(R.string.save_ticket_url);
			// 查询是否有缓存的订单数据
			// delayOrderDispose();
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("token", GlobalContant.instance().token);
			// PayTradeNo 掌优宝订单号 orderId
			sParaTemp.put("orderid", order);
			// PayMoney 金额
			sParaTemp.put("money",
					Double.toString(Double.parseDouble(str_monery) / 100));
			// PayType 原平台支付类型
			sParaTemp.put("type", String.valueOf(PaymentMethod));
			// PayTradeNo
			String sign = MD5Util.MD5Encode(GlobalContant.SECRET_KEY + order,
					"UTF-8");
			sParaTemp.put("autograph", sign);

			if (Operater == 2) {
				sParaTemp.put("op", "addcardprice");
			} else if (Operater == 3) {
				sParaTemp.put("op", "server");
			} else {
				sParaTemp.put("op", "memberpay");
			}
			// 折后价
			if (null != getIntent().getExtras().getString(IntentDiscount)) {
				sParaTemp.put("dis",
						getIntent().getExtras().getString(IntentDiscount));
			} else {
				sParaTemp.put("dis", "0");
			}
			// 使用的优惠权ID
			if (null != getIntent().getExtras().getString(IntentSncodeid)) {
				sParaTemp.put("sncodeid",
						getIntent().getExtras().getString(IntentSncodeid));
			} else {
				sParaTemp.put("sncodeid", "0");
			}
			// 用户iD
			if (null != getIntent().getExtras().getString(IntentUid)) {
				sParaTemp.put("uid",
						getIntent().getExtras().getString(IntentUid));
			} else {
				sParaTemp.put("uid", "0");
			}
			// 备注
			if (null != getIntent().getExtras().getString(IntentBeizhu)) {
				sParaTemp.put(IntentBeizhu,
						getIntent().getExtras().getString(IntentBeizhu));
			} else {
				sParaTemp.put(IntentBeizhu, "");
			}
			// 商品明细
			sParaTemp.put("items", PayDetails); // 商品列表详情，json串s

			String str1;
			try {

				// 上送商品信息
				str1 = HttpConnection.buildRequest(printTickUrl, sParaTemp);
				Log.e("tempservice", String.valueOf(sParaTemp));
				if (str1 == null) {
					showToast("连接失败");
					updata_upload_status(outOrderId, 0);
					return;
				}
				JSONTokener jsonParser = new JSONTokener(str1);
				JSONObject json = (JSONObject) jsonParser.nextValue();

				if (!json.getString("status").equals("1")) {
					showToast("保存交易结果失败，请检查网络连接");
					updata_upload_status(outOrderId, 0);
					return;
				}
				updata_upload_status(outOrderId, 1);

			} catch (Exception localException1) {
				localException1.printStackTrace();
			}
			Looper.loop();
		}

	}

	// 保存数据
	class SaveTradeInfoThread_status extends Thread {

		public void run() {
			Looper.prepare();
			DBM = new DBManager(getApplicationContext());
			String printTickUrl = GlobalContant.instance().mainUrl
					+ getResources().getString(R.string.save_ticket_url);

			if (myorder.size() > 0) {
				int i = 0;
				while (myorder.size() > i) {

					Order order = myorder.get(i);
					outOrderId = order.getMyorderid();
					Map<String, String> sParaTemp = new HashMap<String, String>();

					sParaTemp.put("token", GlobalContant.instance().token);
					String orderid;
					if (!TextUtils.isEmpty(String.valueOf(order.getOrderid()))) {
						orderid = order.getOrderid();
					} else {
						orderid = outOrderId;
					}

					sParaTemp.put("orderid", orderid);
					sParaTemp.put("money", order.getAmount());
					sParaTemp.put("type", String.valueOf(order.getType()));
					String sign_sc = MD5Util.MD5Encode(GlobalContant.SECRET_KEY
							+ orderid, "UTF-8");
					sParaTemp.put("autograph", sign_sc);
					sParaTemp.put("op", "memberpay");
					sParaTemp.put("dis", "0");
					sParaTemp.put("sncodeid", "0");
					sParaTemp.put("uid", "0");
					sParaTemp.put("beizhu", "");
					sParaTemp.put("items", order.getItems()); // 商品列表详情，json串s
					String str1;
					try {
						// 上送商品信息
						str1 = HttpConnection.buildRequest(printTickUrl,
								sParaTemp);

						if (str1 == null) {

							updata_upload_status(outOrderId, 0);

						} else {
							JSONTokener jsonParser = new JSONTokener(str1);
							JSONObject json = (JSONObject) jsonParser
									.nextValue();
							Log.e("tiaoshu", "return right");
							Log.e("tiaoshu", str1);
							if (!json.getString("status").equals("1")) {
								updata_upload_status(outOrderId, 0);

							} else {
								updata_upload_status(outOrderId, 1);

							}
						}

					} catch (Exception localException1) {
						localException1.printStackTrace();
					}
					i++;
				}
			}
		}
	}

	// 保存数据

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 付款
	 * */
	public void pay(int totalFee, String paycode, String cashierName,
			String clientId, int orderType, int PaymentMethod) {
		updata_shop_items_paymentstatus(outOrderId, 7);
		// utf-8
		String inputCharset = "utf-8";
		// 加密
		String signType = "MD5";
		// 接受异步通知的URL地址
		String notifyUrl = "http://121.41.113.42:8080/PaymentCallback";
		// 签名
		String signature = null;

		StringBuilder sb = new StringBuilder();// 组装mac加密明文串
		sb.append("clientId=").append(clientId);
		sb.append("&inputCharset=").append(inputCharset);
		sb.append("&merchantId=").append(merchantId);
		sb.append("&notifyUrl=").append(notifyUrl);
		sb.append("&orderType=").append(orderType);
		sb.append("&outOrderId=").append(outOrderId);
		sb.append("&payCode=").append(paycode);
		sb.append("&signType=").append(signType);
		sb.append("&totalFee=").append(totalFee);
		sb.append("").append(keyString);
		try {
			signature = CryptTool.md5Digest(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 进行md5加密(商户自己封装MD5加密工具类，此处只提供参考)
		Map<String, String> param = new HashMap<String, String>();// 组装请求参数
		// param.put("cashierName", cashierName);
		param.put("clientId", clientId);
		param.put("inputCharset", inputCharset);
		param.put("merchantId", merchantId);
		param.put("notifyUrl", notifyUrl);
		param.put("orderType", Integer.toString(orderType));
		param.put("outOrderId", outOrderId);
		param.put("payCode", paycode);
		param.put("signType", signType);
		param.put("totalFee", Integer.toString(totalFee));
		param.put("signature", signature);
		// 发送
		new Http_PushTask_false().execute(CryptTool.transMapToString(param),
				Url.transaction_zyb);
		// new Http_PushTask_false().execute(CryptTool.transMapToString(param),
		// "http://www.kuaizh.com/apidatarequest.php?id=62");

	}

	/**
	 * 查询
	 * */
	class QueryThread extends Thread {
		int start, end;

		private boolean isDone = false;

		public void done() {
			isDone = true;
		}

		public void run() {
			all_thread.add(this);
			start = dt.getTime();
			int i = 0;
			while (i != 7 && isDone != true) {
				if (end - start > 60) {
					showToast("获取信息超时！");
					showmessage(8);
					break;
				}
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				// 签名
				String signature = null;
				// 掌优宝平台订单号
				// utf-8
				String inputCharset = "utf-8";
				// 加密类型
				String signType = "MD5";
				StringBuilder sb = new StringBuilder();// 组装mac加密明文串
				sb.append("inputCharset=").append(inputCharset);
				sb.append("&merchantId=").append(merchantId);
				if (order != null) {
					sb.append("&orderId=").append(order);
				} else {
					showToast("数据不完整，无法查询当前交易！");
					return;
				}

				sb.append("&outOrderId=").append(outOrderId);
				sb.append("&signType=").append(signType);
				sb.append("").append(keyString);
				try {
					signature = CryptTool.md5Digest(sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}// 进行md5加密(商户自己封装MD5加密工具类，此处只提供
					// 参考)
				Map<String, String> param = new HashMap<String, String>();// 组装请求参数，参数名大小写敏感
				param.put("inputCharset", inputCharset);
				param.put("merchantId", merchantId);
				param.put("orderId", order);
				param.put("outOrderId", outOrderId);
				param.put("signature", signature);
				param.put("signType", signType);

				Http_PushTask HP = new Http_PushTask();

				try {

					String result = HP.execute(
							CryptTool.transMapToString(param), Url.query_zyb)
							.get(6000, TimeUnit.MILLISECONDS);

					if (!TextUtils.isEmpty(result)) {
						JSONObject js = new JSONObject(result);
						if (js.getBoolean("result")) {
							if (js.has("message")) {
								if (js.has("orderState")) {
									// 交易状态
									showmessage(js.getInt("orderState"));
									i = js.getInt("orderState");
									DismissProDlg();
								}
							}
						} else {
							showToast(js.get("message").toString());
							DismissProDlg();
							showmessage(8);
							break;
						}
					} else {
						showmessage(8);
						break;
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

	public String Paymode(int modenum) {
		String paymode = null;
		if (modenum == 1) {
			paymode = "现金";
		} else if (modenum == 3) {
			paymode = "支付宝";
		} else if (modenum == 4) {
			paymode = "微信";
		} else if (modenum == 6) {
			paymode = "翼支付";
		}
		return paymode;
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
			JSONObject js;
			try {
				js = new JSONObject(result);

				if (!TextUtils.isEmpty(result)) {

					if (js.getBoolean("result")) {
						order = js.get("orderId").toString();
						updata_order(outOrderId, order);
						showToast(js.get("message").toString());
						// 进行查询
						showmessage(6);
					} else {
						// 创建订单失败
						DismissProDlg();
						showToast(js.get("message").toString());
					}
				} else {
					showmessage(8);
					DismissProDlg();
					showToast("交易失败");
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
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

	// 支付状态
	private void updata_shop_items_paymentstatus(String outOrderId,
			int paymentstatus) {
		DBM = new DBManager(getApplicationContext());
		if (DBM.updatePayStatus(outOrderId, paymentstatus)) {
			// showToast("更新状态成功");
		} else {
			showToast("更新状态失败_支付状态_res");
		}
	}

	// 更改单号
	private void updata_order(String outOrderId, String order) {
		DBM = new DBManager(getApplicationContext());
		if (DBM.updateOrderid(outOrderId, order)) {
			// showToast("更新状态成功");
		} else {
			showToast("更新状态失败_更改单号_res");
		}
	}

	// 商品上传状态
	private void updata_upload_status(String myorderid, int uploadstatus) {
		DBM = new DBManager(getApplicationContext());
		if (DBM.updateUploadStatus(myorderid, uploadstatus)) {

		}
	}

	// 更新商品订单完成状态
	private void updata_order_goods(String outOrderId, int paytype) {
		DBM = new DBManager(getApplicationContext());
		if (DBM.updateOrderstatus(outOrderId, paytype)) {
			// showToast("更新状态成功");
		} else {

		}
	}

	public void showToast(String tips) {
		Toast.makeText(this, tips, Toast.LENGTH_SHORT).show();
	}

}
