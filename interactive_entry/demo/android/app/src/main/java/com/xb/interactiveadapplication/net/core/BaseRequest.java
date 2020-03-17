package com.xb.interactiveadapplication.net.core;

import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.Map;

/**
 */
public class BaseRequest {

    public static <T> RequestCall get(final String url, Map<String, String> params, final ResultParse<T> parse, final RequestCallback<T> callback) {
        Log.d("request", "get:" + url + " params:" + params);
        GetBuilder builder = OkHttpUtils.get()
                .url(url)
                .id((int) System.currentTimeMillis())
                .params(params);

        RequestCall requestCall = builder
                .build();
        requestCall.execute(new BaseStringCall<>(parse, callback));
        return requestCall;
    }
}
