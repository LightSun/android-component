package com.heaven7.android.component;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.heaven7.android.component.guide.AppGuideComponent;
import com.heaven7.android.component.image.AppImageComponent;
import com.heaven7.android.component.loading.AppLoadingComponent;
import com.heaven7.android.component.toast.AppToastComponent;

/**
 * the app component factory
 * @author heaven7
 * @since 1.0.5
 */
public interface AppComponentFactory {

    /**
     * @author heaven7
     * @since 1.0.5
     */
    class SimpleAppComponentFactory implements AppComponentFactory{
        @Override
        public AppImageComponent onCreateAppImageComponent(Activity activity) {
            return null;
        }
        @Override
        public AppLoadingComponent onCreateAppLoadingComponent(Activity activity) {
            return null;
        }
        @Override
        public AppGuideComponent onCreateAppGuideComponent(Activity activity) {
            return null;
        }
        @Override
        public AppToastComponent onCreateAppToastComponent(Activity activity) {
            return null;
        }
    }

    /**
     * called on create app image component.
     * @param activity the activity
     * @return the app image component
     */
    AppImageComponent onCreateAppImageComponent(Activity activity);
    /**
     * called on create app loading component.
     * @param activity the activity
     * @return the app loading component
     */
    AppLoadingComponent onCreateAppLoadingComponent(Activity activity);
    /**
     * called on create app guide component.
     * @param activity the activity
     * @return the app guide component
     */
    AppGuideComponent onCreateAppGuideComponent(Activity activity);
    /**
     * called on create app toast component.
     * @param activity the activity
     * @return the app toast component
     */
    AppToastComponent onCreateAppToastComponent(Activity activity);
}
