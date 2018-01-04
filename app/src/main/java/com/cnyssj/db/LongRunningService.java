package com.cnyssj.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sourceforge.simcpux.MD5Util;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.cnyssj.db.util.Order;
import com.cnyssj.pos.R;
import com.jhj.Agreement.ZYB.CryptTool;
import com.jhj.Agreement.ZYB.Http_PushTask;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.jhj.Agreement.ZYB.Url;
import com.ven.pos.GlobalContant;
import com.ven.pos.Util.HttpConnection;


public class LongRunningService extends Service {
    DBManager DBM;
    List<Order> myorder = new ArrayList<Order>();
    List<Order> myorder_pay = new ArrayList<Order>();
    String outOrderId, keyString, merchantId;
    // 订单号
    // 订单号
    String outOrderId_pay;
    SharedPreferences_util su = new SharedPreferences_util();
    public static boolean pay_status = true;
    AlarmManager mAlarmManager = null;
    PendingIntent mPendingIntent = null;
    public static int sign = 0;
    public static boolean status_updata = true;
    private String token;

    @Override
    public void onCreate() {
        Intent intent = new Intent(getApplicationContext(),
                LongRunningService.class);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mPendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        long now = SystemClock.elapsedRealtime();
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, now,
                1 * 1000, mPendingIntent);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sign++;

