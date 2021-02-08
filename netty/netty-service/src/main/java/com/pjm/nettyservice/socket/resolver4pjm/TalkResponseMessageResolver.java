package com.pjm.nettyservice.socket.resolver4pjm;

import com.alibaba.fastjson.JSON;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.nettyservice.socket.MessageTypeEnum4Pjm;
import com.pjm.nettyservice.socket.PjmMsgEntity;
import com.pjm.nettyservice.socket.PjmSocketNewHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Component
public class TalkResponseMessageResolver implements Resolver4Pjm {
    @Autowired
    private JedisUtil jedisUtil;

    @Override
    public boolean support(PjmMsgEntity message) {
        return message.getAction().equals(MessageTypeEnum4Pjm.TALK_RESPONSE.getType());
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
            //由条件判断这个响应请求是响应对人了，是响应方发出来的。
            //获取header中的access头，如果为0，不允许，编写拒绝报文给发起方，并且删除redis中的缓存
            String isAccess = header.get("isAccess");
            PjmMsgEntity res = new PjmMsgEntity();
            res.setId(UuidUtil.next());
            res.setAction("5");
            res.setHeader(header);
            res.setReceiveAccount(message.getReceiveAccount());
            res.setSourceAccount(message.getSourceAccount());
            //拒绝了
            if ("1".equals(isAccess)) {
                jedisUtil.delKey("pjm_cloud:talk:session:" + sourceId);
                jedisUtil.delKey("pjm_cloud:talk:session:" + optionId);

                Integer sourceChannelHashCode = PjmSocketNewHandler.USER_MAP.get(sourceId);
                Channel sourceChannel = PjmSocketNewHandler.CHANNEL_MAP.get(sourceChannelHashCode);
                sourceChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(res)));
            } else {
                //同意开启视频
                //被邀请方生成的url后缀为邀请方的加上option字符串（默认约定）
                //给发起方和接收方均发送消息，发起方进入等待接听界面，接收方显示视频来电界面
                Integer sourceChannelHashCode = PjmSocketNewHandler.USER_MAP.get(sourceId);
                Integer optionChannelHashCode = PjmSocketNewHandler.USER_MAP.get(optionId);
                Channel sourceChannel = PjmSocketNewHandler.CHANNEL_MAP.get(sourceChannelHashCode);
                Channel optionChannel = PjmSocketNewHandler.CHANNEL_MAP.get(optionChannelHashCode);
                ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
                group.add(sourceChannel);
                group.add(optionChannel);
                //直接将消息转发给此channelGroup
                group.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(res)));
                group.clear();
            }
        }

    }
}
