package com.ven.pos;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.basewin.services.ServiceManager;
import com.cnyssj.paytypedialog.MultiChoicDialog;
import com.cnyssj.pos.R;
import com.jhj.Agreement.ZYB.Http_PushTask;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.jhj.print.Print;
import com.jhj.print.Print_HuiYinPos;
import com.unionpay.cloudpos.DeviceException;
import com.unionpay.cloudpos.POSTerminal;
import com.unionpay.cloudpos.printer.PrinterDevice;
import com.ven.pos.Payment.PaymentResultActivity;
import com.ven.pos.Util.CTelephoneInfo;
import com.ven.pos.Util.ReflectUtil;
import com.ven.pos.Util.Util_Screen;
import com.ven.pos.alipay.util.Networkstate;
import com.ven.pos.presention.Presentation;
import com.ven.pos.updata.UpdateService;
import com.ven.pos.updata.Version_util;

/**
 * 个人中心
 */
public class PersonCenterActivity extends Activity {
    /**
     * shezhi_paykey:终端号 shezhi_shh:商戶号 shezhi_datakey:商户名 guanyu:关于我们
     * mode_pay_type:设置支付类型
     */
    TextView shezhi_paykey, shezhi_shh, shezhi_datakey, guanyu, banbencc, mode,
            print_tx, on_off_tx, mode_monery, shezhi_money, line_money,
            data_updata, on_off_goods, line_goods, mode_pay_type, shezhi_openVS;

    Button datakey_qx, paykey_qd, shh_qx, shh_qd, dkey_qx, dkey_qd, UP_data_qx,
            UP_data_qd, qx_money, qd_money;
    Dialog dia_paykey, dia_shh, dia_dkey, dia_mode, dia_about, dia_update,
            dia_money, dia_money_num, dia_vs;
    EditText et_paykey, et_shh, et_dkey, et_money;
    // 设备终端号，收银员名称，商户号
    private String clientId, cashierName, merchantId, Str_mode, print;
    SharedPreferences_util su = new SharedPreferences_util();
    RadioButton RB_camera, RB_Barcode, RB_fixed, RB_input, RB_operVS, RB_closeVS;
    boolean on_off_m, merchantId_type, on_off_m_goods;
    Version_util Vn = new Version_util();
    TextView et_UP_data;
    // 升级相关
    String result;
    boolean force_update;
    String url;
    String description;
    String Str_money, Str_money_num;
    // 错误信息
    String error_msg, manager_num;
    JSONObject js;
    //多选框
    private MultiChoicDialog mMultiChoicDialog;

    //副屏
    private DisplayManager mDisplayManager;
    private Presentation presentation;
    private Display[] displays;
    boolean isLandScreen, isOpenVS;

    PrinterDevice device = null;

    @SuppressWarnings("static-access")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化数据库
        // init data base
        // DataBaseManager.getInstance().init(getApplicationContext());
        // 初始化service manager
        // init service manager
        // ServiceManager.getInstence().init(getApplicationContext());

        isLandScreen = su.getPrefboolean(PersonCenterActivity.this, "isLandScreen", false);
        Util_Screen.screenToFullOrLand(this, true, isLandScreen);

        TitleBar.setTitleBar(this, "返回", "个人中心", "", null);
        setContentView(R.layout.person_center);
        try {
            if (device == null) {
                device = (PrinterDevice) POSTerminal.getInstance(PersonCenterActivity.this)
                        .getDevice("cloudpos.device.printer");
                device.open();
            }
        } catch (DeviceException e) {
            e.printStackTrace();
        }
        //获取设备
        updateContents();

        // 商户号
        merchantId = su.getPrefString(PersonCenterActivity.this, "merchantId",
                null);
        // 商户名称
        cashierName = su.getPrefString(PersonCenterActivity.this,
                "cashiername", null);
        // 设备终端号
        clientId = su
                .getPrefString(PersonCenterActivity.this, "clientId", null);
        //副屏
        isOpenVS = su.getPrefboolean(PersonCenterActivity.this, "isOpenVS", false);

