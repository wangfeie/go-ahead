package org.example.test06;

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
 * 多路复用器 selector 相关
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2023/12/7 21:54
 */
public class NioEventLoop extends SingleThreadEventLoop {

    private static final Logger logger = LoggerFactory.getLogger(NioEventLoop.class);

    private final SelectorProvider provider;

    private Selector selector;

    public NioEventLoop() {
        //java中的方法，通过provider不仅可以得到selector，还可以得到ServerSocketChannel和SocketChannel
        this.provider = SelectorProvider.provider();
        this.selector = openSecector();
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

    public Selector selector() {
        return selector;
    }


    /**
     * 判断线程是否需要在selector阻塞，或者继续运行的方法，为什么现在需要这个方法了？
     * <p>
     * 因为现在我们新创建的线程不仅要处理io事件，还要处理用户提交过来的任务，如果一直在selector上阻塞着，
     * 显然用户提交的任务也就无法执行了。所以要有限时的阻塞，并且只要用户提交了任务，就要去执行那些任务。
     * 在这里，用户提交的任务就是把客户端channel注册到selector上。
     */
    private void select() throws IOException {
        Selector selector = this.selector;
        // 这里是一个死循环
        for (; ; ) {
            // 如果没有就绪事件，就在这里阻塞3秒，有限时的阻塞
            logger.info("新线程阻塞在这里3秒吧。。。。。。。");
            int selectedKeys = selector.select(3000);
            // 如果有事件或者单线程执行器中有任务待执行，就退出循环
            if (selectedKeys != 0 || hasTasks()) {
                break;
            }
        }
    }


    private void processSelectedKeys(Set<SelectionKey> selectedKeys) throws IOException {
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> i = selectedKeys.iterator();
        for (; ; ) {
            final SelectionKey k = i.next();
            i.remove();
            // 处理就绪事件
            processSelectedKey(k);
            if (!i.hasNext()) {
                break;
            }
        }
    }

    private void processSelectedKey(SelectionKey k) throws IOException {
        // 如果是读事件
        if (k.isReadable()) {
            SocketChannel channel = (SocketChannel) k.channel();
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            int len = channel.read(byteBuffer);
            if (len == -1) {
                logger.info("客户端通道要关闭！");
                channel.close();
                return;
            }
            byte[] bytes = new byte[len];
            byteBuffer.flip();
            byteBuffer.get(bytes);
            logger.info("新线程收到客户端发送的数据:{}", new String(bytes));
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 没有事件就阻塞在这里
                select();
                // 如果走到这里，就说明selector没有阻塞了
                processSelectedKeys(selector.selectedKeys());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 执行单线程执行器中的所有任务
                runAllTasks();
            }
        }
    }
}
