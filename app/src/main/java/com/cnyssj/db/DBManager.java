package com.cnyssj.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cnyssj.db.util.Goods;
import com.cnyssj.db.util.Order;
import com.cnyssj.db.util.OrderPaySum;
import com.cnyssj.db.util.Orderdetail;

/**
 * 管理类，持有数据库对象，对各种操作进行了封装
 *
 * @author lubo
 */
public class DBManager {

    private Mysqlitedbhelp helper;
    private SQLiteDatabase db;
    private String tag = "DBManager";

    public DBManager(Context context) {
        Log.d("DBManager", "DBManager开始");
        helper = new Mysqlitedbhelp(context);
        // 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
        // mFactory);
        // 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        // 删除数据库(测试用)
//		helper.deleteDatabase(context);
        db = helper.getWritableDatabase();
    }

    /**
     * 添加商品信息
     *
     * @param goos
     */
    public String addallFoods(List<Goods> goods) {

        db = helper.getWritableDatabase();
        // 设置标签 "0"为添加数据失败
        String flag = "0";
        Log.d(tag, "正在添加商品数据....");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            for (Goods good : goods) {
                db.execSQL(" INSERT INTO " + Mysqlitedbhelp.Goods_TABLE_NAME
                        + " VALUES(?, ?, ?, ?, ?)", new Object[]{
                        good.getId(), good.getTitle(), good.getProductsn(),
                        good.getPrice(), good.getThumb()});
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
            // "1"为发货成功
            flag = "1";
            Log.d(tag, "商品数据添加成功");
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(tag, "添加商品数据失败");
            e.printStackTrace();
        } finally {
            db.endTransaction(); // 结束事务
            db.close();
        }

        return flag;
    }

    /**
     * 添加订单商品详情
     *
     * @param orderdetails
     */
    public String addOrderdetail(List<Orderdetail> orderdetails) {
        db = helper.getWritableDatabase();
        // 设置标签 "0"为添加数据失败
        String flag = "0";
        Log.d(tag, "正在添加订单商品详情....");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            for (Orderdetail orderdetail : orderdetails) {
                db.execSQL(
                        " INSERT INTO " + Mysqlitedbhelp.Orderdetail_TABLE_NAME
                                + " VALUES(null, ?, ?, ?, ?)",
                        new Object[]{orderdetail.getId(),
                                orderdetail.getName(),
                                orderdetail.getQuantity(),
                                orderdetail.getAmount()});
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
            // "1"为发货成功
            flag = "1";
            Log.d(tag, "商品订单详情添加成功");
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(tag, "没有获得商品订单详情");
            e.printStackTrace();
        } finally {
            db.endTransaction(); // 结束事务
            db.close();
        }
        return flag;
    }

    /**
     * 添加订单信息
     *
     * @param orders
     */
    public String addOrder(List<Order> orders) {
        db = helper.getWritableDatabase();
        // 设置标签 "0"为添加数据失败
        String flag = "0";
        Log.d(tag, "正在添加订单信息....");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            for (Order order : orders) {
                db.execSQL(
                        " INSERT INTO "
                                + Mysqlitedbhelp.Order_TABLE_NAME
                                + " VALUES(null,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new Object[]{order.getMyorderid(),
                                order.getOrderid(), order.getType(),
                                order.getAmount(), order.getItems(),
                                order.getPaymentstatus(), order.getDiscount(),
                                order.getCouponid(), order.getRemark(),
                                order.getOrdertime(), order.getMnumber(),
                                order.getUploadstatus(), order.getOrderstatus()});
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
            // "1"为发货成功
            flag = "1";
            Log.d(tag, "商品订单添加成功");
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(tag, "商品订单添加失败");
            e.printStackTrace();
        } finally {
            db.endTransaction(); // 结束事务
            db.close();
        }

