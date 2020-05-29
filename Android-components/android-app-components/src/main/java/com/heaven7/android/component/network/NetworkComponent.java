package com.heaven7.android.component.network;

import android.content.Context;
import android.widget.Toast;

import androidx.core.util.Consumer;
import androidx.lifecycle.LifecycleOwner;

import com.heaven7.android.component.lifecycle.LifeCycleComponent;
import com.heaven7.android.component.lifecycle.LifeCycleComponent2;
import com.heaven7.java.base.util.Disposable;
import com.heaven7.java.base.util.Logger;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * the network component
 *
 * @author heaven7
 * @since 1.1.6
 */
public abstract class NetworkComponent implements LifeCycleComponent, LifeCycleComponent2 {

    private static final String TAG = "NetworkComponent";
    private final List<Disposable> mTasks = new CopyOnWriteArrayList<>();

    public void addTask(Disposable subscribe) {
        mTasks.add(subscribe);
    }

    public void removeTask(Disposable subscribe) {
        mTasks.remove(subscribe);
    }

    public void cancelAll() {
        for (Disposable task : mTasks) {
            task.dispose();
        }
        mTasks.clear();
    }

    @Override
    public void onLifeCycle(Context context, int lifeCycle) {
        if (lifeCycle == LifeCycleComponent.ON_DESTROY) {
            cancelAll();
        }
    }

    @Override
    public void onLifeCycle(LifecycleOwner context, int lifeCycle) {
        if (lifeCycle == LifeCycleComponent.ON_DESTROY) {
            cancelAll();
        }
    }

    /**
     * run the task async
     *
     * @param task the task
     */
    public abstract void asyncRun(Runnable task);

    /**
     * of get request Chain
     *
     * @param url    the url
     * @param params the parameter
     * @return applier
     */
    public abstract Chain ofGet(String url, HashMap<String, Object> params);

    /**
     * of get request Chain
     *
     * @param url  the url
     * @param json the parameter
     * @return applier
     */
    public abstract Chain ofGet(String url, String json);

    /**
     * of post request Chain. which often is used for 'FormUrlEncoded'
     *
     * @param url    the url
     * @param params the parameter
     * @return applier
     */
    public abstract Chain ofPost(String url, HashMap<String, Object> params);

    /**
     * of post request Chain. which often is used for 'FormUrlEncoded'
     *
     * @param url  the url
     * @param json the parameter
     * @return applier
     */
    public abstract Chain ofPost(String url, String json);

    /**
     * * of post request Chain. which often is used for body request.
     *
     * @param url    the url
     * @param params the paramter
     * @return applier
     */
    public abstract Chain ofPostBody(String url, HashMap<String, Object> params);

    /**
     * of post request Chain. which often is used for body request.
     *
     * @param url  the url
     * @param json the parameter of body
     * @return applier
     */
    public abstract Chain ofPostBody(String url, String json);

    /**
     * create upload-chain for target parameters
     * @param url the url
     * @param headers the header
     * @param formKey the formkey
     * @param files the files
     * @return chain
     */
    public abstract Chain ofUpload(String url, Map<String, String> headers, String formKey, List<String> files);

    /**
     * the chain for network request
     */
    public abstract static class Chain{

        private final String mUrl;
        private final NetworkComponent mComponent;

        private Runnable mMustTask;
        private Consumer<String> mConsumer;
        private Consumer<Throwable> mError;

        private Object mSrc;
        private volatile Disposable mReqTask;

        public Chain(String mUrl, NetworkComponent mComponent) {
            this.mUrl = mUrl;
            this.mComponent = mComponent;
        }
        public String getUrl() {
            return mUrl;
        }
        public NetworkComponent getNetworkComponent() {
            return mComponent;
        }
        public Object getSource() {
            return mSrc;
        }
        public Chain source(Object src){
            mSrc = src;
            return this;
        }
        /**
         * set a must task which will run when network come back.
         *
         * @param task the task
         * @return this
         */
        public Chain mustTask(Runnable task) {
            this.mMustTask = task;
            return this;
        }
        /**
         * set a consumer which will consume the network result.
         *  and default run must task on consumer result.
         * @param consume the consumer
         * @return this
         */
        public Chain consumer(Consumer<String> consume) {
            return consumer(consume, true);
        }
        /**
         * set a consumer which will consume the network result
         *
         * @param consume the consumer
         * @param runMust true to run the must task in consumer, false otherwise
         * @return this
         */
        public Chain consumer(Consumer<String> consume, boolean runMust) {
            this.mConsumer = new Consumer<String>() {
                @Override
                public void accept(String s) {
                    if(runMust){
                        runMustTask();
                    }
                    consume.accept(s);
                }
            };
            return this;
        }

        /**
         * set the error consumer
         * @param error the error consumer
         * @return the error consumer
         */
        public Chain error(Consumer<Throwable> error) {
            this.mError = new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) {
                    runMustTask();
                    error.accept(throwable);
                }
            };
            return this;
        }

        /**
         * set the error consumer by string error
         * @param error the error consumer
         * @return the error consumer
         */
        public Chain error(Context context, String error){
            return error(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                }
            });
        }

        /**
         * start request the network
         */
        public void startRequest(){
            if (mError == null) {
                error(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) {
                        t.printStackTrace();
                        System.err.println("Http/https network error. \n" + Logger.toString(t));
                    }
                });
            }
            mReqTask = startRequestImpl(mConsumer, mError);
            mComponent.addTask(mReqTask);
        }
        /**
         * callback error consumer
         * @param e the exception
         */
        protected void onError(Throwable e){
            mError.accept(e);
        }
        /**
         * cancel this chain-request
         */
        public void cancel(){
            if(mReqTask != null){
                mComponent.removeTask(mReqTask);
                mReqTask.dispose();
                mReqTask = null;
            }
        }
        /**
         * consume the result as json-protocol
         * @param type the type of response-body
         * @param consumer the consumer
         * @param <T> the result data type
         * @return this
         */
        public abstract <T> Chain jsonConsume(Type type, Consumer<T> consumer);

        /**
         * start request implements
         * @param expect the expect consumer
         * @param error the error consumer
         * @return the disposable task
         */
        protected abstract Disposable startRequestImpl(Consumer<String> expect, Consumer<Throwable> error);


        protected void runMustTask() {
            if (mMustTask != null) {
                mMustTask.run();
                mMustTask = null;
            }
        }
    }
}
