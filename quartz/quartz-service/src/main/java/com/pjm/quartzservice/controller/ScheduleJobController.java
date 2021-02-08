package com.pjm.quartzservice.controller;


import com.pjm.common.entity.PageVo;
import com.pjm.common.entity.ResponseEntity;
import com.pjm.quartzservice.entity.ScheduleJob;
import com.pjm.quartzservice.entityExt.ScheduleJobExt;
import com.pjm.quartzservice.service.IScheduleJobService;
import io.swagger.annotations.ApiOperation;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author pjm
 * @since 2020-05-25
 */
@Api(tags = {"定时器"})
@RestController
@RequestMapping("/scheduleJob")
public class ScheduleJobController {
    @Autowired
    private IScheduleJobService scheduleJobService;

    @ApiOperation("添加定时任务")
    @PostMapping("/insert")
//    @RequiresPermissions("scheduleJob/insert")
    public ResponseEntity<ScheduleJob> insertScheduleJob(@RequestBody ScheduleJob scheduleJob) throws SchedulerException {
        return scheduleJobService.insertScheduleJob(scheduleJob);
    }

    @ApiOperation("修改定时任务")
    @PostMapping("/update")
//    @RequiresPermissions("scheduleJob/update")
    public ResponseEntity<ScheduleJob> updateScheduleJob(@RequestBody ScheduleJob scheduleJob) throws SchedulerException {
        return scheduleJobService.updateScheduleJob(scheduleJob);
    }

    @ApiOperation("删除定时任务")
    @PostMapping("/delete")
//    @RequiresPermissions("scheduleJob/delete")
    public ResponseEntity<ScheduleJob> deleteScheduleJob(@RequestBody ScheduleJob scheduleJob) throws SchedulerException {
        return scheduleJobService.deleteScheduleJob(scheduleJob);
    }

    @ApiOperation("定时任务列表")
    @PostMapping("/list")
//    @RequiresPermissions("scheduleJob/list")
    public ResponseEntity<PageVo<List<ScheduleJob>>> listScheduleJob(@RequestBody ScheduleJobExt scheduleJobExt) {
        return new ResponseEntity<>(scheduleJobService.listScheduleJob(scheduleJobExt, scheduleJobExt.getPageNum(), scheduleJobExt.getPageSize()));
    }

    @ApiOperation("暂停任务")
    @PostMapping("/pause")
//    @RequiresPermissions("scheduleJob/pause")
    public ResponseEntity<String> pause(@RequestBody Map<String, List<String>> map) throws SchedulerException {
        scheduleJobService.pause(map.get("ids"));
        return new ResponseEntity<>("success");
    }

    @ApiOperation("恢复任务")
    @PostMapping("/resume")
//    @RequiresPermissions("scheduleJob/resume")
    public ResponseEntity<String> resume(@RequestBody Map<String, List<String>> map) throws SchedulerException {
        scheduleJobService.resume(map.get("ids"));
        return new ResponseEntity<>("success");
    }
}

