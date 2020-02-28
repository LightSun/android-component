package com.heaven7.android.component;

import androidx.annotation.CallSuper;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.heaven7.java.base.util.SmartReference;

import java.util.ArrayList;
import java.util.Iterator;

import static com.heaven7.android.component.lifecycle.LifeCycleComponent.ON_CREATE;
import static com.heaven7.android.component.lifecycle.LifeCycleComponent.ON_DESTROY;
import static com.heaven7.android.component.lifecycle.LifeCycleComponent.ON_PAUSE;
import static com.heaven7.android.component.lifecycle.LifeCycleComponent.ON_RESUME;
import static com.heaven7.android.component.lifecycle.LifeCycleComponent.ON_START;
import static com.heaven7.android.component.lifecycle.LifeCycleComponent.ON_STOP;

/**
 * the abstract life cycle owner.
 * @param <T> the life cycle component. see {@linkplain com.heaven7.android.component.lifecycle.LifeCycleComponent} and
 *           {@linkplain com.heaven7.android.component.lifecycle.LifeCycleComponent2}
 * @since 1.1.3
 */
public abstract class AbstractLifeCycleComponentOwner<T> implements LifecycleObserver {

    protected final ArrayList<SmartReference0<T>> mWeakLives = new ArrayList<>();
    private final LifecycleOwner mOwner;

    public AbstractLifeCycleComponentOwner(LifecycleOwner owner) {
        mOwner = owner;
        owner.getLifecycle().addObserver(this);
    }

    public LifecycleOwner getLifecycleOwner() {
        return mOwner;
    }

    /**
     * register the life cycle component
     * @param component   the life cycle component
     * @since 1.0.6
     */
    public final void registerLifeCycleComponent(T component) {
        mWeakLives.add(new SmartReference0<T>(component));
    }

    /**
     * unregister the life cycle component
     * @param component the life cycle component
     * @since 1.0.6
     */
    public final void unregisterLifeCycleComponent(T component) {
        findLifeCycleComponent(component, true);
    }

    /**
     * register the life cycle component as weakly..
     * @param component  the life cycle component which will be weak reference
     * @since 1.0.7
     */
    public final void registerLifeCycleComponentWeakly(T component) {
        final SmartReference0<T> srf = new SmartReference0<T>(component);
        srf.tryWeakReference();
        mWeakLives.add(srf);
    }

    /**
     * indicate has the life cycle component or not.
     * @param component the life cycle component
     * @return true if has the target life cycle component
     * @since 1.0.6
     */
    public final boolean hasLifeCycleComponent(T component){
        return findLifeCycleComponent(component, false) != null;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    @CallSuper
    public void onCreate() {
        performLifeCycle(ON_CREATE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    @CallSuper
    public void onStart() {
        performLifeCycle(ON_START);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @CallSuper
    public void onResume() {
        performLifeCycle(ON_RESUME);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @CallSuper
    public void onPause() {
        performLifeCycle(ON_PAUSE);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    @CallSuper
    public void onStop() {
        performLifeCycle(ON_STOP);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    @CallSuper
    public void onDestroy() {
        performLifeCycle(ON_DESTROY);
    }

    private T findLifeCycleComponent(T context, boolean remove) {
        T result = null;
        final Iterator<SmartReference0<T>> it = mWeakLives.iterator();
        for (; it.hasNext(); ) {
            final SmartReference0<T> srf = it.next();
            if (srf.isAlive()) {
                final T tem = srf.get();
                if (tem == context || tem.equals(context)) {
                    result = tem;
                    if(remove) {
                        it.remove();
                    }
                    break;
                }
            } else {
                it.remove();
            }
        }
        return result;
    }

    private void performLifeCycle(int liftCycle) {
        LifecycleOwner owner = getLifecycleOwner();
        //currently flag only use as single, future may be multi
        final Iterator<SmartReference0<T>> it = mWeakLives.iterator();
        for (; it.hasNext(); ) {
            SmartReference<T> item = it.next();
            if (item.isAlive()) {
                onLifeCycle(owner, item.get(), liftCycle);
            } else {
                it.remove();
            }
        }
    }

    protected abstract void onLifeCycle(LifecycleOwner owner, T item, int liftCycle);

}
