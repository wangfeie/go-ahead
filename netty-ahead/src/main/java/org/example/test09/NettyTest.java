package org.example.test09;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutionException;

/**
 * 优化自 test08
 * 舍弃 FutureTask 的频繁包装
 * 依赖给 DefaultPromise 的成员变量 result 赋值
 */
public class NettyTest {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        //创建一个selector
        Selector selector = Selector.open();
        //创建一个服务端的通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        //创建一个promise
        DefaultPromise promise = new DefaultPromise();
        //创建一个runnable，异步任务
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    //将channel注册到selector上,关注接收事件
                    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                    //在这里给promise中的result成员变量赋值
                    promise.setSuccess(null);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        //异步执行runnable任务
        Thread thread = new Thread(runnable);
        //启动线程
        thread.start();
        //主线程阻塞，直到promise.setSuccess(null)这行代码执行了才继续向下运行
        promise.sync();
        //服务端channel绑定端口号
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 7000));
    }
}