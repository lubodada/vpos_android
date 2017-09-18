package com.cnyssj.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//继承SQLiteOpenHelper类
public class Mysqlitedbhelp extends SQLiteOpenHelper {

	//数据库版本号	
	private static final int DATABASE_VERSION = 6;
	// 数据库名
	private static final String DATABASE_NAME = "my.db";

	// 数据表名，一个数据库中可以有多个表
	// 商品信息表名
	public static final String Goods_TABLE_NAME = "goods";
	// 订单表名
	public static final String Order_TABLE_NAME = "orders";
	// 订单商品详情表名
	public static final String Orderdetail_TABLE_NAME = "orderdetail";
	// 订单统计表名
	public static final String OrderSum_TABLE_NAME = "ordersum";
	// 盘点表名
	public static final String Check_TABLE_NAME = "checks";
	// 盘点明细表名
	public static final String CheckDetail_TABLE_NAME = "checkdetail";
	// 盘点管理表名
	public static final String CheckManage_TABLE_NAME = "check_manage";
	// 入库表名
	public static final String Storage_TABLE_NAME = "storage";
	//入库明细表名
	public static final String StorageDetail_TABLE_NAME = "storagedetail";
	//入库明管理表名
	public static final String StorageManage_TABLE_NAME = "storage_manage";

	public Mysqlitedbhelp(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// Mysqlitedbhelp的构造函数参数：
		// context：上下文环境   name：数据库名字    factory：游标工厂（可选） version：数据库模型版本号
	}

