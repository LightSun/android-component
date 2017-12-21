package com.heaven7.android.components.demo.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.heaven7.adapter.BaseSelector;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.android.component.loading.AppLoadingComponent;
import com.heaven7.android.components.demo.BaseActivity;
import com.heaven7.android.components.demo.R;
import com.heaven7.android.components.demo.loadingimpl.AppLoadingComponentImpl;
import com.heaven7.android.pullrefresh.PullToRefreshLayout;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;
import com.heaven7.core.util.Toaster;
import com.heaven7.core.util.ViewHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

/**
 * test loading
 * Created by heaven7 on 2017/8/15 0015.
 */

public class TestLoadingComponentActivity extends BaseActivity{

    private static final String TAG = "PullToRefreshTestActivity";

    @BindView(R.id.pull_refresh)
    PullToRefreshLayout mPullView;

    private QuickRecycleViewAdapter<TestBean> mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.ac_test_loading_component;
    }

    @Override
    protected AppLoadingComponent onCreateAppLoadingComponent() {
        return new AppLoadingComponentImpl(mPullView);
    }
    @Override
    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {
        getAppLoadingComponent().setLayoutManager(new LinearLayoutManager(context));
        getAppLoadingComponent().setCallback(new AppLoadingComponent.Callback() {
            @Override
            public void onRefresh(AppLoadingComponent component) {
                Logger.i(TAG,"onRefresh","");
                loadData();
            }

            @Override
            public void onLoadMore(AppLoadingComponent component) {
                Logger.i(TAG,"onLoadMore","");
                component.showLoading(AppLoadingComponent.STATE_LOADING);
                loadMoreData();
            }

            @Override
            public void onClickLoadingView(AppLoadingComponent component, View loading, int state) {
                Logger.i(TAG,"onClickFooter","state = " + state);
            }
        });
        getAppLoadingComponent().setAdapter(mAdapter = new QuickRecycleViewAdapter<TestBean>(
                R.layout.item_test_pull_refresh, getTestList(0)
        ) {
            @Override
            protected void onBindData(Context context, int position, TestBean item, int itemLayoutId, ViewHelper helper) {
                helper.setText(R.id.tv1, item.text1)
                        .setText(R.id.tv2, item.text2);
            }
        });
    }

    private List<TestBean> getTestList(int count ) {
        if( count == 0){
            count = 20;
        }
        List<TestBean> list = new ArrayList<>();
        for(int i = 0 ;  i < count ; i++){
            list.add(new TestBean("PullRefreshView--->heaven7--->", i));
        }
        return list;
    }

    private void loadMoreData(){
        //layout.getFooterView().setState(LoadingFooterView.STATE_LOADING);
        MainWorker.postDelay(2000, new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                mAdapter.getAdapterManager().addItems(getTestList(r.nextInt(10) + 2));
                getAppLoadingComponent().setLoadingComplete();
                Toaster.show(getApplication(), "load more done");
            }
        });
    }

    private void loadData() {
        MainWorker.postDelay(2000, new Runnable() {
            @Override
            public void run() {
                Random r = new Random();
                mAdapter.getAdapterManager().replaceAllItems(getTestList(r.nextInt(10) + 20));
                getAppLoadingComponent().setLoadingComplete();
                Toaster.show(getApplication(), "refresh done");
            }
        });
    }


    static class TestBean extends BaseSelector {

        String text1;
        String text2;

        public TestBean(String text, int pos ) {
            this.text1 = text +"___pos_" + pos + "___1";
            this.text2 = text +"___pos_" + pos + "___2";
        }
    }
}
