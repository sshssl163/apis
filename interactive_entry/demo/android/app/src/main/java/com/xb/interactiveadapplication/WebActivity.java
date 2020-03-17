package com.xb.interactiveadapplication;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

public class WebActivity extends Activity {
    public static final String LOAD_URL = "load_url";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        WebView  web = findViewById(R.id.webview);
        String url =  getIntent().getStringExtra(LOAD_URL);
        WebViewClient mWebviewclient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        };
        web.setWebViewClient(mWebviewclient);
        web.setWebViewClient(new WebViewClient());//使其跳转后依然使用webview来显示
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setJavaScriptEnabled(true);
        Log.d(MainActivity.TAG,"load web ,"+url);
        web.loadUrl(url);//利用loadUrl（）
    }
}
