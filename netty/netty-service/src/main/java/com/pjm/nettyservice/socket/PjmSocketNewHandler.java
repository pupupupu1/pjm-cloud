package com.pjm.nettyservice.socket;

import com.alibaba.fastjson.JSON;
import com.pjm.common.util.UserUtil;
import com.pjm.nettyservice.socket.fac.MessaegResolverFactory4Pjm;
import com.pjm.nettyservice.socket.filter.GatewayLoginFilter;
import com.pjm.nettyservice.socket.resolver4pjm.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ChannelHandler.Sharable
public class PjmSocketNewHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Autowired
    private MessaegResolverFactory4Pjm messaegResolverFactory4Pjm;
    @Autowired
    private EmptyMessageResolver emptyMessageResolver;
    @Autowired
    private TalkRequestMessageResolver talkRequestMessageResolver;
    @Autowired
    private PeopleMessageResolver peopleMessageResolver;
    @Autowired
    private GroupMessageResolver groupMessageResolver;
    @Autowired
    private TalkResponseMessageResolver talkResponseMessageResolver;
    @Autowired
    private TalkCloseMessageResolver talkCloseMessageResolver;
    @Autowired
    private SystemMessageResolver systemMessageResolver;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private GatewayLoginFilter loginFilter;
    //不使用private属性，需要定时器心跳判定离线在线,可以改为redis缓存
    public static final ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    //可以改为redis缓存
    public static final Map<Integer, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();
    //改为redis缓存
    public static final Map<String, Integer> USER_MAP = new ConcurrentHashMap<>();
    //群组map，可以改为redis二级缓存
    public static final Map<String, List<String>> userGroup = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        messaegResolverFactory4Pjm.registerResolver(emptyMessageResolver);
        messaegResolverFactory4Pjm.registerResolver(peopleMessageResolver);
        messaegResolverFactory4Pjm.registerResolver(groupMessageResolver);
        messaegResolverFactory4Pjm.registerResolver(talkRequestMessageResolver);
        messaegResolverFactory4Pjm.registerResolver(talkResponseMessageResolver);
        messaegResolverFactory4Pjm.registerResolver(talkCloseMessageResolver);
        messaegResolverFactory4Pjm.registerResolver(systemMessageResolver);
    }

    @Override
    public void channelRead0(ChannelHandlerContext context, TextWebSocketFrame msg) throws Exception {

        log.info("收到客户端发来的一条信息");
        //解析数据为websocket格式
        System.out.println(msg.text());
        ByteBuf byteBuf = msg.content();
        // 拿到真实的字节数组 并打印
        byte[] byteArray = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(byteArray);
        String req = new String(byteArray);
        System.out.println(req);
        PjmMsgEntity pjmMsgEntity;
        try {
            pjmMsgEntity = JSON.parseObject(req, PjmMsgEntity.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("解析数据异常");
        }
        Resolver4Pjm resolver = messaegResolverFactory4Pjm.getMessageResolver(pjmMsgEntity);
        resolver.resolve(context, pjmMsgEntity);
    }


    @Override
    public void channelActive(ChannelHandlerContext context) {
        if (Objects.isNull(context)) {
            log.error("ChannelHandlerContext is null");
            return;
        }
        try {
            super.channelActive(context);
            log.info("channelActiveSuccesFul!!!!!!!!!!!!!!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) {
        log.info("进入channel断开事件");
        //连接主动断开事件
        Channel channel = context.channel();
        channel.close();
        CHANNEL_GROUP.remove(channel);
        //还需要从usermap中移除此人的信息,通过hashcode?
        CHANNEL_MAP.remove(channel.hashCode());
        List<String> userIds = new ArrayList<>();
        USER_MAP.forEach((k, v) -> {
            if (v.equals(context.channel().hashCode())) {
                userIds.add(k);
            }
        });
        if (userIds.size() > 0) {
            userIds.forEach(USER_MAP::remove);
        }
        log.info("userMap,channelGroup,channelMap移除此用户{}", channel.hashCode());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext context, Object evt) {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            WebSocketServerProtocolHandler.HandshakeComplete complete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            String url0 = complete.requestUri();
            String url = url0.split("/")[2];
            log.info("尝试握手,token={}", url);
            try {
                loginFilter.doFilter(url);
                String account = userUtil.getAccount(url);
                Integer originalChannelHashCode = USER_MAP.get(account);
                if (Objects.nonNull(originalChannelHashCode)) {
                    //移除其中的channel
                    Channel originalChannel = CHANNEL_MAP.get(originalChannelHashCode);
                    CHANNEL_GROUP.remove(originalChannel);
                    if (Objects.nonNull(originalChannel)) {
                        CHANNEL_GROUP.remove(originalChannel);
//                        originalChannel.writeAndFlush(new TextWebSocketFrame("你已经被挤下去了！！"));
                        originalChannel.close();
                        log.info("移除了一个冲突会话");
                    }
                }
                USER_MAP.put(account, context.channel().hashCode());
                log.info("userMap添加账户{}成功", account);
                CHANNEL_GROUP.add(context.channel());
                log.info("channenGroup添加成功");
                CHANNEL_MAP.put(context.channel().hashCode(), context.channel());
                log.info("channelMap添加成功");
                PjmMsgEntity pjmMsgEntity = new PjmMsgEntity();
                pjmMsgEntity.setAction("2");
                pjmMsgEntity.setSourceAccount("system");
                pjmMsgEntity.setReceiveAccount(account);
                pjmMsgEntity.setMsgBody("hello world:" + account);
                Map<String, String> header = new HashMap<>();
                header.put("time", String.valueOf(System.currentTimeMillis()));
                pjmMsgEntity.setHeader(header);
                context.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(pjmMsgEntity)));
            } catch (Exception e) {
//                context.channel().writeAndFlush(new TextWebSocketFrame("无效密钥"));
                log.error("出现异常，{}", e);
                context.close();
            }
        }

    }
}
