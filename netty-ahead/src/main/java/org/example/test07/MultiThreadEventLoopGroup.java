package org.example.test07;

import java.nio.channels.SocketChannel;

/**
 * @author wangfeie
 * @version 1.0.0
 * @description #TODO
 * @date 2023/12/11 9:51
 */
public abstract class MultiThreadEventLoopGroup extends MultiThreadEventExecutorGroup implements EventLoopGroup {

    protected MultiThreadEventLoopGroup(int nThreads) {
        super(nThreads);
    }

    @Override
    protected abstract EventLoop newChild();

    @Override
    public EventLoop next() {
        return (EventLoop) super.next();
    }

    //这个方法是实现了EventLoopGroup接口中的同名方法
    @Override
    public void register(SocketChannel channel, EventLoop eventLoop) {
        next().register(channel, eventLoop);
    }
}
