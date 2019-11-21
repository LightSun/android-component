package com.heaven7.android.component;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.heaven7.android.component.lifecycle.LifeCycleComponent2;

/**
 * app fragment component owner
 * @author heaven7
 */
public class AppComponentOwner2 extends AbstractLifeCycleComponentOwner<LifeCycleComponent2> implements LifecycleObserver {

    private static final String TAG = "AppComponentOwner";

    /**
     * create app-component context. this should be called before 'super.onCreate(saveInstanceState)'
     *
     * @param owner  the owner
     */
    public AppComponentOwner2(@NonNull LifecycleOwner owner) {
        super(owner);
        // ReportFragment.injectIfNeededIn(activity);
    }

    @Override
    protected void onLifeCycle(LifecycleOwner owner, LifeCycleComponent2 t, int liftCycle) {
        t.onLifeCycle(owner, liftCycle);
    }
}