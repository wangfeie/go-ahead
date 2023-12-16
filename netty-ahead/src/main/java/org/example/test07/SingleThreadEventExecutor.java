package org.example.test07;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * EventExecutorGroup 接口中定义了两个方法，由MultithreadEventExecutorGroup 去实现，但最后真正的执行，还是在 EventExecutor 的实现类中
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2023/12/5 16:54
 */
public abstract class SingleThreadEventExecutor implements EventExecutor {

    private static final Logger logger = LoggerFactory.getLogger(SingleThreadEventExecutor.class);

    //任务队列的容量，默认是Integer的最大值
    protected static final int DEFAULT_MAX_PENDING_TASKS = Integer.MAX_VALUE;

    private final Queue<Runnable> taskQueue;

    private final RejectedExecutionHandler rejectedExecutionHandler;

    private volatile boolean start = false;

    private Thread thread;

    public SingleThreadEventExecutor() {
        this.taskQueue = newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
        this.rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return new LinkedBlockingQueue<>(maxPendingTasks);
    }


    @Override
    public void execute(Runnable task) {
        if (Objects.isNull(task)) {
            throw new NullPointerException("task");
        }
        // 把任务提交到任务队列中，这里直接把它提交给队列，是考虑到单线程执行器既要处理IO事件
        // 也要执行用户提交的任务，不可能同一时间做两件事。索性就直接先把任务放到队列中。等IO事件处理了
        // 再来处理用户任务
        addTask(task);
        // 启动单线程执行器中的线程
        startThread();
    }

    private void addTask(Runnable task) {
        if (Objects.isNull(task)) {
            throw new NullPointerException("task");
        }
        // 如果添加失败，执行拒绝策略
        if (!offerTask(task)) {
            reject(task);
        }
    }

    private void startThread() {
        if (start) {
            return;
        }
        start = true;
        new Thread(() -> {
            // 这里是得到了新创建的线程
            thread = Thread.currentThread();
            // 执行run方法，在run方法中，就是对io事件的处理
            SingleThreadEventExecutor.this.run();
        }).start();
        logger.info("新线程创建了！");
    }


    final boolean offerTask(Runnable task) {
        return taskQueue.offer(task);
    }


    /**
     * 判断任务队列中是否有任务
     */
    protected boolean hasTasks() {
        return !taskQueue.isEmpty();
    }

    /**
     * 执行任务队列中的所有任务
     */
    protected void runAllTasks() {
        runAllTasksFrom(taskQueue);
    }


    protected void runAllTasksFrom(Queue<Runnable> taskQueue) {
        // 从任务队列中拉取任务,如果第一次拉取就为null，说明任务队列中没有任务，直接返回即可
        Runnable task = pollTaskFrom(taskQueue);
        if (task == null) {
            return;
        }
        for (; ; ) {
            // 执行任务队列中的任务
            safeExecute(task);
            // 执行完毕之后，拉取下一个任务，如果为null就直接返回
            task = pollTaskFrom(taskQueue);
            if (task == null) {
                return;
            }
        }
    }

    protected static Runnable pollTaskFrom(Queue<Runnable> taskQueue) {
        return taskQueue.poll();
    }


    private void safeExecute(Runnable task) {
        try {
            task.run();
        } catch (Throwable t) {
            logger.warn("A task raised an exception. Task: {}", task, t);
        }
    }


    protected final void reject(Runnable task) {
        //rejectedExecutionHandler.rejectedExecution(task, this);
    }

    public abstract void run();

    @Override
    public void shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        while (this.thread.isAlive()) {
            this.thread.interrupt();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
