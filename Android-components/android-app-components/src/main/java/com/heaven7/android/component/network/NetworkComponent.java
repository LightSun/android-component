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

    public abstract void asyncRun(Runnable task);

    public abstract Applier ofGet(String url, HashMap<String, Object> params);
    public abstract Applier ofGet(String url, String json);
    // ofPost used for 'FormUrlEncoded'.
    public abstract Applier ofPost(String url, HashMap<String, Object> params);
    public abstract Applier ofPost(String url, String json);

    public abstract Applier ofPostBody(String url, HashMap<String, Object> params);
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
