package com.heaven7.android.component.network.list;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.heaven7.android.component.loading.AppLoadingComponent;
import com.heaven7.android.component.network.NetworkContext;
import com.heaven7.android.component.network.RequestConfig;
import com.heaven7.android.pullrefresh.FooterDelegate;
import com.heaven7.android.pullrefresh.PullToRefreshLayout;

import java.util.List;
import java.util.logging.Logger;

/**
 * the list callback
 * @param <T> the data type of http response.
 * @since 1.1.6
 */
public class ListHelper<T> implements AppLoadingComponent.Callback, PageManager.Callback<T> {

    private final NetworkContext mContext;
    private final Callback mCallback;
    private final Factory mFactory;
    private final EmptyRefreshDelegate mRefresh;

    private PageManager mPageM;
    private RequestConfig mConfig;
    private AppLoadingComponent mComponent;
    private IAdapterDelegate mAdapterDelegate;

    public ListHelper(NetworkContext mContext,Factory factory,EmptyRefreshDelegate delegate,Callback mCallback) {
        this.mContext = mContext;
        this.mCallback = mCallback;
        this.mFactory = factory;
        this.mRefresh = delegate;
    }
    public ListHelper(NetworkContext mContext,Factory factory, Callback mCallback) {
       this(mContext, factory, new SwipeRefreshDelegate(), mCallback);
    }

