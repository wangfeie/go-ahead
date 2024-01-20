import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Nio Channel
 *
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/1/20 21:40
 */
public class NioChannelTest {

    public static void main(String[] args) throws IOException {
//        test1();
//        test2();
//        test3();
        test4();
    }

    /**
     * 本地文件写数据
     */
    public static void test1() throws IOException {
        String str = "hello，你好";
        //创建一个输出流->包装到channel中
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\1.txt");

        //通过fileOutputStream输出流获取对应的FileChannel
        //这个fileChannel真实类型是FileChannelImpl
        FileChannel fileChannel = fileOutputStream.getChannel();

        //创建一个缓冲区ByteBuffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //将str放入到byteBuffer中
        byteBuffer.put(str.getBytes());

        //对byteBuffer进行flip
        byteBuffer.flip();

        //将byteBuffer里的数据，写入到fileChannel
        fileChannel.write(byteBuffer);

        //关闭流
        fileOutputStream.close();
    }

    /**
     * 本地文件读数据
     */
    public static void test2() throws IOException {
        //创建文件的输入流
        File file = new File("C:\\Users\\Administrator\\Desktop\\1.txt");
        FileInputStream fileInputStream = new FileInputStream(file);

        //通过fileInputStream获取对应的FileChannel -> 实际类型FileChannelImpl
        FileChannel fileChannel = fileInputStream.getChannel();

        //创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        //将通道的数据读入到byteBuffer中
        fileChannel.read(byteBuffer);

        //将byteBuffer的字节数据转成String
        System.out.println(new String(byteBuffer.array())); //返回buffer中的字节数组hb

        //关闭流
        fileInputStream.close();
    }


    /**
     * 使用一个Buffer完成文件读取
     */
    public static void test3() throws IOException {
//创建文件的输入流
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\1.txt");
        //获取输入流对象的channel
        FileChannel fileChannel01 = fileInputStream.getChannel();

        //文件输出流对象
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\2.txt");
        //获取输入流对象的channel
        FileChannel fileChannel02 = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (true) {
            //读之前有个重要操作，一定不要忘了
            byteBuffer.clear(); //复位：The position is set to zero, the limit is set to the capacity, and the mark is discarded

            //循环读取
            int read = fileChannel01.read(byteBuffer);
            System.out.println("read = " + read);
            if (read == -1) {
                //表示读完
                break;
            }

            //读写切换
            byteBuffer.flip();

            //将buffer中的数据写入到fileChannel02
            fileChannel02.write(byteBuffer);
        }

        //关闭相关的流
        fileInputStream.close();
        fileOutputStream.close();
    }


    public static void test4() throws IOException {
        //创建输入流
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\Administrator\\Desktop\\1.jpg");
        //创建输出流
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\2.jpg");

        //获取各个流对应的fileChannel
        FileChannel source = fileInputStream.getChannel();
        FileChannel dest = fileOutputStream.getChannel();

        //使用transferFrom完成拷贝
        dest.transferFrom(source, 0, source.size());

        //关闭通道和流
        source.close();
        dest.close();
        fileInputStream.close();
        fileOutputStream.close();
    }


}
