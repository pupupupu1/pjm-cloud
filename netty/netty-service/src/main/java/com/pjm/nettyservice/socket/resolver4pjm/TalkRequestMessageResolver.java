package com.pjm.nettyservice.socket.resolver4pjm;

import com.alibaba.fastjson.JSON;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.nettyservice.socket.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class TalkRequestMessageResolver implements Resolver4Pjm {
    @Autowired
    private JedisUtil jedisUtil;

    @Override
    public boolean support(PjmMsgEntity message) {
        return message.getAction().equals(MessageTypeEnum4Pjm.TALK_REQUEST.getType());
    }

    @Override
    public void resolve(ChannelHandlerContext context, PjmMsgEntity message) {
        //只从header里面获取需要的东西
        Map<String, String> header = message.getHeader();
        //发起talk请求需要发起方的id和接收方的id（或者account）。并且接收方必须是发起方的好友，从redis中判别（set）

        String sourceId = message.getSourceAccount();
        String optionId = message.getReceiveAccount();
        //校验option是source的好友，如果通过，生成uuid，设置进入redis，进行报文返回(暂时不做好友校验)
        //校验此用户是否在线，不在线则返回对应消息并且关闭通过话请求
        boolean friendIsOnLine = PjmSocketNewHandler.USER_MAP.containsKey(optionId);
        PjmMsgEntity res = new PjmMsgEntity();
        res.setReceiveAccount(message.getReceiveAccount());
        res.setSourceAccount(message.getSourceAccount());
        res.setAction("4");

        res.setId(UuidUtil.next());

        //给发起方和接收方均发送消息，发起方进入等待接听界面，接收方显示视频来电界面
        Integer sourceChannelHashCode = PjmSocketNewHandler.USER_MAP.get(sourceId);
        Channel sourceChannel = PjmSocketNewHandler.CHANNEL_MAP.get(sourceChannelHashCode);
        if (friendIsOnLine) {
            //生成一个新的推流地址给发起方（使用accesstoken作为地址后缀）
            String talkAccessToken = UuidUtil.next();
            //设置会话状态进入redis,拨打时间为120秒(前端限制)
            jedisUtil.setObject("pjm:im:talk:session:" + sourceId, talkAccessToken);
            jedisUtil.setObject("pjm:im:talk:session:" + optionId, talkAccessToken);
            //token也是地址
            header.put("talkAccessToken", talkAccessToken);
            header.put("waitAccess", "1");
            res.setHeader(header);
            Integer optionChannelHashCode = PjmSocketNewHandler.USER_MAP.get(optionId);

            Channel optionChannel = PjmSocketNewHandler.CHANNEL_MAP.get(optionChannelHashCode);
            ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            group.add(sourceChannel);
            group.add(optionChannel);
            //直接将消息转发给此channelGroup
            group.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(res)));
            group.clear();
        } else {
            header.put("waitAccess", "0");
            res.setHeader(header);
            log.debug("准备发送视频请求失败消息，{}", JSON.toJSONString(res));
            sourceChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(res)));
        }
    }
}
