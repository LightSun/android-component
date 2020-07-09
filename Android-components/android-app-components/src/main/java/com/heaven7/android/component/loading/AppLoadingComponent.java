package com.heaven7.android.component.loading;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.heaven7.android.component.AppComponentContext;

/**
 * this is a specification of android application loading component.
 * <p>here is a demo(from sample code). </p>
 * <pre><code>
public class TestLoadingComponentActivity extends BaseActivity{

     private static final String TAG = "PullToRefreshTestActivity";

     //@BindView(R.id.pull_refresh)
     PullToRefreshLayout mPullView;

     private QuickRecycleViewAdapter<TestBean> mAdapter;
     private AppLoadingComponent mAIC;

     //@Override
     public int getLayoutId() {
          return R.layout.ac_test_loading_component;
     }

     //@Override
     public AppLoadingComponent getAppLoadingComponent() {
         if(mAIC == null){
             mAIC = new AppLoadingComponentImpl(mPullView);
         }
         return mAIC;
     }

     //@Override
     public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {
         getAppLoadingComponent().setLayoutManager(new LinearLayoutManager(context));
         getAppLoadingComponent().setCallback(new AppLoadingComponent.Callback() {
             //@Override
             public void onRefresh(AppLoadingComponent component) {
             Logger.i(TAG,"onRefresh","");
             loadData();
             }

             //@Override
             public void onLoadMore(AppLoadingComponent component) {
                 Logger.i(TAG,"onLoadMore","");
                 component.showLoading(AppLoadingComponent.STATE_LOADING);
                 loadMoreData();
             }

             //@Override
             public void onClickLoadingView(AppLoadingComponent component, View loading, int state) {
                  Logger.i(TAG,"onClickFooter","state = " + state);
             }
         });
     getAppLoadingComponent().setAdapter(mAdapter = new QuickRecycleViewAdapter<TestBean>(
         R.layout.item_test_pull_refresh, getTestList(0)
         ) {
         //@Override
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
         MainWorker.postDelay(2000, new Runnable() {
            // @Override
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
            // @Override
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

  * </code></pre>
 * Created by heaven7 on 2017/7/10 0010.
 */
public interface AppLoadingComponent extends AppComponentContext{

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
     * the code which indicate no network
     * @since 1.1.7
     */
    byte CODE_NO_NETWORK = 1;
    /**
     * the code which indicate exception
     * @since 1.1.7
     */
    byte CODE_EXCEPTION  = 2;

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

    /**
     * set the loading callback.
     * @param callback the loading callback.
     */
    void setCallback(Callback callback);

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
     * get error delegate
     * @return the error delegate
     * @since 1.1.7
     */
    ViewDelegate getErrorDelegate();
    /**
     * get empty delegate
     * @return the error delegate
     * @since 1.1.7
     */
    ViewDelegate getEmptyDelegate();

    /**
     * the view delegate
     * @since 1.1.7
     */
    interface ViewDelegate{
        /**
         * show this view
         * @param code the code
         * @param msg the message
         * @param e the throwable if exception
         */
        void show(int code, String msg, Throwable e);

        /**
         * hide this view
         */
        void hide();

        /**
         * reset to initialize state
         */
        void reset();

        /**
         * get the refresh view, can be null
         * @return the view
         */
        View getRefreshView();

        /**
         * get the reload view can be null.
         * @return the reload view
         */
        View getReloadView();

        /**
         * get whole view
         * @return the view
         */
        View getView();
    }
}
