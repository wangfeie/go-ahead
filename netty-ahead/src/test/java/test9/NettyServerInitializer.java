package test9;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		
		ChannelPipeline pipeline = ch.pipeline();

		//入站的handler进行解码 MyByteToLongDecoder
		pipeline.addLast(new MyByteToLongDecoder());

		//增加返回编码器
		pipeline.addLast(new MyLongToByteEncoder());

		//编码解码位置颠倒无所谓，但必须在handler上面
		pipeline.addLast(new NettyChannelHandler());
	}

}