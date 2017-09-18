package com.cnyssj.db.util;
/**
 * 订单金额统计实体类
 * @author Administrator
 *
 */
public class OrderPaySum {
	
	private int paytype;//支付类型 1 现金支付 2 余额支付 3 支付宝支付 4 微信支付 5 银联支付 6 翼支付
	private double amount;//统计金额
	private String ordersumtime;//统计时间
	
	public int getPaytype() {
		return paytype;
	}
	public void setPaytype(int paytype) {
		this.paytype = paytype;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getOrdersumtime() {
		return ordersumtime;
	}
	public void setOrdersumtime(String ordersumtime) {
		this.ordersumtime = ordersumtime;
	}
	
	
	

}
