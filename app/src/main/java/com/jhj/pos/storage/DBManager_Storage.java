package com.jhj.pos.storage;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cnyssj.db.Mysqlitedbhelp;

/**
 * 商品入库的数据库操作类
 * @author lb
 */
public class DBManager_Storage {
	
	private Mysqlitedbhelp helper;
	private SQLiteDatabase db;
	private String tag = "DBManager";

	public DBManager_Storage(Context context)
	{
		Log.d("DBManager_Check_Storage", "DBManager_Check_Storage开始");
		helper = new Mysqlitedbhelp(context);
		db = helper.getWritableDatabase();
	}
	
	
	// ************************* 入库开始  ***************************************
	
		/**
		 * 添加入库信息
		 * @param orders
		 */
		public String addStorageData(List<Storage> storages)
		{	
			db = helper.getWritableDatabase();
			//设置标签 "0"为添加数据失败
			String  flag = "0";
			Log.d(tag, "正在添加入库信息....");
			// 采用事务处理，确保数据完整性
			db.beginTransaction(); // 开始事务
			try
			{
				for (Storage storage : storages)
				{
					db.execSQL(" INSERT INTO " + Mysqlitedbhelp.Storage_TABLE_NAME
							+ " VALUES(null,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[] { storage.getId(), storage.getTitle(), storage.getProductsn(),
									storage.getQuantity(), storage.getCreatetime(), storage.getUpdatetime(), storage.getCount_time(), 
									storage.getStatus(), storage.getCount_id(), storage.getShelf(),storage.getStorage_people(),
									storage.getRemark(), storage.getUpdate_status()});
					// 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
					// 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
					// 使用占位符有效区分了这种情况
				}
				db.setTransactionSuccessful(); // 设置事务成功完成
				//"1"为添加成功
				flag = "1";
				Log.d(tag, "入库信息添加成功");
			}catch (Exception e) {
				// TODO: handle exception
				Log.d(tag, "入库信息添加失败");
				e.printStackTrace();
			}
			finally
			{
				db.endTransaction(); // 结束事务
				db.close();
			}

