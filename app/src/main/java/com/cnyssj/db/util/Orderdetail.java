package com.cnyssj.db.util;

/**
 * 订单商品详情
 *
 * @author lb
 */
public class Orderdetail {

    private String _id;//主键id
    private String id;//商品id
    private String name;//商品名
    private double quantity;//商品数量
    private double amount;//优惠金额

    public Orderdetail() {

    }

    public Orderdetail(String id, String title, double number, double allmoney) {
        // TODO Auto-generated constructor stub
        this.id = id;
        this.name = title;
        this.quantity = number;
        this.amount = allmoney;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }


}
