package test9;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/1/23 9:41
 */
public class MyByteToLongDecoder extends ByteToMessageDecoder {

    /**
     * decode方法：会根据接收的数据，被调用多次，直到确定没有新的元素被添加到List
     * ，或者是ByteBuf没有更多的可读字节为止
     * 如果List out不为空，就会将List的内容传递给下一个channelInboundHandler处理
     * <p>
     * ctx 上下文对象
     * in 入站的ByteBuf
     * out List集合，将解码后的数据传给下一个handler（解析出一个Long就像下一个handler传递处理了）
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        System.out.println("MyByteToLongDecoder decode 被调用");
        //因为long占8个字节，需要判断有8个字节，才能读取一个long
        if (byteBuf.readableBytes() >= 8) {
            //两端约定好协议 比如是一次读8 就把数据对齐到8的倍数
            //不够8的倍数就填充补齐 然后传过去的信息带有总共大小 和填充数据大小就行了
            list.add(byteBuf.readLong());
        }
    }
}
