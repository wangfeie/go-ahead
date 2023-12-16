package org.example.test03;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 多路复用多线程server test02的升级
 * 解决问题：
 * 1、并不需要两个线程去接收客户端 accept，会造成一个线程 accept 是空指针，所以主线程做 accept
 * 2、新线程只需要处理 read、write，主线程需要把 read、write 事件注册到子线程的 selector 上
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2023/12/5 19:16
 */
public class TestServer {

    private static final Logger logger = LoggerFactory.getLogger(TestServer.class);

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);
        // 创建线程
        Work work = new Work();
        Selector workSelector = work.getSelector();

        Selector selector = Selector.open();
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, serverSocketChannel);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(new InetSocketAddress(7000));

        while (true) {
            logger.info("main函数阻塞在这里吧。。。。。。。");
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            if (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();

                    SocketChannel socketChannel = channel.accept();
                    socketChannel.configureBlocking(false);
                    // 把客户端的channel注册到新线程的selector上，但这时，新的线程还未启动
                    SelectionKey socketChannelKey = socketChannel.register(workSelector, 0, socketChannel);
                    // 给客户端的channel设置可读事件
                    socketChannelKey.interestOps(SelectionKey.OP_READ);
                    // 可以启动新的线程了,在while循环中，我们必须保证线程只启动一次
                    work.start();
                    logger.info("客户端在main函数中连接成功！");
                    socketChannel.write(ByteBuffer.wrap("客户端发送成功了".getBytes()));
                    logger.info("main函数服务器向客户端发送数据成功！");
                }
            }
        }
    }

}
