package com.jhj.Agreement.ZYB;

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

import android.os.AsyncTask;

public class Http_PushTask extends AsyncTask<String, Void, String> {
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
			HttpEntity requestHttpEntity = new UrlEncodedFormEntity(paramList);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(requestHttpEntity);
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpPost);
			showResponseResult(response);
		} catch (Exception e) {
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
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream));
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
