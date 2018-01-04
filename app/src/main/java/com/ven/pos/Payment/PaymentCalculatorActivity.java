package com.ven.pos.Payment;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.cloudpos.scanserver.aidl.IScanService;
import com.cloudpos.scanserver.aidl.ScanParameter;
import com.cloudpos.scanserver.aidl.ScanResult;
import com.cnyssj.pos.R;
import com.jhj.Agreement.ZYB.Http_Pay_ZYB;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.ven.pos.GoodSearchActivity;
import com.ven.pos.TitleBar;
import com.ven.pos.Util.BaseActivity;
import com.ven.pos.Util.ReflectUtil;
import com.ven.pos.Util.UtilTool;
import com.ven.pos.Util.Util_Screen;
import com.ven.pos.control.AidlController;
import com.ven.pos.control.IAIDLListener;

public class PaymentCalculatorActivity extends BaseActivity implements View.OnClickListener, IAIDLListener {
    public static boolean isClear = false;
    private Button btn_0;
    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_4;
    private Button btn_5;
    private Button btn_6;
    private Button btn_7;
    private Button btn_8;
    private Button btn_9;
    private Button btn_clean;
    private LinearLayout btn_confirm;
    private LinearLayout btn_delete;
    private Button btn_divide;
    private Button btn_minus;
    private Button btn_multiply;
    private Button btn_plus;
    private Button btn_point;
    private TextView confirm;
    private SoundPool pool;
    private TextView result;
    private StringBuffer showString = new StringBuffer();
    public String[] symbol = {"+", "-", "x", "÷"};

    /**
     * @迁移
     */
    // 弹出框相关
    Window w;
    WindowManager.LayoutParams lp;
    // 弹出框
    Dialog dia_Scancode;
    EditText dia_ed_Scancode;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    String data_fdm, dz_mode;
    // 设备终端号，收银员名称，商户号
    String cashierName, dtn, clientId;
    Http_Pay_ZYB hp = new Http_Pay_ZYB();
    SharedPreferences_util su = new SharedPreferences_util();
    Button dia_Scancode_qd, dia_Scancode_qx;
    String str_monery;
    String Str_money_num, money_mode;

    boolean isLandScreen;
    private IScanService scanService; //扫码服务
    private ServiceConnection scanConn;
    private Context mContext;
    private IAIDLListener listener;

    @SuppressWarnings("static-access")
    public void data_qy() {
        data_fdm = su
                .getPrefString(PaymentCalculatorActivity.this, "fdm", null);

        clientId = su.getPrefString(PaymentCalculatorActivity.this, "clientId",
                null);
        cashierName = su.getPrefString(PaymentCalculatorActivity.this,
                "cashiername", null);
        dtn = su.getPrefString(PaymentCalculatorActivity.this, "dtn", null);

        Str_money_num = su.getPrefString(PaymentCalculatorActivity.this,
                "Str_money_num", "1000");
        dz_mode = su
                .getPrefString(PaymentCalculatorActivity.this, "mode", null);
        money_mode = su.getPrefString(PaymentCalculatorActivity.this, "money",
                "");
    }

