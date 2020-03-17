package com.xb.interactiveadapplication.net;

import android.content.Context;

import okhttp3.OkHttpClient;

public interface StethoHelper {

    void init(Context context);

    void configureOkHttp(OkHttpClient.Builder builder);

}
