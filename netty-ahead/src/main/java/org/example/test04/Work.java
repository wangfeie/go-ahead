package org.example.test04;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

/**
 * 多路复用使用多线程测试 test02的升级版
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2023/12/5 16:54
 */
public class Work implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Work.class);

    private boolean flags;

    private Selector selector = Selector.open();

    private final SelectorProvider provider;

    private Thread thread;

    public Work() throws IOException {
        // java中的方法，通过provider不仅可以得到selector，还可以得到ServerSocketChannel和SocketChannel
        provider = SelectorProvider.provider();
        this.selector = openSecector();
        this.thread = new Thread(this);
    }

    public void start() {
        if (flags) {
            return;
        }
        flags = true;
        this.thread.start();
    }

    public void register(SocketChannel socketChannel) {
        try {
            socketChannel.configureBlocking(false);
            //在这里注册有用吗？这里仍然是主线程注册channel到新的selector上
            socketChannel.register(selector, SelectionKey.OP_READ);
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        while (true) {
            logger.info("新线程阻塞在这里吧。。。。。。。");
            try {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                        int len = channel.read(byteBuffer);
                        if (len == -1) {
                            logger.info("客户端通道要关闭！");
                            channel.close();
                            break;
                        }
                        byte[] bytes = new byte[len];
                        byteBuffer.flip();
                        byteBuffer.get(bytes);
                        logger.info("新线程收到客户端发送的数据:{}", new String(bytes));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
