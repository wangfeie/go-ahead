package test8;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class NettyClient {

	public static void main(String[] args) throws Exception {
		
		//客户端需要一个事件循环组
		EventLoopGroup group = new NioEventLoopGroup();
		
		try {
			//创建客户端的启动对象
			//注意客户端使用的是Bootstrap
			Bootstrap bootstrap = new Bootstrap();
			
			//设置相关参数
			bootstrap.group(group) //设置线程组
				.channel(NioSocketChannel.class) //设置客户端通道的实现类
				.handler(new ChannelInitializer<SocketChannel>() {
					
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						//在pipeline中加入ProtobufEncoder
						pipeline.addLast("encoder", new ProtobufEncoder());
						//加入自己的处理器
						pipeline.addLast(new NettyClientHandler());
					}
					
				});
				
			System.out.println("...客户端 is ready...");
			
			//启动客户端去连接服务器端
			//ChannelFuture涉及到netty的异步模型
			ChannelFuture cf = bootstrap.connect("127.0.0.1", 6668).sync();
			
			//对关闭通道进行监听
			cf.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//优雅的关闭
			group.shutdownGracefully();
		}
		
	}
}