package com.heaven7.android.component.image;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * the image(bitmap) pool contains:  memory cache and disk cache.
 * <p><h2>Here is a sample implements of Glide.</h2></p>
 * <pre><code>
 public class GlideCachePool implements ImageCachePool {

     private final com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool mImpl;

     public GlideCachePool(com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool mImpl) {
         this.mImpl = mImpl;
     }

     //@Override
     public boolean put(String key, Bitmap bitmap) {
         return mImpl.put(bitmap);
     }

     //@Override
     public Bitmap get(String key, int width, int height, Bitmap.Config config) {
         return mImpl.get(width, height, config);
     }

     //@Override
     public void clearMemory() {
         mImpl.clearMemory();
     }

     //@Override
     public void trimMemory(int level) {
         mImpl.trimMemory(level);
     }

     //@Override
     public void clearDiskCache(Context context) {
         Glide.get(context).clearDiskCache();
     }
 }
 * </code></pre>
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
