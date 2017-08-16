package com.heaven7.android.components.demo;

import android.content.pm.ProviderInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.heaven7.android.component.guide.AppGuideComponent;
import com.heaven7.android.component.image.AppImageComponent;
import com.heaven7.android.component.loading.AppLoadingComponent;
import com.heaven7.android.component.toast.AppToastComponent;
import com.heaven7.android.components.demo.imageimpl.GlideAppImageComponent;
import com.heaven7.android.components.demo.toastimpl.AppToastComponentImpl;
import com.heaven7.android.util2.BackKeyListener;
import com.heaven7.android.util2.GuideHelper;

import butterknife.ButterKnife;

/**
 * Created by heaven7 on 2017/8/15 0015.
 */

public abstract class BaseActivity extends AppCompatActivity implements AppComponentContext{

    private final GlideAppImageComponent mGaic = new GlideAppImageComponent();
    private AppGuideComponent mGuideCP;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPreSetContentView();

        setContentView(getLayoutId());
        ButterKnife.bind(this);
        onInitialize(this, savedInstanceState);
        //沉浸式
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            int height = SystemInfo.getStatusBarHeight(this);
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            if(height > 0 && contentView != null) {
                View statusBarView = new View(this);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, height);
                statusBarView.setBackgroundResource(R.drawable.shape_status_bar_color);
                contentView.addView(statusBarView, lp);
            }
        }*/
        //register back key listener of guide.
        getAppGuideComponent().setOnKeyListener(new BackKeyListener() {
            @Override
            protected void onBackPressed() {
                getAppGuideComponent().dismiss();
            }
        });
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        getAppImageComponent().getBitmapPool(this).clearMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        getAppImageComponent().getBitmapPool(this).trimMemory(level);
    }

    //======================================================================

    @Override
    public AppImageComponent getAppImageComponent() {
        return mGaic;
    }

    @Override
    public AppGuideComponent getAppGuideComponent() {
        if(mGuideCP == null){
            mGuideCP = new GuideHelper(this, getLayoutId());
        }
        return mGuideCP ;
    }

    @Override
    public AppToastComponent getAppToastComponent() {
        return AppToastComponentImpl.create(this);
    }
    @Override
    public AppLoadingComponent getAppLoadingComponent() {
        return null;
    }

    protected void onPreSetContentView() {

    }
}
