package com.ven.pos;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;

import com.cnyssj.pos.R;
import com.jhj.Agreement.ZYB.SharedPreferences_util;
import com.ven.pos.Util.BaseActivity;

/**
 * Created by Administrator on 2018/1/2 0002.
 */

public class StartActivity extends BaseActivity implements View.OnClickListener {

    Dialog dia_landport;
    RadioButton RB_land, RB_port;
    boolean isLandScreen;

    SharedPreferences_util su = new SharedPreferences_util();
    Window w;
    WindowManager.LayoutParams lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        isLandScreen = su.getPrefboolean(StartActivity.this, "isLandScreen", false);

        dia_landport = new Dialog(StartActivity.this, R.style.edit_AlertDialog_style);
        dia_landport.setContentView(R.layout.activity_start_dialog_land_port);
        RB_land = (RadioButton) dia_landport.findViewById(R.id.RB_land);
        RB_port = (RadioButton) dia_landport.findViewById(R.id.RB_port);
        RB_land.setOnClickListener(this);
        RB_port.setOnClickListener(this);

        dia_landport.show();
        dia_landport.setCanceledOnTouchOutside(false);
        w = dia_landport.getWindow();
        lp = w.getAttributes();
        lp.x = 0;
        lp.y = 40;
        dia_landport.onWindowAttributesChanged(lp);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.RB_land:
                isLandScreen = true;
                dia_landport.cancel();
                break;
            case R.id.RB_port:
                isLandScreen = false;
                dia_landport.cancel();
                break;
        }
        su.setPrefboolean(StartActivity.this, "isLandScreen", isLandScreen);
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
        StartActivity.this.finish();

    }
}
