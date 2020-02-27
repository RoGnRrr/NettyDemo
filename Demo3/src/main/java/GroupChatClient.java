import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class GroupChatClient {

    private final String host;
    private final int port;


    public GroupChatClient(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public void run() throws Exception {

        EventLoopGroup eventExecutors = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                     @Override
                     protected void initChannel(SocketChannel socketChannel) {
                         // 获取pipeline
                         socketChannel.pipeline().addLast("decoder", new StringDecoder()); // 向pipeline加入一个解码器
                         socketChannel.pipeline().addLast("encoder", new StringEncoder()); // 向pipeline加入一个编码器
                         socketChannel.pipeline().addLast(new GroupChatClientHandler());
                     }
                 }
            );

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String s = scanner.nextLine();
                channelFuture.channel().writeAndFlush(s);
            }

            channelFuture.channel().closeFuture().sync();

        } finally {
            eventExecutors.shutdownGracefully();
        }


    }


    public static void main(String[] args) throws Exception {

        GroupChatClient groupChatClient = new GroupChatClient("127.0.0.1", 1005);
        groupChatClient.run();

    }

}
