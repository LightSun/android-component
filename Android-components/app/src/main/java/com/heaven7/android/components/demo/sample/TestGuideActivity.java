package com.heaven7.android.components.demo.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.heaven7.android.component.guide.AppGuideComponent;
import com.heaven7.android.component.guide.GuideComponent;
import com.heaven7.android.component.guide.RelativeLocation;
import com.heaven7.android.components.demo.BaseActivity;
import com.heaven7.android.components.demo.R;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by heaven7 on 2017/4/20 0020.
 */

public class TestGuideActivity extends BaseActivity {

    @BindView(R.id.tb)
    ToggleButton mTb_1;

    private TextView mTip;
    private byte mIndex = -1;

    @Override
    public int getLayoutId() {
        return R.layout.test_toggle_button;
    }

    @Override
    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {
        ViewGroup tipView = (ViewGroup) getLayoutInflater().inflate(R.layout.test_tip, null);
        mTip = (TextView) tipView.findViewById(R.id.test_tv_tip);
        tipView.removeAllViews();

        showTip();
    }

    private void showTip() {
        mIndex ++ ;
        /*final GuideComponent gc = new GuideComponent.Builder()
                .anchor(mTb_1)
                .tip(mTip)
                .location(new RelativeLocation(
                        (byte) ((mIndex % 4) + 1), 40,
                        GuideHelper.RELATIVE_ANCHOR, 0.5f))
                .build();
        MainWorker.postDelay(20, new Runnable() {
            @Override
            public void run() {
                getAppGuideComponent().show(gc, new AppGuideComponent.GuideCallback() {
                    @Override
                    public boolean handleClickRoot(View root) {
                        Logger.i("TestUiActivity","handleClickRoot","");
                        return super.handleClickRoot(root);
                    }
                    @Override
                    public boolean handleClickTip(View tip) {
                        Logger.i("TestUiActivity","handleClickTip","");
                        return super.handleClickTip(tip);
                    }
                    @Override
                    public boolean handleClickAnchor(View copyOfAnchor) {
                        Logger.i("TestUiActivity","handleClickAnchor","");
                        return super.handleClickAnchor(copyOfAnchor);
                    }
                    @Override
                    public void onShow() {
                        Logger.i("TestUiActivity","onShow","");
                        super.onShow();
                    }
                    @Override
                    public void onDismiss() {
                        Logger.i("TestUiActivity","onDismiss","");
                        super.onDismiss();
                    }
                });
            }
        });*/
    }

    @OnClick(R.id.tb)
    public void onClickShowToast(View v){
        Logger.i("TestUiActivity","onClickShowToast","tb");
        getAppToastComponent().show("dsfjdsfjdsfjdsk");
    }
    @OnClick(R.id.sc)
    public void onClickSwitchCompat(View v){
        showTip();
    }
}
