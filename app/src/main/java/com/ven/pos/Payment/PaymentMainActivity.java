package com.ven.pos.Payment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudpos.scanserver.aidl.IScanService;
import com.cloudpos.scanserver.aidl.ScanParameter;
import com.cloudpos.scanserver.aidl.ScanResult;
import com.cnyssj.db.DBManager;
import com.cnyssj.pos.R;
import com.jhj.Agreement.ZYB.Http_Pay_ZYB;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.ven.pos.ExchangeActivity;
import com.ven.pos.GlobalContant;
import com.ven.pos.ITitleBarLeftClick;
import com.ven.pos.QRcode_Activity;
import com.ven.pos.TitleBar;
import com.ven.pos.Util.BaseActivity;
import com.ven.pos.Util.HttpConnection;
import com.ven.pos.Util.ReflectUtil;
import com.ven.pos.Util.UtilTool;
import com.ven.pos.Util.Util_Screen;
import com.ven.pos.control.AidlController;
import com.ven.pos.control.IAIDLListener;
import com.ven.pos.qscanbar.PayCameraActivity;

public class PaymentMainActivity extends BaseActivity implements IAIDLListener{
    public class GetPayReq {

        String getPayTypeName() {
            return "";
        }
    }

    private static final int REFRESH_UI = 103;
    private static final int TOAST_SHOW = 102;
    private static final int RECONNECT_WIFI = 104;

    public static final int CONSUMENUM = 7;// 会员扫描返回

    public static final String INTENT_CONSUME_HIDE = "hideConsume"; // 是否显示会员支付

    public static final int CASHPAYWAY = 1; // 现金支付
    public static final int ALIPAYWAY = 3; // 阿里支付
    public static final int WEIXINPAYWAY = 4; // 微信支付
    public static final int CONSUME_PAYWAY = 2; // 会员卡支付
    public static final int UNION_PAYWAY = 5; // 银联 支付
    public static final int YIZHIFU = 6;
    public static final int JIFEN = 11;

    @SuppressWarnings("unused")
    private static final int UNION_RESAULT_RETURN = 2; // 银联支付返回

    public static PaymentMainActivity PaymentMainActivity;
    private double TotalMoney;

    private View unionPay_paid_ll;
    private CheckBox bank_paid_choose_ch;
    private CheckBox cash_paid_choose_ch;
    private CheckBox unionpay_paid_choose_ch;

    private EditText cash_paid_edittext;
    private View cash_paid_ll;
    private LinearLayout cash_paid_return_view_ll;

    private double change = 0.0D;
    private Button payment_click;
    private CheckBox payment_purse_choose_ch;
    private TextView payment_purse_tv;
    private TextView return_money_textview;
    private TextView should_paia_text;
    private double thirdPay = 0.0D;

    private ProgressDialog pDialog = null;
    Context context;

    private LinearLayout memberll, LL_pay_QR;

    private ImageView memberhead;
    private TextView membername;
    private TextView membernum;
    private TextView memberMoney;
    String member = "";
    String memberuid = "";

    // 传的商品明细
    String PayDetails;
    /**
     * @迁移
     */
    // 弹出框相关
    Window w;
    WindowManager.LayoutParams lp;
    // 弹出框
    Dialog dia_Scancode;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    String data_fdm, dz_mode;
    // 设备终端号，收银员名称，商户号
    String clientId, dtn;
    Http_Pay_ZYB hp = new Http_Pay_ZYB();
    SharedPreferences_util su = new SharedPreferences_util();
    Button dia_Scancode_qd, dia_Scancode_qx;
    String str_monery, outOrderId;
    EditText dia_ed_Scancode;
    // 积分兑换
    View exchange_Pay_paid_ll;
    private CheckBox ex_change_check;
    TextView tv_exchange_num, tv_exchange_num_new;
    EditText et_exchange_num;
    String message;
    View view_exchange;
    View exchange_Pay_paid_ll_int;
    String exchange_monery;
    // 二维码
    ImageView pay_yzf, pay_wx, pay_al;
    // 应付金额
    String exchange_monery_free;
    DBManager DBM;
    String mode_pay_type;

    boolean isLandScreen;

    private IScanService scanService; //扫码服务
    private ServiceConnection scanConn;
    private IAIDLListener listener;

    @SuppressWarnings("static-access")
    public void data_qy() {
        data_fdm = su.getPrefString(PaymentMainActivity.this, "fdm", null);
        clientId = su.getPrefString(PaymentMainActivity.this, "clientId", null);

        dtn = su.getPrefString(PaymentMainActivity.this, "dtn", null);
        dz_mode = su.getPrefString(PaymentMainActivity.this, "mode", null);
    }

