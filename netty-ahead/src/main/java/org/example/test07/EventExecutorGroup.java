package org.example.test07;

import java.util.concurrent.TimeUnit;

/**
 * 管理 EventExecutor
 * EventExecutorGroup 接口中定义了两个方法，由MultithreadEventExecutorGroup 去实现，但最后真正的执行，还是在 EventExecutor 的实现类中
 */
public interface EventExecutorGroup {

    EventExecutor next();

    void shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit);

}
