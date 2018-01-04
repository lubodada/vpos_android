package com.jhj.print;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Button;
import android.widget.Toast;

import com.basewin.aidl.OnPrinterListener;
import com.basewin.define.FontsType;
import com.basewin.services.ServiceManager;
import com.cnyssj.pos.R;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.unionpay.cloudpos.DeviceException;
import com.unionpay.cloudpos.POSTerminal;
import com.unionpay.cloudpos.printer.Format;
import com.unionpay.cloudpos.printer.PrinterDevice;
import com.ven.pos.Util.BitmapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/1/4 0004.
 */

public class Print_HuiYinPos {

    SharedPreferences_util su = new SharedPreferences_util();

    Context context;
    String orderId;
    String monery;
    String paycode;
    String PaymentMethod;
    String clientId;
    String Time;
    Dialog print_dia;
    Button Pring_qd, Pring_again;
    String cashiername;

    @SuppressWarnings("static-access")
    @SuppressLint("SimpleDateFormat")
    /**
     * @MerchantName 商户名称
     * @outOrderId 订单号
     * @monery 金额
     * @paycode 付款码
     * @PaymentMethod 付款方式
     * @TerminalNumber 终端号
     * @Time 时间
     * */
    public void prilay(Context context, String orderId, String monery,
                       String paycode, String PaymentMethod, String cashierName,
                       String clientId, String Time) {

        this.context = context;
        this.orderId = orderId;
        this.monery = monery;
        this.paycode = paycode;
        this.PaymentMethod = PaymentMethod;
        this.clientId = clientId;
        this.Time = Time;
        this.cashiername = cashierName;

        try {
            JSONObject bodyjson = new JSONObject();
            bodyjson.put("orderId", orderId);
            bodyjson.put("monery", monery);
            bodyjson.put("paycode", paycode);
            bodyjson.put("PaymentMethod", PaymentMethod);
            bodyjson.put("clientId", clientId);
            bodyjson.put("Time", Time);
            bodyjson.put("shanghm", cashiername);

            // 保存数据
            su.setPrefString(context, "print", bodyjson.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 弹出框

        new_print(context, orderId, cashiername, monery, paycode,
                PaymentMethod, clientId, Time);

    }

    public void new_print(Context context, String orderId, String cashiername,
                          String monery, String paycode, String PaymentMethod,
                          String merchantnum, String Time) {
        if (su.getPrefboolean(context, "on_off", false)) {
            try {
                PrinterDevice device = (PrinterDevice) POSTerminal.getInstance(context)
                        .getDevice("cloudpos.device.printer");
                try {

                    Format format1 = new Format();
                    format1.setParameter("size", "large");//字号
                    format1.setParameter("align", "center");//对齐方式
                    format1.setParameter("density", "light");//打印浓度
                    format1.setParameter("bold", "false");//加粗
                    format1.setParameter("reverse", "false");//反白
                    format1.setParameter("inversion", "false");//上下倒置
                    format1.setParameter("line-through", "1");//删除线
                    format1.setParameter("line-italic", "false");//删除线
                    device.printlnText(format1, "POS签购单");

                    Format format_empty1 = new Format();
                    format_empty1.setParameter("size", "large");//字号
                    format_empty1.setParameter("align", "center");//对齐方式
                    format_empty1.setParameter("density", "medium");//打印浓度
                    format_empty1.setParameter("bold", "false");//加粗
                    format_empty1.setParameter("reverse", "false");//反白
                    format_empty1.setParameter("inversion", "false");//上下倒置
                    format_empty1.setParameter("line-through", "1");//删除线
                    format_empty1.setParameter("line-italic", "false");//删除线
                    device.printlnText(format_empty1, "");

                    //订单流水
                    Format format2 = new Format();
                    format2.setParameter("size", "medium");//字号
                    format2.setParameter("align", "left");//对齐方式
                    format2.setParameter("density", "light");//打印浓度
                    format2.setParameter("bold", "false");//加粗
                    format2.setParameter("reverse", "false");//反白
                    format2.setParameter("inversion", "false");//上下倒置
                    format2.setParameter("line-through", "1");//删除线
                    format2.setParameter("line-italic", "false");//删除线
                    device.printText(format2, "订单流水：" + orderId);

                    //商户名称
                    Format format3 = new Format();
                    format3.setParameter("size", "medium");//字号
                    format3.setParameter("align", "left");//对齐方式
                    format3.setParameter("density", "light");//打印浓度
                    format3.setParameter("bold", "false");//加粗
                    format3.setParameter("reverse", "false");//反白
                    format3.setParameter("inversion", "false");//上下倒置
                    format3.setParameter("line-through", "1");//删除线
                    format3.setParameter("line-italic", "false");//删除线
                    device.printText(format3, "商户名称：" + cashiername);

                    //付款方式
                    Format format4 = new Format();
                    format4.setParameter("size", "medium");//字号
                    format4.setParameter("align", "left");//对齐方式
                    format4.setParameter("density", "medium");//打印浓度
                    format4.setParameter("bold", "false");//加粗
                    format4.setParameter("reverse", "false");//反白
                    format4.setParameter("inversion", "false");//上下倒置
                    format4.setParameter("line-through", "1");//删除线
                    format4.setParameter("line-italic", "false");//删除线
                    device.printlnText(format4, "付款方式：" + PaymentMethod);

                    //付款码
                    Format format5 = new Format();
                    format5.setParameter("size", "medium");//字号
                    format5.setParameter("align", "left");//对齐方式
                    format5.setParameter("density", "medium");//打印浓度
                    format5.setParameter("bold", "false");//加粗
                    format5.setParameter("reverse", "false");//反白
                    format5.setParameter("inversion", "false");//上下倒置
                    format5.setParameter("line-through", "1");//删除线
                    format5.setParameter("line-italic", "false");//删除线
                    device.printlnText(format5, "付款码：" + paycode);

                    //金额
                    Format format6 = new Format();
                    format6.setParameter("size", "large");//字号
                    format6.setParameter("align", "left");//对齐方式
                    format6.setParameter("density", "light");//打印浓度
                    format6.setParameter("bold", "false");//加粗
                    format6.setParameter("reverse", "false");//反白
                    format6.setParameter("inversion", "false");//上下倒置
                    format6.setParameter("line-through", "1");//删除线
                    format6.setParameter("line-italic", "false");//删除线
                    device.printlnText(format6, " RMB：" + monery);

                    //终端号
                    Format format7 = new Format();
                    format7.setParameter("size", "medium");//字号
                    format7.setParameter("align", "left");//对齐方式
                    format7.setParameter("density", "medium");//打印浓度
                    format7.setParameter("bold", "false");//加粗
                    format7.setParameter("reverse", "false");//反白
                    format7.setParameter("inversion", "false");//上下倒置
                    format7.setParameter("line-through", "1");//删除线
                    format7.setParameter("line-italic", "false");//删除线
                    device.printlnText(format7, "终端号：" + merchantnum);

                    //交易日期
                    Format format8 = new Format();
                    format8.setParameter("size", "medium");//字号
                    format8.setParameter("align", "left");//对齐方式
                    format8.setParameter("density", "medium");//打印浓度
                    format8.setParameter("bold", "false");//加粗
                    format8.setParameter("reverse", "false");//反白
                    format8.setParameter("inversion", "false");//上下倒置
                    format8.setParameter("line-through", "1");//删除线
                    format8.setParameter("line-italic", "false");//删除线
                    device.printlnText(format8, "交易日期：" + Time);

                    //签名确认
                    Format format9 = new Format();
                    format9.setParameter("size", "medium");//字号
                    format9.setParameter("align", "left");//对齐方式
                    format9.setParameter("density", "medium");//打印浓度
                    format9.setParameter("bold", "false");//加粗
                    format9.setParameter("reverse", "false");//反白
                    format9.setParameter("inversion", "false");//上下倒置
                    format9.setParameter("line-through", "1");//删除线
                    format9.setParameter("line-italic", "false");//删除线
                    device.printlnText(format9, "签名确认：");

                    Format format_empty2 = new Format();
                    format_empty2.setParameter("size", "large");//字号
                    format_empty2.setParameter("align", "center");//对齐方式
                    format_empty2.setParameter("density", "medium");//打印浓度
                    format_empty2.setParameter("bold", "false");//加粗
                    format_empty2.setParameter("reverse", "false");//反白
                    format_empty2.setParameter("inversion", "false");//上下倒置
                    format_empty2.setParameter("line-through", "1");//删除线
                    format_empty2.setParameter("line-italic", "false");//删除线
                    device.printlnText(format_empty2, "    ");

                    //打印条形码
                    Format format10 = new Format();
                    format10.setParameter("align", "center");//对齐方式
                    format10.setParameter("density", "medium");//打印浓度
                    format10.setParameter("HRI-location", "none");//HRI字符的打印位置
                    device.printBitmap(format10, BitmapUtils.creatBarcode(context, orderId, 400, 100, false));

                    /*Format format_empty3 = new Format();
                    format_empty3.setParameter("size", "large");//字号
                    format_empty3.setParameter("align", "center");//对齐方式
                    format_empty3.setParameter("density", "medium");//打印浓度
                    format_empty3.setParameter("bold", "false");//加粗
                    format_empty3.setParameter("reverse", "false");//反白
                    format_empty3.setParameter("inversion", "false");//上下倒置
                    format_empty3.setParameter("line-through", "1");//删除线
                    format_empty3.setParameter("line-italic", "false");//删除线
                    device.printlnText(format_empty3, "");*/

                    //技术支持
                    Format format11 = new Format();
                    format11.setParameter("size", "small");//字号
                    format11.setParameter("align", "right");//对齐方式
                    format11.setParameter("density", "medium");//打印浓度
                    format11.setParameter("bold", "false");//加粗
                    format11.setParameter("reverse", "false");//反白
                    format11.setParameter("inversion", "false");//上下倒置
                    format11.setParameter("line-through", "1");//删除线
                    format11.setParameter("line-italic", "false");//删除线
                    device.printlnText(format11, " 技术支持：山东银商数据");

                    //电话
                    Format format12 = new Format();
                    format12.setParameter("size", "small");//字号
                    format12.setParameter("align", "right");//对齐方式
                    format12.setParameter("density", "medium");//打印浓度
                    format12.setParameter("bold", "false");//加粗
                    format12.setParameter("reverse", "false");//反白
                    format12.setParameter("inversion", "false");//上下倒置
                    format12.setParameter("line-through", "1");//删除线
                    format12.setParameter("line-italic", "false");//删除线
                    device.printlnText(format12, "电话：4009-222-166");

                    Format format_empty4 = new Format();
                    format_empty4.setParameter("size", "large");//字号
                    format_empty4.setParameter("align", "center");//对齐方式
                    format_empty4.setParameter("density", "medium");//打印浓度
                    format_empty4.setParameter("bold", "false");//加粗
                    format_empty4.setParameter("reverse", "false");//反白
                    format_empty4.setParameter("inversion", "false");//上下倒置
                    format_empty4.setParameter("line-through", "1");//删除线
                    format_empty4.setParameter("line-italic", "false");//删除线
                    device.printlnText(format_empty4, "   ");


                } catch (DeviceException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            toast("未开启打印机");
        }
    }


    public void toast(String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

}
