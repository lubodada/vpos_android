package com.cnyssj.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.GradientDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.cnyssj.pos.R;

/**
 * Created by zzz40500 on 15/6/15.
 */
public class ShapeLoadingDialog {

    private Context mContext;
    private Dialog mDialog;
    private LoadingView mLoadingView;
    private View mDialogContentView;

    public ShapeLoadingDialog(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        mDialog = new Dialog(mContext, R.style.custom_dialog);
        mDialogContentView = LayoutInflater.from(mContext).inflate(
                R.layout.layout_dialog, null);
        mLoadingView = (LoadingView) mDialogContentView
                .findViewById(R.id.loadView);
        mDialog.setContentView(mDialogContentView);

    }

    public void setBackground(int color) {
        GradientDrawable gradientDrawable = (GradientDrawable) mDialogContentView
                .getBackground();
        gradientDrawable.setColor(color);
    }

    public void setLoadingText(CharSequence charSequence) {
        mLoadingView.setLoadingText(charSequence);
    }

    public void show() {
        mDialog.show();

    }

    public void setkeycodeback(boolean cancel) {
        mDialog.setOnKeyListener(new OnKeyListener() {

            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    return true;
                }
                return false;
            }
        });
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public void setCanceledOnTouchOutside(boolean cancel) {
        mDialog.setCanceledOnTouchOutside(cancel);
    }
}
