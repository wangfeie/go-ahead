package test2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    public static void main(String[] args) throws Exception {

        //创建BossGroup和WorkerGroup
        //说明
        //1. 创建两个线程组bossGroup和workerGroup
        //2. bossGroup它只是处理连接请求，真正的与客户端业务处理会交给workerGroup去完成
        //3. 两个都是无限循环
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //创建服务器端的启动对象，配置启动参数
            ServerBootstrap bootstrap = new ServerBootstrap();

            //使用链式编程来进行设置
            bootstrap.group(bossGroup, workerGroup) //设置两个线程组
                    .channel(NioServerSocketChannel.class) //使用NioServerSocketChannel作为服务器的通道实现
                    .handler(new ChannelInitializer<ServerSocketChannel>() {
                        @Override
                        protected void initChannel(ServerSocketChannel serverSocketChannel) throws Exception {
                            System.out.println("handler initChannel");
                        }
                    })
                    .childHandler(new ChannelInitializer<SocketChannel>() { //创建一个通道初始化对象
                        //给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("childHandler initChannel");

                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new NettyChannelHandler()); //向管道的最后增加一个处理器
                            //向管道的最后增加一个处理器
                            pipeline.addLast(new NettyChannelHandler2());
                            pipeline.addLast(new NettyChannelHandler3());

                        }

                        ;
                    }); //给我们的workerGroup的EventLoop对应的管道设置处理器

            //bossGroup参数
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024); //设置线程队列等待连接的个数

            //workerGroup参数
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true); //设置保持活动连接状态

            System.out.println("...服务器 is ready...");

            //绑定一个端口并且同步，生成了一个ChannelFuture对象
            //启动服务器并绑定端口
            ChannelFuture cf = bootstrap.bind(6668).sync();

            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Shutdown Netty Server...");
            //优雅的关闭
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("Shutdown Netty Server Success!");
        }

    }
}