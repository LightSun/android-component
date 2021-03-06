package com.heaven7.android.components.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.heaven7.android.component.AppComponentFactory;
import com.heaven7.android.component.AppComponentOwner;
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

public abstract class BaseActivity extends AppCompatActivity implements AppContext{

    private AppComponentOwner mAppComponentOwner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mAppComponentOwner = new AppComponentOwner(this, new AppComponentFactory() {
            @Override
            public AppImageComponent onCreateAppImageComponent(AppCompatActivity activity) {
                return new GlideAppImageComponent();
            }
            @Override
            public AppLoadingComponent onCreateAppLoadingComponent(AppCompatActivity activity) {
                return BaseActivity.this.onCreateAppLoadingComponent();
            }
            @Override
            public AppGuideComponent onCreateAppGuideComponent(AppCompatActivity activity) {
                final GuideHelper helper = new GuideHelper(activity, getLayoutId());
                //register back key listener of guide.
                helper.setOnKeyListener(new BackKeyListener() {
                    @Override
                    protected void onBackPressed() {
                        getAppGuideComponent().dismiss();
                    }
                });
                return helper;
            }
            @Override
            public AppToastComponent onCreateAppToastComponent(AppCompatActivity activity) {
                return AppToastComponentImpl.create(activity);
            }
        });

        super.onCreate(savedInstanceState);

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
    }

    protected AppLoadingComponent onCreateAppLoadingComponent() {
        return null;
    }

    //======================================================================
    @Override
    public AppImageComponent getAppImageComponent() {
        return mAppComponentOwner.getAppImageComponent();
    }

    @Override
    public AppGuideComponent getAppGuideComponent() {
        return mAppComponentOwner.getAppGuideComponent() ;
    }

    @Override
    public AppToastComponent getAppToastComponent() {
        return mAppComponentOwner.getAppToastComponent();
    }
    @Override
    public AppLoadingComponent getAppLoadingComponent() {
        return mAppComponentOwner.getAppLoadingComponent();
    }
}