        Str_mode = su.getPrefString(PersonCenterActivity.this, "mode", null);
        print = su.getPrefString(PersonCenterActivity.this, "print", null);
        on_off_m = su
                .getPrefboolean(PersonCenterActivity.this, "on_off", false);
        on_off_m_goods = su.getPrefboolean(PersonCenterActivity.this,
                "on_off_goods", false);
        Str_money = su.getPrefString(PersonCenterActivity.this, "money", null);
        Str_money_num = su.getPrefString(PersonCenterActivity.this,
                "Str_money_num", "1000");

        Context context = PersonCenterActivity.this;
        // 打印最后一次
        print_tx = (TextView) findViewById(R.id.print);
        print_tx.setOnClickListener(mylistener);
        // 收银员名称
        shezhi_datakey = (TextView) findViewById(R.id.shezhi_datakey);
        shezhi_datakey.setOnClickListener(mylistener);
        dia_dkey = new Dialog(context, R.style.edit_AlertDialog_style);
        dia_dkey.setContentView(R.layout.activity_start_dialog_cashiername);
        dkey_qx = (Button) dia_dkey.findViewById(R.id.Dkey_qx);
        dkey_qd = (Button) dia_dkey.findViewById(R.id.Dkey_qd);
        et_dkey = (EditText) dia_dkey.findViewById(R.id.et_dkey);
        dkey_qx.setOnClickListener(mylistener);
        dkey_qd.setOnClickListener(mylistener);

        // 选择扫码模式
        dia_mode = new Dialog(context, R.style.edit_AlertDialog_style);
        dia_mode.setContentView(R.layout.activity_start_dialog_mode);
        RB_camera = (RadioButton) dia_mode.findViewById(R.id.RB_camera);
        RB_Barcode = (RadioButton) dia_mode.findViewById(R.id.RB_Barcode);
        RB_camera.setOnClickListener(mylistener);
        RB_Barcode.setOnClickListener(mylistener);
        mode = (TextView) findViewById(R.id.mode);
        mode.setOnClickListener(mylistener);
        if (!TextUtils.isEmpty(Str_mode)) {
            RB_Barcode.setChecked(true);
        } else {
            RB_camera.setChecked(true);
        }
        // 设备终端号
        shezhi_paykey = (TextView) findViewById(R.id.shezhi_paykey);
        shezhi_paykey.setOnClickListener(mylistener);
        dia_paykey = new Dialog(context, R.style.edit_AlertDialog_style);
        dia_paykey.setContentView(R.layout.activity_start_dialog_dtn);
        et_paykey = (EditText) dia_paykey.findViewById(R.id.et_paykey);
        datakey_qx = (Button) dia_paykey.findViewById(R.id.datakey_qx);
        paykey_qd = (Button) dia_paykey.findViewById(R.id.paykey_qd);
        datakey_qx.setOnClickListener(mylistener);
        paykey_qd.setOnClickListener(mylistener);

        /**
         * 商户号
         * */
        shezhi_shh = (TextView) findViewById(R.id.shezhi_shh);
        shezhi_shh.setOnClickListener(mylistener);
        dia_shh = new Dialog(context, R.style.edit_AlertDialog_style);
        dia_shh.setContentView(R.layout.activity_start_dialog_merchantid);
        shh_qx = (Button) dia_shh.findViewById(R.id.shh_qx);
        shh_qd = (Button) dia_shh.findViewById(R.id.shh_qd);
        et_shh = (EditText) dia_shh.findViewById(R.id.et_email);
        shh_qx.setOnClickListener(mylistener);
        shh_qd.setOnClickListener(mylistener);
        // SpannableStringBuilder builder = new
        // SpannableStringBuilder(shezhi_shh
        // .getText().toString());
        // ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.BLACK);
        // ForegroundColorSpan whiteSpan = new ForegroundColorSpan(Color.GRAY);
        // builder.setSpan(redSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // builder.setSpan(whiteSpan, 4, 8, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        // shezhi_shh.setText(builder);

