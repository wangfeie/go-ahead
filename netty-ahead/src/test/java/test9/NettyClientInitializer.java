package test9;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

	/**
	 * 出站的handler从后往前调用，因为你pipeline是用addLast加在最后，入站是从前往后，出站就是从后往前
	 */
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		
		ChannelPipeline pipeline = ch.pipeline();

		//加入一个出站的handler，对数据进行一个编码
		pipeline.addLast(new MyLongToByteEncoder());

		//增加一个入站的解码器
		pipeline.addLast(new MyByteToLongDecoder());

		//加入一个自定义的handler，处理业务逻辑
		pipeline.addLast(new NettyClientHandler());
	}

}