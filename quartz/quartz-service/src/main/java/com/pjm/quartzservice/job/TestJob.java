package com.pjm.quartzservice.job;

import com.pjm.nettyapi.service.NettyClientApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component(value = "testJob")
public class TestJob {
    @Resource
    private NettyClientApi nettyClientApi;
    public void test(){
        nettyClientApi.sendSyncAllOnlineUserLocMsg();
        log.info("hello test!!!!!!!!!!!!!!!!!!!");
    }
}
