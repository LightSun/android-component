package com.heaven7.android.component.gallery;

import android.app.Activity;
import android.content.Intent;

import com.heaven7.android.component.AppComponentContext;

import java.util.List;

/**
 * the image pick delegate
 * @author heaven7
 * @since 1.1.4
 */
public interface ImagePickComponent extends AppComponentContext {

    /**
     * start pick image from camera. you should start with startActivityForResult and setResult for callback
     * @param activity the activity
     * @param op the pick option
     * @param callback the callback of pick
     */
     void startPickFromCamera(Activity activity, PickOption op, Callback callback);

    /**
     * start pick from gallery.
     * @param activity the activity
     * @param op the pick option
     * @param callback the callback of pick
     */
     void startPickFromGallery(Activity activity, PickOption op, Callback callback);

    /**
     * often we handle data from activity onActivityResult.
     * @param activity the activity
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the intent data.
     */
    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);

    /**
     * the callback of pick
     */
    interface Callback{
        /**
         * called on pick result
         * @param activity the activity
         * @param files the files.
         */
        void onPickResult(Activity activity, List<String> files);
    }
}
