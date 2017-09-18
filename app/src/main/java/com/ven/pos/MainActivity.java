package com.ven.pos;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cnyssj.db.GoodsOrder;
import com.cnyssj.pos.CcguanliActivity;
import com.cnyssj.pos.R;
import com.cnyssj.pos.RechargeActivity;
import com.cnyssj.testOrder.OrdersActivity;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.jhj.Agreement.ZYB.Url;
import com.ven.pos.Payment.ConsumeListView;
import com.ven.pos.Payment.PaymentCalculatorActivity;
import com.ven.pos.Payment.PaymentCashierMainAct;
import com.ven.pos.Payment.ShopQuery;
import com.ven.pos.Util.BaseActivity;
import com.ven.pos.Util.UtilTool;
import com.ven.pos.qscanbar.PayCameraActivity;

/**
 * @author Administrator
 * 
 */
public class MainActivity extends BaseActivity implements OnClickListener {

	// public PageComplate onPageFinishedRet;

	private ImageButton btnQuckyPay; // 快速付款
	private ImageButton btnShopConsume; // 商品消费
	private ImageButton btnMemberList; // 会员列表
	private ImageButton btnMemberCharge; // 会员充值
	private ImageButton btnChargeQuery; // 充值查询
	private ImageButton btnPersonSetting; // 个人设置
	private ImageButton btnConstumQuery; // 消费查询
	private ImageButton btnChangeCheck; // 兑换验证
	private ImageButton btnScoreRechange; // 积分充值

	private ImageButton steele_account; // 核算
	private ImageButton revocation_order; // 订单撤销

	private TextView company_name_tv;
	private TextView services_name_tv;

	Url url = new Url();

	private static int count = 0;
	private long lastTime = System.currentTimeMillis();
	private GoodsOrder goodsOrder;// 方法工具类

	private ProgressBar progressBar;
	SharedPreferences_util su = new SharedPreferences_util();
	boolean enable_recharge;

	@SuppressWarnings("unused")
	private class LoginSuccessComplate implements PageComplate {
		public void OnComplate(String b) {
			if (b.contains("ms")) {
			}
		}
	}

	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);
		initView();
		setListener();
//		DownLoadFile DLF = new DownLoadFile(getApplicationContext());
//		DLF.Download(url.path, "e281ecefabfc71698b679ffc32d1a131", handler);
		// 商品加载
		ShopQuery.instance().init(this);
		ShopQuery.instance().QueryShop();
		UtilTool.checkLoginStatusAndReturnLoginActivity(this, null);
		goodsOrder = new GoodsOrder(getApplicationContext());
		// 用户登录时统计前一天各支付类型交易金额
		goodsOrder.getTime();
		enable_recharge = su.getPrefboolean(MainActivity.this,
				"enable_recharge", false);
		
