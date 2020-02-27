package simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    // ChannelHandlerContext是一个上下文,可以得到pipeline 还有 channel等等
    // Object是客户端发来的数据
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {

        // 普通任务队列
//        ctx.channel().eventLoop().execute(new Runnable() {
//            public void run() {
//                try {
//                    Thread.sleep(10 * 1000);
//                    ctx.writeAndFlush(Unpooled.copiedBuffer("休眠10秒钟的异步操作", CharsetUtil.UTF_8));
//                    System.out.println("啦啦啦");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        // 定时任务队列
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                ctx.writeAndFlush(Unpooled.copiedBuffer("休眠10秒钟的异步操作", CharsetUtil.UTF_8));
                System.out.println("啦啦啦");
            }
        }, 10, TimeUnit.SECONDS);


        System.out.println("server ctx = " + ctx);
        ByteBuf buf = (ByteBuf) msg; // netty将ByteBuffer再次封装,ByteBuf性能自然比ByteBuffer高
        System.out.println(buf.toString(CharsetUtil.UTF_8));
        System.out.println(ctx.channel().remoteAddress());
    }

    // 数据读取完毕后执行
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello", CharsetUtil.UTF_8));
    }

    // 处理异常，一般是需要关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