    public AppLoadingComponent getAppLoadingComponent() {
        return mComponent;
    }
    public RequestConfig getRequestConfig() {
        return mConfig;
    }
    public PageManager getPageManager() {
        return mPageM;
    }
    public IAdapterDelegate getAdapterDelegate() {
        return mAdapterDelegate;
    }

    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {
        mPageM = mFactory.onCreatePageManager(mContext);
        mPageM.setParameterProcessor(mCallback);
        mConfig = mCallback.onCreateRequestConfig();
        //reload
        mComponent = mFactory.onCreateAppLoadingComponent(mCallback.getPullToRefreshLayout());
        mComponent.getReloadView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mComponent.showContent(0);
                refresh();
            }
        });
        //empty
        final View emptyView = mComponent.getEmptyView();
        mRefresh.setOnRefreshListener(emptyView, new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                mRefresh.setRefreshing(emptyView, false);
                //mComponent.getEmptyLayout().setRefreshing(false);
                mComponent.showLoading(AppLoadingComponent.STATE_NORMAL);
                mComponent.showContent(0);
                refresh();
            }
        });
        //footer
        FooterDelegate delegate = mCallback.getFooterDelegate();
        if (delegate != null) {
            mCallback.getPullToRefreshLayout().setFooterDelegate(delegate);
        }
        //adapter delegate
        mAdapterDelegate = mFactory.onCreateAdapterDelegate(mComponent.getRecyclerView());

        mComponent.setLayoutManager(new LinearLayoutManager(context));
        mComponent.setCallback(this);
    }

    public void refresh(){
        if(mCallback.handleRefresh()){
            return;
        }
        mCallback.getPullToRefreshLayout().getSwipeRefreshLayout().setRefreshing(true);
        requestData(true);
    }

    public void requestData(boolean refresh) {
        if(refresh){
            final IAdapterDelegate ad = mAdapterDelegate;
            if(ad.getItemSize() > 0){
                ad.clearItems();
            }
        }
        switch (mConfig.method){
            case RequestConfig.TYPE_GET:
                mPageM.get(mConfig.url, refresh, mConfig.type, this);
                break;

            case RequestConfig.TYPE_POST_BODY:
                mPageM.postBody(mConfig.url, refresh, mConfig.type, this);
                break;

            case RequestConfig.TYPE_POST_FORM:
                mPageM.post(mConfig.url, refresh, mConfig.type, this);
                break;

            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    public void onResult(String url, boolean refresh, T data) {
        mCallback.onResult(url, refresh, data);
        mComponent.hideError();
        mComponent.setLoadingComplete();
        List<?> listData = mCallback.getListData(data);
        //empty
        if (listData.isEmpty() && mPageM.getPageNo() == 1) {
            if (!mCallback.showEmpty(this)) {
                mComponent.showEmpty(0);
            }
            return;
        }
        showContent(refresh, listData);
    }

    /**
     * show the list content
     * @param refresh true is the refresh
     * @param listData the data
     */
    public void showContent(boolean refresh, List<?> listData) {
        mComponent.showContent(0);
        if (refresh) {
            mAdapterDelegate.replaceAllItems(mCallback.map(listData));
        } else {
            mAdapterDelegate.addItems(mCallback.map(listData));
        }
        if (listData.size() < mPageM.getPageSize()) {
            mPageM.setAllLoadDone(true);
            mCallback.getPullToRefreshLayout().getFooterDelegate().setState(
                    mPageM.getPageNo() == 1 ? FooterDelegate.STATE_NORMAL : FooterDelegate.STATE_THE_END);
        } else {
            mCallback.getPullToRefreshLayout().getFooterDelegate().setState(FooterDelegate.STATE_NORMAL);
        }
    }

    @Override
    public void onThrowable(String url, boolean refresh, Throwable e) {
        e.printStackTrace();
        getAppLoadingComponent().showError(0);

        mAdapterDelegate.clearItems();
        mCallback.onThrowable(url, refresh, e);
    }

    @Override
    public void onRefresh(AppLoadingComponent component) {
        requestData(true);
    }

    @Override
    public void onLoadMore(AppLoadingComponent component) {
        if (!mPageM.isAllLoadDone()) {
            getAppLoadingComponent().showLoading(AppLoadingComponent.STATE_LOADING);
            requestData(false);
        }
    }
    @Override
    public void onClickLoadingView(AppLoadingComponent component, View view, int state) {

    }

    /**
     * the factory to create some
     */
    public abstract static class Factory{

        /**
         * called on create loading component
         * @param layout the pull-to-refresh layout
         * @return the loading component
         */
        public abstract AppLoadingComponent onCreateAppLoadingComponent(PullToRefreshLayout layout);

        /**
         * called on create adapter delegate
         * @param rv the recycler view
         * @return the adapter delegate
         */
        public abstract IAdapterDelegate onCreateAdapterDelegate(RecyclerView rv);

        /**
         * called on create page manager for multi pages.
         * @param context the network context
         * @return the page manager
         */
        public PageManager onCreatePageManager(NetworkContext context){
            return new PageManager(context);
        }
    }

    /**
     * the empty refresh delegate
     */
    public interface EmptyRefreshDelegate{

        /**
         * set on refresh listener for empty view
         * @param emptyView the empty view
         * @param l the refresh listener
         */
        void setOnRefreshListener(View emptyView, SwipeRefreshLayout.OnRefreshListener l);

        /**
         * set refresh or not.
         * @param emptyView the empty view
         * @param refreshing the refreshing or not
         */
        void setRefreshing(View emptyView, boolean refreshing);
    }

    /**
     * the callback of list request
     */
    public interface Callback extends PageManager.ParameterProcessor {

        /**
         * get the footer delegate
         * @return the footer delegate
         */
        FooterDelegate getFooterDelegate();

        /**
         * get the list data
         * @param data the data
         * @return the list data
         */
        default List<?> getListData(Object data){
            if(data instanceof List){
                return (List<?>) data;
            }else if(data instanceof ListDataOwner){
                return ((ListDataOwner)data).getListData();
            }
            throw new RuntimeException("you must override #getListData().");
        }

        /**
         * get the pull to refresh layout
         * @return the layout
         */
        PullToRefreshLayout getPullToRefreshLayout();
        /**
         * called on create request config
         *
         * @return the request config.
         */
        RequestConfig onCreateRequestConfig();

        /**
         * called on show empty. if return false show empty by default
         * @param helper the list helper
         * @return true if handled show empty
         */
        default boolean showEmpty(ListHelper helper){
            return false;
        }

        /**
         * called on transform/map list data to another list.
         * @param data the list data
         * @return the list data
         */
        default List<?> map(List<?> data){
            return data;
        }

        /**
         *  called when network error.
          * @param url the url
         * @param refresh true if used as refresh
         * @param e the exception
         */
        default void onThrowable(String url, boolean refresh, Throwable e) {
        }

        /**
         * called on every network result
         * @param url  the url
         * @param refresh true if refresh
         * @param data the response data
         */
        default void onResult(String url, boolean refresh, Object data){}

        /**
         * called on refresh . if handled return true. false otherwise. default is false
         * @return true if handled refresh
         */
        default boolean handleRefresh(){
            return false;
        }
    }

    public static class SwipeRefreshDelegate implements EmptyRefreshDelegate{
        @Override
        public void setOnRefreshListener(View emptyView, SwipeRefreshLayout.OnRefreshListener l) {
            SwipeRefreshLayout srl = (SwipeRefreshLayout) emptyView;
            srl.setOnRefreshListener(l);
        }
        @Override
        public void setRefreshing(View emptyView, boolean refreshing) {
            SwipeRefreshLayout srl = (SwipeRefreshLayout) emptyView;
            srl.setRefreshing(refreshing);
        }
    }
}
