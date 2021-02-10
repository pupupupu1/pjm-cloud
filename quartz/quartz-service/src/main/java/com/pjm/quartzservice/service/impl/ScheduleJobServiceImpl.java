package com.pjm.quartzservice.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.common.exception.CustomException;
import com.pjm.common.exception.PjmException;
import com.pjm.common.util.common.UuidUtil;
import com.pjm.quartzservice.componment.ScheduleUtils;
import com.pjm.quartzservice.entity.ScheduleJob;
import com.pjm.quartzservice.entityExt.ScheduleJobExt;
import com.pjm.quartzservice.mapper.ScheduleJobMapper;
import com.pjm.quartzservice.service.IScheduleJobService;
import com.pjm.quartzservice.util.Constant;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author pjm
 * @since 2020-05-25
 */
@Transactional
@Service
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobMapper, ScheduleJob> implements IScheduleJobService {
    @Autowired
    private Scheduler scheduler;

    @Autowired
    private ScheduleJobMapper scheduleJobMapper;

    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init() throws SchedulerException {

        List<ScheduleJob> scheduleJobList = scheduleJobMapper.selectList(new EntityWrapper<>());


        for (ScheduleJob scheduleJob : scheduleJobList) {
            CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getId());
            //如果不存在，则创建
            if (cronTrigger == null) {
                ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            } else {
                ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
    }

    @Override
    public ResponseEntity<ScheduleJob> insertScheduleJob(ScheduleJob scheduleJob) {
        scheduleJob.setCreateTime(new Date());
        scheduleJob.setId(UuidUtil.next());
        scheduleJob.setStatus(Constant.NORMAL);
//        scheduleJob.setCreater()
        insert(scheduleJob);
        try {
            ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            return new ResponseEntity<>(scheduleJob);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PjmException(500, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ScheduleJob> updateScheduleJob(ScheduleJob scheduleJob) throws SchedulerException {
        updateById(scheduleJob);
        scheduleJob = selectById(scheduleJob.getId());
        ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
        return new ResponseEntity<>(scheduleJob);
    }

    @Override
    public ResponseEntity<ScheduleJob> deleteScheduleJob(ScheduleJob scheduleJob) throws SchedulerException {
        deleteById(scheduleJob);
        ScheduleUtils.deleteScheduleJob(scheduler, scheduleJob.getId());
        return new ResponseEntity<>(scheduleJob);
    }

    @Override
    public PageVo<List<ScheduleJob>> listScheduleJob(ScheduleJobExt scheduleJobExt, Integer pageNum, Integer pageSize) {
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        Wrapper<ScheduleJob> scheduleJobWrapper = new EntityWrapper<>(new ScheduleJob());
        if (!StringUtils.isEmpty(scheduleJobExt.getClassPath())) {
            scheduleJobWrapper.andNew().like("class_path", scheduleJobExt.getClassPath());
        }
        if (!StringUtils.isEmpty(scheduleJobExt.getMethodName())) {
            scheduleJobWrapper.andNew().like("method_name", scheduleJobExt.getMethodName());
        }
        if (!StringUtils.isEmpty(scheduleJobExt.getRemark())) {
            scheduleJobWrapper.andNew().like("remark", scheduleJobExt.getRemark());
        }
        List<ScheduleJob> scheduleJobList = selectList(scheduleJobWrapper);
        PageInfo<ScheduleJob> pageInfo = new PageInfo<>(scheduleJobList);
        return new PageVo<>(pageNum, pageSize, pageInfo.getTotal(), scheduleJobList);
    }

    @Override
    public void run(List<String> jobIds) throws SchedulerException {
        for (String jobId : jobIds) {
            ScheduleUtils.run(scheduler, scheduleJobMapper.selectById(jobId));
        }
    }

    @Override
    public void pause(List<String> jobIds) throws SchedulerException {
        List<ScheduleJob> scheduleJobList = new ArrayList<>();
        for (String id : jobIds) {
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setId(id);
            scheduleJob.setStatus(Constant.PAUSE);
            scheduleJobList.add(scheduleJob);
        }
        updateBatchById(scheduleJobList);
        for (String jobId : jobIds) {
            ScheduleUtils.pauseJob(scheduler, jobId);
        }
    }

    @Override
    public void resume(List<String> jobIds) throws SchedulerException {

        List<ScheduleJob> scheduleJobList = new ArrayList<>();
        for (String id : jobIds) {
            ScheduleJob scheduleJob = new ScheduleJob();
            scheduleJob.setId(id);
            scheduleJob.setStatus(Constant.NORMAL);
            scheduleJobList.add(scheduleJob);
        }
        updateBatchById(scheduleJobList);
        for (String jobId : jobIds) {
            ScheduleUtils.resumeJob(scheduler, jobId);
        }
    }

    @Override
    public void jobChange(ScheduleJob mainjob, ScheduleJob sidejob) throws SchedulerException {
        updateScheduleJob(mainjob);
        updateScheduleJob(sidejob);
    }
}



