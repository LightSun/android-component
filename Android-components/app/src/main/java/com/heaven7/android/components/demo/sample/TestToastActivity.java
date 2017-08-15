package com.heaven7.android.components.demo.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.heaven7.android.component.toast.AppToastComponent;
import com.heaven7.android.components.demo.BaseActivity;
import com.heaven7.android.components.demo.R;
import com.heaven7.core.util.Toaster;

import butterknife.OnClick;

/**
 * Created by heaven7 on 2017/7/12 0012.
 */

public class TestToastActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.ac_test_toast;
    }

    @Override
    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {

    }

    @OnClick(R.id.bt_toast_normal)
    public void onClickNormalToast(View v){
        getAppToastComponent().type(AppToastComponent.TYPE_NORMAL).show("your toast message");
    }
    @OnClick(R.id.bt_toast_warn)
    public void onClickWarnToast(final View v){
        getAppToastComponent()
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.show(v.getContext(), "action end...");
                    }
                })
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.show(v.getContext(), "action start...");
                    }
                })
                .type(AppToastComponent.TYPE_WARN)
                .show("your toast message");
    }
    @OnClick(R.id.bt_toast_error)
    public void onClickErrorToast(View v){
        getAppToastComponent().type(AppToastComponent.TYPE_ERROR).show("your toast message");
    }
    @OnClick(R.id.bt_toast_click)
    public void onClickClickToast(View v){
        final View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toaster.show(v.getContext(), "Toast view was clicked.");
            }
        };
        getAppToastComponent()
                .type(AppToastComponent.TYPE_WARN)
                .enableClick(true)
                .bindView(new AppToastComponent.IViewBinder() {
                    @Override
                    public void onBind(View view) {
                        view.setOnClickListener(l);
                    }
                })
                .show("your toast message");
    }
}
