package com.heaven7.android.components.demo.loadingimpl;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.android.component.loading.AppLoadingComponent;
import com.heaven7.android.pullrefresh.LoadingFooterView;
import com.heaven7.android.pullrefresh.PullToRefreshLayout;

/**
 * Created by heaven7 on 2017/8/15 0015.
 */

public class AppLoadingComponentImpl implements AppLoadingComponent{

    private final PullToRefreshLayout mPullLayout;

    public AppLoadingComponentImpl(PullToRefreshLayout mPullLayout) {
        this.mPullLayout = mPullLayout;
    }

    @Override
    public View getLoadingView() {
        return mPullLayout.getFooterView();
    }

    @Override
    public RecyclerView getRecyclerView() {
        return mPullLayout.getRecyclerView();
    }

    @Override
    public ViewGroup getLoadingRootView() {
        return mPullLayout;
    }

    @Override
    public void setPlaceholderViewPerformer(final PlaceholderViewPerformer performer) {
        mPullLayout.setPlaceHolderViewPerformer(new PullToRefreshLayout.PlaceHolderViewPerformer() {
            @Override
            public void performPlaceHolderView(PullToRefreshLayout layout, LinearLayout placeHolderView, int flag) {
                performer.performPlaceholderView(AppLoadingComponentImpl.this, placeHolderView, flag);
            }
        });
    }

    @Override
    public void setLoadingStatePerformer(final LoadingStatePerformer performer) {
        mPullLayout.setStatePerformer(new LoadingFooterView.StatePerformer() {
            @Override
            public View performViewStub(ViewStub vs, int state) {
                return performer.performState(vs, state);
            }
        });
    }

    @Override
    public void setLayoutManager(RecyclerView.LayoutManager lm) {
        mPullLayout.setLayoutManager(lm);
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        if(!(adapter instanceof QuickRecycleViewAdapter)){
            throw new IllegalArgumentException("adapter must extends QuickRecycleViewAdapter");
        }
        mPullLayout.setAdapter((QuickRecycleViewAdapter<?>) adapter);
    }

    @Override
    public void setCallback(final Callback callback) {
        mPullLayout.setCallback(new PullToRefreshLayout.Callback() {
            @Override
            public void onRefresh(PullToRefreshLayout layout) {
                callback.onRefresh(AppLoadingComponentImpl.this);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout layout) {
                callback.onLoadMore(AppLoadingComponentImpl.this);
            }
            @Override
            public void onClickFooter(PullToRefreshLayout layout, LoadingFooterView footer, int state) {
                callback.onClickLoadingView(AppLoadingComponentImpl.this, footer, state);
            }
        });
    }

    @Override
    public void setLoadingComplete() {
        mPullLayout.setLoadingComplete();
    }

    @Override
    public void showPlaceholderView(int code) {
        mPullLayout.showPlaceHolderView(code);
    }

    @Override
    public void showContent(int code) {
        mPullLayout.showContentView();
    }

    @Override
    public void showLoading(int state) {
        switch (state){
            case STATE_LOADING:
                mPullLayout.getFooterView().setState(LoadingFooterView.STATE_LOADING);
                break;
            case STATE_NORMAL:
                mPullLayout.getFooterView().setState(LoadingFooterView.STATE_NORMAL);
                break;
            case STATE_NETWORK_ERROR:
                mPullLayout.getFooterView().setState(LoadingFooterView.STATE_NET_ERROR);
                break;
            case STATE_THE_END:
                mPullLayout.getFooterView().setState(LoadingFooterView.STATE_THE_END);
                break;

            default:
                throw new UnsupportedOperationException("wrong state = " + state);
        }
    }

    @Override
    public void showTips(int code) {
        //here not impl. if you want please implement
    }

    @Override
    public void showError(int code) {
        //here not impl. if you want please implement
    }

    @Override
    public void showEmpty(int code) {
        //here not impl. if you want please implement
    }
}
