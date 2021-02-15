package com.pjm.nettyservice.socket.resolver4pjm;

import com.alibaba.fastjson.JSON;
import com.pjm.common.util.JedisUtil;
import com.pjm.nettyservice.socket.MessageTypeEnum4Pjm;
import com.pjm.nettyservice.socket.PjmMsgEntity;
import com.pjm.nettyservice.socket.PjmSocketNewHandler;
import com.pjm.userapi.entity.UserApi;
import com.pjm.userapi.service.UserClient;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class PeopleMessageResolver implements Resolver4Pjm {
    @Autowired
    private JedisUtil jedisUtil;
    @Resource
    private UserClient userClient;

    @Override
    public boolean support(PjmMsgEntity message) {
        return message.getAction().equals(MessageTypeEnum4Pjm.PEOPLE.getType());
    }

    @Override
    public void resolve(ChannelHandlerContext context, PjmMsgEntity pjmMsgEntity) {
        //一对一消息
        String sourceAccount = pjmMsgEntity.getSourceAccount();
        Integer sourceChannenHshCode = PjmSocketNewHandler.USER_MAP.get(sourceAccount);
        if (Objects.isNull(sourceChannenHshCode)) {
            sourceChannenHshCode = context.channel().hashCode();
            log.info("无效用户");
            context.close();
            return;
//                throw new Exception("sourceAccount不存在");
        }
        pjmMsgEntity.getHeader().put("msgTime", String.valueOf(System.currentTimeMillis()));
        Channel sourceChannel = PjmSocketNewHandler.CHANNEL_MAP.get(sourceChannenHshCode);

        String receiveAccount = pjmMsgEntity.getReceiveAccount();
        Integer receiveChannelHashCode = PjmSocketNewHandler.USER_MAP.get(receiveAccount);
//        UserApi optionUser = userClient.findUserByAccountOrTel(new UserApi().setUserAccount(receiveAccount)).getData();
        UserApi sourceUser = userClient.findUserByAccountOrTel(new UserApi().setUserAccount(sourceAccount)).getData();
        Map<String,String> header=pjmMsgEntity.getHeader();
//        header.put("optionUserInfo",JSON.toJSONString(optionUser));
        header.put("sourceUserInfo",JSON.toJSONString(sourceUser));
        if (Objects.isNull(receiveChannelHashCode)) {
            //转存入消息队列模块
            sourceChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(pjmMsgEntity)));
            log.info("消息存入消息队列,接收人是{}", receiveAccount);
            jedisUtil.rpush("pjm:im:receiveAccount:" + receiveAccount, JSON.toJSONString(pjmMsgEntity));
            return;
        }
        Channel receiveChannel = PjmSocketNewHandler.CHANNEL_MAP.get(receiveChannelHashCode);
        if (Objects.nonNull(receiveChannel)) {
            log.info("接收者{}在线", receiveAccount);
            ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            group.add(sourceChannel);
            group.add(receiveChannel);
            //直接将消息转发给此channelGroup
            group.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(pjmMsgEntity)));
            group.clear();
        }
    }
}
