package test11;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class NettyClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {

	private int count;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {
		int len = msg.getLen();
		byte[] content = msg.getContent();

		System.out.println("客户端接收到消息如下");
		System.out.println("长度：" + len);
		System.out.println("内容：" + new String(content, CharsetUtil.UTF_8));

		System.out.println("客户端接收到消息包数量：" + (++this.count));
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//使用客户端发送5条数据
		for (int i=0; i<5; i++) {
			String msg = "今天天气冷，吃火锅";
			byte[] content = msg.getBytes(CharsetUtil.UTF_8);
			int length = content.length;
			
			//创建协议包
			MessageProtocol messageProtocol = new MessageProtocol();
			messageProtocol.setLen(length);
			messageProtocol.setContent(content);
			ctx.writeAndFlush(messageProtocol);
			
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.channel().close();
	}

}