        /**
         * 设置副屏
         */
        shezhi_openVS = (TextView) findViewById(R.id.shezhi_openVS);
        shezhi_openVS.setOnClickListener(mylistener);
        dia_vs = new Dialog(context, R.style.edit_AlertDialog_style);
        dia_vs.setContentView(R.layout.activity_start_dialog_vs);
        RB_operVS = (RadioButton) dia_vs.findViewById(R.id.RB_operVS);
        RB_closeVS = (RadioButton) dia_vs.findViewById(R.id.RB_closeVS);
        RB_operVS.setOnClickListener(mylistener);
        RB_closeVS.setOnClickListener(mylistener);
        if (isOpenVS)
            RB_operVS.setChecked(true);
        else
            RB_closeVS.setChecked(true);


        /**
         * 关于
         * */
        dia_about = new Dialog(context, R.style.edit_AlertDialog_style);
        dia_about.setContentView(R.layout.activity_start_dialog_about);

        guanyu = (TextView) findViewById(R.id.about);
        guanyu.setOnClickListener(mylistener);

        // 更新参数
        data_updata = (TextView) findViewById(R.id.data_updata);
        data_updata.setOnClickListener(mylistener);
        /**
         * 版本检测
         * */
        banbencc = (TextView) findViewById(R.id.xinbanben);
        banbencc.setOnClickListener(mylistener);
        dia_update = new Dialog(context, R.style.edit_AlertDialog_style);
        dia_update.setContentView(R.layout.activity_start_dialog_update);
        et_UP_data = (TextView) dia_update.findViewById(R.id.et_UP_data);
        UP_data_qx = (Button) dia_update.findViewById(R.id.UP_data_qx);
        UP_data_qd = (Button) dia_update.findViewById(R.id.UP_data_qd);
        UP_data_qx.setOnClickListener(mylistener);
        UP_data_qd.setOnClickListener(mylistener);
        /**
         * 设定余额相关
         * */
        // 模式
        mode_monery = (TextView) findViewById(R.id.mode_monery);
        mode_monery.setOnClickListener(mylistener);
        dia_money = new Dialog(context, R.style.edit_AlertDialog_style);
        dia_money.setContentView(R.layout.activity_start_dialog_money_mode);
        RB_fixed = (RadioButton) dia_money.findViewById(R.id.RB_fixed);
        RB_input = (RadioButton) dia_money.findViewById(R.id.RB_input);
        RB_fixed.setOnClickListener(mylistener);
        RB_input.setOnClickListener(mylistener);
        line_money = (TextView) findViewById(R.id.line_money);
        // 输入金额相关
        shezhi_money = (TextView) findViewById(R.id.shezhi_money);
        shezhi_money.setOnClickListener(mylistener);
        dia_money_num = new Dialog(context, R.style.edit_AlertDialog_style);
        dia_money_num.setContentView(R.layout.activity_start_dialog_monrynum);
        qx_money = (Button) dia_money_num.findViewById(R.id.qx_money);
        qd_money = (Button) dia_money_num.findViewById(R.id.qd_money);
        et_money = (EditText) dia_money_num.findViewById(R.id.et_money);
        qx_money.setOnClickListener(mylistener);
        qd_money.setOnClickListener(mylistener);
        if (TextUtils.isEmpty(Str_money)) {
            shezhi_money.setVisibility(View.GONE);
            line_money.setVisibility(View.GONE);
            RB_input.setChecked(true);

        } else {
            if (!TextUtils.isEmpty(Str_money_num)) {
                shezhi_money.setText("金额：" + settextnum(Str_money_num));
            }
            RB_fixed.setChecked(true);
        }
        // 设置支付方式
        mode_pay_type = (TextView) findViewById(R.id.mode_pay_type);
        mode_pay_type.setOnClickListener(mylistener);
        initDialog();

