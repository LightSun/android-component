package com.heaven7.android.component.lifecycle;

import android.content.Context;

/**
 * the lifecycle context
 * Created by heaven7 on 2017/12/20.
 * @since 1.0.5
 */
public interface LifeCycleComponent {

    int ON_CREATE  = 0x00000001;
    int ON_START   = 0x00000002;
    int ON_RESUME  = 0x00000004;
    int ON_PAUSE   = 0x00000008;
    int ON_STOP    = 0x00000010;
    int ON_DESTROY = 0x00000020;

    /**
     * callback on lifecycle changed
     * @param context the context
     * @param lifeCycle the lifecycle flag
     */
    void onLifeCycle(Context context, int lifeCycle);
}
