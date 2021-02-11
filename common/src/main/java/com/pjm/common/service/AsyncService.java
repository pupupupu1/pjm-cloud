package com.pjm.common.service;

public interface AsyncService {
    void asyncInvoke(AsyncExec consumer) throws InterruptedException;

    @FunctionalInterface
    interface AsyncExec {
        void exec() throws InterruptedException;
    }
}
