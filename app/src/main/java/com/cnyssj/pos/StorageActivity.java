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

import com.cnyssj.pos.InventoryActivity.Http_PushTask_false;
import com.jhj.Agreement.ZYB.CryptTool;
import com.ven.pos.GlobalContant;
import com.ven.pos.TitleBar;
import com.ven.pos.Payment.ShopItem;
import com.ven.pos.Payment.ShopItemMgr;
import com.ven.pos.Util.Time;

/**
 * 商品入库操作类
 * 
 * @author lb
 */
public class StorageActivity extends Activity {

	Button but_invent_updata, but_invent_exit;
	EditText edtext_qc_storage, edtext_num_storage;
	TextView goods_name_storage;

	Time time = new Time();
	private String Updata_url = "http://vpos.cnyssj.net/api/updatePurchase";
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		TitleBar.setTitleBar(StorageActivity.this, "", "入   库", "", null);
		setContentView(R.layout.activity_storage);
		// 数据库操作类实例化

		goods_name_storage = (TextView) findViewById(R.id.goods_name_storage);
		edtext_qc_storage = (EditText) findViewById(R.id.storage_ed_qc);
		edtext_num_storage = (EditText) findViewById(R.id.storage_ed_num);
		edtext_num_storage.addTextChangedListener(textWatcher);
		edtext_num_storage
				.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							// 得到焦点时的处理
							shopnum();
						} else {

						}
					}
				});
		but_invent_exit = (Button) findViewById(R.id.but_storage_exit);
		but_invent_updata = (Button) findViewById(R.id.but_storage_updata);

		but_invent_updata.setOnClickListener(myonclicklistener);
		but_invent_exit.setOnClickListener(myonclicklistener);

		edtext_qc_storage.setInputType(InputType.TYPE_NULL);
		edtext_qc_storage.requestFocus();
		// 商品编码输入框
		edtext_qc_storage.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					InputMethodManager imm = (InputMethodManager) StorageActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					shopnum();
					return true;
				}
				return false;
			}
		});
		edtext_qc_storage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		edtext_qc_storage
				.setOnEditorActionListener(new OnEditorActionListener() {
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
			String text = edtext_num_storage.getText().toString();
			int firstDotPos = text.indexOf(".");
			int secondDotPos = text.indexOf(".", firstDotPos + 1);
			if (firstDotPos >= 0 && secondDotPos > firstDotPos) {
				if (beforeText != null) {
					edtext_num_storage.setText(beforeText);
					edtext_num_storage.setSelection(start);
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
	private View.OnClickListener myonclicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.but_storage_updata:
				// 提交
				// 盘点状态
				if (edtext_num_storage.getText().toString() != null
						|| edtext_qc_storage.getText().toString() != null) {

					// 上传服务器
					Map<String, String> param = new HashMap<String, String>();
					param.put("token", GlobalContant.instance().token);
					param.put("timestamp", time.getTime_10());
					param.put("productsn", edtext_qc_storage.getText().toString());
					param.put("quantity", edtext_num_storage.getText().toString());

					new Http_PushTask_false().execute(
							CryptTool.transMapToString(param), Updata_url);
					but_invent_updata.setEnabled(false);

					
					edtext_num_storage.setText("");
					edtext_qc_storage.setText("");
					goods_name_storage.setText("");
				}
				break;
			case R.id.but_storage_exit:
				finish();
				break;
			

			default:
				break;
			}

		}
	};

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
				showToast("上传" + msg.obj.toString());
				edtext_num_storage.setText("");
				edtext_qc_storage.setText("");
				goods_name_storage.setText("");

				edtext_num_storage.clearFocus();
				edtext_qc_storage.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtext_qc_storage.getWindowToken(),
						0);

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
		String itemNo = edtext_qc_storage.getText().toString().trim();
		if (itemNo.isEmpty()) {
			return;
		}
		// 获取商品信息
		if (!String.valueOf(itemNo).subSequence(0, 1).equals("2")) {

			ShopItem item = ShopItemMgr.instance().GetItem(itemNo);
			if (item != null) {
				goods_name_storage.setText("商品名称：" + item.itemName.toString());
				edtext_qc_storage.clearFocus();
				edtext_num_storage.requestFocus();
			} else {
				showToast("无法找到条码对应的商品！");
				edtext_qc_storage.setText("");
				edtext_num_storage.clearFocus();
				edtext_qc_storage.requestFocus();
			}

		} else {
			// 商品码
			String itemNoJ = itemNo.subSequence(0, 7).toString();
			// 商品价格
			ShopItem item_dn = ShopItemMgr.instance().GetItem(itemNoJ);
			if (item_dn != null) {
				goods_name_storage.setText("商品名称："
						+ item_dn.itemName.toString());
				edtext_qc_storage.clearFocus();
				edtext_num_storage.requestFocus();
			} else {
				showToast("无法找到条码对应的商品！");
				edtext_qc_storage.setText("");
				edtext_num_storage.clearFocus();
				edtext_qc_storage.requestFocus();
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
