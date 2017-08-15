package com.heaven7.android.components.demo;

import android.app.Application;

/**
 * Created by Administrator on 2017/8/15 0015.
 */

public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
