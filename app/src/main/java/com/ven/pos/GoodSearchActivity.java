package com.ven.pos;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.cnyssj.pos.R;
import com.ven.pos.Payment.ConsumeListView;
import com.ven.pos.Payment.PaymentCashierMainAct;
import com.ven.pos.Payment.ShopItem;
import com.ven.pos.Payment.ShopItemMgr;
import com.ven.pos.Util.BaseActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GoodSearchActivity extends BaseActivity {

	private Context context;

	private ListView mEquipListView;
	private ImageButton mSearchBtn;
	private EditText mSearchEt;
	private Map<String, Integer> mSelectShop;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 0) {
				List<ShopItem> items = new ArrayList<ShopItem>(
						ShopItemMgr.instance().ItemList.values());
				GoodAdapter adapter = new GoodAdapter(context, items,
						mListener, mSelectShop);
				mEquipListView.setAdapter(adapter);
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TitleBar.setTitleBar(this, "取消", "商品搜索", "添加", this.onTitleBarLeftClick);

		context = this;

		setContentView(R.layout.good_search);

		mEquipListView = (ListView) findViewById(R.id.good_item_lv);
		 mEquipListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				
			}
		});

		mSearchBtn = (ImageButton) findViewById(R.id.search_shop_ib);
		mSearchEt = (EditText) findViewById(R.id.search_shop_name_et);

		mSearchBtn.setOnClickListener(mOnClickListener);

		mSelectShop = new HashMap<String, Integer>();
	}

	private void UpdateEquipList() {
		Message msg = new Message();
		msg.what = 0;
		msg.obj = "";// (传递的参数， 可不加)
		handler.sendMessage(msg);

	}

	private GoodAdapter.GoodItemClickListener mListener = new GoodAdapter.GoodItemClickListener() {
		@Override
		public void goodAddOnclick(int position, View v) {

			final View tv = v;

		}

		public void goodReduceOnclick(int position, View v) {

		}

		public void onChangeAmount(int position, boolean bAdd) {
			View view = mEquipListView.getChildAt(position
					- mEquipListView.getFirstVisiblePosition());
			EditText goodAmountET = (EditText) view
					.findViewById(R.id.good_amount_ev);
			int amount = Integer.valueOf(goodAmountET.getText().toString());

			if (bAdd) {
				amount += 1;
			} else {
				amount -= 1;
			}
			if (amount < 0)
				amount = 0;
			if (amount > 1000)
				amount = 1000;

			goodAmountET.setText(String.valueOf(amount));

			ShopItem shopItem = (ShopItem) mEquipListView.getAdapter().getItem(
					position);

			mSelectShop.put(shopItem.itemProductsn, amount);
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		// 添加 设备
		UpdateEquipList();
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	private ITitleBarLeftClick onTitleBarLeftClick = new ITitleBarLeftClick() {
		@Override
		public void click() {
			// 添加 商品
			Intent intent = new Intent();
			for (String key : mSelectShop.keySet()) {
				 System.out.println("key= "+ key + " and value= " +
				 mSelectShop.get(key));
				intent.putExtra(key, mSelectShop.get(key));
				ShopItem oldItem = ShopItemMgr.instance().GetItem(key);
				if (!key.isEmpty() && oldItem != null) {

					// 取到旧商品的价格
					double price = ShopItemMgr.instance().GetItem(key).price_100;
					// 商品不一致，加到列表里
					int nTotalMoney = (int) (price * mSelectShop.get(key));
					DecimalFormat fnum = new DecimalFormat("##0.00");
					ConsumeListView.instance()
							.insert(key, Integer.toString(mSelectShop.get(key)),
									fnum.format(nTotalMoney / 100.0f));
					// 更新
					Message message = new Message();
					message.what = PaymentCashierMainAct.UpdateListMsg;
					PaymentCashierMainAct.context.updateList.sendMessage(message);
				}
			}
			//UpdateEquipList();
//
			setResult(RESULT_OK, intent); // intent为A传来的带有Bundle的intent，当然也可以自己定义新的Bundle
			finish();// 此处一定要调用finish()方法
		}
	};

	View.OnClickListener mOnClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.search_shop_ib: {
				@SuppressWarnings("static-access")
				List<ShopItem> items = new ArrayList<ShopItem>(
						ShopItemMgr.instance().ItemList.values());

				List<ShopItem> newItems = new ArrayList<ShopItem>();
				for (int i = 0; i < items.size(); i++) {
					if (items.get(i).itemName.contains(mSearchEt.getText())) {
						newItems.add(items.get(i));
					}
				}
				GoodAdapter adapter = new GoodAdapter(context, newItems,
						mListener, mSelectShop);
				mEquipListView.setAdapter(adapter);
			}

				break;
			}
		}
	};
}
