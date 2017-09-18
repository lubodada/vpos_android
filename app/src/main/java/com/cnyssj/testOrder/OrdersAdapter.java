package com.cnyssj.testOrder;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.cnyssj.db.FoodJson1;
import com.cnyssj.db.util.Model;
import com.cnyssj.db.util.Orderdetail;
import com.cnyssj.pos.R;

/**
 * 商品订单适配器
 */
public class OrdersAdapter extends CursorAdapter {

	private Context mContext;
	private Cursor mCursor;
	private Model mModel;
	private LayoutInflater mInflater;
	FoodJson1 foodjson1 = new FoodJson1();

	public OrdersAdapter(Context context, Cursor c, Model model) {
		super(context, c);
		System.out.println("c = " + c);
		this.mContext = context;
		this.mCursor = c;
		this.mModel = model;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.orders_listview_activity, parent,
				false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = null;
		String goods = "";
		Object tag = view.getTag();
		if (tag instanceof ViewHolder) {
			holder = (ViewHolder) view.getTag();
		}
		if (holder == null) {
			holder = new ViewHolder();
			view.setTag(holder);
			// 寻找控件id
			holder.myorderid = (TextView) view.findViewById(R.id.myorderid);
			holder.orderid = (TextView) view.findViewById(R.id.orderid);
			holder.type = (TextView) view.findViewById(R.id.type);
			holder.amount = (TextView) view.findViewById(R.id.amount);
			holder.paymentstatus = (TextView) view
					.findViewById(R.id.paymentstatus);
			holder.orderstatus = (TextView)view.findViewById(R.id.orderstatus);
			holder.ordertime = (TextView) view.findViewById(R.id.ordertime);
			holder.items = (TextView) view.findViewById(R.id.items);
		}
		// 支付方式
		String paytype = cursor.getString(cursor.getColumnIndexOrThrow("type"));
		String type = getPayType(paytype);
		// 支付状态
		String statu = cursor.getString(cursor
				.getColumnIndexOrThrow("paymentstatus"));
		String paymentstatus = getStatus(statu);
		//订单状态
		String status=cursor.getString(cursor.getColumnIndexOrThrow("orderstatus"));
		String orderstatus=getorderstatus(status);
		// 订单商品详情
		String items = cursor.getString(cursor.getColumnIndexOrThrow("items"));
		List<Orderdetail> orderdetails = foodjson1.parseOrderdetailJsons(items);
		for (int i = 0; i < orderdetails.size(); i++) {
			orderdetails.get(i).getName();
			goods = goods + "商品名称:" + orderdetails.get(i).getName() + "  "
					+ "商品数量:" + orderdetails.get(i).getQuantity() + "  "
					+ "商品总价:" + orderdetails.get(i).getAmount() + "\n";
		}
		// 将从数据库中查询到的title设为ListView的Item项。
		holder.myorderid.setText(cursor.getString(cursor
				.getColumnIndexOrThrow("myorderid")));
		holder.orderid.setText(cursor.getString(cursor
				.getColumnIndexOrThrow("orderid")));
		holder.type.setText(type);
		holder.amount.setText(String.valueOf(cursor.getDouble(cursor
				.getColumnIndexOrThrow("amount"))));
		holder.paymentstatus.setText(paymentstatus);
		holder.orderstatus.setText(orderstatus);
		holder.ordertime.setText(cursor.getString(cursor
				.getColumnIndexOrThrow("ordertime")));
		holder.items.setText(goods);

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
		}
		if (status.equals("7")) {
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
