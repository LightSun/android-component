package com.heaven7.android.component.image;

import android.graphics.Bitmap;

/**
 * the bitmap transformer
 */
public interface BitmapTransformer {

    /**
     * called on transform the bitmap.
     * @param key the key of image
     * @param pool the bitmap pool
     * @param source    the source.
     * @param outWidth  the out/expect width
     * @param outHeight the out/expect height.
     * @return a new bitmap
     */
    Bitmap transform(String key, BitmapPool pool, Bitmap source, int outWidth, int outHeight);

    /**
     * get the id of this transformer.
     * @return the id
     */
    String getId();
}