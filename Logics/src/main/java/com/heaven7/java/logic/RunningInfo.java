package com.heaven7.java.logic;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * the running info of logic tasks
 * @author heaven7
 * @since 1.1.2
 */
public final class RunningInfo {

    private final AtomicInteger successCount;
    private final AtomicInteger failedCount;
    private final int totalCount;

    public RunningInfo(int totalCount) {
        this.totalCount = totalCount;
        this.successCount = new AtomicInteger();
        this.failedCount = new AtomicInteger();
    }

    public int getSuccessCount(){
        return successCount.get();
    }
    public int getFailedCount(){
        return failedCount.get();
    }
    public int getTotalCount(){
        return totalCount;
    }

    /**
     * get finish percent
     * @return the finish percent . 0-1
     * @since 1.1.3
     */
    public float getFinishPercent(){
        return (getSuccessCount() + getFailedCount()) * 1f / totalCount;
    }
    /**
     * get success percent
     * @return the success percent . 0-1
     * @since 1.1.3
     */
    public float getSuccessPercent(){
        return getSuccessCount() * 1f / totalCount;
    }
    /**
     * get failed percent
     * @return the failed percent . 0-1
     * @since 1.1.3
     */
    public float getFailedPercent(){
        return getFailedCount() * 1f / totalCount;
    }

    /*public*/ void increaseSuccess() {
        successCount.incrementAndGet();
    }

    /*public*/ void increaseFailed() {
        failedCount.incrementAndGet();
    }

    @Override
    public String toString() {
        return "RunningInfo{" +
                "successCount=" + successCount +
                ", failedCount=" + failedCount +
                ", totalCount=" + totalCount +
                '}';
    }
}
