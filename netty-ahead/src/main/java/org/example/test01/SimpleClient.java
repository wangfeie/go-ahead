package org.example.test01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

/**
 * 实现简单的 netty 客户端
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2023/12/5 14:15
 */
public class SimpleClient {

    private static final Logger log = LoggerFactory.getLogger(SimpleClient.class);


    public static void main(String[] args) throws IOException {
        // 得到客户端的channel
        SocketChannel socketChannel = SocketChannel.open();

        // 设置非阻塞
        socketChannel.configureBlocking(false);
        // 得到 selector
        Selector selector = Selector.open();
        // 把客户端的chanel注册到selector上
        SelectionKey selectionKey = socketChannel.register(selector, 0);
        // 设置事件
        selectionKey.interestOps(SelectionKey.OP_CONNECT);
        // 客户端的 channel 去连接服务器
        socketChannel.connect(new InetSocketAddress(7000));

        // 开始轮询
        while (true) {
            // 无事件则阻塞
            selector.select();
            // 得到事件的 key
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                // 如果是连接成功事件
                if (key.isConnectable()) {
                    if (socketChannel.finishConnect()) {
                        socketChannel.register(selector, SelectionKey.OP_READ);
                        log.info("已经注册了读事件！");
                        socketChannel.write(ByteBuffer.wrap("客户端发送成功了".getBytes(StandardCharsets.UTF_8)));
                    }
                }
                // 如果是读事件
                if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    // 分配字节缓冲区来接收服务端传过来的数据
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    // 向buffer写入客户端传来的数据
                    int len = channel.read(buffer);
                    byte[] bytes = new byte[len];
                    buffer.flip();
                    buffer.get(bytes);
                    log.info("读到来自服务端的数据：" + new String(bytes));
                }
            }

        }

    }
}
