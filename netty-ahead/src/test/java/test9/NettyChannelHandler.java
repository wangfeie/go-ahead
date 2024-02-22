package test9;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyChannelHandler extends SimpleChannelInboundHandler<Long> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
		System.out.println("从客户端" + ctx.channel().remoteAddress() + "读取到long " + msg);
		
		//给客户端回送一个Long
		ctx.writeAndFlush(98765L); //writeAndFlush是ChannelOutboundInvoker的方法
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.channel().close();
	}
}