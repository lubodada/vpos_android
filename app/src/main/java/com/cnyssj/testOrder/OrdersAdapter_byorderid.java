package com.cnyssj.testOrder;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cnyssj.db.DBManager;
import com.cnyssj.db.FoodJson1;
import com.cnyssj.db.util.Order;
import com.cnyssj.db.util.Orderdetail;
import com.cnyssj.pos.R;

public class OrdersAdapter_byorderid extends BaseAdapter {

	List<Order> orderslist;
	Context mContext;
	FoodJson1 foodjson1 = new FoodJson1();
	List<Object> list = new ArrayList<Object>();
	// 各种数据库操作
	DBManager dbManager;

	public OrdersAdapter_byorderid(Context mContext, List<Order> orderslist) {
		this.mContext = mContext;
		this.orderslist = orderslist;
	}

	@Override
	public int getCount() {
		return orderslist.size();
	}

	@Override
	public Order getItem(int position) {
		return orderslist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		String goods = "";
		if (convertView == null) {
			convertView = View.inflate(mContext,
					R.layout.orders_listview_activity, null);
			holder = new ViewHolder();
			// 寻找控件id
			holder.myorderid = (TextView) convertView
					.findViewById(R.id.myorderid);
			holder.orderid = (TextView) convertView.findViewById(R.id.orderid);
			holder.type = (TextView) convertView.findViewById(R.id.type);
			holder.amount = (TextView) convertView.findViewById(R.id.amount);
			holder.paymentstatus = (TextView) convertView
					.findViewById(R.id.paymentstatus);
			holder.orderstatus = (TextView)convertView.findViewById(R.id.orderstatus);
			holder.ordertime = (TextView) convertView
					.findViewById(R.id.ordertime);
			holder.items = (TextView) convertView.findViewById(R.id.items);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 赋值
		Order order = getItem(position);
		// 支付方式
		String paytype = String.valueOf(order.getType());
		String type = getPayType(paytype);
		// 支付状态
		String statu = String.valueOf(order.getPaymentstatus());
		String paymentstatus = getStatus(statu);
		//订单状态
		String status=String.valueOf(order.getOrderstatus());
		String orderstatus=getorderstatus(status);
		// 订单商品详情
		String items = order.getItems();
		List<Orderdetail> orderdetails = foodjson1.parseOrderdetailJsons(items);
		for (int i = 0; i < orderdetails.size(); i++) {
			orderdetails.get(i).getName();
			goods = goods + "商品名称:" + orderdetails.get(i).getName() + "  "
					+ "商品数量:" + orderdetails.get(i).getQuantity() + "  "
					+ "商品总价:" + orderdetails.get(i).getAmount() + "\n";
		}
		holder.myorderid.setText(order.getMyorderid());
		holder.orderid.setText(order.getOrderid());
		holder.type.setText(type);
		holder.amount.setText(String.valueOf(order.getAmount()));
		holder.paymentstatus.setText(paymentstatus);
		holder.orderstatus.setText(orderstatus);
		holder.ordertime.setText(order.getOrdertime());
		holder.items.setText(goods);

		return convertView;
	}

	// 支付状态
	public String getStatus(String status) {
		if (status.equals("0")) {
			status = "未知";
		}
		if (status.equals("1")) {
			status = "未支付";
		}
		if (status.equals("2")) {
			status = "部分支付";
		}
		if (status.equals("3")) {
			status = "已支付";
		}
		if (status.equals("4")) {
			status = "退款中";
		}
		if (status.equals("5")) {
			status = "部分退款";
		}
		if (status.equals("6")) {
			status = "全额退款";
		}if (status.equals("7")) {
			status = "支付中";
		}
		return status;

	}

	// 支付方式
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
	//订单状态
	public String getorderstatus(String orderstatus){

		if(orderstatus.equals("1")){
			orderstatus="新建订单";
		}
		if(orderstatus.equals("2")){
			orderstatus="支付中";
		}
		if(orderstatus.equals("3")){
			orderstatus="完成订单";
		}
		return orderstatus;

	}

	static class ViewHolder {
		TextView myorderid;// 订单ID（自己）
		TextView orderid;// 订单id(掌游宝)
		TextView type;// 支付类型
		TextView amount;// 交易金额
		TextView paymentstatus;// 支付状态
		TextView orderstatus;//订单状态
		TextView ordertime;// 时间
		TextView items;// 商品详情

	}
}
