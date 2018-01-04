/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.ven.pos.printer;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.PrinterManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.ven.pos.GlobalContant;

public class VenPrinterManager {
    private static final String TAG = "MagActivity";

    private SimpleDateFormat formatter = new SimpleDateFormat(
            "yyyy年MM月dd日   HH:mm:ss:SSS    ");
    private final static String PRNT_ACTION = "android.prnt.message";
    PrinterManager printer = new PrinterManager();

    private final int DEF_TEMP_THROSHOLD = 50;
    private int mTempThresholdValue = DEF_TEMP_THROSHOLD;
    private Context context;

    private static VenPrinterManager inst;

    public static VenPrinterManager getInstance() {
        if (null == inst) {
            inst = new VenPrinterManager();
        }

        return inst;
    }


    public void setContext(Context context) {
        this.context = context;
    }


    private BroadcastReceiver mPrtReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            int ret = intent.getIntExtra("ret", 0);
            if (ret == -1)
                Toast.makeText(context, "缺纸！！！", Toast.LENGTH_SHORT).show();
        }
    };

    public void onPause() {

        context.unregisterReceiver(mPrtReceiver);
    }

    private boolean hasChineseChar(String text) {
        boolean hasChar = false;
        int length = text.length();
        int byteSize = text.getBytes().length;

        hasChar = (length != byteSize);

        return hasChar;
    }

    public void onResume() {


        IntentFilter filter = new IntentFilter();
        filter.addAction(PRNT_ACTION);
        context.registerReceiver(mPrtReceiver, filter);

    }

    //设置纸的宽高
    public void setPrintPage(int width, int height) {
        printer.setupPage(width, height);
    }

    public int printLine(String msg, int x, int y, int nFontSize) {

        if (GlobalContant.MACHINE != GlobalContant.MACHINE_I9000S) {
            return 0;
        }

        return printer.drawTextEx(msg, x, y, 384, -1, "arial", nFontSize, 0, 0, 1);

//		Intent intentService = new Intent(context, PrintBillService.class);
//		intentService.putExtra("SPRT", msg);
//		context.startService(intentService);
    }

    public void printTrack(String strHead, List<String> contentList, Bitmap bmp) {
        printer.setupPage(384, -1);

        int nY = 20;
        printer.drawTextEx("\n\n" + strHead + "\n\n", 100, nY, 384, -1, "arial", 50, 0, 0, 1);

        nY = 200;
        for (int i = 0; i < contentList.size(); i++) {

            nY += 40;
            printer.drawTextEx(contentList.get(i) + "\n", 10, nY, 384, -1, "arial", 25, 0, 0, 1);
        }

        if (null != bmp) {
            nY += 40;
            printBitmap(bmp, 50, nY);
        }
        nY += 40;

        printer.drawTextEx("\n\n", 10, nY, 384, -1, "arial", 25, 0, 0, 1);

        int ret = printer.printPage(0);
        Intent intent = new Intent(PRNT_ACTION);
        intent.putExtra("ret", ret);
        context.sendBroadcast(intent);
    }

    public int printBitmap(Bitmap img, int x, int y) {

        //printer.setupPage(384, -1);

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inDensity = context.getResources().getDisplayMetrics().densityDpi;
        opts.inTargetDensity = context.getResources().getDisplayMetrics().densityDpi;
//		Bitmap img = BitmapFactory.decodeResource(getResources(),
//				R.drawable.ticket, opts);
        return printer.drawBitmap(img, x, y);

//		int ret = printer.printPage(0);
//		Intent intent = new Intent("urovo.prnt.message");
//		intent.putExtra("ret", ret);
//		context.sendBroadcast(intent);
    }

    void doPrint(int type) {

//		printer.setupPage(384, -1);
//		switch (type) {
//		case 1:
//
//		case 2:
//			BitmapFactory.Options opts = new BitmapFactory.Options();
//			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
//			opts.inDensity = getResources().getDisplayMetrics().densityDpi;
//			opts.inTargetDensity = getResources().getDisplayMetrics().densityDpi;
//			Bitmap img = BitmapFactory.decodeResource(getResources(),
//					R.drawable.ticket, opts);
//			printer.drawBitmap(img, 30, 0);
//			break;
//		case 3:
//			printer.drawLine(264, 50, 48, 50, 4);
//			printer.drawLine(156, 0, 156, 120, 2);
//			printer.drawLine(16, 0, 300, 100, 2);
//			printer.drawLine(16, 100, 300, 0, 2);
//			break;
//		}
//
//		int ret = printer.printPage(0);
//		Intent intent = new Intent("urovo.prnt.message");
//		intent.putExtra("ret", ret);
//		this.sendBroadcast(intent);
    }


}
