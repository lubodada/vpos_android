package com.ven.pos.Payment;

import java.io.ByteArrayInputStream;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnyssj.pos.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ven.pos.GlobalContant;
import com.ven.pos.ITitleBarLeftClick;
import com.ven.pos.TitleBar;
import com.ven.pos.WebViewActivity;
import com.ven.pos.Util.BaseActivity;
import com.ven.pos.qscanbar.PayCameraActivity;

public class PaymentCodeAct extends BaseActivity implements OnClickListener {
	static PaymentCodeAct paymentCodeAct;
	static int REFRESH = 1;
	private final int QR_WIDTH = 360;
	private final int QR_HEIGHT = 360;
	// private Button codeImg;
	private ImageView codeImg;
	private TextView pay_notice_money_tv;
	private LinearLayout member_layout;
	private LinearLayout member_pay_btn;
	private EditText member_account_txt;
	private EditText member_password_text;
	private int type; // 1:支付宝 2:微信
	private String barCodeRet;
	private String picUrl;
	private Bitmap bitmap;
	private String tradeNo;

	PayQueryBase payQuery;
	PayCollectionBase payCollection;

	public static final int TOAST = 1;

	public Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case GlobalContant.ShowErrorDialog: {
				String getResult1 = (String) msg.obj;

				paymentCodeAct.ShowMsgBox(getResult1, "错误", "确定", null);
				break;
			}
			case 200: {
				String getResult1 = (String) msg.obj;
				Toast.makeText(PaymentCodeAct.paymentCodeAct, getResult1,
						Toast.LENGTH_LONG).show();

				break;
			}
			}
		}
	};
	private String consumemobile;

	private class AliScanQCode implements ITitleBarLeftClick {
		public void click() {
			Intent openCameraIntent = new Intent(PaymentCodeAct.this,
					PayCameraActivity.class);
			openCameraIntent.putExtra(PayCameraActivity.IntentSCANTYPE,
					PayCameraActivity.AliQSCAN);
			startActivityForResult(openCameraIntent, 0);
		}
	}

	private class WxScanQCode implements ITitleBarLeftClick {
		public void click() {
			Intent openCameraIntent = new Intent(PaymentCodeAct.this,
					PayCameraActivity.class);
			openCameraIntent.putExtra(PayCameraActivity.IntentSCANTYPE,
					PayCameraActivity.WxQSCAN);
			startActivityForResult(openCameraIntent, 0);
		}
	}

	private class ConsumeScanQCode implements ITitleBarLeftClick {
		public void click() {
			Intent openCameraIntent = new Intent(PaymentCodeAct.this,
					PayCameraActivity.class);
			openCameraIntent.putExtra(PayCameraActivity.IntentSCANTYPE,
					PayCameraActivity.CONSUME_QSCAN);
			startActivityForResult(openCameraIntent, 0);
		}
	}

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		paymentCodeAct = this;
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,R.layout.titlebar);

		barCodeRet = getIntent().getStringExtra("BarCodeRet");
		type = getIntent().getIntExtra("Type", 0);
		if (type == PaymentMainActivity.ALIPAYWAY) {
			TitleBar.setTitleBar(this, "后退", "支付宝支付", "", new AliScanQCode());
			payQuery = new AliPayQuery();

		} else if (type == PaymentMainActivity.WEIXINPAYWAY) {
			TitleBar.setTitleBar(this, "后退", "微信支付", "扫一扫", new WxScanQCode());

			payQuery = new WxPayQuery();
		} else if (type == PaymentMainActivity.CONSUME_PAYWAY) {
			TitleBar.setTitleBar(this, "后退", "会员卡支付", "",
					new ConsumeScanQCode());
		}

		setContentView(R.layout.payment_code_act);

		initView();

		String totalMoney = getIntent().getStringExtra("totalMoney");
		pay_notice_money_tv.setText(totalMoney);

		tradeNo = getIntent().getStringExtra("tradeNo");

		if (type == PaymentMainActivity.ALIPAYWAY) {
			aliParse();

			payQuery.ModifyPayInfo(
					this,
					this.handler,
					tradeNo,
					totalMoney,
					getIntent().getExtras().getString(
							PaymentResultActivity.IntentPayDetails),
					getIntent().getExtras().getInt(
							PaymentResultActivity.IntentOperater),
					getIntent().getExtras().getString(
							PaymentResultActivity.IntentUid));

		} else if (type == PaymentMainActivity.WEIXINPAYWAY) {
			wxParse();

			payQuery.ModifyPayInfo(
					this,
					this.handler,
					tradeNo,
					totalMoney,
					getIntent().getExtras().getString(
							PaymentResultActivity.IntentPayDetails),
					getIntent().getExtras().getInt(
							PaymentResultActivity.IntentOperater),
					getIntent().getExtras().getString(
							PaymentResultActivity.IntentUid));

		} else if (type == PaymentMainActivity.CONSUME_PAYWAY) {
			consumemobile = getIntent().getExtras().getString("membermobile");
			member_layout.setVisibility(View.VISIBLE);
		}

	}

	public void onStart() {
		TitleBar.setActivity(this);
		super.onStart();
	}

	public void onBackPressed() {

		finish();
		if (type != PaymentMainActivity.CONSUME_PAYWAY) {
			payQuery.onDestory();
		}
		super.onBackPressed();
	}

	public void onDestory() {
		super.onDestroy();

		if (type != PaymentMainActivity.CONSUME_PAYWAY) {
			payQuery.onDestory();
		}

	}

	private void aliParse() {

		try {
			createQRImage(barCodeRet);
			// AliPayQuery.instance().init(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void aliPayResult(String result) {
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance(); // 取得DocumentBuilderFactory实例
			DocumentBuilder builder = factory.newDocumentBuilder(); // 从factory获取DocumentBuilder实例
			// Document doc = builder.parse(is); //解析输入流 得到Document实例

			ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(
					result.getBytes());
			org.w3c.dom.Document doc = builder.parse(tInputStringStream);
			Element rootElement = doc.getDocumentElement();
			NodeList items = rootElement.getChildNodes();
			for (int iItemPos = 0; iItemPos < items.getLength(); iItemPos++) {

				Node item = items.item(iItemPos);
				String nodeName = item.getNodeName();
				String nodeContent = item.getTextContent();
				if (nodeName.equals("is_success") && nodeContent.equals("T")) {

				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void wxParse() {
		createQRImage(barCodeRet);

		// 监听查询
		// WxPayQuery.instance().init(this);
		// WxPayQuery.instance().QueryPay(tradeNo,
		// pay_notice_money_tv.getText().toString(),
		// getIntent().getExtras().getString(
		// PaymentResultActivity.IntentPayDetails),
		// getIntent().getExtras().getInt(
		// PaymentResultActivity.IntentOperater) );
	}

	private void initView() {
		codeImg = (ImageView) findViewById(R.id.code_img);
		pay_notice_money_tv = (TextView) findViewById(R.id.pay_notice_money_tv);
		member_layout = (LinearLayout) findViewById(R.id.member_layout);

		member_pay_btn = (LinearLayout) findViewById(R.id.member_pay_btn);
		member_account_txt = (EditText) findViewById(R.id.member_layout_account);
		member_password_text = (EditText) findViewById(R.id.member_layout_password);

		member_pay_btn.setOnClickListener(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// requestCode标示请求的标示 resultCode表示有数据
		if (resultCode == RESULT_OK) {
			int nScanType = data.getExtras().getInt(
					PayCameraActivity.IntentSCANTYPE);
			String customerNo = data.getExtras().getString(
					PayCameraActivity.RetCustomerNo);

			if (nScanType == PayCameraActivity.WxQSCAN) {

				String strMoney = pay_notice_money_tv.getText().toString();
				int nMoney = (int) (Double.valueOf(strMoney) * 100);

				payCollection = new WxPayCollection(this, this.payQuery);
				payCollection.collectionMoney(
						strMoney,
						customerNo,
						getIntent().getExtras().getString(
								PaymentResultActivity.IntentPayDetails),
						getIntent().getExtras().getInt(
								PaymentResultActivity.IntentOperater));
			} else if (nScanType == PayCameraActivity.AliQSCAN) {
				String strMoney = pay_notice_money_tv.getText().toString();

				payCollection = new AliPayCollection(this, this.payQuery);

				payCollection.collectionMoney(
						strMoney,
						customerNo,
						getIntent().getExtras().getString(
								PaymentResultActivity.IntentPayDetails),
						getIntent().getExtras().getInt(
								PaymentResultActivity.IntentOperater));
			} else if (nScanType == PayCameraActivity.CONSUME_QSCAN) {
				// String strMoney = pay_notice_money_tv.getText().toString();
				// AliPayCollection.instance().collectionMoney(strMoney,
				// customerNo);
				try {
					String extraPost = "&money="
							+ pay_notice_money_tv.getText().toString();

					String[] paramPP = customerNo.split("\\?");

					String params = paramPP[1];

					extraPost += "&" + params;
					// String[] paramArray = params.split("&");
					// for(int i =0; i<paramArray.length; i++){
					// String[] subParam = paramArray[i].split("=");
					// if(subParam[0].equals("i") || subParam[0].equals("mid")
					// ){
					// extraPost += "&" + paramArray[i] ;
					// }
					// }
					if (null != getIntent().getExtras().getString(
							PaymentResultActivity.IntentPayDetails)) {
						extraPost += "&items="
								+ getIntent().getExtras().getString(
										PaymentResultActivity.IntentPayDetails);
					}

					Intent intent = new Intent(this, WebViewActivity.class);

					// 用Bundle携带数据
					Bundle bundle = new Bundle();
					// 传递name参数为tinyphp
					String url = GlobalContant.instance().mainUrl
							+ this.getResources().getString(
									R.string.consume_pay_url);

					bundle.putString("url", url);
					bundle.putString("extrapost", extraPost);

					intent.putExtras(bundle);

					startActivity(intent);
				} catch (Exception ex) {
					ex.printStackTrace();
					Toast.makeText(this, "会员支付扫描二维码出错", Toast.LENGTH_LONG)
							.show();
				}
			}
		}
		// Toast.makeText(
		// this,
		// "requestCode=" + requestCode + ":" + "resultCode=" + resultCode,
		// Toast.LENGTH_LONG).show();
	}

	public void createQRImage(String url) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			// 显示到一个ImageView上面
			// sweepIV.setImageBitmap(bitmap);
			paymentCodeAct.codeImg.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	protected void ShowMsgBox(String paramString1) {
		// ShowMsgBox(paramString1, "提示", "确定", null);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == member_pay_btn.getId()) {

			// String phone = member_account_txt.getText().toString().trim();
			String phone = consumemobile;
			if ("".equals(phone)) {
				Toast.makeText(this, "手机号不能为空!", Toast.LENGTH_LONG).show();
				return;
			}

			if (phone.length() != 11) {
				Toast.makeText(this, "请输入正确的手机号!", Toast.LENGTH_LONG).show();
				return;
			}

			String pwd = member_password_text.getText().toString().trim();
			if ("".equals(pwd)) {
				Toast.makeText(this, "密码长度不能为空!", Toast.LENGTH_LONG).show();
				return;
			}
			if (pwd.length() < 6) {
				Toast.makeText(this, "密码长度不能少于6位!", Toast.LENGTH_LONG).show();
				return;
			}

			String extraPost = new String();

			extraPost += ("&money=" + pay_notice_money_tv.getText().toString());
			// extraPost += ("&mobile=" +
			// member_account_txt.getText().toString() );
			extraPost += ("&mobile=" + consumemobile);
			extraPost += ("&password=" + member_password_text.getText()
					.toString());

			if (null != getIntent().getExtras().getString(
					PaymentResultActivity.IntentPayDetails)) {
				extraPost += "&items="
						+ getIntent().getExtras().getString(
								PaymentResultActivity.IntentPayDetails);
			}

			Intent intent = new Intent(this, WebViewActivity.class);

			// 用Bundle携带数据
			Bundle bundle = new Bundle();
			// 传递name参数为tinyphp
			String url = GlobalContant.instance().mainUrl
					+ this.getResources().getString(R.string.consume_pay_url);

			bundle.putString("url", url);
			bundle.putString("extrapost", extraPost);

			intent.putExtras(bundle);

			startActivity(intent);
		}
	}
}
