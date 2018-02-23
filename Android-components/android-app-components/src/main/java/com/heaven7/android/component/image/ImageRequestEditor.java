package com.heaven7.android.component.image;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import java.io.File;

/**
 * the image request editor, used by {@linkplain AppImageComponent}.
 * Created by heaven7 on 2017/8/15 0015.
 * @since 1.0.1
 */

public interface ImageRequestEditor {

    /**
     * flag of cache the source.
     */
    int FLAG_CACHE_SOURCE = 0x01;
    /**
     * flag of cache the result.
     */
    int FLAG_CACHE_RESULT = 0x02;


    /**
     * load the image from url
     * @param url the image url
     * @return this.
     */
    ImageRequestEditor load(String url);
    /**
     * load the image from uri
     * @param uri the image uri
     * @return this.
     */
    ImageRequestEditor load(Uri uri);
    /**
     * load the image from local resource.
     * @param resId the local image res id
     * @return this.
     */
    ImageRequestEditor load(int resId);


    /**
     * load the image from file
     * @param image the image file
     * @return this
     */
    ImageRequestEditor load(File image);

    /**
     * assigned the expect image width and height.
     * @param width the expect image width
     * @param height the expect image height
     * @return this.
     */
    ImageRequestEditor override(int width, int height);

    /**
     * assign the placeholder drawable.
     * @param drawable the placeholder
     * @return this.
     */
    ImageRequestEditor placeholder(Drawable drawable);
    /**
     * assign the placeholder resource id.
     * @param resourceId the placeholder id
     * @return this.
     */
    ImageRequestEditor placeholder(int resourceId);
    /**
     * assign the error drawable id.
     * @param resourceId the error resource id
     * @return this.
     */
    ImageRequestEditor error(int resourceId);
    /**
     * assign the error drawable.
     * @param drawable the error drawable
     * @return this.
     */
    ImageRequestEditor error(Drawable drawable);

    /**
     * set the disk cache flags.
     * @param flags the flags. see {@linkplain #FLAG_CACHE_RESULT} , {@linkplain #FLAG_CACHE_SOURCE}
     * @return this.
     */
    ImageRequestEditor diskCacheFlags(int flags);
    /**
     * set skip memory cache or not.
     * @param skip true to skip memory cache.
     * @return this.
     */
    ImageRequestEditor skipMemoryCache(boolean skip);

    /**
     * set the image load callback.
     * @param callback the image load callback.
     * @return this.
     */
    ImageRequestEditor callback(ImageLoadCallback callback);

    /**
     * set the bitmap transformer.
     * @param transformer the bitmap transformer.
     * @return this.
     */
    ImageRequestEditor transform(BitmapTransformer transformer);

    /**
     * set the bitmap transformer.
     * @param transformers the bitmap transformers.
     * @return this.
     */
    ImageRequestEditor transform(BitmapTransformer... transformers);

    /**
     * set the image to circle.
     * @param borderWidth the border width in pixes. or 0 if not need.
     * @param borderColor the border color.
     * @return this.
     */
    ImageRequestEditor circle(float borderWidth, int borderColor);

    /**
     * set the image to circle with no border.
     * @return this.
     */
    ImageRequestEditor circle();

    /**
     * set round size
     * @param roundSize the round size
     * @return this
     */
    ImageRequestEditor round(float roundSize);


    /**
     * make the setting of load image apply to the image view.
     * @param view the image view
     */
    void into(ImageView view);

    /**
     * <p>Use {@linkplain #startLoad()} instead.</p>
     * load image immediately
     */
    @Deprecated
    void load();

    /**
     * start to load the image immediately.
     * @since 1.0.8
     */
    void startLoad();

    /**
     * mark the request resource is from local file/uri or not.
     * @param isLocal true is from local
     * @return this
     * @since 1.0.8
     */
    ImageRequestEditor markLocal(boolean isLocal);

    /**
     * mark some video info .
     * @param fromVideo is from video or not.
     * @param frameTime the frame time of video. if fromVideo is true.
     * @return this
     * @since 1.0.8
     */
    ImageRequestEditor markVideo(boolean fromVideo, long frameTime);

    /**
     * apply the option for this request.
     * @param <Option> the option type
     * @param option the option
     * @return this
     * @since 1.1.0
     */
    <Option>ImageRequestEditor applyOption(Option option);
}
