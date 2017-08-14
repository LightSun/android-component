package com.heaven7.android.component.image;

import android.graphics.Bitmap;

/**
 * multi bitmap transformation
 * @author heaven7
 */
public class MultiBitmapTransformation implements AppImageComponent.BitmapTransformer {

    final AppImageComponent.BitmapTransformer[] mTransformers;

    @SafeVarargs
    public MultiBitmapTransformation(AppImageComponent.BitmapTransformer... transformations) {
        if (transformations.length < 1) {
            throw new IllegalArgumentException("MultiTransformation must contain at least one Transformation");
        }
        this.mTransformers = transformations;
    }

    @Override
    public Bitmap transform(Bitmap source, int outWidth, int outHeight) {

        Bitmap previous = source;
        for (AppImageComponent.BitmapTransformer transformation : mTransformers) {
            previous = transformation.transform(previous, outWidth, outHeight);
        }
        return previous;
    }
}