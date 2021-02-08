package com.pjm.quartzservice.service;

import com.baomidou.mybatisplus.service.IService;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.quartzservice.entity.ScheduleJob;
import com.pjm.quartzservice.entityExt.ScheduleJobExt;
import org.quartz.SchedulerException;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author pjm
 * @since 2020-05-25
 */
public interface IScheduleJobService extends IService<ScheduleJob> {
    ResponseEntity<ScheduleJob> insertScheduleJob(ScheduleJob scheduleJob) throws SchedulerException;

    ResponseEntity<ScheduleJob> updateScheduleJob(ScheduleJob scheduleJob) throws SchedulerException;

    ResponseEntity<ScheduleJob> deleteScheduleJob(ScheduleJob scheduleJob) throws SchedulerException;

    PageVo<List<ScheduleJob>> listScheduleJob(ScheduleJobExt scheduleJobExt, Integer pageNum, Integer pageSize);

    /**
     * 立即执行
     */
    void run(List<String> jobIds) throws SchedulerException;

    /**
     * 暂停运行
     */
    void pause(List<String> jobIds) throws SchedulerException;

    /**
     * 恢复运行
     */
    void resume(List<String> jobIds) throws SchedulerException;

    /**
     * 任务交替
     */
    void jobChange(ScheduleJob mainjob, ScheduleJob sidejob) throws SchedulerException;
}
