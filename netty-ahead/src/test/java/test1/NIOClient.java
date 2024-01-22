package test1;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
	public static void main(String[] args) throws Exception {
		//得到一个网络通道
		SocketChannel socketChannel = SocketChannel.open();
		//设置非阻塞模式
		socketChannel.configureBlocking(false);
		//提供服务器端的ip和端口
		InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);
		
		//连接服务器
		if (!socketChannel.connect(inetSocketAddress)) {
			while(!socketChannel.finishConnect()) {
				System.out.println("因为连接需要时间，客户端不会阻塞，可以做其他工作");
			}
		}
		
		//如果连接成功，就发送数据
		String str = "111";
		//客户端也要关联buffer
		ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes("utf-8")); //根据字节数组产生buffer
		
		//Thread.sleep(30*1000); //模拟线程阻塞
		
		//发送数据
		//将buffer数据写入channel
		socketChannel.write(byteBuffer);
		
		//获取返回
		byteBuffer.clear();
		
		int numBytesRead;
		while ((numBytesRead = socketChannel.read(byteBuffer)) != -1) { //-1是读完
			if (numBytesRead == 0) { //0是读到0个
				if (byteBuffer.limit() == byteBuffer.position()) {
                    byteBuffer.clear();
                }
				continue;
			}
			
			//buffer是数组用这个
			//System.out.println("客户端收到：" + new String(byteBuffer.array(), byteBuffer.arrayOffset(), byteBuffer.arrayOffset()+byteBuffer.position(), "utf-8"));
			//单个buffer用这个
			System.out.println("客户端收到：" + new String(byteBuffer.array(), 0, byteBuffer.position(), "utf-8"));
		}
		
		System.out.println("断开连接...");
		socketChannel.close();
		
		//System.in.read();
		
	}
}