package com.pjm.nettyservice.socket;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义消息处理器
 */
@Slf4j
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {

    static private final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    static private final Map<String, Channel> userMap = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext context) throws Exception {
        if (Objects.isNull(context)) {
            log.error("ChannelHandlerContext is null");
            return;
        }
        try {
            super.channelActive(context);
            log.info("channelActiveSuccesFul!!!!!!!!!!!!!!");
            channelGroup.add(context.channel());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        log.info("收到客户端消息, msgClzz={},msgBody = {}", msg.getClass().getName(), msg);
        // WebSocket 二进制消息会通过 HttpServerCodec 解码成 BinaryWebSocketFrame 类对象
//        BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
        TextWebSocketFrame frame = (TextWebSocketFrame) msg;
        System.out.println(frame.text());
        ByteBuf byteBuf = frame.content();
        // 拿到真实的字节数组 并打印
        byte[] byteArray = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(byteArray);

//        for (byte b : byteArray) {
//            System.out.print(b);
//            System.out.print(", ");
//        }
        String req = new String(byteArray);
        MessageBody reqBody = JSON.parseObject(req, MessageBody.class);
        //判断msg中的action
        if ("1".equals(reqBody.getAction())) {
            log.info("进入了新角色");
            //删除大组中的channel
            Channel channel=userMap.get(reqBody.getName());
            if (Objects.nonNull(channel)){
                channelGroup.remove(channel);
                channel.closeFuture();
            }
            userMap.put(reqBody.getName(),channelHandlerContext.channel());
        }
        System.out.println(reqBody);
        System.out.println("开始发送消息");
//        channelGroup.writeAndFlush(userMap);
//        channelGroup.flush();
        ChannelGroup channels=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        //判断usermap中是否存在此人。
        userMap.forEach((k,v)->{
            if (reqBody.getName().equals(k)){
                v.writeAndFlush(new TextWebSocketFrame("欢迎你！！！！！！！！！！！！"));
            }else {
                channels.add(v);
            }
        });
        if (channels.size()>0){
            channels.writeAndFlush(new TextWebSocketFrame(reqBody.getName()+"进入了游戏！"));
        }
        System.out.println(userMap.keySet());
//        channelGroup.writeAndFlush(new TextWebSocketFrame(reqBody.getName()+"来了"));
//        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("欢迎你"));
    }
}
