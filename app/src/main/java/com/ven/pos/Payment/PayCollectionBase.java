package com.ven.pos.Payment;

import android.content.Context;

public class PayCollectionBase {
    protected PayQueryBase payQuery;
    protected Context parentContext;

    PayCollectionBase(Context c, PayQueryBase payQuery) {
        this.payQuery = payQuery;
        this.parentContext = c;
    }

    public void collectionMoney(String money, String tAuthCode/* 顾客二维码 */, String payDetails, int oper) {

    }
}
