package com.xb.interactiveadapplication;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.xb.interactiveadapplication.utils.BuildUtils;

public class InteractiveAdApplication extends Application {

    private static InteractiveAdApplication sInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        Fresco.initialize(this);
        BuildUtils.initOkHttp(this);
    }

    public static InteractiveAdApplication getInstance() {
        return sInstance;
    }
}
