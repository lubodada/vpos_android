package com.ven.pos.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;

public class Time {

    @SuppressLint("SimpleDateFormat")
    public int getTime() {

        SimpleDateFormat formatter = new SimpleDateFormat("mmss");
        Date curDate = new Date(System.currentTimeMillis());
        return Integer.parseInt(formatter.format(curDate));
    }

    public String getTime_10() {
        long time = System.currentTimeMillis() / 1000;// 获取系统时间的10位的时间戳
        String str = String.valueOf(time);
        return str;
    }
}