package com.xb.interactiveadapplication.net.core;

import android.webkit.MimeTypeMap;


import java.io.IOException;
import java.io.InputStream;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 */

public class WebResourceInterceptor implements Interceptor {

    private WebResourceProvider mProvider;

    public WebResourceInterceptor(WebResourceProvider provider) {
        mProvider = provider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl url = chain.request().url();
        String urlString = url.toString();

        String host = url.host();
        String path = formatPath(url.url().getPath());

        InputStream is = mProvider.provide(urlString, host, path);
        if (is == null) {
            return chain.proceed(chain.request());
        }

        String extension = MimeTypeMap.getFileExtensionFromUrl(urlString);
        String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        int length = is.available();
        byte[] bytes = new byte[length];
        int read = is.read(bytes);
        if (read != length) {
            return chain.proceed(chain.request());
        }

        MediaType mideaType = mimetype != null ? MediaType.parse(mimetype) : null;

        return new Response.Builder()
                .code(200)
                .message("")
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(mideaType, bytes))
                .build();
    }

    public static String formatPath(String path) {
        if (path == null) {
            return path;
        }

        int lastIndex = path.lastIndexOf("/");
        if (lastIndex < 0 || lastIndex != path.length() - 1) {
            return path;
        }

        return path.substring(0, path.lastIndexOf("/"));
    }

}
