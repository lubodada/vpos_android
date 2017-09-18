package com.cnyssj.pos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ven.pos.TitleBar;



public class CcguanliActivity extends Activity {
	Button but_storage, but_pandian;

	// 出入库，盘点
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TitleBar.setTitleBar(this, "返回", "", "", null);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ccguanli);
		but_storage = (Button) findViewById(R.id.storage_but);
		but_storage.setOnClickListener(myonclicklistener);
		but_pandian = (Button) findViewById(R.id.inventory_but);
		but_pandian.setOnClickListener(myonclicklistener);

	}

	View.OnClickListener myonclicklistener = new OnClickListener() {
		Intent intent = new Intent();

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.inventory_but:
				intent.setClass(CcguanliActivity.this, InventoryActivity.class);
				startActivity(intent);
				break;
			case R.id.storage_but:
				intent.setClass(CcguanliActivity.this, StorageActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}

		}
	};
}
