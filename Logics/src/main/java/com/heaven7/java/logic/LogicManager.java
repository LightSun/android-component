package com.heaven7.java.logic;


import com.heaven7.java.base.util.SparseArray;
import com.heaven7.java.base.util.Throwables;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * the logic manager help we handle an order logic tasks.
 * it can run a sequence/parallel tasks, no matter sync or async.
 * @author heaven7
 */
public final class LogicManager extends ContextDataImpl {

    private static final String TAG = "LogicManager";
    private final SparseArray<LogicTask> mLogicMap;

    /*
          type    secondary     index
     * 0x ff      ff            ffff
     */
    private static final int MASK_MAIN      = 0x0000ffff;
    private static final int MASK_SECONDARY = 0x00ff0000;
    private static final int MASK_TYPE      = 0xff000000;

    private static final int SHIFT_TYPE      = 24;
    private static final int SHIFT_SECONDARY = 16;
    private static final int TYPE_PARALLEL   = 1;

    private static final int MAX_PARALLEL_COUNT  = 0xff;
    private final AtomicInteger mIndex = new AtomicInteger();

    public LogicManager() {
        this.mLogicMap = new SparseArray<LogicTask>(3);
    }

    /**
     * cancel the all task which is running.
     */
    public void cancelAll(){
    	 synchronized (mLogicMap) {
             final int size = mLogicMap.size();
             for (int i = 0; i < size; i++) {
                 mLogicMap.valueAt(i).cancel();
             }
             mLogicMap.clear();
         }
    }
    /**
     * cancel the task which is assigned by target key.
     * @param key the key . see {@linkplain #executeParallel(List, Runnable)} or {@linkplain #executeSequence(List, Runnable)}.
     */
    public void cancel(int key) {
    	cancelByKey(key);
    }

    /**
     * indicate the target key of tasks is running or not.
     * @param key the key of last operation
     * @return true if is running.
     */
    public boolean isRunning(int key){
        final int type = (key & MASK_TYPE) >> 24;
        switch (type){
            case TYPE_PARALLEL:
                int baseKey = key & MASK_MAIN ;
                int count = (key & MASK_SECONDARY) >> SHIFT_SECONDARY;
                synchronized (mLogicMap) {
                    for (int i = 0; i < count; i++) {
                        if(mLogicMap.get(baseKey + i) != null){
                            return true;
                        }
                    }
                }
                break;

            case 0:
                synchronized (mLogicMap){
                    return mLogicMap.get(key) != null;
                }
        }
        return false;
    }

    /**
     * indicate the target task is running or not.
     * @param task the logic task.
     * @return true if is running.
     */
    public boolean isRunning(LogicTask task){
        synchronized (mLogicMap){
            return mLogicMap.indexOfValue(task, false) >= 0;
        }
    }

    /**
     * executeSequence the tasks in Parallel.
     * @param parallels the all tasks.
     * @param endAction the end action , called when all task done.
     * @return the key of this operation.
     */
    public int executeParallel(LogicTask[] parallels, Runnable endAction) {
        return executeParallel(Arrays.asList(parallels), endAction);
    }

    /**
     * executeSequence the tasks in Parallel.
     * @param parallels the all tasks.
     * @param endAction the end action , called when all task done.
     * @return the key of this operation.
     */
    public int executeParallel(List<LogicTask> parallels, Runnable endAction) {
        Throwables.checkEmpty(parallels);
        final int size = parallels.size();
        if(size > MAX_PARALLEL_COUNT){
            throw new UnsupportedOperationException("max parallel count must below " + MAX_PARALLEL_COUNT);
        }
        final int baseKey =  mIndex.incrementAndGet();
        mIndex.addAndGet(size);
        
        //put to logic pool
        synchronized (mLogicMap) {
            for(int i = 0 ; i < size ; i++){
                mLogicMap.put(baseKey + i, parallels.get(i));
            }
        }
        //add callback and perform
        final ParallelCallback callback = new ParallelCallback(parallels, endAction);
        final Object data = getContextData();
        for(LogicTask task : parallels){
            task.setContextData(data);
            task.addStateCallback(callback);
            task.perform();
        }
        return baseKey + (size << SHIFT_SECONDARY) + (TYPE_PARALLEL << SHIFT_TYPE);
    }

    /**
     * execute the tasks in sequence with the end action.
     * @param tag the tag
     * @param   logicAction state performer
     * @param lp the logic parameter
     * @param endAction the end action, called when perform the all target logic tasks done. can be null.
     * @return the key of this operation.
     */
    public int executeSequence(int tag, LogicAction logicAction, LogicParam lp, Runnable endAction) {
        return executeSequence(new LogicTask[] {LogicTask.of(tag, logicAction, lp)}, endAction);
    }

    /**
     *execute the tasks in sequence with the end action.
     *
     * @param task     the logic task
     * @param endAction the end action, called when perform the all target logic tasks done. can be null.
     * @return the key of this operation.
     */
    public int executeSequence(LogicTask task, Runnable endAction) {
        return executeSequence(new LogicTask[]{task}, endAction);
    }
    /**
     *execute the tasks in sequence with the end action.
     *
     * @param task1     the logic task1
     * @param task2     the logic task2
     * @param endAction the end action, called when perform the all target logic tasks done. can be null.
     * @return the key of this operation.
     */
    public int executeSequence(LogicTask task1, LogicTask task2, Runnable endAction) {
        return executeSequence(new LogicTask[]{task1, task2}, endAction);
    }
    /**
     *execute the tasks in sequence with the end action.
     *
     * @param task1     the logic task1
     * @param task2     the logic task2
     * @param task3     the logic task3
     * @param endAction the end action, called when perform the all target logic tasks done. can be null.
     * @return the key of this operation.
     */
    public int executeSequence(LogicTask task1, LogicTask task2, LogicTask task3, Runnable endAction) {
        return executeSequence(new LogicTask[]{task1, task2, task3}, endAction);
    }

