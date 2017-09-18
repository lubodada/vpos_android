package com.jhj.Agreement.ZYB;

public class Url {
	// 交易
	public static String transaction = "https://webpaywg.bestpay.com.cn/barcode/placeOrder";
	// 查询
	public static String query = "https://webpaywg.bestpay.com.cn/query/queryOrder";
	// 退款
	public static String refund = "https://webpaywg.bestpay.com.cn/refund/commonRefund";
	// 撤销
	public static String revoke = "https://webpaywg.bestpay.com.cn/reverse/reverse";
	/**
	 * 掌优宝
	 * */
	// 交易
	public static String transaction_zyb = "http://api.zhangyoo.cn/order/create";
	// 查询
	public static String query_zyb = "http://api.zhangyoo.cn/order/query";
	// 订单取消
	public static String revoke_zyb = "http://api.zhangyoo.cn/order/cancel";
	// 退款
	public static String refund_zyb = "http://api.zhangyoo.cn/order/refund";

	// 上传交易数据
	public static String updata_paymessage = "http://121.41.113.42:8080/PaymentProxy/payments/submitAccumulatedPointsPayment";
	// 积分兑换测试
	// http://api.zhangyoobao.com/api/order/query
	// 积分兑换查询
	public static String exchange_query = "http://api.zhangyoo.cn/order/query";
	// 二维码 下单
	public static String QR_code_pay = "http://api.zhangyoo.cn/order/preCreate";
	// 二维码 查询
	public static String QR_code_query = "http://api.zhangyoo.cn/order/query";
	// 下载商品列表
	public static String path = "http://cnyssj.net/data/test/test_product_list_50000.json";
	
}
