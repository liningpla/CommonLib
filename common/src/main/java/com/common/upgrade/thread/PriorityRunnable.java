package com.common.upgrade.thread;

/**
 * Created by lining on 2017/6/13.
 */

public class PriorityRunnable implements Runnable {
    private final Runnable runnable;//任务真正执行者
    public  final Priority priority;//任务优先级
    public long SEQ;//任务唯一标示

    public PriorityRunnable(Priority priority, Runnable runnable) {
        this.priority = priority == null ? Priority.NORMAL : priority;
        this.runnable = runnable;
    }

    @Override
    public final void run() {
        this.runnable.run();
    }
}
