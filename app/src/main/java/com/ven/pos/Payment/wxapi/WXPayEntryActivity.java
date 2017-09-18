package com.ven.pos.Payment.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cnyssj.pos.R;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.ven.pos.GlobalContant;
import com.ven.pos.Util.BaseActivity;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_result);

		api = WXAPIFactory.createWXAPI(this,
				GlobalContant.instance().wxConfig.APP_ID);

		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			// AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// builder.setTitle(R.string.app_tip);
			// builder.setMessage(getString(R.string.pay_result_callback_msg,
			// resp.errStr +";code=" + String.valueOf(resp.errCode)));
			// builder.show();
		}
	}
}