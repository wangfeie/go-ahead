package test3;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author wangfeie
 * @version 1.0.0
 * @description #TODO
 * @date 2024/1/21 19:15
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //向管道加入处理器

        //得到管道
        ChannelPipeline pipeline = socketChannel.pipeline();

        //加入一个netty提供的httpServerCodec（编解码器）
        //HttpServerCodec的说明
        //1. HttpServerCodec是netty提供的处理http的编解码器
        pipeline.addLast("MyHttpServerCodec", new HttpServerCodec());

        //增加一个自定义的Handler
        pipeline.addLast("MyHttpServerHandler", new HttpServerHandler());
    }
}
