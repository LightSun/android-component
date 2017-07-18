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
    private final Scheduler mScheduler;

    public DelayLogicAction(Scheduler scheduler, boolean wantCount, long delayTime) {
        super(wantCount);
        Throwables.checkNull(scheduler);
        this.mScheduler = scheduler;
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
        mScheduler.postDelay(mDelayTime, r);
    }

    protected void callSuperPerform(int tag, LogicParam param){
        super.perform(tag, param);
    }

    @Override
    protected void cancelImpl(int tag, boolean immediately) {
        final Runnable r = mDelayMap.get(tag);
        if(r != null) {
        	mScheduler.remove(r);
            mDelayMap.remove(tag);
        }
    }

}
