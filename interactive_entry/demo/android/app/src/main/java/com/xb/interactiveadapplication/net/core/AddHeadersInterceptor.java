package com.xb.interactiveadapplication.net.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.xb.interactiveadapplication.InteractiveAdApplication;
import com.xb.jni.Encrypt;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;

/**
 *
 * 添加header拦截器
 * User-Agent、Cookie、Sign
 *
 */

public class AddHeadersInterceptor implements Interceptor {


    private Context mContext;

    public AddHeadersInterceptor(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        return null;
    }

}
