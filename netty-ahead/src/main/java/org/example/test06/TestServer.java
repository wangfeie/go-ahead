package org.example.test06;

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
 * 多路复用多线程server test05的升级
 * 解决问题：
 * 1、将 Executor 功能拆分出来
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

        Selector selector = Selector.open();
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, serverSocketChannel);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(new InetSocketAddress(7000));

        //初始化NioEventLoop数组
        NioEventLoop[] workerGroup = new NioEventLoop[2];
        workerGroup[0] = new NioEventLoop();
        workerGroup[1] = new NioEventLoop();
        int i = 0;

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
                    // 得到客户端的channel
                    SocketChannel socketChannel = channel.accept();
                    //计算要取值的数组的下标
                    int index = i % workerGroup.length;
                    // 把客户端的channel注册到新线程的selector上
                    workerGroup[index].register(socketChannel, workerGroup[index]);
                    i++;
                    logger.info("socketChannel注册到了第{}个单线程执行器上：",index);
                    // 连接成功之后，用客户端的channel写回一条消息
                    socketChannel.write(ByteBuffer.wrap("客户端发送成功了".getBytes()));
                    logger.info("main函数服务器向客户端发送数据成功！");
                }
            }
        }
    }

}
