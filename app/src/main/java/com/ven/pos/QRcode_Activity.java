package com.ven.pos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cnyssj.pos.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jhj.Agreement.ZYB.CryptTool;
import com.jhj.Agreement.ZYB.Http_PushTask;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.jhj.Agreement.ZYB.Url;
import com.ven.pos.Payment.PaymentResultActivity;
import com.ven.pos.Util.Time;

public class QRcode_Activity extends Activity {
	SharedPreferences_util su = new SharedPreferences_util();
	int orderType;
	TextView qr_code_tx, qr_code_zt;
	ImageView qr_code_iv;
	private int QR_WIDTH = 300;
	private int QR_HEIGHT = 300;
	Time dt = new Time();
	String merchantId, keyString, order, outOrderId, clientId, totalMoney;
	String qr_code;
	String qr_code_monery;
	// 商品列表
	String PayDetails;

	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode_);

		merchantId = su.getPrefString(QRcode_Activity.this, "merchantId", null);
		keyString = su.getPrefString(QRcode_Activity.this, "key", null);
		clientId = su.getPrefString(QRcode_Activity.this, "clientId", null);

		PayDetails = getIntent().getExtras().getString("PayDetails");
		orderType = getIntent().getExtras().getInt("orderType");
		outOrderId=getIntent().getExtras().getString("outOrderId");
		//支付金额
		qr_code_monery = getIntent().getExtras().getString("TotalMoney");
		String monery = Double
				.toString((Double.parseDouble(qr_code_monery) * 100));
		//支付金额
		totalMoney = monery.substring(0, monery.length() - 2);
		qr_code_tx = (TextView) findViewById(R.id.qr_code_tx);
		qr_code_tx.setText("请使用" + Paymode(orderType) + "扫描下面二维码");
		
		qr_code_iv = (ImageView) findViewById(R.id.qr_code_iv);
		qr_code_zt = (TextView) findViewById(R.id.qr_code_zt);
		(new Pay()).start();
	}

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 1:
				qr_code_zt.setText("待付款");
				break;
			case 3:
				break;
			case 4:
				qr_code_zt.setText("已取消");
				break;
			case 6:
				toast("生成二维码");
				final Bitmap scanbitmap = createQRImage(qr_code);
				qr_code_iv.setImageBitmap(GetRoundedCornerBitmap(scanbitmap));
				(new QueryThread()).start();
				break;
			case 7:
				SimpleDateFormat sDateFormat = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				String Time_new = sDateFormat.format(new java.util.Date());
				qr_code_zt.setText("支付成功");
				Intent intent = new Intent();
				intent.setClass(QRcode_Activity.this,
						PaymentResultActivity.class);
				intent.putExtra("Payment_switch", false);
				intent.putExtra("Time_new", Time_new);
				intent.putExtra("PaymentMethod", paymode_change(orderType));
				// 商品列表
				intent.putExtra("PayDetails", PayDetails);
				intent.putExtra("outOrderId", outOrderId);
				intent.putExtra("order", order);
				// 总金额
				intent.putExtra("str_monery", totalMoney);
				startActivity(intent);
				finish();
				break;
			case 8:
				toast("获取信息超时！");
				break;
			}
		} 
	};

	public void showmessage(int message) {
		Message localMessage = new Message();
		localMessage.what = message;
		this.handler.sendMessage(localMessage);
	}

	class Pay extends Thread {
		public void run() {
			
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
			sb.append("&signType=").append(signType);
			sb.append("&totalFee=").append(totalMoney);
			sb.append("").append(keyString);
			try {
				signature = CryptTool.md5Digest(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			// 进行md5加密(商户自己封装MD5加密工具类，此处只提供参考)
			Map<String, String> param = new HashMap<String, String>();// 组装请求参数
			param.put("clientId", clientId);
			param.put("inputCharset", inputCharset);
			param.put("merchantId", merchantId);
			param.put("notifyUrl", notifyUrl);
			param.put("orderType", Integer.toString(orderType));
			param.put("outOrderId", outOrderId);
			param.put("signType", signType);
			param.put("totalFee", totalMoney);
			param.put("signature", signature);
			// 发送
			new Http_PushTask_false().execute(
					CryptTool.transMapToString(param), Url.QR_code_pay);
		}
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
				if (result != null) {
					js = new JSONObject(result);
					if (!TextUtils.isEmpty(result)) {

						if (js.getBoolean("result")) {

							order = js.get("orderId").toString();
							qr_code = js.getJSONObject("data").get("qrCode")
									.toString();
							// 进行查询
							showmessage(6);
						} else {
							// 创建订单失败
							toast(js.get("message").toString());
						}
					} else {
						toast("交易失败");
					}
				} else {
					showmessage(3);
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

	/**
	 * 查询
	 * */
	class QueryThread extends Thread {
		int start, end;

		public void run() {
			start = dt.getTime();
			int i = 0;
			while (i != 7) {
				if (end - start > 60) {
					showmessage(8);
					break;
				}
				try {
					Thread.sleep(100);
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
				sb.append("&orderId=").append(order);
				sb.append("&outOrderId=").append(outOrderId);
				sb.append("&signType=").append(signType);
				sb.append("").append(keyString);
				try {
					signature = CryptTool.md5Digest(sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}// 进行md5加密(商户自己封装MD5加密工具类，此处只提供
					// 参考)
				Map<String, String> param = new HashMap<String, String>();//
				// 组装请求参数，参数名大小写敏感
				param.put("inputCharset", inputCharset);
				param.put("merchantId", merchantId);
				param.put("orderId", order);
				param.put("outOrderId", outOrderId);
				param.put("signature", signature);
				param.put("signType", signType);
				Http_PushTask HP = new Http_PushTask();

				try {
					String result = HP.execute(
							CryptTool.transMapToString(param),
							Url.QR_code_query)
							.get(60000, TimeUnit.MILLISECONDS);
					JSONObject js = new JSONObject(result);

					if (js.getBoolean("result")) {
						if (js.has("message")) {
							// toast(context, js.get("message").toString());
							if (js.has("orderState")) {
								i = js.getInt("orderState");
								// 交易状态
								showmessage(js.getInt("orderState"));
								i = js.getInt("orderState");
							}
						}
					} else {
						toast(js.get("message").toString());
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

	// 生成二维码图片
	public Bitmap createQRImage(String url) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];

			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void toast(String str) {
		Toast.makeText(QRcode_Activity.this, str, Toast.LENGTH_SHORT).show();
	}

	public String Paymode(int modenum) {
		if (modenum == 1) {
			return "支付宝客户端";
		} else if (modenum == 4) {
			return "微信客户端";
		} else if (modenum == 13) {
			return "翼支付手机客户端";
		}
		return null;
	}

	public int paymode_change(int r) {
		if (r == 1) {
			return 3;
		} else if (r == 4) {
			return 4;
		} else if (r == 13) {
			return 6;
		}
		return 0;
	}

	// 生成圆角图片
	public static Bitmap GetRoundedCornerBitmap(Bitmap bitmap) {
		try {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());
			final RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight()));
			final float roundPx = 15;
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(Color.BLACK);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

			final Rect src = new Rect(0, 0, bitmap.getWidth(),
					bitmap.getHeight());

			canvas.drawBitmap(bitmap, src, rect, paint);
			return output;
		} catch (Exception e) {
			return bitmap;
		}
	}
}
