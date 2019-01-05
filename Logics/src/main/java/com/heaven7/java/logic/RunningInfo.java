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
