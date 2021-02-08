package com.pjm.nettyservice.socket.resolver4pjm;

import com.alibaba.fastjson.JSON;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.nettyservice.socket.MessageTypeEnum4Pjm;
import com.pjm.nettyservice.socket.PjmMsgEntity;
import com.pjm.nettyservice.socket.PjmSocketNewHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
public class TalkCloseMessageResolver implements Resolver4Pjm {
    @Autowired
    private JedisUtil jedisUtil;

    @Override
    public boolean support(PjmMsgEntity message) {
        return message.getAction().equals(MessageTypeEnum4Pjm.TALK_CLOSE.getType());
    }

    @Override
    public void resolve(ChannelHandlerContext context, PjmMsgEntity message) {
        //首先获取到header中的accessToken以及sourceId和OptionId去redis中对比
        Map<String, String> header = message.getHeader();
        String sourceId = message.getSourceAccount();
        String optionId = message.getReceiveAccount();
        String token1 = (String) jedisUtil.getObject("pjm:im:talk:session:" + sourceId);
        String token2 = (String) jedisUtil.getObject("pjm:im:talk:session:" + optionId);
        if (!StringUtils.isEmpty(token1) && !StringUtils.isEmpty(token2) && token1.equals(token2)) {
            jedisUtil.delKey("pjm_cloud:talk:session:" + sourceId);
            jedisUtil.delKey("pjm_cloud:talk:session:" + optionId);
            PjmMsgEntity res = new PjmMsgEntity();
            res.setReceiveAccount(message.getReceiveAccount());
            res.setSourceAccount(message.getSourceAccount());
            res.setId(UuidUtil.next());
            res.setAction("6");
            res.setHeader(header);
            Integer optitonChannelHashCode = PjmSocketNewHandler.USER_MAP.get(optionId);
            Channel optionChannel = PjmSocketNewHandler.CHANNEL_MAP.get(optitonChannelHashCode);
            optionChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(res)));
        }

    }
}
