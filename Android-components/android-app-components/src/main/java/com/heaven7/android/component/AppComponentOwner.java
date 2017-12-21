package com.heaven7.android.component;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.heaven7.android.component.guide.AppGuideComponent;
import com.heaven7.android.component.image.AppImageComponent;
import com.heaven7.android.component.lifecycle.LifeCycleContext;
import com.heaven7.android.component.loading.AppLoadingComponent;
import com.heaven7.android.component.toast.AppToastComponent;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

import static com.heaven7.android.component.lifecycle.LifeCycleContext.ON_CREATE;
import static com.heaven7.android.component.lifecycle.LifeCycleContext.ON_DESTROY;
import static com.heaven7.android.component.lifecycle.LifeCycleContext.ON_PAUSE;
import static com.heaven7.android.component.lifecycle.LifeCycleContext.ON_RESUME;
import static com.heaven7.android.component.lifecycle.LifeCycleContext.ON_START;
import static com.heaven7.android.component.lifecycle.LifeCycleContext.ON_STOP;

/**
 * the app component owner.
 * Created by heaven7 on 2017/8/15 0015.
 * @since 1.0.5
 */
public final class AppComponentOwner implements LifecycleObserver {

    private final ArrayList<WeakReference<LifeCycleContext>> mWeakLives;
    private final AppComponentFactory mFactory;
    private final AppCompatActivity mContext;

    private AppImageComponent mImageCpt;
    private AppLoadingComponent mLoadingCpt;
    private AppGuideComponent mGuideCpt;
    private AppToastComponent mToastCpt;

    /**
     * create app-component context. this should be called before 'super.onCreate(saveInstanceState)'
     *
     * @param activity         the activity context
     * @param componentFactory the component factory
     */
    public AppComponentOwner(@NonNull AppCompatActivity activity, AppComponentFactory componentFactory) {
        this.mContext = activity;
        this.mFactory = componentFactory;
        this.mWeakLives = new ArrayList<>(6);
        activity.getLifecycle().addObserver(this);
       // ReportFragment.injectIfNeededIn(activity);
    }

    @SuppressWarnings("unchecked")
    public final <T extends AppCompatActivity> T getActivity() {
        return (T) mContext;
    }

   /* public void registerLifeCycleContext(LifeCycleContext cycleContext){
        mWeakLives.add(new WeakReference<LifeCycleContext>(cycleContext));
    }*/

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
    public void onCreate() {
        performLifeCycle(ON_CREATE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        performLifeCycle(ON_START);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        performLifeCycle(ON_RESUME);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        performLifeCycle(ON_PAUSE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        performLifeCycle(ON_STOP);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        performLifeCycle(ON_DESTROY);
    }

    private void registerLifeCycleContextIfNeed(Object component) {
        if (component != null && component instanceof LifeCycleContext) {
            mWeakLives.add(new WeakReference<LifeCycleContext>((LifeCycleContext) component));
        }
    }

    private void performLifeCycle(int liftCycle) {
        final Activity activity = getActivity();
        //currently flag only use as single, future may be multi
        final Iterator<WeakReference<LifeCycleContext>> it = mWeakLives.iterator();
        for (; it.hasNext(); ) {
            WeakReference<LifeCycleContext> item = it.next();
            LifeCycleContext cycleContext;
            if ( (cycleContext = item.get()) != null) {
                cycleContext.onLifeCycle(activity, liftCycle);
            }else{
                it.remove();
            }
        }
    }
}
