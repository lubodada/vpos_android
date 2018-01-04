package com.jhj.print;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/1/3 0003.
 */

public class Print_goods_huiyin {


    SharedPreferences_util su = new SharedPreferences_util();

    Context context;
    String data;

    @SuppressWarnings("static-access")
    public void prilay(Context context, String data) {

        this.context = context;
        this.data = data;

        try {
            JSONObject bodyjson = new JSONObject();
            bodyjson.put("orderId", data);

            // 保存数据
            su.setPrefString(context, "print_goods", bodyjson.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //
        new_print(context, data);

    }

    @SuppressWarnings("static-access")
    public void new_print(Context context, String data) {
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
                    device.printlnText(format1, "POS购物清单");

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

                    Format format2 = new Format();
                    format2.setParameter("size", "medium");//字号
                    format2.setParameter("align", "left");//对齐方式
                    format2.setParameter("density", "light");//打印浓度
                    format2.setParameter("bold", "false");//加粗
                    format2.setParameter("reverse", "false");//反白
                    format2.setParameter("inversion", "false");//上下倒置
                    format2.setParameter("line-through", "1");//删除线
                    format2.setParameter("line-italic", "false");//删除线
                    device.printlnText(format2, "商品名称            数量  价格 ");

                    //分割线
                    Format format_line = new Format();
                    format_line.setParameter("size", "small");//字号
                    format_line.setParameter("align", "left");//对齐方式
                    format_line.setParameter("density", "medium");//打印浓度
                    format_line.setParameter("bold", "false");//加粗
                    format_line.setParameter("reverse", "false");//反白
                    format_line.setParameter("inversion", "false");//上下倒置
                    format_line.setParameter("line-through", "2");//删除线
                    format_line.setParameter("line-italic", "false");//删除线
                    device.printlnText(format_line, "------------------------------------------");

                    //订单流水
                    Format format3 = new Format();
                    format3.setParameter("size", "medium");//字号
                    format3.setParameter("align", "left");//对齐方式
                    format3.setParameter("density", "light");//打印浓度
                    format3.setParameter("bold", "false");//加粗
                    format3.setParameter("reverse", "false");//反白
                    format3.setParameter("inversion", "false");//上下倒置
                    format3.setParameter("line-through", "1");//删除线
                    format3.setParameter("line-italic", "false");//删除线
                    device.printText(format3, data);

                    Format format_empty2 = new Format();
                    format_empty2.setParameter("size", "large");//字号
                    format_empty2.setParameter("align", "center");//对齐方式
                    format_empty2.setParameter("density", "medium");//打印浓度
                    format_empty2.setParameter("bold", "false");//加粗
                    format_empty2.setParameter("reverse", "false");//反白
                    format_empty2.setParameter("inversion", "false");//上下倒置
                    format_empty2.setParameter("line-through", "1");//删除线
                    format_empty2.setParameter("line-italic", "false");//删除线
                    device.printlnText(format_empty2, "");

                    //技术支持
                    Format format4 = new Format();
                    format4.setParameter("size", "small");//字号
                    format4.setParameter("align", "right");//对齐方式
                    format4.setParameter("density", "medium");//打印浓度
                    format4.setParameter("bold", "false");//加粗
                    format4.setParameter("reverse", "false");//反白
                    format4.setParameter("inversion", "false");//上下倒置
                    format4.setParameter("line-through", "1");//删除线
                    format4.setParameter("line-italic", "false");//删除线
                    device.printlnText(format4, " 技术支持：山东银商数据");

                    //电话
                    Format format5 = new Format();
                    format5.setParameter("size", "small");//字号
                    format5.setParameter("align", "right");//对齐方式
                    format5.setParameter("density", "medium");//打印浓度
                    format5.setParameter("bold", "false");//加粗
                    format5.setParameter("reverse", "false");//反白
                    format5.setParameter("inversion", "false");//上下倒置
                    format5.setParameter("line-through", "1");//删除线
                    format5.setParameter("line-italic", "false");//删除线
                    device.printlnText(format5, "电话：4009-222-166");

                    Format format_empty3 = new Format();
                    format_empty3.setParameter("size", "large");//字号
                    format_empty3.setParameter("align", "center");//对齐方式
                    format_empty3.setParameter("density", "medium");//打印浓度
                    format_empty3.setParameter("bold", "false");//加粗
                    format_empty3.setParameter("reverse", "false");//反白
                    format_empty3.setParameter("inversion", "false");//上下倒置
                    format_empty3.setParameter("line-through", "1");//删除线
                    format_empty3.setParameter("line-italic", "false");//删除线
                    device.printlnText(format_empty3, "   ");


                } catch (DeviceException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
                toast("设备打印机开启失败");
            }
        } else {
            toast("未开启打印机");
        }
    }


    public void toast(String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
