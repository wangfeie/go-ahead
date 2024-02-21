package test8;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.Random;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    //当通道就绪时，就会触发该方法
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //随机的发送Student或者Worker对象
        int i = new Random().nextInt(3);
        MyDataInfo.MyMessage myMessage = null;
        if (0 == i) {
            myMessage = MyDataInfo.MyMessage.newBuilder().setDataType(MyDataInfo.MyMessage.DataType.StudentType)
                    .setStudent(
                            MyDataInfo.MyMessage.Student.newBuilder().setId(10).setName("张三").build()
                    ).build();

        } else {
            //发送一个Worker对象
            myMessage = MyDataInfo.MyMessage.newBuilder().setDataType(MyDataInfo.MyMessage.DataType.WorkerType)
                    .setWorker(
                            MyDataInfo.MyMessage.Worker.newBuilder().setAge(20).setName("李四").build()
                    ).build();
        }

        ctx.writeAndFlush(myMessage);
    }

    //当通道有读取事件时会触发
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("服务器回复的消息：" + buf.toString(CharsetUtil.UTF_8));
        System.out.println("服务器的地址：" + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}