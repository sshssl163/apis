package com.xb.interactiveadapplication.net.core;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
//import com.xb.topnews.ad.baseplugin.bean.AdvertData;
//import com.xb.topnews.ad.ssp.AdvertDataJsonAdapter;
//import com.xb.topnews.localevent.LocalEvent;
//import com.xb.topnews.localevent.LocalEventAdapter;

/**
 */
public class GsonResultParse<T> implements ResultParse<T> {

    public static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
//            .registerTypeAdapter(LocalEvent.class, new LocalEventAdapter())
//            .registerTypeAdapter(AdvertData.class, new AdvertDataJsonAdapter())
//            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    private Class<T> clazz;
    private String[] keys;

    public GsonResultParse(Class<T> clazz) {
        this(clazz, new String[0]);
    }

    public GsonResultParse(Class<T> clazz, String... keys) {
        this.clazz = clazz;
        this.keys = keys;
    }

    @Override
    public T parse(JsonElement response) throws JsonParseException {
        JsonElement jsonElement = response;
        if (keys != null && keys.length > 0) {
            for (String key : keys) {
                jsonElement = jsonElement.getAsJsonObject().get(key);
            }
        }
        try {
            T t = gson.fromJson(jsonElement, clazz);
            if (t == null)
                throw new JsonParseException("json parse error: \n" + new GsonBuilder().setPrettyPrinting().create().toJson(response));
            return t;
        } catch (Exception e) {
            throw new JsonParseException(e.getMessage());
        }
    }

    @Override
    public T parse(String response) throws JsonParseException {
        JsonElement jsonElement = new JsonParser().parse(response);
        return parse(jsonElement);
    }

}
