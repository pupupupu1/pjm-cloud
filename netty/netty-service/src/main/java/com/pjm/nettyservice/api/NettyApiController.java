package com.pjm.nettyservice.api;

import com.alibaba.fastjson.JSON;
import com.pjm.common.aop.cache.EnableCache;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.util.JedisUtil;
import com.pjm.nettyservice.socket.PjmMsgEntity;
import com.pjm.nettyservice.socket.PjmSocketNewHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("nettyApiClient")
public class NettyApiController {
    @Autowired
    private JedisUtil jedisUtil;

    @ApiOperation("发送同步所有在线用户的地址进monggo的消息")
    @GetMapping("sendSyncAllOnlineUserLocMsg")
    public void sendSyncAllOnlineUserLocMsg() {
        log.info("准备发送同步所有在线用户的地址进monggo的消息数据");
        PjmMsgEntity msg = new PjmMsgEntity();
        msg.setAction("0");
        Map<String, String> header = new HashMap<>();
        header.put("type", "quartz_loc_update");
        msg.setHeader(header);
        PjmSocketNewHandler.CHANNEL_GROUP.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
        log.info("发送同步所有在线用户的地址进monggo的消息完毕");
    }

    @ApiOperation("推送用户有新的好友申请")
    @PostMapping("callUser4FriendAddRequest")
    public void callUser4FriendAddRequest(@RequestBody Map<String, String> info) {
        log.info("好友请求申请：{}", info);
        PjmMsgEntity msg = new PjmMsgEntity();
        msg.setAction("0");
        Map<String, String> header = new HashMap<>();
        header.put("type", "friend_add_request");
        msg.setHeader(header);
        msg.setMsgBody(info);
        Integer optionChannelHashCode = PjmSocketNewHandler.USER_MAP.get(info.get("optionUserAccount"));
        if (Objects.isNull(optionChannelHashCode)) {
            log.info("对方不在线，好友请求申请改为离线消息存储{}", msg);
            jedisUtil.rpush("pjm:im:receiveAccount:" + info.get("optionUserAccount"), JSON.toJSONString(msg));
            return;
        }
        log.info("对方在线，发送好友请求申请：{}", msg);
        PjmSocketNewHandler.CHANNEL_MAP.get(optionChannelHashCode).writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
    }

    @ApiOperation("推送用户有新的群聊申请")
    @PostMapping("callUser4GroupAddRequest")
    public void callUser4GroupAddRequest(@RequestBody Map<String, String> info) {
        List<String> optionUserAccountList = Arrays.asList(info.get("optionUserAccounts").split(";"));
        log.info("群聊请求申请：{}", info);
        optionUserAccountList.forEach(optionUserAccount -> {
            PjmMsgEntity msg = new PjmMsgEntity();
            msg.setAction("0");
            Map<String, String> header = new HashMap<>();
            header.put("type", "gourp_add_request");
            msg.setHeader(header);
            msg.setMsgBody(info);
            Integer optionChannelHashCode = PjmSocketNewHandler.USER_MAP.get(optionUserAccount);
            if (Objects.isNull(optionChannelHashCode)) {
                log.info("{}不在线，群聊请求申请改为离线消息存储{}", optionUserAccount, msg);
                jedisUtil.rpush("pjm:im:receiveAccount:" + info.get("optionUserAccount"), JSON.toJSONString(msg));
                return;
            } else {
                log.info("{}在线，群聊请求申请请求申请：{}", optionUserAccount, msg);
                PjmSocketNewHandler.CHANNEL_MAP.get(optionChannelHashCode).writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
            }

        });
    }

    @ApiOperation("推送用户有新的朋友圈评论消息")
    @PostMapping("callUser4CommentResponse")
    public void callUser4CommentResponse(@RequestBody Map<String, String> info) {
        log.info("新的朋友圈评论消息：{}", info);
        PjmMsgEntity msg = new PjmMsgEntity();
        msg.setAction("0");
        Map<String, String> header = new HashMap<>();
        header.put("type", "comment_response");
        msg.setHeader(header);
        msg.setMsgBody(info);
        Integer optionChannelHashCode = PjmSocketNewHandler.USER_MAP.get(info.get("optionUserAccount"));
        if (Objects.isNull(optionChannelHashCode)) {
            log.info("对方不在线，新的朋友圈评论消息改为离线消息存储{}", msg);
            jedisUtil.rpush("pjm:im:receiveAccount:" + info.get("optionUserAccount"), JSON.toJSONString(msg));
            return;
        }
        log.info("对方在线，发送新的朋友圈评论消息：{}", msg);
        PjmSocketNewHandler.CHANNEL_MAP.get(optionChannelHashCode).writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
    }
}