        // 商品打印状态
        on_off_goods = (TextView) findViewById(R.id.on_off_goods);
        line_goods = (TextView) findViewById(R.id.line_goods);
        on_off_goods.setOnClickListener(mylistener);
        /**
         * 打印机状态
         * */
        on_off_tx = (TextView) findViewById(R.id.on_off);
        on_off_tx.setOnClickListener(mylistener);
        if (on_off_m) {
            on_off_tx.setText(col_tx("打印机状态：已开启", 6));

            on_off_goods.setVisibility(View.VISIBLE);
            line_goods.setVisibility(View.VISIBLE);
            if (on_off_m_goods) {
                on_off_goods.setText(col_tx("商品打印状态：已开启", 7));
            } else {
                on_off_goods.setText(col_tx("商品打印状态：已关闭", 7));
            }
        } else {
            on_off_tx.setText(col_tx("打印机状态：已关闭", 6));
            on_off_goods.setVisibility(View.GONE);
            line_goods.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(clientId)) {
            shezhi_paykey.setText("设备终端号: " + clientId);
        } else {
            shezhi_paykey.setText("设备终端号 ");
        }
        if (!TextUtils.isEmpty(cashierName)) {
            shezhi_datakey.setText("商户名称: " + cashierName);
        } else {
            shezhi_datakey.setText("商户名称 ");
        }
        if (!TextUtils.isEmpty(merchantId)) {
            merchantId_type = true;
            shezhi_shh.setText("商户号: " + merchantId);
        } else {
            merchantId_type = false;
            shezhi_shh.setText("商户号 ");
        }
        String imeiSIM1 = null;
        TextView ms_ID = (TextView) findViewById(R.id.ms_ID);
        if (Build.MANUFACTURER.contains("BASEWIN")
                || Build.MANUFACTURER.contains("basewin")) {
            try {
                imeiSIM1 = ServiceManager.getInstence().getDeviceinfo().getSN();
            } catch (RemoteException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else {
            CTelephoneInfo telephonyInfo = CTelephoneInfo.getInstance(this);
            telephonyInfo.setCTelephoneInfo();
            imeiSIM1 = telephonyInfo.getImeiSIM1();
            if (TextUtils.isEmpty(imeiSIM1))
                imeiSIM1 = Build.SERIAL;
            ms_ID.setText("设备识别号:" + imeiSIM1);
        }
        ms_ID.setText("设备识别号:" + imeiSIM1);
    }

    // 获取显示设备。
    public void updateContents() {
        mDisplayManager = (DisplayManager) getSystemService(
                Context.DISPLAY_SERVICE);
        mDisplayManager.registerDisplayListener(mDisplayListener, null);
        displays = mDisplayManager.getDisplays();
        Log.d("主屏", "获取设备" + displays.length);
    }

    private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener() {
        public void onDisplayAdded(int displayId) {

            Log.d("主屏", "onDisplayAdded");

        }

        public void onDisplayChanged(int displayId) {

            Log.d("主屏", "onDisplayChanged");

        }

        public void onDisplayRemoved(int displayId) {

            Log.d("主屏", "onDisplayRemoved");

        }
    };

    View.OnClickListener mylistener = new View.OnClickListener() {
        Window w;
        WindowManager.LayoutParams lp;

        @SuppressWarnings("static-access")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.shezhi_datakey:
                    dia_dkey.show();
                    dia_dkey.setCanceledOnTouchOutside(false);
                    w = dia_dkey.getWindow();
                    lp = w.getAttributes();
                    lp.x = 0;
                    lp.y = 40;
                    dia_dkey.onWindowAttributesChanged(lp);
                    break;
                case R.id.shezhi_paykey:
                    // 设置
                    toast("暂不支持更改！");
                    // dia_paykey.show();
                    // dia_paykey.setCanceledOnTouchOutside(false);
                    // w = dia_paykey.getWindow();
                    // lp = w.getAttributes();
                    // lp.x = 0;
                    // lp.y = 40;
                    // dia_paykey.onWindowAttributesChanged(lp);

                    break;
                case R.id.shezhi_shh:
                    // 商户号
                    toast("暂不支持更改！");
                    // dia_shh.show();
                    // dia_shh.setCanceledOnTouchOutside(false);
                    // w = dia_shh.getWindow();
                    // lp = w.getAttributes();
                    // lp.x = 0;
                    // lp.y = 40;
                    // dia_shh.onWindowAttributesChanged(lp);

                    break;
                case R.id.shezhi_openVS:
                    dia_vs.show();
                    dia_vs.setCanceledOnTouchOutside(false);
                    w = dia_vs.getWindow();
                    lp = w.getAttributes();
                    lp.x = 0;
                    lp.y = 40;
                    dia_vs.onWindowAttributesChanged(lp);
                    break;
                case R.id.RB_operVS:
                    if (displays.length > 1) {
                        isOpenVS = true;
                        su.setPrefboolean(PersonCenterActivity.this, "isOpenVS", isOpenVS);
                        dia_vs.cancel();
                    } else {
                        toast("未检测到副屏，请检查HDMI线是否连接副屏！");
                    }
                    break;
                case R.id.RB_closeVS:
                    isOpenVS = false;
                    su.setPrefboolean(PersonCenterActivity.this, "isOpenVS", isOpenVS);
                    dia_vs.cancel();
                    break;

                case R.id.xinbanben:
                    showmessage(1);

                    break;
                case R.id.UP_data_qx:
                    if (!force_update) {
                        dia_update.cancel();
                    } else {
                        toast("大哥，新版本可美了，你得升级啊！");
                    }

                    break;
                case R.id.UP_data_qd:
                    dia_update.cancel();
                    // 跳转service下载程序
                    Intent intent = new Intent(PersonCenterActivity.this,
                            UpdateService.class);
                    intent.putExtra("Key_App_Name", "翼开店超市版");
                    intent.putExtra("Key_Down_Url", url);
                    startService(intent);
                    break;
                case R.id.data_updata:
                    // 更新参数
                    showmessage(5);
                    break;
                case R.id.datakey_qx:
                    dia_paykey.cancel();// 关闭弹出框
                    et_paykey.setText("");
                    break;
                case R.id.paykey_qd:
                    clientId = String.valueOf(et_paykey.getText());

                    su.setPrefString(PersonCenterActivity.this, "clientId",
                            clientId);
                    shezhi_paykey.setText("设备终端号：" + clientId);
                    dia_paykey.cancel();// 关闭弹出框

                    break;
                case R.id.shh_qx:
                    dia_shh.cancel();// 关闭弹出框
                    et_shh.setText("");
                    break;
                case R.id.shh_qd:
                    toast("暂不支持更改！");
                    // merchantId = String.valueOf(et_shh.getText());
                    // if (!TextUtils.isEmpty(merchantId) && merchantId != "") {
                    // if (merchantId_type) {
                    // String num = et_shh.getText().toString();
                    // if (num.substring(0, 4).equals("1027")) {
                    // shezhi_shh.setText("商户号："
                    // + num.substring(4, num.length()));
                    // su.setPrefString(SettingActivity.this,
                    // "merchantId",
                    // num.substring(4, num.length()));
                    // dia_shh.cancel();
                    // et_shh.setText("");
                    // } else {
                    // stoi("请输入正确的商户号编码");
                    // }
                    // } else {
                    // shezhi_shh
                    // .setText("商户号：" + et_shh.getText().toString());
                    // su.setPrefString(SettingActivity.this, "merchantId",
                    // et_shh.getText().toString());
                    // et_shh.setText("");
                    // dia_shh.cancel();
                    // }
                    //
                    // } else {
                    // stoi("请输入正确的商户号");
                    // }

                    break;

                case R.id.Dkey_qx:
                    dia_dkey.cancel();// 关闭弹出框
                    et_dkey.setText("");
                    break;
                case R.id.Dkey_qd:

                    String cn = String.valueOf(et_dkey.getText());
                    shezhi_datakey.setText("商户名称：" + cn);
                    su.setPrefString(PersonCenterActivity.this, "cashiername", cn);
                    et_dkey.setText("");
                    dia_dkey.cancel();// 关闭弹出框

                    break;
                case R.id.mode:
                    // 模式弹出框
                    dia_mode.show();
                    dia_mode.setCanceledOnTouchOutside(false);
                    w = dia_mode.getWindow();
                    lp = w.getAttributes();
                    lp.x = 0;
                    lp.y = 40;
                    dia_mode.onWindowAttributesChanged(lp);
                    break;
                case R.id.RB_camera:
                    // 相机扫码
                    su.setPrefString(PersonCenterActivity.this, "mode", null);
                    RB_camera.setChecked(true);
                    dia_mode.cancel();
                    break;
                case R.id.RB_Barcode:
                    // 扫码枪
                    su.setPrefString(PersonCenterActivity.this, "mode", "mode");
                    dia_mode.cancel();
                    break;
                case R.id.print:

                    try {
                        if (print != null) {
                            JSONObject jsonObject = new JSONObject(print);
                            String orderId = jsonObject.getString("orderId").toString();
                            String monery = jsonObject.getString("monery").toString();
                            String paycode = jsonObject.getString("paycode").toString();
                            String PaymentMethod = jsonObject.getString("PaymentMethod").toString();
                            String clientId = jsonObject.getString("clientId").toString();
                            String Time = jsonObject.getString("Time").toString();
                            String shanghm = jsonObject.getString("shanghm").toString();

                            if (Build.MANUFACTURER.contains("BASEWIN")
                                    || Build.MANUFACTURER.contains("basewin")) {

                                Print pt = new Print();
                                pt.prilay(PersonCenterActivity.this, orderId,
                                        monery, paycode, PaymentMethod, shanghm,
                                        clientId, Time);

                            } else if (Build.MANUFACTURER.contains("WIZAR")
                                    || Build.MANUFACTURER.contains("wizar")
                                    || ReflectUtil.getWizarType()) {

                                try {
                                    if (device.queryStatus() == PrinterDevice.STATUS_OUT_OF_PAPER) {
                                        toast("打印机缺纸！");
                                    } else {
                                        Print_HuiYinPos pthy = new Print_HuiYinPos();
                                        pthy.prilay(PersonCenterActivity.this, orderId,
                                                monery, paycode, PaymentMethod, shanghm,
                                                clientId, Time);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            } else {
                                toast(Build.MANUFACTURER.toString());
                                toast("未检测到打印机！无法进行打印打印");
                            }

                        } else {
                            toast("没有有效的交易数据");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.about:
                    dia_about.setCancelable(true);
                    dia_about.show();
                    dia_about.setCanceledOnTouchOutside(true);
                    w = dia_about.getWindow();
                    lp = w.getAttributes();
                    lp.x = 0;
                    lp.y = 40;
                    dia_about.onWindowAttributesChanged(lp);
                    break;
                case R.id.on_off:
                    if (on_off_m) {
                        on_off_m = false;
                        su.setPrefboolean(PersonCenterActivity.this, "on_off",
                                on_off_m);
                        on_off_tx.setText(col_tx("打印机状态：已关闭", 6));
                        on_off_goods.setVisibility(View.GONE);
                        line_goods.setVisibility(View.GONE);

                    } else {
                        on_off_m = true;
                        su.setPrefboolean(PersonCenterActivity.this, "on_off",
                                on_off_m);
                        on_off_tx.setText(col_tx("打印机状态：已开启", 6));
                        on_off_goods.setVisibility(View.VISIBLE);
                        line_goods.setVisibility(View.VISIBLE);
                        if (on_off_m_goods) {
                            on_off_goods.setText(col_tx("商品打印状态：已开启", 7));
                        } else {
                            on_off_goods.setText(col_tx("商品打印状态：已关闭", 7));
                        }

                    }
                    break;
                case R.id.on_off_goods:
                    if (on_off_m_goods) {
                        on_off_m_goods = false;
                        su.setPrefboolean(PersonCenterActivity.this,
                                "on_off_goods", on_off_m_goods);
                        on_off_goods.setText(col_tx("商品打印状态：已关闭", 7));

                    } else {
                        on_off_m_goods = true;
                        su.setPrefboolean(PersonCenterActivity.this,
                                "on_off_goods", on_off_m_goods);
                        on_off_goods.setText(col_tx("商品打印状态：已开启", 7));

                    }

                    break;
                case R.id.mode_monery:
                    dia_money.show();
                    dia_money.setCanceledOnTouchOutside(false);
                    w = dia_money.getWindow();
                    lp = w.getAttributes();
                    lp.x = 0;
                    lp.y = 40;
                    dia_money.onWindowAttributesChanged(lp);
                    break;
                case R.id.RB_fixed:
                    shezhi_money.setVisibility(View.VISIBLE);
                    line_money.setVisibility(View.VISIBLE);
                    su.setPrefString(PersonCenterActivity.this, "money", "money");
                    shezhi_money.setText("金额：" + settextnum(Str_money_num));
                    RB_fixed.setChecked(true);
                    dia_money.cancel();
                    break;
                case R.id.RB_input:
                    shezhi_money.setVisibility(View.GONE);
                    line_money.setVisibility(View.GONE);
                    su.setPrefString(PersonCenterActivity.this, "money", null);
                    RB_input.setChecked(true);
                    dia_money.cancel();
                    break;
                case R.id.shezhi_money:
                    dia_money_num.show();
                    dia_money_num.setCanceledOnTouchOutside(false);
                    w = dia_money_num.getWindow();
                    lp = w.getAttributes();
                    lp.x = 0;
                    lp.y = 40;
                    dia_money_num.onWindowAttributesChanged(lp);
                    break;
                case R.id.qx_money:
                    et_money.setText("");
                    dia_money_num.cancel();
                    break;
                case R.id.qd_money:
                    if (!TextUtils.isEmpty(et_money.getText().toString())) {
                        shezhi_money.setText("金额：" + et_money.getText().toString());
                        su.setPrefString(PersonCenterActivity.this,
                                "Str_money_num",
                                Double.toString(Double.parseDouble(et_money
                                        .getText().toString()) * 100));
                        et_money.setText("");
                        dia_money_num.cancel();
                    } else {
                        toast("请输入正确的金额");
                    }

                    break;
                case R.id.mode_pay_type:

                    mMultiChoicDialog.show();
                    break;
                default:
                    break;
            }
        }
    };

    public void initDialog() {

        mMultiChoicDialog = new MultiChoicDialog(this);
        mMultiChoicDialog.setTitle("请选择支付方式！");
        mMultiChoicDialog
                .setOnOKButtonListener(new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean[] selItems = mMultiChoicDialog.getSelectItem();
                        int size = selItems.length;
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < size; i++) {
                            if (selItems[i]) {
                                stringBuffer.append(i);
                            }
                        }
                        su.setPrefString(getApplicationContext(), "mode_pay_type", stringBuffer.toString());

                    }

                });

    }

