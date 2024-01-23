package test2;

import java.util.Map;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;

/**
 * 说明
 * 1. 我们自定义一个Handler，需要继承netty规定好的某个HandlerAdapter（规范）
 * 2. 这时我们自定义一个Handler，才能称之为Handler
 * 
 */
public class NettyChannelHandler4 extends ChannelInboundHandlerAdapter {
	
	private Map<Integer, SocketChannel> map;
	
	public NettyChannelHandler4(Map<Integer, SocketChannel> map) {
		this.map = map;
	}

	//读取数据的事件（这里我们可以读取客户端发送的消息）
	/*
	 * 1. ChannelHandlerContext ctx：上下文对象，含有管道pipeline，通道channel，地址
	 * 2. Object msg：就是客户端发送的数据，默认是Object
	 * 3. 通道读写数据，管道处理数据
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		System.err.println("channel的数量：" + map.size());
		
		//方案3：服务器端要推送到管道A、管道B、管道C。。。
		map.forEach((key, value) -> {  
            value.writeAndFlush(Unpooled.copiedBuffer("server向"+key+"发送消息", CharsetUtil.UTF_8));
        });
	}
	
	//数据读取完毕
	//这个方法会在channelRead读完后触发
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		//把数据写到缓冲区，并且刷新缓冲区，是write + flush
		//一般来讲，我们对这个发送的数据进行编码
		//ctx.channel().writeAndFlush(Unpooled.copiedBuffer("hello，客户端~", CharsetUtil.UTF_8));
		
	}
	
	//处理异常，一般是需要关闭通道
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		//客户端主动断开连接时会进到这里
		
		//移除集合
		map.remove(ctx.channel().hashCode());
		ctx.channel().close();
	}
}