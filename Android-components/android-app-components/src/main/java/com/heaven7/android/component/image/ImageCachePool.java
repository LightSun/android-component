package com.heaven7.android.component.image;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * the image(bitmap) pool contains:  memory cache and disk cache.
 * Created by heaven7 on 2017/8/15 0015.
 * @since 1.0.1
 */

public interface ImageCachePool {


    /**
     * put the bitmap to the cache.
     * @param key the key.
     * @param bitmap the bitmap
     * @return true if put success.
     */
    boolean put(String key, Bitmap bitmap);

    /**
     * get bitmap by key and some config.
     * @param key the key
     * @param width the expect image width.
     * @param height the expect image height
     * @param config the bitmap config
     * @return the expect bitmap.
     */
    Bitmap get(String key, int width, int height, Bitmap.Config config);


    /**
     * Removes all {@link android.graphics.Bitmap}s from the pool.
     */
    void clearMemory();

    /**
     * Reduces the size of the cache by evicting items based on the given level.
     *
     * @see android.content.ComponentCallbacks2
     *
     * @param level The level from {@link android.content.ComponentCallbacks2} to use to determine how many
     * {@link android.graphics.Bitmap}s to evict.
     */
    void trimMemory(int level);

    /**
     * clear the disk cache.
     * @param context the context
     */
    void clearDiskCache(Context context);
}
