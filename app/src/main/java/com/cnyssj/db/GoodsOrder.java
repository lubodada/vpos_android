package com.cnyssj.db;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import com.cnyssj.db.util.Goods;
import com.cnyssj.db.util.Order;
import com.cnyssj.db.util.OrderPaySum;
import com.cnyssj.db.util.Orderdetail;
import com.jhj.pos.storage.Storage;
import com.jhj.pos.storage.StorageDetail;
import com.jhj.pos.storage.StorageManage;

/**
 * 商品订单详情操作类
 *
 * @author lb
 */
public class GoodsOrder {

    private static Context context;
    private DBManager dbManager;

    public GoodsOrder(Context context) {
        this.context = context;
        dbManager = new DBManager(context);
    }

    /**
     * 将扫码查询的商品信息放入orderdetails
     *
     * @param number   数量
     * @param allmoney 价钱
     * @return orderdetails 订单商品详情集合
     */
    public static List<Orderdetail> getGoods(List<Goods> goodlist) {

        List<Orderdetail> orderdetails = new ArrayList<Orderdetail>();
        double number = 2;
        double allmoney = number * goodlist.get(0).getPrice();

        for (Goods goods : goodlist) {
            Orderdetail orderdetail = new Orderdetail(goods.getId(),
                    goods.getTitle(), number, allmoney);
            orderdetails.add(orderdetail);
        }

        return orderdetails;

    }

