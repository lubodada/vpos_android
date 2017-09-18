package com.cnyssj.db.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FoodJson {

	public List<Goods> parseJsons(String result) {
		List<Goods> goods_list = null;
		
		try {
			JSONArray jsonObjs = (new JSONObject(result)).getJSONArray("goods");
			JSONObject jsonObj = null;
			if (jsonObjs.length() != 0) {
				Goods foods = null;
				goods_list = new ArrayList<Goods>();
				for (int i = 0; i < jsonObjs.length(); i++) {
					jsonObj = ((JSONObject) jsonObjs.opt(i));
					
					String id = jsonObj.getString("id");
					String title = jsonObj.getString("title");
					String productsn = jsonObj.getString("productsn");
					double price = jsonObj.getDouble("price")/100;
					String thumb = jsonObj.getString("thumb");
					foods = new Goods(id,title,productsn,price,thumb);
					goods_list.add(foods);
				}
			}
		} catch (JSONException e) {
			System.out.println("Jsons转换错误");
			e.printStackTrace();
		}

		return goods_list;
	}
}
