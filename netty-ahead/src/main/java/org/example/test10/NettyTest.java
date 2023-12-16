package org.example.test10;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutionException;

public class NettyTest {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        //创建一个selector
        Selector selector = Selector.open();
        //创建一个服务端的通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        //创建一个promise
        DefaultPromise promise = new DefaultPromise();
        //向Promise中添加一个监听器
        promise.addListener(
                //创建一个监听器对象
                new ChannelListener() {
                    @Override
                    public void operationComplete(Promise promise) throws Exception {
                        //服务端channel绑定端口号
                        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 7000));
                    }
                });
        //创建一个runnable，异步任务
        Runnable runnable = () -> {
            try {
                //将channel注册到selector上,关注接收事件
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                //在这里给promise中的result成员变量赋值
                promise.setSuccess(null);
            } catch (ClosedChannelException e) {
                throw new RuntimeException(e);
            }

        };
        //异步执行runnable任务
        Thread thread = new Thread(runnable);
        //启动线程
        thread.start();
        //主线程阻塞就可以注释掉了，因为单线程执行器执行完了注册方法后，会通过回调监听器的方法
        //来将服务端channel和端口号绑定
        //promise.sync();

    }
}