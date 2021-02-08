package com.pjm.quartzservice.componment;

//import com.example.dynamicschedule.service.ScheduleJobLogService;

import com.pjm.quartzservice.entity.ScheduleJob;
import com.pjm.quartzservice.mapper.ScheduleJobMapper;
import com.pjm.quartzservice.util.Constant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * 定时任务工具
 */
@Slf4j
@Component
public class ScheduleJobUtils extends QuartzJobBean {

    private ExecutorService service = Executors.newSingleThreadExecutor();

    @Autowired
    private ScheduleJobMapper scheduleJobLogMapper;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        ScheduleJob scheduleJob = (ScheduleJob) context.getMergedJobDataMap()
                .get(Constant.JOB_PARAM_KEY);

        //获取spring bean
//        ScheduleJobLogService scheduleJobLogService = (ScheduleJobLogService) SpringContextUtils.getBean("scheduleJobLogService");


        //任务开始时间
        long startTime = System.currentTimeMillis();
        Byte zero = 0;

        Byte one = 1;
        try {
            //执行任务
            log.info("任务准备执行，任务ID：" + scheduleJob.getId());
            ScheduleRunnable task = new ScheduleRunnable(scheduleJob.getClassPath(),
                    scheduleJob.getMethodName(), scheduleJob.getParams());
            Future<?> future = service.submit(task);
            future.get();
            //任务执行总时长
            long times = System.currentTimeMillis() - startTime;
            scheduleJob.setTimeConsuming(times + "");
            scheduleJob.setLastExecutionTime(new Date());
            log.info("任务执行完毕，任务ID：" + scheduleJob.getId() + "  总共耗时：" + times + "毫秒");
        } catch (Exception e) {
            log.error("任务执行失败，任务ID：" + scheduleJob.getId(), e);

            //任务执行总时长
            long times = System.currentTimeMillis() - startTime;

            //任务状态    0：成功    1：失败
        } finally {
            scheduleJob.setStatus(Constant.NORMAL);
            scheduleJobLogMapper.updateById(scheduleJob);
        }
    }
}



