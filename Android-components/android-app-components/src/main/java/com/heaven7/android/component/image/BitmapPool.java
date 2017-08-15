package com.heaven7.android.component.image;

import android.graphics.Bitmap;

/**
 * the bitmap pool.
 * Created by heaven7 on 2017/8/15 0015.
 */

public interface BitmapPool {


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
}
