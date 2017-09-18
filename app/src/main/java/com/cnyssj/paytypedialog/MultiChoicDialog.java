package com.cnyssj.paytypedialog;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cnyssj.pos.R;

public class MultiChoicDialog extends AbstractChoickDialog {

	private MultiChoicAdapter<String> mMultiChoicAdapter;
	private static List<String> mMultiDataList;

	public MultiChoicDialog(Context context) {

		super(context, mMultiDataList);
		initData();
	}

	private final static int COUNT = 4;

	public void initData() {
		mMultiDataList = new ArrayList<String>();
		boolean booleans[] = new boolean[COUNT * 5];
		String str_bytes[] = { "现金支付  -->", "在线支付  -->", "二维码支付  -->",
				"混合交易  -->" };

		for (int i = 0; i < str_bytes.length; i++) {
			mMultiDataList.add(str_bytes[i]);
		}
		initData(booleans);
	}

	protected void initData(boolean flag[]) {
		mMultiChoicAdapter = new MultiChoicAdapter<String>(mContext,
				mMultiDataList, flag, R.drawable.selector_checkbox1);

		mListView.setAdapter(mMultiChoicAdapter);
		mListView.setOnItemClickListener(mMultiChoicAdapter);

		Utils.setListViewHeightBasedOnChildren(mListView);

	}

	public boolean[] getSelectItem() {
		return mMultiChoicAdapter.getSelectItem();
	}

}