        return flag;
    }

    /**
     * 查询订单表，分别统计各支付类型的交易金额总和 type--交易类型 0 未知 1 现金支付 2 余额支付 3 支付宝支付 4 微信支付 5
     * 银联支付 6 翼支付 paymentstatus 支付状态 1 未支付 2 部分支付 3 已支付 4 退款中 5 部分退款 6 全额退款
     *
     * @return SELECT Customer,SUM(OrderPrice) FROM Orders GROUP BY Customer
     */
    public List<OrderPaySum> queryOrderPaySum(String starttime,
                                              String stoptime, String tradingtime) {

        List<OrderPaySum> orderPaySums = new ArrayList<OrderPaySum>();
        db = helper.getReadableDatabase();

        String sql = " SELECT paymentstatus,ordertime,type,SUM(amount) AS amount FROM "
                + Mysqlitedbhelp.Order_TABLE_NAME
                + " where ordertime >= ? and ordertime <= ? and paymentstatus = ? GROUP BY type ";
        Cursor mCursor = db.rawQuery(sql, new String[]{starttime, stoptime,
                String.valueOf(3)});
        while (mCursor.moveToNext()) {
            OrderPaySum orderPaySum = new OrderPaySum();

            orderPaySum.setPaytype(mCursor.getInt(mCursor
                    .getColumnIndex("type")));
            orderPaySum.setAmount(mCursor.getDouble(mCursor
                    .getColumnIndex("amount")));
            orderPaySum.setOrdersumtime(tradingtime);
            ;
            orderPaySums.add(orderPaySum);
        }

        return orderPaySums;
    }

    /**
     * 添加订单金额统计信息到订单金额统计表(OrderSum_TABLE_NAME)
     *
     * @param paySums
     */
    public String addOrderPaySumData(List<OrderPaySum> orderPaySums) {
        db = helper.getWritableDatabase();
        // 设置标签 "0"为添加数据失败
        String flag = "0";
        Log.d(tag, "正在添加订单金额统计信息....");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            for (OrderPaySum orderPaySum : orderPaySums) {
                db.execSQL(
                        " INSERT INTO " + Mysqlitedbhelp.OrderSum_TABLE_NAME
                                + " VALUES(null,?, ?, ?) ",
                        new Object[]{orderPaySum.getPaytype(),
                                orderPaySum.getAmount(),
                                orderPaySum.getOrdersumtime()});
            }

            db.setTransactionSuccessful(); // 设置事务成功完成
            // "1"为发货成功
            flag = "1";
            Log.d(tag, "订单金额统计信息添加成功");
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(tag, "订单金额统计信息添加失败");
            e.printStackTrace();
        } finally {
            db.endTransaction(); // 结束事务
            db.close();
        }

        return flag;
    }

    /**
     * 查询订单金额统计表中各支付类型的"日/月/年统计信息"
     *
     * @param starttime 开始时间 例：2017-03-03 17:18:19
     * @param stoptime  结束时间
     * @return
     */
    public List<OrderPaySum> queryOrderPaySumDate(String starttime,
                                                  String stoptime) {
        db = helper.getReadableDatabase();
        // 截取时间 例：2017-03-03 10:10:10 ----- 2017-03-03
        String Starttime = (String) starttime.subSequence(0, 10);
        String Endtime = (String) stoptime.subSequence(0, 10);

        List<OrderPaySum> orderPaySums = new ArrayList<OrderPaySum>();

        Cursor c = queryMonthpaysum(Starttime, Endtime);

        if (c == null || "".equals(c)) {
            Log.d(tag, "订单金额统计表没有数据");
            return orderPaySums;
        }

        while (c.moveToNext()) {
            OrderPaySum orderPaySum = new OrderPaySum();

            orderPaySum.setPaytype(c.getInt(c.getColumnIndex("paytype")));
            orderPaySum.setAmount(c.getDouble(c.getColumnIndex("amount")));
            orderPaySum.setOrdersumtime(c.getString(c
                    .getColumnIndex("ordersumtime")));

            orderPaySums.add(orderPaySum);
        }
        c.close();
        db.close();

        return orderPaySums;
    }

    // select * from tblName where rDate Between '2008-06-10' and '2008-06-12'
    public Cursor queryMonthpaysum(String Starttime, String Endtime) {

        Cursor c = db.rawQuery(" SELECT * FROM "
                        + Mysqlitedbhelp.OrderSum_TABLE_NAME
                        + " where ordersumtime >= ? and ordersumtime <= ? ",
                new String[]{Starttime, Endtime});

        return c;
    }

    /**
     * 根据订单编号(自己的)查询订单信息
     *
     * @return
     */
    public List<Order> queryOrderByMyorderid(String myorderid) {
        db = helper.getReadableDatabase();
        List<Order> orders = new ArrayList<Order>();

        Cursor c = queryorderbyMyorderid(myorderid);

        if (c == null || "".equals(c)) {
            Log.d(tag, "没有订单编号(自己的)为" + myorderid + "的数据");
            return orders;
        }

        while (c.moveToNext()) {
            Order order = new Order();
            order.setMyorderid(c.getString(c.getColumnIndex("myorderid")));
            order.setOrderid(c.getString(c.getColumnIndex("orderid")));
            order.setType(c.getInt(c.getColumnIndex("type")));
            order.setAmount(c.getString(c.getColumnIndex("amount")));
            order.setItems(c.getString(c.getColumnIndex("items")));
            order.setPaymentstatus(c.getInt(c.getColumnIndex("paymentstatus")));
            order.setDiscount(c.getDouble(c.getColumnIndex("discount")));
            order.setCouponid(c.getString(c.getColumnIndex("couponid")));
            order.setRemark(c.getString(c.getColumnIndex("remark")));
            order.setOrdertime(c.getString(c.getColumnIndex("ordertime")));
            order.setMnumber(c.getString(c.getColumnIndex("mnumber")));
            order.setUploadstatus(c.getInt(c.getColumnIndex("uploadstatus")));
            order.setOrderstatus(c.getInt(c.getColumnIndex("orderstatus")));
            orders.add(order);
        }
        c.close();
        db.close();

        return orders;
    }

    public Cursor queryorderbyMyorderid(String myorderid) {
        Cursor c = db.rawQuery(" SELECT * FROM "
                        + Mysqlitedbhelp.Order_TABLE_NAME + " where myorderid = ? ",
                new String[]{myorderid});
        return c;
    }

    /**
     * 根据订单编号(掌游宝)查询订单信息
     *
     * @return
     */
    public List<Order> queryOrderByOrderid(String orderid) {

        db = helper.getReadableDatabase();
        List<Order> orders = new ArrayList<Order>();

        Cursor c = queryorderbyOrderid(orderid);

        if (c == null || "".equals(c)) {
            Log.d(tag, "没有订单编号(掌游宝)为" + orderid + "的数据");
            return orders;
        }

        while (c.moveToNext()) {
            Order order = new Order();
            order.setMyorderid(c.getString(c.getColumnIndex("myorderid")));
            order.setOrderid(c.getString(c.getColumnIndex("orderid")));
            order.setType(c.getInt(c.getColumnIndex("type")));
            order.setAmount(c.getString(c.getColumnIndex("amount")));
            order.setItems(c.getString(c.getColumnIndex("items")));
            order.setPaymentstatus(c.getInt(c.getColumnIndex("paymentstatus")));
            order.setDiscount(c.getDouble(c.getColumnIndex("discount")));
            order.setCouponid(c.getString(c.getColumnIndex("couponid")));
            order.setRemark(c.getString(c.getColumnIndex("remark")));
            order.setOrdertime(c.getString(c.getColumnIndex("ordertime")));
            order.setMnumber(c.getString(c.getColumnIndex("mnumber")));
            order.setUploadstatus(c.getInt(c.getColumnIndex("uploadstatus")));
            order.setOrderstatus(c.getInt(c.getColumnIndex("orderstatus")));
            orders.add(order);
        }
        c.close();
        db.close();

        return orders;
    }

    public Cursor queryorderbyOrderid(String orderid) {
        Cursor c = db.rawQuery(" SELECT * FROM "
                        + Mysqlitedbhelp.Order_TABLE_NAME + " where orderid = ? ",
                new String[]{orderid});

        return c;
    }

    /**
     * 查询订单表中所有订单信息
     *
     * @return
     */
    public List<Order> queryAllOrder() {
        db = helper.getReadableDatabase();
        List<Order> orders = new ArrayList<Order>();

        Cursor c = queryallorder();

        if (c == null || "".equals(c)) {
            Log.d(tag, "订单表中没有数据");
            return orders;
        }

        while (c.moveToNext()) {
            Order order = new Order();
            order.setMyorderid(c.getString(c.getColumnIndex("myorderid")));
            order.setOrderid(c.getString(c.getColumnIndex("orderid")));
            order.setType(c.getInt(c.getColumnIndex("type")));
            order.setAmount(c.getString(c.getColumnIndex("amount")));
            order.setItems(c.getString(c.getColumnIndex("items")));
            order.setPaymentstatus(c.getInt(c.getColumnIndex("paymentstatus")));
            order.setDiscount(c.getDouble(c.getColumnIndex("discount")));
            order.setCouponid(c.getString(c.getColumnIndex("couponid")));
            order.setRemark(c.getString(c.getColumnIndex("remark")));
            order.setOrdertime(c.getString(c.getColumnIndex("ordertime")));
            order.setMnumber(c.getString(c.getColumnIndex("mnumber")));
            order.setUploadstatus(c.getInt(c.getColumnIndex("uploadstatus")));
            order.setOrderstatus(c.getInt(c.getColumnIndex("orderstatus")));
            orders.add(order);
        }
        c.close();
        db.close();

        return orders;
    }

    public Cursor queryallorder() {
        Cursor c = db.rawQuery(" SELECT * FROM "
                + Mysqlitedbhelp.Order_TABLE_NAME, null);
        return c;
    }

    /**
     * 根据订单交易状态和上传状态查询订单信息
     *
     * @return
     */
    public List<Order> queryOrderBystatus() {

        db = helper.getReadableDatabase();
        List<Order> orders = new ArrayList<Order>();

        Cursor c = queryorderbystatus();

        if (c == null || "".equals(c)) {
            return orders;
        }

        while (c.moveToNext()) {
            Order order = new Order();
            order.setMyorderid(c.getString(c.getColumnIndex("myorderid")));
            order.setOrderid(c.getString(c.getColumnIndex("orderid")));
            order.setType(c.getInt(c.getColumnIndex("type")));
            order.setAmount(c.getString(c.getColumnIndex("amount")));
            order.setItems(c.getString(c.getColumnIndex("items")));
            order.setPaymentstatus(c.getInt(c.getColumnIndex("paymentstatus")));
            order.setDiscount(c.getDouble(c.getColumnIndex("discount")));
            order.setCouponid(c.getString(c.getColumnIndex("couponid")));
            order.setRemark(c.getString(c.getColumnIndex("remark")));
            order.setOrdertime(c.getString(c.getColumnIndex("ordertime")));
            order.setMnumber(c.getString(c.getColumnIndex("mnumber")));
            order.setUploadstatus(c.getInt(c.getColumnIndex("uploadstatus")));
            order.setOrderstatus(c.getInt(c.getColumnIndex("orderstatus")));
            orders.add(order);
        }
        c.close();
        db.close();

        return orders;
    }

    public Cursor queryorderbystatus() {
        Cursor c = db.rawQuery(" SELECT * FROM "
                        + Mysqlitedbhelp.Order_TABLE_NAME
                        + " where paymentstatus = ? and  uploadstatus= ? ",
                new String[]{"3", "0"});

        return c;
    }

    /**
     * 查询100条商品信息
     *
     * @return
     */
    public List<Goods> queryFoods2() {
        List<Goods> foods = new ArrayList<Goods>();
        Cursor c = queryfoods2();

        if (c == null || "".equals(c)) {
            Log.d(tag, "查询无数据");
            return foods;
        }

        while (c.moveToNext()) {
            Goods food = new Goods();
            food.setId(c.getString(c.getColumnIndex("id")));
            food.setPrice(c.getDouble(c.getColumnIndex("price")));
            food.setProductsn(c.getString(c.getColumnIndex("productsn")));
            food.setThumb(c.getString(c.getColumnIndex("thumb")));
            food.setTitle(c.getString(c.getColumnIndex("title")));
            foods.add(food);
        }
        c.close();
        db.close();

        return foods;
    }

    public Cursor queryfoods2() {
        // select * from table_name limit 0,10
        Cursor c = db.rawQuery(" SELECT id,price,productsn,thumb,title FROM "
                        + Mysqlitedbhelp.Goods_TABLE_NAME + " limit ?,? ",
                new String[]{"0", "100"});

        return c;
    }

    /**
     * 查询所有商品信息
     *
     * @return foods
     */
    public List<Goods> queryallGoods() {
        List<Goods> foods = new ArrayList<Goods>();
        Cursor c = queryallgoods();

        if (c == null || "".equals(c)) {
            Log.d(tag, "商品表中没有数据");
            return foods;
        }

        while (c.moveToNext()) {
            Goods food = new Goods();
            food.setId(c.getString(c.getColumnIndex("id")));
            food.setPrice(c.getDouble(c.getColumnIndex("price") / 100));
            food.setProductsn(c.getString(c.getColumnIndex("productsn")));
            food.setThumb(c.getString(c.getColumnIndex("thumb")));
            food.setTitle(c.getString(c.getColumnIndex("title")));
            foods.add(food);
        }
        c.close();
        db.close();

        return foods;
    }

    public Cursor queryallgoods() {
        Cursor c = db.rawQuery(" SELECT id,price,productsn,thumb,title FROM "
                + Mysqlitedbhelp.Goods_TABLE_NAME, null);

        return c;
    }

    /**
     * 删除所有数据
     */
    public void deleteallData() {
        try {
            db.delete(Mysqlitedbhelp.Goods_TABLE_NAME, "id >= ?",
                    new String[]{"0"});
            Log.d(tag, "成功删除所有的数据");
        } catch (Exception e) {
            Log.d(tag, "删除所有的数据出错了");
            // TODO: handle exception
        }

    }

    /**
     * 删除指定数据
     */
    public void deletesomeData() {
        try {
            db.delete(Mysqlitedbhelp.Goods_TABLE_NAME, " id = ? ",
                    new String[]{"5"});
            Log.d(tag, "成功删除指定的数据");
        } catch (Exception e) {
            Log.d(tag, "删除指定的数据出错了");
            // TODO: handle exception
        }

    }

    /**
     * 订单支付状态更新
     *
     * @param myorderid     订单ID(自己的)
     * @param paymentstatus 状态
     * @return flag
     */
    public boolean updatePayStatus(String myorderid, int paymentstatus) {

        db = helper.getWritableDatabase();
        boolean flag = false;
        ContentValues cv = new ContentValues();
        cv.put("paymentstatus", paymentstatus);
        int c = db.update(Mysqlitedbhelp.Order_TABLE_NAME, cv,
                " myorderid = ? ", new String[]{myorderid});
        if (c == 0) {
            Log.d(tag, "订单支付状态更新失败");
            return flag;
        } else {
            flag = true;
            Log.d(tag, "订单支付状态更新成功");

        }

        return flag;

    }

    /**
     * 订单上传状态更新
     *
     * @param myorderid     订单ID(自己的)
     * @param paymentstatus 状态
     * @return flag
     */
    public boolean updateUploadStatus(String myorderid, int uploadstatus) {

        db = helper.getWritableDatabase();
        boolean flag = false;
        ContentValues cv = new ContentValues();
        cv.put("uploadstatus", uploadstatus);
        int c = db.update(Mysqlitedbhelp.Order_TABLE_NAME, cv,
                " myorderid = ? ", new String[]{myorderid});
        if (c == 0) {
            Log.d(tag, "订单上传状态更新失败");
            return flag;
        } else {
            flag = true;
            Log.d(tag, "订单上传状态更新成功");

        }

        return flag;

    }

    /**
     * 支付方式更新
     *
     * @param myorderid 订单ID(自己的)
     * @param type      支付方式 0 未知 1 现金支付 2 余额支付 3 支付宝支付 4 微信支付 5 银联支付 6 翼支付
     * @return flag
     */
    public boolean updatePayType(String myorderid, int type) {

        db = helper.getWritableDatabase();
        boolean flag = false;
        ContentValues cv = new ContentValues();
        cv.put("type", type);
        int c = db.update(Mysqlitedbhelp.Order_TABLE_NAME, cv,
                " myorderid = ? ", new String[]{myorderid});
        if (c == 0) {
            Log.d(tag, "订单支付方式更新失败");
            return flag;
        } else {
            flag = true;
            Log.d(tag, "订单支付方式更新成功");

        }

        return flag;

    }

    /**
     * 订单号(掌游宝)更新
     *
     * @param myorderid 订单ID(自己)
     * @param orderid   订单ID(掌游宝)
     * @return flag
     */
    public boolean updateOrderid(String myorderid, String orderid) {

        boolean flag = false;
        ContentValues cv = new ContentValues();
        cv.put("orderid", orderid);
        int c = db.update(Mysqlitedbhelp.Order_TABLE_NAME, cv,
                " myorderid = ? ", new String[]{myorderid});
        if (c == 0) {
            Log.d(tag, "订单号(掌游宝)更新失败");
            // return flag;
        } else {
            flag = true;
            Log.d(tag, "订单号(掌游宝)更新成功");
        }
        return flag;

    }

    /**
     * 时间更新
     *
     * @param myorderid 订单ID(自己的)
     * @param ordertime 时间
     * @param time      时间
     * @return
     */
    public boolean updatePayTime(String myorderid, String ordertime) {
        boolean flag = false;
        ContentValues cv = new ContentValues();
        cv.put("ordertime", ordertime);
        int c = db.update(Mysqlitedbhelp.Order_TABLE_NAME, cv,
                " myorderid = ? ", new String[]{myorderid});
        if (c == 0) {
            Log.d(tag, "时间更新失败");
            return flag;
        } else {
            flag = true;
            Log.d(tag, "时间更新成功");
        }
        return flag;

    }

    /**
     * 商品订单状态更新
     *
     * @param myorderid   订单ID(自己的)
     * @param orderstatus 订单状态
     * @return flag
     */
    public boolean updateOrderstatus(String myorderid, int orderstatus) {

        db = helper.getWritableDatabase();
        boolean flag = false;
        ContentValues cv = new ContentValues();
        cv.put("orderstatus", orderstatus);
        int c = db.update(Mysqlitedbhelp.Order_TABLE_NAME, cv, " myorderid = ? ",
                new String[]{myorderid});
        if (c == 0) {
            Log.d(tag, "订单状态更新失败");
            return flag;
        } else {
            flag = true;
            Log.d(tag, "订单状态更新成功");

        }

        return flag;

    }


    /**
     * 查询商品信息记录的总数
     *
     * @return length
     */
    public long getCount() {
        db = helper.getWritableDatabase();
        String sql = "select count(*) from " + Mysqlitedbhelp.Goods_TABLE_NAME;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        long length = c.getLong(0);
        c.close();
        return length;
    }

    /**
     * 拿到所有的商品信息条数
     *
     * @param firstResult 从第几条数据开始查询。
     * @param maxResult   每页显示多少条记录。
     * @return 当前页的记录
     */
    public Cursor getAllItems(int firstResult, int maxResult) {
        db = helper.getWritableDatabase();
        String sql = "select id as _id,title,productsn,price,thumb from "
                + Mysqlitedbhelp.Goods_TABLE_NAME + " limit ?,?";
        Cursor mCursor = db.rawQuery(
                sql,
                new String[]{String.valueOf(firstResult),
                        String.valueOf(maxResult)});
        return mCursor;
    }

    /**
     * 获取每条线程已经下载的文件长度
     *
     * @param path
     * @return
     */
    public Map<Integer, Integer> getData(String path) {
        db = helper.getReadableDatabase();
        Cursor cursor = db
                .rawQuery(
                        "select threadid, downlength from filedownlog where downpath=?",
                        new String[]{path});
        Map<Integer, Integer> data = new HashMap<Integer, Integer>();
        while (cursor.moveToNext()) {
            data.put(cursor.getInt(0), cursor.getInt(1));
        }
        cursor.close();
        db.close();
        return data;
    }

    /**
     * 保存每条线程已经下载的文件长度
     *
     * @param path
     * @param map
     */
    public void save(String path, Map<Integer, Integer> map) {
        // int threadid,int position
        db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
                db.execSQL(
                        "insert into filedownlog(downpath, threadid, downlength) values(?,?,?)",
                        new Object[]{path, entry.getKey(), entry.getValue()});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    /**
     * 实时更新每条线程已经下载的文件长度
     *
     * @param path
     * @param map
     */
    public void update(String path, int threadId, int pos) {
        db = helper.getWritableDatabase();
        db.execSQL(
                "update filedownlog set downlength=? where downpath=? and threadid=?",
                new Object[]{pos, path, threadId});
        db.close();
    }

    /**
     * 当文件下载完成后，删除对应的下载记录
     *
     * @param path
     */
    public void delete(String path) {
        db = helper.getWritableDatabase();
        db.execSQL("delete from filedownlog where downpath=?",
                new Object[]{path});
        db.close();
    }

    public double updateMoney(Goods goods) {
        String flag = "0";
        ContentValues cv = new ContentValues();
        cv.put("price", goods.getPrice());
        db.update(Mysqlitedbhelp.Goods_TABLE_NAME, cv, " name = ? ",
                new String[]{null});
        flag = "1";
        return Double.valueOf(flag);

    }

    /**
     * 查询最近一次订单记录的时间
     *
     * @return SELECT LAST(OrderPrice) AS LastOrderPrice FROM Orders
     */
    public String queryLastOrderTime() {
        String lastOrderTime = null;
        db = helper.getReadableDatabase();
        String sql = " SELECT ordertime FROM "
                + Mysqlitedbhelp.Order_TABLE_NAME
                + " ORDER BY ordertime DESC limit ?,?";
        Cursor mCursor = db.rawQuery(sql, new String[]{"0", "1"});
        while (mCursor.moveToNext()) {

            lastOrderTime = mCursor.getString(mCursor
                    .getColumnIndex("ordertime"));
        }

        return lastOrderTime;
    }

    /**
     * 查询最早的订单记录的时间
     *
     * @return SELECT LAST(OrderPrice) AS LastOrderPrice FROM Orders
     */
    public String queryFirstOrderTime() {
        String lastOrderTime = null;
        db = helper.getReadableDatabase();
        String sql = " SELECT ordertime FROM "
                + Mysqlitedbhelp.Order_TABLE_NAME
                + " ORDER BY ordertime ASC limit ?,?";
        Cursor mCursor = db.rawQuery(sql, new String[]{"0", "1"});
        while (mCursor.moveToNext()) {

            lastOrderTime = mCursor.getString(mCursor
                    .getColumnIndex("ordertime"));
        }

        return lastOrderTime;
    }

    /**
     * 查询上一次订单金额统计的时间
     *
     * @return SELECT LAST(OrderPrice) AS LastOrderPrice FROM Orders
     */
    public String queryLastOrderPaySumTime() {
        String lastOrderPaySumTime = null;
        db = helper.getReadableDatabase();
        String sql = " SELECT ordersumtime FROM "
                + Mysqlitedbhelp.OrderSum_TABLE_NAME
                + " ORDER BY ordersumtime DESC limit ?,?";
        Cursor mCursor = db.rawQuery(sql, new String[]{"0", "1"});
        while (mCursor.moveToNext()) {

            lastOrderPaySumTime = mCursor.getString(mCursor
                    .getColumnIndex("ordersumtime"));
        }

        return lastOrderPaySumTime;
    }

    /**
     * 查询订单记录的总数
     *
     * @return length
     */
    public long getOrderCount() {
        db = helper.getWritableDatabase();
        String sql = "select count(*) from " + Mysqlitedbhelp.Order_TABLE_NAME;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        long length = c.getLong(0);
        c.close();
        return length;
    }

    /**
     * 拿到所有的订单记录条数,分页查询
     *
     * @param firstResult 从第几条数据开始查询。
     * @param maxResult   每页显示多少条记录。
     * @param starttimes  开始时间
     * @param stoptimes   结束时间
     * @return 当前页的记录
     */
    public Cursor getAll_OrderItems(int firstResult, int maxResult,
                                    String starttimes, String stoptimes) {
        db = helper.getWritableDatabase();
        String sql = "select * from "
                + Mysqlitedbhelp.Order_TABLE_NAME
                + " where ordertime >= ? and ordertime <= ? ORDER BY ordertime DESC limit ?,? ";
        Cursor mCursor = db.rawQuery(sql, new String[]{starttimes, stoptimes,
                String.valueOf(firstResult), String.valueOf(maxResult)});
        return mCursor;
    }

    /**
     * 查询订单金额统计记录的总数
     *
     * @return length
     */
    public long getPaySumCount() {
        db = helper.getWritableDatabase();
        String sql = "select count(*) from "
                + Mysqlitedbhelp.OrderSum_TABLE_NAME;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        long length = c.getLong(0);
        c.close();
        return length;
    }

    /**
     * 根据时间段查询订单金额统计记录的总数
     *
     * @return length
     */
    public long getPaySumCountByTime(String starttimes, String stoptimes) {
        db = helper.getWritableDatabase();
        String sql = "select count(*) from "
                + Mysqlitedbhelp.OrderSum_TABLE_NAME
                + " where ordersumtime >= ? and ordersumtime <= ? ";
        Cursor c = db.rawQuery(sql, new String[]{starttimes, stoptimes});
        c.moveToFirst();
        long length = c.getLong(0);
        c.close();
        return length;
    }

    /**
     * 拿到所有的订单金额统计条数,分页查询
     *
     * @param firstResult 从第几条数据开始查询。
     * @param maxResult   每页显示多少条记录。
     * @param starttimes  开始时间
     * @param stoptimes   结束时间
     * @return 当前页的记录
     */
    public Cursor getAll_PayStatisticalItems(int firstResult, int maxResult,
                                             String starttimes, String stoptimes) {
        db = helper.getWritableDatabase();
        String sql = "select _id,paytype,amount,ordersumtime,SUM(amount) AS amount from "
                + Mysqlitedbhelp.OrderSum_TABLE_NAME
                + " where ordersumtime >= ? and ordersumtime <= ? GROUP BY paytype ORDER BY ordersumtime DESC limit ?,? ";
        Cursor mCursor = db.rawQuery(sql, new String[]{starttimes, stoptimes,
                String.valueOf(firstResult), String.valueOf(maxResult)});
        return mCursor;
    }

    /**
     * 查询订单表，拿到当天的的订单金额统计条数
     *
     * @param firstResult 从第几条数据开始查询。
     * @param maxResult   每页显示多少条记录。
     * @param starttimes  开始时间
     * @param stoptimes   结束时间
     * @return 当前页的记录
     */
    public Cursor getAll_TodayPayOrderItems(int firstResult, int maxResult,
                                            String starttimes, String stoptimes) {
        db = helper.getWritableDatabase();
        String sql = " SELECT _id,ordertime,type,SUM(amount) AS amount FROM "
                + Mysqlitedbhelp.Order_TABLE_NAME
                + " where ordertime >= ? and ordertime <= ? and paymentstatus = ? GROUP BY type ORDER BY ordertime DESC limit ?,? ";
        Cursor mCursor = db
                .rawQuery(sql,
                        new String[]{starttimes, stoptimes,
                                String.valueOf(3), String.valueOf(firstResult),
                                String.valueOf(maxResult)});
        return mCursor;
    }

    /**
     * 查询订单表，统计金额
     *
     * @param starttime 开始时间 例：2017-03-03 17:18:19
     * @param stoptime  结束时间
     * @return
     */
    public List<OrderPaySum> queryOrderPayDate(String starttime, String stoptime) {
        db = helper.getReadableDatabase();
        // 截取时间 例：2017-03-03 10:10:10 ----- 2017-03-03
        String Starttime = (String) starttime.subSequence(0, 10);
        String Endtime = (String) stoptime.subSequence(0, 10);

        List<OrderPaySum> orderPaySums = new ArrayList<OrderPaySum>();

        Cursor c = queryMonthpaysum(Starttime, Endtime);

        if (c == null || "".equals(c)) {
            Log.d(tag, "订单表没有数据");
            return orderPaySums;
        }

        while (c.moveToNext()) {
            OrderPaySum orderPaySum = new OrderPaySum();

            orderPaySum.setPaytype(c.getInt(c.getColumnIndex("type")));
            orderPaySum.setAmount(c.getDouble(c.getColumnIndex("amount")));
            orderPaySum.setOrdersumtime(c.getString(c
                    .getColumnIndex("ordertime")));

            orderPaySums.add(orderPaySum);
        }
        c.close();
        db.close();

        return orderPaySums;
    }

    // select * from tblName where rDate Between '2008-06-10' and '2008-06-12'
    public Cursor queryOrderpaysum(String Starttime, String Endtime) {

        Cursor c = db.rawQuery(" SELECT * FROM "
                + Mysqlitedbhelp.Order_TABLE_NAME
                + " where ordertime >= ? and ordertime <= ? ", new String[]{
                Starttime, Endtime});

        return c;
    }

    /**
     * 查询统计表，分别统计各支付类型的交易金额总和 type--交易类型 0 未知 1 现金支付 2 余额支付 3 支付宝支付 4 微信支付 5
     * 银联支付 6 翼支付
     *
     * @return SELECT Customer,SUM(OrderPrice) FROM Orders GROUP BY Customer
     */
    public List<OrderPaySum> queryOrderSum(String starttime, String stoptime,
                                           String tradingtime) {

        List<OrderPaySum> orderPaySums = new ArrayList<OrderPaySum>();
        db = helper.getReadableDatabase();

        String sql = " SELECT _id,paytype,amount,ordersumtime,SUM(amount) AS amount from "
                + Mysqlitedbhelp.OrderSum_TABLE_NAME
                + " where ordersumtime >= ? and ordersumtime <= ? GROUP BY paytype ";
        Cursor mCursor = db.rawQuery(sql, new String[]{starttime, stoptime});
        while (mCursor.moveToNext()) {
            OrderPaySum orderPaySum = new OrderPaySum();

            orderPaySum.setPaytype(mCursor.getInt(mCursor
                    .getColumnIndex("paytype")));
            orderPaySum.setAmount(mCursor.getDouble(mCursor
                    .getColumnIndex("amount")));
            orderPaySum.setOrdersumtime(tradingtime);
            ;
            orderPaySums.add(orderPaySum);
        }

        return orderPaySums;
    }

    /**
     * 根据支付状态查询订单信息
     *
     * @param paymentstatus 支付状态 1 未支付 2 部分支付 3 已支付 4 退款中 5 部分退款 6 全额退款 7支付中
     * @return
     */
    public List<Order> queryOrderByPaymentstatus(int paymentstatus, String starttime, String stoptime) {
        db = helper.getReadableDatabase();
        List<Order> orders = new ArrayList<Order>();

        Cursor c = queryorderbyPaymentstatus(paymentstatus, starttime, stoptime);

        if (c == null || "".equals(c)) {
            Log.d(tag, "没有支付状态为" + paymentstatus + "的订单数据");
            return orders;
        }

        while (c.moveToNext()) {
            Order order = new Order();
            order.setMyorderid(c.getString(c.getColumnIndex("myorderid")));
            order.setOrderid(c.getString(c.getColumnIndex("orderid")));
            order.setType(c.getInt(c.getColumnIndex("type")));
            order.setAmount(c.getString(c.getColumnIndex("amount")));
            order.setItems(c.getString(c.getColumnIndex("items")));
            order.setPaymentstatus(c.getInt(c.getColumnIndex("paymentstatus")));
            order.setDiscount(c.getDouble(c.getColumnIndex("discount")));
            order.setCouponid(c.getString(c.getColumnIndex("couponid")));
            order.setRemark(c.getString(c.getColumnIndex("remark")));
            order.setOrdertime(c.getString(c.getColumnIndex("ordertime")));
            order.setMnumber(c.getString(c.getColumnIndex("mnumber")));
            order.setUploadstatus(c.getInt(c.getColumnIndex("uploadstatus")));
            orders.add(order);
        }
        c.close();
        db.close();

        return orders;
    }

    public Cursor queryorderbyPaymentstatus(int paymentstatus,
                                            String starttime, String stoptime) {
        Cursor c = db
                .rawQuery(
                        " SELECT * FROM "
                                + Mysqlitedbhelp.Order_TABLE_NAME
                                + " where paymentstatus = ? and ordertime >= ? and ordertime <= ? ",
                        new String[]{String.valueOf(paymentstatus),
                                starttime, stoptime});

        return c;
    }

    /**
     * 根据时间段查询订单记录的总数
     *
     * @return length
     */
    public long getPayOrderCountByTime(String starttimes, String stoptimes) {
        db = helper.getWritableDatabase();
        String sql = "select count(*) from " + Mysqlitedbhelp.Order_TABLE_NAME
                + " where ordertime >= ? and ordertime <= ? ";
        Cursor c = db.rawQuery(sql, new String[]{starttimes, stoptimes});
        c.moveToFirst();
        long length = c.getLong(0);
        c.close();
        return length;
    }

    // ************************* 盘点开始 ***************************************

    /**
     * 添加盘点信息
     *
     * @param orders
     */
    public String addCheckData(List<Check> checks) {
        db = helper.getWritableDatabase();
        // 设置标签 "0"为添加数据失败
        String flag = "0";
        Log.d(tag, "正在添加盘点信息....");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            for (Check check : checks) {
                db.execSQL(
                        " INSERT INTO "
                                + Mysqlitedbhelp.Check_TABLE_NAME
                                + " VALUES(null,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new Object[]{check.getId(), check.getTitle(),
                                check.getProductsn(), check.getQuantity(),
                                check.getCreatetime(), check.getUpdatetime(),
                                check.getCount_time(), check.getStatus(),
                                check.getCount_id(), check.getShelf(),
                                check.getChecker(), check.getRemark()});
                // 带两个参数的execSQL()方法，采用占位符参数？，把参数值放在后面，顺序对应
                // 一个参数的execSQL()方法中，用户输入特殊字符时需要转义
                // 使用占位符有效区分了这种情况
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
            // "1"为添加成功
            flag = "1";
            Log.d(tag, "盘点信息添加成功");
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(tag, "盘点信息添加失败");
            e.printStackTrace();
        } finally {
            db.endTransaction(); // 结束事务
            db.close();
        }

        return flag;
    }

    /**
     * 根据商品条码查询商品信息
     *
     * @return
     */
    public List<Goods> queryGoodsByProductsn(String productsn) {
        db = helper.getReadableDatabase();
        List<Goods> foods = new ArrayList<Goods>();

        Cursor c = queryProductsnGoods(productsn);

        if (c == null || "".equals(c)) {
            Log.d(tag, "商品表中没有商品条码为" + productsn + "的商品信息");
            return foods;
        }

        while (c.moveToNext()) {
            Goods food = new Goods();
            food.setId(c.getString(c.getColumnIndex("id")));
            food.setPrice(c.getDouble(c.getColumnIndex("price")));
            food.setTitle(c.getString(c.getColumnIndex("title")));
            food.setProductsn(c.getString(c.getColumnIndex("productsn")));
            foods.add(food);
        }
        c.close();
        db.close();

        return foods;
    }

    public Cursor queryProductsnGoods(String productsn) {
        Cursor c = db.rawQuery(" SELECT id,price,title,productsn FROM "
                        + Mysqlitedbhelp.Goods_TABLE_NAME + " where productsn = ? ",
                new String[]{productsn});

        return c;
    }

    /**
     * 根据商品条码查询盘点信息
     *
     * @return
     */
    public List<Check> queryCheckByProductsn(String productsn, String count_id) {
        db = helper.getReadableDatabase();
        List<Check> checks = new ArrayList<Check>();

        Cursor c = querycheckByproductsn(productsn, count_id);

        if (c == null || "".equals(c)) {
            Log.d(tag, "盘点表中没有商品条码为" + productsn + "的商品信息");
            return checks;
        }

        while (c.moveToNext()) {
            Check check = new Check();
            check.setId(c.getString(c.getColumnIndex("id")));
            check.setTitle(c.getString(c.getColumnIndex("title")));
            check.setProductsn(c.getString(c.getColumnIndex("productsn")));
            check.setQuantity(c.getDouble(c.getColumnIndex("quantity")));
            check.setCreatetime(c.getString(c.getColumnIndex("createtime")));
            check.setUpdatetime(c.getString(c.getColumnIndex("updatetime")));
            check.setCount_time(c.getString(c.getColumnIndex("count_time")));
            check.setStatus(c.getInt(c.getColumnIndex("status")));
            check.setCount_id(c.getString(c.getColumnIndex("count_id")));
            check.setShelf(c.getString(c.getColumnIndex("shelf")));
            check.setChecker(c.getString(c.getColumnIndex("checker")));
            check.setRemark(c.getString(c.getColumnIndex("remark")));

            checks.add(check);
        }
        c.close();
        db.close();

        return checks;
    }

    public Cursor querycheckByproductsn(String productsn, String count_id) {
        Cursor c = db.rawQuery(" SELECT * FROM "
                + Mysqlitedbhelp.Check_TABLE_NAME
                + " where productsn = ? and count_id = ? ", new String[]{
                productsn, count_id});

        return c;
    }

    /**
     * 上传时根据盘点账号查询所有的盘点信息
     *
     * @param count_id 盘点账号
     * @return
     */
    public List<Check> queryCheckByCount_id(String count_id) {
        db = helper.getReadableDatabase();
        List<Check> checks = new ArrayList<Check>();

        Cursor c = querycheckByCount_id(count_id);

        if (c == null || "".equals(c)) {
            Log.d(tag, "盘点表中没有盘点账号为" + count_id + "的盘点信息");
            return checks;
        }

        while (c.moveToNext()) {
            Check check = new Check();
            check.setId(c.getString(c.getColumnIndex("id")));
            check.setTitle(c.getString(c.getColumnIndex("title")));
            check.setProductsn(c.getString(c.getColumnIndex("productsn")));
            check.setQuantity(c.getDouble(c.getColumnIndex("quantity")));
            check.setCreatetime(c.getString(c.getColumnIndex("createtime")));
            check.setUpdatetime(c.getString(c.getColumnIndex("updatetime")));
            check.setCount_time(c.getString(c.getColumnIndex("count_time")));
            check.setStatus(c.getInt(c.getColumnIndex("status")));
            check.setCount_id(c.getString(c.getColumnIndex("count_id")));
            check.setShelf(c.getString(c.getColumnIndex("shelf")));
            check.setChecker(c.getString(c.getColumnIndex("checker")));
            check.setRemark(c.getString(c.getColumnIndex("remark")));
            checks.add(check);
        }
        c.close();
        db.close();

        return checks;
    }

    public Cursor querycheckByCount_id(String count_id) {
        Cursor c = db.rawQuery(" SELECT * FROM "
                + Mysqlitedbhelp.Check_TABLE_NAME
                + " where count_id = ? and status = ? ", new String[]{
                count_id, "0"});

        return c;
    }

    /**
     * 用户退出时 根据上传状态查询所有未上传的盘点信息 注： 0：未上传 1：上传中 2：上传成功
     *
     * @return
     */
    public List<Check> queryAllCheckByStatus() {
        db = helper.getReadableDatabase();
        List<Check> checks = new ArrayList<Check>();

        Cursor c = queryallcheckBystatus();

        if (c == null || "".equals(c)) {
            Log.d(tag, "盘点表中没有上传状态为" + "0" + "的商品信息");
            return checks;
        }

        while (c.moveToNext()) {
            Check check = new Check();
            check.setId(c.getString(c.getColumnIndex("id")));
            check.setTitle(c.getString(c.getColumnIndex("title")));
            check.setProductsn(c.getString(c.getColumnIndex("productsn")));
            check.setQuantity(c.getDouble(c.getColumnIndex("quantity")));
            check.setCreatetime(c.getString(c.getColumnIndex("createtime")));
            check.setUpdatetime(c.getString(c.getColumnIndex("updatetime")));
            check.setCount_time(c.getString(c.getColumnIndex("count_time")));
            check.setStatus(c.getInt(c.getColumnIndex("status")));
            check.setCount_id(c.getString(c.getColumnIndex("count_id")));
            check.setShelf(c.getString(c.getColumnIndex("shelf")));
            check.setChecker(c.getString(c.getColumnIndex("checker")));
            check.setRemark(c.getString(c.getColumnIndex("remark")));

            checks.add(check);
        }
        c.close();
        db.close();

        return checks;
    }

    public Cursor queryallcheckBystatus() {
        Cursor c = db.rawQuery(" SELECT * FROM "
                        + Mysqlitedbhelp.Check_TABLE_NAME + " where status = ? ",
                new String[]{"0"});

        return c;
    }

    /**
     * 盘点商品上传状态批量更新
     *
     * @param checks 盘点上传商品集合
     * @param status 上传状态 0：未上传 1：上传中 2：上传成功
     * @return flag
     */
    public boolean updateCheckStatus(List<Check> checks, String status) {

        db = helper.getWritableDatabase();
        boolean flag = false;
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        for (Check check : checks) {

            int c = db.update(Mysqlitedbhelp.Check_TABLE_NAME, cv,
                    " productsn = ? ", new String[]{check.getProductsn()});
            if (c == 0) {
                Log.d(tag, "条码为" + check.getProductsn() + "的商品上传状态更新失败");
                return flag;
            } else {
                flag = true;
                Log.d(tag, "条码为" + check.getProductsn() + "的商品上传状态更新成功");

            }
        }

        return flag;

    }

    /**
     * 盘点商品上传状态更新
     *
     * @param productsn 商品条码
     * @param status    上传状态 0：未上传 1：上传中 2：上传成功
     * @return flag
     */
    public boolean updateCheckStatusByProductsn(String productsn, String status) {
        db = helper.getWritableDatabase();
        boolean flag = false;
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        int c = db.update(Mysqlitedbhelp.Check_TABLE_NAME, cv,
                " productsn = ? ", new String[]{productsn});
        if (c == 0) {
            Log.d(tag, "条码为" + productsn + "的商品上传状态更新失败");
            return flag;
        } else {
            flag = true;
            Log.d(tag, "条码为" + productsn + "的商品上传状态更新成功");
        }

        return flag;

    }

    /**
     * 盘点商品数量更新
     *
     * @param productsn 商品条码
     * @param quantity  现在数量
     * @return flag
     */
    public boolean updateCheckQuantity(String productsn, double quantity) {

        db = helper.getWritableDatabase();
        boolean flag = false;
        ContentValues cv = new ContentValues();
        cv.put("quantity", quantity);
        int c = db.update(Mysqlitedbhelp.Check_TABLE_NAME, cv,
                " productsn = ? ", new String[]{productsn});
        if (c == 0) {
            Log.d(tag, "盘点商品数量更新失败");
            return flag;
        } else {
            flag = true;
            Log.d(tag, "盘点商品数量更新成功");

        }

        return flag;

    }

    /**
     * 盘点表中时间更新
     *
     * @param productsn  商品条码
     * @param updatetime 更新时间
     * @return
     */
    public boolean updateCheckTime(String productsn, String updatetime) {
        boolean flag = false;
        ContentValues cv = new ContentValues();
        cv.put("updatetime", updatetime);
        int c = db.update(Mysqlitedbhelp.Check_TABLE_NAME, cv,
                " productsn = ? ", new String[]{productsn});
        if (c == 0) {
            Log.d(tag, "时间更新失败");
            return flag;
        } else {
            flag = true;
            Log.d(tag, "时间更新成功");
        }
        return flag;

    }

    /**
     * 查询盘点记录的总数
     *
     * @return length
     */
    public long getCheckSumCount() {
        db = helper.getWritableDatabase();
        String sql = "select count(*) from " + Mysqlitedbhelp.Check_TABLE_NAME;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        long length = c.getLong(0);
        c.close();
        return length;
    }

    /**
     * 查询盘点时间
     *
     * @return count_times
     */
    public String queryLastCheckTime() {
        String count_time = null;
        db = helper.getReadableDatabase();
        String sql = " SELECT count_time FROM "
                + Mysqlitedbhelp.Check_TABLE_NAME
                + " ORDER BY count_time DESC limit ?,?";
        Cursor mCursor = db.rawQuery(sql, new String[]{"0", "1"});
        while (mCursor.moveToNext()) {

            count_time = mCursor
                    .getString(mCursor.getColumnIndex("count_time"));
        }

        return count_time;
    }

    /**
     * 删除所有盘点数据
     */
    public void deleteAllCheckData() {
        db = helper.getWritableDatabase();
        try {
            db.delete(Mysqlitedbhelp.Check_TABLE_NAME, null, null);
            Log.d(tag, "成功删除盘点表中所有的数据");
        } catch (Exception e) {
            Log.d(tag, "删除盘点表中所有的数据失败了");
            // TODO: handle exception
        }

    }

    /**
     * 根据盘点账号删除盘点数据
     */
    public void deleteCheckDataByCount_id(String count_id) {
        db = helper.getWritableDatabase();
        try {
            db.delete(Mysqlitedbhelp.Check_TABLE_NAME, " count_id = ? ",
                    new String[]{count_id});
            Log.d(tag, "成功删除盘点账号为" + count_id + "的盘点数据");
        } catch (Exception e) {
            Log.d(tag, "删除盘点账号为" + count_id + "的盘点数据失败了");
            // TODO: handle exception
        }

    }

    /**
     * 拿到所有的盘点信息条数
     *
     * @param firstResult 从第几条数据开始查询。
     * @param maxResult   每页显示多少条记录。
     * @return 当前页的记录
     */
    public Cursor getAll_CheckItems(String count_id, int firstResult,
                                    int maxResult) {
        db = helper.getWritableDatabase();
        String sql = "select _id,id,title,productsn,quantity,count_id from "
                + Mysqlitedbhelp.Check_TABLE_NAME
                + " where count_id = ? limit ?,?";
        Cursor mCursor = db.rawQuery(
                sql,
                new String[]{count_id, String.valueOf(firstResult),
                        String.valueOf(maxResult)});
        return mCursor;
    }

    // ************盘点明细************//

    /**
     * 添加盘点明细
     *
     * @param orders
     */
    public String addCheckDetailData(List<CheckDetail> checkDetails) {
        db = helper.getWritableDatabase();
        // 设置标签 "0"为添加数据失败
        String flag = "0";
        Log.d(tag, "正在添加盘点明细信息....");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            for (CheckDetail checkDetail : checkDetails) {
                db.execSQL(
                        " INSERT INTO " + Mysqlitedbhelp.CheckDetail_TABLE_NAME
                                + " VALUES(null,?, ?, ?, ?, ?, ?)",
                        new Object[]{checkDetail.getCount_id(),
                                checkDetail.getTitle(),
                                checkDetail.getProductsn(),
                                checkDetail.getQuantity(),
                                checkDetail.getUpdatetime(),
                                checkDetail.getUpdatetype()});
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
            // "1"为添加成功
            flag = "1";
            Log.d(tag, "盘点明细信息添加成功");
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(tag, "盘点明细信息添加失败");
            e.printStackTrace();
        } finally {
            db.endTransaction(); // 结束事务
            db.close();
        }

        return flag;
    }

    /**
     * 查询盘点明细记录的总数
     *
     * @return length
     */
    public long getCheckDetailSumCount() {
        db = helper.getWritableDatabase();
        String sql = "select count(*) from "
                + Mysqlitedbhelp.CheckDetail_TABLE_NAME;
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        long length = c.getLong(0);
        c.close();
        return length;
    }

    /**
     * 拿到所有的盘点明细信息条数
     *
     * @param firstResult 从第几条数据开始查询。
     * @param maxResult   每页显示多少条记录。
     * @return 当前页的记录
     */
    public Cursor getAll_CheckDetailItems(String count_id, int firstResult,
                                          int maxResult) {
        db = helper.getWritableDatabase();
        String sql = "select _id,count_id,title,productsn,quantity,updatetime,updatetype from "
                + Mysqlitedbhelp.CheckDetail_TABLE_NAME
                + " where count_id = ? limit ?,?";
        Cursor mCursor = db.rawQuery(
                sql,
                new String[]{count_id, String.valueOf(firstResult),
                        String.valueOf(maxResult)});
        return mCursor;
    }

    /**
     * 删除所有盘点明细数据
     */
    public void deleteAllCheckDetailData() {
        db = helper.getWritableDatabase();
        try {
            db.delete(Mysqlitedbhelp.CheckDetail_TABLE_NAME, null, null);
            Log.d(tag, "成功删除盘点明细表中所有的数据");
        } catch (Exception e) {
            Log.d(tag, "删除盘点明细表中所有的数据失败了");
            // TODO: handle exception
        }

    }

    /**
     * 根据盘点账号删除盘点明细
     */
    public void deleteCheckDetailByCount_id(String count_id) {
        db = helper.getWritableDatabase();
        try {
            db.delete(Mysqlitedbhelp.CheckDetail_TABLE_NAME, " count_id = ? ",
                    new String[]{count_id});
            Log.d(tag, "成功删除盘点账号为" + count_id + "的盘点明细数据");
        } catch (Exception e) {
            Log.d(tag, "删除盘点账号为" + count_id + "的盘点明细数据失败了");
            // TODO: handle exception
        }

    }

    /**
     * 查询订单信息
     * 拿到所有的订单记录条数,分页查询
     *
     * @param firstResult 从第几条数据开始查询。
     * @param maxResult   每页显示多少条记录。
     * @param starttimes  开始时间
     * @param stoptimes   结束时间
     * @return orders
     */
    public List<Order> getOrdersItemsByOnLongClick(int firstResult, int maxResult, String starttimes, String stoptimes) {
        db = helper.getWritableDatabase();
        List<Order> orders = new ArrayList<Order>();
        Cursor c = getordersItemsByOnLongClick(firstResult, maxResult, starttimes, stoptimes);

        if (c == null || "".equals(c)) {
            Log.d(tag, "订单表中没有数据");
            return orders;
        }
        while (c.moveToNext()) {

            Order order = new Order();
            order.setMyorderid(c.getString(c.getColumnIndex("myorderid")));
            order.setOrderid(c.getString(c.getColumnIndex("orderid")));
            order.setType(c.getInt(c.getColumnIndex("type")));
            order.setAmount(c.getString(c.getColumnIndex("amount")));
            order.setItems(c.getString(c.getColumnIndex("items")));
            order.setPaymentstatus(c.getInt(c.getColumnIndex("paymentstatus")));
            order.setDiscount(c.getDouble(c.getColumnIndex("discount")));
            order.setCouponid(c.getString(c.getColumnIndex("couponid")));
            order.setRemark(c.getString(c.getColumnIndex("remark")));
            order.setOrdertime(c.getString(c.getColumnIndex("ordertime")));
            order.setMnumber(c.getString(c.getColumnIndex("mnumber")));
            order.setUploadstatus(c.getInt(c.getColumnIndex("uploadstatus")));
            order.setOrderstatus(c.getInt(c.getColumnIndex("orderstatus")));
            orders.add(order);
        }
        return orders;

    }

    public Cursor getordersItemsByOnLongClick(int firstResult, int maxResult, String starttimes, String stoptimes) {
        String sql = "select * from "
                + Mysqlitedbhelp.Order_TABLE_NAME
                + " where ordertime >= ? and ordertime <= ? ORDER BY ordertime DESC limit ?,? ";
        Cursor c = db.rawQuery(sql, new String[]{starttimes, stoptimes, String.valueOf(firstResult), String.valueOf(maxResult)});

        return c;
    }

    // ******************盘点管理***************//

    /**
     * 添加盘点管理信息
     *
     * @param orders
     */
    public String addCheckManageData(List<CheckManage> checkManages) {
        db = helper.getWritableDatabase();
        // 设置标签 "0"为添加数据失败
        String flag = "0";
        Log.d(tag, "正在添加盘点管理信息....");
        // 采用事务处理，确保数据完整性
        db.beginTransaction(); // 开始事务
        try {
            for (CheckManage checkManage : checkManages) {
                db.execSQL(
                        " INSERT INTO " + Mysqlitedbhelp.CheckManage_TABLE_NAME
                                + " VALUES(?, ?, ?, ?)",
                        new Object[]{checkManage.getCount_id(),
                                checkManage.getCheck_status(),
                                checkManage.getCount_time(),
                                checkManage.getRemark()});
            }
            db.setTransactionSuccessful(); // 设置事务成功完成
            // "1"为添加成功
            flag = "1";
            Log.d(tag, "盘点管理信息添加成功");
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(tag, "盘点管理信息添加失败");
            e.printStackTrace();
        } finally {
            db.endTransaction(); // 结束事务
            db.close();
        }

        return flag;
    }

    /**
     * 根据盘点唯一id查询盘点管理信息
     *
     * @return
     */
    public List<CheckManage> queryCheckManageByCount_id(String count_id) {
        db = helper.getReadableDatabase();
        List<CheckManage> checkManages = new ArrayList<CheckManage>();

        Cursor c = querycheckmanageBycount_id(count_id);

        if (c == null || "".equals(c)) {
            Log.d(tag, "盘点管理表中没有盘点号为" + count_id + "的信息");
            return checkManages;
        }

        while (c.moveToNext()) {
            CheckManage checkManage = new CheckManage();
            checkManage.setCount_id(c.getString(c.getColumnIndex("count_id")));
            checkManage.setCheck_status(c.getInt(c
                    .getColumnIndex("check_status")));
            checkManage.setCount_time(c.getString(c
                    .getColumnIndex("count_time")));
            checkManage.setRemark(c.getString(c.getColumnIndex("remark")));

            checkManages.add(checkManage);
        }
        c.close();
        db.close();

        return checkManages;
    }

    public Cursor querycheckmanageBycount_id(String count_id) {
        Cursor c = db.rawQuery(" SELECT * FROM "
                + Mysqlitedbhelp.CheckManage_TABLE_NAME
                + " where count_id = ? ", new String[]{count_id});

        return c;
    }

    /**
     * 查询盘点管理表中的信息
     *
     * @return
     */
    public List<CheckManage> queryAllCheckManage() {
        db = helper.getReadableDatabase();
        List<CheckManage> checkManages = new ArrayList<CheckManage>();

        Cursor c = queryallcheckmanage();

        if (c == null || "".equals(c)) {
            Log.d(tag, "盘点管理表中没有信息");
            return checkManages;
        }

        while (c.moveToNext()) {
            CheckManage checkManage = new CheckManage();
            checkManage.setCount_id(c.getString(c.getColumnIndex("count_id")));
            checkManage.setCheck_status(c.getInt(c
                    .getColumnIndex("check_status")));
            checkManage.setCount_time(c.getString(c
                    .getColumnIndex("count_time")));
            checkManage.setRemark(c.getString(c.getColumnIndex("remark")));

            checkManages.add(checkManage);
        }
        c.close();
        db.close();

        return checkManages;
    }

    public Cursor queryallcheckmanage() {
        Cursor c = db.rawQuery(" SELECT * FROM "
                + Mysqlitedbhelp.CheckManage_TABLE_NAME, null);

        return c;
    }

    /**
     * 查询盘点时间
     *
     * @return count_time
     */
    public String queryLastCheck_Count_Time() {
        String count_time = null;
        db = helper.getReadableDatabase();
        String sql = " SELECT count_time FROM "
                + Mysqlitedbhelp.CheckManage_TABLE_NAME
                + " ORDER BY count_time DESC limit ?,?";
        Cursor mCursor = db.rawQuery(sql, new String[]{"0", "1"});
        while (mCursor.moveToNext()) {

            count_time = mCursor
                    .getString(mCursor.getColumnIndex("count_time"));
        }

        return count_time;
    }

    /**
     * 盘点管理表中盘点状态更新
     *
     * @param count_id     盘点唯一id
     * @param check_status 盘点状态 0:未完成 1:已完成
     * @return
     */
    public boolean updateCheckManage_Check_status(String count_id,
                                                  int check_status) {
        boolean flag = false;
        ContentValues cv = new ContentValues();
        cv.put("check_status", check_status);
        int c = db.update(Mysqlitedbhelp.CheckManage_TABLE_NAME, cv,
                " count_id = ? ", new String[]{count_id});
        if (c == 0) {
            Log.d(tag, "盘点状态更新失败");
            return flag;
        } else {
            flag = true;
            Log.d(tag, "盘点状态更新成功");
        }
        return flag;

    }

    /**
     * 查询最近创建的盘点账号
     *
     * @return Count_id
     */
    public String queryLastCheckCount_id() {
        String count_id = "";
        db = helper.getReadableDatabase();
        String sql = " SELECT count_id FROM "
                + Mysqlitedbhelp.CheckManage_TABLE_NAME
                + " ORDER BY count_id DESC limit ?,?";
        Cursor mCursor = db.rawQuery(sql, new String[]{"0", "1"});
        while (mCursor.moveToNext()) {

            count_id = mCursor.getString(mCursor.getColumnIndex("count_id"));
        }

        return count_id;
    }

    /**
     * 查询最近盘点的盘点状态
     *
     * @return check_status 0:未完成 1：已完成 -1:无数据
     */
    public int queryLastCheckManage_Check_status() {
        int check_status = -1;
        db = helper.getReadableDatabase();
        String sql = " SELECT check_status FROM "
                + Mysqlitedbhelp.CheckManage_TABLE_NAME
                + " ORDER BY count_id DESC limit ?,?";
        Cursor mCursor = db.rawQuery(sql, new String[]{"0", "1"});
        while (mCursor.moveToNext()) {

            check_status = mCursor.getInt(mCursor
                    .getColumnIndex("check_status"));
        }

        return check_status;
    }

    /**
     * 根据盘点账号删除盘点管理数据
     */
    public void deleteCheckManageByCount_id(String count_id) {
        db = helper.getWritableDatabase();
        try {
            db.delete(Mysqlitedbhelp.CheckManage_TABLE_NAME, " count_id = ? ",
                    new String[]{count_id});
            Log.d(tag, "成功删除盘点账号为" + count_id + "的盘点管理数据");
        } catch (Exception e) {
            Log.d(tag, "删除盘点账号为" + count_id + "的盘点管理数据失败了");
            // TODO: handle exception
        }

    }

    // ************************* 盘点结束 ***************************************
    public void closeDB() {
        // 释放数据库资源
        db.close();
    }
}
