package com.common.download.thread;


import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lining on 2017/6/13.
 */

public class PriorityExecutor extends ThreadPoolExecutor {

    private static final int CORE_POOL_SIZE = ThreadManger.DEV_CUP_NUM + 1;//线程池维护线程的最少数量
    private static final int MAXIMUM_POOL_SIZE = ThreadManger.DEV_CUP_NUM*2 + 1;//线程池维护线程的最大数量
    private static final int MAXIMUM_QUEUE_SIZE = 50;//最大的任务队列
    private static final int KEEP_ALIVE = 1;//保持存活时间，当线程数大于corePoolSize的空闲线程能保持的最大时间。
    private static final AtomicLong SEQ_SEED = new AtomicLong(0);//主要获取添加任务
    /**
     * 创建线程工厂
     */
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "工作线程" + mCount.getAndIncrement());
        }
    };


    /**
     * 线程队列方式 先进先出
     */
    private static final Comparator<Runnable> FIFO = new Comparator<Runnable>() {
        @Override
        public int compare(Runnable lhs, Runnable rhs) {
            //lhs - rhs的值：1(lhs 排在 rhs的后面),0(位置相同),-1(lhs 排在 rhs的前面)
            if (lhs instanceof PriorityRunnable && rhs instanceof PriorityRunnable) {
                PriorityRunnable lpr = ((PriorityRunnable) lhs);
                PriorityRunnable rpr = ((PriorityRunnable) rhs);
                int result = lpr.priority.ordinal() - rpr.priority.ordinal();
                return result == 0 ? (int) (lpr.SEQ - rpr.SEQ) : result;
            } else {
                return 0;
            }
        }
    };
    public PriorityExecutor() {
        this(CORE_POOL_SIZE);
    }
    /**
     * @param poolSize 工作线程数
     */
    public PriorityExecutor(int poolSize) {
        this(poolSize, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, new PriorityBlockingQueue<>(MAXIMUM_QUEUE_SIZE, FIFO), sThreadFactory);
    }

    public PriorityExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    /**
     * 判断当前线程池是否繁忙
     * @return
     */
    public boolean isBusy() {
        return getActiveCount() >= getCorePoolSize();
    }

    /**
     * 提交任务
     * @param runnable
     */
    @Override
    public void execute(Runnable runnable) {
        if (runnable instanceof PriorityRunnable) {
            ((PriorityRunnable) runnable).SEQ = SEQ_SEED.getAndIncrement();
        }
        super.execute(runnable);
    }

}
