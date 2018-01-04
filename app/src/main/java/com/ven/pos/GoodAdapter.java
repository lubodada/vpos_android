package com.ven.pos;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnyssj.pos.R;
import com.ven.pos.Payment.ShopItem;

public class GoodAdapter extends BaseAdapter {


    private static final String TAG = "EquipItemAdapter";
    private List<ShopItem> mContentList;
    private LayoutInflater mInflater;
    private GoodItemClickListener mListener;
    private Map<String, Integer> mSelectShop;


    public GoodAdapter(Context context,
                       List<ShopItem> contentList,
                       GoodItemClickListener listener,
                       Map<String, Integer> selectShop) {
        mContentList = contentList;
        mInflater = LayoutInflater.from(context);
        mListener = listener;
        mSelectShop = selectShop;
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount");
        return mContentList.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i(TAG, "getItem");
        return mContentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.i(TAG, "getItemId");
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "getView");
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.good_item, null);
            holder = new ViewHolder();
            holder.goodImageIV = (ImageView) convertView.findViewById(R.id.good_image);
            holder.goodNameTV = (TextView) convertView.findViewById(R.id.good_name);
            holder.goodPriceTV = (TextView) convertView.findViewById(R.id.good_price);
            holder.goodAmountTV = (EditText) convertView.findViewById(R.id.good_amount_ev);
            //  holder.equipDeleteButton = (Button) convertView.findViewById(R.id.equip_delete);
            holder.goodAdd = (ImageView) convertView.findViewById(R.id.good_add_ib);
            holder.goodReduce = (ImageView) convertView.findViewById(R.id.good_reduce_ib);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //holder.equipNameEV.setText();

        ShopItem good = (ShopItem) mContentList.get(position);
        holder.goodNameTV.setText(good.itemName);

        if (null != good.itemPic)
            holder.goodImageIV.setImageBitmap(good.itemPic);

        DecimalFormat df = new DecimalFormat("###.00");
        holder.goodPriceTV.setText(df.format(good.price));

        int amount = mSelectShop.containsKey(good.itemProductsn) ? mSelectShop.get(good.itemProductsn) : 0;
        holder.goodAmountTV.setText(String.valueOf(amount));

        holder.goodAdd.setOnClickListener(mListener);
        holder.goodAdd.setTag(position);
        holder.goodReduce.setOnClickListener(mListener);
        holder.goodReduce.setTag(position);
        holder.position = position;

        convertView.setOnClickListener(mListener);


        return convertView;
    }

    public class ViewHolder {
        public int position;
        public ImageView goodImageIV;
        public TextView goodNameTV;
        public TextView goodPriceTV;
        //   public Button equipDeleteButton;
        public TextView goodAmountTV;
        public ImageView goodAdd;    //加
        public ImageView goodReduce;  //减
    }

    /**
     * 用于回调的抽象类
     *
     * @author Ivan Xu
     *         2014-11-26
     */
    public static abstract class GoodItemClickListener implements OnClickListener {
        /**
         * 基类的onClick方法
         */
        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.good_add_ib) {
                goodAddOnclick((Integer) v.getTag(), v);
                onChangeAmount((Integer) v.getTag(), true);
            }
            if (v.getId() == R.id.good_reduce_ib) {
                goodReduceOnclick((Integer) v.getTag(), v);
                onChangeAmount((Integer) v.getTag(), false);
            }

        }


        public abstract void goodAddOnclick(int position, View v);

        public abstract void goodReduceOnclick(int position, View v);

        public abstract void onChangeAmount(int position, boolean bAdd);
    }
}