    /**
     *execute the tasks in sequence with the end action.
     *
     * @param tasks     the logic tasks
     * @param endAction the end action, called when perform the all target logic tasks done. can be null.
     * @return the key of this operation.
     */
    public int executeSequence(LogicTask[] tasks, Runnable endAction) {
        return executeSequence(Arrays.asList(tasks), endAction);
    }
    /**
     * execute the tasks in sequence with the end action.
     *
     * @param tasks     the logic tasks, you can't modify this list outside. or else you may cause bug.
     * @param endAction the end action, called when perform the all target logic tasks done. can be null.
     * @return the key of this operation.
     */
    public int executeSequence(List<LogicTask> tasks, Runnable endAction) {
        Throwables.checkNull(tasks);
        if (tasks.size() == 0) {
            throw new IllegalArgumentException("must assign a logic action");
        }
        final int key = mIndex.incrementAndGet();

        performSequenceImpl(tasks, key, 0, endAction);
        return key;
    }


    private void performSequenceImpl(List<LogicTask> tasks, int key, int currentIndex, Runnable endAction) {
        final LogicTask target = tasks.get(currentIndex);
        target.setContextData(getContextData());
        target.addStateCallback(new SequenceCallback(tasks, key, currentIndex, endAction));
        target.perform();
    }

    //this key is blur
    private void cancelByKey(int key) {
        final int type = (key & MASK_TYPE) >> 24;
        switch (type){
            case TYPE_PARALLEL:
                int baseKey = key & MASK_MAIN ;
                int count = (key & MASK_SECONDARY) >> SHIFT_SECONDARY;
                for(int i = 0 ; i < count ; i++){
                    cancelByRealKey(baseKey + i);
                }
                break;

            case 0:
                cancelByRealKey(key);
        }
    }

    private void cancelByRealKey(int realKey){
        LogicTask task;
        synchronized (mLogicMap) {
            task = mLogicMap.getAndRemove(realKey);
        }
        if(task != null){
            task.cancel();
        }else{
            System.err.println(TAG + " >>> called [ cancelByRealKey() ]" +
                    "cancel task .but key not exists , key = " + realKey);
           // Logger.w(TAG,"cancelByRealKey","cancel task .but key not exists , key = " + realKey);
        }
    }

    private class ParallelCallback extends SimpleLogicCallback{

       /* final int key;*/
        final List<LogicTask> parallelTasks;
        final Runnable endAction;
        final AtomicInteger finishCount;

        public ParallelCallback(List<LogicTask> parallelTasks, Runnable endAction) {
            this.parallelTasks = parallelTasks;
            this.endAction = endAction;
            this.finishCount = new AtomicInteger(0);
        }

        private int getTaskCount(){
            return parallelTasks.size();
        }

        @Override
        public void onLogicStart(LogicAction action, int tag, LogicParam param) {
        	//if put parallel task in here. can cause the last Logic task may not be cancelled.
            /*int count = getTaskCount();
            synchronized (mLogicMap) {
                for(int i = 0 ; i < count ; i++){
                    mLogicMap.put(key + i, parallelTasks.get(i));
                }
            }*/
        }
        @Override
        protected void onSuccess(LogicAction action, int tag, LogicParam param) {
            removeTask(LogicTask.of(tag, action, param));
            int count = finishCount.incrementAndGet();
            if(count == getTaskCount() && endAction != null){
                endAction.run();
                //System.out.println("task ok. " + param);
            }
        }

        @Override
        protected void onFailed(LogicAction action, int tag, LogicParam param) {
            removeTask(LogicTask.of(tag, action, param));
        }

        private void removeTask(LogicTask task){
            synchronized (mLogicMap) {
                int index = mLogicMap.indexOfValue(task, false);
                if(index >= 0){
                    mLogicMap.removeAt(index);
                   // Logger.i(TAG,"removeTask","task is removed . task = " + task);
                }
            }
            task.removeStateCallback(this);
        }
    }

    private class SequenceCallback extends SimpleLogicCallback {

        final int key;
        final int curIndex;
        final List<LogicTask> mTasks;
        final Runnable endAction;

        public SequenceCallback(List<LogicTask> tasks, int key, int currentIndex, Runnable endAction) {
            this.key = key;
            this.curIndex = currentIndex;
            this.mTasks = tasks;
            this.endAction = endAction;
        }

        @Override
        public void onLogicStart(LogicAction action, int tag, LogicParam param) {
            synchronized (mLogicMap) {
                mLogicMap.put(key, mTasks.get(curIndex));
            }
        }

        @Override
        protected void onSuccess(LogicAction action, int tag, LogicParam param) {
            removeTask();
            if (curIndex == mTasks.size() - 1) {
                //all end
                if(endAction != null) {
                    endAction.run();
                }
            } else {
                //perform next
                performSequenceImpl(mTasks, key, curIndex + 1, endAction);
            }
        }
        @Override
        protected void onFailed(LogicAction action, int tag, LogicParam param) {
            removeTask();
        }
        private void removeTask(){
            synchronized (mLogicMap) {
                mLogicMap.remove(key);
            }
            //unregister.
            mTasks.get(curIndex).removeStateCallback(this);
        }
    }

}
