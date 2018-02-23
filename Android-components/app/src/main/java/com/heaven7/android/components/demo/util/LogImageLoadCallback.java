package com.heaven7.android.components.demo.util;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.heaven7.android.component.image.ImageLoadCallback;
import com.heaven7.core.util.Logger;

/**
 * Created by Administrator on 2017/8/15 0015.
 */

public class LogImageLoadCallback implements ImageLoadCallback {

    private static final String TAG = "LogImageLoadCallback";

    @Override
    public void onLoadStarted(String key, Drawable placeholder) {
        Logger.i(TAG,"onLoadStarted","key = " + key);
    }

    @Override
    public void onLoadFailed(String key, Exception e, Drawable errorDrawable) {
        Logger.i(TAG,"onLoadFailed","key = " + key);
    }

    @Override
    public void onLoadComplete(String key, Bitmap result) {
        Logger.i(TAG,"onLoadComplete","key = " + key);
    }

    @Override
    public void onLoadGifComplete(String key, Drawable gif) {
        Logger.i(TAG,"onLoadGifComplete","key = " + key);
    }
}
