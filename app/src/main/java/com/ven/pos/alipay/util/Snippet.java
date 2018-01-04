package com.ven.pos.alipay.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apaches.commons.codec.binary.Hex;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.jhj.Agreement.ZYB.CryptTool;

public class Snippet {
    /**
     * 查询余额
     */
    @SuppressLint("DefaultLocale")
    public String select_yue() {
        String url = "http://120.55.72.146:6219/sXJCWI/XJQryBlnc.aspx";
        StringBuffer s = new StringBuffer();
        s.append("agentid=").append("18354129777");
        s.append("&merchantKey=").append(
                "hjhUYT7678fgdeTGYUHu766PUPGYCW4390IHFvb");
        String szVerifyString = new String(Hex.encodeHex(DigestUtils.md5(s
                .toString()))).toLowerCase();

        Map<String, String> param = new HashMap<String, String>();
        param.put("agentid", "18354129777");
        param.put("verifystring", szVerifyString);
        try {
            return new Http_PushTask_false().execute(
                    CryptTool.transMapToString(param), url).get();
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
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

            // showmessagedata(Integer.parseInt(result), null);
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

}
