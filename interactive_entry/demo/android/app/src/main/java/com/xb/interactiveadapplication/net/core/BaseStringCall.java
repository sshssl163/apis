package com.xb.interactiveadapplication.net.core;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zhy.http.okhttp.callback.StringCallback;


import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

/**
 */
public class BaseStringCall<T> extends StringCallback {

    private static final String TAG = "request";

//    private ResultParse<T> parse;
    private RequestCallback<T> callback;
//    private long beginTime;

    public BaseStringCall(ResultParse<T> parse, RequestCallback<T> callback) {
//        this.parse = parse;
        this.callback = callback;
    }

    @Override
    public void onBefore(Request request, int id) {
        super.onBefore(request, id);
//        beginTime = System.currentTimeMillis();
    }

    @Override
    public void onAfter(int id) {
        super.onAfter(id);
    }

    @Override
    public String parseNetworkResponse(Response response, int id) throws IOException {

        String body = super.parseNetworkResponse(response, id);

        Log.d(TAG, "response: " + response.request().url().url() + " " + response.code()
                + "\n" + response.headers()
                + "\n" + body);

        Headers headers = response.headers();
        for (String name : headers.names()) {
            if (name != null && TextUtils.equals(name.toLowerCase(), "set-cookie")) {
                String urlString = response.request().url().url().toString();
                List<String> cookies = headers.values(name);
                Log.d("Cookie", "set: " + urlString + ", " + cookies.toString());
            }
        }

        return body;
    }

    @Override
    public boolean validateReponse(Response response, int id) {

        Log.d(TAG, "validateReponse: " + response.request().url().url() + " " + response.code()
                + "\n" + response.headers());

        return super.validateReponse(response, id);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        e.printStackTrace();
        Log.e(TAG, e.getMessage(), e.getCause());
    }

    @Override
    public void onResponse(String response, int id) {

        callback.onSuccessed(response);
    }

}
