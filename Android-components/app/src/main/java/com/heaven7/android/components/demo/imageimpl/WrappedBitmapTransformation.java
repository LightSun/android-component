package com.heaven7.android.components.demo.imageimpl;


import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.heaven7.android.component.image.BitmapTransformer;

import java.util.List;

/**
 * @author heaven7
 */
/*
public class WrappedBitmapTransformation extends BitmapTransformation {

    private List<BitmapTransformer> mTransformers;
    private String id;
    private String key;

    public WrappedBitmapTransformation(Context context) {
        super(context);
    }

    public WrappedBitmapTransformation(com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool bitmapPool) {
        super(bitmapPool);
    }

    public void setBitmapTransformer(List<BitmapTransformer> transformers) {
        this.mTransformers = transformers;
    }

    public void setKey(String key){
        this.key = key;
    }

    @Override
    protected Bitmap transform(com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool pool,
                               Bitmap toTransform, int outWidth, int outHeight) {
        final GlideCachePool poolWrapper = new GlideCachePool(pool);
        Bitmap previous = toTransform;
        for (BitmapTransformer transformer : mTransformers) {
            previous = transformer.transform(key, poolWrapper, previous, outWidth, outHeight);
        }
        return previous;
    }

    @Override
    public String getId() {
        if (id == null) {
            StringBuilder sb = new StringBuilder();
            for (BitmapTransformer transformer : mTransformers) {
                sb.append(transformer.getId());
            }
            id = sb.toString();
        }
        return id;
    }
}*/
