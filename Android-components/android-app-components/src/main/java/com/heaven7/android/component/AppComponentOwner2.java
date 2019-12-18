package com.heaven7.android.component;

import android.app.Activity;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.heaven7.android.component.guide.AppGuideComponent;
import com.heaven7.android.component.image.AppImageComponent;
import com.heaven7.android.component.lifecycle.LifeCycleComponent;
import com.heaven7.android.component.lifecycle.LifeCycleComponent2;
import com.heaven7.android.component.loading.AppLoadingComponent;
import com.heaven7.android.component.toast.AppToastComponent;

/**
 * app fragment component owner
 * @author heaven7
 */
public class AppComponentOwner2 extends AbstractLifeCycleComponentOwner<LifeCycleComponent2> implements LifecycleObserver {

    private static final String TAG = "AppComponentOwner";
    private final AppComponentFactory mFactory;

    private AppImageComponent mImageCpt;
    private AppLoadingComponent mLoadingCpt;
    private AppGuideComponent mGuideCpt;
    private AppToastComponent mToastCpt;

    /**
     * create app-component context. this should be called before 'super.onCreate(saveInstanceState)'
     *
     * @param owner  the owner
     */
    public AppComponentOwner2(@NonNull LifecycleOwner owner) {
        this(owner, null);
    }
    public AppComponentOwner2(@NonNull LifecycleOwner owner, AppComponentFactory factory) {
        super(owner);
        this.mFactory = factory;
        // ReportFragment.injectIfNeededIn(activity);
    }
    /**
     * get the activity or null if the owner is unknown.
     * @param <T> the activity type
     * @return the activity
     */
    @SuppressWarnings("unchecked")
    public <T extends FragmentActivity> T getActivity() {
        LifecycleOwner owner = getLifecycleOwner();
        if(owner instanceof FragmentActivity){
            return (T) owner;
        }else if(owner instanceof Fragment){
            return (T) ((Fragment) owner).getActivity();
        }else if (owner instanceof android.app.Fragment){
            return (T) ((android.app.Fragment) owner).getActivity();
        }
        return null;
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

    private void registerLifeCycleContextIfNeed(Object component) {
        if (component != null && component instanceof LifeCycleComponent2) {
            mWeakLives.add(new SmartReference0<>((LifeCycleComponent2) component));
        }
    }
    @Override
    protected void onLifeCycle(LifecycleOwner owner, LifeCycleComponent2 t, int liftCycle) {
        t.onLifeCycle(owner, liftCycle);
    }
}