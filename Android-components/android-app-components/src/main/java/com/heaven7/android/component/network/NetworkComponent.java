package com.heaven7.android.component.network;

import android.content.Context;

import androidx.core.util.Consumer;
import androidx.lifecycle.LifecycleOwner;

import com.heaven7.android.component.lifecycle.LifeCycleComponent;
import com.heaven7.android.component.lifecycle.LifeCycleComponent2;
import com.heaven7.java.base.util.Disposable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * the network component
 * @author heaven7
 * @since 1.1.6
 */
public abstract class NetworkComponent implements LifeCycleComponent, LifeCycleComponent2 {

    private final List<Disposable> mTasks = new CopyOnWriteArrayList<>();

    public void addTask(Disposable subscribe) {
        mTasks.add(subscribe);
    }

    public void removeTask(Disposable subscribe){
        mTasks.remove(subscribe);
    }

    public void cancelAll(){
        for (Disposable task : mTasks){
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
     * @param task the task
     */
    public abstract void asyncRun(Runnable task);

    /**
     * of get request Applier
     * @param url the url
     * @param params the parameter
     * @return applier
     */
    public abstract Applier ofGet(String url, HashMap<String, Object> params);
    public abstract Applier ofGet(String url, String json);
    /**
     * of post request Applier. which often is used for 'FormUrlEncoded'
     * @param url the url
     * @param params the parameter
     * @return applier
     */
    public abstract Applier ofPost(String url, HashMap<String, Object> params);

    /**
     * of post request Applier. which often is used for 'FormUrlEncoded'
     * @param url the url
     * @param json the parameter
     * @return applier
     */
    public abstract Applier ofPost(String url, String json);

    public abstract Applier ofPostBody(String url, HashMap<String, Object> params);
    /**
     * of post request Applier. which often is used for body request
     * @param url the url
     * @param json the parameter of body
     * @return applier
     */
    public abstract Applier ofPostBody(String url, String json);

    public interface Applier{
        Applier mustTask(Runnable task);
        Applier consume(Consumer<String> consume);
        <T> Applier jsonConsume(Type type, Consumer<T> consumer);
        Applier error(Consumer<Throwable> error);
        Applier error(Context context, String error);
        void subscribe();
    }
}
