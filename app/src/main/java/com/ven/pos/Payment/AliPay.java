package com.ven.pos.Payment;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.ven.pos.GlobalContant;
import com.ven.pos.alipay.util.AlipaySubmit;
import com.ven.pos.alipay.util.UtilDate;

public class AliPay {

    private static final String TAG = "AliPay";
    public static AliPay inst;

    static AliPay instance() {
        if (null == inst) {
            inst = new AliPay();
        }
        return inst;
    }

    private Context parentContext;
    PayReq req;
    IWXAPI msgApi;
    TextView show;
    Map<String, String> resultunifiedorder;
    StringBuffer sb;
    String nTotalFee;
    String outTradNo;

    public void init(Context c) {
        parentContext = c;
    }

    public void reqPay(String totalFee, String payDetails, int oper,
                       String memberuid) throws JSONException {

        nTotalFee = totalFee;
        // 商户订单号

        String out_trade_no = UtilDate.getOrderNumUNIX();

        Map<String, String> sParaTemp = new HashMap<String, String>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time_expire = sdf.format(System.currentTimeMillis());

        sParaTemp.put("method", "alipay.trade.precreate");
        sParaTemp.put("charset",
                GlobalContant.instance().aliConfig.input_charset);

        sParaTemp.put("timestamp", time_expire);

        JSONObject jsonBizContent = new JSONObject();
        jsonBizContent.put("out_trade_no", out_trade_no);
        jsonBizContent.put("total_amount", totalFee);
        jsonBizContent.put("subject", "即时支付");
        sParaTemp.put("biz_content", jsonBizContent.toString());

        // 建立请求

        String sHtmlText = null;
        try {
            sHtmlText = AlipaySubmit.buildRequest("", "", sParaTemp);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {

            JSONObject alipayResultJson = new JSONObject(sHtmlText);

            Iterator<?> it = alipayResultJson.keys();
            while (it.hasNext()) {// 遍历JSONObject
                String jsonKey = (String) it.next().toString();

                if (jsonKey.contains("response")) {
                    JSONObject responseJson = alipayResultJson
                            .getJSONObject(jsonKey);

                    if (!responseJson.getString("code").equals("10000")) {
                        Looper.prepare();
                        Toast.makeText(parentContext,
                                responseJson.getString("msg"),
                                Toast.LENGTH_LONG).show();
                        Looper.loop();
                        return;
                    }

                    Intent localIntent = new Intent(parentContext,
                            PaymentCodeAct.class);
                    localIntent.putExtra("BarCodeRet",
                            responseJson.getString("qr_code"));
                    localIntent.putExtra("totalMoney", totalFee);
                    localIntent.putExtra("Type", PaymentMainActivity.ALIPAYWAY);
                    localIntent.putExtra("tradeNo", out_trade_no);
                    localIntent.putExtra(
                            PaymentResultActivity.IntentPayDetails, payDetails);

                    localIntent.putExtra(PaymentResultActivity.IntentOperater,
                            oper);
                    if (!memberuid.equals("") && memberuid != null) {
                        localIntent.putExtra(PaymentResultActivity.IntentUid,
                                memberuid);
                    }

                    parentContext.startActivity(localIntent);

                    // AliPayQuery.instance().ModifyPayInfo(out_trade_no,
                    // totalFee, payDetails, oper);

                    break;
                }
            }

        } catch (Exception ex) {
            Looper.prepare();
            Toast.makeText(parentContext, "请联系管理员查看配置是否正确", Toast.LENGTH_LONG)
                    .show();
            Looper.loop();
            ex.printStackTrace();
        }

    }

}
