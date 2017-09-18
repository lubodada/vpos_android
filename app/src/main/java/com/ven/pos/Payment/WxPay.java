package com.ven.pos.Payment;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sourceforge.simcpux.MD5;
import net.sourceforge.simcpux.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

import com.cnyssj.pos.R;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ven.pos.GlobalContant;
import com.ven.pos.alipay.util.UtilDate;

public class WxPay {

	private static final String TAG = "WxPay";
	public static WxPay inst;

	static WxPay instance() {
		if (null == inst) {
			inst = new WxPay();
		}
		return inst;
	}

	private Context parentContext;
	PayReq req;
	IWXAPI msgApi;
	TextView show;
	Map<String, String> resultunifiedorder;
	StringBuffer sb;
	String nTotalFee;
	String outTradNo;

	public void init(Context c) {

		if (null == msgApi) {
			parentContext = c;
			msgApi = WXAPIFactory.createWXAPI(c, null);
			msgApi.registerApp(GlobalContant.instance().wxConfig.APP_ID);
			req = new PayReq();
			sb = new StringBuffer();
		}
		String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();

	}

	/**
	 * 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(GlobalContant.instance().wxConfig.API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();
		Log.e("orion", packageSign);
		return packageSign;
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(GlobalContant.instance().wxConfig.API_KEY);

		this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();
		Log.e("orion", appSign);
		return appSign;
	}

	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("orion", sb.toString());
		return sb.toString();
	}

	private class GetPrepayIdTask extends
			AsyncTask<Void, Void, Map<String, String>> {

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			// dialog = ProgressDialog.show(msgApi, getString(R.string.app_tip),
			// getString(R.string.getting_prepayid));
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
			show.setText(sb.toString());

			resultunifiedorder = result;

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {

			String url = String
					.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();

			Log.e("orion", entity);

			byte[] buf = Util.httpPost(url, entity);

			String content = new String(buf);
			Log.e("orion", content);
			Map<String, String> xml = decodeXml(content);

			if (xml.get("result_code").equals("SUCCESS")
					&& xml.get("return_code").equals("SUCCESS")) {
				Intent localIntent = new Intent(parentContext,
						PaymentCodeAct.class);
				localIntent.putExtra("BarCodeRet", "");

				int totalFee = Integer.parseInt(nTotalFee);
				float dTotalFee = totalFee / 100.0f;

				localIntent.putExtra("totalMoney", String.valueOf(dTotalFee));
				localIntent.putExtra("Type", 2);
				localIntent.putExtra("tradeNo", outTradNo);
				parentContext.startActivity(localIntent);
			}
			return xml;
		}
	}

	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						// 实例化student对象
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion", e.toString());
		}
		return null;

	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private String genOutTradNo() {
		// Random random = new Random();

		return MD5.getMessageDigest(UtilDate.getOrderNumUNIX().getBytes());
	}

	//
	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();
			String bodyStr = parentContext.getResources().getString(
					R.string.pay_explain);
			outTradNo = genOutTradNo();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", GlobalContant
					.instance().wxConfig.APP_ID));
			packageParams.add(new BasicNameValuePair("body", bodyStr));
			packageParams.add(new BasicNameValuePair("mch_id", GlobalContant
					.instance().wxConfig.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url",
					GlobalContant.instance().wxConfig.NOTIFY_URL));
			packageParams
					.add(new BasicNameValuePair("out_trade_no", outTradNo));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",
					"127.0.0.1"));
			packageParams.add(new BasicNameValuePair("total_fee", nTotalFee));
			packageParams.add(new BasicNameValuePair("trade_type", "NATIVE"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);

			xmlstring = new String(xmlstring.getBytes("UTF-8"), "ISO-8859-1");
			return xmlstring;

		} catch (Exception e) {
			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
			return null;
		}

	}

	public void genPayReq() {

		req.appId = GlobalContant.instance().wxConfig.APP_ID;
		req.partnerId = GlobalContant.instance().wxConfig.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n" + req.sign + "\n\n");

		show.setText(sb.toString());

		Log.e("orion", signParams.toString());

	}

	/**
	 * @totalFee 钱数
	 * @payDetails 支付详细信息
	 * @oper 操作类型 1:消费 2:充值
	 * @memberuid 用户ID
	 * */
	public void reqPay(String totalFee, String payDetails, int oper,
			String memberuid) {

		nTotalFee = totalFee;

		// GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
		// getPrepayId.execute();

		String url = GlobalContant.instance().wxConfig.WX_PAY_URL;
		String entity = genProductArgs();

		Log.e("orion", entity);

		byte[] buf = Util.httpPost(url, entity);

		String content = new String(buf);
		Log.e("orion", content);
		Map<String, String> xml = decodeXml(content);

		PaymentMainActivity.PaymentMainActivity.DismissProDlg();

		if (null != xml.get("result_code")
				&& xml.get("result_code").equals("SUCCESS")
				&& null != xml.get("return_code")
				&& xml.get("return_code").equals("SUCCESS")) {
			// 跳转到付款完成页面
			Intent localIntent = new Intent(parentContext, PaymentCodeAct.class);
			localIntent.putExtra("BarCodeRet", xml.get("code_url"));

			int ttTotalFee = Integer.parseInt(nTotalFee);
			float dTotalFee = ttTotalFee / 100.0f;

			localIntent.putExtra("totalMoney", String.valueOf(dTotalFee));
			localIntent.putExtra("Type", PaymentMainActivity.WEIXINPAYWAY);
			localIntent.putExtra("tradeNo", outTradNo);
			localIntent.putExtra("nonceStr", xml.get("nonce_str"));
			localIntent.putExtra(PaymentResultActivity.IntentPayDetails,
					payDetails);
			localIntent.putExtra(PaymentResultActivity.IntentOperater, oper);
			if (!memberuid.equals("") && memberuid != null) {
				localIntent
						.putExtra(PaymentResultActivity.IntentUid, memberuid);
			}

			parentContext.startActivity(localIntent);
		} else {
			if (null != xml.get("return_msg"))
				PaymentMainActivity.PaymentMainActivity.ShowMsgBox(xml
						.get("return_msg"));
		}
	}

	public void sendPayReq() {
		msgApi.registerApp(GlobalContant.instance().wxConfig.APP_ID);
		msgApi.sendReq(req);
	}

}
