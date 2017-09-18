package com.cnyssj.paytypedialog;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cnyssj.pos.R;

public abstract class AbstractChoickDialog extends Dialog implements
		OnClickListener {

	protected Context mContext;
	protected View mParentView;
	protected View mRootView;

	protected ScrollView mScrollView;
	protected TextView mTVTitle;
	protected Button mButtonOK;
	protected Button mButtonCancel;
	protected ListView mListView;

	protected List<String> mList;
	protected OnClickListener mOkClickListener;

	public AbstractChoickDialog(Context context, List<String> list) {
		super(context);

		mContext = context;
		mList = list;

		initView(mContext);

	}

	@SuppressWarnings("deprecation")
	protected void initView(Context context) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popwindow_listview_layout);
		mRootView = findViewById(R.id.rootView);
//		mRootView.setBackgroundDrawable(new ColorDrawable(0x0000ff00));
		mScrollView = (ScrollView) findViewById(R.id.scrollView);
		mTVTitle = (TextView) findViewById(R.id.tvTitle);
		mButtonOK = (Button) findViewById(R.id.btnOK);
		mButtonOK.setOnClickListener(this);
		mButtonCancel = (Button) findViewById(R.id.btnCancel);
		mButtonCancel.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.listView);

		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();

		ColorDrawable dw = new ColorDrawable(0x0000ff00);
		dialogWindow.setBackgroundDrawable(dw);
	}

	public void setTitle(String title) {
		mTVTitle.setText(title);
	}

	public void setOnOKButtonListener(OnClickListener onClickListener) {
		mOkClickListener = onClickListener;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnOK:
			onButtonOK();
			break;
		case R.id.btnCancel:
			onButtonCancel();
			break;
		}
	}

	protected void onButtonOK() {
		dismiss();

		if (mOkClickListener != null) {
			mOkClickListener.onClick(this, 0);
		}
	}

	protected void onButtonCancel() {
		dismiss();

	}
}
