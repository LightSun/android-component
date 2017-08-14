package com.heaven7.android.component.loading;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * this is a specification of android application loading component.
 * Created by heaven7 on 2017/7/10 0010.
 */
public interface AppLoadingComponent {

    /**
     * the loading state: normal.
     */
    byte STATE_NORMAL = 1;
    /**
     * the loading state: the end.
     */
    byte STATE_THE_END = 2;
    /**
     * the loading state: loading.
     */
    byte STATE_LOADING = 3;
    /**
     * the loading state: network error.
     */
    byte STATE_NETWORK_ERROR = 4;

    /**
     * the callback of loading component
     */
    interface Callback{

        /**
         * called on refresh.
         * @param component the app loading component
         */
        void onRefresh(AppLoadingComponent component);

        /**
         * called on load more.
         * @param component the app loading component
         */
        void onLoadMore(AppLoadingComponent component);

        /**
         * called on click loading view.
         * @param component the app loading component
         * @param loading the loading view . such as footer(maybe).
         * @param state current loading state. see {@linkplain AppLoadingComponent#STATE_LOADING} and etc.
         */
        void onClickLoadingView(AppLoadingComponent component, View loading, int state);
    }

    /**
     * the place holder performer.
     */
    interface PlaceholderViewPerformer{
        /**
         * called on perform the place holder view/
         * @param component the app loading component
         * @param placeholderView the placeholder view.
         * @param flag the code which comes from {@linkplain AppLoadingComponent#showPlaceholderView(int)}
         */
        void performPlaceholderView(AppLoadingComponent component, View placeholderView, int flag);
    }

    /**
     * the state performer of Loading.
     */
    interface LoadingStatePerformer {

        /**
         * called on perform ViewStub.
         * @param stateItem the state item view
         * @param state the state of footer view. see {@link AppLoadingComponent#STATE_LOADING} and etc.
         * @return the result view of perform ViewStub.
         */
        View performState(View stateItem, int state);
    }

    /**
     * get the loading view.
     * @return the loading view
     */
    View getLoadingView();

    /**
     * get the recycler view
     * @return the recycler view
     */
    RecyclerView getRecyclerView();

    /**
     * get the loading root view
     * @return the root view of loading.
     */
    ViewGroup getLoadingRootView();


    //=========================================================================

    /**
     * set placeholder view performer
     * @param performer the placeholder view performer
     */
    void setPlaceholderViewPerformer(PlaceholderViewPerformer performer);


    /**
     * set the loading state performer.
     * @param performer the loading state performer.
     */
    void setLoadingStatePerformer(LoadingStatePerformer performer);


    /**
     * set the layout manager of {@linkplain RecyclerView}.
     * @param lm the layout manager of {@linkplain RecyclerView}.
     */
    void setLayoutManager(RecyclerView.LayoutManager lm);


    /**
     * set the recycler view adapter.
     * @param adapter the adapter of recycler view.
     */
    void setAdapter(RecyclerView.Adapter adapter);

    //==========================================================

    /**
     * mark the loading complete.
     */
    void setLoadingComplete();

    /**
     * show placeholder view by target code/flag.
     * @param code the code indicate this operation
     */
    void showPlaceholderView(int code);

    /**
     * show the loading
     * @param state the loading state. see {@linkplain #STATE_LOADING} and etc.
     */
    void showLoading(int state);

    /**
     * show the content by target code.
     * @param code the code indicate this operation
      */
    void showContent(int code);

    /**
     * show tips by target code.
     * @param code the code indicate this operation
     */
    void showTips(int code);
    /**
     * show error by target code.
     * @param code the code indicate this operation
     */
    void showError(int code);
    /**
     * show empty by target code.
     * @param code the code indicate this operation
     */
    void showEmpty(int code);



}
