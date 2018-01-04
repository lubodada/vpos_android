package com.ven.pos.printer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.device.PiccManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ven.pos.GlobalContant;

public class VenPiccReader {

    private static final String TAG = "PiccCheck";
    private static final int MSG_BLOCK_NO_NONE = 0;
    private static final int MSG_BLOCK_NO_ILLEGAL = 1;
    private static final int MSG_AUTHEN_FAIL = 2;
    private static final int MSG_WRITE_SUCCESS = 3;
    private static final int MSG_WRITE_FAIL = 4;
    private static final int MSG_READ_FAIL = 5;
    private static final int MSG_SHOW_BLOCK_DATA = 6;
    private static final int MSG_ACTIVE_FAIL = 7;
    private static final int MSG_APDU_FAIL = 8;
    private static final int MSG_SHOW_APDU = 9;
    private static final int MSG_BLOCK_DATA_NONE = 10;
    private static final int MSG_AUTHEN_BEFORE = 11;

    private Button bOpen;
    private Button bCheck;
    private Button bRead;
    private Button bAuthen;
    private Button bWrite;
    private Button bApdu;
    private Button bPass;
    private Button bFail;

    private EditText etBlockData;
    private EditText etBlockNo;

    private TextView tvApdu;

    private PiccManager piccReader = null;
    private Handler handler;
    private ExecutorService exec = null;

    //private static String retValue = "";;

    private boolean bCheckMember = false;

    boolean hasAuthen = false;
    int blkNo;

    byte EMV_APDU[] = {
            0x00, (byte) 0xA4, 0x04, 0x00, 0x0E, 0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53,
            0x2E, 0x44, 0x44, 0x46, 0x30, 0x31, 0x00
    };

    byte keyBuf[] = {
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff
    };

    byte Wbuf[] = {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d,
            0x0e, 0x0f
    };

    Handler parentHandler = null;
    int noticeId = 0;

    public VenPiccReader(Handler handler, int noticeId) {

        if (GlobalContant.MACHINE != GlobalContant.MACHINE_I9000S) {
            return;
        }

        if (GlobalContant.bClose9000SMemberCard)
            return;

        if (null == piccReader) {
            piccReader = new PiccManager();

            // piccReader.activate();
            // piccReader.open();
        }
        if (null == exec)
            exec = Executors.newSingleThreadExecutor();

        bCheckMember = true;
        this.parentHandler = handler;
        this.noticeId = noticeId;
    }

    public void CheckCard() {

        if (null == piccReader)
            return;


        exec.execute(new Thread(new Runnable() {
            //
            // @Override
            public void run() {

                try {
                    Log.v("PiccReader", " open start");
                    piccReader.open();
                    Log.v("PiccReader", " open end");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return;
                }

                while (bCheckMember) {

                    try {
                        Thread.sleep(500);

                        // TODO Auto-generated method stub
                        byte CardType[] = new byte[2];
                        byte Atq[] = new byte[14];
                        char SAK = 1;
                        byte sak[] = new byte[1];
                        sak[0] = (byte) SAK;
                        byte SN[] = new byte[10];


                        int scan_card = -1;
                        int SNLen = -1;

                        Log.v("PiccReader", " check start");
                        scan_card = piccReader.request(CardType, Atq);
                        Log.v("PiccReader", " check end:" + scan_card);
                        if (scan_card > 0) {
                            SNLen = piccReader.antisel(SN, sak);
                            Log.d(TAG, "SNLen = " + SNLen);

                            //retValue = "";
                            String retValue = bytesToHexString(SN, SNLen);

                            if (SNLen > 0 && parentHandler != null) {
                                Message localMessage = new Message();
                                localMessage.what = noticeId;
                                localMessage.obj = retValue;
                                parentHandler.sendMessage(localMessage);
                            }
                        }
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        return;
                    }

                }
            }
        }, "picc check"));
        //
        // return retValue;
    }

    public static String bytesToHexString(byte[] src, int len) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        if (len <= 0) {
            len = src.length;
        }
        for (int i = 0; i < len; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String bytesToString(byte[] src, int len) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        if (len <= 0) {
            len = src.length;
        }
        for (int i = 0; i < len; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public void onDestory() {

        if (piccReader == null)
            return;

        bCheckMember = false;
        try {
            piccReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