    // 发送handle
    public void showmessage(int message) {
        Message localMessage = new Message();
        localMessage.what = message;
        this.handler.sendMessage(localMessage);
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    (new UpdateVersion()).start();
                    break;
                case 2:
                    toast("版本一致，无需升级！");
                    break;
                case 3:
                    if (force_update) {
                        UP_data_qx
                                .setBackgroundResource(R.drawable.button_oncleck_item);
                    }
                    dia_update.setCancelable(false);
                    Window w;
                    WindowManager.LayoutParams lp;
                    dia_update.show();
                    dia_update.setCanceledOnTouchOutside(false);
                    w = dia_update.getWindow();
                    lp = w.getAttributes();
                    lp.x = 0;
                    lp.y = 40;
                    dia_update.onWindowAttributesChanged(lp);
                    et_UP_data.setText(description);
                    break;
                case 4:
                    toast(error_msg + "，请联系服务商！");
                    break;
                case 5:
                    data_up();
                    break;
                case 6:
                    (new downLoad()).start();
                    break;
                case 7:
                    toast("数据获取失败！");
                    break;
                case 8:
                    toast("获取商户号失败，请检查网络连接后重试!");
                    break;
                case 9:
                    try {
                        shezhi_shh.setText("商户号: " + js.getString("merchantId"));
                        shezhi_datakey.setText("商户名称: "
                                + js.getString("merchantName"));
                        shezhi_paykey.setText("设备终端号: "
                                + js.getString("deviceNumber"));
                        toast("参数更新成功");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return true; // 不允许返回
    }

    public void toast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public SpannableStringBuilder col_tx(String str, int num) {
        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.BLACK);
        ForegroundColorSpan whiteSpan = new ForegroundColorSpan(Color.BLUE);
        builder.setSpan(redSpan, 0, num, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(whiteSpan, num, str.length(),
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return builder;
    }

    class UpdateVersion extends Thread {
        @SuppressWarnings("static-access")
        @Override
        public void run() {
            try {
                Http_PushTask HP = new Http_PushTask();
                result = HP.execute(
                        "",
                        "http://cnyssj.net/auto_update/?" + "app_name="
                                + Vn.getAppInfo(PersonCenterActivity.this))
                        .get();
                js = new JSONObject(result);
                if (!TextUtils.isEmpty(result)) {
                    js.getString("version");
                    String version = Vn
                            .getVersionName(PersonCenterActivity.this);
                    if (version.equals(js.getString("version"))) {
                        showmessage(2);
                        return;
                    } else {
                        force_update = js.getBoolean("force_update");
                        url = js.getString("url");
                        description = js.getString("description");
                        showmessage(3);
                        return;
                    }
                } else {
                    toast("数据获取失败！");
                }
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void data_up() {
        if (Build.MANUFACTURER.contains("BASEWIN")
                || Build.MANUFACTURER.contains("basewin")
                || Build.MANUFACTURER.contains("WIZAR")
                || Build.MANUFACTURER.contains("wizar")
                || ReflectUtil.getWizarType()) {
            if (Networkstate.isNetworkAvailable(PersonCenterActivity.this)) {
                // manager_num = mn.manager();
                try {
                    manager_num = ServiceManager.getInstence().getDeviceinfo()
                            .getSN();
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                showmessage(6);
            } else {
                toast("无可用网络！请联网后重试！");
            }
        } else {
            toast("未能识别该设备！");
        }
    }

    class downLoad extends Thread {
        @SuppressWarnings("static-access")
        @Override
        public void run() {

            Http_PushTask HP = new Http_PushTask();
            try {
                String result = HP.execute(
                        "",
                        "http://121.41.113.42/ykdconfig/?" + "app_key="
                                + manager_num)
                        .get(10000, TimeUnit.MILLISECONDS);
                js = new JSONObject(result);
                // onToast(result);
                if (!TextUtils.isEmpty(result)) {
                    if (js.getBoolean("success")) {

                        su.setPrefString(PersonCenterActivity.this,
                                "merchantId", js.getString("merchantId"));

                        su.setPrefString(PersonCenterActivity.this, "key",
                                js.getString("key"));
                        // 商户名称
                        su.setPrefString(PersonCenterActivity.this,
                                "cashiername", js.getString("merchantName"));
                        su.setPrefString(PersonCenterActivity.this, "clientId",
                                js.getString("deviceNumber"));
                        showmessage(9);
                    } else {
                        error_msg = js.getString("error_msg");
                        showmessage(4);
                    }
                } else {
                    showmessage(7);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                showmessage(8);
                e.printStackTrace();
                return;
            }

        }
    }

    public String settextnum(String num) {
        if (!TextUtils.isEmpty(num)) {
            Double money = Double.parseDouble(num) / 100;
            return Double.toString(money);
        } else {
            return null;
        }
    }
}