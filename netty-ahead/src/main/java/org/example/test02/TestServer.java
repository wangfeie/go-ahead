package org.example.test02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 多路复用多线程server
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
        Work work = new Work(serverSocketChannel);

        Selector selector = Selector.open();
        SelectionKey selectionKey = serverSocketChannel.register(selector, 0, serverSocketChannel);
        selectionKey.interestOps(SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(new InetSocketAddress(7000));

        // 启动线程
        work.start();

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
                    //接下来就要管理客户端的channel了，和服务端的channel的做法相同，客户端的channel也应该被注册到selector上
                    //通过一次次的轮询来接受并处理channel上的相关事件
                    //把客户端的channel注册到之前已经创建好的selector上
                    SelectionKey socketChannelKey = socketChannel.register(selector, 0, socketChannel);
                    //给客户端的channel设置可读事件
                    socketChannelKey.interestOps(SelectionKey.OP_READ);
                    logger.info("客户端在main函数中连接成功！");
                    //连接成功之后，用客户端的channel写回一条消息
                    socketChannel.write(ByteBuffer.wrap("我发送成功了".getBytes()));
                    logger.info("main函数服务器向客户端发送数据成功！");
                }
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();

                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int len = channel.read(buffer);
                    logger.info("读到的字节数：" + len);
                    if (len == -1) {
                        channel.close();
                        break;
                    }
                    //切换buffer的读模式
                    buffer.flip();
                    logger.info(Charset.defaultCharset().decode(buffer).toString());
                }
            }
        }
    }

}
