package http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

// 客户端和服务端相互通讯的数据被封装成HttpObject
public class TestServerHandler extends SimpleChannelInboundHandler<HttpObject> {


    // 读取客户端数据
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {

        if(httpObject instanceof HttpRequest) {



            HttpRequest httpRequest = (HttpRequest) httpObject;

            if("/favicon.ico".equals(httpRequest.uri())) {
                return;
            }

            System.out.println("httpObject类型: " + httpObject.getClass());
            System.out.println("客户端地址: " + channelHandlerContext.channel().remoteAddress());

            ByteBuf byteBuf = Unpooled.copiedBuffer("Hello world.", CharsetUtil.UTF_8);

            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

            channelHandlerContext.writeAndFlush(httpResponse);

        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
