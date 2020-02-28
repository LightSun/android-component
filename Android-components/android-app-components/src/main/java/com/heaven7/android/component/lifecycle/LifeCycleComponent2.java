package com.heaven7.android.component.lifecycle;

import androidx.lifecycle.LifecycleOwner;

/**
 * the lifecycle context
 * Created by heaven7 on 2017/12/20.
 * @since 1.1.3
 */
public interface LifeCycleComponent2 {

    /**
     * callback on lifecycle changed
     * @param context the context
     * @param lifeCycle the lifecycle flag .see @{@linkplain LifeCycleComponent#ON_CREATE} and etc.
     */
    void onLifeCycle(LifecycleOwner context, int lifeCycle);
}
