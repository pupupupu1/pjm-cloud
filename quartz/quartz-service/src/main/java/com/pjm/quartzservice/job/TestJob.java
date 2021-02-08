package com.pjm.quartzservice.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component(value = "testJob")
public class TestJob {
    public void test(){
        log.info("hello test!!!!!!!!!!!!!!!!!!!");
    }
}
