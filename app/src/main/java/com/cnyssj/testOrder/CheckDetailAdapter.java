package com.cnyssj.testOrder;

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
 * 盘点明细适配器
 *
 * @author Administrator
 */
public class CheckDetailAdapter extends CursorAdapter {


    Context mContext;
    Cursor mCursor;
    Model mModel;
    LayoutInflater mInflater;
    double quantity;

    public CheckDetailAdapter(Context context, Cursor c, Model model) {
        super(context, c);
        System.out.println("c = " + c);
        this.mContext = context;
        this.mCursor = c;
        this.mModel = model;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.checkdetail_listview, parent, false);
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
            holder.productsn = (TextView) view.findViewById(R.id.productsn);
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.quantity = (TextView) view.findViewById(R.id.quantity);
            holder.updatetime = (TextView) view.findViewById(R.id.updatetime);
            holder.updatetype = (TextView) view.findViewById(R.id.updatetype);

        }
        int updatetypes = cursor.getInt(cursor.getColumnIndexOrThrow("updatetype"));
        String updatetype = getUpdateType(updatetypes);

        //将从数据库中查询到的title设为ListView的Item项。
        holder.productsn.setText(cursor.getString(cursor.getColumnIndexOrThrow("productsn")));
        holder.title.setText(cursor.getString(cursor.getColumnIndexOrThrow("title")));
        holder.quantity.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("quantity"))));
        holder.updatetime.setText(cursor.getString(cursor.getColumnIndexOrThrow("updatetime")));
        holder.updatetype.setText(updatetype);

    }

    static class ViewHolder {
        TextView productsn;//商品条码
        TextView title;//商品名称
        TextView quantity;//商品数量
        TextView updatetime;//数量更新时间
        TextView updatetype;//数量更新方式

    }

    /**
     * 数量更新类型
     *
     * @param updatetypes
     * @return
     */
    public String getUpdateType(int updatetypes) {
        String updatetype = null;
        if (updatetypes == 0) {
            updatetype = "新增";
        }
        if (updatetypes == 1) {
            updatetype = "增加";
        }
        if (updatetypes == 2) {
            updatetype = "减少";
        }
        if (updatetypes == 3) {
            updatetype = "修改";
        }
        return updatetype;

    }

}
