/**
 *
 */
package com.cloudpos.scanserver.aidl;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class ScanResult implements Parcelable {

    public final static int ERROR_UNKNOWN = -999;
    public final static int SCAN_SUCCESS = 1;
    public final static int CUSTOM_CANCEL = 0;
    public final static int ERROR_OPEN_AGAIN = -1;
    public final static int ERROR_DEVICE_OPEN_FAILED = -2;
    public final static int ERROR_SCAN_TIME_OUT = -3;
    public final static int ERROR_PARAMETER = -4;

    private int resultCode = ERROR_UNKNOWN;
    private byte[] rawBuffer = null;
    private Bitmap decodeBitmap = null;
    private String text = null;
    private String barcodeFormat = null;


    public int getResultCode() {
        return resultCode;
    }

    public byte[] getRawBuffer() {
        return rawBuffer;
    }

    public Bitmap getDecodeBitmap() {
        return decodeBitmap;
    }

    public String getText() {
        return text;
    }

    public String getBarcodeFormat() {
        return barcodeFormat;
    }

    public void setDecodeBitmap(Bitmap decodeBitmap) {
        this.decodeBitmap = decodeBitmap;
    }

    public ScanResult(int errorCode) {
        this.resultCode = errorCode;
    }

    public ScanResult(byte[] rawBuffer, String barcodeFormat) {
        this.resultCode = SCAN_SUCCESS;
        this.rawBuffer = rawBuffer;
        this.barcodeFormat = barcodeFormat;
        try {
            this.text = new String(rawBuffer, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static final String KEY_RAW_BUFFER = "raw_buffer";
    private static final String KEY_BITMAP = "bitmap_buffer";
    private static final String KEY_FORMAT = "barcode_format";

    public ScanResult(Parcel source) {
        this.resultCode = source.readInt();
        if (this.resultCode == SCAN_SUCCESS) {
            Bundle bundle = source.readBundle();
            this.rawBuffer = bundle.getByteArray(KEY_RAW_BUFFER);
            this.barcodeFormat = bundle.getString(KEY_FORMAT);
            try {
                this.text = new String(rawBuffer, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte[] tempBuffer = bundle.getByteArray(KEY_BITMAP);
            if (tempBuffer != null) {
                this.decodeBitmap = convertBuffer2Bitmap(tempBuffer);
            }
        }

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.resultCode);
        if (this.resultCode == SCAN_SUCCESS) {
            Bundle bundle = new Bundle();
            bundle.putByteArray(KEY_RAW_BUFFER, this.rawBuffer);
            bundle.putString(KEY_FORMAT, this.barcodeFormat);
            if (this.decodeBitmap != null) {
                byte[] bytes = convertBitmap2Buffer(this.decodeBitmap);
                bundle.putByteArray(KEY_BITMAP, bytes);
            }
            dest.writeBundle(bundle);
        }
    }

    // 添加一个静态成员,名为CREATOR,该对象实现了Parcelable.Creator接口
    public static final Parcelable.Creator<ScanResult> CREATOR = new Parcelable.Creator<ScanResult>() {
        @Override
        public ScanResult createFromParcel(Parcel source) {// 从Parcel中读取数据，返回RuleItem对象
            return new ScanResult(source);
        }

        @Override
        public ScanResult[] newArray(int size) {
            return new ScanResult[size];
        }
    };

    public String toString() {
        String debug = String.format("resultCode = %s\n rawBuffer =%s\n text = %s\n format = %s\n decodeBitmap = %s ", this.resultCode, this.rawBuffer, this.text, this.barcodeFormat, this.decodeBitmap);
        return debug;
    }


    private byte[] convertBitmap2Buffer(Bitmap bitmap) {
        byte[] result = null;
        ByteArrayOutputStream output = new ByteArrayOutputStream();//初始化一个流对象
        try {
            bitmap.compress(CompressFormat.PNG, 100, output);//把bitmap100%高质量压缩 到 output对象里
            bitmap.recycle();//自由选择是否进行回收
            result = output.toByteArray();//转换成功了
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e2) {
            }
        }
        return result;
    }

    private Bitmap convertBuffer2Bitmap(byte[] buffer) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        return bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
