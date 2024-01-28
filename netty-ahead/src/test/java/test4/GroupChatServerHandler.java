package test4;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

/**
 * @author wangfeie
 * @version 1.0.0
 * @date 2024/1/21 20:31
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    //定义一个channel组，管理所有的channel
    //GlobalEventExecutor.INSTANCE：是全局的事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //handlerAdded表示当连接建立，一旦连接，第一个被执行的方法
    //将当前channel加入到channelGroup
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();

        //将该加入聊天的信息推送给其他在线的客户端
        /**
         * 该方法会将channelGroup中所有的channel遍历，并发送消息
         * 我们不需要自己遍历
         * 在channelGroup.add之前就不会发给自己了
         */
        channelGroup.writeAndFlush(sdf.format(new java.util.Date()) + " [客户端] " + channel.remoteAddress() + " 加入聊天\n");

        //将该客户端加入channelGroup
        channelGroup.add(channel);
    }

    //断开连接会被触发，将xxx客户端离开信息推送给当前在线的客户
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush(sdf.format(new java.util.Date()) + " [客户端] " + channel.remoteAddress() + " 离开聊天\n");
        System.out.println("当前channelGroup大小：" + channelGroup.size());
    }

    //表示channel处于活动状态，提示xxx上线
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 上线了~");
    }

    //表示channel处于不活动状态，提示xxx离线了
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 离线了~");
    }

    //读取数据，业务处理
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //获取到当前channel
        Channel channel = ctx.channel();

        //这时我们遍历channelGroup，根据不同的情况，回送不同的消息
        channelGroup.forEach(ch -> {
            if (channel != ch) {
                //不是当前的channel，直接转发消息
                ch.writeAndFlush("[客户] " + channel.remoteAddress() + " 发送消息：" + msg + "\n");

            } else {
                //是自己，回显自己发送的消息给自己
                ch.writeAndFlush("[自己]发送了消息：" + msg + "\n");
            }
        });
    }

    //发生异常
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //关闭通道
        ctx.close();
    }
}
