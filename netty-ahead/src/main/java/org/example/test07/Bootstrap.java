package org.example.test07;

import java.nio.channels.SocketChannel;

public class Bootstrap {

    private EventLoopGroup eventLoopGroup;

    public Bootstrap() {

    }

    public Bootstrap group(EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
        return this;
    }

    public void register(SocketChannel channel, EventLoop eventLoop) {
        eventLoopGroup.register(channel, eventLoop);
    }
}