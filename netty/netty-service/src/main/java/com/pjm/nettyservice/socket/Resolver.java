package com.pjm.nettyservice.socket;

public interface Resolver {
    boolean support(Message message);
    Message resolve(Message message);
}
