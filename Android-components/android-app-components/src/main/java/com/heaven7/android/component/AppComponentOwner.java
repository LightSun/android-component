package com.heaven7.android.component;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.heaven7.android.component.guide.AppGuideComponent;
import com.heaven7.android.component.image.AppImageComponent;
import com.heaven7.android.component.lifecycle.LifeCycleComponent;
import com.heaven7.android.component.loading.AppLoadingComponent;
import com.heaven7.android.component.toast.AppToastComponent;

/**
 * <p>for this class can only used for activity. we recommend you
 *     use {@linkplain AppComponentOwner2} instead.
 * </p>
 * the app component owner.
 * Created by heaven7 on 2017/8/15 0015.
 * @see AppImageComponent
 * @see AppLoadingComponent
 * @see AppToastComponent
 * @see AppGuideComponent
 *
 * @since 1.0.5
 */
@Deprecated
public class AppComponentOwner extends AbstractLifeCycleComponentOwner<LifeCycleComponent>{
    private final AppComponentFactory mFactory;

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
    public AppComponentOwner(@NonNull FragmentActivity activity, @NonNull AppComponentFactory factory) {
        super(activity);
        this.mFactory = factory;
    }

    /**
     * get the activity.
     * @param <T> the activity type
     * @return the activity
     */
    @SuppressWarnings("unchecked")
    public final <T extends FragmentActivity> T getActivity() {
        return (T) getLifecycleOwner();
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
        if (component != null && component instanceof LifeCycleComponent) {
            mWeakLives.add(new SmartReference0<>((LifeCycleComponent) component));
        }
    }

    @Override
    protected void onLifeCycle(LifecycleOwner owner, LifeCycleComponent item, int liftCycle) {
        item.onLifeCycle((Context) owner, liftCycle);
    }
}
