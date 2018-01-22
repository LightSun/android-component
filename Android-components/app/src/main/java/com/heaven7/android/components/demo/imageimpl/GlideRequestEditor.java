package com.heaven7.android.components.demo.imageimpl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.heaven7.android.component.image.BitmapTransformer;
import com.heaven7.android.component.image.ImageLoadCallback;
import com.heaven7.android.component.image.ImageRequestEditor;
import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.Throwables;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2017/8/15 0015.
 */

public class GlideRequestEditor implements ImageRequestEditor {

    private static final String KEY_PREFIX = "Android://res/";
    private static final SimpleTarget<GlideDrawable> sDefault_Target = new SimpleTarget<GlideDrawable>() {
        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {

        }
    };
    private final Context mContext;

    private DrawableTypeRequest<?> mRequest;

    private String mKey;
    private List<BitmapTransformer> mTransformers;
    private InternalListener mListener;

    private Drawable mErrorDrawable;
    private Drawable mPlaceholder;

    public GlideRequestEditor(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ImageRequestEditor load(String url) {
        return load(Uri.parse(url));
    }

    @Override
    public ImageRequestEditor load(Uri uri) {
        mKey = uri.toString();
        mRequest = Glide.with(mContext).load(uri);
        return this;
    }

    @Override
    public ImageRequestEditor load(int resId) {
        mKey = KEY_PREFIX + resId;
        mRequest = Glide.with(mContext).load(resId);
        return this;
    }

    @Override
    public ImageRequestEditor load(File image) {
        mKey = image.getAbsolutePath();
        mRequest = Glide.with(mContext).load(image);
        return this;
    }

    @Override
    public ImageRequestEditor override(int width, int height) {
        mRequest.override(width, height);
        mKey += "(w=" + width + "&h=" + height + ")";
        return this;
    }

    @Override
    public ImageRequestEditor placeholder(Drawable drawable) {
        this.mPlaceholder = drawable;
        mRequest.placeholder(drawable);
        return this;
    }

    @Override
    public ImageRequestEditor placeholder(int resourceId) {
        this.mPlaceholder = mContext.getResources().getDrawable(resourceId);
        mRequest.placeholder(resourceId);
        return this;
    }

    @Override
    public ImageRequestEditor error(int resourceId) {
        this.mErrorDrawable = mContext.getResources().getDrawable(resourceId);
        mRequest.error(resourceId);
        return this;
    }

    @Override
    public ImageRequestEditor error(Drawable drawable) {
        this.mErrorDrawable = drawable;
        mRequest.error(drawable);
        return this;
    }

    @Override
    public ImageRequestEditor diskCacheFlags(int flags) {
        final boolean cacheResult = (flags & FLAG_CACHE_RESULT) == FLAG_CACHE_RESULT;
        final boolean cacheSrc = (flags & FLAG_CACHE_SOURCE) == FLAG_CACHE_SOURCE;
        if (cacheResult) {
            if (cacheSrc) {
                mRequest.diskCacheStrategy(DiskCacheStrategy.ALL);
            } else {
                mRequest.diskCacheStrategy(DiskCacheStrategy.RESULT);
            }
        } else {
            if (cacheSrc) {
                mRequest.diskCacheStrategy(DiskCacheStrategy.SOURCE);
            } else {
                mRequest.diskCacheStrategy(DiskCacheStrategy.NONE);
            }
        }
        return this;
    }

    @Override
    public ImageRequestEditor skipMemoryCache(boolean skip) {
        mRequest.skipMemoryCache(skip);
        return this;
    }

    @Override
    public ImageRequestEditor callback(final ImageLoadCallback callback) {
        Throwables.checkNull(callback);
        mRequest.listener(mListener = new InternalListener(callback));
        return this;
    }

    @Override
    public ImageRequestEditor transform(BitmapTransformer transformer) {
        if (mTransformers == null) {
            mTransformers = new ArrayList<BitmapTransformer>();
        }
        mTransformers.add(transformer);
        return this;
    }

    @Override
    public ImageRequestEditor transform(BitmapTransformer... transformers) {
        if (mTransformers == null) {
            mTransformers = new ArrayList<BitmapTransformer>();
        }
        mTransformers.addAll(Arrays.asList(transformers));
        return this;
    }

    @Override
    public ImageRequestEditor circle(float borderWidth, int borderColor) {
        if (mTransformers == null) {
            mTransformers = new ArrayList<BitmapTransformer>();
        }
        mTransformers.add(new CircleTransformer(borderWidth, borderColor));
        return this;
    }

    @Override
    public ImageRequestEditor circle() {
        if (mTransformers == null) {
            mTransformers = new ArrayList<BitmapTransformer>();
        }
        mTransformers.add(new CircleTransformer());
        return this;
    }

    @Override
    public ImageRequestEditor round(float roundSize) {
        if (mTransformers == null) {
            mTransformers = new ArrayList<BitmapTransformer>();
        }
        mTransformers.add(new RoundTransformer(roundSize));
        return this;
    }

    @Override
    public void into(ImageView view) {
        onPreStart();
        mRequest.into(view);
    }

    @Override
    public void load() {
        onPreStart();
        final InternalListener l = this.mListener;
        if(l == null){
            throw new IllegalStateException("you must call callback(...) before load.");
        }
        mListener = null;
        mRequest.into(sDefault_Target);
    }

    @Override
    public void startLoad() {

    }

    @Override
    public ImageRequestEditor markLocal(boolean isLocal) {
        return null;
    }

    @Override
    public ImageRequestEditor markVideo(boolean fromVideo, long frameTime) {
        return null;
    }

    private void onPreStart() {
        if (mTransformers != null) {
            WrappedBitmapTransformation transformation = new WrappedBitmapTransformation(mContext);
            transformation.setKey(mKey);
            transformation.setBitmapTransformer(mTransformers);
            mTransformers = null;
            mRequest.transform(transformation);
        }
        if(mListener != null){
            mListener.onStart(mKey, mPlaceholder);
        }
    }

    private class InternalListener implements RequestListener<Object, GlideDrawable> {

        final ImageLoadCallback mCallback;

        public InternalListener(ImageLoadCallback mCallback) {
            this.mCallback = mCallback;
        }

        @Override
        public boolean onException(Exception e, Object model, Target<GlideDrawable> target,
                                   boolean isFirstResource) {
            Logger.i("InternalListener","onException","key = " + mKey);
            mCallback.onLoadFailed(mKey, e, mErrorDrawable);
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, Object model,
                                       Target<GlideDrawable> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            Logger.i("InternalListener","onResourceReady","model = " + model); //if load url. is url.
            GlideBitmapDrawable gbd = (GlideBitmapDrawable) resource;
            mCallback.onLoadComplete(mKey, gbd.getBitmap());
            return false;
        }

        public void onStart(String key, Drawable placeHolder){
            Logger.i("InternalListener","onStart","key = " + key);
            mCallback.onLoadStarted(key, placeHolder);
        }
    }
}
