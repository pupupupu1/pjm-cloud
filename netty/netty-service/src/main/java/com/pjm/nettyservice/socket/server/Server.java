package com.pjm.nettyservice.socket.server;

import com.pjm.nettyservice.socket.ServerMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.http.websocketx.WebSocket08FrameEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

// 服务端

public class Server {



    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossGroup, workerGroup)

                    .channel(NioServerSocketChannel.class)

                    .option(ChannelOption.SO_BACKLOG, 1024)

                    .handler(new LoggingHandler(LogLevel.INFO))

                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override

                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            // 添加用于处理粘包和拆包问题的处理器

                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));

                            pipeline.addLast(new LengthFieldPrepender(4));

                            // 添加自定义协议消息的编码和解码处理器

                            pipeline.addLast(new WebSocket08FrameEncoder(true));

                            pipeline.addLast(new WebSocket08FrameEncoder(true));

                            // 添加具体的消息处理器

                            pipeline.addLast(new ServerMessageHandler());

                        }

                    });



            ChannelFuture future = bootstrap.bind(8585).sync();

            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {

            e.printStackTrace();

        } finally {

            bossGroup.shutdownGracefully();

            workerGroup.shutdownGracefully();

        }

    }

}