    // 付款
    public void pay_Yikaidian(String str) {

        if (!TextUtils.isEmpty(str) && Integer.parseInt(str) > 0) {

            if (!TextUtils.isEmpty(clientId)) {

                if (!TextUtils.isEmpty(dz_mode)) {
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
                        intent.setClass(PaymentCalculatorActivity.this,
                                MipcaActivityCapture.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                    } else if (Build.MANUFACTURER.contains("WIZAR") || Build.MANUFACTURER.contains("wizar") || ReflectUtil.getWizarType()) {
                        synchronizationScan();
                    }
                }

            } else {
                Toast.makeText(PaymentCalculatorActivity.this,
                        "请在设置中设置正确的设备终端号！", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(PaymentCalculatorActivity.this, "请输入正确的金额！",
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

    public void result() {
        String result_r = dia_ed_Scancode.getText().toString();
        int result = Integer.parseInt(result_r.substring(0, 2));
        if (result_r.length() != 20) {
            if (result == 13) {
                // 微信
                Intent localIntent = new Intent(PaymentCalculatorActivity.this,
                        PaymentResultActivity.class);
                // 金额
                localIntent.putExtra("str_monery", str_monery);
                localIntent.putExtra("result_r", result_r);
                // 店名
                localIntent.putExtra("cashierName", cashierName);
                // 设备终端号
                localIntent.putExtra("clientId", clientId);
                // 掌优宝支付类型
                localIntent.putExtra("orderType", 4);
                // 原平台支付类型
                localIntent.putExtra("PaymentMethod", 4);
                // 商品列表
                localIntent.putExtra("PayDetails", "");
                // 消费类型
                localIntent.putExtra("IntentOperater", 1);
                // 是否需要支付
                localIntent.putExtra("Payment_switch", true);
                startActivity(localIntent);

            } else if (result == 51) {

                // 翼支付
                Intent localIntent = new Intent(PaymentCalculatorActivity.this,
                        PaymentResultActivity.class);
                // 金额
                localIntent.putExtra("str_monery", str_monery);
                // 交易码
                localIntent.putExtra("result_r", result_r);
                // 店名
                localIntent.putExtra("cashierName", cashierName);
                // 设备终端号
                localIntent.putExtra("clientId", clientId);
                // 掌优宝支付类型
                localIntent.putExtra("orderType", 13);
                // 原平台支付类型
                localIntent.putExtra("PaymentMethod", 6);
                // 商品列表
                localIntent.putExtra("PayDetails", "");
                // 消费类型
                localIntent.putExtra("IntentOperater", 1);
                // 是否需要支付
                localIntent.putExtra("Payment_switch", true);
                startActivity(localIntent);

            } else if (result == 28) {
                toast("请使用翼支付付款！");
                // 支付宝
                Intent localIntent = new Intent(PaymentCalculatorActivity.this,
                        PaymentResultActivity.class);
                localIntent.putExtra("str_monery", str_monery);
                localIntent.putExtra("result_r", result_r);
                localIntent.putExtra("cashierName", cashierName);
                localIntent.putExtra("clientId", clientId);
                localIntent.putExtra("orderType", 1);
                localIntent.putExtra("PaymentMethod", 3);
                localIntent.putExtra("PayDetails", "");
                localIntent.putExtra("IntentOperater", 1);
                localIntent.putExtra("Payment_switch", true);
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

    @Override
    public void serviceConnected(Object objService, ServiceConnection connection) {
        if (objService instanceof IScanService) {
            scanService = (IScanService) objService;
            scanConn = connection;
        }
    }

    public void onActResult(ScanResult scanResult){
        int result = Integer.parseInt(scanResult.getText().substring(0, 2));
        if (scanResult.getText().length() != 20) {
            if (result == 13) {
                // 微信
                Intent localIntent = new Intent(
                        PaymentCalculatorActivity.this,
                        PaymentResultActivity.class);
                // 金额
                localIntent.putExtra("str_monery", str_monery);
                // 交易码
                localIntent.putExtra("result_r", scanResult.getText());
                // 店名
                localIntent.putExtra("cashierName", cashierName);
                // 设备终端号
                localIntent.putExtra("clientId", clientId);
                // 掌优宝支付类型
                localIntent.putExtra("orderType", 4);
                // 原平台支付类型
                localIntent.putExtra("PaymentMethod", 4);
                // 商品列表
                localIntent.putExtra("PayDetails", "");
                // 消费类型
                localIntent.putExtra("IntentOperater", 1);
                // 是否需要支付
                localIntent.putExtra("Payment_switch", true);
                startActivity(localIntent);

            } else if (result == 51) {
                // 翼支付
                Intent localIntent = new Intent(
                        PaymentCalculatorActivity.this,
                        PaymentResultActivity.class);
                localIntent.putExtra("str_monery", str_monery);
                localIntent.putExtra("result_r", scanResult.getText());
                localIntent.putExtra("cashierName", cashierName);
                localIntent.putExtra("clientId", clientId);
                localIntent.putExtra("orderType", 13);
                localIntent.putExtra("PaymentMethod", 6);
                localIntent.putExtra("PayDetails", "");
                localIntent.putExtra("IntentOperater", 1);
                localIntent.putExtra("Payment_switch", true);
                startActivity(localIntent);

            } else if (result == 28) {
                // 支付宝
                Intent localIntent = new Intent(
                        PaymentCalculatorActivity.this,
                        PaymentResultActivity.class);
                localIntent.putExtra("str_monery", str_monery);
                localIntent.putExtra("result_r", scanResult.getText());
                localIntent.putExtra("cashierName", cashierName);
                localIntent.putExtra("clientId", clientId);
                localIntent.putExtra("orderType", 1);
                localIntent.putExtra("PaymentMethod", 3);
                localIntent.putExtra("PayDetails", "");
                localIntent.putExtra("IntentOperater", 1);
                localIntent.putExtra("Payment_switch", true);
                startActivity(localIntent);
            } else {
                toast("无效二维码数据！");
            }
        } else {
            toast("无效二维码数据！");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                                    PaymentCalculatorActivity.this,
                                    PaymentResultActivity.class);
                            // 金额
                            localIntent.putExtra("str_monery", str_monery);
                            // 交易码
                            localIntent.putExtra("result_r",
                                    bundle.getString("result"));
                            // 店名
                            localIntent.putExtra("cashierName", cashierName);
                            // 设备终端号
                            localIntent.putExtra("clientId", clientId);
                            // 掌优宝支付类型
                            localIntent.putExtra("orderType", 4);
                            // 原平台支付类型
                            localIntent.putExtra("PaymentMethod", 4);
                            // 商品列表
                            localIntent.putExtra("PayDetails", "");
                            // 消费类型
                            localIntent.putExtra("IntentOperater", 1);
                            // 是否需要支付
                            localIntent.putExtra("Payment_switch", true);
                            startActivity(localIntent);

                        } else if (result == 51) {
                            // 翼支付
                            Intent localIntent = new Intent(
                                    PaymentCalculatorActivity.this,
                                    PaymentResultActivity.class);
                            localIntent.putExtra("str_monery", str_monery);
                            localIntent.putExtra("result_r",
                                    bundle.getString("result"));
                            localIntent.putExtra("cashierName", cashierName);
                            localIntent.putExtra("clientId", clientId);
                            localIntent.putExtra("orderType", 13);
                            localIntent.putExtra("PaymentMethod", 6);
                            localIntent.putExtra("PayDetails", "");
                            localIntent.putExtra("IntentOperater", 1);
                            localIntent.putExtra("Payment_switch", true);
                            startActivity(localIntent);

                        } else if (result == 28) {
                            // 支付宝
                            Intent localIntent = new Intent(
                                    PaymentCalculatorActivity.this,
                                    PaymentResultActivity.class);
                            localIntent.putExtra("str_monery", str_monery);
                            localIntent.putExtra("result_r",
                                    bundle.getString("result"));
                            localIntent.putExtra("cashierName", cashierName);
                            localIntent.putExtra("clientId", clientId);
                            localIntent.putExtra("orderType", 1);
                            localIntent.putExtra("PaymentMethod", 3);
                            localIntent.putExtra("PayDetails", "");
                            localIntent.putExtra("IntentOperater", 1);
                            localIntent.putExtra("Payment_switch", true);
                            startActivity(localIntent);
                        } else {
                            toast("无效二维码数据！");
                        }
                    } else {
                        toast("无效二维码数据！");
                    }
                }
                break;
        }
    }

    private void dia_() {
        // 弹出框
        dia_Scancode = new Dialog(PaymentCalculatorActivity.this,
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

    public void toast(String str) {
        Toast.makeText(PaymentCalculatorActivity.this, str, Toast.LENGTH_SHORT)
                .show();
    }

    View.OnClickListener myonclick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.Scancode_qd:
                    result();
                    break;
                case R.id.Scancode_qx:
                    dia_Scancode.cancel();// 关闭弹出框
                    dia_ed_Scancode.setText("");
                    break;
                default:
                    break;
            }
        }
    };

    @SuppressWarnings("unused")
    private boolean checkResult(String paramString) {
        if ((paramString.contains("+")) || (paramString.contains("-"))
                || (paramString.contains("x")) || (paramString.contains("÷"))) {
        }
        while (paramString.endsWith(".")) {
            return true;
        }
        return false;
    }

    private boolean checkResultEndChar(String paramString) {
        return (paramString.endsWith(".")) || (paramString.endsWith("+"))
                || (paramString.endsWith("-")) || (paramString.endsWith("x"))
                || (paramString.endsWith("÷"));
    }

    @SuppressWarnings("unused")
    private boolean checkZeroPosition(String paramString) {
        return (paramString.length() == 0) || ("0".equals(paramString));
    }

    private void clearStringBuffer(StringBuffer paramStringBuffer) {
        paramStringBuffer.delete(0, paramStringBuffer.toString().length());
    }

    private void getResult() {
        String str1 = this.showString.toString();
        Object localObject = new BigDecimal(0.0D);
        String[] arrayOfString = new String[2];

        String str2 = "";
        int i = 0;

        for (; ; ) {
            try {
                if (i >= this.symbol.length) {

                    return;
                } else {
                    int j = str1.lastIndexOf(this.symbol[i]);
                    if ((!str1.contains(this.symbol[i])) || (j <= 0)) {
                        i++;
                        continue;
                    }
                    arrayOfString[0] = str1.substring(0, j);
                    arrayOfString[1] = str1.substring(j + 1, str1.length());
                    str2 = this.symbol[i];

                    if (arrayOfString[1].length() <= 0) {
                        return;
                    }

                    BigDecimal localBigDecimal1 = new BigDecimal(
                            arrayOfString[0]);
                    BigDecimal localBigDecimal2 = new BigDecimal(
                            arrayOfString[1]);
                    MathContext localMathContext = new MathContext(4,
                            RoundingMode.HALF_UP);

                    if ("+".equals(str2)) {
                        localObject = localBigDecimal1.add(localBigDecimal2,
                                localMathContext);
                        clearStringBuffer(this.showString);
                        this.showString.append(localObject);
                    } else if ("-".equals(str2)) {
                        localObject = localBigDecimal1.subtract(
                                localBigDecimal2, localMathContext);
                    } else if ("x".equals(str2)) {
                        localObject = localBigDecimal1.multiply(
                                localBigDecimal2, localMathContext);
                    } else if ("÷".equals(str2)) {
                        BigDecimal localBigDecimal3 = localBigDecimal1.divide(
                                localBigDecimal2, localMathContext);
                        localObject = localBigDecimal3;
                    }

                    clearStringBuffer(this.showString);

                    this.showString.append(localObject.toString());
                    return;
                }

            } catch (Exception localException) {
                localException.printStackTrace();
                clearStringBuffer(this.showString);
                this.showString.append(0);
                return;
            }
        }
    }

    private void initSoundData() {
        try {
            this.pool = new SoundPool(2, 3, 6);
            this.pool.load(this, R.raw.ac, 1);
            this.pool.load(this, R.raw.div, 1);
            this.pool.load(this, R.raw.mul, 1);
            this.pool.load(this, R.raw.zero, 1);
            this.pool.load(this, R.raw.one, 1);
            this.pool.load(this, R.raw.two, 1);
            this.pool.load(this, R.raw.three, 1);
            this.pool.load(this, R.raw.four, 1);
            this.pool.load(this, R.raw.five, 1);
            this.pool.load(this, R.raw.six, 1);
            this.pool.load(this, R.raw.seven, 1);
            this.pool.load(this, R.raw.eight, 1);
            this.pool.load(this, R.raw.nine, 1);
            this.pool.load(this, R.raw.minus, 1);
            this.pool.load(this, R.raw.plus, 1);
            this.pool.load(this, R.raw.point, 1);
            this.pool.load(this, R.raw.del, 1);
            return;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private void initView() {
        this.btn_clean = ((Button) findViewById(R.id.payment__clean_btn));
        this.btn_divide = ((Button) findViewById(R.id.payment_divide_btn));
        this.btn_multiply = ((Button) findViewById(R.id.payment_multiply_btn));
        this.btn_7 = ((Button) findViewById(R.id.payment_7_btn));
        this.btn_8 = ((Button) findViewById(R.id.payment_8_btn));
        this.btn_9 = ((Button) findViewById(R.id.payment_9_btn));
        this.btn_minus = ((Button) findViewById(R.id.payment_minus_btn));
        this.btn_4 = ((Button) findViewById(R.id.payment_4_btn));
        this.btn_5 = ((Button) findViewById(R.id.payment_5_btn));
        this.btn_6 = ((Button) findViewById(R.id.payment_6_btn));
        this.btn_plus = ((Button) findViewById(R.id.payment_plus_btn));
        this.btn_1 = ((Button) findViewById(R.id.payment_1_btn));
        this.btn_2 = ((Button) findViewById(R.id.payment_2_btn));
        this.btn_3 = ((Button) findViewById(R.id.payment_3_btn));
        this.btn_0 = ((Button) findViewById(R.id.payment_0_btn));
        this.btn_point = ((Button) findViewById(R.id.payment_point_btn));

        this.result = ((TextView) findViewById(R.id.payment_result));
        this.confirm = ((TextView) findViewById(R.id.payment_confirm_textview));
        this.btn_confirm = ((LinearLayout) findViewById(R.id.payment_confirm));

        this.btn_delete = ((LinearLayout) findViewById(R.id.payment_delete_btn));

        initSoundData();
    }

    private boolean isContainsOp() {
        for (int i = 0; i < showString.length(); i++) {
            if (i >= this.symbol.length) {
                return false;
            }
            if ((this.showString.toString().contains(this.symbol[i]))
                    && (this.showString.toString().lastIndexOf(this.symbol[i]) > 0)
                    && (!this.showString.toString().endsWith(this.symbol[i]))) {
                return true;
            }
        }
        return false;
    }

    private void setListeners() {
        this.btn_clean.setOnClickListener(this);
        this.btn_divide.setOnClickListener(this);
        this.btn_multiply.setOnClickListener(this);
        this.btn_7.setOnClickListener(this);
        this.btn_8.setOnClickListener(this);
        this.btn_9.setOnClickListener(this);
        this.btn_minus.setOnClickListener(this);
        this.btn_4.setOnClickListener(this);
        this.btn_5.setOnClickListener(this);
        this.btn_6.setOnClickListener(this);
        this.btn_plus.setOnClickListener(this);
        this.btn_1.setOnClickListener(this);
        this.btn_2.setOnClickListener(this);
        this.btn_3.setOnClickListener(this);
        this.btn_0.setOnClickListener(this);
        this.btn_point.setOnClickListener(this);
        this.result.setOnClickListener(this);
        this.btn_delete.setOnClickListener(this);
        this.btn_confirm.setOnClickListener(this);

    }

    @SuppressWarnings("unused")
    private void show() {
        String str = this.showString.toString();
        if ((str.length() > 1) && (str.startsWith("0"))
                && (!str.startsWith("0."))) {
            clearStringBuffer(this.showString);
            this.showString.append(str.substring(1));
        }
        if ((this.showString.toString().contains("e"))
                || (this.showString.toString().contains("E"))) {
            clearStringBuffer(this.showString);
            this.showString.append("0");

        }
        if ((UtilTool.isNumeric(this.showString.toString()))
                && (this.showString.toString().length() > 8)) {
            clearStringBuffer(this.showString);
            this.showString.append("0");
        }
        this.result.setText(this.showString.toString());
    }

    @SuppressWarnings("unused")
    private void soundPlay(int paramInt) {
        try {

            this.pool.play(paramInt, 1.0F, 1.0F, 0, 0, 1.0F);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onStart() {
        TitleBar.setActivity(this);
        super.onStart();
    }

    private boolean isNumKey(View paramView) {
        switch (paramView.getId()) {
            case R.id.payment_0_btn:
            case R.id.payment_1_btn:
            case R.id.payment_2_btn:
            case R.id.payment_3_btn:
            case R.id.payment_4_btn:
            case R.id.payment_5_btn:
            case R.id.payment_6_btn:
            case R.id.payment_7_btn:
            case R.id.payment_8_btn:
            case R.id.payment_9_btn:
            case R.id.payment_point_btn:
                return true;
        }
        return false;
    }

    public void onClick(View paramView) {
        try {
            // 长度太长的数字,格式 化
            if (UtilTool.isNumeric(this.showString.toString())
                    && isNumKey(paramView)) {
                String str1 = this.showString.toString();
                String[] strArray = str1.split("[.]");
                // 小数点太多了
                if (strArray.length > 2)
                    return;
                if (strArray.length >= 2 && strArray[1].length() >= 2) {
                    return;
                }
            }
            if (UtilTool.isNumeric(this.showString.toString())
                    && this.showString.length() >= 8
                    && paramView.getId() != R.id.payment_delete_btn
                    && paramView.getId() != R.id.payment__clean_btn) {
                Toast.makeText(this, "输入的金额太大", Toast.LENGTH_LONG).show();
                return;
            }
            switch (paramView.getId()) {
                case R.id.payment_0_btn:
                    showString.append(0);
                    break;
                case R.id.payment_1_btn:
                    showString.append(1);
                    break;
                case R.id.payment_2_btn:
                    showString.append(2);
                    break;
                case R.id.payment_3_btn:
                    showString.append(3);
                    break;
                case R.id.payment_4_btn:
                    showString.append(4);
                    break;
                case R.id.payment_5_btn:
                    showString.append(5);
                    break;
                case R.id.payment_6_btn:
                    showString.append(6);
                    break;
                case R.id.payment_7_btn:
                    showString.append(7);
                    break;
                case R.id.payment_8_btn:
                    showString.append(8);
                    break;
                case R.id.payment_9_btn:
                    showString.append(9);
                    break;
                case R.id.payment_delete_btn:
                    if (showString.length() > 0) {
                        showString.delete(showString.length() - 1,
                                showString.length());
                    }
                    break;
                case R.id.payment_divide_btn:
                    if (showString.length() > 0) {
                        getResult();
                        showString.append("÷");
                    }
                    break;
                case R.id.payment__clean_btn:
                    showString.delete(0, showString.length());
                    setRText("");
                    break;
                case R.id.payment_multiply_btn:
                    if (showString.length() > 0) {
                        getResult();
                        showString.append("x");
                    }
                    break;
                case R.id.payment_minus_btn:
                    if (showString.length() > 0) {
                        getResult();
                        showString.append("-");
                    }
                    break;
                case R.id.payment_plus_btn:
                    if (showString.length() > 0) {
                        getResult();
                        showString.append("+");
                    }
                    break;
                case R.id.payment_point_btn:
                    if (showString.length() > 0) {
                        showString.append(".");
                    }
                    break;
                case R.id.payment_confirm:
                    /**
                     * 跳转支付按钮
                     */
                    if ("=".equals(this.confirm.getText().toString().trim())) {
                        if (showString.toString().length() > 0
                                && !checkResultEndChar(this.showString.toString())) {
                            if (isContainsOp()) {
                                getResult();
                            }
                            String str2 = this.showString.toString();
                            if ((str2.contains("."))
                                    && (str2.substring(1 + str2.indexOf("."))
                                    .length() > 2)) {
                                Object[] arrayOfObject2 = new Object[1];
                                arrayOfObject2[0] = Double.valueOf(Double
                                        .parseDouble(str2));
                                String str3 = String.format("%.2f", arrayOfObject2);
                                this.showString.delete(0, this.showString.length());
                                this.showString.append(str3);
                            }
                            this.confirm.setText("确 认");
                        }

                    } else if ("确 认".equals(this.confirm.getText().toString()
                            .trim())) {
                        if (UtilTool.isNumeric(this.showString.toString())) {
                            if (Double.parseDouble(this.showString.toString()) > 0.0D) {
                                // Intent localIntent = new Intent(this,
                                // PaymentMainActivity.class);
                                String str1 = this.showString.toString();
                                if (str1.substring(1 + str1.indexOf(".")).length() > 2) {
                                    Object[] arrayOfObject1 = new Object[1];
                                    arrayOfObject1[0] = Double.valueOf(Double
                                            .parseDouble(str1));
                                    str1 = String.format("%.2f", arrayOfObject1);
                                    clearStringBuffer(this.showString);
                                    this.showString.append(str1);

                                }
                                // localIntent.putExtra("TotalMoney", str1);
                                //
                                // JSONArray shopsJson = new JSONArray();
                                // JSONObject shopJson = new JSONObject();
                                // shopJson.put(ConsumeListView.list_id_name,
                                // "快速支付 ");
                                // shopJson.put(ConsumeListView.list_id_amount,"1");
                                // shopJson.put(ConsumeListView.list_id_money,
                                // str1);
                                // shopJson.put("price", str1 );
                                // shopJson.put("id", "0");
                                //
                                // shopsJson.put(shopJson);
                                //
                                // localIntent.putExtra(PaymentResultActivity.IntentPayDetails,
                                // shopsJson.toString());
                                // localIntent.putExtra(
                                // PaymentResultActivity.IntentOperater, 1);
                                // // ((NewBaseActivityGroup)
                                // // getParent()).startSubActivity(
                                // // CashierAct.class, localIntent, true);
                                // startActivity(localIntent);

                                Integer value = (int) ((Double.parseDouble(str1) * 100));
                                str_monery = value.toString();
                                // 翼开店付款
                                pay_Yikaidian(str_monery);
                            }
                        }
                    }

                    break;
            }

            if (!UtilTool.isNumeric(this.showString.toString())) {
                confirm.setText("=");
            } else {
                confirm.setText("确 认");
            }

            setRText(showString.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1: {
                    // 关闭ProgressDialog
                    setContentView(R.layout.payment_calculator);
                    initView();
                    setListeners();
                    // 预加载数据
                    data_qy();
                    // 初始化控件
                    dia_();
                    // 初始化赋值
                    if (!TextUtils.isEmpty(money_mode)) {
                        showString.append(Double.toString(Double
                                .parseDouble(Str_money_num.replaceAll("\\.\\d+$",
                                        "")) / 100));
                        setRText(Double.toString(Double.parseDouble(Str_money_num
                                .replaceAll("\\.\\d+$", "")) / 100));
                    }

                    UtilTool.checkLoginStatusAndReturnLoginActivity(
                            PaymentCalculatorActivity.this, null);
                    break;
                }
            }
        }
    };

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        isLandScreen = su.getPrefboolean(PaymentCalculatorActivity.this, "isLandScreen", false);
        if (isLandScreen)
            Util_Screen.screenToFullOrLand(this, true, isLandScreen);
        // 状态栏
        TitleBar.setTitleBar(this, "返回", "输入金额", "", null);

        setContentView(R.layout.loading);
        mContext = this;
        listener = this;
        AidlController.getInstance().startScanService(mContext, listener);

        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(100);
                    Message localMessage = new Message();
                    localMessage.what = 1;
                    handler.sendMessage(localMessage);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (scanService != null) {
            this.unbindService(scanConn);
            scanService = null;
            scanConn = null;
        }
        if (pool != null)
            this.pool.release();
        isClear = false;
        setRText("");
        showString.delete(0, showString.length());
    }

    protected void onResume() {
        super.onResume();
        setTitle("输入金额");
        setRText("");
        showString.delete(0, showString.length());
    }

    private void setRText(String str) {
        if (null != result)
            result.setText(str);
    }
}
