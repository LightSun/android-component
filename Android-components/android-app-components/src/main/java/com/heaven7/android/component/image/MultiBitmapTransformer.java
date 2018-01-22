package com.heaven7.android.component.image;

import android.graphics.Bitmap;

import java.util.List;

/**
 * multi bitmap transformer
 * Created by heaven7 on 2018/1/22 0022.
 * @since 1.0.8
 */

public class MultiBitmapTransformer implements BitmapTransformer {

    private List<BitmapTransformer> mTransformers;
    private String id;

    public void setBitmapTransformer(List<BitmapTransformer> transformers) {
        this.mTransformers = transformers;
    }
    @Override
    public Bitmap transform(String key, ImageCachePool imageCachePool, Bitmap toTransform, int width, int height) {
        Bitmap previous = toTransform;
        for (BitmapTransformer transformer : mTransformers) {
            previous = transformer.transform(key, imageCachePool, previous, width, height);
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
}
