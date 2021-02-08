package com.pjm.nettyservice.socket;

import com.alibaba.fastjson.JSON;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.UserUtil;
import com.pjm.nettyservice.socket.filter.GatewayLoginFilter;
import com.pjm.userapi.entity.ext.UserGroupMemberInfoApiExt;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
@ChannelHandler.Sharable
public class PjmSocketMsgHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    @Autowired
    private JedisUtil jedisUtil;
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

    public PjmSocketMsgHandler() {
    }

    @Override
    public void channelRead0(ChannelHandlerContext context, TextWebSocketFrame msg) throws Exception {

        log.info("收到客户端发来的一条信息，内容是{}", msg);
        //解析数据为websocket格式
        TextWebSocketFrame frame = msg;
        System.out.println(frame.text());
        ByteBuf byteBuf = frame.content();
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
        //获取channel以及sourceAccount,如果在map中对的上，就可以用,否则抛出异常

        //分析数据，发送消息，进入数据分析器
        if ("2".equals(pjmMsgEntity.getAction())) {
            //一对一消息
            String sourceAccount = pjmMsgEntity.getSourceAccount();
            Integer sourceChannenHshCode = USER_MAP.get(sourceAccount);
            if (Objects.isNull(sourceChannenHshCode)) {
                sourceChannenHshCode = context.channel().hashCode();
                log.info("无效用户");
//                context.channel().writeAndFlush(new TextWebSocketFrame("无效用户！！！"));
                context.close();
                return;
//                throw new Exception("sourceAccount不存在");
            }
            Channel sourceChannel = CHANNEL_MAP.get(sourceChannenHshCode);

            String receiveAccount = pjmMsgEntity.getReceiveAccount();
            Integer receiveChannelHashCode = USER_MAP.get(receiveAccount);
            if (Objects.isNull(receiveChannelHashCode)) {
                //转存入消息队列模块
                sourceChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(pjmMsgEntity)));
                log.info("消息存入消息队列,接收人是{}", receiveAccount);
                jedisUtil.rpush("pjm:im:receiveAccount:" + receiveAccount, JSON.toJSONString(pjmMsgEntity));
                return;
            }
            Channel receiveChannel = CHANNEL_MAP.get(receiveChannelHashCode);
            if (Objects.nonNull(receiveChannel)) {
                log.info("接收者{}在线", receiveAccount);
                ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                group.add(sourceChannel);
                group.add(receiveChannel);
                //直接将消息转发给此channelGroup
                group.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(pjmMsgEntity)));
                group.clear();
            }
        } else {
            //群内消息,usergroup,
            String sourceAccount = pjmMsgEntity.getSourceAccount();
            Integer sourceChannenHshCode = USER_MAP.get(sourceAccount);
            if (Objects.isNull(sourceChannenHshCode)) {
                sourceChannenHshCode = context.channel().hashCode();
                log.info("无效用户");
//                context.channel().writeAndFlush(new TextWebSocketFrame("无效用户！！！"));
                context.close();
                return;
//                throw new Exception("sourceAccount不存在");
            }
            Channel sourceChannel = CHANNEL_MAP.get(sourceChannenHshCode);

            String receiveAccount = pjmMsgEntity.getReceiveAccount();
            //从redis加载群员列表
            List<UserGroupMemberInfoApiExt> userGroupMemberInfoExts = JSON.parseArray(jedisUtil.getJson("cloud:cache:group:member:" + receiveAccount), UserGroupMemberInfoApiExt.class);
            if (Objects.isNull(userGroupMemberInfoExts)) {
                log.error("不存在这个群聊的群员列表信息，群id为：{}", receiveAccount);
                return;
            }
            List<String> userIds = userGroupMemberInfoExts.stream().map(item -> {
                return item.getUser().getUserAccount();
            }).collect(Collectors.toList());
//            if (Objects.isNull(userIds)) {
//                log.info("不存在这个群组");
//                return;
//            }
            ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            group.add(sourceChannel);
            userIds.forEach(item -> {
                Integer receiveChannelHashCode = USER_MAP.get(item);
                if (Objects.isNull(receiveChannelHashCode)) {
                    //转存入消息队列模块
                    log.info("消息存入消息队列,接收人是{}", item);
                    jedisUtil.rpush("pjm:im:receiveAccount:" + item, JSON.toJSONString(pjmMsgEntity));
                } else {
                    Channel receiveChannel = CHANNEL_MAP.get(receiveChannelHashCode);
                    if (Objects.nonNull(receiveChannel)) {
                        log.info("接收者{}在线", item);
                        group.add(receiveChannel);
                    }
                }

            });
            //直接将消息转发给此channelGroup
            group.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(pjmMsgEntity)));
            group.clear();
        }
    }

//    @Override
//    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
//
//    }


    @Override
    public void channelActive(ChannelHandlerContext context) {
        if (Objects.isNull(context)) {
            log.error("ChannelHandlerContext is null");
            return;
        }
        try {
            super.channelActive(context);
            log.info("channelActiveSuccesFul!!!!!!!!!!!!!!");
//            channelGroup.add(context.channel());
//            CHANNEL_MAP.put(context.channel().hashCode(), context.channel());
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
                context.close();
            }
        }
    }
}
