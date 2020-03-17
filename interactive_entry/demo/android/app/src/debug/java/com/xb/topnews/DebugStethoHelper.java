package com.xb.topnews;

import android.content.Context;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.xb.interactiveadapplication.net.StethoHelper;

import okhttp3.OkHttpClient;

public class DebugStethoHelper implements StethoHelper {

    @Override
    public void init(Context context) {
        Stetho.initializeWithDefaults(context);
    }

    @Override
    public void configureOkHttp(OkHttpClient.Builder builder) {
        builder.addNetworkInterceptor(new StethoInterceptor());
    }

}
