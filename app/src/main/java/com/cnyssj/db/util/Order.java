package com.cnyssj.db.util;

/**
 * 订单实体类
 *
 * @author lb
 */
public class Order {

    private String _id;//主键id
    private String myorderid;//订单编号(自己的)
    private String orderid;//订单编号(掌游宝)
    private int type;//交易支付类型  0 未知 1 现金支付 2 余额支付 3 支付宝支付 4 微信支付 5 银联支付 6 翼支付
    private String amount;//消费金额
    private String items;//消费物品
    private int paymentstatus;//支付状态  1 未支付 2 部分支付 3 已支付 4 退款中 5 部分退款 6 全额退款  7支付中
    private double discount;//优惠金额
    private String couponid;//优惠券id
    private String remark;//备注
    private String ordertime;//订单时间
    private String mnumber;//客户付款码
    private int uploadstatus;//上传状态
    private int orderstatus;//订单状态1 新建，2：支付，3：完成

    public Order() {

    }

    public Order(String myorderid, String orderid, int type, String items, int paymentstatus, String amount, double discount, String couponid,
                 String remark, int orderstatus) {
        // TODO Auto-generated constructor stub

        this.myorderid = myorderid;
        this.orderid = orderid;
        this.type = type;
        this.items = items;
        this.paymentstatus = paymentstatus;
        this.amount = amount;
        this.discount = discount;
        this.couponid = couponid;
        this.remark = remark;
        this.orderstatus = orderstatus;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getMyorderid() {
        return myorderid;
    }

    public void setMyorderid(String myorderid) {
        this.myorderid = myorderid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public int getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(int paymentstatus) {
        this.paymentstatus = paymentstatus;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getCouponid() {
        return couponid;
    }

    public void setCouponid(String couponid) {
        this.couponid = couponid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }

    public String getMnumber() {
        return mnumber;
    }

    public void setMnumber(String mnumber) {
        this.mnumber = mnumber;
    }

    public int getUploadstatus() {
        return uploadstatus;
    }

    public void setUploadstatus(int uploadstatus) {
        this.uploadstatus = uploadstatus;
    }

    public int getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(int orderstatus) {
        this.orderstatus = orderstatus;
    }


}
