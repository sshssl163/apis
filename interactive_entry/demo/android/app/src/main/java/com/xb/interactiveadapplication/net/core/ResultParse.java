package com.xb.interactiveadapplication.net.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 *
 * @author jinbing
 */
public interface ResultParse<T> {

    public T parse(JsonElement response) throws JsonParseException;

    public T parse(String response) throws JsonParseException;

}
