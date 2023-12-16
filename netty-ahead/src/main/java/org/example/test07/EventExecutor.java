package org.example.test07;

import java.util.concurrent.Executor;

/**
 * 执行器，继承自 Executor，具有 execute() 方法
 * <p>
 * 继承 EventExecutorGroup 与 EventLoop 接口同理
 * 这样，EventExecutor 就不必定义方法，方法都定义在 EventLoopGroup 中，但是在 EventExecutorGroup、SingleThreadEventLoop 中都可以得到实现，
 * * 如果你创建的是 EventExecutorGroup 的对象，那么你调用register 方法逻辑就会来到 EventExecutorGroup 类中的 shutdownGracefully 方法中，该方法为你返回一个 EventExecutor 对象，然后再执行该对象的 shutdownGracefully 方法。
 * * 如果你创建的是 EventExecutor 的对象，那么调用该方法就会直接为你注册客户端连接。
 */
public interface EventExecutor extends Executor, EventExecutorGroup {

}
