package com.jhj.print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.basewin.aidl.OnPrinterListener;
import com.basewin.define.FontsType;
import com.basewin.services.ServiceManager;
import com.cnyssj.pos.R;
import com.jhj.Agreement.ZYB.SharedPreferences_util;

public class Print_goods {

	SharedPreferences_util su = new SharedPreferences_util();

	Context context;
	String data;

	@SuppressWarnings("static-access")
	public void prilay(Context context, String data) {

		this.context = context;
		this.data = data;

		try {
			JSONObject bodyjson = new JSONObject();
			bodyjson.put("orderId", data);

			// 保存数据
			su.setPrefString(context, "print_goods", bodyjson.toString());

		} catch (JSONException e) {
			e.printStackTrace();
		}
		// 弹出框
		new_print(context, data);

	}

	@SuppressWarnings("static-access")
	public void new_print(Context context, String data) {
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
					json1.put("content", "POS购物清单");
					json1.put("size", "50");
					json1.put("position", "center");
					json1.put("offset", "0");
					json1.put("bold", "0");
					json1.put("italic", "0");
					json1.put("height", "0");

					JSONObject json2 = new JSONObject();
					json2.put("content-type", "txt");
					// 4+10+2+4+2+1
					json2.put("content", "商品名称            数量  价格 ");
					json2.put("size", "2");
					json2.put("position", "left");
					json2.put("offset", "10");
					json2.put("bold", "0");
					json2.put("italic", "0");
					json2.put("height", "10");

					// 订单流水
					JSONObject json3 = new JSONObject();
					json3.put("content-type", "txt");
					json3.put("content", data);
					json3.put("size", "2");
					json3.put("position", "left");
					json3.put("offset", "10");
					json3.put("bold", "0");
					json3.put("italic", "0");
					json3.put("height", "10");

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

					JSONObject json_free = new JSONObject();
					json_free.put("content-type", "txt");
					json_free.put("content", "    ");
					json_free.put("size", "2");
					json_free.put("position", "left");
					json_free.put("offset", "0");
					json_free.put("bold", "1");
					json_free.put("italic", "0");
					json_free.put("height", "-1");

					JSONObject json_free_sm = new JSONObject();
					json_free_sm.put("content-type", "txt");
					json_free_sm
							.put("content",
									"----------------------------------------------------------------------------");
					json_free_sm.put("size", "9");
					json_free_sm.put("position", "left");
					json_free_sm.put("offset", "0");
					json_free_sm.put("bold", "1");
					json_free_sm.put("italic", "0");
					json_free_sm.put("height", "0");

					printTest.put(json1);
					printTest.put(json_free);
					printTest.put(json2);
					printTest.put(json_free_sm);
					printTest.put(json3);
					printTest.put(json_free);
					printTest.put(json_TN);
					printTest.put(json_Phone);

					printJson.put("spos", printTest);
					// 设置底部空3行
					ServiceManager.getInstence().getPrinter()
							.printBottomFeedLine(0);
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
			// TODO 打印开始
			// toast("打印开始");
		}

		public void onFinish() {
			// TODO 打印结束
			// toast("打印结束");
		}

		public void onError(int errorCode, String detail) {
			// TODO 打印出错
			// toast("打印出错    " + detail);
		}
	};

	public void toast(String str) {
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

}
