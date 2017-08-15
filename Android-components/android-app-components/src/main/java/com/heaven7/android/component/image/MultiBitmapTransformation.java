package com.heaven7.android.component.image;

import android.graphics.Bitmap;

/**
 * multi bitmap transformation
 * @author heaven7
 */
public class MultiBitmapTransformation implements BitmapTransformer {

    private final BitmapTransformer[] mTransformers;

    public MultiBitmapTransformation(BitmapTransformer[] transformations) {
        if(transformations == null){
            throw new NullPointerException();
        }
        if (transformations.length < 1) {
            throw new IllegalArgumentException("MultiTransformation must contain at least one Transformation");
        }
        this.mTransformers = transformations;
    }

    @Override
    public Bitmap transform(BitmapPool pool,Bitmap source, int outWidth, int outHeight) {

        Bitmap previous = source;
        for (BitmapTransformer transformation : mTransformers) {
            previous = transformation.transform(pool, previous, outWidth, outHeight);
        }
        return previous;
    }
}