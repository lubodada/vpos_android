package com.jhj.pos.storage;

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

import com.cnyssj.db.Dialog_ListView_Utils;
import com.cnyssj.db.Dialog_ListView_Utils.SelectCallBack;
import com.cnyssj.db.util.Model;
import com.cnyssj.pos.R;

/**
 * 入库操作明细类
 * @author lb
 */
public class StorageDetailActivity extends Activity implements OnClickListener{

	private ListView listview;
	private StorageDetailAdapter storageDetailAdapter;
	private StorageGoodsAdapter storageGoodsAdapter;
	private Button mLeftButton;//上一页
	private Button mRightButton;//下一页
	private TextView tv_storage,storage;
	private Cursor mCursor;
	private Model mModel;
	private DBManager_Storage dbManager_Storage;//数据库操作类
	private long length;
	private int type;//判断是入库信息还是入库明细
	private String storagetype;
	private String count_id;//入库账号
	private Context context;
	private List<StorageManage> storageManages;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.storagedetail_activity);
		context=this;
		//数据库操作类实例化
		dbManager_Storage=new DBManager_Storage(getApplicationContext());
		getView();
		Intent intent = getIntent();
		type=intent.getExtras().getInt("type");
		storagetype=intent.getExtras().getString("storage");
		tv_storage.setText(storagetype);
		//入库管理信息 
		storageManages = dbManager_Storage.queryAllStorageManage();
		if(storageManages.size()!=0){
			showDialog();
		}else{
			mLeftButton.setEnabled(false);
			mRightButton.setEnabled(false);
		}
	}

	public void getView(){
		listview=(ListView) findViewById(R.id.listview);
		mLeftButton = (Button) findViewById(R.id.leftButton);
		mRightButton = (Button) findViewById(R.id.rightButton);
		storage=(TextView) findViewById(R.id.storage);
		tv_storage=(TextView) findViewById(R.id.tv_storage);

		mLeftButton.setOnClickListener(this);
		mRightButton.setOnClickListener(this);
		storage.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.storage:
			showDialog();
			break;
		case R.id.leftButton:
			if(type==0){
				StorageDetail_leftButton();
			}else{
				Storage_leftButton();
			}
			checkButton(length);
			break;
		case R.id.rightButton:
			if(type==0){
				StorageDetail_rigjtButton();
			}else{
				storage_rightButton();
			}
			checkButton(length);
			break;

		default:
			break;
		}

	}
	/**
	 * 分页查询并显示入库明细信息
	 */
	public void StorageDetail_limitData(){
		//创建一个Model的对象，表面这是首页，并且每页显示100个Item项
		mModel = new Model(0, 100);
		//mCursor查询到的是第0页的100个数据。
		mCursor = dbManager_Storage.getAll_StorageDetailItems(count_id,mModel.getIndex()*mModel.getView_Count(), mModel.getView_Count());
		System.out.println("mCursor = " + mCursor);
		//根据参数创建一个TestListAdapter对象，并设给ListView。
		storageDetailAdapter = new StorageDetailAdapter(this, mCursor, mModel);
		listview.setAdapter(storageDetailAdapter);
	}

	/**
	 * 入库详情上一页按钮
	 */
	public void StorageDetail_leftButton(){
		mModel.setIndex(mModel.getIndex() - 1);
		mCursor = dbManager_Storage.getAll_StorageDetailItems(count_id,mModel.getIndex()*mModel.getView_Count(), mModel.getView_Count());
		storageDetailAdapter.changeCursor(mCursor);
		storageDetailAdapter.notifyDataSetChanged();
		//入库明细记录条数
		length=dbManager_Storage.getStorageDetailSumCount();
	}
	/**
	 * 入库详情下一页按钮
	 */
	public void StorageDetail_rigjtButton(){
		mModel.setIndex(mModel.getIndex() + 1);
		mCursor = dbManager_Storage.getAll_StorageDetailItems(count_id,mModel.getIndex()*mModel.getView_Count(), mModel.getView_Count());
		storageDetailAdapter.changeCursor(mCursor);
		storageDetailAdapter.notifyDataSetChanged();
		//入库明细记录条数
		length=dbManager_Storage.getStorageDetailSumCount();
	}

	/**
	 * 分页查询并显示入库信息
	 */
	public void Storage_limitData(){
		//创建一个Model的对象，表面这是首页，并且每页显示5个Item项
		mModel = new Model(0, 100);
		//mCursor查询到的是第0页的5个数据。
		mCursor = dbManager_Storage.getAll_StorageItems(count_id,mModel.getIndex()*mModel.getView_Count(), mModel.getView_Count());
		System.out.println("mCursor = " + mCursor);
		//根据参数创建一个StorageGoodsAdapter对象，并设给ListView。
		storageGoodsAdapter = new StorageGoodsAdapter(this, mCursor, mModel);
		listview.setAdapter(storageGoodsAdapter);
	}
	/**
	 * 入库表信息上一页按钮
	 */
	public void Storage_leftButton(){
		mModel.setIndex(mModel.getIndex() - 1);
		mCursor = dbManager_Storage.getAll_StorageItems(count_id,mModel.getIndex()*mModel.getView_Count(), mModel.getView_Count());
		storageGoodsAdapter.changeCursor(mCursor);
		storageGoodsAdapter.notifyDataSetChanged();
		//入库记录条数
		length=dbManager_Storage.getStorageSumCount();
	}
	/**
	 * 入库表信息下一页按钮
	 */
	public void storage_rightButton(){
		mModel.setIndex(mModel.getIndex() + 1);
		mCursor = dbManager_Storage.getAll_StorageItems(count_id,mModel.getIndex()*mModel.getView_Count(), mModel.getView_Count());
		storageGoodsAdapter.changeCursor(mCursor);
		storageGoodsAdapter.notifyDataSetChanged();
		//入库记录条数
		length=dbManager_Storage.getStorageSumCount();
	}

	/**
	 * 如果页数小于或等于0，表示在第一页，向左的按钮设为不可用，向右的按钮设为可用。
	 * 如果总数目减前几页的数目，得到的是当前页的数目，如果比这一页要显示的少，则说明这是最后一页，向右的按钮不可用，向左的按钮可用。
	 * 如果不是以上两种情况，则说明页数在中间，两个按钮都设为可用。  
	 */
	private void checkButton(long length) {
		if(mModel.getIndex() <= 0) {
			mLeftButton.setEnabled(false);
			mRightButton.setEnabled(true);
		} else if(length - mModel.getIndex()*mModel.getView_Count() <= mModel.getView_Count()) {
			mRightButton.setEnabled(false);
			mLeftButton.setEnabled(true);
		} else {
			mLeftButton.setEnabled(true);
			mRightButton.setEnabled(true);
		}
	}

	/**
	 * 入库账号选择弹框
	 */
	public void showDialog() {

		Dialog_ListView_Utils dialog = new Dialog_ListView_Utils(context,
				"请选择入库账号") {
			@Override
			protected ArrayList<HashMap<String, String>> getListDate() {
				ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
				for (int m = 0; m < storageManages.size(); m++) {  
					HashMap<String, String> Item = new HashMap<String, String>();
					Item.put("count_id", storageManages.get(m).getCount_id());  
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
				count_id=edString;
				if(count_id==null){
					count_id="";
				}
				if(type==0){
					StorageDetail_limitData();
				}else{
					Storage_limitData();
				}
			}
		});
	}
}
