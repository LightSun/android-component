package com.heaven7.java.logic;

import com.heaven7.java.base.util.SparseArray;
import com.heaven7.java.base.util.Throwables;

/**
 * a class indicate delay action
 * Created by heaven7 on 2017/6/19 0019.
 */
public abstract class DelayLogicAction extends AbstractLogicAction {

    private final SparseArray<Runnable> mDelayMap;
    private final long mDelayTime;
    private final Callback mCallback;

    public DelayLogicAction(Callback callback, boolean wantCount, long delayTime) {
        super(wantCount);
        Throwables.checkNull(callback);
        this.mCallback = callback;
        this.mDelayMap = new SparseArray<Runnable>(3);
        this.mDelayTime = delayTime;
    }

    @Override
    public void perform(final int tag, final LogicParam param) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                mDelayMap.remove(tag);
                callSuperPerform(tag, param);
            }
        };
        mDelayMap.put(tag, r);
        mCallback.postDelay(mDelayTime, r);
    }

    protected void callSuperPerform(int tag, LogicParam param){
        super.perform(tag, param);
    }

    @Override
    protected void cancelImpl(int tag, boolean immediately) {
        final Runnable r = mDelayMap.get(tag);
        if(r != null) {
            mCallback.remove(r);
            mDelayMap.remove(tag);
        }
    }

    /**
     * a callback help handle the delay task.
     */
    public interface Callback{
        /**
         * post the runnable by target delay.
         * @param delay the delay in mills
         * @param task the runnable task,
         */
        void postDelay(long delay, Runnable task);

        /**
         * remove the task from message pool.
         * @param task the task.
         */
        void remove(Runnable task);
    }
}
