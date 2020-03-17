package com.xb.interactiveadapplication.net.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author jinbing
 */
public class UrlBuilder {

    private static final String TAG = UrlBuilder.class.getSimpleName();

    private String url;
    private Map<String, Object> params;


    public UrlBuilder(String url) {
        this.params = new HashMap<>();

        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public UrlBuilder addParams(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public Map<String, String> getParams() {
        Map<String, String> newParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            newParams.put(entry.getKey(), entry.getValue().toString());
        }

        String[] keys = new String[newParams.size()];
        String[] values = new String[newParams.size()];
        SortedSet<String> sortedKeys = new TreeSet<>(newParams.keySet());
        int i = 0;
        for (String key : sortedKeys) {
            keys[i] = key;
            values[i] = newParams.get(key) != null ? newParams.get(key).toString() : "";
            i += 1;
        }
//        String sig = Encrypt.signUrlParams(NewsApplication.getInstance().getApplicationContext(), keys, values);
//        newParams.put("sig", sig);

        return newParams;
    }
}
