/*
 * Basic no frills app which integrates the ZBar barcode scanner with
 * the camera.
 * 
 * Created by lisah0 on 2012-02-24
 */
package com.ven.pos.qscanbar;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cnyssj.pos.R;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.ven.pos.ITitleBarLeftClick;
import com.ven.pos.Payment.PaymentMainActivity;
import com.ven.pos.TitleBar;
import com.ven.pos.Payment.ConsumeListView;
import com.ven.pos.Payment.PaymentCashierMainAct;
import com.ven.pos.Payment.ShopItem;
import com.ven.pos.Payment.ShopItemMgr;
import com.ven.pos.Util.BaseActivity;
import com.ven.pos.Util.PlaySoundPool;
import com.ven.pos.Util.Util_Screen;

/* Import ZBar Class files */

public class ShopCameraActivity extends BaseActivity implements OnClickListener {

    private static ShopCameraActivity shopCameraActivity;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private boolean canCamera = true;
    private PlaySoundPool playSoundPool;
    private TextView scanText;
    private TextView scanShopText;
    private EditText shop_item_amount_edit;
    private TextView priceText;
    private TextView all_priceText;
    private Button plusBtn;
    private Button minusBtn;
    // Button scanButton;

    ImageScanner scanner;
    ShopItem item;
    private boolean barcodeScanned = false;
    private boolean previewing = true;

    SharedPreferences_util su = new SharedPreferences_util();
    boolean isLandScreen;

    static {
        System.loadLibrary("iconv");
    }

