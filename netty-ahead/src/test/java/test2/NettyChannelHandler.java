package test2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 说明
 * 1. 我们自定义一个Handler，需要继承netty规定好的某个HandlerAdapter（规范）
 * 2. 这时我们自定义一个Handler，才能称之为Handler
 * 
 */
public class NettyChannelHandler extends ChannelInboundHandlerAdapter {

	//读取数据的事件（这里我们可以读取客户端发送的消息）
	/*
	 * 1. ChannelHandlerContext ctx：上下文对象，含有管道pipeline，通道channel，地址
	 * 2. Object msg：就是客户端发送的数据，默认是Object
	 * 3. 通道读写数据，管道处理数据
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		System.out.println("server ctx = " + ctx);
		//将msg转成一个ByteBuf
		//这个ByteBuf是netty提供的，不是nio的ByteBuffer
		ByteBuf buf = (ByteBuf) msg;
		System.out.println("客户端发送消息是：" + buf.toString(CharsetUtil.UTF_8));
		System.out.println("客户端地址：" + ctx.channel().remoteAddress());
		
		//channelReadComplete方法的返回，写在这里也可以，因为能获取ctx.channel()
		ctx.channel().writeAndFlush(Unpooled.copiedBuffer("hello，客户端~", CharsetUtil.UTF_8));
		super.channelRead(ctx, buf);
	}

	
	//处理异常，一般是需要关闭通道
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.channel().close();
	}
}