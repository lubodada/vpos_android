package com.cnyssj.db;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cnyssj.db.util.Goods;
import com.cnyssj.db.util.Order;
import com.cnyssj.db.util.Orderdetail;

public class FoodJson1 {

	/**
	 * 商品信息
	 * 
	 * @param result
	 * @return
	 */
	public List<Goods> parseJsons(String result) {
		List<Goods> goods_list = null;

		try {
			JSONArray jsonObjs = new JSONArray(result);
			JSONObject jsonObj = null;
			if (jsonObjs.length() != 0) {
				Goods foods = null;
				goods_list = new ArrayList<Goods>();
				for (int i = 0; i < jsonObjs.length(); i++) {
					jsonObj = ((JSONObject) jsonObjs.opt(i));

					String id = jsonObj.getString("id");
					String title = jsonObj.getString("title");
					String productsn = jsonObj.getString("productsn");
					double price = jsonObj.getDouble("price");
					String thumb = jsonObj.getString("thumb");
					foods = new Goods(id, title, productsn, price, thumb);
					goods_list.add(foods);
				}
			}
		} catch (JSONException e) {
			System.out.println("Jsons转换错误");
			e.printStackTrace();
		}

		return goods_list;
	}

	/**
	 * 订单信息
	 * 
	 * @param result
	 * @return
	 */
	public List<Order> parseOrderJsons(String result) {
		List<Order> Order_list = null;

		try {
			JSONArray jsonObjs = new JSONArray(result);
			JSONObject jsonObj = null;
			if (jsonObjs.length() != 0) {
				Order order = null;
				Order_list = new ArrayList<Order>();
				for (int i = 0; i < jsonObjs.length(); i++) {
					jsonObj = ((JSONObject) jsonObjs.opt(i));

					String myorderid = jsonObj.getString("myorderid");
					String orderid = jsonObj.getString("orderid");
					int type = jsonObj.getInt("type");
					String items = jsonObj.getString("items");
					int paymentstatus = jsonObj.getInt("paymentstatus");
					String amount = jsonObj.getString("amount");
					double discount = jsonObj.getDouble("discount");
					String couponid = jsonObj.getString("couponid");
					String remark = jsonObj.getString("remark");
					int orderstatus = jsonObj.getInt("orderstatus");
					order = new Order(myorderid, orderid, type, items,
							paymentstatus, amount, discount, couponid, remark,orderstatus);
					Order_list.add(order);
				}
			}
		} catch (JSONException e) {
			System.out.println("Jsons转换错误");
			e.printStackTrace();
		}

		return Order_list;
	}

	/**
	 * 商品订单详情
	 * 
	 * @param result
	 * @return
	 */
	public List<Orderdetail> parseOrderdetailJsons(String result) {
		List<Orderdetail> Orderdetail_list = null;

		try {
			JSONArray jsonObjs = new JSONArray(result);
			JSONObject jsonObj = null;
			if (jsonObjs.length() != 0) {

				Orderdetail orderdetail = null;
				Orderdetail_list = new ArrayList<Orderdetail>();
				for (int i = 0; i < jsonObjs.length(); i++) {
					jsonObj = ((JSONObject) jsonObjs.opt(i));

					String id = jsonObj.getString("id");
					String name = jsonObj.getString("name");
					double quantity = jsonObj.getDouble("amount");
					double amount = jsonObj.getDouble("money");
					orderdetail = new Orderdetail(id, name, quantity, amount);
					Orderdetail_list.add(orderdetail);
				}
			}
		} catch (JSONException e) {
			System.out.println("Jsons转换错误");
			e.printStackTrace();
		}

		return Orderdetail_list;
	}
}
