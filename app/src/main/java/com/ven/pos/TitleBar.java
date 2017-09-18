package com.ven.pos;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.cnyssj.pos.R;

/**
 * @author aaron
 * 
 */
public class TitleBar {

	private static Activity mActivity;

	public static void setTitleBar(Activity activity, String btnBackTxt,
			String title, String lText, final ITitleBarLeftClick leftClick) {
		mActivity = activity;
		activity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		activity.setContentView(R.layout.titlebar);
		activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);
		Button retView = (Button) activity.findViewById(R.id.btnBack);

		if (!btnBackTxt.equals("")) {
			retView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// KeyEvent newEvent = new KeyEvent(KeyEvent.ACTION_DOWN,
					// KeyEvent.KEYCODE_BACK);
					// mActivity.onKeyDown(KeyEvent.KEYCODE_BACK, newEvent);
					try {
						if (null != mActivity && !mActivity.isFinishing())
							mActivity.onBackPressed();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});

			retView.setVisibility(View.VISIBLE);
		} else {
			retView.setVisibility(View.INVISIBLE);
		}

		TextView titleShow = (TextView) activity
				.findViewById(R.id.titlebar_text);
		titleShow.setText(title);

		Button titleBackBtn = (Button) activity.findViewById(R.id.rimageview);
		titleBackBtn.setText(lText);
		if (("").equals(lText)) {
			titleBackBtn.setVisibility(View.INVISIBLE);
		} else {
			titleBackBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (null != leftClick) {
						leftClick.click();
					}
				}
			});
			titleBackBtn.setVisibility(View.VISIBLE);
		}

	}

	public static void setTitle(Activity activity, String title) {

		TextView titleShow = (TextView) activity
				.findViewById(R.id.titlebar_text);
		titleShow.setText(title);

	}

	public static void setActivity(Activity activity) {
		mActivity = activity;
	}
}
