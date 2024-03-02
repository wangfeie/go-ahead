package test11;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class MyMessageDecoder extends ReplayingDecoder<Void> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		System.out.println("MyMessageDecoder decode 被调用");
		
		//需要将二进制字节码转成MessageProtocol对象
		int len = in.readInt();
		
		//创建一个len长度的字节数组
		byte[] content = new byte[len];
		in.readBytes(content);
		
		//封装成MessageProtocol对象，放入out，传递给下一个handler处理
		MessageProtocol messageProtocol = new MessageProtocol();
		messageProtocol.setLen(len);
		messageProtocol.setContent(content);
		
		out.add(messageProtocol);
	}

}