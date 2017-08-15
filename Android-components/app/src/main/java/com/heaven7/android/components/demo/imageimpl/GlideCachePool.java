package com.heaven7.android.components.demo.imageimpl;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.heaven7.android.component.image.ImageCachePool;

/**
 * Created by heaven7 on 2017/8/15 0015.
 */

public class GlideCachePool implements ImageCachePool {

    private final com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool mImpl;

    public GlideCachePool(com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool mImpl) {
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

    @Override
    public void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }
}
