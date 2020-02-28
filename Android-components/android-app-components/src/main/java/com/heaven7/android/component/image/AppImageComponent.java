package com.heaven7.android.component.image;

import android.content.Context;
import android.graphics.Bitmap;

import com.heaven7.android.component.AppComponentContext;

/**
 * image component of android application
 * here is the Editor demo.<pre><code>
getAppImageComponent().newEditor(this)
     .load(TestUtil.TEST_IMAGE)
     .placeholder(R.mipmap.ic_launcher)
     .override(450, 300)
     .diskCacheFlags(ImageRequestEditor.FLAG_CACHE_SOURCE)
     // .transform(new CenterCropTransformer())
     .round(30)
     // .callback()
     .into(mIv1);
 * </code></pre>
 * And here is the memory manage demo. In Activity.
 * <pre><code>
public void onLowMemory() {
super.onLowMemory();
getAppImageComponent().getBitmapPool(this).clearMemory();
}

 public void onTrimMemory(int level) {
 super.onTrimMemory(level);
 getAppImageComponent().getBitmapPool(this).trimMemory(level);
 }
 * </code></pre>
 * Created by heaven7 on 2017/8/14 0014.
 * @since 1.0.1
 */

public interface AppImageComponent extends AppComponentContext{

    /**
     * new a image request editor.
     * @param context  the context
     * @return the image request editor.
     * @see ImageRequestEditor
     */
    ImageRequestEditor newEditor(Context context);

    /**
     * get the bitmap pool
     * @param context the context.
     * @return the bitmap pool.
     * @see ImageCachePool
     */
    ImageCachePool getBitmapPool(Context context);

    /**
     * set the image load paused.
     * @param paused true to pause .
     * @since 1.0.9
     */
    void setPauseWork(boolean paused);

    /**
     * set the loading image to apply to the all image request.
     * @param resId the image resource id
     * @since 1.0.8
     */
    void setLoadingImage(int resId);
    /**
     * set the loading image to apply to the all image request.
     * @param bitmap the loading bitmap
     * @since 1.0.8
     */
    void setLoadingImage(Bitmap bitmap);

}
