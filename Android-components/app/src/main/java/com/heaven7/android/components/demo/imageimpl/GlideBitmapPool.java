package com.heaven7.android.components.demo.imageimpl;

import android.graphics.Bitmap;

/**
 * Created by heaven7 on 2017/8/15 0015.
 */

public class GlideBitmapPool implements com.heaven7.android.component.image.BitmapPool{

    private final com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool mImpl;

    public GlideBitmapPool(com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool mImpl) {
        this.mImpl = mImpl;
    }

    @Override
    public boolean put(String key, Bitmap bitmap) {
        return mImpl.put(bitmap);
    }

    @Override
    public Bitmap get(String key, int width, int height, Bitmap.Config config) {
        return mImpl.get(width, height, config);
    }

    @Override
    public void clearMemory() {
        mImpl.clearMemory();
    }

    @Override
    public void trimMemory(int level) {
        mImpl.trimMemory(level);
    }
}