//		Intent updata_error = new Intent(getApplicationContext(),
//				LongRunningService.class);
//		startService(updata_error);
	}

	@Override
	protected void onResume() {
		company_name_tv.setText(GlobalContant.instance().companyName);
		if (GlobalContant.instance().companyName.isEmpty()) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			finish();
		}
		UtilTool.checkLoginStatusAndReturnLoginActivity(this, null);
		super.onResume();
	}

	private void initView() {

		btnQuckyPay = (ImageButton) findViewById(R.id.quick_pay); // 快速付款
		btnShopConsume = (ImageButton) findViewById(R.id.shop_pay); // 商品消费
		btnMemberList = (ImageButton) findViewById(R.id.member_list); // 联盟商家
		btnMemberCharge = (ImageButton) findViewById(R.id.member_charge); // 会员充值
		btnChargeQuery = (ImageButton) findViewById(R.id.charge_query); // 友拉商城
		btnPersonSetting = (ImageButton) findViewById(R.id.person_setting); // 个人设置
		btnConstumQuery = (ImageButton) findViewById(R.id.custum_query); // 消费查询
		btnChangeCheck = (ImageButton) findViewById(R.id.change_check); // 积分兑换
		btnScoreRechange = (ImageButton) findViewById(R.id.score_rechange); // 仓储管理
		steele_account = (ImageButton) findViewById(R.id.steele_account);// 油卡充值
		revocation_order = (ImageButton) findViewById(R.id.revocation_order);// 话费充值
		company_name_tv = (TextView) findViewById(R.id.company_name);// 公司 名
		services_name_tv = (TextView) findViewById(R.id.services_name); // 操作人名

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
	}

	/**
	 * 用Handler来更新UI
	 */
	@SuppressLint("HandlerLeak")
	public Handler handler = new Handler() {

		@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 9:
				int updateCount = msg.getData().getInt("updateCount");
				if (updateCount == 100) {
					Drawable progressDrawable = getResources().getDrawable(
							R.drawable.pg_all);
					progressBar.setProgressDrawable(progressDrawable);
				} else {
					progressBar.setProgress(updateCount);
				}
			}
		}
	};

	private void setListener() {
		btnQuckyPay.setOnClickListener(this);
		btnShopConsume.setOnClickListener(this);
		btnMemberList.setOnClickListener(this);
		btnMemberCharge.setOnClickListener(this);
		btnChargeQuery.setOnClickListener(this);
		btnPersonSetting.setOnClickListener(this);
		btnConstumQuery.setOnClickListener(this);
		btnChangeCheck.setOnClickListener(this);
		btnScoreRechange.setOnClickListener(this);
		steele_account.setOnClickListener(this);
		revocation_order.setOnClickListener(this);
		company_name_tv.setText(GlobalContant.instance().companyName);
		services_name_tv.setText(GlobalContant.instance().userName);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {

		// ①　重写回调函数onKeyDown:响应用户点击按钮
		Toast.makeText(this, "再次点击返回到登陆页", Toast.LENGTH_SHORT).show();
		count++;
		long offTime = System.currentTimeMillis() - lastTime;
		lastTime = System.currentTimeMillis();
		if (offTime <= 2000 && count == 2) {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra(LoginActivity.INTAL_LOGIN_FIRST, false);
			finish();
			startActivity(intent);

		} else {
			count = 1;
		}
	}

	@Override
	protected void onDestroy() {
		// OpenIntegralWall.getInstance().onUnbind();
		super.onDestroy();
	}

	public void openUrlInWebViewActivity(String url) {
		Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
		// 用Bundle携带数据
		Bundle bundle = new Bundle();
		// 传递name参数为tinyphp
		bundle.putString("url", url);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.quick_pay: {
			Intent intent = new Intent(MainActivity.this,
					PaymentCalculatorActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
		}
 			break;
		case R.id.shop_pay: {

			if (null != ConsumeListView.instance().consumelistArray)
				ConsumeListView.instance().consumelistArray.clear();

			Intent intent = new Intent(MainActivity.this,
					PaymentCashierMainAct.class);
			// finish();
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
		}
			break;

		case R.id.member_charge: {
			Intent intent = new Intent(MainActivity.this,
					MemberInfoActivity.class);
			// finish();
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
		}
			break;
		case R.id.member_list:
			openWebView_(getResources().getString(R.string.member_manager),
					"http://vpos.cnyssj.net/allianceBusiness");
			break;
		case R.id.charge_query: // 充值记录
			openWebView(getResources().getString(R.string.recharge_record),
					getResources().getString(R.string.recharge_record_url));
			break;
		case R.id.person_setting:
			Intent intent = new Intent(MainActivity.this,
					PersonCenterActivity.class);
			// finish();
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
			break;
		// 消费查询
		case R.id.custum_query:
			Intent testIntent = new Intent(MainActivity.this,
					OrdersActivity.class);
			startActivity(testIntent);
			// openWebView(getResources().getString(R.string.pay_record),
			// getResources().getString(R.string.pay_record_url));
			break;
		case R.id.change_check:

			changeCheck();
			break;
		case R.id.score_rechange:
			// 仓储管理
//			Toast.makeText(MainActivity.this, "正在开发，敬请期待！", Toast.LENGTH_SHORT)
//			.show();
			Intent intent_cc = new Intent();
			intent_cc.setClass(MainActivity.this, CcguanliActivity.class);
			startActivity(intent_cc);
			break;
		case R.id.steele_account:

			Intent intent1 = new Intent("com.urovo.poscup.view.activity.menu");
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("TransType", "3");
			intent1.putExtra("map", (Serializable) map);
			startActivityForResult(intent1, 9);

			break;
		case R.id.revocation_order:
			
			if(enable_recharge){
				 Intent intent_cz = new Intent();
				 intent_cz.setClass(MainActivity.this, RechargeActivity.class);
				 startActivity(intent_cz);
			}else{
				Toast.makeText(MainActivity.this, "您尚未开通话费充值业务！", Toast.LENGTH_SHORT)
				.show();
			}
			
			// Intent intent2 = new
			// Intent("com.urovo.poscup.view.activity.menu");
			// Map<String, Object> map1 = new HashMap<String, Object>();
			// map1.put("TransType", "14");
			// intent2.putExtra("map", (Serializable) map1);
			// startActivityForResult(intent2, 9);
			break;
		}
	}

	private void changeCheck() {

		Intent openCameraIntent = new Intent(this, PayCameraActivity.class);
		openCameraIntent.putExtra(PayCameraActivity.IntentSCANTYPE,
				PayCameraActivity.CHANGE_CHECK);
		startActivityForResult(openCameraIntent, 0);
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

			if (nScanType == PayCameraActivity.CHANGE_CHECK) {

				try {
					String extraPost = "&money=0.0";
					String[] paramPP = customerNo.split("\\?");
					String params = paramPP[1];
					extraPost += "&" + params;
					extraPost += "&cancel=cancel";
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
					overridePendingTransition(android.R.anim.fade_in,
							android.R.anim.fade_out);
				} catch (Exception ex) {
					ex.printStackTrace();
					Toast.makeText(this, "兑换验证出错", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	private void openWebView(String name, String url) {
		Intent intent = new Intent(MainActivity.this, WebViewActivity.class);

		intent.putExtra("titleName", name);
		intent.putExtra("url", GlobalContant.instance().mainUrl + url);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	private void openWebView_(String name, String url) {
		Intent intent = new Intent(MainActivity.this, WebViewActivity.class);

		intent.putExtra("titleName", name);
		intent.putExtra("url", url);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}
}
