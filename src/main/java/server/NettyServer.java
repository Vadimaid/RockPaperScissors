package server;

import data.PlayerData;
import handler.ProcessingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import queue.CustomNonBlockingQueue;
import storage.PlayerChoiceStorage;

import java.nio.charset.StandardCharsets;

public class NettyServer {

    private final int PORT = 8080;

    private final CustomNonBlockingQueue<PlayerData> playerQueue;
    private final PlayerChoiceStorage playerChoiceStorage;

    public NettyServer(
            CustomNonBlockingQueue<PlayerData> playerQueue,
            PlayerChoiceStorage playerChoiceStorage
    ) {
        this.playerQueue = playerQueue;
        this.playerChoiceStorage = playerChoiceStorage;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(
                                    new LoggingHandler(LogLevel.INFO),
                                    new StringEncoder(StandardCharsets.UTF_8),
                                    new ProcessingHandler(playerQueue, playerChoiceStorage)
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            System.out.println("Netty server started on port: " + PORT);

            ChannelFuture future = bootstrap.bind(PORT).sync();
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
