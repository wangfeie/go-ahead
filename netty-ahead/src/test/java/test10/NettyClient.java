package test10;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyClient {
	public static void main(String[] args) {
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group) //设置线程组
				.channel(NioSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.DEBUG))
				.handler(new NettyClientInitializer()); //自定义一个初始化对象
			
			ChannelFuture cf = bootstrap.connect("127.0.0.1", 7000).sync();
			cf.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
}