package com.heaven7.android.component;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.heaven7.android.component.lifecycle.LifeCycleComponent2;
import com.heaven7.java.base.util.SmartReference;

import java.util.Iterator;

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

    protected void performLifeCycle(int liftCycle) {
        LifecycleOwner owner = getLifecycleOwner();
        //currently flag only use as single, future may be multi
        final Iterator<SmartReference0<LifeCycleComponent2>> it = mWeakLives.iterator();
        for (; it.hasNext(); ) {
            SmartReference<LifeCycleComponent2> item = it.next();
            if (item.isAlive()) {
                item.get().onLifeCycle(owner, liftCycle);
            } else {
                it.remove();
            }
        }
    }

}