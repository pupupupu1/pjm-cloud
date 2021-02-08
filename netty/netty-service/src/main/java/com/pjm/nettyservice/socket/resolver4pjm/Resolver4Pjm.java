package com.pjm.nettyservice.socket.resolver4pjm;

import com.pjm.nettyservice.socket.PjmMsgEntity;
import io.netty.channel.ChannelHandlerContext;

public interface Resolver4Pjm {
    boolean support(PjmMsgEntity message);

    void resolve(ChannelHandlerContext context, PjmMsgEntity message);
}