    /**
     * 将商品详情转成json格式
     *
     * @param orderdetails
     * @return sBuffer.toString() 返回json格式字符串
     */
    public static String addJson(List<Orderdetail> orderdetails) {

        StringBuffer sBuffer = new StringBuffer();
        try {

            sBuffer.append("{\"" + "orderdetail" + "\"" + ":[");
            for (int i = 0; i < orderdetails.size(); i++) {

                sBuffer.append("{\"id\"" + ":" + "\""
                        + orderdetails.get(i).getId() + "\"");
                sBuffer.append("," + "\"neme\"" + ":" + "\""
                        + orderdetails.get(i).getName() + "\"");
                sBuffer.append("," + "\"quantity\"" + ":" + "\""
                        + orderdetails.get(i).getQuantity() + "\"");
                sBuffer.append("," + "\"amount\"" + ":" + "\""
                        + orderdetails.get(i).getAmount() + "\"" + "}" + ",");
            }
            sBuffer.deleteCharAt(sBuffer.length() - 1);
            sBuffer.append("]}");

            System.out.println(sBuffer);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sBuffer.toString();
    }

    /**
     * 将日期转换成时间戳
     *
     * @param time 时间
     * @param type 时间类型
     * @return 时间戳
     */
    public static String getTimestamp(String time, String type) {
        SimpleDateFormat sdr = new SimpleDateFormat(type, Locale.CHINA);
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
            Log.d("--444444---", times);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return times;
    }

    /**
     * 订单信息
     *
     * @param sBuffer
     */
    public static List<Order> addOrder(String myorderid, String orderid,
                                       String monery, int type, String sBuffer, int paymentstatus,
                                       String time, String mnumber, int uploadstatus) {

        List<Order> orders = new ArrayList<Order>();
        // 获取当前时间
        // 订单编号
        Order order = new Order();
        order.setMyorderid(myorderid);
        order.setOrderid(orderid);// 订单编号
        order.setType(type);// 支付类型 交易类型，1 现金支付 2 余额支付 3 支付宝支付 4 微信支付 5 银联支付 6
        // 翼支付
        order.setAmount(monery);// 消费金额
        order.setItems(sBuffer);// 消费物品集合(json格式)
        order.setPaymentstatus(paymentstatus);// 消费状态 1 未支付 2 部分支付 3 已支付 4 退款中 5
        // 部分退款 6 全额退款
        order.setDiscount(0.00);// 优惠金额
        order.setCouponid("");// 优惠券id
        order.setRemark("");// 备注
        order.setOrdertime(time);// 订单时间
        order.setMnumber(mnumber);
        order.setUploadstatus(uploadstatus);
        order.setOrderstatus(1);
        orders.add(order);
        return orders;
    }

    /**
     * 将订单信息封装进map
     *
     * @param orders
     * @return map
     */
    public static Map<String, String> addOrderMap(List<Order> orders) {

        String time = orders.get(0).getOrdertime();
        // 将当前日期转换成时间戳
        String times = getTimestamp(time, "yyyy-MM-dd HH:mm:ss");
        Map<String, String> map = new HashMap<String, String>();

        map.put("orderid", orders.get(0).getOrderid());// 订单编号
        map.put("type", String.valueOf(orders.get(0).getType()));// 支付类型
        map.put("amount", String.valueOf(orders.get(0).getAmount()));// 消费金额
        map.put("items", orders.get(0).getItems());// 消费物品集合(json格式)
        map.put("paymentstatus",
                String.valueOf(orders.get(0).getPaymentstatus()));// 消费状态
        map.put("discount", String.valueOf(orders.get(0).getDiscount()));// 优惠金额
        map.put("couponid", String.valueOf(orders.get(0).getCouponid()));// 优惠券id
        map.put("remark", orders.get(0).getRemark());// 备注
        map.put("ordertime", times);// 上传订单时间

        return map;
    }

    /**
     * 以HttpClient的DoPost方式提交数据到服务器
     *
     * @param map  传递进来的数据，以map的形式进行了封装
     * @param path 要求服务器servlet的地址
     * @return 返回的boolean类型的参数
     * @throws Exception
     */
    public static Boolean submintDataByHttpClientDoPost(
            Map<String, String> map, String path) throws Exception {
        // 1. 获得一个相当于浏览器对象HttpClient，使用这个接口的实现类来创建对象，DefaultHttpClient
        HttpClient hc = new DefaultHttpClient();
        // DoPost方式请求的时候设置请求，关键是路径
        HttpPost request = new HttpPost(path);
        // 2. 为请求设置请求参数，也即是将要上传到web服务器上的参数
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            NameValuePair nameValuePairs = new BasicNameValuePair(
                    entry.getKey(), entry.getValue());
            parameters.add(nameValuePairs);
        }
        // 请求实体HttpEntity也是一个接口，我们用它的实现类UrlEncodedFormEntity来创建对象，后面一个String类型的参数是用来指定编码的
        HttpEntity entity = new UrlEncodedFormEntity(parameters, "UTF-8");
        request.setEntity(entity);
        // 3. 执行请求
        HttpResponse response = hc.execute(request);
        // 4. 通过返回码来判断请求成功与否
        if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
            String result = EntityUtils.toString(response.getEntity());
            JSONObject jsonobject = new JSONObject(result);
            if (jsonobject.getString("status").equals("1")) {
                return true;
            } else {
                return false;
            }
        }
        return null;
    }

    /**
     * 以HttpClient的DoGet方式向服务器发送请数据
     *
     * @param map  传递进来的数据，以map的形式进行了封装
     * @param path 要求服务器servlet的地址
     * @return 返回的boolean类型的参数
     * @throws Exception
     */
    public static String submitDataByHttpClientDoGet(Map<String, String> map,
                                                     String path) throws Exception {
        HttpClient hc = new DefaultHttpClient();
        // 请求路径
        StringBuilder sb = new StringBuilder(path);
        sb.append("?");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        String str = sb.toString();
        System.out.println(str);
        HttpGet request = new HttpGet(sb.toString());

        HttpResponse response = hc.execute(request);

        if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
            String result = EntityUtils.toString(response.getEntity());

            return result;
        }
        return null;
    }

    /**
     * 使用get请求以普通方式提交数据
     *
     * @param map  传递进来的数据，以map的形式进行了封装
     * @param path 要求服务器servlet的地址
     * @return 返回的boolean类型的参数
     * @throws Exception
     */
    public Boolean submitDataByDoGet(Map<String, String> map, String path)
            throws Exception {
        // 拼凑出请求地址
        StringBuilder sb = new StringBuilder(path);
        sb.append("?");
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            sb.append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        String str = sb.toString();
        System.out.println(str);
        URL Url = new URL(str);
        HttpURLConnection HttpConn = (HttpURLConnection) Url.openConnection();
        HttpConn.setRequestMethod("GET");
        HttpConn.setReadTimeout(5000);
        // GET方式的请求不用设置什么DoOutPut()之类的吗？
        if (HttpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentDataTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new java.util.Date());
    }

    /**
     * 当用户没有选择时间时，默认时间段是当天00:00:00到24:00:00
     *
     * @return
     */
    public static String getCurrentData1() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return sdf.format(new java.util.Date());
    }

    public static String getCurrentData2() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 24:00:00");
        return sdf.format(new java.util.Date());
    }

    public static String getCurrentDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new java.util.Date());
    }

    /**
     * 得到前一天时间 type:时间格式
     *
     * @return
     */
    public static String getSubtractDay(String type) {
        SimpleDateFormat sdf = new SimpleDateFormat(type);
        String nowtime = sdf.format(new java.util.Date());

        Date dt;
        String subtracttime = null;
        try {
            dt = sdf.parse(nowtime);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.DAY_OF_YEAR, -1);// 日期减1天
            Date dt1 = rightNow.getTime();
            subtracttime = sdf.format(dt1);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return subtracttime;

    }

    /**
     * 得到最后统计的交易金额的时间的后一天时间
     *
     * @return time
     */
    public static String getAddDay(String lasttime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date dt;
        String time = null;
        try {
            dt = sdf.parse(lasttime);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.DAY_OF_YEAR, 1);// 日期加1天
            Date dt1 = rightNow.getTime();
            time = sdf.format(dt1);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return time;

    }

    // 获得所需时间
    public void getTime() {
        // 最早的订单记录时间
        String firstOrderTime = dbManager.queryFirstOrderTime();
        // 最近一次订单记录的时间
        String lastOrderTime = dbManager.queryLastOrderTime();
        // 最后一次统计的"有订单金额记录的时间"
        String lastOrderPaySumTime = dbManager.queryLastOrderPaySumTime();
        // 登录当天时间(当前时间)
        String todayTime = getCurrentDay();
        // 登录的前一天时间
        String yesterday = getSubtractDay("yyyy-MM-dd");
        // 有订单信息
        if (lastOrderTime != null) {
            // 截取最早交易时间 为"yyyy-MM-dd"格式
            String firsttime = (String) firstOrderTime.subSequence(0, 10);
            // 截取订单时间 为"yyyy-MM-dd"格式
            String lasttime = (String) lastOrderTime.subSequence(0, 10);
            String starttimes = null;
            String stoptimes = null;
            String sumtime = null;
            // 最后订单记录时间不是登录当天
            if (!lasttime.equals(todayTime)) {
                // 没有统计记录
                if (lastOrderPaySumTime == null) {
                    // 最早和最晚订单记录时间是同一天
                    if (firsttime.equals(lasttime)) {
                        // 要统计的记录的时间段
                        starttimes = lasttime + " 00:00:00";
                        stoptimes = lasttime + " 24:00:00";
                        sumtime = lasttime;
                    } else {
                        starttimes = firsttime + " 00:00:00";
                        stoptimes = firsttime + " 24:00:00";
                        sumtime = firsttime;
                    }
                    List<OrderPaySum> orderPaySums = dbManager
                            .queryOrderPaySum(starttimes, stoptimes, sumtime);
                    dbManager.addOrderPaySumData(orderPaySums);
                    getTime();
                } else {
                    // 最后一次统计的"有订单金额记录的时间"的后一天(要统计的记录的时间)
                    String ordersumtime = getAddDay(lastOrderPaySumTime);
                    addPaySum(lastOrderPaySumTime, yesterday, ordersumtime);
                }

            } else {
                // 没有统计记录
                if (lastOrderPaySumTime == null) {
                    // 最早订单记录时间和最晚时间不是同一天
                    if (!firsttime.equals(lasttime)) {
                        // 要统计的记录的时间段
                        starttimes = firsttime + " 00:00:00";
                        stoptimes = firsttime + " 24:00:00";
                        sumtime = firsttime;
                        List<OrderPaySum> orderPaySums = dbManager
                                .queryOrderPaySum(starttimes, stoptimes,
                                        sumtime);
                        dbManager.addOrderPaySumData(orderPaySums);
                        getTime();
                    }
                } else {
                    // 最后一次统计的"有订单金额记录的时间"的后一天(要统计的记录的时间)
                    String ordersumtime = getAddDay(lastOrderPaySumTime);
                    addPaySum(lastOrderPaySumTime, yesterday, ordersumtime);
                }
            }
        }
    }

    /**
     * 订单金额统计及添加
     *
     * @param lasttime
     * @param nowtime
     * @param tradingtime
     */
    public void addPaySum(String lastPaySumTime, String yesterday,
                          String ordersumtime) {
        // 最后统计时间和登录当天的前一天时间不等
        if (!yesterday.equals(lastPaySumTime)) {
            // 要统计的订单记录的时间段
            String starttimes = ordersumtime + " 00:00:00";
            String stoptimes = ordersumtime + " 24:00:00";
            List<OrderPaySum> orderPaySums = dbManager.queryOrderPaySum(
                    starttimes, stoptimes, ordersumtime);
            if (orderPaySums.size() == 0) {
                OrderPaySum orderPaySum = new OrderPaySum();
                orderPaySum.setPaytype(0);
                orderPaySum.setAmount(0.00);
                orderPaySum.setOrdersumtime(ordersumtime);
                orderPaySums.add(orderPaySum);
            }
            dbManager.addOrderPaySumData(orderPaySums);
            getTime();
        }
    }

    /**
     * 添加盘点信息
     *
     * @return
     */
    public static List<Check> addCheckData(List<Goods> goods, double quantity, String count_time, String shelf, String checker, String count_id) {

        List<Check> checks = new ArrayList<Check>();

        //获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new java.util.Date());
        //插入时间
        String createtime = getCurrentDataTime();
        //更新时间(初始值和插入时间一样)
        String updatetime = getCurrentDataTime();
        Check check = new Check();
        check.setId(goods.get(0).getId());//商品id
        check.setTitle(goods.get(0).getTitle());//商品名称
        check.setProductsn(goods.get(0).getProductsn());//商品条码
        check.setQuantity(quantity);
        check.setCreatetime(createtime);//插入时间
        check.setUpdatetime(updatetime);//更新时间
        check.setCount_time(count_time);//盘点时间
        check.setStatus(0);//上传状态 0：未上传 1：上传中 2：上传成功
        check.setCount_id(count_id);//盘点唯一id
        check.setShelf(shelf);//货架
        check.setChecker(checker);//盘点人
        check.setRemark("");//备注
        checks.add(check);

        return checks;

    }

    /**
     * 将上传的盘点信息转成json格式
     *
     * @param checks
     * @return sBuffer.toString() 返回json格式字符串
     */
    public static String addChechsToJsonObject(List<Check> checks) {

        String jsonresult = "";//定义返回字符串
        JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
        try {
            JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
            for (Check check : checks) {
                JSONObject jsonObj = new JSONObject();//pet对象，json形式
                jsonObj.put("id", check.getId());//向pet对象里面添加值
                jsonObj.put("title", check.getTitle());
                jsonObj.put("productsn", check.getProductsn());
                jsonObj.put("quantity", check.getQuantity());
                jsonObj.put("createtime", check.getCreatetime());
                jsonObj.put("updatetime", check.getUpdatetime());
                jsonObj.put("count_time", check.getCount_time());
                jsonObj.put("status", check.getStatus());
                jsonObj.put("count_id", check.getCount_id());
                jsonObj.put("shelf", check.getShelf());
                jsonObj.put("checker", check.getChecker());
                jsonObj.put("remark", check.getRemark());
                // 把每个数据当作一对象添加到数组里
                jsonarray.put(jsonObj);//向json数组里面添加pet对象
            }
            object.put("check", jsonarray);//向总对象里面添加包含pet的数组
            jsonresult = object.toString();//生成返回字符串
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonresult;
    }

    /**
     * 添加盘点明细记录
     *
     * @return
     */
    public static List<CheckDetail> addCheckDetaulData(List<Goods> goods, double quantity, String updatetime, int updatetype, String count_id) {

        List<CheckDetail> checkDetails = new ArrayList<CheckDetail>();
        CheckDetail checkDetail = new CheckDetail();

        checkDetail.setCount_id(count_id);//盘点账号
        checkDetail.setTitle(goods.get(0).getTitle());//商品名称
        checkDetail.setProductsn(goods.get(0).getProductsn());//商品条码
        checkDetail.setQuantity(quantity);//数量
        checkDetail.setUpdatetime(updatetime);//更新时间
        checkDetail.setUpdatetype(updatetype);
        ;//更新方式  0:插入  1:添加  2:减少  3:修改

        checkDetails.add(checkDetail);

        return checkDetails;

    }

    /**
     * 添加盘点管理信息
     *
     * @param count_id     盘点账号
     * @param count_time   盘点时间
     * @param check_status 盘点状态  0:未完成  1：已完成   -1:无数据
     * @param remark       备注
     * @return CheckManage  盘点管理信息集合
     */
    public static List<CheckManage> addCheckManageData(String count_id, int check_status, String count_time, String remark) {

        List<CheckManage> checkManages = new ArrayList<CheckManage>();
        CheckManage checkManage = new CheckManage();
        checkManage.setCount_id(count_id);//盘点账号
        checkManage.setCheck_status(check_status);//盘点状态
        checkManage.setCount_time(count_time);//盘点时间
        checkManage.setRemark(remark);//备注
        checkManages.add(checkManage);
        return checkManages;

    }

    /**
     * 获得入库信息
     *
     * @param goods          入库商品信息集合
     * @param quantity       入库数量
     * @param count_time     入库时间
     * @param shelf          货架
     * @param storage_people 入库操作员
     * @param count_id       入库账号
     * @param update_status  数量更新状态  默认为0
     * @return
     */
    public static List<Storage> addStorageData(List<Goods> goods, double quantity, String count_time,
                                               String shelf, String storage_people, String count_id, int update_status) {

        List<Storage> storages = new ArrayList<Storage>();
        //获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = sdf.format(new java.util.Date());
        //插入时间
        String createtime = getCurrentDataTime();
        //更新时间(初始值和插入时间一样)
        String updatetime = getCurrentDataTime();
        Storage storage = new Storage();

        storage.setId(goods.get(0).getId());//商品id
        storage.setTitle(goods.get(0).getTitle());//商品名称
        storage.setProductsn(goods.get(0).getProductsn());//商品条码
        storage.setQuantity(quantity);//数量
        storage.setCreatetime(createtime);//插入时间
        storage.setUpdatetime(updatetime);//更新时间
        storage.setCount_time(count_time);//入库时间
        storage.setStatus(0);//上传状态 0：未上传 1：上传中 2：上传成功
        storage.setCount_id(count_id);//入库唯一id
        storage.setShelf(shelf);//货架
        storage.setStorage_people(storage_people);//入库操作员
        storage.setRemark("");//备注
        storage.setUpdate_status(update_status);//数量更新状态  默认为0
        storages.add(storage);

        return storages;

    }

    /**
     * 添加入库明细记录
     *
     * @return
     */
    public static List<StorageDetail> addStorageDetaulData(List<Goods> goods, double quantity, String updatetime, int updatetype, String count_id) {

        List<StorageDetail> storageDetails = new ArrayList<StorageDetail>();
        StorageDetail storageDetail = new StorageDetail();
        storageDetail.setTitle(goods.get(0).getTitle());//商品名称
        storageDetail.setProductsn(goods.get(0).getProductsn());//商品条码
        storageDetail.setQuantity(quantity);//数量
        storageDetail.setUpdatetime(updatetime);//更新时间
        storageDetail.setUpdatetype(updatetype);//跟新方式
        storageDetail.setCount_id(count_id);//入库唯一id

        storageDetails.add(storageDetail);

        return storageDetails;

    }

    /**
     * 将上传的入库信息转成json格式
     *
     * @param storages
     * @return jsonresult 返回json格式字符串
     */
    public static String addStorageToJsonObject(List<Storage> storages) {

        String jsonresult = "";//定义返回字符串
        JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
        try {
            JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
            for (Storage storage : storages) {
                JSONObject jsonObj = new JSONObject();//pet对象，json形式
                jsonObj.put("id", storage.getId());//向pet对象里面添加值
                jsonObj.put("title", storage.getTitle());
                jsonObj.put("productsn", storage.getProductsn());
                jsonObj.put("quantity", storage.getQuantity());
                jsonObj.put("createtime", storage.getCreatetime());
                jsonObj.put("updatetime", storage.getUpdatetime());
                jsonObj.put("count_time", storage.getCount_time());
                jsonObj.put("status", storage.getStatus());
                jsonObj.put("count_id", storage.getCount_id());
                jsonObj.put("shelf", storage.getShelf());
                jsonObj.put("storage_people", storage.getStorage_people());
                jsonObj.put("remark", storage.getRemark());
                jsonObj.put("update_status", storage.getUpdate_status());
                // 把每个数据当作一对象添加到数组里
                jsonarray.put(jsonObj);//向json数组里面添加pet对象
            }
            object.put("storage", jsonarray);//向总对象里面添加包含pet的数组
            jsonresult = object.toString();//生成返回字符串
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jsonresult;
    }

    /**
     * 添加入库管理信息
     *
     * @param count_id       入库盘点账号
     * @param storage_status 入库状态  0:未完成  1：已完成   -1:无数据
     * @param count_time     入库时间
     * @param remark         备注
     * @return storageManages  入库管理信息集合
     */
    public static List<StorageManage> addStorageManageData(String count_id, int storage_status, String count_time, String remark) {

        List<StorageManage> storageManages = new ArrayList<StorageManage>();
        StorageManage storageManage = new StorageManage();
        storageManage.setCount_id(count_id);//入库账号
        storageManage.setStorage_status(storage_status);//入库状态
        storageManage.setCount_time(count_time);//入库时间
        storageManage.setRemark(remark);//备注
        storageManages.add(storageManage);
        return storageManages;

    }

    /**
     * 将要修改的入库信息转成json格式
     *
     * @param count_id      入库账号
     * @param productsn     商品条码
     * @param quantity      数量
     * @param update_status 修改方式   0:新增  1:减少
     * @return jsonupdate
     */
    public static String addStorageUpdateToJsonObject(String count_id, String productsn, double quantity, int update_status) {

        String jsonupdate = "";//定义返回字符串
        JSONObject object = new JSONObject();//创建一个总的对象，这个对象对整个json串
        try {
            JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
            JSONObject jsonObj = new JSONObject();//pet对象，json形式
            jsonObj.put("count_id", count_id);
            jsonObj.put("productsn", productsn);
            jsonObj.put("quantity", quantity);
            jsonObj.put("update_status", update_status);
            // 把每个数据当作一对象添加到数组里
            jsonarray.put(jsonObj);//向json数组里面添加pet对象
            object.put("storage_update", jsonarray);//向总对象里面添加包含pet的数组
            jsonupdate = object.toString();//生成返回字符串
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonupdate;
    }

    /**
     * 得到最近一周时间
     * type:时间格式
     *
     * @return
     */
    public static String getNear_WeekDay(String type) {
        SimpleDateFormat sdf = new SimpleDateFormat(type);
        String nowtime = sdf.format(new java.util.Date());

        Date dt;
        String subtracttime = null;
        try {
            dt = sdf.parse(nowtime);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(dt);
            rightNow.add(Calendar.DAY_OF_YEAR, -6);//日期减6天
            Date dt1 = rightNow.getTime();
            subtracttime = sdf.format(dt1) + " 00:00:00";
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return subtracttime;

    }

}
