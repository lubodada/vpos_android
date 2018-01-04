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

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.widget.TextView;

import com.cnyssj.pos.R;
import com.ven.pos.GlobalContant;
import com.ven.pos.alipay.util.UtilDate;

public class WxPayCollection extends PayCollectionBase {

    private static final String TAG = "WxPayCollection";
    public static WxPayCollection inst;

    public WxPayCollection(Context c, PayQueryBase payQuery) {
        super(c, payQuery);
    }

//	static WxPayCollection instance() {
//		if (null == inst) {
//			inst = new WxPayCollection();
//		}
//		return inst;
//	}

    //private Context parentContext;

    TextView show;
    Map<String, String> resultunifiedorder;
    StringBuffer sb;
    String nTotalFee;
    String outTradNo;
    String authCode; // 顾客的码
    String currentPayDetails;
    int oper;


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

    private String genOutTradNo() {
        // Random random = new Random();

        return MD5.getMessageDigest(UtilDate.getOrderNum().getBytes());
    }

    //
    private String genProductArgs() {
        StringBuffer xml = new StringBuffer();

        try {
            // String nonceStr = genNonceStr();

            outTradNo = genOutTradNo();
            String bodyStr = parentContext.getResources().getString(R.string.pay_explain);

            xml.append("</xml>");
            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
            packageParams.add(new BasicNameValuePair("appid", GlobalContant
                    .instance().wxConfig.APP_ID));
            packageParams.add(new BasicNameValuePair("attach", "shoufei"));
            packageParams.add(new BasicNameValuePair("auth_code", authCode));
            packageParams.add(new BasicNameValuePair("body", bodyStr));
            packageParams.add(new BasicNameValuePair("mch_id", GlobalContant
                    .instance().wxConfig.MCH_ID));
            packageParams
                    .add(new BasicNameValuePair("nonce_str", genNonceStr()));

            // packageParams.add(new BasicNameValuePair("notify_url",
            // GlobalContant.instance().wxConfig.NOTIFY_URL));
            packageParams
                    .add(new BasicNameValuePair("out_trade_no", outTradNo));
            packageParams.add(new BasicNameValuePair("spbill_create_ip",
                    "127.0.0.1"));
            packageParams.add(new BasicNameValuePair("total_fee", nTotalFee));

            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));

            String xmlstring = toXml(packageParams);
            xmlstring = new String(xmlstring.getBytes("UTF-8"), "ISO-8859-1");
            return xmlstring;

        } catch (Exception e) {
            Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
            return null;
        }

    }

    public void ModifyPayInfo() {

        Double dTotalFee = Double.valueOf(nTotalFee) / 100;

        //	ModifyPayInfo(outTradNo, dTotalFee.toString(), currentPayDetails, oper);
    }

    public void collectionMoney(String money, String tAuthCode/* 顾客二维码 */, String payDetails, int oper) {


        int totalFee = (int) (Float.parseFloat(money) * 100);
        this.nTotalFee = String.valueOf(totalFee);

        //	nTotalFee = money;

        authCode = tAuthCode;
        this.currentPayDetails = payDetails;
        this.oper = oper;

        new Thread() {
            @Override
            public void run() {
                try {
                    String url = GlobalContant.instance().wxConfig.WX_COLLECTION_PAY;
                    String entity = genProductArgs();

                    Log.e("orion", entity);

                    byte[] buf = Util.httpPost(url, entity);

                    String content = new String(buf);
                    Log.e("orion", content);
                    Map<String, String> xml = decodeXml(content);

                    if (xml.get("result_code").equals("SUCCESS")
                            && xml.get("return_code").equals("SUCCESS")) {

                        payQuery.ModifyOrderId(outTradNo);
                        //ModifyPayInfo();
                    } else {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();

    }

}
