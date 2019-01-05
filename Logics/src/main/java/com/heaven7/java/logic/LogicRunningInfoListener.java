package com.heaven7.java.logic;

/**
 * the logic running info listener
 * @author heaven7
 * @since 1.1.2
 */
public interface LogicRunningInfoListener {

    /**
     * called on running process changed.
     * @param lm the logic manager
     * @param key the key
     * @param info the running info. which contains success and failed info.
     */
    void onRunningProcessChanged(LogicManager lm, int key, RunningInfo info);
}
