package com.ven.pos.Payment;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

public abstract class PayQueryBase {

    public static final int TOAST_MSG = 200;

    protected Context parentContext;
    protected StringBuffer sb;
    protected String nTotalFee;
    protected String outTradNo;
    protected String authCode; // 顾客的码
    protected String currentPayDetails;
    protected int oper;
    protected Handler handler;

    protected String memberUid;

    protected boolean bQuerying = false;

    public void onDestory() {
        bQuerying = false;
    }

    public void ModifyPayInfo(Context c, Handler handler, String tradeNo,
                              String totalMoney, String payDetails, int oper, String memberUid) {
        this.parentContext = c;
        this.handler = handler;
        this.outTradNo = tradeNo;
        this.nTotalFee = totalMoney;
        this.currentPayDetails = payDetails;
        this.oper = oper;
        this.memberUid = memberUid;

        bQuerying = true;
    }

    public void ModifyOrderId(String tradeNo) {
        this.outTradNo = tradeNo;
    }

    public void Toast(String strContent) {
        Message msg = handler.obtainMessage();// 其中这句与msg.arg1一起使用，<span
        // style="color:#ff0000;">以避免再次运行程序时提示msg.arg1定义的值已使用，如This
        // message is already in
        // use.</span>
        msg.what = TOAST_MSG;
        msg.obj = strContent;
        handler.sendMessage(msg);
    }

    public void ShowResult(boolean bResult, String strErrorMsg,
                           String strPayMoney, String strTradeNo, int nPayType,
                           String strPayTime) {

        Intent localIntent = new Intent(parentContext,
                PaymentResultActivity.class);
        /**
         * @IntentPayErrMsg 支付错误信息
         * @IntentPayMoney 付款金额
         * @IntentPayTradeNo 订单号
         * @IntentPayType 支付类型
         * @IntentPayTime 付款时间
         * @currentPayDetails 支付详情
         * @oper 付款类型 1为付款，2为充值
         * @memberUid 用户ID
         * */
        localIntent.putExtra(PaymentResultActivity.IntentPayResult, bResult);
        localIntent
                .putExtra(PaymentResultActivity.IntentPayErrMsg, strErrorMsg);
        localIntent.putExtra(PaymentResultActivity.IntentPayMoney, strPayMoney);
        localIntent
                .putExtra(PaymentResultActivity.IntentPayTradeNo, strTradeNo);
        localIntent.putExtra(PaymentResultActivity.IntentPayType, nPayType);
        localIntent.putExtra(PaymentResultActivity.IntentPayTime, strPayTime);
        localIntent.putExtra(PaymentResultActivity.IntentPayDetails,
                currentPayDetails);
        localIntent.putExtra(PaymentResultActivity.IntentOperater, oper);
        localIntent.putExtra(PaymentResultActivity.IntentUid, memberUid);

        parentContext.startActivity(localIntent);
    }
}
