package com.pjm.nettyservice.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 服务器入口类
 */
@Slf4j
@Component
public class ServerMain {

    private static PjmSocketMsgHandler pjmSocketMsgHandler;
    private static PjmSocketNewHandler pjmSocketNewHandler;

    @Autowired
    private void setPjmSocketMsgHandler(PjmSocketMsgHandler pjmSocketMsgHandler, PjmSocketNewHandler pjmSocketNewHandler) {
        ServerMain.pjmSocketMsgHandler = pjmSocketMsgHandler;
        ServerMain.pjmSocketNewHandler = pjmSocketNewHandler;
    }

    //    @PostConstruct
    public void startImServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();     //拉客的group
        EventLoopGroup workGroup = new NioEventLoopGroup();     //干活的group

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workGroup);
        b.channel(NioServerSocketChannel.class);  //服务器信道的处理方式
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new HttpServerCodec(), // Http 服务器编解码器
                        new HttpObjectAggregator(65535), // 内容长度限制
                        new WebSocketServerProtocolHandler("/websocket", null,true,65535000,true,true), // WebSocket 协议处理器, 在这里处理握手、ping、pong 等消息
                        pjmSocketNewHandler // 自定义的消息处理器

                );
            }
        });

        b.option(ChannelOption.SO_BACKLOG, 128);
        b.childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            // 绑定 12345 端口,
            // 注意: 实际项目中会使用 argArray 中的参数来指定端口号
            ChannelFuture f = b.bind(1234).sync();

            if (f.isSuccess()) {
                log.info("服务器启动成功!");
            }

            // 等待服务器信道关闭,
            // 也就是不要立即退出应用程序, 让应用程序可以一直提供服务
            f.channel().closeFuture().sync();
        } catch (Exception ex) {
            //关闭服务器
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            log.error(ex.getMessage(), ex);
        }
        StringBuilder stringBuilder = new StringBuilder();
    }
    /**
     * 应用主函数
     * @param args 参数数组
     */
//    public static void main(String[] args) {
//
//    }
}

