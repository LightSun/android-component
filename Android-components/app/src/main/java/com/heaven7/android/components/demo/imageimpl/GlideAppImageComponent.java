package com.heaven7.android.components.demo.imageimpl;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.Glide;
import com.heaven7.android.component.image.AppImageComponent;
import com.heaven7.android.component.image.ImageCachePool;
import com.heaven7.android.component.image.ImageRequestEditor;

/**
 * Created by heaven7 on 2017/8/15 0015.
 */

public class GlideAppImageComponent implements AppImageComponent {

    @Override
    public ImageRequestEditor newEditor(Context context) {
        return new GlideRequestEditor(context);
    }

    @Override
    public ImageCachePool getBitmapPool(Context context) {
        return new GlideCachePool(Glide.get(context).getBitmapPool());
    }

    @Override
    public void setPauseWork(boolean paused) {

    }

    @Override
    public void setLoadingImage(int resId) {

    }

    @Override
    public void setLoadingImage(Bitmap bitmap) {

    }

}
