package com.ven.pos.Payment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;

import com.ven.pos.GlobalContant;
import com.ven.pos.alipay.util.AlipaySubmit;

public class AliPayQuery extends PayQueryBase {

    private static final String TAG = "AliPayQuery";

    Thread queryThread = null;

    public AliPayQuery() {
        if (queryThread == null) {
            queryThread = new QueryThread();
            queryThread.start();
        }
    }

    protected void finalize() {
        // .............................
        // add something....................
        if (queryThread != null) {
            queryThread.interrupt();
            queryThread = null;
        }
    }

    private SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy年MM月dd日   HH:mm:ss:SSS    ");

    class QueryThread extends Thread {
        public void run() {

            // ////////////////////////////////////////////////////////////////////////////////
            int nSecond = 0;
            while (true) {
                try {
                    this.currentThread().sleep(5000);

                    // 查询结束
                    if (!bQuerying) {
                        return;
                    }

                    nSecond += 5;
                    // 10分钟还没处理完，退出线程
                    if (nSecond > 600) {
                        return;
                    }

                    // 把请求参数打包成数组
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    String time_expire = sdf.format(System.currentTimeMillis());

                    Map<String, String> sParaTemp = new HashMap<String, String>();
                    sParaTemp.put("method", "alipay.trade.query");
                    // sParaTemp.put("out_trade_no", outTradNo);

                    sParaTemp.put("charset",
                            GlobalContant.instance().aliConfig.input_charset);
                    sParaTemp.put("timestamp", time_expire);

                    JSONObject jsonBizContent = new JSONObject();
                    jsonBizContent.put("out_trade_no", outTradNo);
                    sParaTemp.put("biz_content", jsonBizContent.toString());

                    String sHtmlText = AlipaySubmit.buildRequest("", "",
                            sParaTemp);

                    JSONObject alipayResultJson = new JSONObject(sHtmlText);

                    Iterator<?> it = alipayResultJson.keys();
                    while (it.hasNext()) {// 遍历JSONObject
                        String jsonKey = (String) it.next().toString();

                        if (jsonKey.contains("response")) {
                            JSONObject responseJson = alipayResultJson
                                    .getJSONObject(jsonKey);

                            if (responseJson.getString("code").equals("40004")) {
                                Toast("等待用户付款");
                                continue;
                            }

                            if (!responseJson.getString("code").equals("10000")) {

                                Toast(responseJson.getString("sub_msg")
                                        + " 订单编号 :" + outTradNo);

                                // Toast.makeText(parentContext,
                                // responseJson.getString("sub_msg"),
                                // Toast.LENGTH_LONG).show();

                                continue;
                            }

                            String tradeStatus = responseJson
                                    .getString("trade_status");
                            if (tradeStatus.equals("TRADE_SUCCESS")) {

                                // Intent localIntent = new
                                // Intent(parentContext,
                                // PaymentCodeAct.class);
                                // // localIntent.putExtra("BarCodeRet",
                                // // responseJson.getString("qr_code"));
                                // localIntent.putExtra("totalMoney",
                                // responseJson.getString("total_amount"));
                                // localIntent.putExtra("Type",
                                // PaymentMainActivity.ALIPAYWAY);
                                // localIntent.putExtra("tradeNo",
                                // responseJson.getString("out_trade_no"));
                                // localIntent.putExtra(
                                // PaymentResultActivity.IntentPayDetails,
                                // currentPayDetails);
                                //
                                // localIntent.putExtra(
                                // PaymentResultActivity.IntentOperater,
                                // oper);
                                //
                                // parentContext.startActivity(localIntent);

                                backtoresult(
                                        responseJson.getString("total_amount"),
                                        responseJson.getString("out_trade_no"),
                                        oper, PaymentMainActivity.ALIPAYWAY,
                                        currentPayDetails);

                                return;
                            } else if (tradeStatus.equals("WAIT_BUYER_PAY")) {
                                Toast("等待买家付款");
                            } else if (tradeStatus.equals("TRADE_CLOSED")) {
                                Toast("未付款交易超时关闭，或支付完成后全额退款");
                            } else if (tradeStatus.equals("TRADE_FINISHED")) {
                                Toast("交易结束，不可退款");

                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Looper.loop();
        }
    }

    @Override
    public void onDestory() {
        // TODO Auto-generated method stub
        // queryThread.destroy();
        super.onDestory();
    }

    public void backtoresult(String money, String ordernum, int op, int type,
                             String details) {
        Intent localIntent = new Intent(parentContext,
                PaymentResultActivity.class);
        localIntent.putExtra(PaymentResultActivity.IntentPayResult, true);
        localIntent.putExtra(PaymentResultActivity.IntentPayErrMsg, "");
        localIntent.putExtra(PaymentResultActivity.IntentPayMoney, money);
        localIntent.putExtra(PaymentResultActivity.IntentPayTradeNo, ordernum);
        localIntent.putExtra(PaymentResultActivity.IntentPayType, type);
        localIntent.putExtra(PaymentResultActivity.IntentOperater, op);
        localIntent.putExtra(PaymentResultActivity.IntentUid, memberUid);

        localIntent.putExtra(PaymentResultActivity.IntentPayDetails, details);

        // SimpleDateFormat sdf = new SimpleDateFormat(
        // "yyyyMMDDHHMMSS");
        // String timeEnd = sdf.format(new java.util.Date());
        //

        long curS = System.currentTimeMillis();
        Date curDate = new Date(curS);
        String str = formatter.format(curDate);

        localIntent.putExtra(PaymentResultActivity.IntentPayTime, str);

        parentContext.startActivity(localIntent);
    }

}
