package com.heaven7.android.component;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.Build;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.heaven7.android.component.guide.AppGuideComponent;
import com.heaven7.android.component.image.AppImageComponent;
import com.heaven7.android.component.lifecycle.LifeCycleComponent;
import com.heaven7.android.component.loading.AppLoadingComponent;
import com.heaven7.android.component.toast.AppToastComponent;
import com.heaven7.java.base.util.SmartReference;

import java.util.ArrayList;
import java.util.Iterator;

import static com.heaven7.android.component.lifecycle.LifeCycleComponent.ON_CREATE;
import static com.heaven7.android.component.lifecycle.LifeCycleComponent.ON_DESTROY;
import static com.heaven7.android.component.lifecycle.LifeCycleComponent.ON_PAUSE;
import static com.heaven7.android.component.lifecycle.LifeCycleComponent.ON_RESUME;
import static com.heaven7.android.component.lifecycle.LifeCycleComponent.ON_START;
import static com.heaven7.android.component.lifecycle.LifeCycleComponent.ON_STOP;

/**
 * the app component owner.
 * Created by heaven7 on 2017/8/15 0015.
 * @see AppImageComponent
 * @see AppLoadingComponent
 * @see AppToastComponent
 * @see AppGuideComponent
 *
 * @since 1.0.5
 */
public class AppComponentOwner implements LifecycleObserver {
    private static final String TAG = "AppComponentOwner";
    private final ArrayList<SmartReference0> mWeakLives;
    private final AppComponentFactory mFactory;
    private final AppCompatActivity mContext;

    private AppImageComponent mImageCpt;
    private AppLoadingComponent mLoadingCpt;
    private AppGuideComponent mGuideCpt;
    private AppToastComponent mToastCpt;

    /**
     * create app-component context. this should be called before 'super.onCreate(saveInstanceState)'
     *
     * @param activity  the activity context
     * @param factory   the component factory
     */
    public AppComponentOwner(@NonNull AppCompatActivity activity, @NonNull AppComponentFactory factory) {
        this.mContext = activity;
        this.mFactory = factory;
        this.mWeakLives = new ArrayList<>(8);
        activity.getLifecycle().addObserver(this);
        // ReportFragment.injectIfNeededIn(activity);
    }

    /**
     * get the activity.
     * @param <T> the activity type
     * @return the activity
     */
    @SuppressWarnings("unchecked")
    public final <T extends AppCompatActivity> T getActivity() {
        return (T) mContext;
    }

    /**
     * register the life cycle component
     * @param component   the life cycle component
     * @since 1.0.6
     */
    public final void registerLifeCycleComponent(LifeCycleComponent component) {
        mWeakLives.add(new SmartReference0(component));
    }

    /**
     * unregister the life cycle component
     * @param component the life cycle component
     * @since 1.0.6
     */
    public final void unregisterLifeCycleComponent(LifeCycleComponent component) {
        findLifeCycleComponent(component, true);
    }

    /**
     * register the life cycle component as weakly..
     * @param component  the life cycle component which will be weak reference
     * @since 1.0.7
     */
    public final void registerLifeCycleComponentWeakly(LifeCycleComponent component) {
        final SmartReference0 srf = new SmartReference0(component);
        srf.tryWeakReference();
        mWeakLives.add(srf);
    }

    /**
     * indicate has the life cycle component or not.
     * @param component the life cycle component
     * @return true if has the target life cycle component
     * @since 1.0.6
     */
    public final boolean hasLifeCycleComponent(LifeCycleComponent component){
        return findLifeCycleComponent(component, false) != null;
    }
    public @Nullable
    AppImageComponent getAppImageComponent() {
        if (mImageCpt == null) {
            mImageCpt = mFactory.onCreateAppImageComponent(getActivity());
            registerLifeCycleContextIfNeed(mImageCpt);
        }
        return mImageCpt;
    }

    public @Nullable
    AppLoadingComponent getAppLoadingComponent() {
        if (mLoadingCpt == null) {
            mLoadingCpt = mFactory.onCreateAppLoadingComponent(getActivity());
            registerLifeCycleContextIfNeed(mLoadingCpt);
        }
        return mLoadingCpt;
    }

