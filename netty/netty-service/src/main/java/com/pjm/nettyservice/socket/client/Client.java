package com.pjm.nettyservice.socket.client;


import com.pjm.nettyservice.socket.MessageDecoder;
import com.pjm.nettyservice.socket.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;


public class Client {

    public static void main(String[] args) {

        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        try {

            bootstrap.group(group)

                    .channel(NioSocketChannel.class)

                    .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)

                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override

                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            // 添加用于解决粘包和拆包问题的处理器

                            pipeline.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));

                            pipeline.addLast(new LengthFieldPrepender(4));

                            // 添加用于进行心跳检测的处理器

                            pipeline.addLast(new IdleStateHandler(10, 12, 0));

                            // 添加用于根据自定义协议将消息与字节流进行相互转换的处理器

                            pipeline.addLast(new MessageEncoder());

                            pipeline.addLast(new MessageDecoder());

                            // 添加客户端消息处理器

                            pipeline.addLast(new ClientMessageHandler());

                        }

                    });
            ChannelFuture future = bootstrap.connect("127.0.0.1", 8585).sync();

            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {

            e.printStackTrace();

        } finally {

            group.shutdownGracefully();

        }

    }

}

