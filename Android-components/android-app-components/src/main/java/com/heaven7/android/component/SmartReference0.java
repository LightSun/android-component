package com.heaven7.android.component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.heaven7.java.base.util.SmartReference;

/**
 * @since 1.0.6
 */
class SmartReference0<T> extends SmartReference<T> {

    private static final String TAG = "SmartReference0";

    /**
     * create the smart reference for target object.
     *
     * @param t the object to reference.
     */
    SmartReference0(T t) {
        super(t);
    }

    @Override
    protected boolean shouldWeakReference(T t) {
        return t instanceof AppComponentContext
                || t instanceof Context
                || t instanceof Fragment
                || t instanceof android.support.v4.app.Fragment
                || t instanceof View
                || t instanceof Dialog;
    }

    @Override
    protected boolean shouldDestroyReference(T t) {
        final String name = t.getClass().getName();
        if (t instanceof Activity) {
            final Activity ac = (Activity) t;
            if (ac.isFinishing()) {
                Log.w(TAG, "shouldDestroyReference >>> the activity(" + name + ") is finishing.");
                return true;
            }
            if (Build.VERSION.SDK_INT >= 17 && ac.isDestroyed()) {
                Log.w(TAG, "shouldDestroyReference>>> memory leaked ? activity = "
                        + name);
                return true;
            }
        }
        if (t instanceof android.support.v4.app.Fragment) {
            final android.support.v4.app.Fragment frag = (android.support.v4.app.Fragment) t;
            if (frag.isDetached() || frag.isRemoving()) {
                Log.w(TAG, "shouldDestroyReference>>> fragment is detached or removing. fragment = "
                        + name);
                return true;
            }
        }
        if (t instanceof Fragment) {
            final Fragment frag = (Fragment) t;
            if (frag.isDetached() || frag.isRemoving()) {
                Log.w(TAG, "shouldDestroyReference>>> fragment is detached or removing. fragment = "
                        + name);
                return true;
            }
        }
        return false;
    }
}