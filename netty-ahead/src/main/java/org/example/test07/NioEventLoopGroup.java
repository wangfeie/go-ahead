package org.example.test07;

import java.nio.channels.SocketChannel;

/**
 * 管理 NioEventLoop
 * <p>
 * NioEventLoopGroup 需要停止运行，需要调用 shutdownGracefully ，那么直接继承 MultiThreadEventLoopGroup
 * <p>
 * 最终需要 NioEventLoop 去实现方法，同时再创建 NioEventLoop 对象的背后，也需要创建一个执行器
 */
public class NioEventLoopGroup extends MultiThreadEventLoopGroup {
    public NioEventLoopGroup(int threads) {
        super(threads);
    }

    @Override
    protected EventLoop newChild() {
        return new NioEventLoop();
    }

    @Override
    public void register(SocketChannel channel, EventLoop eventLoop) {
        super.register(channel, eventLoop);
    }

    @Override
    public EventLoop next() {
        return super.next();
    }

}