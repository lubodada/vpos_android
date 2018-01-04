package com.ven.pos.Payment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnyssj.pos.R;
import com.ven.pos.TitleBar;
import com.ven.pos.Util.BaseActivity;
import com.ven.pos.qscanbar.ShopCameraActivity;

public class BarCodeActivity extends BaseActivity {
    /**
     * Called when the activity is first created.
     */
    private TextView resultTextView;
    private EditText qrStrEditText;
    private ImageView qrImgImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TitleBar.setTitleBar(this, "返回", "二维码扫描", "", null);

        setContentView(R.layout.qcode_scan_main);

        resultTextView = (TextView) this.findViewById(R.id.tv_scan_result);
        qrStrEditText = (EditText) this.findViewById(R.id.et_qr_string);
        qrImgImageView = (ImageView) this.findViewById(R.id.iv_qr_image);

        Button scanBarCodeButton = (Button) this
                .findViewById(R.id.btn_scan_barcode);
        scanBarCodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 打开扫描界面扫描条形码或二维码
//				Intent openCameraIntent = new Intent(BarCodeActivity.this,
//						CaptureActivity.class);
//				startActivity(openCameraIntent);

                Intent openCameraIntent = new Intent(BarCodeActivity.this, ShopCameraActivity.class);
                startActivityForResult(openCameraIntent, 0);
            }
        });

    }

    // @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            resultTextView.setText(scanResult);
        }
    }
}