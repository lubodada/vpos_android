package com.ven.pos.Util;

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

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.jhj.Agreement.ZYB.CryptTool;

// 非阻塞http
	public class Http_PushTask_false extends AsyncTask<Object, Void, String> {
		String retu = null;
		Handler handler;
		@Override
		protected String doInBackground(Object... params) {
			if (params.length > 0) {
				httppost((String)params[0], (String)params[1]);
			}
			handler = (Handler)params[2];
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
			if (!"".equals(result) && result != null) {
				Message localMessage = new Message();
			
				try {
					js = new JSONObject(result);

					if (!TextUtils.isEmpty(result)) {

						if (js.getString("status").equals("1")) {
							localMessage.what = 1;
							localMessage.obj =  js.getString("message");
							handler.sendMessage(localMessage);
						} else {
							localMessage.what = 2;
							localMessage.obj =  js.getString("message");
							handler.sendMessage(localMessage);
						}

					} else {
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}
			} else {
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