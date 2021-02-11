package com.pjm.common.service.impl;

import com.pjm.common.service.AsyncService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncServiceImpl implements AsyncService {
    @Async
    @Override
    public void asyncInvoke(AsyncExec asyncExec) throws InterruptedException {
        asyncExec.exec();
    }
}