package com.xb.interactiveadapplication.net.core;

import android.util.Log;

import java.io.IOException;

import okhttp3.Connection;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 为Response的Header中添加服务器ip地址信息，返回给接收端
 */
public class AddressInterceptor implements Interceptor {

    public static final String HEADER_KEY_HOST_ADDRESS = "Interceptor-Host-Address";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());

        String url = chain.request().url().url().toString();

        Connection connection = chain.connection();
        if (connection != null) {
            String ip = connection.route().socketAddress().getAddress().getHostAddress();
            Log.d("request", "chain: " + url + ", " + connection.route().address() + ", " + connection.route().socketAddress() + ", ip: " + ip);

            return response.newBuilder()
                    .addHeader(HEADER_KEY_HOST_ADDRESS, ip)
                    .build();
        } else {
            Log.d("request", "chain, "  + url + ", " + "connection null");
            return response;
        }
    }
}
