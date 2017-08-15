package com.heaven7.android.components.demo.imageimpl;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.heaven7.android.component.image.AppImageComponent;
import com.heaven7.android.component.image.BitmapPool;
import com.heaven7.android.component.image.ImageRequestEditor;

/**
 * Created by heaven7 on 2017/8/15 0015.
 */

public class GlideAppImageComponent implements AppImageComponent {

    @Override
    public ImageRequestEditor newEditor(Context context) {
        return new GlideRequestEditor(context);
    }

    @Override
    public BitmapPool getBitmapPool(Context context) {
        return new GlideBitmapPool(Glide.get(context).getBitmapPool());
    }

}
