package com.heaven7.android.component.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

/**
 * image component of android application
 * Created by heaven7 on 2017/8/14 0014.
 */

public interface AppImageComponent {

    /**
     * flag of cache the source.
     */
    int FLAG_CACHE_SOURCE = 0x01;
    /**
     * flag of cache the result.
     */
    int FLAG_CACHE_RESULT = 0x02;

    /**
     * the image callback
     */
    interface ImageLoadCallback{
        /**
         * called on load start
         * @param placeholder the place holder drawable.
         */
        void onLoadStarted(Drawable placeholder);

        /**
         * called on load failed.
         * @param e the exception
         * @param errorDrawable the error drawable
         */
        void onLoadFailed(Exception e, Drawable errorDrawable);

        /**
         * called on load complete
         * @param result the bitmap.
         */
        void onLoadComplete(Bitmap result);
    }

    /**
     * the bitmap transformer
     */
    interface BitmapTransformer{

        /**
         * called on transform the bitmap.
         * @param source the source.
         * @param outWidth the out/expect width
         * @param outHeight the out/expect width.
         * @return a new bitmap
         */
        Bitmap transform(Bitmap source, int outWidth, int outHeight);
    }

    /**
     * assigned the image url
     * @param url the image url
     * @return this.
     */
    AppImageComponent fromUrl(String url);
    /**
     * assigned the image uri
     * @param uri the image uri
     * @return this.
     */
    AppImageComponent fromUri(Uri uri);
    /**
     * assigned the local image resource.
     * @param resId the local image resid
     * @return this.
     */
    AppImageComponent fromLocal(int resId);

    /**
     * assigned the expect image width and height.
     * @param width the expect image width
     * @param height the expect image height
     * @return this.
     */
    AppImageComponent override(int width, int height);

    /**
     * assign the placeholder drawable.
     * @param drawable the placeholder
     * @return this.
     */
    AppImageComponent placeholder(Drawable drawable);
    /**
     * assign the placeholder resource id.
     * @param resourceId the placeholder id
     * @return this.
     */
    AppImageComponent placeholder(int resourceId);
    /**
     * assign the error drawable id.
     * @param resourceId the error resource id
     * @return this.
     */
    AppImageComponent error(int resourceId);
    /**
     * assign the error drawable.
     * @param drawable the error drawable
     * @return this.
     */
    AppImageComponent error(Drawable drawable);

    /**
     * set the disk cache flags.
     * @param flags the flags. see {@linkplain #FLAG_CACHE_RESULT} , {@linkplain #FLAG_CACHE_SOURCE}
     * @return this.
     */
    AppImageComponent diskCacheFlags(int flags);
    /**
     * set skip memory cache or not.
     * @param skip true to skip memory cache.
     * @return this.
     */
    AppImageComponent skipMemoryCache(boolean skip);

    /**
     * set the image load callback.
     * @param callback the image load callback.
     * @return this.
     */
    AppImageComponent callback(ImageLoadCallback callback);

    /**
     * set the bitmap transformer.
     * @param transformer the bitmap transformer.
     * @return this.
     */
    AppImageComponent transform(BitmapTransformer transformer);

    /**
     * set the bitmap transformer.
     * @param transformers the bitmap transformers.
     * @return this.
     */
    AppImageComponent transform(BitmapTransformer... transformers);

    /**
     * set the image to circle.
     * @param borderWidth the border width in pixes. or 0 if not need.
     * @param borderColor the border color.
     * @return this.
     */
    AppImageComponent circle(float borderWidth, int borderColor);

    /**
     * set the image to circle
     * @return this.
     */
    AppImageComponent circle();

    /**
     * set round size
     * @param roundSize the round size
     * @return this
     */
    AppImageComponent round(float roundSize);

    /**
     * make the setting of load image apply to the image view.
     * @param view the image view
     */
    void into(ImageView view);

    /**
     * load immediately
     * @throws IllegalStateException if state error.
     */
    void load() throws IllegalStateException;
}
