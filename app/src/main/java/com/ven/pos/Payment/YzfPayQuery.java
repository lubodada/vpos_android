package com.ven.pos.Payment;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sourceforge.simcpux.MD5;
import net.sourceforge.simcpux.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.util.Log;
import android.util.Xml;

import com.ven.pos.GlobalContant;

public class YzfPayQuery extends PayQueryBase {

    private static final String TAG = "YzfPayQuery";

    private QueryPayThread queryPayThread = null;

    public YzfPayQuery() {
        if (null == queryPayThread) {
            queryPayThread = new QueryPayThread();
            queryPayThread.start();
        }
    }

    protected void finalize() {
        // .............................
        // add something....................
        if (queryPayThread != null) {
            queryPayThread.interrupt();
            queryPayThread = null;
        }
    }

    /**
     * 生成签名
     */

    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(GlobalContant.instance().wxConfig.API_KEY);

        String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
                .toUpperCase();
        Log.e("orion", packageSign);
        return packageSign;
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(GlobalContant.instance().wxConfig.API_KEY);

        this.sb.append("sign str\n" + sb.toString() + "\n\n");
        String appSign = MD5.getMessageDigest(sb.toString().getBytes())
                .toUpperCase();
        Log.e("orion", appSign);
        return appSign;
    }

    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");

            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        Log.e("orion", sb.toString());
        return sb.toString();
    }

    public Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if ("xml".equals(nodeName) == false) {
                            // 实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;

    }

    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
                .getBytes());
    }

    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

    //
    private String genProductArgs() {
        StringBuffer xml = new StringBuffer();

        try {
            String nonceStr = genNonceStr();

            // outTradNo = genOutTradNo();

            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("appid", GlobalContant
                    .instance().wxConfig.APP_ID));
            packageParams.add(new BasicNameValuePair("mch_id", GlobalContant
                    .instance().wxConfig.MCH_ID));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            packageParams
                    .add(new BasicNameValuePair("out_trade_no", outTradNo));

            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));

            String xmlstring = toXml(packageParams);

            return xmlstring;

        } catch (Exception e) {
            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }

    }

    private class QueryPayThread extends Thread {

        // private String strTradeNo;

        QueryPayThread() {
            // strTradeNo = tradeNo;
        }

        @Override
        public void run() {
            super.run();
            try {
                int nSecond = 0;
                while (true) {
                    this.currentThread().sleep(5000); // 10秒查一次

                    nSecond += 5;
                    // 10分钟还没处理完，退出线程
                    if (nSecond > 600) {
                        return;
                    }

                    // 查询结束
                    if (!bQuerying) {
                        return;
                    }

                    String url = GlobalContant.instance().wxConfig.WX_ORDER_QUIERY_URL;
                    String entity = genProductArgs();

                    Log.e("orion", entity);

                    byte[] buf = Util.httpPost(url, entity);

                    String content = new String(buf);
                    Log.e("orion", content);
                    Map<String, String> xml = decodeXml(content);

                    // 交易成功
                    if (null != xml.get("trade_state")) {

                        String strTradeState = xml.get("trade_state");

                        String timeEnd = "";
                        if (null != xml.get("time_end")) {
                            timeEnd = xml.get("time_end");
                        }

                        Double dTotalFee = Double.valueOf(nTotalFee);
                        if (null != xml.get("total_fee")) {
                            String strTotalFee = xml.get("total_fee");
                            dTotalFee = Double.valueOf(strTotalFee) / 100;
                        }

                        if (strTradeState.equals("SUCCESS")) {
                            ShowResult(true, "", dTotalFee.toString(),
                                    xml.get("out_trade_no"),
                                    PaymentMainActivity.WEIXINPAYWAY, timeEnd);

                            return;
                        } else if (strTradeState.equals("NOTPAY")) {
                            Toast("等待买家付款");
                        } else if (strTradeState.equals("PAYERROR")) {
                            Toast("支付失败，请重新支付");
                        } else if (strTradeState.equals("REVOKED")) {
                            Toast("交易已撤销，请重新生成交易");
                        } else if (strTradeState.equals("CLOSED")) {
                            Toast("交易已关闭，请重新生成交易");
                        } else if (strTradeState.equals("REFUND")) {
                            Toast("交易转入退款，请重新生成交易");
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void EndQuery() {
        if (null != queryPayThread && queryPayThread.isAlive()) {
            queryPayThread.interrupt();
            queryPayThread = null;
        }
    }


}
