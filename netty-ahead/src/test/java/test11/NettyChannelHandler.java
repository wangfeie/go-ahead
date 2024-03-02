package test11;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.util.UUID;

public class NettyChannelHandler extends SimpleChannelInboundHandler<MessageProtocol> {

	private int count;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
		//接收到数据并处理
		int len = msg.getLen();
		byte[] content = msg.getContent();
		
		System.out.println("服务端接收到信息如下");
		System.out.println("长度：" + len);
		System.out.println("内容：" + new String(content, CharsetUtil.UTF_8));
		
		System.out.println("服务器接收到消息包数量：" + (++this.count));
		
		//回复消息
		String response = UUID.randomUUID().toString();
		byte[] responseContent = response.getBytes(CharsetUtil.UTF_8);
		int responseLen = responseContent.length;

		//构建一个协议包
		MessageProtocol messageProtocol = new MessageProtocol();
		messageProtocol.setLen(responseLen);
		messageProtocol.setContent(responseContent);

		ctx.writeAndFlush(messageProtocol);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.channel().close();
	}

}