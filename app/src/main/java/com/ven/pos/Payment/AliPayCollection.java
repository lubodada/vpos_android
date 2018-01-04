package com.ven.pos.Payment;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.ven.pos.GlobalContant;
import com.ven.pos.alipay.util.AlipaySubmit;
import com.ven.pos.alipay.util.UtilDate;

public class AliPayCollection extends PayCollectionBase {

    private static final String TAG = "AliPayCollection";
    public static AliPayCollection inst;

//	static AliPayCollection instance() {
//		if (null == inst) {
//			inst = new AliPayCollection();
//		}
//		return inst;
//	}

    public AliPayCollection(Context c, PayQueryBase payQuery) {
        super(c, payQuery);
    }

    private Context parentContext;
    PayReq req;
    IWXAPI msgApi;
    TextView show;
    Map<String, String> resultunifiedorder;
    StringBuffer sb;
    String nTotalFee;
    String outTradNo;
    String authCode;
    String currentPayDetails;
    int oper;

    public void init(Context c) {
        parentContext = c;
    }

    private void collectionMoney() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // 商户订单号
                    long i = (long) (Math.random() * 900000 + 1000000);
                    // String out_trade_no = "" + i;
                    // 商户网站订单系统中唯一订单号，必填
                    outTradNo = UtilDate.getOrderNum();
                    // 订单名称
                    String subject = "即时支付";
                    // 必填

                    // 订单业务类型
                    // String product_code = "QR_CODE_OFFLINE";
                    String product_code = "BARCODE_PAY_OFFLINE";
                    // 付款金额
                    String total_fee = nTotalFee;
                    // 必填
                    // 卖家支付宝帐户
//					String seller_email = "lckj020@126.com";
                    String seller_email = GlobalContant.instance().aliConfig.seller_email;
                    // 必填
                    // 订单描述
                    String body = GlobalContant.instance().companyName;


                    Map<String, String> sParaTemp = new HashMap<String, String>();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time_expire = sdf.format(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

                    //	sParaTemp.put("method", "alipay.trade.createandpay");
                    sParaTemp.put("method", "alipay.trade.pay");
                    sParaTemp.put("charset", GlobalContant.instance().aliConfig.input_charset);
                    sParaTemp.put("timestamp", time_expire);

                    JSONObject jsonBizContent = new JSONObject();
                    jsonBizContent.put("out_trade_no", outTradNo);
                    jsonBizContent.put("sence", "bar_code");
                    jsonBizContent.put("auth_code", authCode);
                    jsonBizContent.put("subject", "即时支付");
                    jsonBizContent.put("total_amount", total_fee);
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

                            if (!responseJson.getString("code").equals("10000")) {
                                Looper.prepare();
                                Toast.makeText(parentContext,
                                        responseJson.getString("sub_msg"),
                                        Toast.LENGTH_LONG).show();
                                Looper.loop();
                                return;
                            }

                            payQuery.ModifyOrderId(outTradNo);

//							Intent localIntent = new Intent(parentContext,
//									PaymentCodeAct.class);
//							localIntent.putExtra("BarCodeRet",
//									responseJson.getString("qr_code"));
//							localIntent.putExtra("totalMoney", total_fee);
//							localIntent.putExtra("Type",
//									PaymentMainActivity.ALIPAYWAY);
//							localIntent.putExtra("tradeNo", outTradNo);
//							localIntent.putExtra(
//									PaymentResultActivity.IntentPayDetails,
//									currentPayDetails);
//
//							localIntent.putExtra(
//									PaymentResultActivity.IntentOperater, oper);
//
//							parentContext.startActivity(localIntent);

                            // AliPayQuery.instance().ModifyPayInfo(out_trade_no,
                            // totalFee, payDetails, oper);

                            break;
                        }
                    }

                    // ////////////////////////////////////////////////////////////////////////////////

                    // 把请求参数打包成数组
//					Map<String, String> sParaTemp = new HashMap<String, String>();
//					// sParaTemp.put("service",
//					// "AliPayCollection.acquire.precreate");
//					sParaTemp.put("service", "alipay.acquire.createandpay");
//					sParaTemp.put("partner",
//							GlobalContant.instance().aliConfig.partner);
//					sParaTemp.put("_input_charset",
//							GlobalContant.instance().aliConfig.input_charset);
//					sParaTemp.put("notify_url",
//							GlobalContant.instance().aliConfig.notify_url);
//					sParaTemp.put("out_trade_no", outTradNo);
//					sParaTemp.put("subject", subject);
//					sParaTemp.put("product_code", product_code);
//					sParaTemp.put("total_fee", total_fee);
//					// sParaTemp.put("seller_email", seller_email);
//					sParaTemp.put("body", body);
//					sParaTemp.put("dynamic_id_type ", "barcode");
//					sParaTemp.put("dynamic_id ", authCode);
                    // sParaTemp.put("buyer_id",authCode);

                    // 建立请求
//					String sHtmlText = null;
//					try {
//						sHtmlText = AlipaySubmit
//								.buildRequest("", "", sParaTemp);
//
//						AlipayResult.paraResult(sHtmlText);
//
//						String successRet = AlipayResult.singleKey
//								.get("is_success");
//						if (null != successRet) {
//							PaymentCodeAct.paymentCodeAct.ShowMsgBox("扫码收款失败");
//							return;
//						}
//
//						Map<String, String> responseMap = AlipayResult.mutilKey
//								.get("response");
//						if (null == responseMap) {
//							PaymentCodeAct.paymentCodeAct.ShowMsgBox("扫码收款失败");
//							return;
//						}
//
//						String resultCode = responseMap.get("result_code");
//						if (null == resultCode) {
//							PaymentCodeAct.paymentCodeAct.ShowMsgBox("扫码收款失败");
//							return;
//						}
//
//						if (resultCode.equals("ORDER_FAIL")) {
//							PaymentCodeAct.paymentCodeAct
//									.ShowMsgBox("下单失败，请重新下单");
//							return;
//						}
//
//						ModifyPayInfo();
//
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    public void collectionMoney(String totalFee, String tAuthCode/* 顾客二维码 */, String payDetails, int oper) {

        nTotalFee = totalFee;
        authCode = tAuthCode;
        currentPayDetails = payDetails;
        this.oper = oper;
        collectionMoney();
        // PaymentMainActivity.PaymentMainActivity.DismissProDlg();
        // if (sHtmlText != null) {
        // Intent localIntent = new Intent( parentContext,
        // PaymentCodeAct.class);
        // localIntent.putExtra("BarCodeRet", sHtmlText);
        // localIntent.putExtra("totalMoney", total_fee);
        // localIntent.putExtra("Type",
        // PaymentMainActivity.AliPayCollectionWAY);
        // localIntent.putExtra("tradeNo", out_trade_no);
        // parentContext.startActivity(localIntent);
        // }else{
        //
        // }
    }

    public void ModifyPayInfo() {

        // Double dTotalFee = Double.valueOf(nTotalFee)/100;

        //AliPayQuery.instance().ModifyPayInfo(outTradNo, nTotalFee, currentPayDetails, oper);
    }

}
