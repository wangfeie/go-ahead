package org.example.test05;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 多路复用使用多线程测试 test02的升级版
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2023/12/5 16:54
 */
public class SingleThreadEventExecutor implements Executor {

    private static final Logger logger = LoggerFactory.getLogger(SingleThreadEventExecutor.class);

    //任务队列的容量，默认是Integer的最大值
    protected static final int DEFAULT_MAX_PENDING_TASKS = Integer.MAX_VALUE;

    private final Queue<Runnable> taskQueue;

    private final RejectedExecutionHandler rejectedExecutionHandler;

    private volatile boolean start = false;

    private final SelectorProvider provider;

    private Selector selector = Selector.open();

    private Thread thread;

    public SingleThreadEventExecutor() throws IOException {
        // java中的方法，通过provider不仅可以得到selector，还可以得到ServerSocketChannel和SocketChannel
        this.provider = SelectorProvider.provider();
        this.taskQueue = newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
        this.rejectedExecutionHandler = new ThreadPoolExecutor.AbortPolicy();
        this.selector = openSecector();
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
        System.out.println("我没任务了！");
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

    /**
     * 判断当前执行任务的线程是否是执行器的线程
     */
    public boolean inEventLoop(Thread thread) {
        return thread == this.thread;
    }


    public void register(SocketChannel socketChannel) {
        // 如果执行该方法的线程就是执行器中的线程，直接执行方法即可
        if (inEventLoop(Thread.currentThread())) {
            register0(socketChannel);
        } else {
            // 在这里，第一次向单线程执行器中提交任务的时候，执行器终于开始执行了,新的线程也开始创建
            this.execute(() -> {
                register0(socketChannel);
                logger.info("客户端的channel已注册到新线程的多路复用器上了！");
            });
        }
    }


    private void register0(SocketChannel channel) {
        try {
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * 得到用于多路复用的selector
     */
    private Selector openSecector() {
        try {
            selector = provider.openSelector();
            return selector;
        } catch (IOException e) {
            throw new RuntimeException("failed to open a new selector", e);
        }
    }


    /**
     * 判断线程是否需要在selector阻塞，或者继续运行的方法，为什么现在需要这个方法了？
     * <p>
     * 因为现在我们新创建的线程不仅要处理io事件，还要处理用户提交过来的任务，如果一直在selector上阻塞着，
     * 显然用户提交的任务也就无法执行了。所以要有限时的阻塞，并且只要用户提交了任务，就要去执行那些任务。
     * 在这里，用户提交的任务就是把客户端channel注册到selector上。
     */
    private void select() throws IOException {
        Selector selector = this.selector;
        // 这里是一个死循环
        for (; ; ) {
            // 如果没有就绪事件，就在这里阻塞3秒，有限时的阻塞
            logger.info("新线程阻塞在这里3秒吧。。。。。。。");
            int selectedKeys = selector.select(3000);
            // 如果有事件或者单线程执行器中有任务待执行，就退出循环
            if (selectedKeys != 0 || hasTasks()) {
                break;
            }
        }
    }


    private void processSelectedKeys(Set<SelectionKey> selectedKeys) throws IOException {
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> i = selectedKeys.iterator();
        for (; ; ) {
            final SelectionKey k = i.next();
            i.remove();
            // 处理就绪事件
            processSelectedKey(k);
            if (!i.hasNext()) {
                break;
            }
        }
    }

    private void processSelectedKey(SelectionKey k) throws IOException {
        // 如果是读事件
        if (k.isReadable()) {
            SocketChannel channel = (SocketChannel) k.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int len = channel.read(byteBuffer);
            if (len == -1) {
                logger.info("客户端通道要关闭！");
                channel.close();
                return;
            }
            byte[] bytes = new byte[len];
            byteBuffer.flip();
            byteBuffer.get(bytes);
            logger.info("新线程收到客户端发送的数据:{}", new String(bytes));
        }
    }

    public void run() {
        while (true) {
            try {
                // 没有事件就阻塞在这里
                select();
                // 如果走到这里，就说明selector没有阻塞了
                processSelectedKeys(selector.selectedKeys());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 执行单线程执行器中的所有任务
                runAllTasks();
            }
        }
    }


}