    public @Nullable
    AppGuideComponent getAppGuideComponent() {
        if (mGuideCpt == null) {
            mGuideCpt = mFactory.onCreateAppGuideComponent(getActivity());
            registerLifeCycleContextIfNeed(mGuideCpt);
        }
        return mGuideCpt;
    }

    public @Nullable
    AppToastComponent getAppToastComponent() {
        if (mToastCpt == null) {
            mToastCpt = mFactory.onCreateAppToastComponent(getActivity());
            registerLifeCycleContextIfNeed(mToastCpt);
        }
        return mToastCpt;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    @CallSuper
    public void onCreate() {
        performLifeCycle(ON_CREATE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    @CallSuper
    public void onStart() {
        performLifeCycle(ON_START);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @CallSuper
    public void onResume() {
        performLifeCycle(ON_RESUME);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @CallSuper
    public void onPause() {
        performLifeCycle(ON_PAUSE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    @CallSuper
    public void onStop() {
        performLifeCycle(ON_STOP);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @CallSuper
    public void onDestroy() {
        performLifeCycle(ON_DESTROY);
    }

    private LifeCycleComponent findLifeCycleComponent(LifeCycleComponent context, boolean remove) {
        LifeCycleComponent result = null;
        final Iterator<SmartReference0> it = mWeakLives.iterator();
        for (; it.hasNext(); ) {
            final SmartReference0 srf = it.next();
            if (srf.isAlive()) {
                final LifeCycleComponent tem = srf.get();
                if (tem == context || tem.equals(context)) {
                    result = tem;
                    if(remove) {
                        it.remove();
                    }
                    break;
                }
            } else {
                it.remove();
            }
        }
        return result;
    }

    private void registerLifeCycleContextIfNeed(Object component) {
        if (component != null && component instanceof LifeCycleComponent) {
            mWeakLives.add(new SmartReference0((LifeCycleComponent) component));
        }
    }

    private void performLifeCycle(int liftCycle) {
        final Activity activity = getActivity();
        //currently flag only use as single, future may be multi
        final Iterator<SmartReference0> it = mWeakLives.iterator();
        for (; it.hasNext(); ) {
            SmartReference<LifeCycleComponent> item = it.next();
            if (item.isAlive()) {
                item.get().onLifeCycle(activity, liftCycle);
            } else {
                it.remove();
            }
        }
    }

    /**
     * @since 1.0.6
     */
    private static class SmartReference0 extends SmartReference<LifeCycleComponent> {

        /**
         * create the smart reference for target object.
         *
         * @param t the object to reference.
         */
        SmartReference0(LifeCycleComponent t) {
            super(t);
        }

        @Override
        protected boolean shouldWeakReference(LifeCycleComponent t) {
            return t instanceof AppComponentContext
                    || t instanceof Context
                    || t instanceof Fragment
                    || t instanceof android.support.v4.app.Fragment
                    || t instanceof View
                    || t instanceof Dialog;
        }

        @Override
        protected boolean shouldDestroyReference(LifeCycleComponent t) {
            final String name = t.getClass().getName();
            if(t instanceof Activity){
                final Activity ac = (Activity) t;
                if(ac.isFinishing()){
                    Log.w(TAG,"shouldDestroyReference >>> the activity(" + name + ") is finishing.");
                    return true;
                }
                if (Build.VERSION.SDK_INT >= 17 && ac.isDestroyed()) {
                    Log.w(TAG, "shouldDestroyReference>>> memory leaked ? activity = "
                            + name);
                    return true;
                }
            }
            if(t instanceof android.support.v4.app.Fragment){
                final android.support.v4.app.Fragment frag = (android.support.v4.app.Fragment) t;
                if(frag.isDetached() || frag.isRemoving()){
                    Log.w(TAG,"shouldDestroyReference>>> fragment is detached or removing. fragment = "
                            + name);
                    return true;
                }
            }
            if( t instanceof Fragment){
                final Fragment frag = (Fragment) t;
                if(frag.isDetached() || frag.isRemoving()){
                    Log.w(TAG,"shouldDestroyReference>>> fragment is detached or removing. fragment = "
                            + name);
                    return true;
                }
            }
            return false;
        }
    }
}
