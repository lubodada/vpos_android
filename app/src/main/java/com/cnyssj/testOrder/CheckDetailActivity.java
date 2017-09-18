package com.cnyssj.testOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cnyssj.db.CheckManage;
import com.cnyssj.db.DBManager;
import com.cnyssj.db.Dialog_ListView_Utils;
import com.cnyssj.db.Dialog_ListView_Utils.SelectCallBack;
import com.cnyssj.db.util.Model;
import com.cnyssj.pos.R;

/**
 * 盘点操作明细类
 * 
 * @author lb
 */
public class CheckDetailActivity extends Activity implements OnClickListener {

	private ListView listview;
	private CheckDetailAdapter checkDetailAdapter;
	private Button mLeftButton;// 上一页
	private Button mRightButton;// 下一页
	private TextView storage, tv_check;
	private Cursor mCursor;
	private Model mModel;
	private DBManager dbManager;// 数据库操作类
	private CheckGoodsAdapter checkGoodsAdapter;
	private long length;
	private int type;
	private String checktype;
	private String count_id;
	private Context context;
	private List<CheckManage> checkManages;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.checkdatail_activaty);
		context = this;
		// 数据库操作类实例化
		dbManager = new DBManager(getApplicationContext());
		getView();
		Intent intent = getIntent();
		type = intent.getExtras().getInt("type");
		checktype = intent.getExtras().getString("check");
		tv_check.setText(checktype);
		// 盘点账号
		checkManages = dbManager.queryAllCheckManage();
		if (checkManages.size() != 0) {
			showDialog();
		} else {
			mLeftButton.setEnabled(false);
			mRightButton.setEnabled(false);
		}
	}

	public void getView() {
		listview = (ListView) findViewById(R.id.listview);
		mLeftButton = (Button) findViewById(R.id.leftButton);
		mRightButton = (Button) findViewById(R.id.rightButton);
		storage = (TextView) findViewById(R.id.storage);
		tv_check = (TextView) findViewById(R.id.tv_check);

		mLeftButton.setOnClickListener(this);
		mRightButton.setOnClickListener(this);
		storage.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.storage:
			showDialog();
			break;
		case R.id.leftButton:
			if (type == 0) {
				CheckDetail_leftButton();
			} else {
				Check_leftButton();
			}
			checkButton(length);
			break;
		case R.id.rightButton:
			if (type == 0) {
				CheckDetail_rigjtButton();
			} else {
				Check_rightButton();
			}
			checkButton(length);
			break;

		default:
			break;
		}

	}

	/**
	 * 分页查询并显示盘点详情
	 */
	public void CheckDetail_limitData() {
		// 创建一个Model的对象，表面这是首页，并且每页显示100个Item项
		mModel = new Model(0, 100);
		// mCursor查询到的是第0页的100个数据。
		mCursor = dbManager.getAll_CheckDetailItems(count_id, mModel.getIndex()
				* mModel.getView_Count(), mModel.getView_Count());
		System.out.println("mCursor = " + mCursor);
		// 根据参数创建一个TestListAdapter对象，并设给ListView。
		checkDetailAdapter = new CheckDetailAdapter(this, mCursor, mModel);
		listview.setAdapter(checkDetailAdapter);
	}

	/**
	 * 盘点详情上一页按钮
	 */
	public void CheckDetail_leftButton() {
		mModel.setIndex(mModel.getIndex() - 1);
		mCursor = dbManager.getAll_CheckDetailItems(count_id, mModel.getIndex()
				* mModel.getView_Count(), mModel.getView_Count());
		checkDetailAdapter.changeCursor(mCursor);
		checkDetailAdapter.notifyDataSetChanged();
		// 盘点详情条数
		length = dbManager.getCheckDetailSumCount();
	}

	/**
	 * 盘点详情下一页按钮
	 */
	public void CheckDetail_rigjtButton() {
		mModel.setIndex(mModel.getIndex() + 1);
		mCursor = dbManager.getAll_CheckDetailItems(count_id, mModel.getIndex()
				* mModel.getView_Count(), mModel.getView_Count());
		checkDetailAdapter.changeCursor(mCursor);
		checkDetailAdapter.notifyDataSetChanged();
		// 盘点详情条数
		length = dbManager.getCheckDetailSumCount();
	}

	/**
	 * 分页查询并显示盘点信息
	 */
	public void Check_limitData() {
		// 创建一个Model的对象，表面这是首页，并且每页显示5个Item项
		mModel = new Model(0, 100);
		// mCursor查询到的是第0页的5个数据。
		mCursor = dbManager.getAll_CheckItems(count_id, mModel.getIndex()
				* mModel.getView_Count(), mModel.getView_Count());
		System.out.println("mCursor = " + mCursor);
		// 根据参数创建一个TestListAdapter对象，并设给ListView。
		checkGoodsAdapter = new CheckGoodsAdapter(this, mCursor, mModel);
		listview.setAdapter(checkGoodsAdapter);
	}

	/**
	 * 盘点表信息上一页按钮
	 */
	public void Check_leftButton() {
		mModel.setIndex(mModel.getIndex() - 1);
		mCursor = dbManager.getAll_CheckItems(count_id, mModel.getIndex()
				* mModel.getView_Count(), mModel.getView_Count());
		checkGoodsAdapter.changeCursor(mCursor);
		checkGoodsAdapter.notifyDataSetChanged();
		// 盘点记录条数
		length = dbManager.getCheckSumCount();
	}

	/**
	 * 盘点表信息下一页按钮
	 */
	public void Check_rightButton() {
		mModel.setIndex(mModel.getIndex() + 1);
		mCursor = dbManager.getAll_CheckItems(count_id, mModel.getIndex()
				* mModel.getView_Count(), mModel.getView_Count());
		checkGoodsAdapter.changeCursor(mCursor);
		checkGoodsAdapter.notifyDataSetChanged();
		// 盘点记录条数
		length = dbManager.getCheckSumCount();
	}

	/**
	 * 如果页数小于或等于0，表示在第一页，向左的按钮设为不可用，向右的按钮设为可用。
	 * 如果总数目减前几页的数目，得到的是当前页的数目，如果比这一页要显示的少，则说明这是最后一页，向右的按钮不可用，向左的按钮可用。
	 * 如果不是以上两种情况，则说明页数在中间，两个按钮都设为可用。
	 */
	private void checkButton(long length) {
		if (mModel.getIndex() <= 0) {
			mLeftButton.setEnabled(false);
			mRightButton.setEnabled(true);
		} else if (length - mModel.getIndex() * mModel.getView_Count() <= mModel
				.getView_Count()) {
			mRightButton.setEnabled(false);
			mLeftButton.setEnabled(true);
		} else {
			mLeftButton.setEnabled(true);
			mRightButton.setEnabled(true);
		}
	}

	/**
	 * 盘点账号选择弹框
	 */
	public void showDialog() {

		Dialog_ListView_Utils dialog = new Dialog_ListView_Utils(context,
				"请选择盘点账号") {
			@Override
			protected ArrayList<HashMap<String, String>> getListDate() {
				ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
				for (int m = 0; m < checkManages.size(); m++) {// initData为一个list类型的数据源
					HashMap<String, String> Item = new HashMap<String, String>();
					Item.put("count_id", checkManages.get(m).getCount_id());
					listData.add(Item);
				}
				return listData;
			}

			@Override
			protected int getDialogWidth() {
				return 890;
			}
		};

		dialog.onCreateDialog();
		dialog.setCallBack(new SelectCallBack() {
			@Override
			public void isConfirm(String edString) {
				System.out.println("你选择了" + edString);
				count_id = edString;
				if (count_id == null) {
					count_id = "";
				}
				if (type == 0) {
					CheckDetail_limitData();
				} else {
					Check_limitData();
				}
			}
		});
	}

}
