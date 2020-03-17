package com.xb.interactiveadapplication.net.core;

/**
 *
 * @author jinbing
 */
public interface RequestCallback<T> {

    /**
     * 请求成功
     * @param t
     */
    void onSuccessed(T t);

    /**
     * 请求成功
     * @param response
     */
    void onSuccessed(String  response);


    /**
     * 请求失败
     * @param status
     * @param msg
     */
    void onFailed(int status, String msg);

    /**
     * 请求错误
     */
    void onError();
}
