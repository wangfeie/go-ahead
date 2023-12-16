package org.example.test07;

import java.util.concurrent.TimeUnit;

/**
 * @author wangfeie
 * @version 1.0.0
 * @description #TODO
 * @date 2023/12/11 9:31
 */
public abstract class MultiThreadEventExecutorGroup implements EventExecutorGroup {

    private EventExecutor[] eventExecutors;

    private int index = 0;

    public MultiThreadEventExecutorGroup(int threads) {
        eventExecutors = new EventExecutor[threads];
        for (int i = 0; i < threads; i++) {
            eventExecutors[i] = newChild();
        }
    }


    /**
     * 这里定义了一个抽象方法。是给子类实现的。因为你不知道要返回的是EventExecutor的哪个实现类。
     */
    protected abstract EventExecutor newChild();

    @Override
    public EventExecutor next() {
        int id = index % eventExecutors.length;
        index++;
        return eventExecutors[id];
    }

    @Override
    public void shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        next().shutdownGracefully(quietPeriod, timeout, unit);
    }
}
