package com.pjm.quartzservice.util;

import com.alibaba.fastjson.JSON;
import com.pjm.quartzservice.entityExt.ScheduleJobExt;
import com.pjm.quartzservice.service.IScheduleJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Component("TestTask")
public class TestTask {
    @Autowired
    private IScheduleJobService scheduleJobService;
    public void test1() {
        System.out.println("hello task");
    }

    public void test2() {
        System.out.println("hello");
    }

    public void test3(String param) {
        Map<String,String> map= JSON.parseObject(param,Map.class);
        System.out.println(map);
        System.out.println(param);
    }
    public void test4(){
        System.out.println(scheduleJobService.listScheduleJob(new ScheduleJobExt(),0,0).getList().size());
    }
}
