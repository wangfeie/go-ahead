package test1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
	public static void main(String[] args) throws Exception {
		//创建ServerSocketChannel -> 类似于ServerSocket
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		
		//得到一个Selector对象
		Selector selector = Selector.open();
		
		//绑定一个端口6666，在服务器端监听
		serverSocketChannel.socket().bind(new InetSocketAddress(6666));
		//设置为非阻塞
		serverSocketChannel.configureBlocking(false);
		
		//把serverSocketChannel注册到selector，关心事件为OP_ACCEPT
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		//循环等待客户端连接
		while(true) {
			if (selector.select(5*1000) == 0) {
				//等待5秒钟，如果没有事件发生，返回
				System.out.println("服务器等待了5秒，无连接");
				continue;
			}
			
			//如果返回的>0，就获取到相关的selectionKey集合
			//1.如果返回的>0，表示已经获取到关注的事件
			//2.selector.selectedKeys()返回关注的集合
			//3.通过selectionKeys反向获取通道
			Set<SelectionKey> selectionKeys = selector.selectedKeys();
			
			//遍历集合
			Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
			
			while (keyIterator.hasNext()) {
				//获取到SelectionKey
				SelectionKey key = keyIterator.next();
				
				//根据key，对应的通道发生的事件，做相应的处理
				if (key.isAcceptable()) {
					//如果是OP_ACCEPT，有新的客户端连接
					//给该客户端生成一个SocketChannel
					SocketChannel socketChannel = serverSocketChannel.accept();
					//将socketChannel设置为非阻塞模式
					socketChannel.configureBlocking(false);
					//将客户端的socketChannel也注册到selector，关注事件为SelectionKey.OP_READ
					//同时给该socketChannel关联一个buffer
					socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(10240));
					
					System.out.println("from " + socketChannel.hashCode() + " 客户端建立了连接");
					
				} else if (key.isReadable()) {
					//发生OP_READ
					//通过key反向获取到对应的channel
					SocketChannel socketChannel = (SocketChannel)key.channel();
					//获取到该channel关联的buffer
					ByteBuffer byteBuffer = (ByteBuffer)key.attachment();
					
					byteBuffer.clear();
					//把当前通道的数据读到buffer中
					try {
						if (socketChannel.read(byteBuffer) == -1) {
							System.out.println("from " + socketChannel.hashCode() + " 客户端断开了连接");
							keyIterator.remove();
							socketChannel.close();
							continue;
						}
						
						//解析客户端数据
						//socket通讯格式可以自己定义：4字节报文长度+报文体
						String request = new String(byteBuffer.array(), 0, byteBuffer.position(), "utf-8");
						System.out.println("from " + socketChannel.hashCode() + " 客户端：" + request);
						
						
						//----------------------------------------------------------------//
						/**
						 * 业务模块处理（能否做成异步，传入socketChannel对象）主线程只负责网络IO读写
						 */
						
						//业务处理
						System.out.println("业务处理...");
						
						//使用新buffer返回
						ByteBuffer newByteBuffer = ByteBuffer.allocate(10240);
						newByteBuffer.clear();
						
						//获取返回数据
						String response = "result ok";
						newByteBuffer.put(response.getBytes("utf-8"));
						newByteBuffer.put((byte)'9');
						
						//注册写事件
						socketChannel.register(selector, SelectionKey.OP_WRITE, newByteBuffer);
						//----------------------------------------------------------------//
						
						
					} catch (IOException ioe) {
						ioe.printStackTrace();
						keyIterator.remove();
						socketChannel.close();
						continue;
					}
					
				} else if (key.isWritable()) {
					//通过key反向获取到对应的channel
					SocketChannel socketChannel = (SocketChannel)key.channel();
					//获取到该channel关联的buffer
					ByteBuffer byteBuffer = (ByteBuffer)key.attachment();
					
					//读写切换
					byteBuffer.flip(); //只有buffer出数据需要切换
					
					//把buffer数据写入到channel
					try {
						socketChannel.write(byteBuffer);
					} catch (IOException ioe) {
						ioe.printStackTrace();
						keyIterator.remove();
						socketChannel.close();
						continue;
					}
					
					String response = new String(byteBuffer.array(), 0, byteBuffer.position(), "utf-8");
					System.out.println("to " + socketChannel.hashCode() + " 服务端：" + response);
					
					//如果长连接继续注册事件等待
					//注册读事件
					//socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(10240));
					//如果短连接则断开
					socketChannel.close();
					
				} else if (key.isConnectable()) {
					System.out.println("Connectable");
				} else if (key.isValid()) {
					System.out.println("Valid");
				}
				
				//手动从集合中移除当前的SelectionKey，防止重复操作
				keyIterator.remove();
				
			}
			
		}
		
	}
}