			return flag;
		}
		
		/**
		 * 根据商品条码查询入库信息
		 * @return
		 */
		public List<Storage> queryStorageByProductsn(String productsn,String count_id)
		{
			db = helper.getReadableDatabase();
			List<Storage> storages = new ArrayList<Storage>();

			Cursor c = querystorageByproductsn(productsn,count_id);

			if( c == null || "".equals(c)){
				Log.d(tag, "入库表中没有商品条码为"+productsn+"的商品信息");
				return storages;
			}

			while (c.moveToNext())
			{
				Storage storage = new Storage();
				storage.setId(c.getString(c.getColumnIndex("id")));
				storage.setTitle(c.getString(c.getColumnIndex("title")));
				storage.setProductsn(c.getString(c.getColumnIndex("productsn")));
				storage.setQuantity(c.getDouble(c.getColumnIndex("quantity")));
				storage.setCreatetime(c.getString(c.getColumnIndex("createtime")));
				storage.setUpdatetime(c.getString(c.getColumnIndex("updatetime")));
				storage.setCount_time(c.getString(c.getColumnIndex("count_time")));
				storage.setStatus(c.getInt(c.getColumnIndex("status")));
				storage.setCount_id(c.getString(c.getColumnIndex("count_id")));
				storage.setShelf(c.getString(c.getColumnIndex("shelf")));
				storage.setStorage_people(c.getString(c.getColumnIndex("storage_people")));
				storage.setRemark(c.getString(c.getColumnIndex("remark")));

				storages.add(storage);
			}
			c.close();
			db.close();

			return storages;
		}

		public Cursor querystorageByproductsn(String productsn,String count_id)
		{
			Cursor c = db.rawQuery(" SELECT * FROM " 
					+ Mysqlitedbhelp.Storage_TABLE_NAME+" where productsn = ? and count_id = ? ", 
					new String[] {productsn,count_id});

			return c;
		}
		/**
		 *  根据上传状态查询自定义条数的未上传的入库信息
		 * @param status 上传状态    0：未上传 1：上传中 2：上传成功
		 * @param checkitems 查询条数
		 * @return
		 */
		public List<Storage> queryStorageByStatus(String checkitems)
		{
			db = helper.getReadableDatabase();
			List<Storage> storages = new ArrayList<Storage>();

			Cursor c = querystorageBystatus(checkitems);

			if( c == null || "".equals(c)){
				Log.d(tag, "盘点表中没有上传状态为"+"0"+"的商品信息");
				return storages;
			}

			while (c.moveToNext())
			{
				Storage storage = new Storage();
				storage.setId(c.getString(c.getColumnIndex("id")));
				storage.setTitle(c.getString(c.getColumnIndex("title")));
				storage.setProductsn(c.getString(c.getColumnIndex("productsn")));
				storage.setQuantity(c.getDouble(c.getColumnIndex("quantity")));
				storage.setCreatetime(c.getString(c.getColumnIndex("createtime")));
				storage.setUpdatetime(c.getString(c.getColumnIndex("updatetime")));
				storage.setCount_time(c.getString(c.getColumnIndex("count_time")));
				storage.setStatus(c.getInt(c.getColumnIndex("status")));
				storage.setCount_id(c.getString(c.getColumnIndex("count_id")));
				storage.setShelf(c.getString(c.getColumnIndex("shelf")));
				storage.setStorage_people(c.getString(c.getColumnIndex("storage_people")));
				storage.setRemark(c.getString(c.getColumnIndex("remark")));

				storages.add(storage);
			}
			c.close();
			db.close();

			return storages;
		}

		public Cursor querystorageBystatus(String checkitems)
		{
			Cursor c = db.rawQuery(" SELECT * FROM " 
					+ Mysqlitedbhelp.Storage_TABLE_NAME+" where status = ? limit ?,? ", 
					new String[] {"0","0",checkitems});

			return c;
		}
		/**
		 * 用户退出时
		 * 根据入库账号查询所有的入库信息
		 * @return
		 */
		public List<Storage> queryAllStorageByCount_id(String count_id)
		{
			db = helper.getReadableDatabase();
			List<Storage> storages = new ArrayList<Storage>();

			Cursor c = queryallstorageBystatus(count_id);

			if( c == null || "".equals(c)){
				Log.d(tag, "入库表中没有上传状态为"+"0"+"的商品信息");
				return storages;
			}

			while (c.moveToNext())
			{
				Storage storage = new Storage();
				storage.setId(c.getString(c.getColumnIndex("id")));
				storage.setTitle(c.getString(c.getColumnIndex("title")));
				storage.setProductsn(c.getString(c.getColumnIndex("productsn")));
				storage.setQuantity(c.getDouble(c.getColumnIndex("quantity")));
				storage.setCreatetime(c.getString(c.getColumnIndex("createtime")));
				storage.setUpdatetime(c.getString(c.getColumnIndex("updatetime")));
				storage.setCount_time(c.getString(c.getColumnIndex("count_time")));
				storage.setStatus(c.getInt(c.getColumnIndex("status")));
				storage.setCount_id(c.getString(c.getColumnIndex("count_id")));
				storage.setShelf(c.getString(c.getColumnIndex("shelf")));
				storage.setStorage_people(c.getString(c.getColumnIndex("storage_people")));
				storage.setRemark(c.getString(c.getColumnIndex("remark")));

				storages.add(storage);
			}
			c.close();
			db.close();

			return storages;
		}

		public Cursor queryallstorageBystatus(String count_id)
		{
			Cursor c = db.rawQuery(" SELECT * FROM " 
					+ Mysqlitedbhelp.Storage_TABLE_NAME+" where count_id = ? ", 
					new String[] {count_id});

			return c;
		}

		/**
		 * 入库商品上传状态批量更新
		 * @param storages 入库上传商品集合
		 * @param status 上传状态  0：未上传 1：上传中 2：上传成功
		 * @return flag
		 */
		public boolean updateStorageStatus(List<Storage> storages,String status){	

			db = helper.getWritableDatabase();
			boolean flag=false;
			ContentValues cv = new ContentValues();
			cv.put("status",status);
			for(Storage storage : storages){

				int c=db.update(Mysqlitedbhelp.Storage_TABLE_NAME, cv, " productsn = ? ",
						new String[]{storage.getProductsn()});
				if(c==0){
					Log.d(tag, "条码为"+storage.getProductsn()+"的商品上传状态更新失败");
					return flag;
				}else{
					flag=true;
					Log.d(tag, "条码为"+storage.getProductsn()+"的商品上传状态更新成功");
				}
			}

			return flag;

		}
		/**
		 * 入库商品上传状态更新
		 * @param productsn 商品条码
		 * @param status 上传状态  0：未上传 1：上传中 2：上传成功
		 * @return flag
		 */
		public boolean updateStorageStatusByProductsn(String productsn,String status){	
			db = helper.getWritableDatabase();
			boolean flag=false;
			ContentValues cv = new ContentValues();
			cv.put("status",status);
			int c=db.update(Mysqlitedbhelp.Storage_TABLE_NAME, cv, " productsn = ? ",
					new String[]{productsn});
			if(c==0){
				Log.d(tag, "条码为"+productsn+"的商品上传状态更新失败");
				return flag;
			}else{
				flag=true;
				Log.d(tag, "条码为"+productsn+"的商品上传状态更新成功");
			}
			return flag;

		}
		/**
		 * 入库商品数量更新
		 * @param productsn 商品条码
		 * @param quantity 现在数量
		 * @return flag
		 */
		public boolean updateStorageQuantity(String productsn,double quantity){	

			db = helper.getWritableDatabase();
			boolean flag=false;
			ContentValues cv = new ContentValues();
			cv.put("quantity",quantity);
			int c=db.update(Mysqlitedbhelp.Storage_TABLE_NAME, cv, " productsn = ? ",
					new String[]{productsn});
			if(c==0){
				Log.d(tag, "入库商品数量更新失败");
				return flag;
			}else{
				flag=true;
				Log.d(tag, "入库商品数量更新成功");

			}
			return flag;

		}

		/**
		 * 入库表中时间更新
		 * @param productsn 商品条码
		 * @param updatetime 更新时间
		 * @return
		 */
		public boolean updateStorageTime(String productsn,String updatetime){
			boolean flag=false;
			ContentValues cv = new ContentValues();
			cv.put("updatetime",updatetime);
			int c = db.update(Mysqlitedbhelp.Storage_TABLE_NAME, cv, " productsn = ? ",
					new String[]{productsn});
			if(c==0){
				Log.d(tag, "时间更新失败");
				return flag;
			}else{
				flag=true;
				Log.d(tag, "时间更新成功");
			}
			return flag;

		}

		/**
		 * 查询入库记录的总数
		 * @return length
		 */
		public long getStorageSumCount() {
			db = helper.getWritableDatabase();
			String sql = "select count(*) from "+ Mysqlitedbhelp.Storage_TABLE_NAME;
			Cursor c = db.rawQuery(sql, null);
			c.moveToFirst();
			long length = c.getLong(0);
			c.close();
			return length;
		}

		/**
		 * 查询入库表中最新入库信息的的上传状态
		 * @return 
		 */
		public int queryLastStorage_Status(){
			int status = -1;
			db = helper.getReadableDatabase();
			String sql = " SELECT status FROM "
					+Mysqlitedbhelp.Storage_TABLE_NAME+" ORDER BY count_id DESC limit ?,?";
			Cursor mCursor = db.rawQuery(sql,new String[]{"0","1"});
			while (mCursor.moveToNext()) {

				status=mCursor.getInt(mCursor.getColumnIndex("status"));
			}

			return status;
		}

		/**
		 * 根据入库账号删除入库数据
		 */
		public void deleteStorageDataByCount_id(String count_id){
			db = helper.getWritableDatabase();
			try {
				db.delete(Mysqlitedbhelp.Storage_TABLE_NAME, " count_id = ? ",
						new String[]{count_id});
				Log.d(tag, "成功删除入库表中入库账号为"+count_id+"的数据");
			} catch (Exception e) {
				Log.d(tag, "删除入库表中入库账号为"+count_id+"的数据失败了");
				// TODO: handle exception
			}

		}

		/**
		 * 拿到所有的入库信息条数
		 * @param firstResult 从第几条数据开始查询。
		 * @param maxResult   每页显示多少条记录。
		 * @return 当前页的记录
		 */
		public Cursor getAll_StorageItems(String count_id,int firstResult, int maxResult) {
			db = helper.getWritableDatabase();
			String sql = "select _id,id,title,productsn,quantity from "
					+Mysqlitedbhelp.Storage_TABLE_NAME 
					+ " where count_id = ? limit ?,?";
			Cursor mCursor = db.rawQuery(sql, new String[]{count_id,String.valueOf(firstResult), String.valueOf(maxResult)});
			return mCursor;
		}
		
		//**************入库明细***********************//
		/**
		 * 添加入库操作明细信息
		 * @param orders
		 */
		public String addStorageDetailData(List<StorageDetail> storageDetails)
		{	
			db = helper.getWritableDatabase();
			//设置标签 "0"为添加数据失败
			String  flag = "0";
			Log.d(tag, "正在添加操作明细信息....");
			// 采用事务处理，确保数据完整性
			db.beginTransaction(); // 开始事务
			try
			{
				for (StorageDetail storageDetail : storageDetails)
				{
					db.execSQL(" INSERT INTO " + Mysqlitedbhelp.StorageDetail_TABLE_NAME
							+ " VALUES(null,?, ?, ?, ?, ?, ?)", new Object[] { storageDetail.getCount_id(),storageDetail.getTitle(), storageDetail.getProductsn(),
									storageDetail.getQuantity(), storageDetail.getUpdatetime(), storageDetail.getUpdatetype()});
				}
				db.setTransactionSuccessful(); // 设置事务成功完成
				//"1"为添加成功
				flag = "1";
				Log.d(tag, "入库操作明细信息添加成功");
			}catch (Exception e) {
				// TODO: handle exception
				Log.d(tag, "入库操作明细信息添加失败");
				e.printStackTrace();
			}
			finally
			{
				db.endTransaction(); // 结束事务
				db.close();
			}

			return flag;
		}
		
		/**
		 * 查询入库明细记录的总数
		 * @return length
		 */
		public long getStorageDetailSumCount() {
			db = helper.getWritableDatabase();
			String sql = "select count(*) from "+ Mysqlitedbhelp.StorageDetail_TABLE_NAME;
			Cursor c = db.rawQuery(sql, null);
			c.moveToFirst();
			long length = c.getLong(0);
			c.close();
			return length;
		}
		
		/**
		 * 拿到所有的入库明细信息条数
		 * @param firstResult 从第几条数据开始查询。
		 * @param maxResult   每页显示多少条记录。
		 * @return 当前页的记录
		 */
		public Cursor getAll_StorageDetailItems(String count_id,int firstResult, int maxResult) {
			db = helper.getWritableDatabase();
			String sql = "select _id,count_id,title,productsn,quantity,updatetime,updatetype from "
					+Mysqlitedbhelp.StorageDetail_TABLE_NAME 
					+ " where count_id = ? limit ?,?";
			Cursor mCursor = db.rawQuery(sql, new String[]{count_id,String.valueOf(firstResult), String.valueOf(maxResult)});
			return mCursor;
		}
		
		/**
		 * 根据入库账号删除入库明细数据
		 */
		public void deleteStorageDetailByCount_id(String count_id){
			db = helper.getWritableDatabase();
			try {
				db.delete(Mysqlitedbhelp.StorageDetail_TABLE_NAME, " count_id = ? ",
						new String[]{count_id});
				Log.d(tag, "成功删除入库明细表中入库账号为"+count_id+"对应的数据");
			} catch (Exception e) {
				Log.d(tag, "删除入库明细表中入库账号为"+count_id+"的数据失败了");
			}
		}
		
		
		//*********************入库管理**********************//
		
		/**
		 * 添加入库管理信息
		 * @param orders
		 */
		public String addStorageManageData(List<StorageManage> storageManages)
		{	
			db = helper.getWritableDatabase();
			//设置标签 "0"为添加数据失败
			String  flag = "0";
			Log.d(tag, "正在添加入库管理信息....");
			// 采用事务处理，确保数据完整性
			db.beginTransaction(); // 开始事务
			try
			{
				for (StorageManage storageManage : storageManages)
				{
					db.execSQL(" INSERT INTO " + Mysqlitedbhelp.StorageManage_TABLE_NAME
							+ " VALUES(?, ?, ?, ?)", 
							new Object[] { storageManage.getCount_id(),storageManage.getStorage_status(), 
									storageManage.getCount_time(),storageManage.getRemark()});
				}
				db.setTransactionSuccessful(); // 设置事务成功完成
				//"1"为添加成功
				flag = "1";
				Log.d(tag, "入库管理信息添加成功");
			}catch (Exception e) {
				// TODO: handle exception
				Log.d(tag, "入库管理信息添加失败");
				e.printStackTrace();
			}
			finally
			{
				db.endTransaction(); // 结束事务
				db.close();
			}

			return flag;
		}
		
		/**
		 * 根据入库唯一id查询入库管理信息
		 * @return
		 */
		public List<StorageManage> queryStorageManageByCount_id(String count_id)
		{
			db = helper.getReadableDatabase();
			List<StorageManage> storageManages = new ArrayList<StorageManage>();

			Cursor c = querystoragemanageBycount_id(count_id);

			if( c == null || "".equals(c)){
				Log.d(tag, "入库管理表中没有入库账号为"+count_id+"的信息");
				return storageManages;
			}

			while (c.moveToNext())
			{
				StorageManage storageManage = new StorageManage();
				storageManage.setCount_id(c.getString(c.getColumnIndex("count_id")));
				storageManage.setStorage_status(c.getInt(c.getColumnIndex("storage_status")));
				storageManage.setCount_time(c.getString(c.getColumnIndex("count_time")));
				storageManage.setRemark(c.getString(c.getColumnIndex("remark")));

				storageManages.add(storageManage);
			}
			c.close();
			db.close();

			return storageManages;
		}

		public Cursor querystoragemanageBycount_id(String count_id)
		{
			Cursor c = db.rawQuery(" SELECT * FROM " 
					+ Mysqlitedbhelp.StorageManage_TABLE_NAME+" where count_id = ? ", 
					new String[] {count_id});

			return c;
		}
		/**
		 * 查询入库管理表中的信息
		 * @return
		 */
		public List<StorageManage> queryAllStorageManage()
		{
			db = helper.getReadableDatabase();
			List<StorageManage> storageManages = new ArrayList<StorageManage>();
			
			Cursor c = queryall_storagemanage();
			
			if( c == null || "".equals(c)){
				Log.d(tag, "入库管理表中没有信息");
				return storageManages;
			}
			
			while (c.moveToNext())
			{
				StorageManage storageManage = new StorageManage();
				storageManage.setCount_id(c.getString(c.getColumnIndex("count_id")));
				storageManage.setStorage_status(c.getInt(c.getColumnIndex("storage_status")));
				storageManage.setCount_time(c.getString(c.getColumnIndex("count_time")));
				storageManage.setRemark(c.getString(c.getColumnIndex("remark")));
				
				storageManages.add(storageManage);
			}
			c.close();
			db.close();
			
			return storageManages;
		}
		
		public Cursor queryall_storagemanage()
		{
			Cursor c = db.rawQuery(" SELECT * FROM " 
					+ Mysqlitedbhelp.StorageManage_TABLE_NAME, 
					null);
			
			return c;
		}
		/**
		 * 查询入库时间
		 * @return count_time
		 */
		public String queryLastStorage_Count_Time(){
			String count_time = null;
			db = helper.getReadableDatabase();
			String sql = " SELECT count_time FROM "
					+Mysqlitedbhelp.StorageManage_TABLE_NAME+" ORDER BY count_time DESC limit ?,?";
			Cursor mCursor = db.rawQuery(sql,new String[]{"0","1"});
			while (mCursor.moveToNext()) {

				count_time=mCursor.getString(mCursor.getColumnIndex("count_time"));
			}

			return count_time;
		}
		/**
		 * 入库管理表中入库状态更新
		 * @param count_id 盘点唯一id
		 * @param storage_status 入库状态  0:未完成  1:已完成   -1:无数据
		 * @return
		 */
		public boolean updateStorageManage_Storage_status(String count_id,int storage_status){
			boolean flag=false;
			ContentValues cv = new ContentValues();
			cv.put("storage_status",storage_status);
			int c = db.update(Mysqlitedbhelp.StorageManage_TABLE_NAME, cv, " count_id = ? ",
					new String[]{count_id});
			if(c==0){
				Log.d(tag, "入库状态更新失败");
				return flag;
			}else{
				flag=true;
				Log.d(tag, "入库状态更新成功");
			}
			return flag;

		}
		
		/**
		 * 查询最近创建的入库账号
		 * @return Count_id
		 */
		public String queryLastStorageCount_id(){
			String count_id = "";
			db = helper.getReadableDatabase();
			String sql = " SELECT count_id FROM "
					+Mysqlitedbhelp.StorageManage_TABLE_NAME+" ORDER BY count_id DESC limit ?,?";
			Cursor mCursor = db.rawQuery(sql,new String[]{"0","1"});
			while (mCursor.moveToNext()) {
				
				count_id=mCursor.getString(mCursor.getColumnIndex("count_id"));
			}
			
			return count_id;
		}
		/**
		 * 查询最近入库的入库状态
		 * @return storage_status 0:未完成  1：已完成   -1:无数据
		 */
		public int queryLastStorageManage_Stoage_status(){
			int storage_status = -1;
			db = helper.getReadableDatabase();
			String sql = " SELECT storage_status FROM "
					+Mysqlitedbhelp.StorageManage_TABLE_NAME+" ORDER BY count_id DESC limit ?,?";
			Cursor mCursor = db.rawQuery(sql,new String[]{"0","1"});
			while (mCursor.moveToNext()) {
				
				storage_status=mCursor.getInt(mCursor.getColumnIndex("storage_status"));
			}
			
			return storage_status;
		}
		
		/**
		 * 根据入库账号删除入库管理数据
		 */
		public void deleteStorageManageByCount_id(String count_id){
			db = helper.getWritableDatabase();
			try {
				db.delete(Mysqlitedbhelp.StorageManage_TABLE_NAME, " count_id = ? ",
						new String[]{count_id});
				Log.d(tag, "成功删除入库账号为"+count_id+"的入库管理数据");
			} catch (Exception e) {
				Log.d(tag, "删除入库账号为"+count_id+"的入库管理数据失败了");
				// TODO: handle exception
			}
			
		}
		
		//**************************入库结束************************//
	
}
