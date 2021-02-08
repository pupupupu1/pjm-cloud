package com.pjm.nettyservice.socket.resolver;


// ping消息处理器

import com.pjm.nettyservice.socket.Message;
import com.pjm.nettyservice.socket.MessageTypeEnum;
import com.pjm.nettyservice.socket.Resolver;

public class PingMessageResolver implements Resolver {



    @Override

    public boolean support(Message message) {

        return message.getMessageType() == MessageTypeEnum.PING;

    }



    @Override

    public Message resolve(Message message) {

        // 接收到ping消息后，返回一个pong消息返回

        System.out.println("receive ping message: " + System.currentTimeMillis());

        Message pong = new Message();

        pong.setMessageType(MessageTypeEnum.PONG);

        return pong;

    }

}

