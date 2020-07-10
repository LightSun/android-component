package com.heaven7.android.components.demo.loadingimpl;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.android.component.loading.AppLoadingComponent;
import com.heaven7.android.components.demo.R;
import com.heaven7.android.pullrefresh.LoadingFooterView;
import com.heaven7.android.pullrefresh.PullToRefreshLayout;

/**
 * Created by heaven7 on 2017/8/15 0015.
 */

public class AppLoadingComponentImpl implements AppLoadingComponent{

    private final PullToRefreshLayout mPullLayout;

    private final ErrorDelegateImpl mErrorDelegate;
    private final EmptyDelegateImpl mEmptyDelegate;

    public AppLoadingComponentImpl(PullToRefreshLayout mPullLayout) {
        this.mPullLayout = mPullLayout;

        this.mErrorDelegate = new ErrorDelegateImpl(mPullLayout.getWholeOverlapView());
        this.mEmptyDelegate = new EmptyDelegateImpl(mPullLayout.getWholeOverlapView());
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
        getEmptyDelegate().hide();
        getErrorDelegate().hide();
    }

    @Override
    public void showLoading(int state) {
        switch (state){
            case STATE_LOADING:
                mPullLayout.getFooterDelegate().setState(LoadingFooterView.STATE_LOADING);
                break;
            case STATE_NORMAL:
                mPullLayout.getFooterDelegate().setState(LoadingFooterView.STATE_NORMAL);
                break;
            case STATE_NETWORK_ERROR:
                mPullLayout.getFooterDelegate().setState(LoadingFooterView.STATE_NET_ERROR);
                break;
            case STATE_THE_END:
                mPullLayout.getFooterDelegate().setState(LoadingFooterView.STATE_THE_END);
                break;

            default:
                throw new UnsupportedOperationException("wrong state = " + state);
        }
    }

    @Override
    public ViewDelegate getErrorDelegate() {
        return mErrorDelegate;
    }
    @Override
    public ViewDelegate getEmptyDelegate() {
        return mEmptyDelegate;
    }

    private static class ErrorDelegateImpl implements ViewDelegate{

        private final View mTv_Reload;
        private final TextView mTv_notice;
        private final ImageView mIv_notice;
        private final View mView;

        private CharSequence mInitNotice;

        public ErrorDelegateImpl(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_net_error, parent, false);
            mView = view;
            mTv_Reload = view.findViewById(R.id.tv_reload);
            mTv_notice = view.findViewById(R.id.tv_notice);
            mIv_notice = view.findViewById(R.id.iv_notice);
            mInitNotice = mTv_notice.getText();
        }
        @Override
        public void show(int code, String msg, Throwable e) {
            if(e != null){
                msg = e.getMessage();
            }
            if(msg != null){
                mTv_notice.setText(msg);
            }
        }
        @Override
        public void hide() {
            ViewParent parent = mView.getParent();
            if(parent instanceof ViewGroup){
                ((ViewGroup) parent).removeView(mView);
            }
        }
        @Override
        public void reset() {
            mTv_notice.setText(mInitNotice);
        }
        @Override
        public View getRefreshView() {
            return null;
        }
        @Override
        public View getReloadView() {
            return mTv_Reload;
        }

        @Override
        public View getView() {
            return mView;
        }

        @Override
        public ImageView getImageView() {
            return mIv_notice;
        }
    }

    private static class EmptyDelegateImpl implements ViewDelegate{

        private final View mTv_Reload;
        private final TextView mTv_notice;
        private final ImageView mIv_notice;
        private final View mView;

        private CharSequence mInitNotice;

        public EmptyDelegateImpl(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.include_empty_data, parent, false);
            mView = view;
            mTv_Reload = view.findViewById(R.id.tv_reload);
            mTv_notice = view.findViewById(R.id.tv_notice);
            mIv_notice = view.findViewById(R.id.iv_notice);
            mInitNotice = mTv_notice.getText();
        }
        @Override
        public void show(int code, String msg, Throwable e) {
            if(e != null){
                msg = e.getMessage();
            }
            if(msg != null){
                mTv_notice.setText(msg);
            }
        }
        @Override
        public void hide() {
            ViewParent parent = mView.getParent();
            if(parent instanceof ViewGroup){
                ((ViewGroup) parent).removeView(mView);
            }
        }
        @Override
        public void reset() {
            mTv_notice.setText(mInitNotice);
        }
        @Override
        public View getRefreshView() {
            return mView;
        }
        @Override
        public View getReloadView() {
            return mTv_Reload;
        }

        @Override
        public View getView() {
            return mView;
        }
        @Override
        public ImageView getImageView() {
            return mIv_notice;
        }
    }
}
