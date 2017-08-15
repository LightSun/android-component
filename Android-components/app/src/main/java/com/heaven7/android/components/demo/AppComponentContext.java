package com.heaven7.android.components.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.heaven7.android.component.guide.AppGuideComponent;
import com.heaven7.android.component.image.AppImageComponent;
import com.heaven7.android.component.loading.AppLoadingComponent;
import com.heaven7.android.component.toast.AppToastComponent;
import com.heaven7.core.util.Toaster;

/**
 * Created by heaven7 on 2017/8/15 0015.
 */

public interface AppComponentContext {


    AppImageComponent getAppImageComponent();
    AppLoadingComponent getAppLoadingComponent();
    AppGuideComponent getAppGuideComponent();
    AppToastComponent getAppToastComponent();
    /**
     * get the layout id.
     * @return the layout id
     */
    int getLayoutId();

    /**
     * on initialize
     * @param context the context
     * @param savedInstanceState the bundle of save instance
     */
    void onInitialize(Context context, @Nullable Bundle savedInstanceState);
}
