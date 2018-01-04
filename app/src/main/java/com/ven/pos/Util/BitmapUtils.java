package com.ven.pos.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * Created by Administrator on 2018/1/4 0004.
 */

public class BitmapUtils {

    public static Bitmap creatBarcode(Context context, String contents,
                                      int desiredWidth, int desiredHeight, boolean displayCode) {
        Bitmap ruseltBitmap = null;
        int marginW = 20;
        BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;

        if (displayCode) {
            Bitmap barcodeBitmap = encodeAsBitmap(contents, barcodeFormat,
                    desiredWidth, desiredHeight);
            Bitmap codeBitmap = creatCodeBitmap(contents, desiredWidth + 2
                    * marginW, desiredHeight, context);
            ruseltBitmap = mixtureBitmap(barcodeBitmap, codeBitmap, new PointF(
                    0, desiredHeight));
        } else {
            ruseltBitmap = encodeAsBitmap(contents, barcodeFormat,
                    desiredWidth, desiredHeight);
        }

        return ruseltBitmap;
    }

    protected static Bitmap encodeAsBitmap(String contents,
                                           BarcodeFormat format, int desiredWidth, int desiredHeight) {
        final int WHITE = 0xFFFFFFFF;
        final int BLACK = 0xFF000000;

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = null;
        try {
            result = writer.encode(contents, format, desiredWidth,
                    desiredHeight, null);
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        // All are 0, or black, by default
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }


    protected static Bitmap creatCodeBitmap(String contents, int width,
                                            int height, Context context) {
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setHeight(height);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setWidth(width);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.BLACK);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.buildDrawingCache();
        Bitmap bitmapCode = tv.getDrawingCache();
        return bitmapCode;
    }


    protected static Bitmap mixtureBitmap(Bitmap first, Bitmap second,
                                          PointF fromPoint) {
        if (first == null || second == null || fromPoint == null) {
            return null;
        }
        int marginW = 20;
        Bitmap newBitmap = Bitmap.createBitmap(
                first.getWidth() + second.getWidth() + marginW,
                first.getHeight() + second.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas cv = new Canvas(newBitmap);
        cv.drawBitmap(first, marginW, 0, null);
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null);
        cv.save(Canvas.ALL_SAVE_FLAG);
        cv.restore();

        return newBitmap;
    }
}
