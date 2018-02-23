package com.heaven7.android.component.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * the image load callback
 * Created by heaven7 on 2017/8/15 0015.
 * @since 1.0.1
 */

public interface ImageLoadCallback {

    /**
     * called on load start
     * @param key the key of this image request.
     * @param placeholder the place holder drawable.
     */
    void onLoadStarted(String key, Drawable placeholder);

    /**
     * called on load failed.
     * @param key the key of this image request.
     * @param e the exception
     * @param errorDrawable the error drawable
     */
    void onLoadFailed(String key, Exception e, Drawable errorDrawable);

    /**
     * called on load complete
     * @param key the key of this image request.
     * @param result the bitmap.
     */
    void onLoadComplete(String key, Bitmap result);


    /**
     * called on load gif complete
     * @param key the key of this image request.
     * @param gif the gif drawable.
     * @since 1.1.0
     */
    void onLoadGifComplete(String key,  Drawable gif);
}
