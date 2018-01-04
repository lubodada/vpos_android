package com.ven.pos.Payment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class ConsumeListView {

    private static ConsumeListView inst = new ConsumeListView();

    public static ConsumeListView instance() {
        return inst;
    }

    // 商品名称
    public static String list_id_name = "name";
    // 商品数量
    public static String list_id_amount = "amount";
    // 商品金额
    public static String list_id_money = "money";

    public HashMap<String, HashMap<String, String>> consumelistArray = new LinkedHashMap<String, HashMap<String, String>>();

    public void insert(String itemNo, String amount, String money) {
        ShopItem shopItem = null;

        if (String.valueOf(itemNo).length() == 18) {
            shopItem = ShopItemMgr.instance().GetItem(
                    itemNo.subSequence(0, 7).toString());
        } else {
            shopItem = ShopItemMgr.instance().GetItem(itemNo);
        }

        if (null != shopItem) {

            if (String.valueOf(itemNo).length() == 18) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(list_id_name, shopItem.itemName);
                map.put(list_id_amount, amount);
                map.put(list_id_money, money);
                consumelistArray.put(itemNo, map);
            } else {
                if (consumelistArray.containsKey(itemNo)) {

                    int newAmount = Integer.parseInt(consumelistArray.get(
                            itemNo).get(list_id_amount));
                    newAmount += Integer.parseInt(amount);
                    int nTotalMoney = (int) (shopItem.price_100) * newAmount;
                    DecimalFormat fnum = new DecimalFormat("##0.00");
                    consumelistArray.get(itemNo).put(list_id_amount,
                            String.valueOf(newAmount));
                    consumelistArray.get(itemNo).put(list_id_money,
                            fnum.format(nTotalMoney / 100.0f));

                } else {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(list_id_name, shopItem.itemName);
                    map.put(list_id_amount, amount);
                    map.put(list_id_money, money);
                    consumelistArray.put(itemNo, map);
                }
            }

        }
    }

    public void ModifyAmount(String itemNo, int amount) {
        ShopItem shopItem = ShopItemMgr.instance().GetItem(itemNo);
        if (null != shopItem) {
            if (consumelistArray.containsKey(itemNo)) {
                int newAmount = Integer.parseInt(consumelistArray.get(itemNo)
                        .get(list_id_amount));
                newAmount = amount;

                String newPrice = new DecimalFormat("##0.00").format(newAmount
                        * shopItem.price);

                consumelistArray.get(itemNo).put(list_id_amount,
                        String.valueOf(newAmount));
                consumelistArray.get(itemNo).put(list_id_money, newPrice);
            }
        }
    }

    public ArrayList<HashMap<String, String>> getAllShop() {
        ArrayList<HashMap<String, String>> retArray = new ArrayList<HashMap<String, String>>();

        Iterator iter = consumelistArray.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            HashMap<String, String> val = consumelistArray.get(key);

            retArray.add(val);
        }
        return retArray;
    }

    public void removeByItemNo(String itemNo) {
        HashMap<String, String> subMap = consumelistArray.get(itemNo);

        if (null != subMap) {
            consumelistArray.remove(itemNo);
            return;
        }
    }

    public void remove(String name) {

        for (int i = 0; i < consumelistArray.size(); i++) {
            if (consumelistArray.get(i).get(list_id_name) == name) {
                consumelistArray.remove(i);
                return;
            }
        }
    }

}
