package com.heaven7.java.logic;

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
        return of(new LogicManager(), tasks, performFlags, sequence);
    }

    /**
     * create logic task group.
     * @param lm the logic manager
     * @param tasks the logic tasks
     * @param performFlags the perform flags. eg: {@linkplain LogicManager#FLAG_SHARE_TO_NEXT} and {@linkplain LogicManager#FLAG_SHARE_TO_POOL}.
     * @param sequence true to perform sequence , false to perform Parallel.
     * @return the tasks group
     */
    public static LogicTaskGroup of(LogicManager lm, List<LogicTask> tasks, int performFlags, boolean sequence){
        GroupAction action = new GroupAction();
        action.mLM = lm;
        action.sequence = sequence;
        action.mLogicFlags = performFlags;
        action.mTasks = tasks;
        return new LogicTaskGroup(action, null);
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

    private static class GroupAction extends SimpleLogicAction implements LogicResultListener{
        LogicManager mLM;
        boolean sequence;
        int mLogicFlags;
        List<LogicTask> mTasks;
        @Override
        protected void performImpl(int tag, int count, LogicParam param) {
            if(sequence){
                mLM.performSequence(mTasks, mLogicFlags, this);
            }else{
                mLM.performParallel(mTasks, mLogicFlags, this);
            }
        }
        @Override
        public void onFailed(LogicManager lm, List<LogicTask> failedTask, Object lastResult, List<?> results) {
            dispatchResult(0, new LogicResult(LogicAction.RESULT_FAILED, new GroupData(lastResult,results)));
        }
        @Override
        public void onSuccess(LogicManager lm, LogicTask lastTask, Object lastResult, List<?> results) {
            dispatchResult(0, new LogicResult(LogicAction.RESULT_SUCCESS, new GroupData(lastResult,results)));
        }
    }
}