    // 付款
    public void pay_Yikaidian(String str) {

        if (!TextUtils.isEmpty(str) && Integer.parseInt(str) > 0) {
            if (!TextUtils.isEmpty(clientId)) {
                if (!TextUtils.isEmpty(dz_mode)) {
                    DismissProDlg();
                    dia_Scancode.show();
                    dia_Scancode.setCanceledOnTouchOutside(false);
                    w = dia_Scancode.getWindow();
                    lp = w.getAttributes();
                    lp.x = 0;
                    lp.y = 40;
                    dia_Scancode.onWindowAttributesChanged(lp);
                } else {
                    if (Build.MANUFACTURER.contains("BASEWIN") || Build.MANUFACTURER.contains("basewin")) {
                        Intent intent = new Intent();
                        intent.setClass(PaymentMainActivity.this,
                                MipcaActivityCapture.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                    } else if (Build.MANUFACTURER.contains("WIZAR") || Build.MANUFACTURER.contains("wizar") || ReflectUtil.getWizarType()) {
                        synchronizationScan();
                    }
                }

            } else {
                Toast.makeText(PaymentMainActivity.this, "请在设置中设置正确的设备终端号！",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(PaymentMainActivity.this, "请输入正确的金额！",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 慧银POS扫码
     */
    private void synchronizationScan() {
        ScanParameter parameter = new ScanParameter();
        try {
            ScanResult result = scanService.scanBarcode(parameter);
            onActResult(result);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void onActResult(ScanResult scanResult){
        int result = Integer.parseInt(scanResult.getText().substring(0, 2));
        if (scanResult.getText().length() != 20) {
            if (result == 13) {
                // 微信
                Intent localIntent = new Intent(
                        PaymentMainActivity.this,
                        PaymentResultActivity.class);
                // 金额
                localIntent.putExtra("str_monery", str_monery);
                // 交易码
                localIntent.putExtra("result_r", scanResult.getText());
                // 掌优宝支付类型
                localIntent.putExtra("orderType", 4);
                updata_order(outOrderId, 4);
                updata_order_goods(outOrderId, 2);
                // 原平台支付类型
                localIntent.putExtra("PaymentMethod", 4);
                // 商品列表
                localIntent.putExtra("PayDetails", PayDetails);
                // 消费类型
                localIntent.putExtra("IntentOperater", 1);
                // 是否需要支付
                localIntent.putExtra("Payment_switch", true);
                updata_order(outOrderId, 4);
                updata_order_goods(outOrderId, 2);
                localIntent.putExtra("outOrderId", outOrderId);
                startActivity(localIntent);

            } else if (result == 51) {
                // 翼支付
                Intent localIntent = new Intent(
                        PaymentMainActivity.this,
                        PaymentResultActivity.class);
                localIntent.putExtra("str_monery", str_monery);
                localIntent.putExtra("result_r", scanResult.getText());
                localIntent.putExtra("orderType", 13);
                updata_order(outOrderId, 13);
                updata_order_goods(outOrderId, 2);
                localIntent.putExtra("PaymentMethod", 6);
                localIntent.putExtra("PayDetails", PayDetails);
                localIntent.putExtra("IntentOperater", 1);
                localIntent.putExtra("Payment_switch", true);
                updata_order(outOrderId, 6);
                updata_order_goods(outOrderId, 2);
                localIntent.putExtra("outOrderId", outOrderId);
                startActivity(localIntent);
            } else if (result == 28) {
                // 支付宝
                Intent localIntent = new Intent(
                        PaymentMainActivity.this,
                        PaymentResultActivity.class);
                localIntent.putExtra("str_monery", str_monery);
                localIntent.putExtra("result_r", scanResult.getText());
                localIntent.putExtra("orderType", 1);
                localIntent.putExtra("PaymentMethod", 3);
                localIntent.putExtra("PayDetails", PayDetails);
                localIntent.putExtra("IntentOperater", 1);
                localIntent.putExtra("Payment_switch", true);
                updata_order(outOrderId, 3);
                updata_order_goods(outOrderId, 2);
                localIntent.putExtra("outOrderId", outOrderId);
                startActivity(localIntent);
            } else {
                toast("无效二维码数据！");
            }
        } else {
            toast("无效二维码数据！");
        }
    }

    @Override
    public void serviceConnected(Object objService, ServiceConnection connection) {
        if (objService instanceof IScanService) {
            scanService = (IScanService) objService;
            scanConn = connection;
        }
    }

    public void result() {

        String result_r = dia_ed_Scancode.getText().toString();
        int result = Integer.parseInt(result_r.substring(0, 2));
        if (result_r.length() != 20) {
            if (result == 13) {
                // 微信
                Intent localIntent = new Intent(PaymentMainActivity.this,
                        PaymentResultActivity.class);
                // 金额
                localIntent.putExtra("str_monery", str_monery);
                // 交易码
                localIntent.putExtra("result_r", result_r);
                // 掌优宝支付类型
                localIntent.putExtra("orderType", 4);
                // 原平台支付类型
                localIntent.putExtra("PaymentMethod", 4);
                // 商品列表
                localIntent.putExtra("PayDetails", PayDetails);
                // 消费类型
                localIntent.putExtra("IntentOperater", 1);
                // 是否需要支付
                localIntent.putExtra("Payment_switch", true);
                localIntent.putExtra("outOrderId", outOrderId);
                updata_order(outOrderId, 4);
                updata_order_goods(outOrderId, 2);
                startActivity(localIntent);
            } else if (result == 51) {
                // 翼支付
                Intent localIntent = new Intent(PaymentMainActivity.this,
                        PaymentResultActivity.class);
                localIntent.putExtra("str_monery", str_monery);
                localIntent.putExtra("result_r", result_r);
                localIntent.putExtra("orderType", 13);
                localIntent.putExtra("PaymentMethod", 6);
                localIntent.putExtra("PayDetails", PayDetails);
                localIntent.putExtra("IntentOperater", 1);
                localIntent.putExtra("Payment_switch", true);
                localIntent.putExtra("outOrderId", outOrderId);
                updata_order(outOrderId, 6);
                updata_order_goods(outOrderId, 2);
                startActivity(localIntent);
            } else if (result == 28) {
                // 支付宝
                Intent localIntent = new Intent(PaymentMainActivity.this,
                        PaymentResultActivity.class);
                localIntent.putExtra("str_monery", str_monery);
                localIntent.putExtra("result_r", result_r);
                localIntent.putExtra("orderType", 1);
                localIntent.putExtra("PaymentMethod", 3);
                localIntent.putExtra("PayDetails", PayDetails);
                localIntent.putExtra("IntentOperater", 1);
                localIntent.putExtra("Payment_switch", true);
                localIntent.putExtra("outOrderId", outOrderId);
                updata_order(outOrderId, 3);
                updata_order_goods(outOrderId, 2);
                startActivity(localIntent);
            } else {
                toast("无效二维码数据！");
            }
        } else {
            toast("无效二维码数据！");
        }
        dia_ed_Scancode.setText("");
        dia_Scancode.cancel();
    }

    private void dia_() {
        // 弹出框
        dia_Scancode = new Dialog(PaymentMainActivity.this,
                R.style.edit_AlertDialog_style);
        dia_Scancode.setContentView(R.layout.activity_start_dialog_scancode);
        dia_Scancode_qd = (Button) dia_Scancode.findViewById(R.id.Scancode_qd);
        dia_Scancode_qx = (Button) dia_Scancode.findViewById(R.id.Scancode_qx);
        dia_ed_Scancode = (EditText) dia_Scancode
                .findViewById(R.id.et_Scancode);
        dia_Scancode_qd.setOnClickListener(myonclick);
        dia_Scancode_qx.setOnClickListener(myonclick);
        dia_ed_Scancode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    result();
                    return true;
                }
                return false;
            }
        });
        dia_ed_Scancode.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                }
                return true;
            }
        });
    }