        if (sign == 5) {

            if (status_updata) {
                showmessage(100);
            }
            if (pay_status) {
                showmessage(101);
            }
            sign = 0;
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void showmessage(int message) {
        Message localMessage = new Message();
        localMessage.what = message;
        this.handler.sendMessage(localMessage);
    }

    public Handler handler = new Handler() {
        @SuppressWarnings("static-access")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    DBM = new DBManager(getApplicationContext());
                    myorder = DBM.queryOrderBystatus();

                    if (myorder.size() > 0) {
                        (new SaveTradeInfoThread_status()).start();
                    }
                    break;
                case 101:
                    keyString = su.getPrefString(LongRunningService.this, "key",
                            null);
                    merchantId = su.getPrefString(LongRunningService.this,
                            "merchantId", null);

                    new SaveTradeInfoThread_pay().start();

                    break;
                case 1:
                    updata_shop_items_paymentstatus(outOrderId_pay, 1);
                    break;
                case 4:
                    updata_shop_items_paymentstatus(outOrderId_pay, 1);
                    break;
                case 7:
                    updata_shop_items_paymentstatus(outOrderId_pay, 3);
                    break;
                case 10:
                    updata_shop_items_paymentstatus(outOrderId_pay, 6);
                    break;
                case 13:
                    updata_shop_items_paymentstatus(outOrderId_pay, 7);
                    break;
            }
        }
    };

    // 保存数据
    class SaveTradeInfoThread_status extends Thread {

        @SuppressWarnings("static-access")
        public void run() {
            Looper.prepare();
            status_updata = false;
            DBM = new DBManager(LongRunningService.this);
            String printTickUrl = GlobalContant.instance().mainUrl
                    + getResources().getString(R.string.save_ticket_url);

            if (myorder.size() > 0) {
                int i = 0;
                while (myorder.size() > i) {

                    Order order = myorder.get(i);
                    outOrderId = order.getMyorderid();

                    Map<String, String> sParaTemp = new HashMap<String, String>();
                    token = su.getPrefString(getApplicationContext(), "token",
                            null);
                    sParaTemp.put("token", token);
                    String orderid;
                    if (!TextUtils.isEmpty(String.valueOf(order.getOrderid()))) {
                        orderid = order.getOrderid();
                    } else {
                        orderid = outOrderId;
                    }

                    sParaTemp.put("orderid", orderid);
                    sParaTemp.put("money", order.getAmount());
                    sParaTemp.put("type", String.valueOf(order.getType()));
                    String sign_sc = MD5Util.MD5Encode(GlobalContant.SECRET_KEY
                            + orderid, "UTF-8");
                    sParaTemp.put("autograph", sign_sc);
                    sParaTemp.put("op", "memberpay");
                    sParaTemp.put("dis", "0");
                    sParaTemp.put("sncodeid", "0");
                    sParaTemp.put("uid", "0");
                    sParaTemp.put("beizhu", "");
                    sParaTemp.put("items", order.getItems()); // 商品列表详情，json串s
                    String str1;
                    try {
                        // 上送商品信息
                        str1 = HttpConnection.buildRequest(printTickUrl,
                                sParaTemp);
                        Log.e("temp", String.valueOf(sParaTemp) + str1 + "返回");
                        if (str1 == null) {
                            Log.e("tiaoshu", "return error");
                            updata_upload_status(outOrderId, 0);

                        } else {
                            JSONTokener jsonParser = new JSONTokener(str1);
                            JSONObject json = (JSONObject) jsonParser
                                    .nextValue();
                            Log.e("tiaoshu", "return right");
                            Log.e("tiaoshu", str1);
                            if (!json.getString("status").equals("1")) {
                                updata_upload_status(outOrderId, 0);
                                Log.e("tiaoshu", "updata error");
                            } else {
                                updata_upload_status(outOrderId, 1);
                                Log.e("tiaoshu", "updata right");
                            }
                        }

                    } catch (Exception localException1) {
                        localException1.printStackTrace();
                    }

                    i++;
                }
            }
            status_updata = true;
        }

    }

    // 查询支付状态明确的订单
    class SaveTradeInfoThread_pay extends Thread {

        public void run() {
            pay_status = false;
            Looper.prepare();
            DBM = new DBManager(LongRunningService.this);
            String printTickUrl = GlobalContant.instance().mainUrl
                    + getResources().getString(R.string.save_ticket_url);
            String starttime = GoodsOrder.getNear_WeekDay("yyyy-MM-dd");
            String stoptime = GoodsOrder.getCurrentData2();
            /* 查询是否有缓存的订单数据 */
            myorder_pay = DBM.queryOrderByPaymentstatus(7, starttime, stoptime);
            showToast(String.valueOf(myorder_pay.size()));
            if (myorder_pay.size() > 0) {
                for (int i = 0; i < myorder.size(); i++) {
                    Order order = myorder_pay.get(i);
                    // 签名
                    outOrderId_pay = order.getMyorderid();
                    String signature = null;
                    StringBuilder sb = new StringBuilder();// 组装mac加密明文串
                    sb.append("inputCharset=").append("utf-8");
                    sb.append("&merchantId=").append(merchantId);
                    if (order.getOrderid() != null) {
                        sb.append("&orderId=").append(order.getOrderid());
                    } else {
                        showToast("数据不完整，无法查询当前交易！");
                        continue;
                    }

                    sb.append("&outOrderId=").append(outOrderId_pay);
                    sb.append("&signType=").append("MD5");
                    sb.append("").append(keyString);
                    try {
                        signature = CryptTool.md5Digest(sb.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }// 进行md5加密(商户自己封装MD5加密工具类，此处只提供
                    // 参考)
                    Map<String, String> sParaTemp = new HashMap<String, String>();
                    sParaTemp.put("inputCharset", "utf-8");

                    sParaTemp.put("orderid", order.getOrderid());
                    sParaTemp.put("merchantId", merchantId);
                    sParaTemp.put("orderId", order.getOrderid());
                    sParaTemp.put("outOrderId", outOrderId_pay);
                    sParaTemp.put("signature", signature);
                    sParaTemp.put("signType", "MD5");

                    Http_PushTask HP = new Http_PushTask();

                    try {
                        String result = HP.execute(
                                CryptTool.transMapToString(sParaTemp),
                                Url.query_zyb).get(1000 * 5,
                                TimeUnit.MILLISECONDS);
                        JSONObject js = new JSONObject(result);

                        if (js.getBoolean("result")) {
                            if (js.has("message")) {
                                if (js.has("orderState")) {
                                    // 交易状态
                                    showmessage(js.getInt("orderState"));
                                }
                            }
                        } else {
                            // showToast(js.get("message").toString());
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }

                }
                pay_status = true;
            }

        }
    }

    private void updata_upload_status(String myorderid, int uploadstatus) {
        DBM = new DBManager(getApplicationContext());
        if (DBM.updateUploadStatus(myorderid, uploadstatus)) {
            showToast("更新成功");
        } else {
            showToast("更新状态失败");
        }
    }

    public void showToast(String tips) {
        Toast.makeText(getApplicationContext(), tips, Toast.LENGTH_SHORT)
                .show();
    }

    private void updata_shop_items_paymentstatus(String outOrderId,
                                                 int paymentstatus) {
        DBM = new DBManager(getApplicationContext());
        if (DBM.updatePayStatus(outOrderId, paymentstatus)) {
            // showToast("更新状态成功");
        } else {
            showToast("更新状态失败");
        }
    }

    // private void updata_upload_Paystatus(int paymentstatus) {
    // DBM = new DBManager(getApplicationContext());
    // if (DBM.queryOrderByPaymentstatus( paymentstatus) != null) {
    //
    // }
    // }
}