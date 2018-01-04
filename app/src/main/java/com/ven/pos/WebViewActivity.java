package com.ven.pos;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.HttpAuthHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cnyssj.pos.R;
import com.ven.pos.Payment.ConsumePayResult;
import com.ven.pos.Util.BaseActivity;
import com.ven.pos.Util.UtilTool;

@SuppressLint("JavascriptInterface")
public class WebViewActivity extends BaseActivity {

    private String TAG = "WebViewActivity";
    // private ProgressDialog pDialog;
    public PageComplate onPageFinishedRet;
    private WebView webView;
    static WebViewActivity webViewActivity;
    private final int UpdateView = 1;

    private class PageFinished implements PageComplate {
        public void OnComplate(String b) {
            // pDialog.dismiss();

            try {
                if (b.contains(WebViewActivity.webViewActivity.getResources()
                        .getString(R.string.index_url))) {
                    if (null != WebViewActivity.webViewActivity
                            && !WebViewActivity.webViewActivity.isFinishing()) {
                    }
                } else if (b.contains(WebViewActivity.webViewActivity
                        .getResources().getString(R.string.ret_login_url))) {
                    if (null != WebViewActivity.webViewActivity
                            && !WebViewActivity.webViewActivity.isFinishing()) {

                    }
                } else if (b.contains(WebViewActivity.webViewActivity
                        .getResources().getString(
                                R.string.consume_pay_success_url))) {
                    if (null != WebViewActivity.webViewActivity
                            && !WebViewActivity.webViewActivity.isFinishing()) {
                        // WebViewActivity.webViewActivity.onBackPressed();

                        String postData = "";
                        if (null != getIntent().getStringExtra("extrapost")) {
                            postData = getIntent().getStringExtra("extrapost");
                        }
                        ConsumePayResult.CosumePayRet(
                                WebViewActivity.webViewActivity, b, postData);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    private class PageWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.d("ANDROID_LAB", "TITLE=" + title);
            // txtTitle.setText("ReceivedTitle:" +title);
            // setTitle(title);
            TitleBar.setTitle(WebViewActivity.webViewActivity, title);
        }

    }

    ;

    private class EndWebView implements ITitleBarLeftClick {
        public void click() {
            // ActivityManager.getInstance()
            // .closeAllActivityNoincludeMainAndLogin();
        }
    }

    /**
     * 用Handler来更新UI
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case UpdateView: {
                    // 关闭ProgressDialog
                    initView();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
        // WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //
        webViewActivity = this;

        Bundle bundle = this.getIntent().getExtras();
        String titleName = bundle.getString("titleName");

        TitleBar.setTitleBar(this, "后退", titleName, "关闭", new EndWebView());

        setContentView(R.layout.loading);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000);
                    Message localMessage = new Message();
                    localMessage.what = 1;
                    webViewActivity.handler.sendMessage(localMessage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return;
            }
        }).start();

        UtilTool.checkLoginStatusAndReturnLoginActivity(this, null);
    }

    private void initView() {
        // this.pDialog = new ProgressDialog(this);
        // Bundle bundle = this.getIntent().getExtras();
        // String titleName = bundle.getString("titleName");

        // TitleBar.setTitleBar(this, "后退", titleName, "完成", new EndWebView());

        setContentView(R.layout.webview);

        onPageFinishedRet = new PageFinished();

        // 接收name值
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        if (UtilTool.isNetworkAvailable(this)) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        } else {
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        }
        String url = getIntent().getExtras().getString("url");
        // webView.loadUrl(url);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new PageWebChromeClient());
        // webView.addJavascriptInterface(new InJavaScriptLocalObj(),
        // "local_obj");
        loadUrl(url);

        webView.setFocusable(true);
        // String tUrl = url + "?" + postData;
        // webView.loadUrl(tUrl);

        // pDialog.show();
    }

    private void loadUrl(String url) {
        String postData = "token=" + GlobalContant.instance().token;

        if (null != getIntent().getStringExtra("extrapost")) {
            postData += getIntent().getStringExtra("extrapost");
        }
        webView.postUrl(url, postData.getBytes());
    }

    @Override
    public void onStart() {


        TitleBar.setActivity(this);

        super.onStart();
    }

    @Override
    public void onBackPressed() {

        if (null != webView && webView.canGoBack()) {
            webView.goBack();
            return;
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        // OpenIntegralWall.getInstance().onUnbind();
        super.onDestroy();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        // 在WebView中而不是默认浏览器中显示页面
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                if (url.contains(WebViewActivity.webViewActivity.getResources()
                        .getString(R.string.index_url))) {
                    if (null != WebViewActivity.webViewActivity
                            && !WebViewActivity.webViewActivity.isFinishing()) {
                        // WebViewActivity.webViewActivity.onBackPressed();
                        WebViewActivity.webViewActivity.finish();
                        return true;
                    }
                }

                view.loadUrl(url);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return true;
        }

        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Log.e(TAG, "Error: " + description);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        public void onReceivedHttpAuthRequest(WebView view,
                                              HttpAuthHandler handler, String host, String realm) {

            if (null != onPageFinishedRet) {
                onPageFinishedRet.OnComplate(realm);
            }

            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        public void onPageFinished(WebView view, String url) {

            view.loadUrl("javascript:window.local_obj.showSource('<head>'+"
                    + "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            try {
                if (url.contains(WebViewActivity.webViewActivity.getResources()
                        .getString(R.string.consume_pay_success_url))) {
                    if (null != WebViewActivity.webViewActivity
                            && !WebViewActivity.webViewActivity.isFinishing()) {
                        // WebViewActivity.webViewActivity.onBackPressed();

                        String postData = "";
                        if (null != getIntent().getStringExtra("extrapost")) {
                            postData = getIntent().getStringExtra("extrapost");
                        }
                        ConsumePayResult.CosumePayRet(
                                WebViewActivity.webViewActivity, url, postData);
                        return;
                    }
                } else if (url.contains(WebViewActivity.webViewActivity
                        .getResources().getString(R.string.ret_login_url))) {

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    final class InJavaScriptLocalObj {
        public void showSource(String html) {
            try {

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
