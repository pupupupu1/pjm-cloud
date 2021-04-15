package com.pjm.nettyservice.socket.resolver4pjm;

import com.alibaba.fastjson.JSON;
import com.pjm.common.util.JedisUtil;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.nettyservice.socket.MessageTypeEnum4Pjm;
import com.pjm.nettyservice.socket.PjmMsgEntity;
import com.pjm.nettyservice.socket.PjmSocketNewHandler;
import com.pjm.userapi.entity.UserApi;
import com.pjm.userapi.entity.ext.UserGroupMemberInfoApiExt;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GroupMessageResolver implements Resolver4Pjm {
    @Autowired
    private JedisUtil jedisUtil;
    @Autowired
    private UserClient userClient;

    @Override
    public boolean support(PjmMsgEntity message) {
        return message.getAction().equals(MessageTypeEnum4Pjm.GROUP.getType());
    }

    @Override
    public void resolve(ChannelHandlerContext context, PjmMsgEntity pjmMsgEntity) {
        //群内消息,usergroup,
        String sourceAccount = pjmMsgEntity.getSourceAccount();
        UserApi sourceUser = userClient.findUserByAccountOrTel(new UserApi().setUserAccount(sourceAccount)).getData();
        Map<String, String> header = pjmMsgEntity.getHeader();
        header.put("sourceUserInfo", JSON.toJSONString(sourceUser));
        log.info("获取到souceUserInfo:{}",sourceUser);
        pjmMsgEntity.setHeader(header);
        Integer sourceChannenHshCode = PjmSocketNewHandler.USER_MAP.get(sourceAccount);
        if (Objects.isNull(sourceChannenHshCode)) {
            log.info("无效用户");
            context.close();
            return;
//                throw new Exception("sourceAccount不存在");
        }
        pjmMsgEntity.getHeader().put("msgTime", String.valueOf(System.currentTimeMillis()));
        Channel sourceChannel = PjmSocketNewHandler.CHANNEL_MAP.get(sourceChannenHshCode);

        String receiveAccount = pjmMsgEntity.getReceiveAccount();
        //从redis加载群员列表
//        List<UserGroupMemberInfoApiExt> userGroupMemberInfoExts = JSON.parseArray(jedisUtil.getJson("cloud:cache:group:member:" + receiveAccount), UserGroupMemberInfoApiExt.class);
        List<UserGroupMemberInfoApiExt> userGroupMemberInfoExts = userClient.getUserListByGroupId(receiveAccount);
        if (Objects.isNull(userGroupMemberInfoExts)) {
            log.error("不存在这个群聊的群员列表信息，群id为：{},准备调用api刷新信息", receiveAccount);
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
            Integer receiveChannelHashCode = PjmSocketNewHandler.USER_MAP.get(item);
            if (Objects.isNull(receiveChannelHashCode)) {
                //转存入消息队列模块
                log.info("消息存入消息队列,接收人是{}", item);
                header.put("id", sourceAccount+UuidUtil.next());
                jedisUtil.rpush("pjm:im:receiveAccount:" + item, JSON.toJSONString(pjmMsgEntity));
            } else {
                Channel receiveChannel = PjmSocketNewHandler.CHANNEL_MAP.get(receiveChannelHashCode);
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
