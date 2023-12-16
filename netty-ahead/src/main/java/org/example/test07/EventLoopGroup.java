package org.example.test07;

import java.nio.channels.SocketChannel;

/**
 * 成千上万客户端连接时，需要管理多个 EventLoop，同意调度 register() 进行负载注册
 *
 * 因为 EventLoopGroup 中调用的 EventLoop 得有先后顺序，所以需要一个 next 方法，返回 EventLoop，
 */
public interface EventLoopGroup extends EventExecutorGroup {

    void register(SocketChannel channel, EventLoop eventLoop);

}
