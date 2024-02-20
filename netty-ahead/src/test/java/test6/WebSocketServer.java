package test6;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * WebSocket长连接开发
 * <p>
 * 1、 http协议是无状态的，浏览器和服务器间的请求响应一次，下一次会重新创建连接；
 * 2、 要求：实现基于webSocket的长连接的全双工的交互；
 * 3、 改变http协议多次请求的约束，实现长连接了，服务器可以发送消息给浏览器；
 * 4、 客户端浏览器和服务器端会相互感知，比如服务器关闭了，浏览器会感知，同样浏览器关闭了，服务器会感知；
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/1/21 20:51
 */
public class WebSocketServer {

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

                            //因为基于http协议，所以我们使用http的编码和解码器
                            pipeline.addLast(new HttpServerCodec());

                            //是以块方式写的，添加ChunkedWriteHandler处理器
                            //chunkedWriteHandler是处理大数据传输的，不会占用大内存或导致内存溢出问题，它会维护管理大文件传输过程中时复杂的状态
                            pipeline.addLast(new ChunkedWriteHandler());

                            /**
                             * 说明：
                             * 1. http数据在传输过程中是分段的，HttpObjectAggregator就是可以将多个段聚合起来
                             * 2. 这就是为什么，当浏览器发送大量数据时，就会发送多次http请求
                             */
                            pipeline.addLast(new HttpObjectAggregator(10240));

                            /**
                             * 说明：
                             * 1. 对应WebSocket，它的数据是以帧（frame）形式传递
                             * 2. 可以看到WebSocketFrame下面有六个子类
                             * 3. 浏览器请求时：ws://localhost:7000/hello 表示请求的uri
                             * 4. WebSocketServerProtocolHandler核心功能是将http协议升级为ws协议，保持长连接
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));

                            //自定义的handler，处理业务逻辑
                            pipeline.addLast(new MyTextWebSocketFrameHandler());


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
