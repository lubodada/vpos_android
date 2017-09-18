package com.jhj.print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Button;
import android.widget.Toast;

import com.basewin.aidl.OnPrinterListener;
import com.basewin.define.FontsType;
import com.basewin.services.ServiceManager;
import com.cnyssj.pos.R;
import com.jhj.Agreement.ZYB.SharedPreferences_util;

public class Print {

	SharedPreferences_util su = new SharedPreferences_util();

	Context context;
	String orderId;
	String monery;
	String paycode;
	String PaymentMethod;
	String clientId;
	String Time;
	Dialog print_dia;
	Button Pring_qd, Pring_again;
	String cashiername;

	@SuppressWarnings("static-access")
	@SuppressLint("SimpleDateFormat")
	/**
	 * @MerchantName 商户名称
	 * @outOrderId 订单号  
	 * @monery 金额
	 * @paycode 付款码
	 * @PaymentMethod 付款方式
	 * @TerminalNumber 终端号
	 * @Time 时间
	 * */
	public void prilay(Context context, String orderId, String monery,
			String paycode, String PaymentMethod, String cashierName,
			String clientId, String Time) {

		this.context = context;
		this.orderId = orderId;
		this.monery = monery;
		this.paycode = paycode;
		this.PaymentMethod = PaymentMethod;
		this.clientId = clientId;
		this.Time = Time;
		this.cashiername = cashierName;

		try {
			JSONObject bodyjson = new JSONObject();
			bodyjson.put("orderId", orderId);
			bodyjson.put("monery", monery);
			bodyjson.put("paycode", paycode);
			bodyjson.put("PaymentMethod", PaymentMethod);
			bodyjson.put("clientId", clientId);
			bodyjson.put("Time", Time);
			bodyjson.put("shanghm", cashiername);

			// 保存数据
			su.setPrefString(context, "print", bodyjson.toString());

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// 弹出框

		new_print(context, orderId, cashiername, monery, paycode,
				PaymentMethod, clientId, Time);

	}

	public void new_print(Context context, String orderId, String cashiername,
			String monery, String paycode, String PaymentMethod,
			String merchantnum, String Time) {
		JSONObject printJson = new JSONObject();
		if (su.getPrefboolean(context, "on_off", false)) {
			PrinterListener printer_callback = new PrinterListener();
			try {
				ServiceManager.getInstence().getPrinter().setPrintGray(1000);
				ServiceManager.getInstence().getPrinter()
						.setPrintFont(FontsType.simsun);
				try {
					// 组打印json字符串
					JSONArray printTest = new JSONArray();
					// 添加文本打印,正常

					JSONObject json1 = new JSONObject();
					json1.put("content-type", "txt");
					json1.put("content", "POS签购单");
					json1.put("size", "50");
					json1.put("position", "center");
					json1.put("offset", "0");
					json1.put("bold", "0");
					json1.put("italic", "0");
					json1.put("height", "0");

					JSONObject json_free = new JSONObject();
					json_free.put("content-type", "txt");
					json_free.put("content", "    ");
					json_free.put("size", "3");
					json_free.put("position", "left");
					json_free.put("offset", "0");
					json_free.put("bold", "1");
					json_free.put("italic", "0");
					json_free.put("height", "-1");

					// 订单流水
					JSONObject json3 = new JSONObject();
					json3.put("content-type", "txt");
					json3.put("content", "订单流水：" + orderId);
					json3.put("size", "2");
					json3.put("position", "left");
					json3.put("offset", "10");
					json3.put("bold", "0");
					json3.put("italic", "0");
					json3.put("height", "10");
					// 商户名称
					JSONObject json4 = new JSONObject();
					json4.put("content-type", "txt");
					json4.put("content", "商户名称：" + cashiername);
					json4.put("size", "2");
					json4.put("position", "left");
					json4.put("offset", "30");
					json4.put("bold", "0");
					json4.put("italic", "0");
					json4.put("height", "10");
					// 付款方式
					JSONObject json5 = new JSONObject();
					json5.put("content-type", "txt");
					json5.put("content", "付款方式：" + PaymentMethod);
					json5.put("size", "2");
					json5.put("position", "left");
					json5.put("offset", "0");
					json5.put("bold", "0");
					json5.put("italic", "0");
					json5.put("height", "0");
					// 付款码
					JSONObject json_num = new JSONObject();
					json_num.put("content-type", "txt");
					json_num.put("content", "付款码：" + paycode);
					json_num.put("size", "2");
					json_num.put("position", "left");
					json_num.put("offset", "0");
					json_num.put("bold", "0");
					json_num.put("italic", "0");
					json_num.put("height", "0");

					// 金额
					JSONObject json6 = new JSONObject();
					json6.put("content-type", "txt");
					json6.put("content", " RMB：" + monery);
					json6.put("size", "40");
					json6.put("position", "left");
					json6.put("offset", "0");
					json6.put("bold", "0");
					json6.put("italic", "1");
					json6.put("height", "0");
					// 终端号
					JSONObject json7 = new JSONObject();
					json7.put("content-type", "txt");
					json7.put("content", "终端号：" + merchantnum);
					json7.put("size", "2");
					json7.put("position", "left");
					json7.put("offset", "0");
					json7.put("bold", "0");
					json7.put("italic", "0");
					json7.put("height", "0");
					// 时间

					JSONObject json8 = new JSONObject();
					json8.put("content-type", "txt");
					json8.put("content", "交易日期：" + Time);
					json8.put("size", "2");
					json8.put("position", "left");
					json8.put("offset", "0");
					json8.put("bold", "0");
					json8.put("italic", "0");
					json8.put("height", "0");

					// JSONObject json9 = new JSONObject();
					// json9.put("content-type", "txt");
					// json9.put(
					// "content",
					// "  有效期："
					// + Time.substring(0, 8)
					// + Integer.toString(Integer.parseInt(Time
					// .substring(8, 10)) + 1) + " 00:00:00");
					// json9.put("size", "2");
					// json9.put("position", "left");
					// json9.put("offset", "0");
					// json9.put("bold", "0");
					// json9.put("italic", "0");
					// json9.put("height", "0");
					// 添加图片
					JSONObject json11 = new JSONObject();
					json11.put("content-type", "jpg");
					json11.put("position", "center");
					// 添加二维码打印
					// JSONObject json10 = new JSONObject();
					// json10.put("content-type", "two-dimension");
					// json10.put("content", "18217488102");
					// json10.put("size", "3");
					// json10.put("position", "center");

					// 添加一维码打印
					JSONObject json12 = new JSONObject();
					json12.put("content-type", "one-dimension");
					json12.put("content", orderId);
					json12.put("size", "3");
					json12.put("position", "center");
					json12.put("height", "2");
					// 签名确认
					JSONObject json = new JSONObject();
					json.put("content-type", "txt");
					json.put("content", "签名确认：");
					json.put("size", "2");
					json.put("position", "left");
					json.put("offset", "0");
					json.put("bold", "0");
					json.put("italic", "0");
					json.put("height", "0");
					// 技术支持
					JSONObject json_TN = new JSONObject();
					json_TN.put("content-type", "txt");
					json_TN.put("content", " 技术支持：山东银商数据");
					json_TN.put("size", "1");
					json_TN.put("position", "right");
					json_TN.put("offset", "0");
					json_TN.put("bold", "0");
					json_TN.put("italic", "0");
					json_TN.put("height", "0");
					// 电话
					JSONObject json_Phone = new JSONObject();
					json_Phone.put("content-type", "txt");
					json_Phone.put("content", "电话：4009-222-166");
					json_Phone.put("size", "1");
					json_Phone.put("position", "right");
					json_Phone.put("offset", "0");
					json_Phone.put("bold", "0");
					json_Phone.put("italic", "0");
					json_Phone.put("height", "0");

					JSONObject json_free_little = new JSONObject();
					json_free_little.put("content-type", "txt");
					json_free_little.put("content", "    ");
					json_free_little.put("size", "10");
					json_free_little.put("position", "left");
					json_free_little.put("offset", "0");
					json_free_little.put("bold", "1");
					json_free_little.put("italic", "0");
					json_free_little.put("height", "-1");

					JSONObject json_free_sm = new JSONObject();
					json_free_sm.put("content-type", "txt");
					json_free_sm.put("content", "    ");
					json_free_sm.put("size", "9");
					json_free_sm.put("position", "left");
					json_free_sm.put("offset", "0");
					json_free_sm.put("bold", "1");
					json_free_sm.put("italic", "0");
					json_free_sm.put("height", "-1");

					printTest.put(json1);
					printTest.put(json_free);
					printTest.put(json3);
					printTest.put(json4);
					printTest.put(json5);
					printTest.put(json_num);
					printTest.put(json6);
					printTest.put(json7);
					printTest.put(json8);
					// printTest.put(json9);
					printTest.put(json_free_little);
					printTest.put(json);
					// 空行
					printTest.put(json_free);
					printTest.put(json_free);
					printTest.put(json_free);
					printTest.put(json12);
					printTest.put(json_free_sm);
					printTest.put(json_TN);
					printTest.put(json_Phone);

					printJson.put("spos", printTest);
					// 设置底部空3行
					ServiceManager.getInstence().getPrinter()
							.printBottomFeedLine(6);
					Bitmap[] bitmaps = new Bitmap[] { BitmapFactory
							.decodeResource(context.getResources(),
									R.drawable.ic_launcher) };
					ServiceManager
							.getInstence()
							.getPrinter()
							.print(printJson.toString(), bitmaps,
									printer_callback);

				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			toast("未开启打印机");
		}
	}

	class PrinterListener implements OnPrinterListener {

		public void onStart() {
			//toast("打印开始");
		}

		public void onFinish() {
			//toast("打印结束");
		}

		public void onError(int errorCode, String detail) {
			//toast("打印出错    " + detail);
		}
	};

	public void toast(String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

	public void dia_cancel() {
		print_dia.cancel();
	}
}