    /**
     * 用Handler来更新UI
     */
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case GlobalContant.CloseProgressDialog: {
                    // 关闭ProgressDialog
                    PaymentMainActivity.pDialog.dismiss();
                    break;
                }
                case GlobalContant.ShowProgressDialog: {
                    PaymentMainActivity.pDialog.show();
                    break;
                }
                case GlobalContant.ShowErrorDialog: {
                    String getResult1 = (String) msg.obj;
                    PaymentMainActivity.ShowMsgBox(getResult1, "错误", "确定", null);
                    break;
                }
                case TOAST_SHOW: {
                    Toast.makeText(context, (String) msg.obj, Toast.LENGTH_LONG)
                            .show();
                }
                break;
                case RECONNECT_WIFI: {
                    WifiManager mWiFiManager = (WifiManager) context
                            .getSystemService(Context.WIFI_SERVICE);
                    if (!mWiFiManager.isWifiEnabled()) {
                        mWiFiManager.setWifiEnabled(true);
                    }
                }
                break;
                case REFRESH_UI: {
                    try {
                        JSONObject jsonContent = new JSONObject((String) msg.obj);
                        membername.setText(jsonContent.getString("name"));
                        member = jsonContent.getString("mobile");
                        memberuid = jsonContent.getString("uid");
                        membernum.setText(member);
                        memberMoney.setText(jsonContent.getString("credit2"));
                        String headurl = jsonContent.getString("avatar");
                        Glide.with(context).load(headurl).into(memberhead);
                        memberll.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        Toast.makeText(context, "服务器返回信息错误", Toast.LENGTH_LONG)
                                .show();
                        e.printStackTrace();
                    }
                    break;
                }
                case 100: {
                    setContentView(R.layout.payment_main);
                    initView();
                    setOnClick();
                    UtilTool.checkLoginStatusAndReturnLoginActivity(
                            PaymentMainActivity.this, null);
                    break;
                }
                case 1:
                    toast("上传成功");
                    break;
                case 2:
                    toast(message);
                    break;
                case 3:
                    toast("数据获取失败！");
                    break;
            }
        }
    };

    @SuppressWarnings("static-access")
    public void initView() {
        mode_pay_type = su.getPrefString(getApplicationContext(),
                "mode_pay_type", null);


        outOrderId = getIntent().getStringExtra("outOrderId");
        // 现金
        this.cash_paid_choose_ch = ((CheckBox) findViewById(R.id.cash_paid_choose_ch));
        this.cash_paid_choose_ch.setEnabled(false);
        this.cash_paid_ll = findViewById(R.id.cash_paid_ll);
        this.cash_paid_return_view_ll = ((LinearLayout) findViewById(R.id.cash_paid_return_view));


        this.should_paia_text = ((TextView) findViewById(R.id.should_paia_text));
        this.return_money_textview = ((TextView) findViewById(R.id.return_money_textview));
        this.payment_purse_tv = ((TextView) findViewById(R.id.payment_purse_tv));
        this.cash_paid_edittext = ((EditText) findViewById(R.id.cash_paid_edittext));
        this.payment_purse_choose_ch = ((CheckBox) findViewById(R.id.payment_purse_choose_ch));
        // 积分兑换
        this.ex_change_check = (CheckBox) findViewById(R.id.ex_change_check);
        this.exchange_Pay_paid_ll = findViewById(R.id.exchange_Pay_paid_ll);
        this.tv_exchange_num = (TextView) findViewById(R.id.tv_exchange_num);
        this.et_exchange_num = (EditText) findViewById(R.id.et_exchange_num);
        tv_exchange_num_new = (TextView) findViewById(R.id.tv_exchange_num_new);
        exchange_Pay_paid_ll_int = findViewById(R.id.exchange_Pay_paid_ll_int);
        view_exchange = findViewById(R.id.view_exchange);
        et_exchange_num.setVisibility(View.GONE);
        view_exchange.setVisibility(View.GONE);
        tv_exchange_num_new.setVisibility(View.GONE);
        exchange_Pay_paid_ll_int.setVisibility(View.GONE);
        // 在线支付
        this.unionPay_paid_ll = findViewById(R.id.UnionPay_paid_ll);
        this.unionpay_paid_choose_ch = ((CheckBox) findViewById(R.id.unionpay_paid_choose_ch));

        this.payment_click = ((Button) findViewById(R.id.payment_click));
        // 获取金额
        this.TotalMoney = Double.parseDouble(getIntent().getStringExtra(
                "TotalMoney"));

        this.should_paia_text.setText(Double.toString(this.TotalMoney));
        boolean sinkactivity = getIntent().getBooleanExtra("mark_activity",
                false);
        if (sinkactivity) {
            View free = findViewById(R.id.free_view);
            ImageView iv1 = (ImageView) findViewById(R.id.imageView1);
            TextView tv1 = (TextView) findViewById(R.id.textView1);
            ex_change_check.setVisibility(View.GONE);
            PayDetails = getIntent().getStringExtra("PayDetails");
            free.setVisibility(View.GONE);
            iv1.setVisibility(View.GONE);
            tv1.setVisibility(View.GONE);
            TextView Surplus_monery = (TextView) findViewById(R.id.Surplus_monery);
            Surplus_monery.setText("还需支付 ");
            exchange_Pay_paid_ll.setVisibility(View.GONE);
        } else {
            PayDetails = getIntent().getExtras().getString(
                    PaymentResultActivity.IntentPayDetails);
        }
        memberll = (LinearLayout) findViewById(R.id.memberll);
        memberhead = (ImageView) findViewById(R.id.memberhead);
        membername = (TextView) findViewById(R.id.membername);
        membernum = (TextView) findViewById(R.id.membernum);
        memberMoney = (TextView) findViewById(R.id.membermoney);

        if (GlobalContant.MACHINE != GlobalContant.MACHINE_I9000S) {
            this.unionPay_paid_ll.setVisibility(View.GONE);
        }
        //二维码付款
        LL_pay_QR = (LinearLayout) findViewById(R.id.LL_pay_QR);
        pay_al = (ImageView) findViewById(R.id.pay_al);
        pay_wx = (ImageView) findViewById(R.id.pay_wx);
        pay_yzf = (ImageView) findViewById(R.id.pay_yzf);
        pay_al.setOnClickListener(myonclick);
        pay_wx.setOnClickListener(myonclick);
        pay_yzf.setOnClickListener(myonclick);
        //设置开关，
        if (mode_pay_type != null) {
            boolean pay_type = false;
            if (mode_pay_type.indexOf("0") == -1) {
                cash_paid_ll.setVisibility(View.GONE);
                cash_paid_return_view_ll.setVisibility(View.GONE);
            } else {
                if (!pay_type) {
                    PaymentMainActivity.this.cash_paid_choose_ch
                            .performClick();
                    pay_type = true;
                }
            }
            if (mode_pay_type.indexOf("1") == -1) {
                this.unionPay_paid_ll.setVisibility(View.GONE);
            } else {
                if (!pay_type) {
                    PaymentMainActivity.this.unionpay_paid_choose_ch
                            .performClick();
                    pay_type = true;
                }
            }
            if (mode_pay_type.indexOf("2") == -1) {
                this.LL_pay_QR.setVisibility(View.GONE);
            } else {
                if (!pay_type) {
                    pay_type = true;
                }
            }

            if (mode_pay_type.indexOf("3") == -1) {
                view_exchange.setVisibility(View.GONE);
                this.exchange_Pay_paid_ll.setVisibility(View.GONE);
            } else {
                if (!pay_type) {
                    PaymentMainActivity.this.ex_change_check
                            .performClick();
                    pay_type = true;
                    tv_exchange_num.setVisibility(View.VISIBLE);
                    et_exchange_num.setVisibility(View.VISIBLE);
                    tv_exchange_num_new.setVisibility(View.VISIBLE);
                    et_exchange_num.setFocusable(true);
                    et_exchange_num.setFocusableInTouchMode(true);
                    et_exchange_num.requestFocus();

                    view_exchange.setVisibility(View.VISIBLE);
                    exchange_Pay_paid_ll_int.setVisibility(View.VISIBLE);
                    String str = Double.toString(Math.ceil(TotalMoney * 100));
                    tv_exchange_num.setText("  需 "
                            + str.substring(0, str.length() - 2) + " 个积分" + "  +"
                            + Double.toString(Math.ceil(TotalMoney * 10 * 3) / 100)
                            + "元");
                    et_exchange_num
                            .addTextChangedListener(new EditChangedListener());
                    PaymentMainActivity.this.unionpay_paid_choose_ch
                            .setChecked(false);
                    PaymentMainActivity.this.cash_paid_choose_ch.setChecked(false);
                }
            }

        }
    }

    View.OnClickListener myonclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {

                case R.id.Scancode_qd:
                    result();
                    break;
                case R.id.Scancode_qx:
                    dia_Scancode.cancel();// 关闭弹出框
                    dia_ed_Scancode.setText("");
                    break;
                case R.id.pay_al:
                    intent.putExtra("orderType", 1);
                    // 商品列表
                    intent.putExtra("PayDetails", PayDetails);
                    // 总金额
                    intent.putExtra("TotalMoney", Double.toString(TotalMoney));
                    intent.putExtra("outOrderId", outOrderId);
                    updata_order(outOrderId, 3);
                    updata_order_goods(outOrderId, 2);
                    intent.setClass(PaymentMainActivity.this, QRcode_Activity.class);
                    startActivity(intent);
                    break;
                case R.id.pay_wx:
                    intent.putExtra("orderType", 4);
                    intent.putExtra("PayDetails", PayDetails);
                    intent.putExtra("TotalMoney", Double.toString(TotalMoney));
                    intent.putExtra("outOrderId", outOrderId);
                    updata_order(outOrderId, 4);
                    updata_order_goods(outOrderId, 2);
                    intent.setClass(PaymentMainActivity.this, QRcode_Activity.class);
                    startActivity(intent);
                    break;
                case R.id.pay_yzf:
                    intent.putExtra("orderType", 13);
                    intent.putExtra("PayDetails", PayDetails);
                    intent.putExtra("TotalMoney", Double.toString(TotalMoney));
                    intent.putExtra("outOrderId", outOrderId);
                    updata_order(outOrderId, 6);
                    updata_order_goods(outOrderId, 2);
                    intent.setClass(PaymentMainActivity.this, QRcode_Activity.class);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        }
    };
    ITitleBarLeftClick memberloginlistener = new ITitleBarLeftClick() {
        @Override
        public void click() {
            Intent openCameraIntent = new Intent(PaymentMainActivity.this,
                    PayCameraActivity.class);
            openCameraIntent.putExtra(PayCameraActivity.IntentSCANTYPE,
                    PayCameraActivity.CONSUME_QSCAN);
            startActivityForResult(openCameraIntent, 0);
        }
    };

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        isLandScreen = su.getPrefboolean(PaymentMainActivity.this, "isLandScreen", false);

        Util_Screen.screenToFullOrLand(this, true, isLandScreen);
        TitleBar.setTitleBar(this, "返回", "收款", "会员登录", memberloginlistener);
        setContentView(R.layout.loading);
        context = this;
        listener = this;
        AidlController.getInstance().startScanService(context, listener);

        // 预加载数据
        data_qy();
        // 初始化控件
        dia_();
        PaymentMainActivity = this;
        this.pDialog = new ProgressDialog(this);
        this.pDialog.setProgressStyle(0);

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(100);
                    Message localMessage = new Message();
                    localMessage.what = 100;
                    handler.sendMessage(localMessage);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void onStart() {
        DismissProDlg();
        TitleBar.setActivity(this);
        super.onStart();
    }

    protected void onDestroy() {

        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        setTitle("收款");
    }

    public void packDataForPay(String paramString1, String paramString2) {

    }

    public void resetCashPayDate() {
        this.cash_paid_edittext.setText("0.00");
        this.return_money_textview.setText("0.00");
        this.payment_purse_tv.setText("(￥0.00)");
        this.change = 0.0D;
    }

    public void setOnClick() {
        this.cash_paid_ll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (!PaymentMainActivity.this.cash_paid_choose_ch.isChecked())
                    PaymentMainActivity.this.cash_paid_choose_ch.performClick();
                PaymentMainActivity.this.cash_paid_edittext.selectAll();
            }
        });
        // 在线支付
        this.unionPay_paid_ll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (!PaymentMainActivity.this.unionpay_paid_choose_ch
                        .isChecked())
                    PaymentMainActivity.this.unionpay_paid_choose_ch
                            .performClick();
            }
        });
        this.unionpay_paid_choose_ch
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramAnonymousView) {
                        if (!PaymentMainActivity.this.unionpay_paid_choose_ch
                                .isChecked())
                            PaymentMainActivity.this.unionpay_paid_choose_ch
                                    .performClick();
                        tv_exchange_num.setVisibility(View.GONE);
                        et_exchange_num.setVisibility(View.GONE);
                        view_exchange.setVisibility(View.GONE);
                        tv_exchange_num_new.setVisibility(View.GONE);
                        exchange_Pay_paid_ll_int.setVisibility(View.GONE);
                        PaymentMainActivity.this.ex_change_check
                                .setChecked(false);
                        PaymentMainActivity.this.cash_paid_choose_ch
                                .setChecked(false);
                    }
                });

        // 积分支付
        this.exchange_Pay_paid_ll
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View paramAnonymousView) {
                        if (!PaymentMainActivity.this.ex_change_check
                                .isChecked())
                            PaymentMainActivity.this.ex_change_check
                                    .performClick();
                    }
                });
        this.ex_change_check.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PaymentMainActivity.this.ex_change_check.isChecked())
                    PaymentMainActivity.this.ex_change_check.performClick();
                tv_exchange_num.setVisibility(View.VISIBLE);
                et_exchange_num.setVisibility(View.VISIBLE);
                tv_exchange_num_new.setVisibility(View.VISIBLE);
                et_exchange_num.setFocusable(true);
                et_exchange_num.setFocusableInTouchMode(true);
                et_exchange_num.requestFocus();

                view_exchange.setVisibility(View.VISIBLE);
                exchange_Pay_paid_ll_int.setVisibility(View.VISIBLE);
                String str = Double.toString(Math.ceil(TotalMoney * 100));
                tv_exchange_num.setText("  需 "
                        + str.substring(0, str.length() - 2) + " 个积分" + "  +"
                        + Double.toString(Math.ceil(TotalMoney * 10 * 3) / 100)
                        + "元");
                et_exchange_num
                        .addTextChangedListener(new EditChangedListener());
                PaymentMainActivity.this.unionpay_paid_choose_ch
                        .setChecked(false);
                PaymentMainActivity.this.cash_paid_choose_ch.setChecked(false);
            }
        });
        // 现金支付
        this.cash_paid_choose_ch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PaymentMainActivity.this.cash_paid_choose_ch.isChecked())
                    PaymentMainActivity.this.cash_paid_choose_ch.performClick();
                PaymentMainActivity.this.cash_paid_edittext.selectAll();
                PaymentMainActivity.this.cash_paid_edittext.setFocusable(true);
                PaymentMainActivity.this.cash_paid_edittext
                        .setFocusableInTouchMode(true);
                PaymentMainActivity.this.cash_paid_edittext.requestFocus();
                InputMethodManager inputManager = (InputMethodManager) PaymentMainActivity.this.cash_paid_edittext
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(
                        PaymentMainActivity.this.cash_paid_edittext, 0);
                //
                PaymentMainActivity.this.unionpay_paid_choose_ch
                        .setChecked(false);
                PaymentMainActivity.this.ex_change_check.setChecked(false);
            }
        });
        this.cash_paid_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                showCashPay();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
        // 付款
        this.payment_click.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                onPay();
            }
        });
    }

    public void setSingleChooice(final boolean cash_pay,
                                 final boolean wechat_pay, final boolean alipy_pay,
                                 final boolean consume_pay, final boolean union_pay,
                                 final boolean yunzhixun_paid) {
        // 设置需要显示的控件
        PaymentMainActivity.this.cash_paid_choose_ch.setChecked(cash_pay);
        if (cash_pay) {
            // PaymentMainActivity.this.cash_paid_choose_ch.setEnabled(false);
            PaymentMainActivity.this.unionpay_paid_choose_ch.setEnabled(true);
        }
        if (wechat_pay) {
            PaymentMainActivity.this.unionpay_paid_choose_ch.setEnabled(true);
        }
        if (alipy_pay) {
            PaymentMainActivity.this.unionpay_paid_choose_ch.setEnabled(true);
        }

        if (consume_pay) {
            PaymentMainActivity.this.unionpay_paid_choose_ch.setEnabled(true);
        }

        PaymentMainActivity.this.unionpay_paid_choose_ch.setChecked(union_pay);
        if (union_pay) {
            PaymentMainActivity.this.unionpay_paid_choose_ch.setEnabled(false);
        }

    }

    public void showCashPay() {

        try {
            if (this.cash_paid_choose_ch.isChecked()) {
                double d1 = Double.parseDouble(this.should_paia_text.getText()
                        .toString());
                double d2 = Double.parseDouble(this.cash_paid_edittext
                        .getText().toString());
                // if ( d2-d1 >= 0) {
                DecimalFormat df = new DecimalFormat("##0.00");
                this.return_money_textview.setText(df.format(d2 - d1));
                // }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // 现金支付
    private void cashPay() {
        double d1 = Double.parseDouble(this.should_paia_text.getText()
                .toString());
        double d2 = Double.parseDouble(this.cash_paid_edittext.getText()
                .toString());
        if (d2 <= 0.00) {
            toast("请输入正确的金额");
            DismissProDlg();
            return;
        }
        if (d2 - d1 < 0.0) {
            Toast.makeText(this, "请输入金额小于支付金额", Toast.LENGTH_LONG).show();
            DismissProDlg();
        }
        this.cash_paid_choose_ch
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(

                            CompoundButton paramAnonymousCompoundButton,
                            boolean paramAnonymousBoolean) {
                        if (paramAnonymousBoolean) {
                            PaymentMainActivity.this.cash_paid_edittext
                                    .setEnabled(true);
                            PaymentMainActivity.this.setSingleChooice(true,
                                    false, false, false, false, false);
                            PaymentMainActivity.this.payment_purse_choose_ch
                                    .setEnabled(true);
                            PaymentMainActivity.this.showCashPay();
                            return;
                        }
                        PaymentMainActivity.this.resetCashPayDate();
                    }
                });
        Intent localIntent = new Intent(this, PaymentResultActivity.class);
        // 是否需要支付
        localIntent.putExtra("Payment_switch", false);
        String monery;
        if ((Double.parseDouble(cash_paid_edittext.getText().toString()) - TotalMoney) > 0) {
            monery = Double.toString(TotalMoney * 100);
        } else {
            monery = Double.toString((Double.parseDouble(cash_paid_edittext
                    .getText().toString()) * 100));
        }

        // 支付金额
        String str_monery = monery.substring(0, monery.length() - 2);

        localIntent.putExtra("str_monery", str_monery);

        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy/MM/dd HH:mm:ss");
        String Time_new = sDateFormat.format(new java.util.Date());
        localIntent.putExtra("Time_new", Time_new);
        // 交易类型
        localIntent.putExtra("PaymentMethod", CASHPAYWAY);
        // 商品列表
        localIntent.putExtra("PayDetails", PayDetails);
        localIntent.putExtra("outOrderId", outOrderId);
        updata_order(outOrderId, 1);
        updata_order_goods(outOrderId, 2);
        startActivity(localIntent);
    }

    public void ShowProDlg() {
        Message localMessage = new Message();
        localMessage.what = GlobalContant.ShowProgressDialog;
        this.handler.sendMessage(localMessage);
    }

    public void DismissProDlg() {
        Message localMessage = new Message();
        localMessage.what = GlobalContant.CloseProgressDialog;
        this.handler.sendMessage(localMessage);
    }

    protected void ShowMsgBox(String paramString1) {
        Message localMessage = new Message();
        localMessage.what = GlobalContant.ShowErrorDialog;
        localMessage.obj = paramString1;
        this.handler.sendMessage(localMessage);
    }

    // 发送handle
    public void showmessage(int message) {
        Message localMessage = new Message();
        localMessage.what = message;
        this.handler.sendMessage(localMessage);
    }

    protected void ShowMsgBox(String paramString1, String paramString2,
                              String paramString3,
                              DialogInterface.OnClickListener paramOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(paramString2);
        builder.setIcon(android.R.drawable.ic_dialog_info);

        TextView inputEdit = new TextView(this);
        inputEdit.setText(paramString1);
        builder.setPositiveButton(paramString3, paramOnClickListener);
        builder.setView(inputEdit);
        builder.show();
    }

    private void onPay() {
        ShowProDlg();
        if (PaymentMainActivity.this.cash_paid_choose_ch.isChecked()) {
            cashPay();
        } else if (PaymentMainActivity.this.unionpay_paid_choose_ch.isChecked()) {
            unionPay();
        } else if (PaymentMainActivity.this.ex_change_check.isChecked()) {
            exchange();
        } else {
            DismissProDlg();
            toast("请选择支付方式");
        }
    }

    private void unionPay() {
        Integer value = (int) ((Double.parseDouble(should_paia_text.getText()
                .toString()) * 100));
        str_monery = value.toString();
        // 翼开店付款
        pay_Yikaidian(str_monery);
    }

    private void exchange() {
        DismissProDlg();
        Intent intent = new Intent();
        intent.putExtra("integral", et_exchange_num.getText().toString().trim());
        // 兑换金额
        intent.putExtra("totalFee", exchange_monery);
        // 商品列表
        intent.putExtra("PayDetails", PayDetails);
        // 应付金额
        intent.putExtra("exchange_monery_free", exchange_monery_free);
        // 总金额
        intent.putExtra("TotalMoney", Double.toString(TotalMoney));
        intent.setClass(PaymentMainActivity.this, ExchangeActivity.class);
        startActivity(intent);
        finish();
    }

    Runnable unionPayRunnable = new Runnable() {
        public void run() {
            try {
                Double nValue = Double.parseDouble(should_paia_text.getText()
                        .toString());
                pay_Yikaidian(Double.toString(nValue * 100));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }
    };

    // 回调方法，从第二个页面回来的时候会执行这个方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 根据上面发送过去的请求码来区别
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    Bundle bundle = data.getExtras();
                    // 显示扫描到的内容
                    int result = Integer.parseInt(bundle.getString("result")
                            .substring(0, 2));
                    if (bundle.getString("result").length() != 20) {
                        if (result == 13) {
                            // 微信
                            Intent localIntent = new Intent(
                                    PaymentMainActivity.this,
                                    PaymentResultActivity.class);
                            // 金额
                            localIntent.putExtra("str_monery", str_monery);
                            // 交易码
                            localIntent.putExtra("result_r",
                                    bundle.getString("result"));
                            // 掌优宝支付类型
                            localIntent.putExtra("orderType", 4);
                            updata_order(outOrderId, 4);
                            updata_order_goods(outOrderId, 2);
                            // 原平台支付类型
                            localIntent.putExtra("PaymentMethod", 4);
                            // 商品列表
                            localIntent.putExtra("PayDetails", PayDetails);
                            // 消费类型
                            localIntent.putExtra("IntentOperater", 1);
                            // 是否需要支付
                            localIntent.putExtra("Payment_switch", true);
                            updata_order(outOrderId, 4);
                            updata_order_goods(outOrderId, 2);
                            localIntent.putExtra("outOrderId", outOrderId);
                            startActivity(localIntent);

                        } else if (result == 51) {
                            // 翼支付
                            Intent localIntent = new Intent(
                                    PaymentMainActivity.this,
                                    PaymentResultActivity.class);
                            localIntent.putExtra("str_monery", str_monery);
                            localIntent.putExtra("result_r",
                                    bundle.getString("result"));
                            localIntent.putExtra("orderType", 13);
                            updata_order(outOrderId, 13);
                            updata_order_goods(outOrderId, 2);
                            localIntent.putExtra("PaymentMethod", 6);
                            localIntent.putExtra("PayDetails", PayDetails);
                            localIntent.putExtra("IntentOperater", 1);
                            localIntent.putExtra("Payment_switch", true);
                            updata_order(outOrderId, 6);
                            updata_order_goods(outOrderId, 2);
                            localIntent.putExtra("outOrderId", outOrderId);
                            startActivity(localIntent);
                        } else if (result == 28) {
                            // 支付宝
                            Intent localIntent = new Intent(
                                    PaymentMainActivity.this,
                                    PaymentResultActivity.class);
                            localIntent.putExtra("str_monery", str_monery);
                            localIntent.putExtra("result_r",
                                    bundle.getString("result"));
                            localIntent.putExtra("orderType", 1);
                            localIntent.putExtra("PaymentMethod", 3);
                            localIntent.putExtra("PayDetails", PayDetails);
                            localIntent.putExtra("IntentOperater", 1);
                            localIntent.putExtra("Payment_switch", true);
                            updata_order(outOrderId, 3);
                            updata_order_goods(outOrderId, 2);
                            localIntent.putExtra("outOrderId", outOrderId);
                            startActivity(localIntent);
                        } else {
                            toast("无效二维码数据！");
                        }
                    } else {
                        toast("无效二维码数据！");
                    }
                }
                break;

            case 0:
                // 会员卡扫描结果
                switch (resultCode) {
                    case CONSUMENUM:
                        String num = data
                                .getStringExtra(PayCameraActivity.RetCustomerNo);
                        new QueryThread(num).start();
                        ShowProDlg();
                        break;
                    case RESULT_OK:
                        String info = data
                                .getStringExtra(PayCameraActivity.RetCustomerNo);
                        int type = data.getIntExtra(PayCameraActivity.IntentSCANTYPE,
                                PayCameraActivity.CONSUME_QSCAN);
                        if (type == PayCameraActivity.CONSUME_QSCAN) {
                            int start = info.indexOf("mid=");
                            String subStr = info.substring(start + 4);
                            int end = subStr.indexOf("&");
                            String tnum = subStr.substring(0, end);
                            new QueryThread(tnum).start();
                            ShowProDlg();
                        }
                        break;

                    default:
                        break;
                }

                break;

            default:
                break;
        }
    }

    class QueryThread extends Thread {
        private String userAccount;

        public QueryThread(String paramString1) {
            this.userAccount = paramString1;
        }

        public void run() {
            Looper.prepare();
            // this.serverUrl = GlobalContant.instance().serverUrl;
            String serverUrl = GlobalContant.instance().mainUrl
                    + getResources().getString(R.string.getmember_url);

            Map<String, String> sParaTemp = new HashMap<String, String>();
            sParaTemp.put("mobile", userAccount);
            sParaTemp.put("token", GlobalContant.instance().token);

            String str1 = "";
            try {
                str1 = HttpConnection.buildRequest(serverUrl, sParaTemp);

                Log.v("member recv", str1);

                DismissProDlg();
                if (str1 == null) {
                    Message localMessage = new Message();
                    localMessage.what = TOAST_SHOW;
                    localMessage.obj = "查询失败";
                    handler.sendMessage(localMessage);
                    return;
                }

                JSONObject json = new JSONObject(str1);
                if (json.isNull("msgContent")) {
                    Message localMessage = new Message();
                    localMessage.what = TOAST_SHOW;
                    localMessage.obj = "查询失败";
                    handler.sendMessage(localMessage);
                    return;
                }

                Message localMessage = new Message();
                localMessage.what = REFRESH_UI;
                localMessage.obj = json.getString("msgContent");
                handler.sendMessage(localMessage);
            } catch (Exception localException1) {
                localException1.printStackTrace();
                Toast.makeText(context, str1, Toast.LENGTH_LONG).show();
            }

            Looper.loop();
        }
    }

    class EditChangedListener implements TextWatcher {
        private CharSequence temp;// 监听前的文本
        private int editStart;// 光标开始位置
        private int editEnd;// 光标结束位置
        private final int charMaxNum = 10;

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            temp = s;
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            int str_num = et_exchange_num.getText().toString().trim().length();
            if (str_num != 0) {
                exchange_monery_free = Double
                        .toString(Math.ceil(TotalMoney
                                * 100
                                - (Math.floor(Double
                                .parseDouble(et_exchange_num.getText()
                                        .toString().trim()) / 10 * 7))) / 100);
                exchange_monery = Double.toString((Math.floor(Double
                        .parseDouble(et_exchange_num.getText().toString()
                                .trim())) / 100));
                tv_exchange_num_new.setText("还需付款：" + exchange_monery_free);

            } else {
                tv_exchange_num_new.setText("还需付款：");
            }
        }

        public void afterTextChanged(Editable s) {

            editStart = et_exchange_num.getSelectionStart();
            editEnd = et_exchange_num.getSelectionEnd();
            if (temp.length() > charMaxNum) {
                toast("你输入的字数已经超过了限制！");
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                et_exchange_num.setText(s);
                et_exchange_num.setSelection(tempSelection);
            }

        }
    }

    ;

    private void updata_order(String outOrderId, int paytype) {
        DBM = new DBManager(getApplicationContext());
        if (DBM.updatePayType(outOrderId, paytype)) {
            // showToast("更新状态成功");
        } else {
            toast("更新状态失败_支付方式更新");
        }
    }

    // 更新商品订单状态
    private void updata_order_goods(String outOrderId, int paytype) {
        DBM = new DBManager(getApplicationContext());
        if (DBM.updateOrderstatus(outOrderId, paytype)) {
            // showToast("更新状态成功");
        } else {
            toast("更新状态失败_商品订单状态更新");
        }
    }

    public void toast(String str) {
        Toast.makeText(PaymentMainActivity.this, str, Toast.LENGTH_SHORT)
                .show();
    }
}
