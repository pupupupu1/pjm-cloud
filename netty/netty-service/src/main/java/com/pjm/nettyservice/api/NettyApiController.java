package com.pjm.nettyservice.api;

import com.alibaba.fastjson.JSON;
import com.pjm.common.aop.cache.EnableCache;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.nettyservice.socket.PjmMsgEntity;
import com.pjm.nettyservice.socket.PjmSocketNewHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("nettyApiClient")
public class NettyApiController {
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
}
