package org.example.test03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
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

    private Thread thread;

    public Work() throws IOException {
        this.thread = new Thread(this);
    }

    public void start() {
        if (flags) {
            return;
        }
        flags = true;
        this.thread.start();
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
