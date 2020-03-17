package com.xb.interactiveadapplication.net.core;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 */

public class WebResourceAssetUtils {

    public static InputStream loadWebResource(Context context, String url, String host, String path) {
        String fileName = "webresource/" + host + path;
        try {
            InputStream is = context.getAssets().open(fileName);
            Log.d("WebResourceAssetUtils", "loadWebResource: " + url + ", " + fileName);
            return is;
        } catch (IOException e) { }
        return null;
    }
}
