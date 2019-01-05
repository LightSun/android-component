package com.heaven7.java.logic;

import com.heaven7.java.base.anno.Deprecated;
import com.heaven7.java.base.anno.Nullable;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * the logic task group.
 * @author heaven7
 * @since 1.1.0
 */
public class LogicTaskGroup extends LogicTask{

    private LogicTaskGroup(LogicAction action, LogicParam logicParam) {
        super(action, logicParam);
    }

    /**
     * create logic task group.
     * @param tasks the logic tasks
     * @param performFlags the perform flags. eg: {@linkplain LogicManager#FLAG_SHARE_TO_NEXT} and {@linkplain LogicManager#FLAG_SHARE_TO_POOL}.
     * @param sequence true to perform sequence , false to perform Parallel.
     * @return the tasks group
     */
    public static LogicTaskGroup of(List<LogicTask> tasks, int performFlags, boolean sequence){
        return of(tasks, performFlags, sequence, null);
    }
    /**
     * create logic task group.
     * @param tasks the logic tasks
     * @param performFlags the perform flags. eg: {@linkplain LogicManager#FLAG_SHARE_TO_NEXT} and {@linkplain LogicManager#FLAG_SHARE_TO_POOL}. etc.
     * @param sequence true to perform sequence , false to perform Parallel.
     * @param l the logic running info listener
     * @return the tasks group
     * @since 1.1.4
     */
    public static LogicTaskGroup of(List<LogicTask> tasks, int performFlags, boolean sequence,
                                    @Nullable LogicRunningInfoListener l){
        GroupAction action = new GroupAction();
        action.sequence = sequence;
        action.mLogicFlags = performFlags;
        action.mTasks = tasks;
        action.runningListener = l;
        return new LogicTaskGroup(action, null);
    }

    /**
     * create logic task group.
     * @param lm the logic manager
     * @param tasks the logic tasks
     * @param performFlags the perform flags. eg: {@linkplain LogicManager#FLAG_SHARE_TO_NEXT} and {@linkplain LogicManager#FLAG_SHARE_TO_POOL}.
     * @param sequence true to perform sequence , false to perform Parallel.
     * @return the tasks group
     */
    @java.lang.Deprecated
    @Deprecated("the logic manager is removed from here, and just use internal.")
    public static LogicTaskGroup of(LogicManager lm, List<LogicTask> tasks, int performFlags, boolean sequence){
        return of(tasks, performFlags, sequence);
    }
    public static class GroupData{
        private final Object lastResult;
        private final List<?> results;

        GroupData(Object lastResult, List<?> results) {
            this.lastResult = lastResult;
            this.results = results;
        }
        public Object getLastResult() {
            return lastResult;
        }
        public List<?> getResults() {
            return results;
        }
        @Override
        public String toString() {
            return "GroupData{" +
                    "lastResult=" + lastResult +
                    ", results=" + results +
                    '}';
        }
    }

    private static class GroupAction extends SimpleLogicAction implements LogicResultListener, LogicRunningInfoListener{
        WeakReference<LogicManager> mWeakLM;
        boolean sequence;
        int mLogicFlags;
        List<LogicTask> mTasks;
        LogicRunningInfoListener runningListener;

        int key;

        @Override
        protected void cancelImpl(int tag) {
            LogicManager lm = mWeakLM.get();
            if(lm != null && key != 0){
                lm.cancel(key);
                key = 0;
            }
        }
        @Override
        protected void performImpl(LogicManager lm, int tag, int count, LogicParam param) {
            mWeakLM = new WeakReference<>(lm);
            if(sequence){
                key = lm.performSequence(mTasks, mLogicFlags, this);
            }else{
                key = lm.performParallel(mTasks, mLogicFlags, this);
            }
        }
        @Override
        public void onFailed(LogicManager lm, List<LogicTask> failedTask, Object lastResult, List<?> results) {
            key = 0;
            dispatchResult(0, new LogicResult(LogicAction.RESULT_FAILED, new GroupData(lastResult, results)));
        }
        @Override
        public void onSuccess(LogicManager lm, LogicTask lastTask, Object lastResult, List<?> results) {
            key = 0;
            dispatchResult(0, new LogicResult(LogicAction.RESULT_SUCCESS, new GroupData(lastResult, results)));
        }
        @Override
        public void onRunningProcessChanged(LogicManager lm, int key, RunningInfo info) {
            if(runningListener != null){
                runningListener.onRunningProcessChanged(lm, key, info);
            }
        }
    }
}
