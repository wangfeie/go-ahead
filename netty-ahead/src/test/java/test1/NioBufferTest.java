package test1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * Nio Buffer
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/1/20 21:14
 */
public class NioBufferTest {

    public static void main(String[] args) throws IOException {
//        test1();
//        test2();
//        test3();
        test4();
    }

    /**
     * 测试 Buffer 的 flip()
     * <p>
     * public final Buffer flip() {
     * limit = position;
     * position = 0;
     * mark = -1;
     * return this;
     * }
     */
    public static void test1() {
        //创建一个Buffer，大小为5，即可以存放5个int
        IntBuffer intBuffer = IntBuffer.allocate(5);

        //向Buffer存放数据
        //intBuffer.capacity：buffer的容量
        for (int i = 0; i < intBuffer.capacity(); i++) {
            intBuffer.put(i * 2);
        }

        //如何从buffer读取数据
        //将buffer转换，读写切换
        intBuffer.flip();

        while (intBuffer.hasRemaining()) {  //是否有未读数据
            System.out.println(intBuffer.get());  //通过buffer的index获取数据
        }
    }

    /**
     * ByteBuffer支持类型化的put和get，put放入的是什么数据类型，get就应该使用相应的数据类型来取出，否则可能有BufferUnderflowException异常；
     */
    public static void test2() {
        //创建一个Buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //类型化方式放入数据
        byteBuffer.putInt(10);
        byteBuffer.putLong(9L);
        byteBuffer.putChar('上');
        byteBuffer.putShort((short) 1);

        //取出
        byteBuffer.flip();

        System.out.println(byteBuffer.getInt());
        System.out.println(byteBuffer.getLong());
        System.out.println(byteBuffer.getChar());
        System.out.println(byteBuffer.getLong());  //抛异常
    }


    /**
     * 可以将一个普通的Buffer转成只读Buffer；
     */
    public static void test3() {
        //创建一个Buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        for (int i = 0; i < 1024; i++) {
            byteBuffer.put((byte) i);
        }

        //切换
        byteBuffer.flip();

        //得到一个只读的buffer
        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
        System.out.println(readOnlyBuffer.getClass());

        //读取
        while (readOnlyBuffer.hasRemaining()) {
            System.out.println(readOnlyBuffer.get());
        }

        //写入
        readOnlyBuffer.put((byte) 1);  //抛异常
    }


    /**
     * NIO还提供了MappedByteBuffer，可以让文件直接在内存（堆外的内存）中进行修改，操作系统不需要拷贝一次，而如何同步到文件由NIO来完成
     */
    public static void test4() throws IOException {
        //修改文件
        RandomAccessFile randomAccessFile = new RandomAccessFile("C:\\Users\\Administrator\\Desktop\\1.txt", "rw");
        //获取对应的通道
        FileChannel fileChannel = randomAccessFile.getChannel();

        /**
         * MapMode mode：使用的模式
         * long position：可以直接修改的起始位置
         * long size：是映射到内存的大小（最多可以映射多少大小），即将文件file01.txt的多少个字节映射到内存
         * 可以直接修改的范围就是0-5（不包含5）
         * 实际类型是DirectByteBuffer
         */
        MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 10);
        mappedByteBuffer.put(0, (byte) 'H');
        mappedByteBuffer.put(3, (byte) '9');
        mappedByteBuffer.put(6, (byte) '9');
        mappedByteBuffer.put(9, (byte) '9');

        //关闭文件
        randomAccessFile.close();


    }

    /**
     * NIO还支持通过多个Buffer（即Buffer数组）完成读写操作，即Scattering和Gatering；
     */
    public static void test5() throws IOException {
        //使用ServerSocketChannel和SocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //创建server的address
        InetSocketAddress inetSocketAddress = new InetSocketAddress(7000);
        //绑定端口到socket，并启动
        serverSocketChannel.socket().bind(inetSocketAddress);

        //创建buffer数组
        ByteBuffer[] byteBuffers = new ByteBuffer[2];
        byteBuffers[0] = ByteBuffer.allocate(5); //第一个分配5个字节
        byteBuffers[1] = ByteBuffer.allocate(3); //第二个分配3个字节

        //等待客户端连接telnet
        SocketChannel socketChannel = serverSocketChannel.accept();

        //假定从客户端接收8个字节
        int messageLength = 8;

        //循环的读取
        while (true) {
            //统计读了多少个字节
            int byteRead = 0;

            while (byteRead < messageLength) {
                long l = socketChannel.read(byteBuffers); //返回读取到的个数，会自动处理数组
                byteRead += l; //累计读取到的字节数
                System.out.println("byteRead=" + byteRead);
                //使用流打印，看看当前的buffer的position和limit
                Arrays.asList(byteBuffers).stream().map(buffer -> "position=" + buffer.position()
                        + ", limit=" + buffer.limit()).forEach(System.out::println);
            }

            //将所有的buffer进行flip
            //Arrays.asList(byteBuffers).stream().map(buffer -> buffer.flip());
            Arrays.asList(byteBuffers).stream().forEach(buffer -> buffer.flip()); //注意stream().map遍历不会改变原来的值

            //将数据读出显示到客户端
            long byteWrite = 0;
            while (byteWrite < messageLength) {
                long l = socketChannel.write(byteBuffers);
                byteWrite += l;
            }

            //将所有的buffer进行clear
            Arrays.asList(byteBuffers).stream().forEach(buffer -> buffer.clear());

            System.out.println("byteRead=" + byteRead + ", byteWrite=" + byteWrite
                    + ", messageLength=" + messageLength);
        }

    }

}
