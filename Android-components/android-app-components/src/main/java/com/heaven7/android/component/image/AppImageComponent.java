package com.heaven7.android.component.image;

import android.content.Context;

/**
 * image component of android application
 * Created by heaven7 on 2017/8/14 0014.
 */

public interface AppImageComponent {


    /**
     * new a image request editor.
     * @return the image request editor.
     * @see ImageRequestEditor
     */
    ImageRequestEditor newEditor();

    /**
     * get the bitmap pool
     * @param context the context.
     * @return the bitmap pool.
     * @see BitmapPool
     */
    BitmapPool getBitmapPool(Context context);

}
