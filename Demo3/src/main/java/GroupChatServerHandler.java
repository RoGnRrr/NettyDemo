import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    // 定义一个channel组,管理所有channel
    // GlobalEventExecutor.INSTANCE 是全局的事件执行器,是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
        Channel channel = channelHandlerContext.channel();
        channelGroup.forEach(ch -> {
            if(channel != ch) {
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + "发送: " + s + "\n");
            } else {
                ch.writeAndFlush("[自己]" + channel.remoteAddress() + "发送: " + s + "\n");
            }
        });
    }

    // 连接建立时触发事件
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 将建立的连接加入管理组
        channelGroup.writeAndFlush("[客户端 " + simpleDateFormat.format(new Date()) + "]" + ctx.channel().remoteAddress() + " 加入聊天\n");
        channelGroup.add(ctx.channel());
    }

    // 连接断开时触发事件
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channelGroup.writeAndFlush("[自己 " + simpleDateFormat.format(new Date()) + "]" + ctx.channel().remoteAddress() + " 离开聊天\n");
    }

    // channel处于活动状态触发事件
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 上线了~");
    }

    // channel处于非活动状态触发事件
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + " 离线了~");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
