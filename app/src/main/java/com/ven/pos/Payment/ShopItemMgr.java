package com.ven.pos.Payment;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ShopItemMgr {

	private static ShopItemMgr inst = new ShopItemMgr();

	public static ShopItemMgr instance() {
		return inst;
	}

	public static HashMap<String, ShopItem> ItemList = new HashMap<String, ShopItem>();

	public void InsertItem(ShopItem item) {
		ItemList.put(item.itemProductsn, item);
	}

	public void RemoveItem(String itemNo) {
		ItemList.remove(itemNo);
	}

	public boolean FindItem(String itemNo) {
		return ItemList.containsKey(itemNo);
	}

	public ShopItem GetItem(String itemNo) {
		return ItemList.get(itemNo);
	}

	@SuppressWarnings("rawtypes")
	public ShopItem GetItemByName(String itemName) {
		Iterator iter = ItemList.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			ShopItem val = (ShopItem) entry.getValue();

			if (val.itemName.equals(itemName)) {
				return val;
			}
		}
		return null;
	}
}
