package com.cnyssj.testOrder;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnyssj.db.DBManager;
import com.cnyssj.db.GoodsOrder;
import com.cnyssj.db.util.Model;
import com.cnyssj.db.util.OrderPaySum;
import com.cnyssj.pos.R;

/**
 * 订单统计适配器
 *
 * @author lb
 */
public class Order_PaySum_Adapter extends CursorAdapter {

    Context mContext;
    Cursor mCursor;
    Model mModel;
    LayoutInflater mInflater;
    String starttimes;//获得起始时间
    String stoptimes;//获得终止时间
    DBManager dbManager;//数据库操作类
    Handler handler;

    public Order_PaySum_Adapter(Context context, Cursor c, Model model, String starttimes, String stoptimes, Handler handler) {
        super(context, c);
        System.out.println("c = " + c);
        this.mContext = context;
        this.mCursor = c;
        this.mModel = model;
        this.starttimes = starttimes;
        this.stoptimes = stoptimes;
        this.handler = handler;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.order_paysum_listview_activity, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = null;

        Object tag = view.getTag();
        if (tag instanceof ViewHolder) {
            holder = (ViewHolder) view.getTag();
        }
        if (holder == null) {
            holder = new ViewHolder();
            view.setTag(holder);
            //寻找控件ID
            holder.paytype = (TextView) view.findViewById(R.id.paytype);
            holder.amount = (TextView) view.findViewById(R.id.amount);
            holder.ordersumtime = (TextView) view.findViewById(R.id.ordersumtime);
            holder.ll = (LinearLayout) view.findViewById(R.id.ll);

            holder.ll.setVisibility(View.VISIBLE);

        }

        dbManager = new DBManager(mContext);

        //时间
        String nowtime = GoodsOrder.getCurrentDay();
        String nowtimes = (String) stoptimes.subSequence(0, 10);
        String nowtimess = (String) starttimes.subSequence(0, 10);
        String nowtime1 = GoodsOrder.getCurrentData1();
        String nowtime2 = GoodsOrder.getCurrentData2();
        String type = "";
        double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
        //查询订单表统计当天金额
        List<OrderPaySum> orderPaySums2 = dbManager.queryOrderPaySum(nowtime1, nowtime2, nowtime);
        //开始时间是当天
        if (nowtime.equals(nowtimess)) {
            //支付方式
            type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
            handler.sendEmptyMessage(Integer.valueOf(type));

        } else {
            //支付方式
            type = cursor.getString(cursor.getColumnIndexOrThrow("paytype"));
        }

        //如果用户选择的终止时间在当天
        if (stoptimes.equals(nowtime)) {
            for (int i = 0; i < orderPaySums2.size(); i++) {
                String types = String.valueOf(orderPaySums2.get(i).getPaytype());
                double moneys = orderPaySums2.get(i).getAmount();
                if (types.equals(type)) {
                    amount = amount + moneys;
                }
            }

        }
        //支付方式
        String paytype = getPayType(type);
        //根据时间段查询统计表
        List<OrderPaySum> orderPaySums = dbManager.queryOrderPaySumDate(starttimes, stoptimes);
        for (int i = 0; i < orderPaySums.size(); i++) {
            type = String.valueOf(orderPaySums.get(i).getPaytype());
            handler.sendEmptyMessage(Integer.valueOf(type));
        }
        //统计的时间段
        String ordersumtime = starttimes + "一一" + stoptimes;

        if (starttimes.equals(stoptimes)) {
            ordersumtime = starttimes;
        }
        if (nowtimes.equals(nowtimess)) {
            ordersumtime = nowtimes;
        }

        //将从数据库中查询到的title设为ListView的Item项。
        holder.paytype.setText(paytype);
        holder.amount.setText(String.valueOf(amount));
        holder.ordersumtime.setText(ordersumtime);

    }

    //支付方式
    public String getPayType(String paytype) {

        if (paytype.equals("1")) {
            paytype = "现金";
        }
        if (paytype.equals("2")) {
            paytype = "余额";
        }
        if (paytype.equals("3")) {
            paytype = "支付宝";
        }
        if (paytype.equals("4")) {
            paytype = "微信";
        }
        if (paytype.equals("5")) {
            paytype = "银联";
        }
        if (paytype.equals("6")) {
            paytype = "翼支付";
        }
        return paytype;

    }


    static class ViewHolder {
        TextView paytype;//支付类型
        TextView amount;//统计金额
        TextView ordersumtime;//统计时间
        LinearLayout ll;
    }

}
