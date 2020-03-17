package com.xb.interactiveadapplication.utils;

import android.content.Context;
import android.util.Log;

import com.xb.interactiveadapplication.BuildConfig;
import com.xb.interactiveadapplication.InteractiveAdApplication;
import com.xb.interactiveadapplication.net.core.AddHeadersInterceptor;
import com.xb.interactiveadapplication.net.core.AddressInterceptor;
import com.xb.interactiveadapplication.net.core.WebResourceAssetUtils;
import com.xb.interactiveadapplication.net.core.WebResourceInterceptor;
import com.xb.interactiveadapplication.net.core.WebResourceProvider;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * Created by yejinbing on 2017/6/6.
 */

public class BuildUtils {

    public static void initOkHttp(final Context context) {
        BuildConfig.STETHO.init(context);
        WebResourceProvider provider = new WebResourceProvider() {

            private Context mContext = context;

            @Override
            public InputStream provide(String url, String host, String path) {
                return WebResourceAssetUtils.loadWebResource(mContext, url, host, path);
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .addInterceptor(new AddHeadersInterceptor(InteractiveAdApplication.getInstance().getApplicationContext()))
                .addInterceptor(new WebResourceInterceptor(provider))
                .addNetworkInterceptor(new AddressInterceptor());
//                .dns(new OkHttpDns(context))

        BuildConfig.STETHO.configureOkHttp(builder);

        OkHttpClient okHttpClient = builder.build();

        OkHttpUtils.initClient(okHttpClient);
    }
}
