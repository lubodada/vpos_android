package com.cnyssj.pos;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.jhj.Agreement.ZYB.CryptTool;
import com.ven.pos.GlobalContant;
import com.ven.pos.TitleBar;
import com.ven.pos.Payment.ShopItem;
import com.ven.pos.Payment.ShopItemMgr;
import com.ven.pos.Util.Time;

/**
 * 盘点
 */
public class InventoryActivity extends Activity {
	Button but_invent_updata, but_invent_exit;
	EditText edtext_qc, edtext_num;
	TextView goods_name;
	Time time = new Time();
	String itemNo;
	private String Updata_url = "http://vpos.cnyssj.net/api/countInventory";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		TitleBar.setTitleBar(InventoryActivity.this, "", "盘    点", "", null);
		setContentView(R.layout.activity_inventory);
		// 数据库操作类实例化

		goods_name = (TextView) findViewById(R.id.goods_name);
		edtext_qc = (EditText) findViewById(R.id.storage_edtext_qc);
		edtext_num = (EditText) findViewById(R.id.storage_edtext_num);
		edtext_num.addTextChangedListener(textWatcher);
		edtext_num
				.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							shopnum();
						} else {

						}
					}
				});
		but_invent_exit = (Button) findViewById(R.id.but_invent_exit);
		but_invent_updata = (Button) findViewById(R.id.but_invent_updata);
		but_invent_updata.setOnClickListener(myonclicklistener);
		but_invent_exit.setOnClickListener(myonclicklistener);

		edtext_qc.setInputType(InputType.TYPE_NULL);
		edtext_qc.requestFocus();
		// 商品编码输入框
		edtext_qc.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					InputMethodManager imm = (InputMethodManager) InventoryActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					shopnum();
					return true;

				}
				return false;
			}
		});
		edtext_qc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		edtext_qc.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {

				}
				return true;
			}
		});

	}

	private TextWatcher textWatcher = new TextWatcher() {

		String beforeText = null;

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String text = edtext_num.getText().toString();
			int firstDotPos = text.indexOf(".");
			int secondDotPos = text.indexOf(".", firstDotPos + 1);
			if (firstDotPos >= 0 && secondDotPos > firstDotPos) {
				if (beforeText != null) {
					edtext_num.setText(beforeText);
					edtext_num.setSelection(start);
				}
			}
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			beforeText = s.toString();
		}

		public void afterTextChanged(Editable s) {

		}
	};
	private View.OnClickListener myonclicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.but_invent_updata:
				// 提交
				// 盘点状态
				if (edtext_num.getText().toString() != null
						|| edtext_qc.getText().toString() != null) {

					// 上传服务器
					Map<String, String> param = new HashMap<String, String>();
					param.put("token", GlobalContant.instance().token);
					param.put("timestamp", time.getTime_10());
					param.put("productsn", edtext_qc.getText().toString());
					param.put("quantity", edtext_num.getText().toString());

					new Http_PushTask_false().execute(
							CryptTool.transMapToString(param), Updata_url);
					but_invent_updata.setEnabled(false);

					
					edtext_num.setText("");
					edtext_qc.setText("");
					goods_name.setText("");
				}

				break;
			case R.id.but_invent_exit:
				finish();
				break;
			
			
			default:
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

	public void showmessagedata(int message, String what) {
		Message localMessage = new Message();
		localMessage.what = message;
		localMessage.obj = what;
		this.handler.sendMessage(localMessage);
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

			case 1:
				// 本地存儲
				// CheckGoods();
				showToast("上传成功！");
				edtext_num.setText("");
				edtext_qc.setText("");
				goods_name.setText("");
				//
				edtext_num.clearFocus();
				edtext_qc.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtext_qc.getWindowToken(), 0);

				 but_invent_updata.setEnabled(true);
				break;
			case 2:
				showToast("上传失败！" + msg.obj.toString());
				but_invent_updata.setEnabled(true);
				break;
			case 4:
				showToast("已取消！");
				break;

			}
		}
	};

	// 添加商品
	public void shopnum() {
		itemNo = edtext_qc.getText().toString().trim();
		if (itemNo.isEmpty()) {
			return;
		}
		// 获取商品信息
		if (!String.valueOf(itemNo).subSequence(0, 1).equals("2")) {

			ShopItem item = ShopItemMgr.instance().GetItem(itemNo);
			if (item != null) {
				goods_name.setText("商品名称：" + item.itemName.toString());
				edtext_qc.clearFocus();
				edtext_num.requestFocus();
			} else {
				showToast("无法找到条码对应的商品！");
				edtext_qc.setText("");
				edtext_num.clearFocus();
				edtext_qc.requestFocus();
			}

		} else {
			// 商品码
			String itemNoJ = itemNo.subSequence(0, 7).toString();
			// 商品价格
			ShopItem item_dn = ShopItemMgr.instance().GetItem(itemNoJ);
			if (item_dn != null) {
				goods_name.setText("商品名称：" + item_dn.itemName.toString());
				edtext_qc.clearFocus();
				edtext_num.requestFocus();
			} else {
				showToast("无法找到条码对应的商品！");
				edtext_qc.setText("");
				edtext_num.clearFocus();
				edtext_qc.requestFocus();
			}
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
				js = new JSONObject(result);

				if (!TextUtils.isEmpty(result)) {

					if (js.getString("status").equals("1")) {
						showmessagedata(1, js.getString("message"));
					} else {
						showmessagedata(2, js.getString("message"));
					}
				} else {
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

	public void showToast(String tips) {
		Toast.makeText(this, tips, Toast.LENGTH_SHORT).show();
	}

}