    private class ScanEnd implements ITitleBarLeftClick {
        public void click() {
            // Intent intent = new Intent(ShopCameraActivity.this,
            // ShopCameraActivity.class);

            shopCameraActivity.onAddItemIntoList();
            shopCameraActivity.finish();

            // startActivity(intent);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLandScreen = su.getPrefboolean(ShopCameraActivity.this, "isLandScreen", false);
        if (isLandScreen)
            Util_Screen.screenToFullOrLand(this, true, isLandScreen);
        TitleBar.setTitleBar(this, "", "商品扫码", "关闭", new ScanEnd());
        setContentView(R.layout.shop_qcode_main);
        shopCameraActivity = this;

        try {
            /*setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);*/

            autoFocusHandler = new Handler();
            mCamera = getCameraInstance();

            scanner = new ImageScanner();
            scanner.setConfig(0, Config.X_DENSITY, 3);
            scanner.setConfig(0, Config.Y_DENSITY, 3);

            mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
            FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
            preview.addView(mPreview);

            scanText = (TextView) findViewById(R.id.scanText);
            // scanText.setText(R.string.msg_default_status);
            scanText.setText("");

            scanShopText = (TextView) findViewById(R.id.scanShopText);
            scanShopText.setText("");

            priceText = (TextView) findViewById(R.id.priceText);
            priceText.setText("");

            all_priceText = (TextView) findViewById(R.id.all_priceText);
            all_priceText.setText("***");

            shop_item_amount_edit = (EditText) findViewById(R.id.shop_item_amount_edit);
            shop_item_amount_edit.setText("0");
            shop_item_amount_edit.setOnClickListener(this);

            plusBtn = (Button) findViewById(R.id.shop_item_amount_plus);
            plusBtn.setOnClickListener(this);

            minusBtn = (Button) findViewById(R.id.shop_item_amount_minus);
            minusBtn.setOnClickListener(this);

            playSoundPool = new PlaySoundPool(getApplicationContext());
            playSoundPool.loadSfx(R.raw.beep, 1);
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(this, "扫描二维码错误，请检查摄像头是否正常", Toast.LENGTH_LONG);
        }

    }

    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(0);
        } catch (Exception e) {
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    @SuppressWarnings("unused")
    private void QuitTask(final int scanType, final String resultString) {
        Intent intent = new Intent();

        setResult(RESULT_OK, intent);
        finish();
    }

    ;

    private void ResetCamera() {
        canCamera = true;
    }

    ;

    PreviewCallback previewCb = new PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {

            if (!canCamera) {
                return;
            }

            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                // previewing = false;
                // mCamera.setPreviewCallback(null);
                // mCamera.stopPreview();
                canCamera = false;

                playSoundPool.play(1, 0);

                SymbolSet syms = scanner.getResults();
                String rs = "";
                for (Symbol sym : syms) {
                    rs = sym.getData();

                    barcodeScanned = true;
                }
                final String resultString = rs;
                String itemNo = "";

                Pattern p = Pattern.compile("[0-9]*");
                Matcher m = p.matcher(resultString);
                if (m.matches()) {
                    itemNo = resultString;
                } else {
                    String PRE_STR = "productsn=";
                    int start = resultString.indexOf(PRE_STR);
                    String subStr = resultString.substring(start
                            + PRE_STR.length());
                    int end = subStr.indexOf("&");
                    itemNo = subStr.substring(0, end);
                }

                item = ShopItemMgr.instance().GetItem(itemNo);
                if (item == null) {

                    Toast.makeText(
                            getApplicationContext(),
                            getResources()
                                    .getString(R.string.shop_item_no_find),
                            Toast.LENGTH_SHORT).show();

                } else {
                    // 是之前扫过的商品，就加1

                    String strOldItemNo = scanText.getText().toString();

                    if (itemNo.equals(strOldItemNo)) {
                        onIncreaseItem(itemNo);
                    } else {

                        scanText.setText(item.itemProductsn);
                        scanShopText.setText(item.itemName);
                        priceText.setText((item.price).toString());
                        shop_item_amount_edit.setText("1");
                        all_priceText.setText(String.valueOf(item.price));
                        // onAddItemIntoList();
                    }
                }

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            ResetCamera();
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("thread error...");
                        }
                    }
                }.start();
            }
        }
    };

    AutoFocusCallback autoFocusCB = new AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    public void onIncreaseItem(String strItemNo) {
        if (item != null) {

            if (scanText.getText().toString().equals(strItemNo)) {
                try {
                    String strItemAmount = shop_item_amount_edit.getText()
                            .toString();
                    int nItemAmount = Integer.parseInt(strItemAmount);
                    nItemAmount += 1;
                    if (nItemAmount > 999) {
                        return;
                    }

                    shop_item_amount_edit.setText(String.valueOf(nItemAmount));
                    DecimalFormat fnum = new DecimalFormat("##0.00");
                    all_priceText.setText(fnum.format((item.price_100 * nItemAmount) / 100.0f));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void onAddItemIntoList() {
        String strOldItemNo = scanText.getText().toString();
        ShopItem oldItem = ShopItemMgr.instance().GetItem(strOldItemNo);
        if (!strOldItemNo.isEmpty() && oldItem != null) {

            int amount = Integer.parseInt(shop_item_amount_edit.getText()
                    .toString());
            // 取到旧商品的价格
            double price = ShopItemMgr.instance().GetItem(strOldItemNo).price_100;
            // 商品不一致，加到列表里
            int nTotalMoney = (int) (price * amount);
            DecimalFormat fnum = new DecimalFormat("##0.00");
            ConsumeListView.instance()
                    .insert(strOldItemNo, Integer.toString(amount),
                            fnum.format(nTotalMoney / 100.0f));
            // 更新
            Message message = new Message();
            message.what = PaymentCashierMainAct.UpdateListMsg;
            PaymentCashierMainAct.context.updateList.sendMessage(message);
        }
    }

    // ID识别商品
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == plusBtn.getId()) {
            onIncreaseItem(scanText.getText().toString());
        } else if (v.getId() == minusBtn.getId()) {
            try {
                String strItemAmount = shop_item_amount_edit.getText()
                        .toString();
                int nItemAmount = Integer.parseInt(strItemAmount);
                nItemAmount -= 1;

                if (nItemAmount < 0) {
                    return;
                }

                shop_item_amount_edit.setText(String.valueOf(nItemAmount));
                DecimalFormat fnum = new DecimalFormat("##0.00");
                all_priceText.setText(fnum.format((item.price_100 * nItemAmount) / 100.0f));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
