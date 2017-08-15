package com.heaven7.android.components.demo.imageimpl;

import android.graphics.Bitmap;

import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.heaven7.android.component.image.ImageCachePool;
import com.heaven7.android.component.image.BitmapTransformer;

/**
 * Created by heaven7 on 2017/8/15 0015.
 */

public class CenterCropTransformer implements BitmapTransformer{

    @Override
    public Bitmap transform(String key, ImageCachePool pool, Bitmap source, int outWidth, int outHeight) {
        final Bitmap toReuse = pool.get(key, outWidth, outHeight, (source.getConfig() != null
                ? source.getConfig() : Bitmap.Config.ARGB_8888));
        Bitmap transformed = TransformationUtils.centerCrop(toReuse, source, outWidth, outHeight);
        if (toReuse != null && toReuse != transformed && !pool.put(key, toReuse)) {
            toReuse.recycle();
        }
        return transformed;
    }

    @Override
    public String getId() {
        return null;
    }
}
