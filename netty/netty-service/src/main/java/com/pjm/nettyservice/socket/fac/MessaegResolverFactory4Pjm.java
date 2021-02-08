package com.pjm.nettyservice.socket.fac;

import com.pjm.nettyservice.socket.PjmMsgEntity;
import com.pjm.nettyservice.socket.resolver4pjm.Resolver4Pjm;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class MessaegResolverFactory4Pjm {
    private final List<Resolver4Pjm> resolvers = new LinkedList<>();


    public void registerResolver(Resolver4Pjm resolver) {

        resolvers.add(resolver);

    }



    // 根据解码后的消息，在工厂类处理器中查找可以处理当前消息的处理器

    public Resolver4Pjm getMessageResolver(PjmMsgEntity message) {

        for (Resolver4Pjm resolver : resolvers) {

            if (resolver.support(message)) {
                return resolver;
            }

        }



        throw new RuntimeException("cannot find resolver, message type: " + message.getAction());

    }
}
