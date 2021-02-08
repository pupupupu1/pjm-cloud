package com.pjm.nettyservice.socket.resolver4pjm;

import com.pjm.nettyservice.socket.MessageTypeEnum4Pjm;
import com.pjm.nettyservice.socket.PjmMsgEntity;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class EmptyMessageResolver implements Resolver4Pjm {
    @Override
    public boolean support(PjmMsgEntity message) {
        return message.getAction().equals(MessageTypeEnum4Pjm.EMPTY.getType());
    }

    @Override
    public void resolve(ChannelHandlerContext context, PjmMsgEntity message) {

    }
}
