package test5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * 1、 编写一个netty心跳检测机制案例，当服务器超过3秒没有读时，就提示读空闲；
 * 2、 当服务器超过5秒没有写操作时，就提示写空闲；
 * 3、 当服务器超过7秒没有读或者写操作时，就提示读写空闲；
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/1/21 20:45
 */
public class MyServer {

    public static void main(String[] args) {
        //创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(); //默认cpu核数*2

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //在boosGroup增加一个日志处理器
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //加入一个netty提供的IdleStateHandler
                            /**
                             * 说明
                             * 1. IdleStateHandler是netty提供的处理空闲状态的处理器
                             * 2. public IdleStateHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit)
                             * 3. 参数
                             * long readerIdleTime：表示多长时间没有读了，就会发送一个心跳检测包，检测是否还是连接的状态
                             * long writerIdleTime：表示多长时间没有写了，也会发送一个心跳检测包
                             * long allIdleTime：表示多长时间既没有读也没有写了，也会发送一个心跳检测包
                             * 4. 文档说明
                             * Triggers an {@link IdleStateEvent} when a {@link Channel} has not performed read, write, or both operation for a while.
                             * 5. 当IdleStateEvent触发后，就会传递给管道的下一个handler
                             * 6. 通过调用（触发）下一个handler的userEventTriggered，在该方法中去处理IdleStateEvent事件
                             *
                             */
                            pipeline.addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS));

                            //加入一个对空闲检测进一步处理的自定义handler
                            pipeline.addLast(new MyServerHandler());
                        }
                    });

            //启动服务器
            ChannelFuture cf = server.bind(7000).sync();
            cf.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
