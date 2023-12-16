package org.example.test07;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * SingleThreadEventLoop 既是 EventLoop 也是 EventExecutor，是 Nio 的注册方法实现
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2023/12/7 21:51
 */
public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop {

    private static final Logger logger = LoggerFactory.getLogger(SingleThreadEventLoop.class);

    public SingleThreadEventLoop() {
        super();
    }


    @Override
    public void register(SocketChannel socketChannel, EventLoop eventLoop) {
        eventLoop.execute(() -> {
            try {
                socketChannel.configureBlocking(false);
                socketChannel.register(((NioEventLoop) eventLoop).selector(), SelectionKey.OP_READ);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            logger.info("客户端的channel已注册到新线程的多路复用器上了！{}", eventLoop);
        });

    }


    public EventLoop next() {
        return this;
    }

}
