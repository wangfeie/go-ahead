package test11;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		//加入编码器
		pipeline.addLast(new MyMessageEncoder());
		//加入解码器
		pipeline.addLast(new MyMessageDecoder());
		//加入一个自定义handler
		pipeline.addLast(new NettyClientHandler());
	}

}