	//首次使用软件时生成数据库表
	@Override
	public void onCreate(SQLiteDatabase db) {

		//商品表 
		//id:商品ID(主键)，title：商品类型，productsn：商品条码，price：商品价格，thumb：商品描述
		db.execSQL(" CREATE TABLE goods(id VARCHAR(20) PRIMARY KEY,title VARCHAR(20), productsn VARCHAR(30), "
				+ " price DOUBLE(20), thumb VARCHAR(1000) ) ");

		//商品订单详情表
		//id:商品id，name：商品名，quantity：商品数量，amount：消费金额
		db.execSQL(" CREATE TABLE orderdetail( _id INTEGER PRIMARY KEY AUTOINCREMENT,id VARCHAR(30),name VARCHAR(20),"
				+ " quantity DOUBLE(20), amount DOUBLE(20) ) ");

		//订单表
		//myorderid:订单id(自己),orderid:订单编号(掌游宝)，type：交易类型，amount：消费金额，items：消费物品（json类型），paymentstatus:状态 ，discount：优惠金额，
		//couponid：优惠券id，remark：备注,ordertime:时间, mnumber：用户付款码, uploadstatus:上传状态, orderstatus:订单状态
		db.execSQL(" CREATE TABLE orders( _id INTEGER PRIMARY KEY AUTOINCREMENT,myorderid VARCHAR(30),orderid VARCHAR(30),type INTEGER, amount DOUBLE(20), "
				+ " items VARCHAR(1000), paymentstatus INTEGER, discount DOUBLE(20), couponid VARCHAR(20), remark VARCHAR(100), ordertime VARCHAR(30),"
				+ " mnumber VARCHAR(20), uploadstatus INTEGER, orderstatus INTEGER ) ");
		//订单统计表
		//paytype：支付方式，amount：总金额，ordersumtime:时间
		db.execSQL(" CREATE TABLE ordersum( _id INTEGER PRIMARY KEY AUTOINCREMENT,paytype INTEGER, amount DOUBLE(20), ordersumtime VARCHAR(30) ) ");

		// 创建filedownlog表(商品列表下载进度记录)
		db.execSQL("CREATE TABLE IF NOT EXISTS filedownlog (id integer primary key autoincrement, downpath varchar(100), threadid INTEGER, downlength INTEGER)");

		//盘点表 
		//count_id:盘点唯一id(主键),title：商品名称，productsn：商品条码，quantity：商品数量，createtime：插入商品时间，updatetime:更新时间，
		//count_time:盘点时间，status:上传状态,id:盘点商品ID，shelf:货架,checker:盘点人,remark:备注
		db.execSQL(" CREATE TABLE checks(_id INTEGER PRIMARY KEY AUTOINCREMENT,id VARCHAR(20), title VARCHAR(20), productsn VARCHAR(30),"
				+ " quantity DOUBLE(20), createtime VARCHAR(20), updatetime VARCHAR(20), count_time VARCHAR(20),"
				+ " status INTEGER, count_id VARCHAR(20), shelf VARCHAR(20), checker VARCHAR(20), remark VARCHAR(20) ) ");
		//盘点明细表 
		//count_id:盘点唯一id(主键),title：商品名称，productsn：商品条码，quantity：商品数量，updatetime:更新时间，
		//updatetype:更新方式
		db.execSQL(" CREATE TABLE checkdetail(_id INTEGER PRIMARY KEY AUTOINCREMENT,count_id VARCHAR(20), title VARCHAR(20), productsn VARCHAR(30),"
				+ " quantity DOUBLE(20), updatetime VARCHAR(20), updatetype INTEGER ) ");

		// 创建盘点管理表
		//count_id: 盘点唯一id，check_status：盘点状态，count_time:盘点时间，remark：备注
		db.execSQL("CREATE TABLE check_manage (count_id VARCHAR(20) PRIMARY KEY, check_status INTEGER, count_time VARCHAR(20),remark VARCHAR(20))");
		//入库表 
		//count_id:入库唯一ID(主键)，title：商品名称，productsn：商品条码，quantity：商品数量，createtime：插入商品时间，updatetime:更新时间，
		//count_time:入库时间，status:上传状态,id:入库商品id,shelf:货架,checker:入库人,remark:备注，update_status：数量更新状态
		db.execSQL(" CREATE TABLE storage(_id INTEGER PRIMARY KEY AUTOINCREMENT,id VARCHAR(20), title VARCHAR(20), productsn VARCHAR(30),"
				+ " quantity DOUBLE(20), createtime VARCHAR(20), updatetime VARCHAR(20), count_time VARCHAR(20),"
				+ " status INTEGER, count_id VARCHAR(20), shelf VARCHAR(20), storage_people VARCHAR(20), remark VARCHAR(20),update_status INTEGER ) ");

		//入库明细表 
		//count_id:入库唯一id(主键),title：商品名称，productsn：商品条码，quantity：商品数量，updatetime:更新时间，
		//updatetype:更新方式
		db.execSQL(" CREATE TABLE storagedetail(_id INTEGER PRIMARY KEY AUTOINCREMENT,count_id VARCHAR(20), title VARCHAR(20), productsn VARCHAR(30),"
				+ " quantity DOUBLE(20), updatetime VARCHAR(20), updatetype INTEGER ) ");
		// 创建入库管理表
		//count_id: 入库唯一id，storage_status：入库状态，count_time:入库时间，remark：备注
		db.execSQL("CREATE TABLE storage_manage (count_id VARCHAR(20) PRIMARY KEY, storage_status INTEGER, count_time VARCHAR(20),remark VARCHAR(20))");
	}

	/**
	 * 软件版本号发生改变时调用
	 * 在数据库的版本发生变化时会被调用， 
	 * 一般在软件升级时才需改变版本号，
	 * 而数据库的版本是由程序员控制的，
	 * 假设数据库现在的 版本是1，
	 * 由于业务的变更，修改了数据库表结构，
	 * 这时候就需要升级软件，
	 * 升级软件时希望 更新用户手机里的数据库表结构，
	 * 为了实现这一目的，
	 * 可以把原来的数据库版本设置为2 
	 * 或者其他与旧版本号不同的数字即可！
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// 调用时间：如果DATABASE_VERSION值被改为别的数,系统发现现有数据库版本不同,即会调用onUpgrade
		db.execSQL(" ALTER TABLE orders ADD orderstatus INTEGER ");

	}

	@Override
	public void onOpen(SQLiteDatabase db)
	{
		super.onOpen(db);
		// 每次打开数据库之后首先被执行

//		Log.d("Mysqlitedbhelp", "DatabaseHelper 打开");
	}

	public Mysqlitedbhelp(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// 数据库实际被创建是在getWritableDatabase()或getReadableDatabase()方法调用时
		Log.d("Mysqlitedbhelp", "DatabaseHelper Constructor");

	}
	/**
	 * 删除数据库
	 * @param context
	 */
	public void deleteDatabase(Context context) {
		context.deleteDatabase(DATABASE_NAME);
	}

}
