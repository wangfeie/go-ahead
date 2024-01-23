package test2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Unpooled 操作 ByteBuf
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/1/21 19:45
 */
public class NettyByteBuf01 {

    public static void main(String[] args) {
        //创建一个ByteBuf
        //说明
        //1. 创建一个对象，该对象包含一个数组arr，是一个byte[10]
        //2. 在netty的buffer中，不需要使用flip进行反转
        //   是因为底层维护了readerIndex属性和writerIndex属性
        //3. 通过 readerIndex 和 writerIndex 和 capacity，将buffer分成了三段
        //   0 --> readerIndex：已经读取的区域
        //   readerIndex --> writerIndex：可读的区域
        //   writerIndex --> capacity：可写的区域
        ByteBuf buffer = Unpooled.buffer(10);

        for (int i = 0; i < 10; i++) {
            buffer.writeByte(i);
        }

        System.out.println("capacity=" + buffer.capacity()); //10
        //输出
        for (int i = 0; i < buffer.capacity(); i++) {
            System.out.println(buffer.readByte());
        }
    }

}
