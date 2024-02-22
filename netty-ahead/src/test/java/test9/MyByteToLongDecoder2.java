package test9;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class MyByteToLongDecoder2 extends ReplayingDecoder<Void> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		System.out.println("MyByteToLongDecoder2 decode 被调用");
		//在ReplayingDecoder 不需要判断数据是否足够读取，它内部会进行处理和判断
		
		out.add(in.readLong());
	}

}