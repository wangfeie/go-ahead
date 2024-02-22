package test9;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class NettyClientHandler extends SimpleChannelInboundHandler<Long> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {
        System.out.println("服务器的ip=" + ctx.channel().remoteAddress());
        System.out.println("收到服务器消息=" + msg);
    }

    //重写channelActive 发送数据
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("NettyClientHandler 发送数据");
        ctx.writeAndFlush(123456L); //发送的是一个long

        //如果这里传字符串
        //ctx.writeAndFlush(Unpooled.copiedBuffer("abcdabcdabcdabcd", CharsetUtil.UTF_8));
        //分析
        //1. "abcdabcdabcdabcd"是16个字节
        //2. 该处理器的前一个handler 是 MyLongToByteEncoder
        //3. MyLongToByteEncoder 父类是 MessageToByteEncoder
        //4. 父类有一个write方法，会判断msg的类型是不是自己要处理的，如果不是就写出去
        //5. 因此我们编写Encoder时要注意，传入的数据类型和处理的数据类型一致
		/*
		   try {
            if (acceptOutboundMessage(msg)) {
                @SuppressWarnings("unchecked")
                I cast = (I) msg;
                buf = allocateBuffer(ctx, cast, preferDirect);
                try {
                    encode(ctx, cast, buf);
                } finally {
                    ReferenceCountUtil.release(cast);
                }

                if (buf.isReadable()) {
                    ctx.write(buf, promise);
                } else {
                    buf.release();
                    ctx.write(Unpooled.EMPTY_BUFFER, promise);
                }
                buf = null;
            } else {
                ctx.write(msg, promise);
            }
		 */
    }
}