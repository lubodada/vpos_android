package com.ven.pos;

import android.app.Activity;
import android.os.Bundle;

import com.cnyssj.pos.R;
import com.ven.pos.Util.ClearEditText;

public class RegisterActivity extends Activity {
    ClearEditText username = (ClearEditText) findViewById(R.id.username);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}
