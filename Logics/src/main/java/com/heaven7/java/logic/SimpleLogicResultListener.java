package com.heaven7.java.logic;

import java.util.List;

/**
 * the simple logic result listener
 * @author heaven7
 * @since 1.1.2
 * @see LogicResultListener
 * @see LogicRunningInfoListener
 */
public class SimpleLogicResultListener implements LogicResultListener, LogicRunningInfoListener {

    @Override
    public void onFailed(LogicManager lm, List<LogicTask> failedTask, Object lastResult, List<?> results) {

    }

    @Override
    public void onSuccess(LogicManager lm, LogicTask lastTask, Object lastResult, List<?> results) {

    }

    @Override
    public void onRunningProcessChanged(LogicManager lm, int key, RunningInfo info) {

    }
}
