package com.heaven7.android.component.network.list;

import androidx.core.util.Consumer;

import com.heaven7.android.component.network.NetworkContext;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * page manager
 *
 * @author heaven7
 * @since 1.1.6
 */
public final class PageManager {

    private int mPageNo = 0;
    private int mPageSize = 10;
    private final NetworkContext mContext;
    private boolean mAllLoadDone;
    private ParameterProcessor mParameterProcessor;

    public PageManager(NetworkContext mContext) {
        this.mContext = mContext;
    }
    public int getPageNo() {
        return mPageNo;
    }
    public void setPageNo(int mPageNo) {
        this.mPageNo = mPageNo;
    }
    public int getPageSize() {
        return mPageSize;
    }

    public void setPageSize(int mPageSize) {
        this.mPageSize = mPageSize;
    }
    public boolean isAllLoadDone(){
        return mAllLoadDone;
    }

    public void setParameterProcessor(ParameterProcessor pp){
        this.mParameterProcessor = pp;
    }

    //TypeToken<HttpResult<T>> tt
    @SuppressWarnings("unchecked")
    public <T> void get(String url, boolean refresh, Type type, Callback<T> callback) {
        HashMap<String, Object> map = getParameterMap(refresh);
        mContext.getNetworkComponent().ofGet(url, map)
                .jsonConsume(type,
                        data ->
                                callback.onResult(url, refresh, (T) data)
                ).error(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable){
                callback.onThrowable(url, refresh, throwable);
            }
        })
                .startRequest();
    }

    @SuppressWarnings("unchecked")
    public <T> void postBody(String url, boolean refresh, Type type, Callback<T> callback) {
        HashMap<String, Object> map = getParameterMap(refresh);
        mContext.getNetworkComponent().ofPostBody(url, map
        )
                .jsonConsume(type,
                        data ->
                                callback.onResult(url, refresh, (T) data)
                ).error(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable){
                callback.onThrowable(url, refresh, throwable);
            }
        })
                .startRequest();
    }
    @SuppressWarnings("unchecked")
    public <T> void post(String url, boolean refresh, Type type, Callback<T> callback) {
        HashMap<String, Object> map = getParameterMap(refresh);
        mContext.getNetworkComponent().ofPost(url, map
        )
                .jsonConsume(type,
                        data ->
                                callback.onResult(url, refresh, (T) data)
                ).error(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable){
                callback.onThrowable(url, refresh, throwable);
            }
        })
                .startRequest();
    }

    private HashMap<String, Object> getParameterMap(boolean refresh) {
        if (refresh) {
            mPageNo = 1;
            mAllLoadDone = false;
        } else {
            mPageNo += 1;
        }
        HashMap<String, Object> map = createRequestMap(mPageNo, mPageSize);
        if(mParameterProcessor != null){
            mParameterProcessor.addRequestParams(map);
        }
        return map;
    }

    public void setAllLoadDone(boolean allDone) {
        mAllLoadDone = allDone;
    }

    protected HashMap<String, Object> createRequestMap(int pageNo, int pageSize){
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("pageNo", pageNo);
        map.put("pageSize", pageSize);
        return map;
    }

    public interface Callback<T> {
        default void onThrowable(String url, boolean refresh, Throwable e) {
        }
        void onResult(String url, boolean refresh, T data);
    }

    /**
     * sometimes when want to add extra parameter to the list request.
     * this is used for that.
     */
    public interface ParameterProcessor {
        void addRequestParams(Map<String, Object> params);
    }
}
