package com.heaven7.android.components.demo;

import android.app.Application;

//import com.heaven7.android.components.demo.imageimpl.GlideAppImageComponent;

/**
 * Created by heaven7 on 2017/8/22 0022.
 */

public class SampleApplication extends Application{

    @Override
    public void onLowMemory() {
        super.onLowMemory();
       // new GlideAppImageComponent().getBitmapPool(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
       // new GlideAppImageComponent().getBitmapPool(this).trimMemory(level);
    }
}
