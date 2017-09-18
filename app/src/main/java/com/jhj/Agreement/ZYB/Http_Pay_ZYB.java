package com.jhj.Agreement.ZYB;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

public class Http_Pay_ZYB {
	static String mac;
	static String outOrderId;
	// 支付平台的支付码 扫描二维码
	String paycode;
	String merchantnum;
	int orderType;
	int PaymentMethod;
	int totalFee;
	Context context;
	String cashierName;
	String clientId;
	// 商品明细
	String PayDetails;
	SharedPreferences_util su = new SharedPreferences_util();

	public void runService(int totalFee, Context context, String paycode,
			String cashierName, String clientId, int orderType,
			int PaymentMethod, String PayDetails) {
		this.totalFee = totalFee;
		this.context = context;
		this.paycode = paycode;
		this.cashierName = cashierName;
		this.clientId = clientId;
		this.orderType = orderType;
		this.PaymentMethod = PaymentMethod;
		this.PayDetails = PayDetails;
		new Thread(new Runnable() {
			public void run() {
				myShowDilog(1);
			}
		}).start();
	}

	public void myShowDilog(int what) {
		Message msg = new Message();
		msg.what = what;
		this.mHandler.sendMessage(msg);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				pay(totalFee, context, paycode, cashierName, clientId,
						orderType, PaymentMethod);
				break;
			case 2:

				break;

			}

		}

	};

	@SuppressWarnings({ "unused", "static-access" })
	public void pay(int totalFee, Context context, String paycode,
			String cashierName, String clientId, int orderType,
			int PaymentMethod) {
		this.context = context;
		merchantnum = clientId;
		this.orderType = orderType;
		this.PaymentMethod = PaymentMethod;

		String keyString = su.getPrefString(context, "key", null);
		Http_Query_ZYB HQ = new Http_Query_ZYB();
		/**
		 * 掌悦宝
		 * */
		// // 收银员姓名
		// String cashierName = "jhj01501";
		// // 终端号
		// String clientId = "API000000001";
		// // 商戶号
		// String merchantId = "369";

		// 接入方订单号
		outOrderId = CryptTool.getCurrentDate()
				+ clientId.substring(clientId.length() - 4, clientId.length());
		// 支付平台的支付码 扫描二维码
		this.paycode = paycode;
		// utf-8
		String inputCharset = "utf-8";
		// 加密
		String signType = "MD5";
		// 订单类型
		String merchantId = su.getPrefString(context, "merchantId", null);
		// 接受异步通知的URL地址
		String notifyUrl = "http://121.41.113.42:8080/PaymentCallback";
		// 签名
		String signature = null;

		StringBuilder sb = new StringBuilder();// 组装mac加密明文串
		// sb.append("cashierName=").append(cashierName);
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
		}// 进行md5加密(商户自己封装MD5加密工具类，此处只提供参考)
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
		Http_PushTask HP = new Http_PushTask();
		try {
			String rerurn = HP.execute(CryptTool.transMapToString(param),
					Url.transaction_zyb).get();
			JSONObject js = new JSONObject(rerurn);

			if (!TextUtils.isEmpty(rerurn)) {

				if (js.getBoolean("result")) {
					toast(context, "下单成功！");
					String outOrderId_cx = js.get("outOrderId").toString();
					// 掌优宝订单号
					String orderId = js.get("orderId").toString();
					SimpleDateFormat sDateFormat = new SimpleDateFormat(
							"yyyy/MM/dd hh:mm:ss");
					String Time_new = sDateFormat.format(new java.util.Date());
					HQ.startTimer(context, outOrderId, orderId,
							Integer.toString(totalFee), paycode, cashierName,
							merchantnum, PaymentMethod, PayDetails, Time_new,
							true);

				} else {

					toast(context, js.get("message").toString());
				}

			} else {

				toast(context, "交易失败");
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void toast(Context context, String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

}
