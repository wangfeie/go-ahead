package test2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

/**
 * Unpooled 操作 ByteBuf
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/1/21 19:51
 */
public class NettyByteBuf02 {

    public static void main(String[] args) {
        //创建ByteBuf
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello, world!", Charset.forName("utf-8"));

        //使用相关的API
        if (byteBuf.hasArray()) {
            byte[] content = byteBuf.array();
            //将content转成字符串
            //trim()去掉结尾的0
            System.out.println(new String(content, Charset.forName("utf-8")).trim());
            System.out.println("byteBuf=" + byteBuf);

            System.out.println(byteBuf.arrayOffset()); //数组偏移量
            System.out.println(byteBuf.readerIndex());

            System.out.println(byteBuf.writerIndex());
            System.out.println(byteBuf.capacity());

            int len = byteBuf.readableBytes();
            System.out.println("len=" + len);

            //使用for取出各个字节
            for (int i = 0; i < len; i++) {
                System.out.println((char) byteBuf.getByte(i));
            }

            //从某个位置读取多少个长度
            System.out.println(byteBuf.getCharSequence(0, 4, Charset.forName("utf-8")));
            System.out.println(byteBuf.getCharSequence(4, 6, Charset.forName("utf-8")));
        }
    }

}
