package com.jhj.pos.storage;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.cnyssj.db.util.Model;
import com.cnyssj.pos.R;

/**
 * 入库适配器
 * @author lb
 */
public class StorageGoodsAdapter extends CursorAdapter {

	Context mContext;
	Cursor mCursor;
	Model mModel;
	LayoutInflater mInflater;
	double quantity;
	public StorageGoodsAdapter(Context context, Cursor c, Model model) {
		super(context, c);
		System.out.println("c = " + c);
		this.mContext = context;
		this.mCursor = c;
		this.mModel = model;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return  mInflater.inflate(R.layout.checkgoods_listview, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder holder = null;

		Object tag = view.getTag();
		if(tag instanceof ViewHolder) {
			holder = (ViewHolder) view.getTag();
		}
		if(holder == null) {
			holder = new ViewHolder();
			view.setTag(holder);
			//寻找控件ID
			holder.productsn = (TextView)view.findViewById(R.id.productsn);
			holder.title = (TextView)view.findViewById(R.id.title);
			holder.quantity = (TextView)view.findViewById(R.id.quantity);
			
		}
		//将从数据库中查询到的title设为ListView的Item项。
		holder.productsn.setText(cursor.getString(cursor.getColumnIndexOrThrow("productsn")));
		holder.title.setText(cursor.getString(cursor.getColumnIndexOrThrow("title")));
		holder.quantity.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("quantity"))));

	}

	static class ViewHolder {
		TextView productsn;//商品条码
		TextView title;//商品名称
		TextView quantity;//商品数量
	}


}
